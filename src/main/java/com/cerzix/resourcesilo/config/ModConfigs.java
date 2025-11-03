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
        /**
         * If > 0, this human-friendly setting (items per minute) controls generation rate.
         * If <= 0, falls back to ticksPerItem.
         */
        public final ForgeConfigSpec.DoubleValue ITEMS_PER_MINUTE;

        /** How many ticks it takes to generate 1 item (20 ticks = 1 second). Used when itemsPerMinute <= 0. */
        public final ForgeConfigSpec.IntValue TICKS_PER_ITEM;

        /** Maximum number of Supplies that can be stored internally. */
        public final ForgeConfigSpec.IntValue MAX_STORAGE;

        Common(ForgeConfigSpec.Builder b) {
            b.push("resource_silo");

            ITEMS_PER_MINUTE = b
                    .comment("Generation rate in ITEMS PER MINUTE (more human-friendly).",
                            "Set > 0 to use this value. Set to 0 or negative to use ticksPerItem instead.")
                    .defineInRange("itemsPerMinute", 0.0, -1_000_000.0, 1_000_000.0);

            TICKS_PER_ITEM = b
                    .comment("Fallback: ticks per item (20 ticks = 1 second). Used when itemsPerMinute <= 0.")
                    .defineInRange("ticksPerItem", 40, 1, 20_000);

            MAX_STORAGE = b
                    .comment("Maximum number of Supplies that can be stored internally.")
                    .defineInRange("maxStorage", 2000, 1, Integer.MAX_VALUE);

            b.pop();
        }
    }
}
