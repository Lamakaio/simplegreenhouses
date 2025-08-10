package com.koala.simplegreenhouses.blocks;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.blocks.entities.GhGlassBlockEntity;
import com.koala.simplegreenhouses.blocks.entities.RichSoilBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlocksRegistrar {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimpleGreenhouses.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleGreenhouses.MODID);
    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_TYPE, SimpleGreenhouses.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, SimpleGreenhouses.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, SimpleGreenhouses.MODID);

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
                    .isViewBlocking(BlocksRegistrar::never)
                    .isSuffocating(BlocksRegistrar::never)));

    public static final DeferredBlock<Block> GH_GLASS_BLOCK_DEFAULT = BLOCKS.registerSimpleBlock(
            "greenhouse_glass_default",
            BlockBehaviour.Properties.of()
                    .destroyTime(0.5f)
                    .explosionResistance(1.0f)
                    .sound(SoundType.GLASS)
                    .noOcclusion().isValidSpawn(Blocks::never)
                    .isViewBlocking(BlocksRegistrar::never)
                    .isSuffocating(BlocksRegistrar::never));

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

    // Block entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GhControllerBlockEntity>> GH_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register(
                    "gh_controller_block_entity",
                    () -> BlockEntityType.Builder.of(
                            (BlockEntityType.BlockEntitySupplier<GhControllerBlockEntity>) GhControllerBlockEntity::new,
                            GH_CONTROLLER_BLOCK.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GhGlassBlockEntity>> GH_GLASS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register(
                    "gh_glass_block_entity",
                    () -> BlockEntityType.Builder.of(
                            (BlockEntityType.BlockEntitySupplier<GhGlassBlockEntity>) GhGlassBlockEntity::new,
                            GH_GLASS_BLOCK.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RichSoilBlockEntity>> RICH_SOIL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register(
                    "rich_soil_block_entity",
                    () -> BlockEntityType.Builder.of(
                            (BlockEntityType.BlockEntitySupplier<RichSoilBlockEntity>) RichSoilBlockEntity::new,
                            RICH_SOIL_BLOCK.get())
                            .build(null));

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

    public static void registerEvent(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
