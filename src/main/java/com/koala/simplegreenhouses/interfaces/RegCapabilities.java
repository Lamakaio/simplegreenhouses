package com.koala.simplegreenhouses.interfaces;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.block_entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.block_entities.GhGlassBlockEntity;
import com.koala.simplegreenhouses.block_entities.RichSoilBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RegCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, // capability to register for
            SimpleGreenhouses.GH_CONTROLLER_BLOCK_ENTITY.get(), // block entity type to register for
            (BlockEntity be, Direction side) -> {
                if (be instanceof GhControllerBlockEntity ghbe) {
                    return ghbe.ioHandler;
                } else {
                    return null;
                }
            });
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK, // capability to register for
            SimpleGreenhouses.GH_CONTROLLER_BLOCK_ENTITY.get(), // block entity type to register for
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
            SimpleGreenhouses.GH_GLASS_BLOCK.get()
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
            SimpleGreenhouses.GH_GLASS_BLOCK.get()
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
            SimpleGreenhouses.RICH_SOIL_BLOCK.get()
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
            SimpleGreenhouses.RICH_SOIL_BLOCK.get()
        );

        
        
    }
}
