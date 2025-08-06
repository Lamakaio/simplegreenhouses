package com.koala.simplegreenhouses;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

public class GhGlassBlock extends Block {

    public static final IntegerProperty Y = IntegerProperty.create("y", 0, 10);
    public static final IntegerProperty X = IntegerProperty.create("x", 0, 10);
    public static final BooleanProperty IS_NEG_X = BooleanProperty.create("is_neg_x");
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, 10);
    public static final BooleanProperty IS_NEG_Z = BooleanProperty.create("is_neg_z");

    public GhGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GhGlassBlock> codec() {
        return SimpleGreenhouses.GH_GLASS_CODEC.value();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // this is where the properties are actually added to the state
        pBuilder.add(Y, X, IS_NEG_X, Z, IS_NEG_Z);
    }

    // On use :
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer) {
            int x = state.getValue(IS_NEG_X) ? -state.getValue(X) : state.getValue(X);
            int z = state.getValue(IS_NEG_Z) ? -state.getValue(Z) : state.getValue(Z);
            int y = state.getValue(Y);

            BlockPos controller_pos = new BlockPos(pos.getX() + x, pos.getY() - y, pos.getZ() + z);
            SimpleGreenhouses.LOGGER.info("Trying to get controller at pos {} from {}", controller_pos, pos);
            BlockEntity controller_entity = level.getBlockEntity(controller_pos);
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
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        int x = state.getValue(IS_NEG_X) ? -state.getValue(X) : state.getValue(X);
        int z = state.getValue(IS_NEG_Z) ? -state.getValue(Z) : state.getValue(Z);
        int y = state.getValue(Y);

        BlockPos controller_pos = new BlockPos(pos.getX() + x, pos.getY() - y, pos.getZ() + z);
        SimpleGreenhouses.LOGGER.info("Trying to get controller at pos {} from {}", controller_pos, pos);
        BlockEntity controller_entity = level.getBlockEntity(controller_pos);
        if (controller_entity instanceof GhControllerBlockEntity ghbe) {
            ghbe.assembled = false;
        }
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
