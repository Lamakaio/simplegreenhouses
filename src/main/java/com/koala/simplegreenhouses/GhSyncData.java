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
				return te.assembled ? 1 : 0;
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
		return 3;
	}

}
