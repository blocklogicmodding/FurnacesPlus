package com.blocklogic.furnacesplus.item;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.block.FPBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FPCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FurnacesPlus.MODID);

    public static final Supplier<CreativeModeTab> FURNACESPLUS = CREATIVE_MODE_TAB.register("furnacesplus",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(FPBlocks.GLASS_KILN.get()))
                    .title(Component.translatable("creativetab.furnacesplus"))
                    .displayItems((ItemDisplayParameters, output) -> {
                        output.accept(FPBlocks.GLASS_KILN);
                        output.accept(FPBlocks.KILN);
                        output.accept(FPBlocks.FOUNDRY);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
