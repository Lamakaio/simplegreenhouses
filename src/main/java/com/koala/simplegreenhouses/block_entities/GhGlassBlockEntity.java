package com.koala.simplegreenhouses.block_entities;

import com.koala.simplegreenhouses.SimpleGreenhouses;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GhGlassBlockEntity extends BlockEntity {

    public static final String CONTROLLER_POS = "controller_pos";
    public static final String RENDER_MODEL = "render_model";

    public BlockPos controllerPos = BlockPos.ZERO;
    public BlockState state;

    public GhGlassBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleGreenhouses.GH_GLASS_BLOCK_ENTITY.get(), pos, state);
        this.state = SimpleGreenhouses.GH_GLASS_BLOCK_DEFAULT.get().defaultBlockState();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.controllerPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, compound.get(CONTROLLER_POS)).result().orElse(BlockPos.ZERO);
        this.state = BlockState.CODEC.parse(NbtOps.INSTANCE, compound.get(RENDER_MODEL)).result().orElse(SimpleGreenhouses.GH_GLASS_BLOCK_DEFAULT.get().defaultBlockState());
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);

        BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.controllerPos).ifSuccess(tag -> {
            compound.put(CONTROLLER_POS, tag);
        }); 
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.state).ifSuccess(tag -> {
            compound.put(RENDER_MODEL, tag);
        });
    }

    //update be on block update (for renderer)
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}