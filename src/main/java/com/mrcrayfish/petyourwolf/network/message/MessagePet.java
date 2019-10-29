package com.mrcrayfish.petyourwolf.network.message;

import com.mrcrayfish.petyourwolf.common.CustomDataParameters;
import com.mrcrayfish.petyourwolf.common.PettingTracker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessagePet implements IMessage<MessagePet>
{
    private boolean pet;

    public MessagePet() {}

    public MessagePet(boolean pet)
    {
        this.pet = pet;
    }

    @Override
    public void encode(MessagePet message, PacketBuffer buffer)
    {
        buffer.writeBoolean(message.pet);
    }

    @Override
    public MessagePet decode(PacketBuffer buffer)
    {
        return new MessagePet(buffer.readBoolean());
    }

    @Override
    public void handle(MessagePet message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity entity = supplier.get().getSender();
            if(entity != null)
            {
                entity.getDataManager().set(CustomDataParameters.PETTING, message.pet);
                if(message.pet)
                {
                    PettingTracker.startPetting(entity);
                }
                else
                {
                    PettingTracker.stopPetting(entity);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
