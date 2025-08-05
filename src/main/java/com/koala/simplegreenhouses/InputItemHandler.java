package com.koala.simplegreenhouses;

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
	protected void onContentsChanged(int slot)
	{
		super.onContentsChanged(slot);
		this.te.setChanged();
		this.te.markInputInventoryChanged();
	}
}
