package com.blocklogic.furnacesplus.container;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.container.menu.FurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FPMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FurnacesPlus.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> GLASS_KILN_MENU =
            registerMenuType("glass_kiln_menu", FurnaceMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> KILN_MENU =
            registerMenuType("kiln_menu", FurnaceMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> FOUNDRY_MENU =
            registerMenuType("foundry_menu", FurnaceMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> OVEN_MENU =
            registerMenuType("oven_menu", FurnaceMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}