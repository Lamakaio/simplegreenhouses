package com.koala.simplegreenhouses;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.koala.simplegreenhouses.blocks.BlocksRegistrar;
import com.koala.simplegreenhouses.client.ClientProxy;
import com.koala.simplegreenhouses.interfaces.InterfaceRegistrar;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SimpleGreenhouses.MODID)
public class SimpleGreenhouses {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "simplegreenhouses";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registers
    
    


    // config
    public static List<TagKey<Item>> tagsWhitelist = new ArrayList<TagKey<Item>>();
    public static List<Block> blockBlacklist = new ArrayList<Block>();

    // The constructor for the mod class is the first code that is run when your mod
    // is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and
    // pass them in automatically.
    public SimpleGreenhouses(IEventBus modEventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class
        // (SimpleGreenhouses) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in
        // this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.register(InterfaceRegistrar.class);
        InterfaceRegistrar.registerEvent(modEventBus);
        BlocksRegistrar.registerEvent(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config
        // file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // register client thingies
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientProxy.addClientListeners(modEventBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Load config item and item tags
        for (String t : Config.TAGS_WHITELIST.get()) {
            tagsWhitelist.add(TagKey.create(Registries.ITEM, ResourceLocation.parse(t)));
        }
        for (String b : Config.BLOCK_BLACKLIST.get()) {
            blockBlacklist.add(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(b)));
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static boolean isItemCultivable(ItemStack i) {
        for (TagKey<Item> t : tagsWhitelist) {
            if (i.is(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockBlacklisted(Block b) {

        for (Block bb : blockBlacklist) {
            if (b == bb) {
                return true;
            }
        }
        return false;
    }
}
