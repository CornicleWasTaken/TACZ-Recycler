package com.samik.taczrecycler.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RecyclerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> defaultProcessingTime;
    public static final ForgeConfigSpec.ConfigValue<Double> outputMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enableGunRecycling;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enableAttachmentRecycling;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enableAmmoRecycling;

    static {
        BUILDER.push("TACZ Recycler Settings");

        defaultProcessingTime = BUILDER.comment("Default processing time in ticks if recipe doesn't specify")
                .define("defaultProcessingTime", 400);

        outputMultiplier = BUILDER.comment("Multiplier for item output chances")
                .defineInRange("outputMultiplier", 1.0, 0.0, 10.0);

        enableGunRecycling = BUILDER.comment("Allow guns to be recycled (Requires recipes to be present)")
                .define("enableGunRecycling", true);

        enableAttachmentRecycling = BUILDER.comment("Allow attachments to be recycled (Requires recipes to be present)")
                .define("enableAttachmentRecycling", true);

        enableAmmoRecycling = BUILDER.comment("Allow ammo to be recycled (Requires recipes to be present)")
                .define("enableAmmoRecycling", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
