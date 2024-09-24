package net.kyrptonaught.linkedstorage.world.level.block.entity;

import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

public class StorageBlockEntity extends OpenableBlockEntity {
    private DyeChannel dyeChannel = DyeChannel.defaultChannel();
    private LinkedInventory linkedInventory;

    public StorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.loadAdditional(compoundTag, registries);
        this.dyeChannel = DyeChannel.fromTag(compoundTag);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.saveAdditional(compoundTag, registries);
        this.dyeChannel.toTag(compoundTag);
    }

    public LinkedInventory getLinkedInventory() {
        if (this.linkedInventory == null) {
            this.updateInventory();
        }
        return this.linkedInventory;
    }

    private void updateInventory() {
        if (!this.level.isClientSide) {
            this.linkedInventory = LinkedStorageMod.getInventory(this.dyeChannel);
        }
    }

    public void setDye(int slot, int dye) {
        this.dyeChannel.setSlot(slot, (byte) dye);
        this.updateInventory();
        this.markUpdated();
    }

    public void setChannel(DyeChannel channel) {
        this.dyeChannel = channel;
        this.updateInventory();
        this.markUpdated();
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public DyeChannel getChannel() {
        return this.dyeChannel;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public int countViewers() {
        return ChannelViewers.getViewersFor(this.dyeChannel.getChannelName()) ? 1 : 0;
    }
}