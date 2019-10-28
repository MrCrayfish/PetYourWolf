package com.mrcrayfish.petyourwolf.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class CommonEvents
{
    @SubscribeEvent
    public void onEntityConstruct(EntityEvent.EntityConstructing event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            event.getEntity().getDataManager().register(CustomDataParameters.PETTING, false);
        }
    }
}
