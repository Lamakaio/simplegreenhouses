package com.koala.simplegreenhouses.interfaces;

import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OutputItemHandler extends ItemStackHandler
{
	
	public final GhControllerBlockEntity te;
	public boolean forcingInserts = false;
	public float storedExperience = 0F;
	
	public OutputItemHandler(GhControllerBlockEntity te)
	{
		super(12);
		this.te = te;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return this.forcingInserts;
	}
	
	public ItemStack insertCraftResult(ItemStack stack, boolean simulate)
	{
		this.forcingInserts = true;
		ItemStack result = ItemHandlerHelper.insertItemStacked(this, stack, simulate);
		this.forcingInserts = false;
		return result;
	}

	@Override
	protected void onContentsChanged(int slot)
	{
		super.onContentsChanged(slot);
		this.te.setChanged();
		this.te.markOutputInventoryChanged();
	}
	
}
