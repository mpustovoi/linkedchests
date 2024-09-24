package net.kyrptonaught.linkedstorage.network.client;

import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.DyeItem;

public record SetDyePacket(int slot, BlockPos blockPos) implements ServerboundMessage<SetDyePacket> {

    @Override
    public ServerMessageListener<SetDyePacket> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(SetDyePacket message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                int dye = ((DyeItem) player.getMainHandItem().getItem()).getDyeColor().getId();
                LinkedInventoryHelper.setBlockDye(message.slot, dye, level, message.blockPos);
                if (!player.isCreative()) {
                    player.getMainHandItem().shrink(1);
                }
            }
        };
    }
}
