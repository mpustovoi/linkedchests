package fuzs.linkedchests.world.level.block.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.linkedchests.LinkedChests;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerTickEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DyeChannelManager extends SavedData {
    public static final Codec<DyeChannelManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(map(
            DyeChannel.CODEC,
            DyeChannelStorage.CODEC).optionalFieldOf("dye_channels", Collections.emptyMap())
            .forGetter(DyeChannelManager::getDyeChannels)).apply(instance, DyeChannelManager::new));
    public static final SavedDataType<DyeChannelManager> TYPE = new SavedDataType<>(LinkedChests.id(
            "dye_channel_manager").toDebugFileName(), DyeChannelManager::new, CODEC, null);
    private static DyeChannelManager instance;

    private final Map<DyeChannel, DyeChannelStorage> dyeChannels;

    private DyeChannelManager() {
        this.dyeChannels = new Object2ObjectArrayMap<>();
    }

    private DyeChannelManager(Map<DyeChannel, DyeChannelStorage> dyeChannels) {
        this.dyeChannels = new Object2ObjectArrayMap<>(dyeChannels);
    }

    @Deprecated
    public static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return map(keyCodec.fieldOf("key"), valueCodec.fieldOf("value"));
    }

    @Deprecated
    public static <K, V> Codec<Map<K, V>> map(MapCodec<K> keyCodec, MapCodec<V> valueCodec) {
        return Codec.mapPair(keyCodec, valueCodec).codec().listOf().xmap((List<Pair<K, V>> list) -> {
                    return list.stream()
                            .collect(ImmutableMap.<Pair<K, V>, K, V>toImmutableMap(Pair::getFirst, Pair::getSecond));
                },
                (Map<K, V> map) -> map.entrySet()
                        .stream()
                        .map((Map.Entry<K, V> entry) -> new Pair<>(entry.getKey(), entry.getValue()))
                        .toList());
    }

    public static void registerEventHandlers() {
        ServerLifecycleEvents.STARTED.register((MinecraftServer minecraftServer) -> {
            instance = minecraftServer.overworld().getDataStorage().computeIfAbsent(DyeChannelManager.TYPE);
        });
        ServerLifecycleEvents.STOPPED.register((MinecraftServer minecraftServer) -> {
            instance = null;
        });
        ServerTickEvents.END.register((MinecraftServer minecraftServer) -> {
            // the vanilla recheck runs as a block tick every five ticks while a chest is open
            // since this always runs on the server make it 20
            if (minecraftServer.getTickCount() % 20 == 0) {
                DyeChannelManager channelManager = instance;
                if (channelManager != null) {
                    channelManager.dyeChannels.forEach((DyeChannel dyeChannel, DyeChannelStorage storage) -> {
                        storage.openersCounter().recheckOpeners(dyeChannel, minecraftServer);
                    });
                }
            }
        });
    }

    public static DyeChannelStorage getStorage(DyeChannel dyeChannel) {
        DyeChannelManager channelManager = instance;
        if (channelManager != null) {
            if (!channelManager.dyeChannels.containsKey(dyeChannel)) {
                channelManager.setDirty();
            }
            return channelManager.dyeChannels.computeIfAbsent(dyeChannel, DyeChannel::createStorage);
        } else {
            return new DyeChannelStorage(3);
        }
    }

    private Map<DyeChannel, DyeChannelStorage> getDyeChannels() {
        return this.dyeChannels;
    }
}