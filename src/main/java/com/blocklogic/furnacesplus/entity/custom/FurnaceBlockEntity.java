package com.blocklogic.furnacesplus.entity.custom;

import com.blocklogic.furnacesplus.block.custom.FoundryBlock;
import com.blocklogic.furnacesplus.block.custom.GlassKilnBlock;
import com.blocklogic.furnacesplus.block.custom.KilnBlock;
import com.blocklogic.furnacesplus.block.custom.OvenBlock;
import com.blocklogic.furnacesplus.config.FPConfig;
import com.blocklogic.furnacesplus.container.menu.FurnaceMenu;
import com.blocklogic.furnacesplus.entity.FPBlockEntities;
import com.blocklogic.furnacesplus.util.FurnaceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

public class FurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private static final int INPUT_SLOT_START = 0;
    private static final int INPUT_SLOT_COUNT = 3;
    private static final int FUEL_SLOT_START = 3;
    private static final int FUEL_SLOT_COUNT = 3;
    private static final int OUTPUT_SLOT_START = 6;
    private static final int OUTPUT_SLOT_COUNT = 6;
    private static final int TOTAL_SLOTS = 12;

    private int cookingProgress = 0;
    private int maxCookingTime = 0;
    private int burnTime = 0;
    private int maxBurnTime = 0;

    private final FurnaceType furnaceType;

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= INPUT_SLOT_START && slot < INPUT_SLOT_START + INPUT_SLOT_COUNT) {
                return furnaceType.isValidInput(stack, level);
            }
            if (slot >= FUEL_SLOT_START && slot < FUEL_SLOT_START + FUEL_SLOT_COUNT) {
                return getBurnTime(stack) > 0;
            }
            return false;
        }
    };

    private final IItemHandler inputHandler = new RangedWrapper(inventory, INPUT_SLOT_START, INPUT_SLOT_START + INPUT_SLOT_COUNT);
    private final IItemHandler fuelHandler = new RangedWrapper(inventory, FUEL_SLOT_START, FUEL_SLOT_START + FUEL_SLOT_COUNT);
    private final IItemHandler outputHandler = new RangedWrapper(inventory, OUTPUT_SLOT_START, OUTPUT_SLOT_START + OUTPUT_SLOT_COUNT) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookingProgress;
                case 1 -> maxCookingTime;
                case 2 -> burnTime;
                case 3 -> maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookingProgress = value;
                case 1 -> maxCookingTime = value;
                case 2 -> burnTime = value;
                case 3 -> maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public FurnaceBlockEntity(BlockPos pos, BlockState blockState, FurnaceType furnaceType) {
        super(getFurnaceBlockEntityType(furnaceType), pos, blockState);
        this.furnaceType = furnaceType;
    }

    private static BlockEntityType<?> getFurnaceBlockEntityType(FurnaceType type) {
        return switch (type) {
            case GLASS_KILN -> FPBlockEntities.GLASS_KILN_BE.get();
            case KILN -> FPBlockEntities.KILN_BE.get();
            case FOUNDRY -> FPBlockEntities.FOUNDRY_BE.get();
            case OVEN -> FPBlockEntities.OVEN_BE.get();
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.furnacesplus." + furnaceType.getName());
    }

    public FurnaceType getFurnaceType() {
        return furnaceType;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        boolean wasLit = isBurning();
        boolean hasChanged = false;

        if (isBurning()) {
            burnTime--;
        }

        if (canCook()) {
            if (!isBurning() && consumeFuel()) {
                hasChanged = true;
            }

            if (isBurning()) {
                if (maxCookingTime == 0) {
                    maxCookingTime = FPConfig.getFurnaceCookingTime();
                }

                cookingProgress++;
                if (cookingProgress >= maxCookingTime) {
                    cookItems();
                    cookingProgress = 0;
                    maxCookingTime = 0;
                    hasChanged = true;
                }
            }
        } else {
            cookingProgress = 0;
            maxCookingTime = 0;
        }

        if (wasLit != isBurning()) {
            hasChanged = true;
            updateBlockState(level, pos, isBurning());
        }

        if (hasChanged) {
            setChanged();
        }
    }

    private boolean canCook() {
        for (int i = INPUT_SLOT_START; i < INPUT_SLOT_START + INPUT_SLOT_COUNT; i++) {
            ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty()) {
                ItemStack result = furnaceType.getSmeltingResult(inputStack, level);
                if (!result.isEmpty() && canInsertResult(result)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cookItems() {
        for (int i = INPUT_SLOT_START; i < INPUT_SLOT_START + INPUT_SLOT_COUNT; i++) {
            ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty()) {
                ItemStack result = furnaceType.getSmeltingResult(inputStack, level);
                if (!result.isEmpty() && canInsertResult(result)) {
                    insertResult(result.copy());
                    inputStack.shrink(1);
                    return;
                }
            }
        }
    }

    private boolean canInsertResult(ItemStack result) {
        for (int i = OUTPUT_SLOT_START; i < OUTPUT_SLOT_START + OUTPUT_SLOT_COUNT; i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slotStack, result) &&
                    slotStack.getCount() + result.getCount() <= slotStack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void insertResult(ItemStack result) {
        for (int i = OUTPUT_SLOT_START; i < OUTPUT_SLOT_START + OUTPUT_SLOT_COUNT; i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                inventory.setStackInSlot(i, result);
                return;
            }
            if (ItemStack.isSameItemSameComponents(slotStack, result)) {
                int canAdd = Math.min(result.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                if (canAdd > 0) {
                    slotStack.grow(canAdd);
                    result.shrink(canAdd);
                    if (result.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean consumeFuel() {
        for (int i = FUEL_SLOT_START; i < FUEL_SLOT_START + FUEL_SLOT_COUNT; i++) {
            ItemStack fuelStack = inventory.getStackInSlot(i);
            int fuelTime = getBurnTime(fuelStack);
            if (fuelTime > 0) {
                int scaledBurnTime = (fuelTime * FPConfig.getFurnaceCookingTime()) / 200;
                burnTime = scaledBurnTime;
                maxBurnTime = scaledBurnTime;
                fuelStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private int getBurnTime(ItemStack stack) {
        if (level == null) return 0;
        return stack.getBurnTime(null);
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public ContainerData getContainerData() {
        return data;
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FurnaceMenu(i, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("cookingProgress", cookingProgress);
        tag.putInt("maxCookingTime", maxCookingTime);
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putString("furnaceType", furnaceType.getName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        cookingProgress = tag.getInt("cookingProgress");
        maxCookingTime = tag.getInt("maxCookingTime");
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithFullMetadata(pRegistries);
    }

    private void updateBlockState(Level level, BlockPos pos, boolean lit) {
        switch (furnaceType) {
            case GLASS_KILN -> GlassKilnBlock.updateBlockState(level, pos, lit);
            case KILN -> KilnBlock.updateBlockState(level, pos, lit);
            case FOUNDRY -> FoundryBlock.updateBlockState(level, pos, lit);
            case OVEN -> OvenBlock.updateBlockState(level, pos, lit);
        }
    }

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) return inventory;
        Direction blockFacing = getBlockState().getValue(GlassKilnBlock.FACING);

        if (side == Direction.UP) return fuelHandler;
        if (side == Direction.DOWN) return outputHandler;

        if (side == blockFacing.getOpposite()) return fuelHandler;
        return inputHandler;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.GLASS_KILN_BE.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.KILN_BE.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.FOUNDRY_BE.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.OVEN_BE.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
    }
}