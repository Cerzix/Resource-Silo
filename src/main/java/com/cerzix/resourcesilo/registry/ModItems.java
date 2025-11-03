package com.cerzix.resourcesilo.registry;

import com.cerzix.resourcesilo.ResourceSiloMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ResourceSiloMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ResourceSiloMod.MODID);

    public static final RegistryObject<Item> SUPPLIES = ITEMS.register(
            "supplies",
            () -> new Item(new Item.Properties().stacksTo(2000)) // <-- important
    );

    public static final RegistryObject<Item> RESOURCE_SILO_ITEM =
            ITEMS.register("resource_silo",
                    () -> new BlockItem(ModBlocks.RESOURCE_SILO.get(), new Item.Properties()));

    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) e.accept(RESOURCE_SILO_ITEM.get());
        if (e.getTabKey() == CreativeModeTabs.INGREDIENTS) e.accept(SUPPLIES.get());
    }
}
