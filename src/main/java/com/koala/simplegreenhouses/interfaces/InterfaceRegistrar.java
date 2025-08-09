package com.koala.simplegreenhouses.interfaces;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.blocks.BlocksRegistrar;
import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.blocks.entities.GhGlassBlockEntity;
import com.koala.simplegreenhouses.blocks.entities.RichSoilBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class InterfaceRegistrar {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SimpleGreenhouses.MODID);
   
    // menus
    public static final DeferredHolder<MenuType<?>, MenuType<GreenhouseMenu>> GH_MENU = MENUS.register(
            "greenhouse_menu",
            () -> new MenuType<>(GreenhouseMenu::getClientMenu, FeatureFlags.VANILLA_SET));

    public static void registerEvent(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, // capability to register for
            BlocksRegistrar.GH_CONTROLLER_BLOCK_ENTITY.get(), // block entity type to register for
            (BlockEntity be, Direction side) -> {
                if (be instanceof GhControllerBlockEntity ghbe) {
                    return ghbe.ioHandler;
                } else {
                    return null;
                }
            });
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK, // capability to register for
            BlocksRegistrar.GH_CONTROLLER_BLOCK_ENTITY.get(), // block entity type to register for
            (BlockEntity be, Direction side) -> {
                if (be instanceof GhControllerBlockEntity ghbe) {
                    return ghbe.fluidHandler;
                } else {
                    return null;
                }
            });
        event.registerBlock(
            Capabilities.ItemHandler.BLOCK, // capability to register for
            (level, pos, state, be, side) -> {
                if (be instanceof GhGlassBlockEntity glassbe) {
                    BlockEntity cbe = level.getBlockEntity(glassbe.controllerPos);
                    if (cbe instanceof GhControllerBlockEntity ghbe) {
                        return ghbe.ioHandler;
                    }
                }
                return null;
            },
            // blocks to register for
            BlocksRegistrar.GH_GLASS_BLOCK.get()
        );

        event.registerBlock(
            Capabilities.FluidHandler.BLOCK, // capability to register for
            (level, pos, state, be, side) -> {
                if (be instanceof GhGlassBlockEntity glassbe) {
                    BlockEntity cbe = level.getBlockEntity(glassbe.controllerPos);
                    if (cbe instanceof GhControllerBlockEntity ghbe) {
                        return ghbe.fluidHandler;
                    }
                }
                return null;
            },
            // blocks to register for
            BlocksRegistrar.GH_GLASS_BLOCK.get()
        );

        event.registerBlock(
            Capabilities.ItemHandler.BLOCK, // capability to register for
            (level, pos, state, be, side) -> {
                if (be instanceof RichSoilBlockEntity rsbe) {
                    BlockEntity cbe = level.getBlockEntity(rsbe.controllerPos);
                    if (cbe instanceof GhControllerBlockEntity ghbe) {
                        return ghbe.ioHandler;
                    }
                }
                return null;
            },
            // blocks to register for
            BlocksRegistrar.RICH_SOIL_BLOCK.get()
        );

        event.registerBlock(
            Capabilities.FluidHandler.BLOCK, // capability to register for
            (level, pos, state, be, side) -> {
                if (be instanceof RichSoilBlockEntity rsbe) {
                    BlockEntity cbe = level.getBlockEntity(rsbe.controllerPos);
                    if (cbe instanceof GhControllerBlockEntity ghbe) {
                        return ghbe.fluidHandler;
                    }
                }
                return null;
            },
            // blocks to register for
            BlocksRegistrar.RICH_SOIL_BLOCK.get()
        );

        
        
    }
}
