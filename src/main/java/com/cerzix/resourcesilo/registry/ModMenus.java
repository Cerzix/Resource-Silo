package com.cerzix.resourcesilo.registry;

import com.cerzix.resourcesilo.ResourceSiloMod;
import com.cerzix.resourcesilo.menu.ResourceSiloMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ResourceSiloMod.MODID);

    public static final RegistryObject<MenuType<ResourceSiloMenu>> RESOURCE_SILO =
            MENUS.register("resource_silo",
                    () -> IForgeMenuType.create((id, inv, buf) ->
                            new ResourceSiloMenu(id, inv, buf.readBlockPos(), inv.player.level())));
}
