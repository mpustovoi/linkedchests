package fuzs.linkedchests.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.codec.v1.CodecExtras;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public record DyeChannelStorage(NonNullList<ItemStack> items, LinkedChestOpenersCounter openersCounter) {
    public static final Codec<DyeChannelStorage> CODEC = CodecExtras.NON_NULL_ITEM_STACK_LIST_CODEC.xmap(
            DyeChannelStorage::new, DyeChannelStorage::items);

    public DyeChannelStorage(int containerSize) {
        this(NonNullList.withSize(containerSize, ItemStack.EMPTY));
    }

    public DyeChannelStorage(NonNullList<ItemStack> items) {
        this(items, new LinkedChestOpenersCounter(otherItems -> items == otherItems));
    }
}
