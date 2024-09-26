package fuzs.linkedchests.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record DyeChannel(DyeColor leftColor, DyeColor middleColor, DyeColor rightColor, Optional<UUID> uuid) {
    public static final DyeChannel DEFAULT_CHANNEL = new DyeChannel(DyeColor.WHITE);
    public static final Codec<DyeChannel> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(DyeColor.CODEC.listOf()
                            .validate(list -> Util.fixedSize(list, 3))
                            .fieldOf("colors")
                            .forGetter(dyeChannel -> {
                                return List.of(dyeChannel.leftColor, dyeChannel.middleColor, dyeChannel.rightColor);
                            }), UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(DyeChannel::uuid))
                    .apply(instance, (List<DyeColor> colors, Optional<UUID> uuid) -> {
                        return new DyeChannel(colors.get(0), colors.get(1), colors.get(2), uuid);
                    }));
    public static final StreamCodec<ByteBuf, DyeChannel> STREAM_CODEC = StreamCodec.composite(DyeColor.STREAM_CODEC,
            DyeChannel::leftColor, DyeColor.STREAM_CODEC, DyeChannel::leftColor, DyeColor.STREAM_CODEC,
            DyeChannel::leftColor, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), DyeChannel::uuid,
            DyeChannel::new
    );

    public DyeChannel(DyeColor dyeColor) {
        this(dyeColor, dyeColor, dyeColor, Optional.empty());
    }

    public DyeChannel withColorAt(int index, DyeColor dyeColor) {
        return switch (index) {
            case 0 -> this.withLeftColor(dyeColor);
            case 1 -> this.withMiddleColor(dyeColor);
            case 2 -> this.withRightColor(dyeColor);
            default -> throw new RuntimeException();
        };
    }

    public DyeChannel withLeftColor(DyeColor leftColor) {
        return new DyeChannel(leftColor, this.middleColor, this.rightColor, this.uuid);
    }

    public DyeChannel withMiddleColor(DyeColor middleColor) {
        return new DyeChannel(this.leftColor, middleColor, this.rightColor, this.uuid);
    }

    public DyeChannel withRightColor(DyeColor rightColor) {
        return new DyeChannel(this.leftColor, this.middleColor, rightColor, this.uuid);
    }

    public DyeChannel withUUID(@Nullable UUID uuid) {
        return new DyeChannel(this.leftColor, this.middleColor, this.rightColor, Optional.ofNullable(uuid));
    }

    public DyeChannelStorage createStorage() {
        return new DyeChannelStorage(getInventoryRows(this.uuid.isPresent()));
    }

    public static int getInventoryRows(boolean personalChannel) {
        if (personalChannel) {
            return LinkedChests.CONFIG.get(ServerConfig.class).personalInventoryRows;
        } else {
            return LinkedChests.CONFIG.get(ServerConfig.class).inventoryRows;
        }
    }

    public static DyeColor getDyeColor(Item item) {
        return item instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : DyeColor.WHITE;
    }
}
