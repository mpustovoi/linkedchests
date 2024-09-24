package fuzs.linkedchests.neoforge;

import fuzs.linkedchests.LinkedChests;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(LinkedChests.MOD_ID)
public class LinkedChestsNeoForge {

    public LinkedChestsNeoForge() {
        ModConstructor.construct(LinkedChests.MOD_ID, LinkedChests::new);
    }
}
