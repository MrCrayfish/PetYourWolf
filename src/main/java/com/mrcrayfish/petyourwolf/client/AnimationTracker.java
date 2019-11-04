package com.mrcrayfish.petyourwolf.client;

import com.mrcrayfish.petyourwolf.common.CustomDataParameters;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Author: MrCrayfish
 */
public class AnimationTracker
{
    public static final int MAX_ANIMATION_TICKS = 5;

    private float lastDeltaRotation;
    private int counter;
    private int prevCounter;

    public void tick(PlayerEntity player)
    {
        this.prevCounter = counter;
        if(player.getDataManager().get(CustomDataParameters.PETTING))
        {
            if(this.counter < MAX_ANIMATION_TICKS)
            {
                this.counter++;
            }
        }
        else if(this.counter > 0)
        {
            this.counter--;
        }
    }

    public int getCounter()
    {
        return counter;
    }

    public int getPrevCounter()
    {
        return prevCounter;
    }

    public void setLastDeltaRotation(float lastDeltaRotation)
    {
        this.lastDeltaRotation = lastDeltaRotation;
    }

    public float getLastDeltaRotation()
    {
        return lastDeltaRotation;
    }
}
