package com.cerzix.resourcesilo.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfigs {
    private ModConfigs() {}

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        COMMON = new Common(b);
        COMMON_SPEC = b.build();
    }

    public static final class Common {
        public final ForgeConfigSpec.IntValue TICKS_PER_ITEM;
        public final ForgeConfigSpec.IntValue MAX_STORAGE;

        Common(ForgeConfigSpec.Builder b) {
            b.push("resource_silo");

            TICKS_PER_ITEM = b
                    .comment("How many ticks it takes to generate 1 Supplies item (20 ticks = 1 second).")
                    .defineInRange("ticksPerItem", 40, 1, 20_000);

            MAX_STORAGE = b
                    .comment("Maximum number of Supplies that can be stored internally.")
                    .defineInRange("maxStorage", 2000, 1, Integer.MAX_VALUE);

            b.pop();
        }
    }
}
