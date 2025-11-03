package com.cerzix.resourcesilo.blockentity;

import com.cerzix.resourcesilo.config.ModConfigs;
import com.cerzix.resourcesilo.menu.ResourceSiloMenu;
import com.cerzix.resourcesilo.registry.ModBlockEntities;
import com.cerzix.resourcesilo.registry.ModBlocks;
import com.cerzix.resourcesilo.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResourceSiloBlockEntity extends BlockEntity implements MenuProvider {

    /** Total items stored (not an inventory; just a counter). */
    private int stored = 0;

    /** Fixed-point accumulator in micro-items (1_000_000 micro = 1 item). */
    private long microAccum = 0L;
    private static final long ONE_ITEM_MICROS = 1_000_000L;

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

    // ---- Exposed API ----
    public int getStored() { return stored; }

    public ItemStack getDisplayedStack() {
        return new ItemStack(ModItems.SUPPLIES.get());
    }

    /** Take up to amount (server side). */
    public ItemStack takeUpTo(int amount) {
        if (amount <= 0 || stored <= 0) return ItemStack.EMPTY;
        int take = Math.min(amount, stored);
        stored -= take;
        setChangedAndNotify();
        return new ItemStack(ModItems.SUPPLIES.get(), take);
    }

    private static int getMaxStorage() {
        return ModConfigs.COMMON.MAX_STORAGE.get();
    }

    // ---- Ticking (fixed-point; requires redstone) ----
    public static void serverTick(ResourceSiloBlockEntity be) {
        Level lvl = be.level;
        if (lvl == null || lvl.isClientSide) return;

        // Only generate while powered
        if (!lvl.hasNeighborSignal(be.getBlockPos())) return;

        if (be.stored >= getMaxStorage()) return;

        long perTickMicros = calcPerTickMicros();
        if (perTickMicros <= 0) return;

        // Fixed-point accumulation
        be.microAccum += perTickMicros;

        if (be.microAccum >= ONE_ITEM_MICROS) {
            long wholeItems = be.microAccum / ONE_ITEM_MICROS;
            if (wholeItems > 0) {
                int space = getMaxStorage() - be.stored;
                int toAdd = (int)Math.min(wholeItems, Math.max(0, space));
                if (toAdd > 0) {
                    be.stored += toAdd;
                    be.microAccum -= (long)toAdd * ONE_ITEM_MICROS;
                    be.setChangedAndNotify();
                } else {
                    // full: don't lose fractional progress
                    be.microAccum = Math.min(be.microAccum, ONE_ITEM_MICROS - 1);
                }
            }
        }
    }

    /** Per-tick rate in micro-items (1e6 units per item). */
    private static long calcPerTickMicros() {
        double ipm = ModConfigs.COMMON.ITEMS_PER_MINUTE.get();
        if (ipm > 0.0) {
            // items per minute -> per tick (20 tps, 1200 ticks/min)
            double perTick = ipm / 1200.0;
            return Math.max(0L, Math.round(perTick * ONE_ITEM_MICROS));
        } else {
            int tpi = ModConfigs.COMMON.TICKS_PER_ITEM.get();
            if (tpi <= 0) return 0L;
            double perTick = 1.0 / (double)tpi;
            return Math.max(0L, Math.round(perTick * ONE_ITEM_MICROS));
        }
    }

    // ---- Persistence & Sync ----
    private void setChangedAndNotify() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Stored", this.stored);
        tag.putLong("MicroAccum", this.microAccum);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.stored = tag.getInt("Stored");
        this.microAccum = tag.getLong("MicroAccum");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Stored", this.stored);
        tag.putLong("MicroAccum", this.microAccum);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
