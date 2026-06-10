package com.samik.taczrecycler.blockentity;

import com.samik.taczrecycler.config.RecyclerConfig;
import com.samik.taczrecycler.menu.RecyclerMenu;
import com.samik.taczrecycler.recipe.RecyclingRecipe;
import com.samik.taczrecycler.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 400; // Default processing time
    private final Random random = new Random();

    public RecyclerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RECYCLER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RecyclerBlockEntity.this.progress;
                    case 1 -> RecyclerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RecyclerBlockEntity.this.progress = pValue;
                    case 1 -> RecyclerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tacz_recycler.recycler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new RecyclerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("recycler.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("recycler.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Optional<RecyclingRecipe> match = level.getRecipeManager()
                .getRecipeFor(RecyclingRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            RecyclingRecipe recipe = match.get();
            this.itemHandler.extractItem(0, 1, false);

            double multiplier = RecyclerConfig.outputMultiplier.get();

            int currentOutSlot = 1;
            for (RecyclingRecipe.Result result : recipe.getResults()) {
                if (currentOutSlot > 3) break; // Ensure we don't exceed the 3 output slots

                if (random.nextFloat() <= (result.chance * multiplier)) {
                    ItemStack outputStack = result.item.copy();
                    ItemStack currentStackInSlot = this.itemHandler.getStackInSlot(currentOutSlot);
                    
                    // Simple logic to add to existing stacks or create new ones
                    if (currentStackInSlot.isEmpty()) {
                        this.itemHandler.setStackInSlot(currentOutSlot, outputStack);
                        currentOutSlot++;
                    } else if (ItemStack.isSameItemSameTags(currentStackInSlot, outputStack) && 
                            currentStackInSlot.getCount() + outputStack.getCount() <= currentStackInSlot.getMaxStackSize()) {
                        currentStackInSlot.grow(outputStack.getCount());
                        currentOutSlot++;
                    } else {
                         // Attempt to find next empty slot if this one is full or different
                         boolean found = false;
                         for(int i = currentOutSlot + 1; i <= 3; i++) {
                             ItemStack nextSlot = this.itemHandler.getStackInSlot(i);
                             if(nextSlot.isEmpty()) {
                                 this.itemHandler.setStackInSlot(i, outputStack);
                                 found = true;
                                 break;
                             } else if(ItemStack.isSameItemSameTags(nextSlot, outputStack) &&
                                     nextSlot.getCount() + outputStack.getCount() <= nextSlot.getMaxStackSize()) {
                                 nextSlot.grow(outputStack.getCount());
                                 found = true;
                                 break;
                             }
                         }
                         if(!found) {
                             Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), outputStack);
                         }
                    }
                }
            }
        }
    }

    private boolean hasRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Optional<RecyclingRecipe> match = level.getRecipeManager()
                .getRecipeFor(RecyclingRecipe.Type.INSTANCE, inventory, level);

        if (match.isPresent()) {
            // Apply config default processing time if recipe doesn't specify (or if you want config to override)
            // Here we use the recipe's value, but fallback to config if it were 0. 
            // Based on plan, processing time is configurable later.
            int recipeTime = match.get().getProcessingTime();
            this.maxProgress = recipeTime > 0 ? recipeTime : RecyclerConfig.defaultProcessingTime.get();
            
            return canInsertAmountIntoOutputSlots(match.get());
        }

        return false;
    }

    private boolean canInsertAmountIntoOutputSlots(RecyclingRecipe recipe) {
        for(int i = 1; i <= 3; i++) {
            if(this.itemHandler.getStackInSlot(i).isEmpty()) {
                return true; 
            }
        }
        return false;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }
}
