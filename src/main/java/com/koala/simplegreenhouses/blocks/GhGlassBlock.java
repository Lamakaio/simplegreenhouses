package com.koala.simplegreenhouses.blocks;

import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.blocks.entities.GhGlassBlockEntity;
import com.koala.simplegreenhouses.interfaces.GreenhouseMenu;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

public class GhGlassBlock extends Block implements EntityBlock {
    public static enum GhGlassModel implements StringRepresentable {
        DEFAULT(ResourceLocation.fromNamespaceAndPath("simplegreenhouses", "block/greenhouse_glass"), "greenhouse_glass"),
        GLASS(ResourceLocation.fromNamespaceAndPath("minecraft", "block/glass"), "glass"),
        BLACK(ResourceLocation.fromNamespaceAndPath("minecraft", "block/black_stained_glass"), "black_stained_glass"),
        BLUE(ResourceLocation.fromNamespaceAndPath("minecraft", "block/blue_stained_glass"), "blue_stained_glass"),
        BROWN(ResourceLocation.fromNamespaceAndPath("minecraft", "block/brown_stained_glass"), "brown_stained_glass"),
        CYAN(ResourceLocation.fromNamespaceAndPath("minecraft", "block/cyan_stained_glass"), "cyan_stained_glass"),
        GRAY(ResourceLocation.fromNamespaceAndPath("minecraft", "block/gray_stained_glass"), "gray_stained_glass"),
        GREEN(ResourceLocation.fromNamespaceAndPath("minecraft", "block/green_stained_glass"), "green_stained_glass"),
        LIME(ResourceLocation.fromNamespaceAndPath("minecraft", "block/lime_stained_glass"), "lime_stained_glass"),
        LIGHT_BLUE(ResourceLocation.fromNamespaceAndPath("minecraft", "block/light_blue_stained_glass"), "light_blue_stained_glass"),
        LIGHT_GRAY(ResourceLocation.fromNamespaceAndPath("minecraft", "block/light_gray_stained_glass"), "light_gray_stained_glass"),
        MAGENTA(ResourceLocation.fromNamespaceAndPath("minecraft", "block/magenta_stained_glass"), "magenta_stained_glass"),
        ORANGE(ResourceLocation.fromNamespaceAndPath("minecraft", "block/orange_stained_glass"), "orange_stained_glass"),
        PINK(ResourceLocation.fromNamespaceAndPath("minecraft", "block/pink_stained_glass"), "pink_stained_glass"),
        PURPLE(ResourceLocation.fromNamespaceAndPath("minecraft", "block/purple_stained_glass"), "purple_stained_glass"),
        RED(ResourceLocation.fromNamespaceAndPath("minecraft", "block/red_stained_glass"), "red_stained_glass"),
        WHITE(ResourceLocation.fromNamespaceAndPath("minecraft", "block/white_stained_glass"), "white_stained_glass"),
        YELLOW(ResourceLocation.fromNamespaceAndPath("minecraft", "block/yellow_stained_glass"), "yellow_stained_glass"),
        CIRCLE_OAK(ResourceLocation.fromNamespaceAndPath("chipped", "block/circle_oak_glass"), "circle_oak_glass"),
        CLEAR_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/clear_leaded_glass"), "clear_leaded_glass"),
        FANCY_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/fancy_leaded_glass"), "fancy_leaded_glass"),
        LARGE_DIAMOND_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/large_diamond_leaded_glass"), "large_diamond_leaded_glass"),
        LEAD_WOVEN(ResourceLocation.fromNamespaceAndPath("chipped", "block/lead_woven_glass"), "lead_woven_glass"),
        OAK_BARED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_bared_glass"), "oak_bared_glass"),
        OAK_BORDERED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_bordered_glass"), "oak_bordered_glass"),
        OAK_DIAMOND_BORDERED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_diamond_bordered_glass"), "oak_diamond_bordered_glass"),
        OAK_HORIZONTAL_LINED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_horizontal_lined_glass"), "oak_horizontal_lined_glass"),
        OAK_LARGE_DIAMOND(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_large_diamond_glass"), "oak_large_diamond_glass"),
        OAK_LINE_BARED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_line_bared_glass"), "oak_line_bared_glass"),
        OAK_ORNATE_BARED(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_ornate_bared_glass"), "oak_ornate_bared_glass"),
        OAK_SNOWFLAKE(ResourceLocation.fromNamespaceAndPath("chipped", "block/oak_snowflake_glass"), "oak_snowflake_glass"),
        ORNATE_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/ornate_leaded_glass"), "ornate_leaded_glass"),
        RASTER_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/raster_leaded_glass"), "raster_leaded_glass"),
        VERTICAL_LEADED(ResourceLocation.fromNamespaceAndPath("chipped", "block/vertical_leaded_glass"), "vertical_leaded_glass"),
        BORDERLESS(ResourceLocation.fromNamespaceAndPath("connectedglass", "borderless"), "borderless"),
        CLEAR(ResourceLocation.fromNamespaceAndPath("connectedglass", "clear"), "clear"),
        SCRATCHED(ResourceLocation.fromNamespaceAndPath("connectedglass", "scratched"), "scratched");

        private final ResourceLocation loc;
        private final String name;

        GhGlassModel(ResourceLocation loc, String name) {
            this.loc = loc;
            this.name = name;
        }

        public ResourceLocation getLoc() {
            return this.loc;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static GhGlassModel from(ResourceKey<Block> key) {
            switch (key.toString()) {
                case "simplegreenhouses:greenhouse_glass":
                    return GhGlassModel.DEFAULT;
                case "minecraft:glass":
                    return GhGlassModel.GLASS;
                case "minecraft:black_stained_glass":
                    return GhGlassModel.BLACK;
                case "minecraft:blue_stained_glass":
                    return GhGlassModel.BLUE;
                case "minecraft:brown_stained_glass":
                    return GhGlassModel.BROWN;
                case "minecraft:cyan_stained_glass":
                    return GhGlassModel.CYAN;
                case "minecraft:gray_stained_glass":
                    return GhGlassModel.GRAY;
                case "minecraft:green_stained_glass":
                    return GhGlassModel.GREEN;
                case "minecraft:lime_stained_glass":
                    return GhGlassModel.LIME;
                case "minecraft:light_blue_stained_glass":
                    return GhGlassModel.LIGHT_BLUE;
                case "minecraft:light_gray_stained_glass":
                    return GhGlassModel.LIGHT_GRAY;
                case "minecraft:magenta_stained_glass":
                    return GhGlassModel.MAGENTA;
                case "minecraft:orange_stained_glass":
                    return GhGlassModel.ORANGE;
                case "minecraft:pink_stained_glass":
                    return GhGlassModel.PINK;
                case "minecraft:purple_stained_glass":
                    return GhGlassModel.PURPLE;
                case "minecraft:red_stained_glass":
                    return GhGlassModel.RED;
                case "minecraft:white_stained_glass":
                    return GhGlassModel.WHITE;
                case "minecraft:yellow_stained_glass":
                    return GhGlassModel.YELLOW;
                case "chipped:circle_oak_glass":
                    return GhGlassModel.CIRCLE_OAK;
                case "chipped:clear_leaded_glass":
                    return GhGlassModel.CLEAR_LEADED;
                case "chipped:fancy_leaded_glass":
                    return GhGlassModel.FANCY_LEADED;
                case "chipped:large_diamond_leaded_glass":
                    return GhGlassModel.LARGE_DIAMOND_LEADED;
                case "chipped:lead_woven_glass":
                    return GhGlassModel.LEAD_WOVEN;
                case "chipped:oak_bared_glass":
                    return GhGlassModel.OAK_BARED;
                case "chipped:oak_bordered_glass":
                    return GhGlassModel.OAK_BORDERED;
                case "chipped:oak_diamond_bordered_glass":
                    return GhGlassModel.OAK_DIAMOND_BORDERED;
                case "chipped:oak_horizontal_lined_glass":
                    return GhGlassModel.OAK_HORIZONTAL_LINED;
                case "chipped:oak_large_diamond_glass":
                    return GhGlassModel.OAK_LARGE_DIAMOND;
                case "chipped:oak_line_bared_glass":
                    return GhGlassModel.OAK_LINE_BARED;
                case "chipped:oak_ornate_bared_glass":
                    return GhGlassModel.OAK_ORNATE_BARED;
                case "chipped:oak_snowflake_glass":
                    return GhGlassModel.OAK_SNOWFLAKE;
                case "chipped:ornate_leaded_glass":
                    return GhGlassModel.ORNATE_LEADED;
                case "chipped:raster_leaded_glass":
                    return GhGlassModel.RASTER_LEADED;
                case "chipped:vertical_leaded_glass":
                    return GhGlassModel.VERTICAL_LEADED;
                case "connectedglass:borderless":
                    return GhGlassModel.BORDERLESS;
                case "connectedglass:clear":
                    return GhGlassModel.CLEAR;
                case "connectedglass:scratched":
                    return GhGlassModel.SCRATCHED;
                default:
                    return null;
            }
        }

    }

    public static EnumProperty<GhGlassModel> GLASS_MODEL = EnumProperty.create("model", GhGlassModel.class);

    public GhGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(GLASS_MODEL, GhGlassModel.DEFAULT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // this is where the properties are actually added to the state
        pBuilder.add(GLASS_MODEL);
    }

    @Override
    public MapCodec<GhGlassBlock> codec() {
        return BlocksRegistrar.GH_GLASS_CODEC.value();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GhGlassBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        ItemStack i = player.getMainHandItem();
        if (i.is(Tags.Items.GLASS_BLOCKS)) {
            if (i.getItem() instanceof BlockItem bi) {
                ResourceKey<Block> key = bi.getBlock().defaultBlockState().getBlockHolder().getKey();
                GhGlassModel model = key == null ? null : GhGlassModel.from(key);

                if (model != null) level.setBlock(pos, state.setValue(GLASS_MODEL, model), UPDATE_CLIENTS);
                return ItemInteractionResult.CONSUME;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    // On use :
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GhGlassBlockEntity glassbe && player instanceof ServerPlayer serverPlayer) {
            BlockEntity controller_entity = level.getBlockEntity(glassbe.controllerPos);
            if (controller_entity instanceof GhControllerBlockEntity ghbe) {
                if (ghbe.assembled) {
                    serverPlayer.openMenu(GreenhouseMenu.getServerMenuProvider(ghbe, pos));
                    return InteractionResult.SUCCESS;

                }
            }
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    // disassemble multiblock on destroy
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GhGlassBlockEntity glassbe) {
                BlockEntity controller_entity = level.getBlockEntity(glassbe.controllerPos);
                if (controller_entity instanceof GhControllerBlockEntity ghbe) {
                    ghbe.assembled = false;
                }
            }
        }
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos,
            FluidState fluidstate) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
    }

    // make it transparent
    @Override
    protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_,
            CollisionContext p_309006_) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

}
