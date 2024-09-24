package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChannelManager extends SavedData {
    private final InventoryStorage globalInventories = new InventoryStorage("GLOBAL");
    private final Map<UUID, InventoryStorage> personalInventories = new HashMap<>();

    public static SavedData.Factory<ChannelManager> factory() {
        return new SavedData.Factory<>(ChannelManager::new, ChannelManager::load, DataFixTypes.SAVED_DATA_RAIDS);
    }

    public static ChannelManager load(CompoundTag tag, HolderLookup.Provider registries) {
        ChannelManager channelManager = new ChannelManager();
        channelManager.globalInventories.fromTag(tag, registries);
        channelManager.personalInventories.clear();
        CompoundTag personalInvs = tag.getCompound("personalInvs");
        personalInvs.getAllKeys().forEach(uuid -> {
            InventoryStorage personalInv = new InventoryStorage(uuid);
            personalInv.fromTag(personalInvs.getCompound(uuid), registries);
            channelManager.personalInventories.put(UUID.fromString(uuid), personalInv);
        });
        return channelManager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        this.globalInventories.toTag(tag, registries);
        CompoundTag personalInvs = new CompoundTag();
        this.personalInventories.values().forEach(inventoryStorage -> {
            if (inventoryStorage.getInventories().size() > 0)
                personalInvs.put(inventoryStorage.name, inventoryStorage.toTag(new CompoundTag(), registries));
        });
        tag.put("personalInvs", personalInvs);
        return tag;
    }

    public LinkedInventory getInv(DyeChannel dyeChannel) {
        if (dyeChannel instanceof PlayerDyeChannel) {
            return this.getPersonalInv((PlayerDyeChannel) dyeChannel);
        } else {
            return this.globalInventories.getInv(dyeChannel);
        }
    }

    public LinkedInventory getPersonalInv(PlayerDyeChannel dyeChannel) {
        if (!this.personalInventories.containsKey(dyeChannel.playerUUID)) {
            this.personalInventories.put(dyeChannel.playerUUID, new InventoryStorage(dyeChannel.playerUUID.toString()));
        }
        return this.personalInventories.get(dyeChannel.playerUUID).getInv(dyeChannel);
    }

    public InventoryStorage getGlobalInventories() {
        return this.globalInventories;
    }

    public Map<UUID, InventoryStorage> getPersonalInventories() {
        return this.personalInventories;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}