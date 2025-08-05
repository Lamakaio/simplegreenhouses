package com.koala.simplegreenhouses;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.util.TriState;

public class RichSoilBlock extends Block {

    public static final IntegerProperty X = IntegerProperty.create("x", 0, 10);
    public static final BooleanProperty IS_NEG_X = BooleanProperty.create("is_neg_x");
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, 10);
    public static final BooleanProperty IS_NEG_Z = BooleanProperty.create("is_neg_z");


    public RichSoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<RichSoilBlock> codec() {
        return SimpleGreenhouses.RICH_SOIL_CODEC.value();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // this is where the properties are actually added to the state
        pBuilder.add(X, IS_NEG_X, Z, IS_NEG_Z);
    }

    //can sustain any plant (for now)
    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        return TriState.TRUE;
    }
    //And is always fertile
    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
