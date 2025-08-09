package com.koala.simplegreenhouses.interfaces;

import java.util.HashMap;
import java.util.Optional;

import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GreenhouseMenu extends AbstractContainerMenu {
	public static final String TITLE = "container.simplegreenhouses.greenhouse_controller";

	// slot positions
	public static final int SLOT_SPACING = 18;
	public static final int BACKPACK_START_X = 8;
	public static final int OUTPUT_START_X = 88;
	public static final int OUTPUT_START_Y = 25;
	public static final int BACKPACK_START_Y = 99;
	public static final int HOTBAR_START_X = BACKPACK_START_X;
	public static final int HOTBAR_START_Y = 157;
	public static final int INPUT_SLOT_X = 52;
	public static final int INPUT_SLOT_Y = 16;

	// slot counts
	public static final int SLOT_ROWS = 3;
	public static final int SLOT_COLUMNS = 4;
	public static final int BACKPACK_ROWS = 3;
	public static final int PLAYER_COLUMNS = 9;
	public static final int OUTPUT_SLOTS = SLOT_ROWS * SLOT_COLUMNS;
	public static final int INPUT_SLOTS = 1;
	public static final int BACKPACK_SLOTS = BACKPACK_ROWS * PLAYER_COLUMNS;
	public static final int HOTBAR_SLOTS = PLAYER_COLUMNS;

	// slot indices
	public static final int FIRST_INPUT_SLOT = 0;
	public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS;
	public static final int FIRST_HOTBAR_SLOT = FIRST_OUTPUT_SLOT + OUTPUT_SLOTS;
	public static final int FIRST_BACKPACK_SLOT = FIRST_HOTBAR_SLOT + HOTBAR_SLOTS;
	public static final int FIRST_PLAYER_SLOT = FIRST_HOTBAR_SLOT;

	public static final int END_INPUT_SLOTS = FIRST_INPUT_SLOT + INPUT_SLOTS;
	public static final int END_OUTPUT_SLOTS = FIRST_OUTPUT_SLOT + OUTPUT_SLOTS;
	public static final int END_PLAYER_SLOTS = FIRST_BACKPACK_SLOT + BACKPACK_SLOTS;

	/**
	 * Used by the Server to determine whether the player is close enough to use the
	 * Container
	 **/
	private final ContainerData furnaceData;
	private final Optional<GhControllerBlockEntity> serverController;

	public final static HashMap<String, Object> guistate = new HashMap<>();

	/** Container factory for opening the container clientside **/
	public static GreenhouseMenu getClientMenu(int id, Inventory playerInventory) {
		// init client inventory with dummy slots
		return new GreenhouseMenu(id, playerInventory, BlockPos.ZERO, new ItemStackHandler(1),
				new UninsertableItemStackHandler(12), new SimpleContainerData(5),
				Optional.empty());
	}

	/**
	 * Get the server container provider for NetworkHooks.openGui
	 * 
	 * @param te            The TileEntity of the furnace core
	 * @param activationPos The position of the block that the player actually
	 *                      activated to open the container (may be different than
	 *                      te.getPos)
	 * @return
	 */
	public static MenuProvider getServerMenuProvider(GhControllerBlockEntity te, BlockPos activationPos) {
		return new SimpleMenuProvider(
				(id, playerInventory, serverPlayer) -> new GreenhouseMenu(id, playerInventory, activationPos, te.input,
						te.output, te.dataSlot,
						Optional.of(te)),
				Component.translatable(TITLE));
	}

	protected GreenhouseMenu(int id, Inventory playerInventory, BlockPos pos, IItemHandler inputs,
			IItemHandler outputs, ContainerData furnaceData,
			Optional<GhControllerBlockEntity> serverController) {
		super(InterfaceRegistrar.GH_MENU.get(), id);

		Player player = playerInventory.player;
		this.furnaceData = furnaceData;
		this.serverController = serverController;

		this.addSlot(new SlotItemHandler(inputs, 0, INPUT_SLOT_X, INPUT_SLOT_Y));
		
		// add output slots
		for (int row = 0; row < SLOT_ROWS; row++) {
			int y = OUTPUT_START_Y + SLOT_SPACING * row;
			for (int column = 0; column < SLOT_COLUMNS; column++) {
				int x = OUTPUT_START_X + SLOT_SPACING * column;
				int index = row * SLOT_COLUMNS + column;
				this.addSlot(new GhOutputSlot(player, outputs, index, x, y));
			}
		}
		// add hotbar slots
		for (int hotbarSlot = 0; hotbarSlot < PLAYER_COLUMNS; hotbarSlot++) {
			int x = HOTBAR_START_X + SLOT_SPACING * hotbarSlot;
			this.addSlot(new Slot(playerInventory, hotbarSlot, x, HOTBAR_START_Y));
		}

		// add backpack slots
		for (int row = 0; row < BACKPACK_ROWS; row++) {
			int y = BACKPACK_START_Y + SLOT_SPACING * row;
			for (int column = 0; column < PLAYER_COLUMNS; column++) {
				int x = BACKPACK_START_X + SLOT_SPACING * column;
				int index = row * PLAYER_COLUMNS + column + HOTBAR_SLOTS;
				this.addSlot(new Slot(playerInventory, index, x, y));
			}
		}

		this.addDataSlots(furnaceData);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			slotStackCopy = stackInSlot.copy();

			// if this is an input/fuel/output/upgrade slot, try to put the item in the
			// player slots
			if (index < FIRST_PLAYER_SLOT) {
				if (!this.moveItemStackTo(stackInSlot, FIRST_PLAYER_SLOT, END_PLAYER_SLOTS, true)) {
					return ItemStack.EMPTY;
				}
			}
			// otherwise, this is a player slot
			else {
				// otherwise, try to put it in the input slots
				if (this.moveItemStackTo(stackInSlot, FIRST_INPUT_SLOT, END_INPUT_SLOTS, false)) {
					this.serverController.ifPresent(GhControllerBlockEntity::markInputInventoryChanged);
				} else {
					return ItemStack.EMPTY;
				}
			}

			if (stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (stackInSlot.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, stackInSlot);

		}

		return slotStackCopy;
	}

	public int getProgress() {
		return this.furnaceData.get(0);
	}

	public int getMaxProgressValue() {
		return this.furnaceData.get(1);
	}

	public int getAssembled() {
		return this.furnaceData.get(2);
	}

	public int getWaterAmount() {
		return this.furnaceData.get(3);
	}

	public int getMaxWater() {
		return this.furnaceData.get(4);
	}

	// public int getCookProgressionScaled()
	// {
	// int cookProgress = this.getCookProgress();
	// int cookTimeForRecipe =
	// JumboFurnace.get().serverConfig.jumboFurnaceCookTime().get();
	// return cookTimeForRecipe != 0 && cookProgress != 0 ? cookProgress * 24 /
	// cookTimeForRecipe : 0;
	// }

	public int getProgressLeftScaled() {
		int totalBurnTime = this.getMaxProgressValue();
		if (totalBurnTime == 0) {
			totalBurnTime = 250;
		}

		return this.getProgress() * 13 / totalBurnTime;
	}


}
