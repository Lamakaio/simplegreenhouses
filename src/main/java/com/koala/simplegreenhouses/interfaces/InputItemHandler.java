package com.koala.simplegreenhouses.interfaces;

import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InputItemHandler extends ItemStackHandler
{
	public final GhControllerBlockEntity te;
	
	public InputItemHandler(GhControllerBlockEntity te)
	{
		super(1);
		this.te = te;
	}
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return stack.is(Tags.Items.FERTILIZERS);
	}

	@Override
	protected void onContentsChanged(int slot)
	{
		super.onContentsChanged(slot);
		this.te.setChanged();
		this.te.markInputInventoryChanged();
	}
}
