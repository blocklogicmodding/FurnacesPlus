package com.blocklogic.furnacesplus.datagen.custom;

import com.blocklogic.furnacesplus.block.FPBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class FPRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public FPRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.GLASS_KILN.get())
                .pattern("BBB")
                .pattern("GFG")
                .pattern("XXX")
                .define('B', Items.BRICKS)
                .define('F', Items.FURNACE)
                .define('X', Items.SMOOTH_STONE)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.KILN.get())
                .pattern("BBB")
                .pattern("SFS")
                .pattern("XXX")
                .define('B', Items.BRICKS)
                .define('F', Items.FURNACE)
                .define('X', Items.SMOOTH_STONE)
                .define('S', Tags.Items.STONES)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.FOUNDRY.get())
                .pattern("BBB")
                .pattern("IFI")
                .pattern("SSS")
                .define('F', Items.BLAST_FURNACE)
                .define('S', Items.SMOOTH_STONE)
                .define('I', Items.IRON_INGOT)
                .define('B', Items.BRICKS)
                .unlockedBy("has_blast_furnace", has(Items.BLAST_FURNACE))
                .save(recipeOutput);
    }
}
