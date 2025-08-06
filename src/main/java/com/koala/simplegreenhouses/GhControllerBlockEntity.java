package com.koala.simplegreenhouses;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GhControllerBlockEntity extends BlockEntity {

    public static final String INPUT = "input";
    public static final String FUEL = "fuel";
    public static final String OUTPUT = "output";
    public static final String MULTIPROCESS_UPGRADES = "multiprocess_upgrades";
    public static final String BURN_TIME = "burn_time";
    public static final String BURN_VALUE = "burn_value";
    public static final String RECIPES = "recipes";
    public static final String BACKSTOCK = "backstock";
    public static final BlockEntityTicker<GhControllerBlockEntity> SERVER_TICKER = (level, pos, state, core) -> core
            .serverTick();

    public final InputItemHandler input = new InputItemHandler(this);
    public final OutputItemHandler output = new OutputItemHandler(this);

    public GhSyncData dataSlot = new GhSyncData(this);

    public ArrayList<BlockPos> cultivatedBlocks = new ArrayList<>();
    public boolean assembled = false;
    public String asm_result = "";
    /**
     * cached copy of output slots and inflight recipe results, tossed on relevant
     * updates
     */
    public IItemHandler outputSimulatorCache = null;

    public int progress = 0;
    public int maxProgress = 250; // TODO add in config or smth
    public boolean blocked = false;
    public int nextCrop = 0;

    public int remainingFertilizer = 0;

    public LootParams.Builder lootParams;

    public GhControllerBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleGreenhouses.GH_CONTROLLER_BLOCK_ENTITY.get(), pos, state);
    }

    protected void serverTick() {
        if (blocked || !assembled) {
            return;
        }
        if (remainingFertilizer == 0) {
            tryConsumeFertilizer();
        }
        if (remainingFertilizer > 0) {
            remainingFertilizer -= 1;
            progress += 5 * cultivatedBlocks.size();
        } else {
            progress += cultivatedBlocks.size();
        }
        while (progress >= maxProgress) {
            progress -= maxProgress;
            BlockPos cropPos = cultivatedBlocks.get(nextCrop);

            List<ItemStack> items = level.getBlockState(cropPos).getDrops(lootParams);
            for (ItemStack i : items) {
                ItemStack result = output.insertCraftResult(i, false);
                if (!result.isEmpty()) {
                    blocked = true;
                    return;
                }
            }
            nextCrop = (nextCrop + 1) % cultivatedBlocks.size();
            markOutputInventoryChanged();
        }

    }

    protected void tryConsumeFertilizer() {
        if (!input.extractItem(0, 1, false).isEmpty()) {
            remainingFertilizer += 20;
            SimpleGreenhouses.LOGGER.info("got fertilizer");
        }
    }

    public void markInputInventoryChanged() {
        setChanged();
    }

    public void markOutputInventoryChanged() {
        outputSimulatorCache = null;
        blocked = false;
        setChanged();
    }

    public String tryAssembleMultiblock() {
        SimpleGreenhouses.LOGGER.info("start assemble");
        lootParams = new LootParams.Builder((ServerLevel) level);
        // Set our luck value.
        lootParams.withLuck(0);
        lootParams.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
        lootParams.withParameter(LootContextParams.ORIGIN, Vec3.ZERO);

        // discover soil
        Set<BlockPos> discovered = new HashSet<BlockPos>();
        Stack<BlockPos> to_discover = new Stack<BlockPos>();
        to_discover.add(worldPosition);
        while (!to_discover.isEmpty()) {
            BlockPos next = to_discover.pop();
            if (level.getBlockState(next.north()).getBlock() == SimpleGreenhouses.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.north())) {
                to_discover.push(next.north());
            }
            ;
            if (level.getBlockState(next.south()).getBlock() == SimpleGreenhouses.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.south())) {
                to_discover.push(next.south());
            }
            ;
            if (level.getBlockState(next.west()).getBlock() == SimpleGreenhouses.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.west())) {
                to_discover.push(next.west());
            }
            ;
            if (level.getBlockState(next.east()).getBlock() == SimpleGreenhouses.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.east())) {
                to_discover.push(next.east());
            }

        }

        cultivatedBlocks.clear();
        Set<BlockPos> glass_blocks = new HashSet<BlockPos>();
        for (BlockPos soil : discovered) {
            SimpleGreenhouses.LOGGER.info("found soil in %d %d", soil.getX(), soil.getZ());

            // set controller position from the soil
            BlockState soil_state = level.getBlockState(soil);
            int xdiff = worldPosition.getX() - soil.getX();
            int zdiff = worldPosition.getZ() - soil.getZ();

            if (Math.abs(xdiff) > 9 || Math.abs(zdiff) > 9) {
                return "Greenhouse soil layer is too big ! +- 9 blocks from the controller";
            }
            soil_state = soil_state.setValue(RichSoilBlock.X, Math.abs(xdiff));
            soil_state = soil_state.setValue(RichSoilBlock.Z, Math.abs(zdiff));

            soil_state = soil_state.setValue(RichSoilBlock.IS_NEG_X, Math.signum(xdiff) < 0f);
            soil_state = soil_state.setValue(RichSoilBlock.IS_NEG_Z, Math.signum(zdiff) < 0f);

            level.setBlock(soil, soil_state, Block.UPDATE_NONE | Block.UPDATE_SUPPRESS_DROPS);

            // discover the eventual crop
            BlockPos above_pos = soil.above();
            BlockState above = level.getBlockState(above_pos);
            boolean is_crop = false;
            for (ItemStack d : above.getDrops(lootParams)) {
                if (d.is(Tags.Items.CROPS) || d.is(Tags.Items.SEEDS)) {
                    is_crop = true;
                }
            }

            if (is_crop) {
                cultivatedBlocks.add(above_pos);
                SimpleGreenhouses.LOGGER.info("found crop", soil.getX(), soil.getZ());
            }

            // try to find some glass above
            boolean found_glass = false;
            while (above_pos.getY() < soil.getY() + 10) {
                if (above.is(SimpleGreenhouses.GH_GLASS_BLOCK.get())) {
                    found_glass = true;
                    glass_blocks.add(above_pos);

                    above = above.setValue(GhGlassBlock.X, Math.abs(xdiff));
                    above = above.setValue(GhGlassBlock.Z, Math.abs(zdiff));
                    above = above.setValue(GhGlassBlock.Y, above_pos.getY() - soil.getY());

                    above = above.setValue(GhGlassBlock.IS_NEG_X, Math.signum(xdiff) < 0f);
                    above = above.setValue(GhGlassBlock.IS_NEG_Z, Math.signum(zdiff) < 0f);

                    level.setBlock(above_pos, above, Block.UPDATE_NONE | Block.UPDATE_SUPPRESS_DROPS);
                    break;
                }
                above_pos = above_pos.above();
                above = level.getBlockState(above_pos);
            }
            if (!found_glass) {
                return String.format("Found no glass above soil in %d %d %d", soil.getX(), soil.getY(), soil.getZ());
            }
        }

        // discover additional glass blocks
        Vector<BlockPos> stack = new Vector<BlockPos>(glass_blocks);
        SimpleGreenhouses.LOGGER.info("glass blocks : {}", stack.size());
        while (!stack.isEmpty()) {
            BlockPos next = stack.removeLast();
            Iterable<BlockPos> around_iter = () -> new AbstractIterator<BlockPos>() {
                private int origin_x = next.getX();
                private int origin_y = next.getY();
                private int origin_z = next.getZ();
                private int x = -1;
                private int y = -1;
                private int z = -1;

                protected BlockPos computeNext() {
                    x += 1;
                    if (x > 1) {
                        x = -1;
                        y += 1;
                        if (y > 1) {
                            y = -1;
                            z += 1;
                            if (z > 1)
                                return this.endOfData();
                        }
                    }
                    return new BlockPos(new Vec3i(origin_x + x, origin_y + y, origin_z + z));
                }
            };
            for (BlockPos around : around_iter) {
                if (!glass_blocks.contains(around)) {
                    glass_blocks.add(around);
                    BlockState around_state = level.getBlockState(around);
                    if (around_state.is(SimpleGreenhouses.GH_GLASS_BLOCK.get())) {
                        SimpleGreenhouses.LOGGER.info("found new glass : {}", around);
                        int xdiff = worldPosition.getX() - around.getX();
                        int zdiff = worldPosition.getZ() - around.getZ();
                        int ydiff = around.getY() - worldPosition.getY();
                        if (Math.abs(xdiff) <= 9 && Math.abs(zdiff) <= 9 && ydiff <= 9 && ydiff >= 0) {
                            SimpleGreenhouses.LOGGER.info("Adding it");
                            around_state = around_state.setValue(GhGlassBlock.X, Math.abs(xdiff));
                            around_state = around_state.setValue(GhGlassBlock.Z, Math.abs(zdiff));
                            around_state = around_state.setValue(GhGlassBlock.Y, around.getY() - worldPosition.getY());

                            around_state = around_state.setValue(GhGlassBlock.IS_NEG_X, Math.signum(xdiff) < 0f);
                            around_state = around_state.setValue(GhGlassBlock.IS_NEG_Z, Math.signum(zdiff) < 0f);
                            level.setBlock(around, around_state, Block.UPDATE_NONE | Block.UPDATE_SUPPRESS_DROPS);

                            stack.add(around);
                        }
                    }

                }
            }
        }

        SimpleGreenhouses.LOGGER.info("assembled");
        assembled = true;

        return "";
    }
}