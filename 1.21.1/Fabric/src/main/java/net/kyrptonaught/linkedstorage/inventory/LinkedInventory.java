package net.kyrptonaught.linkedstorage.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class LinkedInventory extends SimpleContainer implements WorldlyContainer {

    public LinkedInventory() {
        super(27);
    }

    @Override
    public int[] getSlotsForFace(Direction var1) {
        return IntStream.iterate(0, i -> ++i).limit(this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }
}