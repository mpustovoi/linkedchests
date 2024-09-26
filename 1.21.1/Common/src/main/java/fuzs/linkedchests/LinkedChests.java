package fuzs.linkedchests;

import fuzs.linkedchests.config.ServerConfig;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.network.UpdateLidControllerMessage;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.DyeChannelManager;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedChests implements ModConstructor {
    public static final String MOD_ID = "linkedchests";
    public static final String MOD_NAME = "Linked Chests";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID).registerSerializer(DyeChannel.class,
            DyeChannel.STREAM_CODEC
    ).registerClientbound(UpdateLidControllerMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        DyeChannelManager.registerEventHandlers();
    }

    @Override
    public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
        context.registerCreativeModeTab(CreativeModeTabConfigurator.from(MOD_ID)
                .icon(() -> ModRegistry.LINKED_CHEST_ITEM.value().getDefaultInstance())
                .displayItems((itemDisplayParameters, output) -> {
                    output.accept(ModRegistry.LINKED_CHEST_ITEM.value());
                    output.accept(ModRegistry.LINKED_STORAGE_ITEM.value());
                }));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
