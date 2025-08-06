package com.koala.simplegreenhouses;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.model.IModelBuilder.Simple;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SimpleGreenhouses.MODID)
public class SimpleGreenhouses {
        // Define mod id in a common place for everything to reference
        public static final String MODID = "simplegreenhouses";
        // Directly reference a slf4j logger
        public static final Logger LOGGER = LogUtils.getLogger();

        // Registers
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, MODID);
        public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister
                        .create(BuiltInRegistries.BLOCK_TYPE, SimpleGreenhouses.MODID);
        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(Registries.BLOCK_ENTITY_TYPE, SimpleGreenhouses.MODID);

        // register block types
        public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RichSoilBlock>> RICH_SOIL_CODEC = BLOCK_TYPES
                        .register(
                                        "rich_soil",
                                        () -> BlockBehaviour.simpleCodec(RichSoilBlock::new));

        public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GhGlassBlock>> GH_GLASS_CODEC = BLOCK_TYPES
                        .register(
                                        "greenhouse_glass",
                                        () -> BlockBehaviour.simpleCodec(GhGlassBlock::new));

        public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GhControllerBlock>> GH_CONTROLLER_CODEC = BLOCK_TYPES
                        .register(
                                        "greenhouse_controller_codec",
                                        () -> BlockBehaviour.simpleCodec(GhControllerBlock::new));

        // blocks and their items
        public static final DeferredBlock<RichSoilBlock> RICH_SOIL_BLOCK = BLOCKS.register("rich_soil",
                        () -> new RichSoilBlock(BlockBehaviour.Properties.of()
                                        .destroyTime(0.5f)
                                        .explosionResistance(1.0f)
                                        .sound(SoundType.GRAVEL)));
        public static final DeferredItem<BlockItem> RICH_SOIL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
                        "rich_soil",
                        RICH_SOIL_BLOCK);

        public static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
                return false;
        }

        public static final DeferredBlock<GhGlassBlock> GH_GLASS_BLOCK = BLOCKS.register("greenhouse_glass",
                        () -> new GhGlassBlock(BlockBehaviour.Properties.of()
                                        .destroyTime(0.5f)
                                        .explosionResistance(1.0f)
                                        .sound(SoundType.GLASS)
                                        .noOcclusion().isValidSpawn(Blocks::never)
                                        .isViewBlocking(SimpleGreenhouses::never)
                                        .isSuffocating(SimpleGreenhouses::never)));
        public static final DeferredItem<BlockItem> GH_GLASS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
                        "greenhouse_glass",
                        GH_GLASS_BLOCK);

        public static final DeferredBlock<GhControllerBlock> GH_CONTROLLER_BLOCK = BLOCKS.register(
                        "greenhouse_controller",
                        () -> new GhControllerBlock(BlockBehaviour.Properties.of()
                                        .destroyTime(2f)
                                        .explosionResistance(10.0f)
                                        .sound(SoundType.METAL)));
        public static final DeferredItem<BlockItem> GH_CONTROLLER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
                        "greenhouse_controller",
                        GH_CONTROLLER_BLOCK);

        // Creative tab
        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SGH_TAB = CREATIVE_MODE_TABS
                        .register("simple_greenhouses", () -> CreativeModeTab.builder()
                                        .title(Component.translatable("itemGroup.simplegreenhouses"))
                                        .withTabsBefore(CreativeModeTabs.COMBAT)
                                        .icon(() -> GH_CONTROLLER_BLOCK_ITEM.get().getDefaultInstance())
                                        .displayItems((parameters, output) -> {
                                                output.accept(GH_CONTROLLER_BLOCK_ITEM.get());
                                                output.accept(RICH_SOIL_BLOCK_ITEM.get());
                                                output.accept(GH_GLASS_BLOCK_ITEM.get());
                                        }).build());

        // Block entities
        public static final Supplier<BlockEntityType<GhControllerBlockEntity>> GH_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
                        .register(
                                        "my_block_entity",
                                        // The block entity type, created using a builder.
                                        () -> BlockEntityType.Builder.of(
                                                        // The supplier to use for constructing the block entity
                                                        // instances.
                                                        GhControllerBlockEntity::new,
                                                        // A vararg of blocks that can have this block entity.
                                                        // This assumes the existence of the referenced blocks as
                                                        // DeferredBlock<Block>s.
                                                        GH_CONTROLLER_BLOCK.get(), GH_CONTROLLER_BLOCK.get())
                                                        // Build using null; vanilla does some datafixer shenanigans
                                                        // with the parameter
                                                        // that we don't need.
                                                        .build(null));

        // menus
        public static final DeferredHolder<MenuType<?>, MenuType<GreenhouseMenu>> GH_MENU = MENUS.register(
                        "greenhouse_menu",
                        () -> new MenuType<>(GreenhouseMenu::getClientMenu, FeatureFlags.VANILLA_SET));

        // The constructor for the mod class is the first code that is run when your mod
        // is loaded.
        // FML will recognize some parameter types like IEventBus or ModContainer and
        // pass them in automatically.
        public SimpleGreenhouses(IEventBus modEventBus, ModContainer modContainer) {

                // Register the commonSetup method for modloading
                modEventBus.addListener(this::commonSetup);

                // Register the Deferred Registers to the mod event bus
                BLOCKS.register(modEventBus);
                ITEMS.register(modEventBus);
                CREATIVE_MODE_TABS.register(modEventBus);
                BLOCK_TYPES.register(modEventBus);
                BLOCK_ENTITY_TYPES.register(modEventBus);
                MENUS.register(modEventBus);

                // Register ourselves for server and other game events we are interested in.
                // Note that this is necessary if and only if we want *this* class
                // (SimpleGreenhouses) to respond directly to events.
                // Do not add this line if there are no @SubscribeEvent-annotated functions in
                // this class, like onServerStarting() below.
                NeoForge.EVENT_BUS.register(this);

                // Register our mod's ModConfigSpec so that FML can create and load the config
                // file for us
                modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

                // register client thingies
                if (FMLEnvironment.dist == Dist.CLIENT) {
                        ClientProxy.addClientListeners(modEventBus);
                }
        }

        private void commonSetup(FMLCommonSetupEvent event) {
                // Some common setup code
                LOGGER.info("HELLO FROM COMMON SETUP");

                if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
                        LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
                }

                LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

                Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
        }

        // You can use SubscribeEvent and let the Event Bus discover methods to call
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
                // Do something when the server starts
                LOGGER.info("HELLO from server starting");
        }

        public static ResourceLocation id(String path) {
                return ResourceLocation.fromNamespaceAndPath(MODID, path);
        }
}
