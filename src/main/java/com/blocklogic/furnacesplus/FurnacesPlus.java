package com.blocklogic.furnacesplus;

import com.blocklogic.furnacesplus.block.FPBlocks;
import com.blocklogic.furnacesplus.config.FPConfig;
import com.blocklogic.furnacesplus.container.FPMenuTypes;
import com.blocklogic.furnacesplus.container.screen.FurnaceScreen;
import com.blocklogic.furnacesplus.entity.FPBlockEntities;
import com.blocklogic.furnacesplus.entity.custom.FurnaceBlockEntity;
import com.blocklogic.furnacesplus.item.FPCreativeTab;
import com.blocklogic.furnacesplus.item.FPItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(FurnacesPlus.MODID)
public class FurnacesPlus {

    public static final String MODID = "furnacesplus";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FurnacesPlus(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        FPBlocks.register(modEventBus);
        FPItems.register(modEventBus);
        FPCreativeTab.register(modEventBus);
        FPBlockEntities.register(modEventBus);
        FPMenuTypes.register(modEventBus);

        modEventBus.addListener(FurnaceBlockEntity::registerCapabilities);

        NeoForge.EVENT_BUS.register(this);

        FPConfig.register(modContainer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }


        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(FPMenuTypes.GLASS_KILN_MENU.get(), FurnaceScreen::new);
            event.register(FPMenuTypes.KILN_MENU.get(), FurnaceScreen::new);
            event.register(FPMenuTypes.FOUNDRY_MENU.get(), FurnaceScreen::new);
        }


    }
}
