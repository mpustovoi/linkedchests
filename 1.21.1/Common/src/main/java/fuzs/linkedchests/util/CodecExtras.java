package fuzs.linkedchests.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class CodecExtras {
    public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = nonNullList(ItemStack.CODEC,
            Predicate.not(ItemStack::isEmpty), ItemStack.EMPTY
    );

    private CodecExtras() {
        // NO-OP
    }

    public static <T> Codec<NonNullList<T>> nonNullList(Codec<T> codec, Predicate<T> filter, @Nullable T defaultValue) {
        return RecordCodecBuilder.create(instance -> {
            return instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("size").forGetter(NonNullList::size), Codec.mapPair(
                            ExtraCodecs.POSITIVE_INT.fieldOf("slot"), codec.fieldOf("item"))
                    .codec()
                    .listOf()
                    .fieldOf("items")
                    .forGetter((NonNullList<T> items) -> {
                        return IntStream.range(0, items.size())
                                .mapToObj(index -> new Pair<>(index, items.get(index)))
                                .filter(pair -> filter.test(pair.getSecond()))
                                .toList();
                    })).apply(instance, (Integer size, List<Pair<Integer, T>> items) -> {
                NonNullList<T> nonNullList = defaultValue != null ? NonNullList.withSize(size, defaultValue) :
                        NonNullList.createWithCapacity(size);
                for (Pair<Integer, T> pair : items) {
                    nonNullList.set(pair.getFirst(), pair.getSecond());
                }
                return nonNullList;
            });
        });
    }

    public static Function<Tag, DataResult<CompoundTag>> mapCompoundTag() {
        return (Tag tag) -> {
            return tag instanceof CompoundTag compoundTag ? DataResult.success(compoundTag) : DataResult.error(
                    () -> "Not a compound tag: " + tag);
        };
    }
}
