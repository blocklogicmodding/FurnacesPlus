package com.blocklogic.furnacesplus.entity;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.block.FPBlocks;
import com.blocklogic.furnacesplus.entity.custom.FurnaceBlockEntity;
import com.blocklogic.furnacesplus.util.FurnaceType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FurnacesPlus.MODID);

    public static final Supplier<BlockEntityType<FurnaceBlockEntity>> GLASS_KILN_BE =
            BLOCK_ENTITIES.register("glass_kiln_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FurnaceBlockEntity(pos, state, FurnaceType.GLASS_KILN),
                    FPBlocks.GLASS_KILN.get()).build(null));

    public static final Supplier<BlockEntityType<FurnaceBlockEntity>> KILN_BE =
            BLOCK_ENTITIES.register("kiln_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FurnaceBlockEntity(pos, state, FurnaceType.KILN),
                    FPBlocks.KILN.get()).build(null));

    public static final Supplier<BlockEntityType<FurnaceBlockEntity>> FOUNDRY_BE =
            BLOCK_ENTITIES.register("foundry_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FurnaceBlockEntity(pos, state, FurnaceType.FOUNDRY),
                    FPBlocks.FOUNDRY.get()).build(null));

    public static final Supplier<BlockEntityType<FurnaceBlockEntity>> OVEN_BE =
            BLOCK_ENTITIES.register("oven_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FurnaceBlockEntity(pos, state, FurnaceType.OVEN),
                    FPBlocks.OVEN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}