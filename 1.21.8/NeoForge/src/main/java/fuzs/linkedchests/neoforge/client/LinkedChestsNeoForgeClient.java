package fuzs.linkedchests.neoforge.client;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.LinkedChestsClient;
import fuzs.linkedchests.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.linkedchests.data.client.ModModelProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = LinkedChests.MOD_ID, dist = Dist.CLIENT)
public class LinkedChestsNeoForgeClient {

    public LinkedChestsNeoForgeClient() {
        ClientModConstructor.construct(LinkedChests.MOD_ID, LinkedChestsClient::new);
        DataProviderHelper.registerDataProviders(LinkedChests.MOD_ID, ModLanguageProvider::new, ModModelProvider::new);
    }
}
