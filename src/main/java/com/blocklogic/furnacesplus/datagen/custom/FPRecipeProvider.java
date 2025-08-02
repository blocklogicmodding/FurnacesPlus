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
                .pattern("BFB")
                .pattern("SSS")
                .define('B', Items.BRICKS)
                .define('F', Items.FURNACE)
                .define('S', Items.SMOOTH_STONE)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.KILN.get())
                .pattern("BBB")
                .pattern("BFB")
                .pattern("TTT")
                .define('B', Items.BRICKS)
                .define('F', Items.FURNACE)
                .define('T', ItemTags.TERRACOTTA)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.FOUNDRY.get())
                .pattern("XXX")
                .pattern("XFX")
                .pattern("IBI")
                .define('F', Items.FURNACE)
                .define('X', Items.SMOOTH_STONE)
                .define('I', Items.IRON_INGOT)
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FPBlocks.OVEN.get())
                .pattern("BBB")
                .pattern("BFB")
                .pattern("SSS")
                .define('B', Items.BRICKS)
                .define('F', Items.FURNACE)
                .define('S', ItemTags.STONE_BRICKS)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(recipeOutput);
    }
}
