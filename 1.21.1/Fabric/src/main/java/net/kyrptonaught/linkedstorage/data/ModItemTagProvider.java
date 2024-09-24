package net.kyrptonaught.linkedstorage.data;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItemTagProvider extends AbstractTagProvider<Item> {

    public ModItemTagProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.add(ModRegistry.COLOR_CHANNEL_PROVIDER_ITEM_TAG).add(ModRegistry.LINKED_CHEST_ITEM, ModRegistry.LINKED_STORAGE_ITEM);
    }
}
