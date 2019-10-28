package com.mrcrayfish.petyourwolf.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

/**
 * Author: MrCrayfish
 */
public class CustomDataParameters
{
    public static final DataParameter<Boolean> PETTING = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.BOOLEAN);
}
