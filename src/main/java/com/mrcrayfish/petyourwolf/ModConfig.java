package com.mrcrayfish.petyourwolf;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class ModConfig
{
    public static class Server
    {
        public final ForgeConfigSpec.IntValue healTime;
        public final ForgeConfigSpec.DoubleValue healAmount;

        Server(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client configuration settings").push("server");
            this.healTime = builder
                    .comment("The amount of ticks to wait while petting before it heals the dog")
                    .translation("petyourwolf.configgui.healTime")
                    .defineInRange("healIntervalTicks", 100, 1, 1000);
            this.healAmount = builder
                    .comment("The amount of health the dog recieves from being pet")
                    .translation("petyourwolf.configgui.healAmount")
                    .defineInRange("petyourwolf.configgui.healAmount", 1.0, 0.5, 20.0);
            builder.pop();
        }
    }

    static final ForgeConfigSpec serverSpec;
    public static final ModConfig.Server SERVER;

    static
    {
        final Pair<ModConfig.Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}