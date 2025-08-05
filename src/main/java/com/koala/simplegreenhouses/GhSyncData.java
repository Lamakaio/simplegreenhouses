package com.koala.simplegreenhouses;

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
				return 0;
			default:
				return 0;
		}
	}

	@Override
	public void set(int index, int value)
	{
		// noop, this is the serverside data and set is only called clientside
	}

	@Override
	public int getCount()
	{
		return 3;
	}

}
