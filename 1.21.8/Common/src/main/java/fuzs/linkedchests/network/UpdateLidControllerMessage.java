package fuzs.linkedchests.network;

import fuzs.linkedchests.client.handler.DyeChannelLidController;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record UpdateLidControllerMessage(DyeChannel dyeChannel,
                                         boolean shouldBeOpen) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, UpdateLidControllerMessage> STREAM_CODEC = StreamCodec.composite(DyeChannel.STREAM_CODEC,
            UpdateLidControllerMessage::dyeChannel,
            ByteBufCodecs.BOOL,
            UpdateLidControllerMessage::shouldBeOpen,
            UpdateLidControllerMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                DyeChannelLidController.getChestLidController(UpdateLidControllerMessage.this.dyeChannel)
                        .shouldBeOpen(UpdateLidControllerMessage.this.shouldBeOpen);
            }
        };
    }
}
