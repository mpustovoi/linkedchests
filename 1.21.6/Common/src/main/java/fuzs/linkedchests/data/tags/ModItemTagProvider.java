package fuzs.linkedchests.data.tags;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItemTagProvider extends AbstractTagProvider<Item> {

    public ModItemTagProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(ItemTags.VANISHING_ENCHANTABLE).add(ModRegistry.LINKED_POUCH_ITEM);
        this.tag(ModRegistry.DYE_CHANNEL_COLOR_PROVIDERS_ITEM_TAG).addOptionalTag("c:dyes");
        this.tag(ModRegistry.PERSONAL_CHANNEL_PROVIDERS_ITEM_TAG).add(Items.DIAMOND);
    }
}
