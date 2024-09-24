package fuzs.linkedchests.client;

import fuzs.linkedchests.LinkedChests;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class LinkedChestsClient implements ClientModConstructor {

    public static ModelLayerLocation id(String path) {
        return new ModelLayerLocation(LinkedChests.id(path), "main");
    }
}
