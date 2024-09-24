package fuzs.linkedchests.neoforge.client;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.LinkedChestsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = LinkedChests.MOD_ID, dist = Dist.CLIENT)
public class LinkedChestsNeoForgeClient {

    public LinkedChestsNeoForgeClient() {
        ClientModConstructor.construct(LinkedChests.MOD_ID, LinkedChestsClient::new);
    }
}
