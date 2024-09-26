package fuzs.linkedchests.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.config.ServerConfig;
import fuzs.linkedchests.util.CodecExtras;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public final class DyeChannelManager extends SavedData {
    public static final Codec<DyeChannelManager> CODEC = Codec.unboundedMap(DyeChannel.CODEC, DyeChannelStorage.CODEC)
            .xmap(DyeChannelManager::new, channelManager -> channelManager.channels);
    private static DyeChannelManager instance;

    private final Map<DyeChannel, DyeChannelStorage> channels;

    private DyeChannelManager() {
        this.channels = new HashMap<>();
    }

    private DyeChannelManager(Map<DyeChannel, DyeChannelStorage> channels) {
        this.channels = new HashMap<>(channels);
    }

    public static SavedData.Factory<DyeChannelManager> factory() {
        // TODO not sure how to handle data fix type
        return new SavedData.Factory<>(DyeChannelManager::new, DyeChannelManager::load, DataFixTypes.SAVED_DATA_RAIDS);
    }

    private static DyeChannelManager load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), compoundTag).resultOrPartial(
                LinkedChests.LOGGER::error).orElseGet(DyeChannelManager::new);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        return CODEC.encode(this, registries.createSerializationContext(NbtOps.INSTANCE), compoundTag).flatMap(
                CodecExtras.mapCompoundTag()).resultOrPartial(LinkedChests.LOGGER::error).orElse(compoundTag);
    }

    public static void registerEventHandlers() {
        ServerLifecycleEvents.STARTED.register((MinecraftServer server) -> {
            instance = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(DyeChannelManager.factory(),
                    LinkedChests.MOD_ID
            );
        });
        ServerLifecycleEvents.STOPPED.register((MinecraftServer server) -> {
            instance = null;
        });
    }

    public static DyeChannelStorage getStorage(DyeChannel dyeChannel, boolean isClientSide) {
        DyeChannelManager channelManager = instance;
        if (!isClientSide && channelManager != null) {
            return channelManager.channels.computeIfAbsent(dyeChannel, DyeChannel::createStorage);
        } else {
            return new DyeChannelStorage(LinkedChests.CONFIG.get(ServerConfig.class).inventoryRows);
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}