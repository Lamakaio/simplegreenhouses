package com.koala.simplegreenhouses;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GhControllerBlock extends Block implements EntityBlock {

    public GhControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GhControllerBlock> codec() {
        return SimpleGreenhouses.GH_CONTROLLER_CODEC.value();
    }

    // Return a new instance of our block entity here.
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GhControllerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (player instanceof ServerPlayer serverPlayer) {
            if (be instanceof GhControllerBlockEntity ghbe) {
                if (!ghbe.assembled) {
                    ghbe.asm_result = ghbe.tryAssembleMultiblock();
                }
                if (ghbe.assembled) {
                    serverPlayer.openMenu(GreenhouseMenu.getServerMenuProvider(ghbe, pos));
                    return InteractionResult.SUCCESS;
                } else {
                    ((ServerPlayer) player).sendChatMessage(OutgoingChatMessage.create(PlayerChatMessage.system(
                            String.format("[Simple Greenhouses] Error assembling multiblock : %s", ghbe.asm_result))),
                            false, ChatType.bind(ChatType.MSG_COMMAND_INCOMING, player.createCommandSourceStack()));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("unchecked") // Due to generics, an unchecked cast is necessary here.
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        // You can return different tickers here, depending on whatever factors you
        // want. A common use case would be
        // to return different tickers on the client or server, only tick one side to
        // begin with,
        // or only return a ticker for some blockstates (e.g. when using a "my machine
        // is working" blockstate property).
        return type == SimpleGreenhouses.GH_CONTROLLER_BLOCK_ENTITY.get()
                ? (BlockEntityTicker<T>) GhControllerBlockEntity.SERVER_TICKER
                : null;
    }
}
