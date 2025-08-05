package com.koala.simplegreenhouses;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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

    public ArrayList<ResourceKey<LootTable>> cultivatedBlocks = new ArrayList<>();
    public boolean assembled = false;
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

    public LootParams lootParams;

    public GhControllerBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleGreenhouses.GH_CONTROLLER_BLOCK_ENTITY.get(), pos, state);
    }

    protected void serverTick() {
        if (blocked || !assembled) {
            return;
        }
        SimpleGreenhouses.LOGGER.info("tick going");
        if (remainingFertilizer == 0) {
            tryConsumeFertilizer();
        }
        if (remainingFertilizer > 0) {
            remainingFertilizer -= 1;
            progress += 5 * cultivatedBlocks.size();
        } else {
            progress += cultivatedBlocks.size();
        }
        SimpleGreenhouses.LOGGER.info("progress : %d", progress);
        while (progress >= maxProgress) {
            progress -= maxProgress;
            LootTable table = level.getServer().reloadableRegistries().getLootTable(cultivatedBlocks.get(nextCrop));
            List<ItemStack> items = table.getRandomItems(lootParams);
            SimpleGreenhouses.LOGGER.info("rolling loot...");
            for (ItemStack i : items) {
                SimpleGreenhouses.LOGGER.info("got %s", i.getDisplayName().getString());
                ItemStack result = output.insertCraftResult(i, false);
                if (!result.isEmpty()) {
                    blocked = true;
                    return;
                }
            }
            CropBlock
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

    public void tryAssembleMultiblock() {
        SimpleGreenhouses.LOGGER.info("start assemble");
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
        for (BlockPos soil : discovered) {
            SimpleGreenhouses.LOGGER.info("found soil in %d %d", soil.getX(), soil.getZ());
            BlockState above = level.getBlockState(soil.above());
            if (above.is(BlockTags.CROPS)) {
                cultivatedBlocks.add(above.getBlock().getLootTable());
                SimpleGreenhouses.LOGGER.info("found crop", soil.getX(), soil.getZ());
            }

            BlockState soil_state = level.getBlockState(soil);
            int xdiff = soil.getX() - worldPosition.getX();
            int zdiff = soil.getZ() - worldPosition.getZ();

            if (Math.abs(xdiff) > 9 || Math.abs(zdiff) > 9) {
                return;
            }

            soil_state = soil_state.setValue(RichSoilBlock.X, Math.abs(xdiff));
            soil_state = soil_state.setValue(RichSoilBlock.Z, Math.abs(zdiff));

            soil_state = soil_state.setValue(RichSoilBlock.IS_NEG_X, Math.signum(xdiff) == -1.0f);
            soil_state = soil_state.setValue(RichSoilBlock.IS_NEG_Z, Math.signum(zdiff) == -1.0f);

            level.setBlock(soil, soil_state, Block.UPDATE_NONE | Block.UPDATE_SUPPRESS_DROPS);
        }
        SimpleGreenhouses.LOGGER.info("assembled");
        if (!level.isClientSide()) {
            LootParams.Builder builder = new LootParams.Builder((ServerLevel) level);
            // Set our luck value.
            builder.withLuck(0);
            lootParams = builder.create(LootContextParamSet.builder().build());
        }
        assembled = true;

    }
}