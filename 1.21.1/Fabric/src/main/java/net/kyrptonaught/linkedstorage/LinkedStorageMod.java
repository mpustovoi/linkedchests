package net.kyrptonaught.linkedstorage;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.network.UpdateViewerList;
import net.kyrptonaught.linkedstorage.network.client.OpenStoragePacket;
import net.kyrptonaught.linkedstorage.network.client.SetDyePacket;
import net.kyrptonaught.linkedstorage.util.ChannelManager;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedStorageMod implements ModConstructor {
    public static final String MOD_ID = "linkedstorage";
    public static final String MOD_NAME = "Linked Storage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerServerbound(SetDyePacket.class)
            .registerServerbound(OpenStoragePacket.class)
            .registerClientbound(UpdateViewerList.class);

    private static ChannelManager channelManager;

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ServerLifecycleEvents.STARTED.register((MinecraftServer server) -> {
            channelManager = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(ChannelManager.factory(),
                    MOD_ID
            );
        });
        ServerLifecycleEvents.STOPPED.register((MinecraftServer server) -> {
            channelManager = null;
        });
        ChannelViewers.registerChannelWatcher();
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

    public static LinkedInventory getInventory(DyeChannel dyeChannel) {
        if (channelManager == null) {
            return new LinkedInventory();
        } else {
            return channelManager.getInv(dyeChannel);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
