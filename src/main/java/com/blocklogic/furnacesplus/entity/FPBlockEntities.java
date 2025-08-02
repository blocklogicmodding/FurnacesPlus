package com.blocklogic.furnacesplus.entity;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.block.FPBlocks;
import com.blocklogic.furnacesplus.entity.custom.GlassKilnBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FurnacesPlus.MODID);

    public static final Supplier<BlockEntityType<GlassKilnBlockEntity>> GLASS_KILN_BE =
            BLOCK_ENTITIES.register("glass_kiln_be", () -> BlockEntityType.Builder.of(
                    GlassKilnBlockEntity::new, FPBlocks.GLASS_KILN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
