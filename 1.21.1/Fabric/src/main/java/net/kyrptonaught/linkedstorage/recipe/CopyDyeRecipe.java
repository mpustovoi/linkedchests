package net.kyrptonaught.linkedstorage.recipe;

import com.mojang.serialization.MapCodec;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.Optional;
import java.util.function.Function;

public class CopyDyeRecipe extends ShapedRecipe {

    public CopyDyeRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getGroup(), shapedRecipe.category(), getShapedRecipePattern(shapedRecipe),
                shapedRecipe.getResultItem(RegistryAccess.EMPTY), shapedRecipe.showNotification()
        );
    }

    static ShapedRecipePattern getShapedRecipePattern(ShapedRecipe shapedRecipe) {
        return new ShapedRecipePattern(shapedRecipe.getWidth(), shapedRecipe.getHeight(), shapedRecipe.getIngredients(),
                Optional.empty()
        );
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider dynamicRegistryManager) {
        ItemStack output = this.getResultItem(dynamicRegistryManager).copy();
        LinkedInventoryHelper.setItemChannel(LinkedInventoryHelper.getItemChannel(inv.getItem(4)), output);
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.COPY_DYE_RECIPE_SERIALIZER.value();
    }

    public static class Serializer implements RecipeSerializer<CopyDyeRecipe> {

        @Override
        public MapCodec<CopyDyeRecipe> codec() {
            return ShapedRecipe.Serializer.SHAPED_RECIPE.codec().xmap(CopyDyeRecipe::new, Function.identity());
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CopyDyeRecipe> streamCodec() {
            return ShapedRecipe.Serializer.SHAPED_RECIPE.streamCodec().map(CopyDyeRecipe::new, Function.identity());
        }
    }
}
