package com.koala.simplegreenhouses;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GreenhouseUnassembledMenu extends AbstractContainerMenu {
	public static final String TITLE = "container.simplegreenhouses.greenhouse_unassembled_controller";

	// slot positions
	public static final int SLOT_SPACING = 18;
	public static final int BACKPACK_START_X = 8;
	public static final int BACKPACK_START_Y = 99;
	public static final int HOTBAR_START_X = BACKPACK_START_X;
	public static final int HOTBAR_START_Y = 157;

	// slot counts
	public static final int BACKPACK_ROWS = 3;
	public static final int PLAYER_COLUMNS = 9;
	public static final int BACKPACK_SLOTS = BACKPACK_ROWS * PLAYER_COLUMNS;
	public static final int HOTBAR_SLOTS = PLAYER_COLUMNS;

	// slot indices
	public static final int FIRST_HOTBAR_SLOT = 0;
	public static final int FIRST_BACKPACK_SLOT = FIRST_HOTBAR_SLOT + HOTBAR_SLOTS;
	public static final int FIRST_PLAYER_SLOT = FIRST_HOTBAR_SLOT;

	public static final int END_PLAYER_SLOTS = FIRST_BACKPACK_SLOT + BACKPACK_SLOTS;

	/**
	 * Used by the Server to determine whether the player is close enough to use the
	 * Container
	 **/
	private final ContainerData furnaceData;
	private final Optional<GhControllerBlockEntity> serverController;

	public final static HashMap<String, Object> guistate = new HashMap<>();

	/** Container factory for opening the container clientside **/
	public static GreenhouseUnassembledMenu getClientMenu(int id, Inventory playerInventory) {
		// init client inventory with dummy slots
		return new GreenhouseUnassembledMenu(id, playerInventory, BlockPos.ZERO, new SimpleContainerData(4),
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
				(id, playerInventory, serverPlayer) -> new GreenhouseUnassembledMenu(id, playerInventory, activationPos, te.dataSlot,
						Optional.of(te)),
				Component.translatable(TITLE));
	}

	protected GreenhouseUnassembledMenu(int id, Inventory playerInventory, BlockPos pos, ContainerData furnaceData,
			Optional<GhControllerBlockEntity> serverController) {
		super(SimpleGreenhouses.GH_UNASSEMBLED_MENU.get(), id);

		this.furnaceData = furnaceData;
		this.serverController = serverController;

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

	public void setAssembled(int assembled) {
		SimpleGreenhouses.LOGGER.info("Set assembled on client");
		this.furnaceData.set(2, assembled);
	}

	public int getAssembled() {
		return this.furnaceData.get(2);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

}
