package fuzs.linkedchests.fabric.client;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.LinkedChestsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.LinkedStorageModClient;

public class LinkedChestsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
//        ClientModConstructor.construct(LinkedChests.MOD_ID, LinkedChestsClient::new);
        ClientModConstructor.construct(LinkedStorageMod.MOD_ID, LinkedStorageModClient::new);
    }
}
