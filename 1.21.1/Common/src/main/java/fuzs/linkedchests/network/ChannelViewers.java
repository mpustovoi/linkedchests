package fuzs.linkedchests.network;

import fuzs.puzzleslib.api.event.v1.server.ServerTickEvents;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.world.inventory.LinkedMenu;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelViewers {
    private static final ConcurrentHashMap<String, Set<UUID>> CHANNEL_VIEWERS = new ConcurrentHashMap<>();

    public static Boolean getViewersFor(String channel) {
        if (!CHANNEL_VIEWERS.containsKey(channel)) {
            return false;
        } else {
            return !CHANNEL_VIEWERS.get(channel).isEmpty();
        }
    }

    static void addViewerFor(String channel, UUID uuid) {
        if (!CHANNEL_VIEWERS.containsKey(channel)) {
            CHANNEL_VIEWERS.put(channel, ConcurrentHashMap.newKeySet());
        }
        CHANNEL_VIEWERS.get(channel).add(uuid);
    }

    public static void addViewerFor(String channel, Player player) {
        addViewerFor(channel, player.getUUID());
        if (!player.level().isClientSide) {
            PlayerSet playerSet = PlayerSet.ofAll(player.getServer());
            LinkedChests.NETWORK.sendMessage(playerSet, new UpdateViewerList(channel, player.getUUID(), true));
        }
    }

    static void removeViewerFor(String channel, UUID player) {
        CHANNEL_VIEWERS.getOrDefault(channel, ConcurrentHashMap.newKeySet()).remove(player);
    }

    private static void removeViewerForServer(String channel, UUID player, MinecraftServer server) {
        removeViewerFor(channel, player);
        PlayerSet playerSet = PlayerSet.ofAll(server);
        LinkedChests.NETWORK.sendMessage(playerSet, new UpdateViewerList(channel, player, false));
    }

    public static void registerChannelWatcher() {
        ServerTickEvents.START.register(server -> {
            for (String channel : ChannelViewers.CHANNEL_VIEWERS.keySet())
                for (UUID uuid : ChannelViewers.CHANNEL_VIEWERS.get(channel)) {
                    Player player = server.getPlayerList().getPlayer(uuid);
                    if (player == null || !(player.containerMenu instanceof LinkedMenu)) {
                        removeViewerForServer(channel, uuid, server);
                    }
                }
        });
    }
}