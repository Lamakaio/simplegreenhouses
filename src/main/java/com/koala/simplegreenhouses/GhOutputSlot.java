package com.koala.simplegreenhouses;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GhOutputSlot extends SlotItemHandler
{
	private int removeCount = 0;
	private final Player player;

	public GhOutputSlot(Player player, IItemHandler itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
		this.player = player;
	}

	@Override
	public void onTake(Player thePlayer, ItemStack stack)
	{
		this.checkTakeAchievements(stack);
		super.onTake(thePlayer, stack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not
	 * ore and wood. Typically increases an internal count then calls
	 * onCrafting(item).
	 */
	@Override
	protected void onQuickCraft(ItemStack stack, int amount)
	{
		this.removeCount += amount;
		this.checkTakeAchievements(stack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not
	 * ore and wood.
	 */
	@Override
	protected void checkTakeAchievements(ItemStack stack)
	{
		stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
		this.removeCount = 0;

	}

}
