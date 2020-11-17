package com.mrcrayfish.petyourwolf.common;

import com.mrcrayfish.petyourwolf.Config;
import com.mrcrayfish.petyourwolf.Reference;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PettingTracker
{
    private static final Map<UUID, Integer> TIMER_MAP = new HashMap<>();

    public static void startPetting(PlayerEntity entity)
    {
        TIMER_MAP.put(entity.getUniqueID(), 0);
        TameableEntity tamable = getNearestTamable(entity);
        if(tamable != null)
        {
            tamable.func_233687_w_(true); //Set sitting
            tamable.setJumping(false);
            tamable.getNavigator().clearPath();
            tamable.setAttackTarget(null);
        }
    }

    public static void stopPetting(PlayerEntity entity)
    {
        TIMER_MAP.remove(entity.getUniqueID());
    }

    private PettingTracker()
    {
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START) return;

        PlayerEntity player = event.player;
        if(player.world.isRemote) return;

        UUID uuid = player.getUniqueID();
        if(player.getDataManager().get(CustomDataParameters.PETTING) && TIMER_MAP.containsKey(uuid))
        {
            int timer = TIMER_MAP.get(uuid) + 1;
            TIMER_MAP.put(uuid, timer);
            if(timer >= Config.COMMON.healTime.get())
            {
                TameableEntity tameableEntity = getNearestTamable(player);
                if(tameableEntity != null)
                {
                    if(tameableEntity.getHealth() < tameableEntity.getMaxHealth())
                    {
                        tameableEntity.setHealth((float) (tameableEntity.getHealth() + Config.COMMON.healAmount.get()));
                    }

                    for(int i = 0; i < 7; i++)
                    {
                        double offsetX = tameableEntity.getRNG().nextGaussian() * 0.02D;
                        double offsetY = tameableEntity.getRNG().nextGaussian() * 0.02D;
                        double offsetZ = tameableEntity.getRNG().nextGaussian() * 0.02D;
                        ((ServerWorld) tameableEntity.world).spawnParticle(ParticleTypes.HEART, tameableEntity.getPosX() + (double) (tameableEntity.getRNG().nextFloat() * tameableEntity.getWidth() * 2.0F) - (double) tameableEntity.getWidth(), tameableEntity.getPosY() + 0.5D + (double) (tameableEntity.getRNG().nextFloat() * tameableEntity.getHeight()), tameableEntity.getPosZ() + (double) (tameableEntity.getRNG().nextFloat() * tameableEntity.getWidth() * 2.0F) - (double) tameableEntity.getWidth(), 1, offsetX, offsetY, offsetZ, 0);
                    }

                    if(tameableEntity instanceof WolfEntity)
                    {
                        tameableEntity.world.playSound(null, tameableEntity.getPosX(), tameableEntity.getPosY(), tameableEntity.getPosZ(), SoundEvents.ENTITY_WOLF_WHINE, SoundCategory.NEUTRAL, 0.7F, 0.9F + tameableEntity.getRNG().nextFloat() * 0.2F);
                    }
                    else if(tameableEntity instanceof CatEntity)
                    {
                        tameableEntity.world.playSound(null, tameableEntity.getPosX(), tameableEntity.getPosY(), tameableEntity.getPosZ(), SoundEvents.ENTITY_CAT_PURR, SoundCategory.NEUTRAL, 0.7F, 0.9F + tameableEntity.getRNG().nextFloat() * 0.2F);
                    }
                    else if(tameableEntity instanceof ParrotEntity)
                    {
                        tameableEntity.world.playSound(null, tameableEntity.getPosX(), tameableEntity.getPosY(), tameableEntity.getPosZ(), SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.NEUTRAL, 0.7F, 0.9F + tameableEntity.getRNG().nextFloat() * 0.2F);
                    }
                }
                TIMER_MAP.put(uuid, 0);
            }
        }
    }

    @Nullable
    public static TameableEntity getNearestTamable(PlayerEntity entity)
    {
        Vector3d lookVec = entity.getLookVec().normalize();
        Vector3d targetPos = entity.getPositionVec().add(lookVec.x, 1, lookVec.z);
        List<TameableEntity> tamableEntities = entity.world.getEntitiesWithinAABB(TameableEntity.class, new AxisAlignedBB(targetPos.subtract(1, 1, 1), targetPos.add(1, 1, 1)));
        if(tamableEntities.size() > 0)
        {
            float closestDistance = 0;
            TameableEntity closetTamable = null;
            for(TameableEntity tameableEntity : tamableEntities)
            {
                if(tameableEntity.isTamed() && tameableEntity.getOwnerId() != null && tameableEntity.getOwnerId().equals(entity.getUniqueID()))
                {
                    float distance = entity.getDistance(tameableEntity);
                    if(distance < closestDistance || closestDistance == 0F)
                    {
                        closestDistance = distance;
                        closetTamable = tameableEntity;
                    }
                }
            }
            return closetTamable;
        }
        return null;
    }
}
