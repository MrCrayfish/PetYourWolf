package com.mrcrayfish.petyourwolf;

import com.mrcrayfish.petyourwolf.client.ClientEvents;
import com.mrcrayfish.petyourwolf.client.KeyBindings;
import com.mrcrayfish.petyourwolf.client.render.entity.CustomWolfModel;
import com.mrcrayfish.petyourwolf.common.CommonEvents;
import com.mrcrayfish.petyourwolf.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
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
        this.patchWolfModel();
    }

    @OnlyIn(Dist.CLIENT)
    private void patchWolfModel()
    {
        LivingRenderer<?, ?> wolfRenderer = (LivingRenderer<?, ?>) Minecraft.getInstance().getRenderManager().renderers.get(EntityType.WOLF);
        ObfuscationReflectionHelper.setPrivateValue(LivingRenderer.class, wolfRenderer, new CustomWolfModel(), "field_77045_g");
    }
}
