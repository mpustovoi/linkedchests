package fuzs.linkedchests.world.item.crafting;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

public class DyeChannelRecipe extends CustomRecipe {

    public DyeChannelRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        PositionedItem positionedItem = this.getDyeChannelItem(craftingInput);
        if (positionedItem != null) {
            DyeChannel dyeChannel = positionedItem.itemStack().getOrDefault(
                    ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT);
            MutableBoolean mutableBoolean = new MutableBoolean();
            List<PositionedItem> items = this.iterateDyeItems(craftingInput, positionedItem.posX(),
                    positionedItem.posY(), (DyeColor dyeColor, int value) -> {
                // do not allow setting the same color again to the same slot
                        if (dyeChannel.withColorAt(value, dyeColor) != dyeChannel) {
                            mutableBoolean.setTrue();
                        }
                    }
            );
            if (mutableBoolean.isTrue()) {
                // check that all other slots are empty
                items.add(positionedItem);
                IntSet set = items.stream().map(item -> item.index(craftingInput.width())).collect(
                        Collectors.toCollection(IntArraySet::new));
                for (int i = 0; i < craftingInput.size(); i++) {
                    if (!set.contains(i) && !craftingInput.getItem(i).isEmpty()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    private PositionedItem getDyeChannelItem(CraftingInput craftingInput) {
        for (int width = 0; width < craftingInput.width(); width++) {
            for (int height = 0; height < craftingInput.height(); height++) {
                ItemStack itemStack = craftingInput.getItem(width, height);
                if (itemStack.has(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value())) {
                    return new PositionedItem(itemStack, width, height);
                }
            }
        }

        return null;
    }

    private List<PositionedItem> iterateDyeItems(CraftingInput craftingInput, int posX, int posY, ObjIntConsumer<DyeColor> consumer) {
        List<PositionedItem> dyeItems = new ArrayList<>();
        posX--;
        posY--;
        for (int i = 0; i < 3; i++, posX++) {
            if (posX >= 0 && posX < craftingInput.width() && posY >= 0 && posY < craftingInput.height()) {
                ItemStack itemStack = craftingInput.getItem(posX, posY);
                if (itemStack.is(ModRegistry.DYE_CHANNEL_COLOR_PROVIDERS_ITEM_TAG)) {
                    consumer.accept(DyeChannel.getDyeColor(itemStack.getItem()), i);
                    dyeItems.add(new PositionedItem(itemStack, posX, posY));
                }
            }
        }
        return dyeItems;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        PositionedItem positionedItem = this.getDyeChannelItem(craftingInput);
        if (positionedItem != null) {
            ItemStack itemStack = positionedItem.itemStack().copy();
            this.iterateDyeItems(craftingInput, positionedItem.posX(), positionedItem.posY(),
                    (DyeColor dyeColor, int value) -> {
                        DyeChannel dyeChannel = itemStack.getOrDefault(
                                ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT);
                        itemStack.set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                                dyeChannel.withColorAt(value, dyeColor)
                        );
                    }
            );
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.DYE_CHANNEL_RECIPE_SERIALIZER.value();
    }

    record PositionedItem(ItemStack itemStack, int posX, int posY) {

        public int index(int width) {
            return this.posX + this.posY * width;
        }
    }
}
