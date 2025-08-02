package com.blocklogic.furnacesplus.config;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

public class FPConfig {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec COMMON_CONFIG;

    public static ModConfigSpec SPEC;

    public static final String CATEGORY_FURNACES = "furnaces";

    public static ModConfigSpec.IntValue FURNACE_COOKING_TIME;

    public static void register(ModContainer container) {
        registerCommonConfigs(container);
    }

    private static void registerCommonConfigs(ModContainer container) {
        furnaceConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
        SPEC = COMMON_CONFIG;
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    private static void furnaceConfig() {
        COMMON_BUILDER.comment("Furnaces+ - Configure cooking time for all specialized furnaces").push(CATEGORY_FURNACES);

        FURNACE_COOKING_TIME = COMMON_BUILDER.comment("Cooking time in ticks for all furnaces",
                        "60 ticks = 3 seconds (40% faster than blast furnace)",
                        "Default: 60 | Min: 20 (1 second) | Max: 1200 (1 minute)")
                .defineInRange("cooking_time", 60, 20, 1200);

        COMMON_BUILDER.pop();
    }

    public static int getFurnaceCookingTime() {
        return FURNACE_COOKING_TIME.get();
    }

    public static void validateConfig() {
        if (getFurnaceCookingTime() < 40) {
            LOGGER.warn("Furnace cooking time ({} ticks) is very fast and may impact game balance!", getFurnaceCookingTime());
        }

        if (getFurnaceCookingTime() > 600) {
            LOGGER.warn("Furnace cooking time ({} ticks) is very slow and may be frustrating for players!", getFurnaceCookingTime());
        }
    }

    public static void loadConfig() {
        LOGGER.info("Furnaces+ configs reloaded");
        validateConfig();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        LOGGER.info("Furnaces+ configuration loaded");
        logConfigValues();
        validateConfig();
    }

    private static void logConfigValues() {
        LOGGER.info("Furnaces+ Configuration:");
        LOGGER.info("  Cooking Time: {} ticks ({} seconds)", getFurnaceCookingTime(), getFurnaceCookingTime() / 20.0f);
    }
}
