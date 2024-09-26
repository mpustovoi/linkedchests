package fuzs.linkedchests.network;

import fuzs.linkedchests.client.handler.DyeChannelLidController;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public record UpdateLidControllerMessage(DyeChannel dyeChannel,
                                         boolean shouldBeOpen) implements ClientboundMessage<UpdateLidControllerMessage> {

    @Override
    public ClientMessageListener<UpdateLidControllerMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(UpdateLidControllerMessage message, Minecraft minecraft, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                DyeChannelLidController.getChestLidController(message.dyeChannel).shouldBeOpen(message.shouldBeOpen);
            }
        };
    }
}
