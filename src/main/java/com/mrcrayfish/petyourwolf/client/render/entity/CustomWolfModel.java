package com.mrcrayfish.petyourwolf.client.render.entity;

import com.mrcrayfish.petyourwolf.common.CustomDataParameters;
import com.mrcrayfish.petyourwolf.common.PettingTracker;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class CustomWolfModel extends WolfModel<WolfEntity>
{
    @Override
    public void setLivingAnimations(WolfEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        if(this.isBeingPet(entityIn))
        {
            double animation = Math.sin((entityIn.ticksExisted + partialTick) * 0.5) * 10;
            this.tail.rotateAngleY = (float) Math.toRadians(animation);
        }
    }

    private boolean isBeingPet(WolfEntity entityIn)
    {
        List<PlayerEntity> players = entityIn.world.getEntitiesWithinAABB(PlayerEntity.class, entityIn.getBoundingBox().grow(5, 5, 5), entity -> entity.getDataManager().get(CustomDataParameters.PETTING));
        return players.stream().anyMatch(player -> PettingTracker.getNearestTamable(player) == entityIn);
    }
}
