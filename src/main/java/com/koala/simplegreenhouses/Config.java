package com.koala.simplegreenhouses;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final ModConfigSpec.DoubleValue SPEED_MULTIPLIER = BUILDER
                        .comment("Mutiplier to the base speed of the greenhouse")
                        .defineInRange("speedMultiplier", 1., 0.1, 10.);

        public static final ModConfigSpec.DoubleValue NOWATER_PENALTY = BUILDER
                        .comment("Penalty to the speed of the machine if water is not supplied")
                        .defineInRange("waterPenalty", 3., 1., 100.);

        public static final ModConfigSpec.DoubleValue FERTILIZER_BONUS = BUILDER
                        .comment("Multiplier to the speed of the machine if fertilizer is supplied")
                        .defineInRange("fertilizerBonus", 3., 1., 100.);
        
        public static final ModConfigSpec.DoubleValue WATER_USAGE_MULTIPLIER = BUILDER
                        .comment("Multiplier to the amount of water used to grow crops")
                        .defineInRange("waterUsageMultiplier", 1., 0.1, 10.);

        public static final ModConfigSpec.IntValue GREENHOUSE_WIDTH = BUILDER
                        .comment("Maximum distance from the controller the greenhouse can extend to")
                        .defineInRange("greenhouseWidth", 10, 2, 100);
        
        public static final ModConfigSpec.IntValue GREENHOUSE_HEIGHT = BUILDER
                        .comment("Maximum height from the controller the greenhouse can extend to")
                        .defineInRange("greenhouseHeight", 10, 2, 100);

        public static final ModConfigSpec.ConfigValue<List<? extends String>> TAGS_WHITELIST = BUILDER
                        .comment("A list of *item tags* to allow in the greenhouse (only items with the tag with loot from crops)")
                        .defineListAllowEmpty("tagsWhitelist",
                                        List.of("c:crops", "c:seeds", "c:foods/fruit", "c:foods/vegetable",
                                                        "c:foods/berry", "minecraft:flowers"),
                                        () -> "", Config::validateItemTagName);

        public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_BLACKLIST = BUILDER
                        .comment("A list of *blocks* to forbid in the greenhouse")
                        .defineListAllowEmpty("blockBlacklist", List.of("minecraft:wither_rose"), () -> "",
                                        Config::validateBlockName);

        static final ModConfigSpec SPEC = BUILDER.build();

        private static boolean validateBlockName(final Object obj) {
                return obj instanceof String blockName
                                && BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(blockName));
        }

        private static boolean validateItemTagName(final Object obj) {
                return obj instanceof String;
        }
}
