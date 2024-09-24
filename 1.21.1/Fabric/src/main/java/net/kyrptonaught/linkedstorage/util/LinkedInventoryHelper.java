package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.world.level.block.entity.StorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class LinkedInventoryHelper {

    public static void setBlockChannel(DyeChannel channel, Level world, BlockPos pos) {
        StorageBlockEntity blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        blockEntity.setChannel(channel.clone());
    }

    public static void setBlockDye(int slot, int dye, Level world, BlockPos pos) {
        StorageBlockEntity blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        blockEntity.setDye(slot, dye);
    }

    public static void setItemChannel(DyeChannel channel, ItemStack itemStack) {
        CompoundTag compoundTag = new CompoundTag();
        channel.clone().toTag(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
    }

    public static DyeChannel getBlockChannel(Level level, BlockPos pos) {
        StorageBlockEntity blockEntity = (StorageBlockEntity) level.getBlockEntity(pos);
        return blockEntity.getChannel();
    }

    public static DyeChannel getItemChannel(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return DyeChannel.fromTag(customData.copyTag());
    }
}