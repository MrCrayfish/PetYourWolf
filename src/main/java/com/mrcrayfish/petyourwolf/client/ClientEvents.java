package com.mrcrayfish.petyourwolf.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import com.mrcrayfish.petyourwolf.common.CustomDataParameters;
import com.mrcrayfish.petyourwolf.network.PacketHandler;
import com.mrcrayfish.petyourwolf.network.message.MessagePet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private boolean pressed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(KeyBindings.PET.isKeyDown() && this.getNearestWolf() != null)
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
    public void onSetupAngles(PlayerModelEvent.SetupAngles event)
    {
        if(event.getEntity().equals(Minecraft.getInstance().player) && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
        {
            return;
        }

        if(event.getEntity().getDataManager().get(CustomDataParameters.PETTING))
        {
            PlayerModel model = event.getModelPlayer();
            model.bipedHead.offsetY = 0.25F;
            model.bipedHead.offsetZ = -0.45F;

            model.bipedBody.rotateAngleX = (float) Math.toRadians(45F);
            model.bipedBody.offsetY = 0.25F;
            model.bipedBody.offsetZ = -0.45F;

            RendererModel mainHand = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedRightArm : model.bipedLeftArm;
            RendererModel offHand = event.getPlayer().getPrimaryHand() == HandSide.RIGHT ? model.bipedLeftArm : model.bipedRightArm;
            boolean rightHanded = mainHand == model.bipedRightArm;

            float deltaRotation = rightHanded ? 15F : -15F;
            WolfEntity entity = this.getNearestWolf();
            if(entity != null)
            {
                float renderYawOffset = event.getPlayer().prevRenderYawOffset + (event.getPlayer().renderYawOffset - event.getPlayer().prevRenderYawOffset) * event.getPartialTicks();
                Vec3d bodyVec = Vec3d.fromPitchYaw(0F, renderYawOffset);
                Vec3d playerLookVec = bodyVec.normalize();
                Vec3d playerLookVecRotated = playerLookVec.rotateYaw(rightHanded ? 90F : -90F);
                Vec3d playerArmVec = event.getEntity().getPositionVec().add(playerLookVec.x * 0.5, 0, playerLookVec.z * 0.5).add(playerLookVecRotated.x * 0.35, 0, playerLookVecRotated.z * 0.35);
                Vec3d wolfLookVec = entity.getLookVec().normalize();
                Vec3d wolfHeadPos = entity.getPositionVec().add(wolfLookVec.x * 0.75, 0, wolfLookVec.z * 0.75);
                double dX = wolfHeadPos.x - playerArmVec.x;
                double dZ = wolfHeadPos.z - playerArmVec.z;
                deltaRotation += (float)(-Math.atan2(dZ, dX)) * (180F / (float)Math.PI) + renderYawOffset + 90;
            }

            mainHand.rotateAngleX = (float) Math.toRadians(-75F);
            mainHand.rotateAngleY = (float) Math.toRadians(Math.sin((event.getPlayer().ticksExisted + event.getPartialTicks()) * 0.25) * 20 - deltaRotation);
            mainHand.offsetY = 0.25F;
            mainHand.offsetZ = -0.4F;

            offHand.rotateAngleX = (float) Math.toRadians(45F);
            offHand.offsetY = 0.25F;
            offHand.offsetZ = -0.4F;
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderSpecificHandEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player.getDataManager().get(CustomDataParameters.PETTING))
        {
            boolean rightHanded = mc.gameSettings.mainHand == HandSide.RIGHT ? event.getHand() == Hand.MAIN_HAND : event.getHand() == Hand.OFF_HAND;
            GlStateManager.rotated(-10F * Math.sin((mc.player.ticksExisted + event.getPartialTicks()) * 0.25) + (rightHanded ? 30F : -30F), 0, 1, 0.5);
        }
    }

    @Nullable
    private WolfEntity getNearestWolf()
    {
        Minecraft mc = Minecraft.getInstance();
        Vec3d lookVec = mc.player.getLookVec().normalize();
        Vec3d targetPos = mc.player.getPositionVec().add(lookVec.x, 1, lookVec.z);
        List<WolfEntity> wolves = mc.world.getEntitiesWithinAABB(WolfEntity.class, new AxisAlignedBB(targetPos.subtract(1, 1, 1), targetPos.add(1, 1, 1)));
        if(wolves.size() > 0)
        {
            float closestDistance = 0;
            WolfEntity entity = null;
            for(WolfEntity wolf : wolves)
            {
                float distance = mc.player.getDistance(wolf);
                if(distance < closestDistance || closestDistance == 0F)
                {
                    closestDistance = distance;
                    entity = wolf;
                }
            }
            return entity;
        }
        return null;
    }
}
