package com.koala.simplegreenhouses.blocks;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.block_entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.block_entities.GhGlassBlockEntity;
import com.koala.simplegreenhouses.interfaces.GreenhouseMenu;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

public class GhGlassBlock extends Block implements EntityBlock {

    public GhGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GhGlassBlock> codec() {
        return SimpleGreenhouses.GH_GLASS_CODEC.value();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GhGlassBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GhGlassBlockEntity glassbe) {
            ItemStack i = player.getMainHandItem();
            if (i.is(Tags.Items.GLASS_BLOCKS)) {
                if (i.getItem() instanceof BlockItem bi) {
                    glassbe.state = bi.getBlock().defaultBlockState();
                    // if (player instanceof ServerPlayer) level.sendBlockUpdated(pos, state, state,
                    // UPDATE_ALL);
                    return ItemInteractionResult.CONSUME;
                }
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
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidstate) {
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
    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (level.getBlockEntity(pos) instanceof GhGlassBlockEntity glassbe) {
            return neighborState == glassbe.state;
        }
        return false;
    }
}
