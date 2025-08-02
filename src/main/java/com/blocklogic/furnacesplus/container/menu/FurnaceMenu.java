package com.blocklogic.furnacesplus.container.menu;

import com.blocklogic.furnacesplus.block.FPBlocks;
import com.blocklogic.furnacesplus.container.FPMenuTypes;
import com.blocklogic.furnacesplus.entity.custom.FurnaceBlockEntity;
import com.blocklogic.furnacesplus.util.FurnaceType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FurnaceMenu extends AbstractContainerMenu {
    public final FurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final FurnaceType furnaceType;

    private static final int INPUT_SLOT_START = 0;
    private static final int INPUT_SLOT_COUNT = 3;
    private static final int FUEL_SLOT_START = 3;
    private static final int FUEL_SLOT_COUNT = 3;
    private static final int OUTPUT_SLOT_START = 6;
    private static final int OUTPUT_SLOT_COUNT = 6;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    public FurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FurnaceMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(getMenuType(((FurnaceBlockEntity) blockEntity).getFurnaceType()), containerId);
        checkContainerSize(inv, 12);
        this.blockEntity = ((FurnaceBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.data = this.blockEntity.getContainerData();
        this.furnaceType = this.blockEntity.getFurnaceType();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(this.data);

        addFurnaceSlots();
    }

    private static MenuType<?> getMenuType(FurnaceType furnaceType) {
        return switch (furnaceType) {
            case GLASS_KILN -> FPMenuTypes.GLASS_KILN_MENU.get();
            case KILN -> FPMenuTypes.KILN_MENU.get();
            case FOUNDRY -> FPMenuTypes.FOUNDRY_MENU.get();
            case OVEN -> FPMenuTypes.OVEN_MENU.get();
        };
    }

    private void addFurnaceSlots() {
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            this.addSlot(new SlotItemHandler(blockEntity.inventory, i, 26 + (i * 18), 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return furnaceType.isValidInput(stack, level);
                }
            });
        }

        for (int i = 0; i < FUEL_SLOT_COUNT; i++) {
            this.addSlot(new SlotItemHandler(blockEntity.inventory, INPUT_SLOT_COUNT + i, 26 + (i * 18), 54) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getBurnTime(null) > 0;
                }
            });
        }

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.inventory,
                        INPUT_SLOT_COUNT + FUEL_SLOT_COUNT + (row * 3) + col,
                        116 + (col * 18), 27 + (row * 18)) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (furnaceType.isValidInput(sourceStack, level)) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                        TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (sourceStack.getBurnTime(null) > 0) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOT_COUNT,
                        TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOT_COUNT + FUEL_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }
        else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOT_COUNT + FUEL_SLOT_COUNT + OUTPUT_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 87 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 146));
        }
    }

    public boolean isBurning() {
        return data.get(2) > 0;
    }

    public int getBurnProgress() {
        int burnTime = data.get(2);
        int maxBurnTime = data.get(3);
        return maxBurnTime != 0 ? burnTime * 14 / maxBurnTime : 0;
    }

    public int getCookProgress() {
        int cookingProgress = data.get(0);
        int maxCookingTime = data.get(1);
        return maxCookingTime != 0 ? cookingProgress * 22 / maxCookingTime : 0;
    }

    public FurnaceType getFurnaceType() {
        return furnaceType;
    }

    @Override
    public boolean stillValid(Player player) {
        Block furnaceBlock = switch (furnaceType) {
            case GLASS_KILN -> FPBlocks.GLASS_KILN.get();
            case KILN -> FPBlocks.KILN.get();
            case FOUNDRY -> FPBlocks.FOUNDRY.get();
            case OVEN -> FPBlocks.OVEN.get();
        };

        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, furnaceBlock);
    }
}