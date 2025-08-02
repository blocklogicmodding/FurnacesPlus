package com.blocklogic.furnacesplus;

import com.blocklogic.furnacesplus.block.FPBlocks;
import com.blocklogic.furnacesplus.config.FPConfig;
import com.blocklogic.furnacesplus.container.FPMenuTypes;
import com.blocklogic.furnacesplus.container.screen.GlassKilnScreen;
import com.blocklogic.furnacesplus.entity.FPBlockEntities;
import com.blocklogic.furnacesplus.item.FPCreativeTab;
import com.blocklogic.furnacesplus.item.FPItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

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
            event.register(FPMenuTypes.GLASS_KILN_MENU.get(), GlassKilnScreen::new);
        }
    }
}
