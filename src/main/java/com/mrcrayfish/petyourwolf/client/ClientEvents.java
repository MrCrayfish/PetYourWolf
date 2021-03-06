package com.mrcrayfish.petyourwolf.client;

import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.petyourwolf.common.CustomDataParameters;
import com.mrcrayfish.petyourwolf.network.PacketHandler;
import com.mrcrayfish.petyourwolf.network.message.MessagePet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private boolean pressed = false;

    private Map<UUID, AnimationTracker> animationTrackerMap = new HashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        if(KeyBindings.PET.isKeyDown() && !mc.player.isSneaking() && this.getNearestTamable(mc.player) != null)
        {
            if(!this.pressed)
            {
                PacketHandler.instance.sendToServer(new MessagePet(true));
                this.pressed = true;
            }
        }
        else if(this.pressed)
        {
            PacketHandler.instance.sendToServer(new MessagePet(false));
            this.pressed = false;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        UUID playerUuid = event.player.getUniqueID();
        AnimationTracker tracker = animationTrackerMap.computeIfAbsent(playerUuid, uuid -> new AnimationTracker());
        tracker.tick(event.player);
    }

    @SubscribeEvent
    public void onSetupAngles(PlayerModelEvent.SetupAngles.Post event)
    {
        if(event.getEntity().equals(Minecraft.getInstance().player) && Minecraft.getInstance().gameSettings.func_243230_g() == PointOfView.FIRST_PERSON)
        {
            return;
        }

        PlayerEntity player = event.getPlayer();
        AnimationTracker tracker = animationTrackerMap.computeIfAbsent(player.getUniqueID(), uuid -> new AnimationTracker());
        if(tracker.getCounter() != 0 || tracker.getPrevCounter() != 0)
        {
            float percent = (tracker.getPrevCounter() + (tracker.getCounter() - tracker.getPrevCounter()) * event.getPartialTicks()) / (float) AnimationTracker.MAX_ANIMATION_TICKS;

            PlayerModel model = event.getModelPlayer();
            model.bipedHead.rotationPointY = 0.25F * 16F * percent;
            model.bipedHead.rotationPointZ = -0.45F * 16F * percent;
            this.copyModelProperties(model.bipedHeadwear, model.bipedHead);

            model.bipedBody.rotateAngleX = (float) Math.toRadians(45F * percent);
            model.bipedBody.rotationPointY = 0.25F * 16F * percent;
            model.bipedBody.rotationPointZ = -0.45F * 16F * percent;
            this.copyModelProperties(model.bipedBodyWear, model.bipedBody);

            ModelRenderer mainHand = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedRightArm : model.bipedLeftArm;
            ModelRenderer offHand = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedLeftArm : model.bipedRightArm;
            ModelRenderer mainHandWear = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedRightArmwear : model.bipedLeftArmwear;
            ModelRenderer offHandWear = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedLeftArmwear : model.bipedRightArmwear;
            boolean rightHanded = mainHand == model.bipedRightArm;

            float armAngle = -75F;
            float renderYawOffset = event.getPlayer().prevRenderYawOffset + (event.getPlayer().renderYawOffset - event.getPlayer().prevRenderYawOffset) * event.getPartialTicks();
            renderYawOffset = MathHelper.wrapDegrees(renderYawOffset + 90);
            float deltaRotation = rightHanded ? 90F : -90F;
            TameableEntity entity = this.getNearestTamable(event.getPlayer());
            if(entity != null)
            {
                Vector3d bodyVec = Vector3d.fromPitchYaw(0F, renderYawOffset);
                Vector3d playerLookVec = bodyVec.normalize();
                Vector3d playerLookVecRotated = playerLookVec.rotateYaw(rightHanded ? 90F : -90F);

                double playerPosX = player.prevPosX + (player.getPosX() - player.prevPosX) * event.getPartialTicks();
                double playerPosY = player.prevPosY + (player.getPosY() - player.prevPosY) * event.getPartialTicks();
                double playerPosZ = player.prevPosZ + (player.getPosZ() - player.prevPosZ) * event.getPartialTicks();
                Vector3d playerArmVec = new Vector3d(playerPosX, playerPosY, playerPosZ).add(playerLookVec.x * 0.5, 0, playerLookVec.z * 0.5).add(playerLookVecRotated.x * 0.45, 0, playerLookVecRotated.z * 0.45);

                float tamableRenderYawOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * event.getPartialTicks();
                tamableRenderYawOffset = MathHelper.wrapDegrees(tamableRenderYawOffset);
                Vector3d tamableBodyVec = Vector3d.fromPitchYaw(0F, tamableRenderYawOffset);
                Vector3d tamableLookVec = tamableBodyVec.normalize();

                double centerOffset = 0.0;
                if(entity instanceof WolfEntity)
                {
                    centerOffset = 0.4;
                    armAngle = -75F;
                }
                else if(entity instanceof CatEntity)
                {
                    centerOffset = 0.3;
                    armAngle = -55F;
                }
                else if(entity instanceof ParrotEntity)
                {
                    centerOffset = 0.1;
                    armAngle = -35F;
                }

                Vector3d headPos = entity.getPositionVec().add(tamableLookVec.x * centerOffset, 0, tamableLookVec.z * centerOffset);
                double dX = headPos.x - playerArmVec.x;
                double dZ = headPos.z - playerArmVec.z;
                deltaRotation += (float)(Math.atan2(dZ, dX)) * (180F / (float)Math.PI) - 90;
                tracker.setLastDeltaRotation(deltaRotation);
                tracker.setLastArmAngle(armAngle);
            }
            else
            {
                deltaRotation = tracker.getLastDeltaRotation();
                armAngle = tracker.getLastArmAngle();
            }

            mainHand.rotateAngleX = (float) Math.toRadians(armAngle) * percent;
            float animation = (float) Math.sin((event.getPlayer().ticksExisted + event.getPartialTicks()) * 0.25) * 10 - 5;
            mainHand.rotateAngleY = (float) Math.toRadians(animation + deltaRotation - renderYawOffset);
            mainHand.rotationPointY = 2.0F + 0.25F * 16F * percent;
            mainHand.rotationPointZ = -0.4F * 16F * percent;
            this.copyModelProperties(mainHandWear, mainHand);

            offHand.rotateAngleX = (float) Math.toRadians(45F) * percent;
            offHand.rotationPointY = 2.0F + 0.25F * 16F * percent;
            offHand.rotationPointZ = -0.4F * 16F * percent;
            this.copyModelProperties(offHandWear, offHand);
        }
    }

    private void copyModelProperties(ModelRenderer to, ModelRenderer from)
    {
        to.copyModelAngles(from);
        to.rotationPointX = from.rotationPointX;
        to.rotationPointY = from.rotationPointY;
        to.rotationPointZ = from.rotationPointZ;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player.getDataManager().get(CustomDataParameters.PETTING))
        {
            boolean rightHanded = mc.gameSettings.mainHand == HandSide.RIGHT ? event.getHand() == Hand.MAIN_HAND : event.getHand() == Hand.OFF_HAND;
            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees((float) (-10F * Math.sin((mc.player.ticksExisted + event.getPartialTicks()) * 0.25F) + (rightHanded ? 30F : -30F))));
        }
    }

    @SubscribeEvent
    public void onRenderThirdPersonHand(RenderItemEvent.Held.Pre event)
    {
        LivingEntity entity = event.getEntity();
        AnimationTracker tracker = animationTrackerMap.computeIfAbsent(entity.getUniqueID(), uuid -> new AnimationTracker());
        if(tracker.getCounter() != 0 || tracker.getPrevCounter() != 0)
        {
            event.setCanceled(true);
        }
    }

    @Nullable
    private TameableEntity getNearestTamable(PlayerEntity player)
    {
        Vector3d lookVec = player.getLookVec().normalize();
        Vector3d targetPos = player.getPositionVec().add(lookVec.x, 1, lookVec.z);
        List<TameableEntity> tameableEntities = player.world.getEntitiesWithinAABB(TameableEntity.class, new AxisAlignedBB(targetPos.subtract(1, 1, 1), targetPos.add(1, 1, 1)));
        if(tameableEntities.size() > 0)
        {
            float closestDistance = 0;
            TameableEntity entity = null;
            for(TameableEntity tameableEntity : tameableEntities)
            {
                float distance = player.getDistance(tameableEntity);
                if(distance < closestDistance || closestDistance == 0F)
                {
                    closestDistance = distance;
                    entity = tameableEntity;
                }
            }
            return entity;
        }
        return null;
    }
}
