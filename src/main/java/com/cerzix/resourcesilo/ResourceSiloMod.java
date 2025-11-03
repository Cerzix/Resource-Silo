package com.cerzix.resourcesilo;

import com.cerzix.resourcesilo.config.ModConfigs;
import com.cerzix.resourcesilo.registry.ModBlockEntities;
import com.cerzix.resourcesilo.registry.ModBlocks;
import com.cerzix.resourcesilo.registry.ModItems;
import com.cerzix.resourcesilo.registry.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ResourceSiloMod.MODID)
public class ResourceSiloMod {
    public static final String MODID = "cerzixresourcesilo";

    public ResourceSiloMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);

        IEventBus bus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenus.MENUS.register(bus);
    }
}
