package com.blocklogic.furnacesplus.block;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.block.custom.FoundryBlock;
import com.blocklogic.furnacesplus.block.custom.GlassKilnBlock;
import com.blocklogic.furnacesplus.block.custom.KilnBlock;
import com.blocklogic.furnacesplus.block.custom.OvenBlock;
import com.blocklogic.furnacesplus.item.FPItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FurnacesPlus.MODID);

    public static final DeferredBlock<Block> GLASS_KILN = registerBlock("glass_kiln",
            () -> new GlassKilnBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(GlassKilnBlock.LIT) ? 13 : 0)
            ));

    public static final DeferredBlock<Block> KILN = registerBlock("kiln",
            () -> new KilnBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(GlassKilnBlock.LIT) ? 13 : 0)
            ));

    public static final DeferredBlock<Block> FOUNDRY = registerBlock("foundry",
            () -> new FoundryBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(GlassKilnBlock.LIT) ? 13 : 0)
            ));

    public static final DeferredBlock<Block> OVEN = registerBlock("oven",
            () -> new OvenBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(GlassKilnBlock.LIT) ? 13 : 0)
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        FPItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
