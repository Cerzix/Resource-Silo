package com.cerzix.resourcesilo.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class ResourceSiloBlockItem extends BlockItem {
    public ResourceSiloBlockItem(Block block, Item.Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        if (ctx.getPlayer() != null && !ctx.getPlayer().isCreative())
            return InteractionResult.FAIL;
        return super.place(ctx);
    }
}
