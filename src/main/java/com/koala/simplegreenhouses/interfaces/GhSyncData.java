package com.koala.simplegreenhouses.interfaces;

import com.koala.simplegreenhouses.blocks.entities.GhControllerBlockEntity;

import net.minecraft.world.inventory.ContainerData;

public class GhSyncData implements ContainerData
{
	private final GhControllerBlockEntity te;
	
	public GhSyncData(GhControllerBlockEntity te)
	{
		this.te = te;
	}

	@Override
	public int get(int index)
	{
		switch (index)
		{
			case 0:
				return te.progress;
			case 1:
				return te.maxProgress;
			case 2:
				return te.assembled ? 1 : 0;
			case 3:
				return te.fluidHandler.getFluidAmount();
			case 4:
				return te.fluidHandler.getCapacity();
			default:
				return 0;
		}
	}

	@Override
	public void set(int index, int value)
	{
	//noop
	}

	@Override
	public int getCount()
	{
		return 5;
	}

}
