package com.koala.simplegreenhouses.interfaces;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;

public class IOItemHandler implements IItemHandler {

    private InputItemHandler input;
    private OutputItemHandler output;

    public IOItemHandler(InputItemHandler input, OutputItemHandler output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public int getSlots() {
        return input.getSlots() + output.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < input.getSlots()) {
            return ItemStack.EMPTY;
        }
        else {
            return output.getStackInSlot(slot - input.getSlots());
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot < input.getSlots()) {
            return input.insertItem(slot, stack, simulate);
        }
        else {
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < input.getSlots()) {
            return ItemStack.EMPTY;
        }
        else {
            return output.extractItem(slot - input.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return input.getSlotLimit(0);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot < input.getSlots()) {
            return stack.is(Tags.Items.FERTILIZERS);
        }
        else {
            return false;
        }
    }
    
}
