package com.blocklogic.furnacesplus.datagen.custom;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.block.FPBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FPBlockTagsProvider extends BlockTagsProvider {
    public FPBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FurnacesPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(FPBlocks.GLASS_KILN.get())
                .add(FPBlocks.KILN.get())
                .add(FPBlocks.FOUNDRY.get());
    }
}
