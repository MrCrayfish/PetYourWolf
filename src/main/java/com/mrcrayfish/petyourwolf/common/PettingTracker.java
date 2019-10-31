package com.mrcrayfish.petyourwolf.common;

import com.mrcrayfish.petyourwolf.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
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
        WolfEntity wolf = getNearestTamedWolf(entity);
        if(wolf != null)
        {
            wolf.getAISit().setSitting(true);
            wolf.setJumping(false);
            wolf.getNavigator().clearPath();
            wolf.setAttackTarget(null);
        }
    }

    public static void stopPetting(PlayerEntity entity)
    {
        TIMER_MAP.remove(entity.getUniqueID());
    }

    private PettingTracker() {}

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        PlayerEntity player = event.player;
        if(player.world.isRemote)
            return;

        UUID uuid = player.getUniqueID();
        if(player.getDataManager().get(CustomDataParameters.PETTING) && TIMER_MAP.containsKey(uuid))
        {
            int timer = TIMER_MAP.get(uuid) + 1;
            TIMER_MAP.put(uuid, timer);
            if(timer == 100)
            {
                WolfEntity wolf = getNearestTamedWolf(player);
                if(wolf != null)
                {
                    if(wolf.getHealth() < wolf.getMaxHealth())
                    {
                        wolf.setHealth(wolf.getHealth() + 1.0F);
                    }
                    for(int i = 0; i < 7; i++)
                    {
                        double offsetX = wolf.getRNG().nextGaussian() * 0.02D;
                        double offsetY = wolf.getRNG().nextGaussian() * 0.02D;
                        double offsetZ = wolf.getRNG().nextGaussian() * 0.02D;
                        ((ServerWorld)wolf.world).spawnParticle(ParticleTypes.HEART, wolf.posX + (double) (wolf.getRNG().nextFloat() * wolf.getWidth() * 2.0F) - (double) wolf.getWidth(), wolf.posY + 0.5D + (double) (wolf.getRNG().nextFloat() * wolf.getHeight()), wolf.posZ + (double) (wolf.getRNG().nextFloat() * wolf.getWidth() * 2.0F) - (double) wolf.getWidth(), 1,offsetX,offsetY, offsetZ, 0);
                    }
                    wolf.world.playSound(null, wolf.posX, wolf.posY, wolf.posZ, SoundEvents.ENTITY_WOLF_WHINE, SoundCategory.NEUTRAL, 0.7F, 0.9F + wolf.getRNG().nextFloat() * 0.2F);
                }
                TIMER_MAP.put(uuid, 0);
            }
        }
    }

    @Nullable
    public static WolfEntity getNearestTamedWolf(PlayerEntity entity)
    {
        Vec3d lookVec = entity.getLookVec().normalize();
        Vec3d targetPos = entity.getPositionVec().add(lookVec.x, 1, lookVec.z);
        List<WolfEntity> wolves = entity.world.getEntitiesWithinAABB(WolfEntity.class, new AxisAlignedBB(targetPos.subtract(1, 1, 1), targetPos.add(1, 1, 1)));
        if(wolves.size() > 0)
        {
            float closestDistance = 0;
            WolfEntity closetWolf = null;
            for(WolfEntity wolf : wolves)
            {
                if(wolf.isTamed() && wolf.getOwnerId() != null && wolf.getOwnerId().equals(entity.getUniqueID()))
                {
                    float distance = entity.getDistance(wolf);
                    if(distance < closestDistance || closestDistance == 0F)
                    {
                        closestDistance = distance;
                        closetWolf = wolf;
                    }
                }
            }
            return closetWolf;
        }
        return null;
    }
}
