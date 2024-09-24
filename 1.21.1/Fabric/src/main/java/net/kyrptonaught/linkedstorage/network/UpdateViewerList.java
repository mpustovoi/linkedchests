package net.kyrptonaught.linkedstorage.network;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

import java.util.UUID;

public record UpdateViewerList(String channel, UUID uuid, boolean add) implements ClientboundMessage<UpdateViewerList> {

    @Override
    public ClientMessageListener<UpdateViewerList> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(UpdateViewerList message, Minecraft minecraft, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                if (message.add) {
                    ChannelViewers.addViewerFor(channel, uuid);
                } else {
                    ChannelViewers.removeViewerFor(channel, uuid);
                }
            }
        };
    }
}
