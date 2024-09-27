package fuzs.linkedchests.world.level.block.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import fuzs.linkedchests.LinkedChests;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerTickEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DyeChannelManager extends SavedData {
    static final String KEY_CHANNELS = LinkedChests.id("channels").toString();
    public static final Codec<Map<DyeChannel, DyeChannelStorage>> CODEC = Codec.mapPair(
            DyeChannel.CODEC.fieldOf("dye_channel"), DyeChannelStorage.CODEC.fieldOf("storage")).codec().listOf().xmap(
            (List<Pair<DyeChannel, DyeChannelStorage>> list) -> {
                return list.stream().collect(
                        ImmutableMap.<Pair<DyeChannel, DyeChannelStorage>, DyeChannel, DyeChannelStorage>toImmutableMap(
                                Pair::getFirst, Pair::getSecond));
            }, (Map<DyeChannel, DyeChannelStorage> map) -> map.entrySet()
                    .stream()
                    .map((Map.Entry<DyeChannel, DyeChannelStorage> entry) -> new Pair<>(entry.getKey(),
                            entry.getValue()
                    ))
                    .toList());
    private static DyeChannelManager instance;

    private final Map<DyeChannel, DyeChannelStorage> channels;

    private DyeChannelManager() {
        this.channels = new HashMap<>();
    }

    private DyeChannelManager(Map<DyeChannel, DyeChannelStorage> channels) {
        this.channels = new HashMap<>(channels);
    }

    private static SavedData.Factory<DyeChannelManager> factory() {
        return new SavedData.Factory<>(DyeChannelManager::new, DyeChannelManager::load, null);
    }

    private static DyeChannelManager load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE),
                compoundTag.getList(KEY_CHANNELS, Tag.TAG_COMPOUND)
        ).resultOrPartial(LinkedChests.LOGGER::error).map(DyeChannelManager::new).orElseGet(DyeChannelManager::new);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.channels).resultOrPartial(
                LinkedChests.LOGGER::error).ifPresent((Tag tag) -> {
            compoundTag.put(KEY_CHANNELS, tag);
        });
        return compoundTag;
    }

    public static void registerEventHandlers() {
        ServerLifecycleEvents.STARTED.register((MinecraftServer server) -> {
            instance = server.overworld().getDataStorage().computeIfAbsent(DyeChannelManager.factory(),
                    LinkedChests.MOD_ID
            );
        });
        ServerLifecycleEvents.STOPPED.register((MinecraftServer server) -> {
            instance = null;
        });
        ServerTickEvents.END.register((MinecraftServer server) -> {
            // the vanilla recheck runs as a block tick every five ticks while a chest is open
            // since this always runs on the server make it 20
            if (server.getTickCount() % 20 == 0) {
                DyeChannelManager channelManager = instance;
                if (channelManager != null) {
                    channelManager.channels.forEach((DyeChannel dyeChannel, DyeChannelStorage storage) -> {
                        storage.openersCounter().recheckOpeners(dyeChannel, server);
                    });
                }
            }
        });
    }

    public static DyeChannelStorage getStorage(DyeChannel dyeChannel) {
        DyeChannelManager channelManager = instance;
        if (channelManager != null) {
            return channelManager.channels.computeIfAbsent(dyeChannel, DyeChannel::createStorage);
        } else {
            return new DyeChannelStorage(3);
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}