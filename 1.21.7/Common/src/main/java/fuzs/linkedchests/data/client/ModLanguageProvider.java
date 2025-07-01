package fuzs.linkedchests.data.client;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addBlock(ModRegistry.LINKED_CHEST_BLOCK, "Linked Chest");
        builder.addItem(ModRegistry.LINKED_POUCH_ITEM, "Linked Pouch");
        builder.add(((LinkedChestBlock) ModRegistry.LINKED_CHEST_BLOCK.value()).getDescriptionComponent(),
                "Grants access to items stored in interdimensional realms.");
    }
}
