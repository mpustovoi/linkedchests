package fuzs.linkedchests.fabric;

import fuzs.linkedchests.LinkedChests;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class LinkedChestsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(LinkedChests.MOD_ID, LinkedChests::new);
    }
}
