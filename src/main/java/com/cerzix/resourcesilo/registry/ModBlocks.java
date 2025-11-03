package com.cerzix.resourcesilo.registry;

import com.cerzix.resourcesilo.ResourceSiloMod;
import com.cerzix.resourcesilo.block.ResourceSiloBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ResourceSiloMod.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ResourceSiloMod.MODID);

    public static final RegistryObject<Block> RESOURCE_SILO =
            BLOCKS.register("resource_silo", ResourceSiloBlock::new);

    public static final RegistryObject<Item> RESOURCE_SILO_ITEM =
            ITEMS.register("resource_silo", () -> new BlockItem(RESOURCE_SILO.get(), new Item.Properties()));
}
