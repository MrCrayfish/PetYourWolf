package com.mrcrayfish.petyourwolf;

import com.mrcrayfish.petyourwolf.client.ClientEvents;
import com.mrcrayfish.petyourwolf.client.KeyBindings;
import com.mrcrayfish.petyourwolf.common.CommonEvents;
import com.mrcrayfish.petyourwolf.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class PetYourWolfMod
{
    public PetYourWolfMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        KeyBindings.init();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
