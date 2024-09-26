package fuzs.linkedchests.neoforge;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.data.ModBlockLootProvider;
import fuzs.linkedchests.data.ModBlockTagProvider;
import fuzs.linkedchests.data.ModItemTagProvider;
import fuzs.linkedchests.data.ModRecipeProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(LinkedChests.MOD_ID)
public class LinkedChestsNeoForge {

    public LinkedChestsNeoForge() {
        ModConstructor.construct(LinkedChests.MOD_ID, LinkedChests::new);
        DataProviderHelper.registerDataProviders(LinkedChests.MOD_ID, ModBlockLootProvider::new,
                ModBlockTagProvider::new, ModItemTagProvider::new, ModRecipeProvider::new
        );
    }
}
