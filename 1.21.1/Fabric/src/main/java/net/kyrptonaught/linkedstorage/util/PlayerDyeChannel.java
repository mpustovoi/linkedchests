package net.kyrptonaught.linkedstorage.util;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.GameProfileCache;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerDyeChannel extends DyeChannel {
    public UUID playerUUID;
    @Nullable
    private Component playerName;

    public PlayerDyeChannel(UUID playerUUID, byte[] dyeChannel) {
        super(dyeChannel);
        this.playerUUID = playerUUID;
        super.type = 1;
    }

    @Override
    public String getChannelName() {
        return this.playerUUID + ":" + super.getChannelName();
    }

    @Override
    public String getSaveName() {
        return super.getChannelName();
    }

    @Override
    public DyeChannel clone() {
        return new PlayerDyeChannel(this.playerUUID, this.dyeChannel.clone());
    }

    @Override
    public List<Component> getCleanName() {
        if (this.playerName == null) {
            GameProfileCache profileCache = CommonAbstractions.INSTANCE.getMinecraftServer().getProfileCache();
            Optional<GameProfile> optional = profileCache.get(this.playerUUID);

            this.playerName = optional.map(gameProfile -> Component.literal(gameProfile.getName())).orElseGet(
                    () -> Component.translatable("text.linkeditem.unknownplayerdyechannel"));
        }
        List<Component> output = new ArrayList<>(super.getCleanName());
        output.add(Component.translatable("text.linkeditem.playerdyechannel", this.playerName));
        return output;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putUUID("playerid", this.playerUUID);
        return super.toTag(tag);
    }
}
