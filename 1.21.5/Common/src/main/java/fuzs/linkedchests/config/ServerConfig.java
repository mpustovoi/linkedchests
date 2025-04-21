package fuzs.linkedchests.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "Amount of inventory slot rows on a general storage channel.")
    @Config.IntRange(min = 1, max = 9)
    public int inventoryRows = 3;
    @Config(description = "Amount of inventory slot rows on a personal storage channel.")
    @Config.IntRange(min = 1, max = 9)
    public int personalInventoryRows = 6;
}
