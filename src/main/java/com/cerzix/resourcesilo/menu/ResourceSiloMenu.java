package com.cerzix.resourcesilo.menu;

import com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity;
import com.cerzix.resourcesilo.registry.ModBlocks;
import com.cerzix.resourcesilo.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.cerzix.resourcesilo.registry.ModMenus.RESOURCE_SILO;

public class ResourceSiloMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos pos;

    // Slot position
    private static final int OUT_X = 80;
    private static final int OUT_Y = 42;

    private final ContainerLevelAccess access;

    public ResourceSiloMenu(int id, Inventory playerInv, BlockPos pos, Level level) {
        super(RESOURCE_SILO.get(), id);
        this.level = level;
        this.pos = pos;
        this.access = ContainerLevelAccess.create(level, pos);

        // Output-only virtual slot backed by BE storage
        Container dummy = new SimpleContainer(1);
        this.addSlot(new Slot(dummy, 0, OUT_X, OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // NO putting items back in.
                return false;
            }

            @Override
            public ItemStack getItem() {
                ResourceSiloBlockEntity be = getBE();
                if (be == null) return ItemStack.EMPTY;
                int count = Math.min(64, be.getStored()); // display up to 64 even if more inside
                return count > 0 ? new ItemStack(ModItems.SUPPLIES.get(), count) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack remove(int amount) {
                // Any click-based removal takes at most 64
                ResourceSiloBlockEntity be = getBE();
                if (be == null) return ItemStack.EMPTY;
                return be.takeUpTo(Math.min(64, amount));
            }

            @Override
            public void set(ItemStack stack) {
                // Ignore attempts to place items into this slot (output-only)
                // Keep the slot visually empty; content is provided via getItem()
            }
        });

        // Player inventory
        addPlayerInv(playerInv, 8, 84);
    }

    private void addPlayerInv(Inventory inv, int left, int top) {
        for (int r = 0; r < 3; ++r)
            for (int c = 0; c < 9; ++c)
                this.addSlot(new Slot(inv, c + r * 9 + 9, left + c * 18, top + r * 18));
        for (int h = 0; h < 9; ++h)
            this.addSlot(new Slot(inv, h, left + h * 18, top + 58));
    }

    @Nullable
    private ResourceSiloBlockEntity getBE() {
        return level.getBlockEntity(pos) instanceof ResourceSiloBlockEntity be ? be : null;
    }

    public int getStoredCount() {
        ResourceSiloBlockEntity be = getBE();
        return be != null ? be.getStored() : 0;
    }

    public int getOutX() { return OUT_X; }
    public int getOutY() { return OUT_Y; }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.RESOURCE_SILO.get());
    }

    /**
     * Shift-click behavior:
     * - If the user shift-clicks the output slot (index 0), pull up to 64 and try to add to the player's inventory.
     * - Shift-clicking player inventory does nothing special.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Output slot is index 0
        if (index == 0) {
            ResourceSiloBlockEntity be = getBE();
            if (be == null) return ItemStack.EMPTY;

            ItemStack taken = be.takeUpTo(64);
            if (taken.isEmpty()) return ItemStack.EMPTY;

            // Try to move into player inventory
            if (!this.moveItemStackTo(taken, 1, this.slots.size(), true)) {
                // If for some reason move fails, place back in player inventory directly
                player.getInventory().placeItemBackInInventory(taken);
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}
