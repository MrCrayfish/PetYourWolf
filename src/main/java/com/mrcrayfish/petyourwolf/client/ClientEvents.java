package com.mrcrayfish.petyourwolf.client;

import com.mrcrayfish.petyourwolf.network.PacketHandler;
import com.mrcrayfish.petyourwolf.network.message.MessagePet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    private boolean pressed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(KeyBindings.PET.isKeyDown() && this.isLookingAtTamedWolf())
        {
            if(!this.pressed)
            {
                PacketHandler.instance.sendToServer(new MessagePet(true));
                this.pressed = true;
                Minecraft.getInstance().player.sendChatMessage("PETTING");
            }
        }
        else if(this.pressed)
        {
            PacketHandler.instance.sendToServer(new MessagePet(false));
            this.pressed = false;
            Minecraft.getInstance().player.sendChatMessage("NOT PETTING");
        }
    }

    private boolean isLookingAtTamedWolf()
    {
        Minecraft mc = Minecraft.getInstance();
        RayTraceResult result = Minecraft.getInstance().objectMouseOver;
        if(result.getType() == RayTraceResult.Type.ENTITY)
        {
            EntityRayTraceResult entityResult = (EntityRayTraceResult) result;
            if(entityResult.getEntity() instanceof WolfEntity)
            {
                WolfEntity wolf = (WolfEntity) entityResult.getEntity();
                //return false if the player is more than 1.5 blocks away
                if(wolf.getDistance(mc.player) > 1.5f)
                {
                    return false;
                }
                //check if the player is the owner of the tamed wolf
                return wolf.isTamed() && wolf.getOwnerId() != null && wolf.getOwnerId().equals(mc.player.getUniqueID());
            }
        }
        return false;
    }
}
