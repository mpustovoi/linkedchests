package fuzs.linkedchests.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.linkedchests.util.CodecExtras;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;

public record DyeChannelStorage(NonNullList<ItemStack> items,
                                ContainerOpenersCounter openersCounter) {
    public static final Codec<DyeChannelStorage> CODEC = CodecExtras.NON_NULL_ITEM_STACK_LIST_CODEC.xmap(
            DyeChannelStorage::new, DyeChannelStorage::items);

    public DyeChannelStorage(int inventoryRows) {
        this(NonNullList.withSize(inventoryRows * 9, ItemStack.EMPTY));
    }

    public DyeChannelStorage(NonNullList<ItemStack> items) {
        this(items, new LinkedChestOpenersCounter(otherItems -> items == otherItems));
    }
}
