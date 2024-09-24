package net.kyrptonaught.linkedstorage.util;

import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

public class DyeChannel implements Cloneable {
    public byte[] dyeChannel;
    protected int type = 0;

    public DyeChannel(byte[] dyeChannel) {
        this.dyeChannel = dyeChannel;
    }

    public void setSlot(int slot, byte dye) {
        this.dyeChannel[slot] = dye;
    }

    public String getChannelName() {
        return this.dyeChannel[0] + ":" + this.dyeChannel[1] + ":" + this.dyeChannel[2];
    }

    public List<Component> getCleanName() {
        Component dyechannel = Component.translatable("item.minecraft.firework_star." + DyeColor.byId(this.dyeChannel[0]).getName()).append(", ")
                .append(Component.translatable("item.minecraft.firework_star." + DyeColor.byId(this.dyeChannel[1]).getName())).append(", ")
                .append(Component.translatable("item.minecraft.firework_star." + DyeColor.byId(this.dyeChannel[2]).getName()));

        return List.of(Component.translatable("text.linkeditem.channel", dyechannel));
    }

    public String getSaveName() {
        return this.getChannelName();
    }

    public DyeChannel clone() {
        return new DyeChannel(this.dyeChannel.clone());
    }

    public static DyeChannel defaultChannel() {
        return new DyeChannel(new byte[]{(byte) DyeColor.WHITE.getId(), (byte) DyeColor.WHITE.getId(), (byte) DyeColor.WHITE.getId()});
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DyeChannel otherChannel)) return false;
        if (this.type != otherChannel.type)
            return false;
        if (otherChannel instanceof PlayerDyeChannel)
            if (!((PlayerDyeChannel) otherChannel).playerUUID.equals(((PlayerDyeChannel) this).playerUUID))
                return false;

        return this.getChannelName().equals(otherChannel.getChannelName());
    }

    public PlayerDyeChannel toPlayerDyeChannel(UUID playerid) {
        return new PlayerDyeChannel(playerid, this.dyeChannel.clone());
    }

    public void toBuf(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.toTag(new CompoundTag()));
    }

    public static DyeChannel fromBuf(FriendlyByteBuf buffer) {
        return fromTag(buffer.readNbt());
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("type", this.type);
        tag.putByteArray("dyechannel", this.dyeChannel);
        return tag;
    }

    public static DyeChannel fromTag(CompoundTag tag) {
        DyeChannel dyeChannel = defaultChannel();
        if (tag.contains("dyechannel", 11)) {
            int[] oldChannel = tag.getIntArray("dyechannel");
            dyeChannel = new DyeChannel(new byte[]{(byte) oldChannel[0], (byte) oldChannel[1], (byte) oldChannel[2]});
        }
        if (tag.contains("dyechannel", 7))
            dyeChannel = new DyeChannel(tag.getByteArray("dyechannel"));
        int type = tag.getInt("type");
        if (type == 1)
            return dyeChannel.toPlayerDyeChannel(tag.getUUID("playerid"));
        return dyeChannel;
    }
}
