package com.koala.simplegreenhouses.blocks;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.block_entities.GhControllerBlockEntity;
import com.koala.simplegreenhouses.block_entities.RichSoilBlockEntity;
import com.koala.simplegreenhouses.interfaces.GreenhouseMenu;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.TriState;

public class RichSoilBlock extends Block implements EntityBlock {

    public RichSoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<RichSoilBlock> codec() {
        return SimpleGreenhouses.RICH_SOIL_CODEC.value();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RichSoilBlockEntity(pos, state);
    }

    // can sustain any plant (for now)
    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing,
            BlockState plant) {
        return TriState.TRUE;
    }

    // And is fertile if linked to a controller
    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof RichSoilBlockEntity rsbe) {
            if (level.getBlockEntity(rsbe.controllerPos) instanceof GhControllerBlockEntity ghbe) {
                return ghbe.assembled;
            }
        }
        return false;
    }

    // On use :
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!SimpleGreenhouses.isItemCultivable(player.getMainHandItem()) && player instanceof ServerPlayer serverPlayer
                && be instanceof RichSoilBlockEntity rsbe) {
            if (level.getBlockEntity(rsbe.controllerPos) instanceof GhControllerBlockEntity ghbe) {
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
            if (be instanceof RichSoilBlockEntity rsbe) {
                BlockEntity controller_entity = level.getBlockEntity(rsbe.controllerPos);
                if (controller_entity instanceof GhControllerBlockEntity ghbe) {
                    ghbe.assembled = false;
                }
            }
        }
    }

}
