package com.koala.simplegreenhouses.blocks.entities;

import com.koala.simplegreenhouses.blocks.BlocksRegistrar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RichSoilBlockEntity extends BlockEntity {

    public static final String CONTROLLER_POS = "controller_pos";

    public BlockPos controllerPos = BlockPos.ZERO;

    public RichSoilBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistrar.RICH_SOIL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.controllerPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, compound.get(CONTROLLER_POS)).result().orElse(BlockPos.ZERO);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);

        BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.controllerPos).ifSuccess(tag -> {
            compound.put(CONTROLLER_POS, tag);
        }); 
    }
}