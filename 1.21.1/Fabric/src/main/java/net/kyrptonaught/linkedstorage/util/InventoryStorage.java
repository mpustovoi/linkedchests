package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import java.util.HashMap;

public class InventoryStorage {
    private final HashMap<String, LinkedInventory> inventories = new HashMap<>();
    public String name;

    public InventoryStorage(String name) {
        this.name = name;
    }

    public void fromTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.inventories.clear();
        CompoundTag invs = tag.getCompound("invs");
        for (String key : invs.getAllKeys()) {
            this.inventories.put(key, this.fromList(invs.getCompound(key), registries));
        }
    }

    public CompoundTag toTag(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag invs = new CompoundTag();
        for (String key : this.inventories.keySet()) {
            if (!this.inventories.get(key).isEmpty())
                invs.put(key, ContainerHelper.saveAllItems(new CompoundTag(), this.toList(this.inventories.get(key)), registries));
        }
        tag.put("invs", invs);
        return tag;
    }

    public LinkedInventory getInv(DyeChannel dyeChannel) {
        String channel = dyeChannel.getSaveName();
        if (!this.inventories.containsKey(channel)) this.inventories.put(channel, new LinkedInventory());
        return this.inventories.get(channel);
    }

    private NonNullList<ItemStack> toList(Container inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getContainerSize(); i++)
            stacks.set(i, inv.getItem(i));
        return stacks;
    }

    private LinkedInventory fromList(CompoundTag tag, HolderLookup.Provider registries) {
        LinkedInventory inventory = new LinkedInventory();
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks, registries);
        for (int i = 0; i < stacks.size(); i++)
            inventory.setItem(i, stacks.get(i));
        return inventory;
    }

    public HashMap<String, LinkedInventory> getInventories() {
        return this.inventories;
    }
}
