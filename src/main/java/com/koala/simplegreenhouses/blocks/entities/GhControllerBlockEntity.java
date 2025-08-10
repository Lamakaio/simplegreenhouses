package com.koala.simplegreenhouses.blocks.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.koala.simplegreenhouses.Config;
import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.blocks.BlocksRegistrar;
import com.koala.simplegreenhouses.interfaces.GhSyncData;
import com.koala.simplegreenhouses.interfaces.IOItemHandler;
import com.koala.simplegreenhouses.interfaces.InputItemHandler;
import com.koala.simplegreenhouses.interfaces.OutputItemHandler;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class GhControllerBlockEntity extends BlockEntity {

    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String PROGRESS = "progress";
    public static final String MAX_PROGRESS = "max_progress";
    public static final String ASSEMBLED = "assembled";
    public static final String BLOCKED = "blocked";
    public static final String NEXT_CROP = "next_crop";
    public static final String SPEED = "speed";
    public static final String CULTIVATED = "cultivated";
    public static final BlockEntityTicker<GhControllerBlockEntity> SERVER_TICKER = (level, pos, state, core) -> core
            .serverTick();

    public static final Codec<List<BlockPos>> CULTIVATED_BLOCKPOS_CODEC = BlockPos.CODEC.listOf();

    public GhSyncData dataSlot = new GhSyncData(this);

    public List<BlockPos> cultivatedBlocks = new ArrayList<>();
    public boolean assembled = false;
    public String asm_result = "";
    /**
     * cached copy of output slots and inflight recipe results, tossed on relevant
     * updates
     */
    public IItemHandler outputSimulatorCache = null;

    public int progress = 0;
    public int maxProgress = 1000; // TODO add in config or smth
    public boolean blocked = false;
    public boolean hasWater = false;
    public boolean hasFertilizer = false;
    public int speed = 10;
    public int nextCrop = 0;
    public int waterPerCrop = 100;

    public final InputItemHandler input = new InputItemHandler(this);
    public final OutputItemHandler output = new OutputItemHandler(this);
    public final IOItemHandler ioHandler = new IOItemHandler(input, output);
    public final FluidTank fluidHandler;

    public LootParams.Builder lootParams;

    public GhControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistrar.GH_CONTROLLER_BLOCK_ENTITY.get(), pos, state);
        fluidHandler = new FluidTank(25000);
        fluidHandler.setValidator((FluidStack f) -> f.is(FluidTags.WATER));
    }

    protected void serverTick() {
        if (blocked || !assembled) {
            return;
        }
        progress += speed;
        while (progress >= maxProgress) {
            progress -= maxProgress;
            BlockPos cropPos = cultivatedBlocks.get(nextCrop);
            BlockState state = level.getBlockState(cropPos);
            if (SimpleGreenhouses.isBlockBlacklisted(state.getBlock())) {
                List<ItemStack> items = state.getDrops(getLootParams());
                for (ItemStack i : items) {
                    if (state.is(BlockTags.CROPS) || SimpleGreenhouses.isItemCultivable(i)) {
                        ItemStack result = output.insertCraftResult(i, false);
                        updateSpeed();
                        if (!result.isEmpty()) {
                            blocked = true;
                            return;
                        }
                    }
                }
            }
            
            nextCrop = (nextCrop + 1) % cultivatedBlocks.size();
            markOutputInventoryChanged();
        }

    }

    protected void updateSpeed() {
        hasWater = !fluidHandler.drain((int) (waterPerCrop * Config.WATER_USAGE_MULTIPLIER.get()), FluidAction.EXECUTE).isEmpty();
        hasFertilizer = !input.extractItem(0, 1, false).isEmpty();

        speed = cultivatedBlocks.size();
        if (!hasWater) {
            speed /= Config.NOWATER_PENALTY.get();
        }
        if (hasFertilizer) {
            speed *= Config.FERTILIZER_BONUS.get();
        }
        speed *= Config.SPEED_MULTIPLIER.get();
    }

    protected LootParams.Builder getLootParams() {
        if (lootParams == null) {
            lootParams = new LootParams.Builder((ServerLevel) level);
            lootParams.withLuck(0);
            lootParams.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
            lootParams.withParameter(LootContextParams.ORIGIN, Vec3.ZERO);
        }
        return lootParams;
    }

    public void markInputInventoryChanged() {
        setChanged();
    }

    public void markOutputInventoryChanged() {
        blocked = false;
        setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.input.deserializeNBT(registries, compound.getCompound(INPUT));
        this.output.deserializeNBT(registries, compound.getCompound(OUTPUT));
        this.progress = compound.getInt(PROGRESS);
        this.nextCrop = compound.getInt(NEXT_CROP);
        this.speed = compound.getInt(SPEED);
        this.assembled = compound.getBoolean(ASSEMBLED);
        this.blocked = compound.getBoolean(BLOCKED);
        fluidHandler.readFromNBT(registries, compound);

        this.cultivatedBlocks = Lists.newArrayList(CULTIVATED_BLOCKPOS_CODEC
                .parse(NbtOps.INSTANCE, compound.get(CULTIVATED)).result().orElse(List.of()));
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put(INPUT, this.input.serializeNBT(registries));
        compound.put(OUTPUT, this.output.serializeNBT(registries));
        compound.putInt(PROGRESS, this.progress);
        compound.putInt(NEXT_CROP, this.nextCrop);
        compound.putInt(SPEED, this.speed);
        compound.putBoolean(ASSEMBLED, this.assembled);
        compound.putBoolean(BLOCKED, this.blocked);

        fluidHandler.writeToNBT(registries, compound);

        CULTIVATED_BLOCKPOS_CODEC.encodeStart(NbtOps.INSTANCE, this.cultivatedBlocks).ifSuccess(tag -> {
            compound.put(CULTIVATED, tag);
        });
    }

    public String tryAssembleMultiblock() {
        // discover soil
        Set<BlockPos> discovered = new HashSet<BlockPos>();
        Stack<BlockPos> to_discover = new Stack<BlockPos>();
        to_discover.add(worldPosition);
        while (!to_discover.isEmpty()) {
            BlockPos next = to_discover.pop();
            if (level.getBlockState(next.north()).getBlock() == BlocksRegistrar.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.north())) {
                to_discover.push(next.north());
            }
            ;
            if (level.getBlockState(next.south()).getBlock() == BlocksRegistrar.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.south())) {
                to_discover.push(next.south());
            }
            ;
            if (level.getBlockState(next.west()).getBlock() == BlocksRegistrar.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.west())) {
                to_discover.push(next.west());
            }
            ;
            if (level.getBlockState(next.east()).getBlock() == BlocksRegistrar.RICH_SOIL_BLOCK.get()
                    && discovered.add(next.east())) {
                to_discover.push(next.east());
            }

        }

        cultivatedBlocks.clear();
        Set<BlockPos> glass_blocks = new HashSet<BlockPos>();
        for (BlockPos soil : discovered) {

            // set controller position from the soil
            BlockEntity be = level.getBlockEntity(soil);
            int xdiff = worldPosition.getX() - soil.getX();
            int zdiff = worldPosition.getZ() - soil.getZ();

            if (Math.abs(xdiff) > Config.GREENHOUSE_WIDTH.get() || Math.abs(zdiff) > Config.GREENHOUSE_WIDTH.get()) {
                return String.format("Greenhouse soil layer is too big ! +- %d blocks from the controller",
                        Config.GREENHOUSE_WIDTH.get());
            }

            if (be instanceof RichSoilBlockEntity rsbe) {
                rsbe.controllerPos = worldPosition;
            }

            // discover the eventual crop
            BlockPos above_pos = soil.above();
            BlockState above = level.getBlockState(above_pos);
            boolean is_crop = false;

            if (!SimpleGreenhouses.isBlockBlacklisted(above.getBlock())) {
                for (ItemStack d : above.getDrops(getLootParams())) {
                    if (SimpleGreenhouses.isItemCultivable(d)) {
                        is_crop = true;
                    }
                }
                if (above.is(BlockTags.CROPS)) {
                    is_crop = true;
                }
            }

            if (is_crop) {
                cultivatedBlocks.add(above_pos);
            }

            // try to find some glass above
            boolean found_glass = false;
            while (above_pos.getY() < soil.getY() + Config.GREENHOUSE_HEIGHT.get()) {
                if (above.is(BlocksRegistrar.GH_GLASS_BLOCK.get())) {
                    found_glass = true;
                    glass_blocks.add(above_pos);
                    be = level.getBlockEntity(above_pos);
                    if (be instanceof GhGlassBlockEntity glassbe) {
                        glassbe.controllerPos = worldPosition;
                    }
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
                    if (around_state.is(BlocksRegistrar.GH_GLASS_BLOCK.get())) {
                        int xdiff = worldPosition.getX() - around.getX();
                        int zdiff = worldPosition.getZ() - around.getZ();
                        int ydiff = around.getY() - worldPosition.getY();
                        if (Math.abs(xdiff) <= Config.GREENHOUSE_WIDTH.get()
                                && Math.abs(zdiff) <= Config.GREENHOUSE_WIDTH.get()
                                && ydiff <= Config.GREENHOUSE_HEIGHT.get() && ydiff >= 0) {
                            BlockEntity be = level.getBlockEntity(around);
                            if (be instanceof GhGlassBlockEntity glassbe) {
                                glassbe.controllerPos = worldPosition;
                            }

                            stack.add(around);
                        }
                    }

                }
            }
        }
        hasWater = assembled = true;

        return "";
    }
}