package net.kyrptonaught.linkedstorage.network.client;

import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.kyrptonaught.linkedstorage.inventory.LinkedMenu;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record OpenStoragePacket(BlockPos blockPos) implements ServerboundMessage<OpenStoragePacket> {

    @Override
    public ServerMessageListener<OpenStoragePacket> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(OpenStoragePacket message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                DyeChannel dyeChannel = LinkedInventoryHelper.getBlockChannel(level, message.blockPos);
                LinkedMenu.openMenu(player, dyeChannel);
            }
        };
    }
}
