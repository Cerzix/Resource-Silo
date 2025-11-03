package com.cerzix.resourcesilo.registry;

import com.cerzix.resourcesilo.ResourceSiloMod;
import com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ResourceSiloMod.MODID);

    public static final RegistryObject<BlockEntityType<ResourceSiloBlockEntity>> RESOURCE_SILO_BE =
            BLOCK_ENTITIES.register("resource_silo",
                    () -> BlockEntityType.Builder.of(ResourceSiloBlockEntity::new,
                            ModBlocks.RESOURCE_SILO.get()).build(null));
}
