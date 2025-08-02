package com.blocklogic.furnacesplus.util;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public enum FurnaceType {
    GLASS_KILN("glass_kiln"),
    KILN("kiln"),
    FOUNDRY("foundry");

    private final String name;

    FurnaceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValidInput(ItemStack input, Level level) {
        return switch (this) {
            case GLASS_KILN -> input.is(Items.SAND) || input.is(Items.RED_SAND);
            case KILN -> isStoneSmeltingRecipe(input, level);
            case FOUNDRY -> isMetalSmeltingRecipe(input, level);
        };
    }

    public ItemStack getSmeltingResult(ItemStack input, Level level) {
        return switch (this) {
            case GLASS_KILN -> getGlassResult(input);
            case KILN, FOUNDRY -> getVanillaSmeltingResult(input, level);
        };
    }

    private ItemStack getGlassResult(ItemStack input) {
        if (input.is(Items.SAND) || input.is(Items.RED_SAND)) {
            return new ItemStack(Items.GLASS);
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getVanillaSmeltingResult(ItemStack input, Level level) {
        var recipeInput = new SingleRecipeInput(input);
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .map(recipeHolder -> recipeHolder.value().getResultItem(level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    private boolean isStoneSmeltingRecipe(ItemStack input, Level level) {
        return input.is(ItemTags.STONE_CRAFTING_MATERIALS) ||
                input.is(ItemTags.TERRACOTTA) ||
                input.is(Items.COBBLESTONE) || input.is(Items.COBBLED_DEEPSLATE) ||
                input.is(Items.NETHERRACK) || input.is(Items.CLAY_BALL) || input.is(Items.CLAY) ||
                (hasVanillaSmeltingRecipe(input, level) && isStoneCategory(input));
    }

    private boolean isMetalSmeltingRecipe(ItemStack input, Level level) {
        return input.is(ItemTags.IRON_ORES) || input.is(ItemTags.GOLD_ORES) || input.is(ItemTags.COPPER_ORES) ||
                input.is(Items.RAW_IRON) || input.is(Items.RAW_GOLD) || input.is(Items.RAW_COPPER) ||
                input.is(Items.ANCIENT_DEBRIS) ||
                (hasVanillaSmeltingRecipe(input, level) && isMetalCategory(input));
    }

    private boolean isFoodSmeltingRecipe(ItemStack input, Level level) {
        return input.getFoodProperties(null) != null || input.is(Items.KELP) || input.is(Items.WET_SPONGE) ||
                (hasVanillaSmeltingRecipe(input, level) && input.getFoodProperties(null) != null);
    }

    private boolean hasVanillaSmeltingRecipe(ItemStack input, Level level) {
        var recipeInput = new SingleRecipeInput(input);
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .isPresent();
    }

    private boolean isStoneCategory(ItemStack input) {
        String itemName = input.getItem().toString().toLowerCase();
        return itemName.contains("stone") || itemName.contains("cobble") ||
                itemName.contains("clay") || itemName.contains("sand") ||
                itemName.contains("netherrack");
    }

    private boolean isMetalCategory(ItemStack input) {
        String itemName = input.getItem().toString().toLowerCase();
        return itemName.contains("ore") || itemName.contains("raw_") ||
                itemName.contains("debris") || itemName.contains("scrap");
    }
}