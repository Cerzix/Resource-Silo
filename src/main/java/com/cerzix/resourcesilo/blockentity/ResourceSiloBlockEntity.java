package com.cerzix.resourcesilo.blockentity;

import com.cerzix.resourcesilo.config.ModConfigs;
import com.cerzix.resourcesilo.menu.ResourceSiloMenu;
import com.cerzix.resourcesilo.registry.ModBlockEntities;
import com.cerzix.resourcesilo.registry.ModBlocks;
import com.cerzix.resourcesilo.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResourceSiloBlockEntity extends BlockEntity implements MenuProvider {

    private int stored = 0;
    private int tickCounter = 0;

    public ResourceSiloBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESOURCE_SILO_BE.get(), pos, state);
    }

    // ---- MenuProvider ----
    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.RESOURCE_SILO.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ResourceSiloMenu(id, inv, getBlockPos(), player.level());
    }

    // ---- Exposed to menu/screen ----
    public int getStored() {
        return stored;
    }

    public ItemStack getDisplayedStack() {
        return new ItemStack(ModItems.SUPPLIES.get());
    }

    public void addSupplies(int amount) {
        if (amount <= 0) return;
        stored = Math.min(stored + amount, getMaxStorage());
        setChanged();
    }

    private static int getMaxStorage() {
        return ModConfigs.COMMON.MAX_STORAGE.get();
    }

    public ItemStack takeUpTo(int amount) {
        if (stored <= 0 || amount <= 0) return ItemStack.EMPTY;
        int take = Math.min(amount, stored);
        stored -= take;
        setChanged();
        return new ItemStack(ModItems.SUPPLIES.get(), take);
    }

    // ---- Tick logic (simple signature) ----
    public static void serverTick(ResourceSiloBlockEntity be) {
        Level lvl = be.level;
        if (lvl == null || lvl.isClientSide) return;

        // Pause production if powered by redstone
        if (lvl.hasNeighborSignal(be.getBlockPos())) return;

        int tpi = ModConfigs.COMMON.TICKS_PER_ITEM.get();
        if (tpi <= 0) return;

        be.tickCounter++;
        if (be.tickCounter >= tpi) {
            be.tickCounter = 0;
            if (be.stored < getMaxStorage()) {
                be.stored++;
                be.setChanged();
            }
        }
    }
}
