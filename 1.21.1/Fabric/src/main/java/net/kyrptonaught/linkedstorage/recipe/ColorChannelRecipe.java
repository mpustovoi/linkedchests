package net.kyrptonaught.linkedstorage.recipe;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ColorChannelRecipe extends CustomRecipe {
    private static final ShapedRecipePattern RECIPE_PATTERN;

    static {
        TagKey<Item> dyesItemTag = TagKey.create(Registries.ITEM, ResourceLocationHelper.parse("c:dyes"));
        Ingredient dyeIngredient = CombinedIngredients.INSTANCE.any(Ingredient.EMPTY, Ingredient.of(dyesItemTag));
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, dyeIngredient, dyeIngredient,
                dyeIngredient, Ingredient.EMPTY, Ingredient.of(ModRegistry.COLOR_CHANNEL_PROVIDER_ITEM_TAG),
                Ingredient.EMPTY
        );
        RECIPE_PATTERN = new ShapedRecipePattern(3, 2, ingredients, Optional.empty());
    }

    public ColorChannelRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return craftingInput.ingredientCount() >= 2 && RECIPE_PATTERN.matches(craftingInput);
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack itemStack = craftingInput.getItem(4).copy();
        DyeChannel dyeChannel = LinkedInventoryHelper.getItemChannel(itemStack).clone();
        for (int i = 0; i < 3; i++)
            if (craftingInput.getItem(i).getItem() instanceof DyeItem) {
                dyeChannel.setSlot(i, (byte) ((DyeItem) craftingInput.getItem(i).getItem()).getDyeColor().getId());
            }
        LinkedInventoryHelper.setItemChannel(dyeChannel, itemStack);
        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= RECIPE_PATTERN.width() && height >= RECIPE_PATTERN.height();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.COLOR_CHANNEL_RECIPE_SERIALIZER.value();
    }
}
