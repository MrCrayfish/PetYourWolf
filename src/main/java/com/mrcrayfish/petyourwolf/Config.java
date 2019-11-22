package com.mrcrayfish.petyourwolf;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class Config
{
    public static class Common
    {
        public final ForgeConfigSpec.IntValue healTime;
        public final ForgeConfigSpec.DoubleValue healAmount;

        Common(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common configuration settings").push("common");
            this.healTime = builder
                    .comment("The amount of ticks to wait while petting before it heals the dog")
                    .translation("petyourwolf.configgui.healTime")
                    .defineInRange("healIntervalTicks", 100, 1, 1000);
            this.healAmount = builder
                    .comment("The amount of health the tameable mob recieves from being pet")
                    .translation("petyourwolf.configgui.healAmount")
                    .defineInRange("healAmount", 1.0, 0.5, 20.0);
            builder.pop();
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static
    {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}