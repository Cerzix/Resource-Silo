package com.cerzix.resourcesilo.menu;

import com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity;
import com.cerzix.resourcesilo.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.cerzix.resourcesilo.registry.ModMenus.RESOURCE_SILO;

public class ResourceSiloMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos pos;

    // Slot position (adjust here to move it)
    private static final int OUT_X = 80;  // center-ish in 176x166
    private static final int OUT_Y = 42;  // move up/down as needed

    private final ContainerLevelAccess access;

    public ResourceSiloMenu(int id, Inventory playerInv, BlockPos pos, Level level) {
        super(RESOURCE_SILO.get(), id);
        this.level = level;
        this.pos = pos;
        this.access = ContainerLevelAccess.create(level, pos);

        // Output slot (client-side preview via screen; this slot lets you take items)
        Container dummy = new SimpleContainer(1);
        this.addSlot(new Slot(dummy, 0, OUT_X, OUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // allow returning Supplies into the silo via placing
                return true;
            }

            @Override
            public ItemStack remove(int amount) {
                ResourceSiloBlockEntity be = getBE();
                return be != null ? be.takeUpTo(Math.min(64, amount)) : ItemStack.EMPTY;
            }

            @Override
            public void set(ItemStack stack) {
                // player put items back -> add to silo and clear slot
                ResourceSiloBlockEntity be = getBE();
                if (be != null && !stack.isEmpty()) {
                    be.addSupplies(stack.getCount());
                }
                super.set(ItemStack.EMPTY);
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

    // Required by AbstractContainerMenu
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Basic: do nothing special (keeps behavior predictable)
        return ItemStack.EMPTY;
    }
}
