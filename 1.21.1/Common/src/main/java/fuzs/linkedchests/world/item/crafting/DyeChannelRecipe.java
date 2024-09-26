package fuzs.linkedchests.world.item.crafting;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.function.ObjIntConsumer;

public class DyeChannelRecipe extends CustomRecipe {

    public DyeChannelRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        PositionedItem positionedItem = this.getDyeChannelItem(craftingInput);
        if (positionedItem != null) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            this.iterateDyeItems(craftingInput, positionedItem.posX(), positionedItem.posY(),
                    (DyeColor dyeColor, int value) -> {
                        mutableBoolean.setTrue();
                    }
            );
            return mutableBoolean.isTrue();
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

    private void iterateDyeItems(CraftingInput craftingInput, int posX, int posY, ObjIntConsumer<DyeColor> consumer) {
        posX--;
        posY--;
        if (posX >= 0 && posX + 3 < craftingInput.width() && posY >= 0 && posY < craftingInput.height()) {
            for (int i = 0; i < 3; i++, posX++) {
                ItemStack itemStack = craftingInput.getItem(posX, posY);
                if (itemStack.getItem() instanceof DyeItem dyeItem) {
                    consumer.accept(dyeItem.getDyeColor(), i);
                }
            }
        }
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
        return width >= 3 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.DYE_CHANNEL_RECIPE_SERIALIZER.value();
    }

    record PositionedItem(ItemStack itemStack, int posX, int posY) {

    }
}
