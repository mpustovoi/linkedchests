package fuzs.linkedchests.data;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.item.crafting.DyeChannelRecipe;
import fuzs.linkedchests.world.item.crafting.ShapedDyeChannelRecipe;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(this.items(), RecipeCategory.DECORATIONS, ModRegistry.LINKED_CHEST_ITEM.value())
                .define('@', Items.ENDER_EYE)
                .define('#', Items.END_STONE)
                .define('C', Items.CHEST)
                .define('W', ItemTags.WOOL)
                .pattern("@W@")
                .pattern("#C#")
                .pattern("@#@")
                .unlockedBy(getHasName(Items.ENDER_EYE), this.has(Items.ENDER_EYE))
                .save(new ForwardingRecipeOutput(recipeOutput,
                        recipe -> new ShapedDyeChannelRecipe((ShapedRecipe) recipe)));
        ShapedRecipeBuilder.shaped(this.items(), RecipeCategory.DECORATIONS, ModRegistry.LINKED_POUCH_ITEM.value())
                .define('@', Items.ENDER_EYE)
                .define('#', Items.LEATHER)
                .define('C', Items.CHEST)
                .define('W', ItemTags.WOOL)
                .pattern("@#@")
                .pattern("#C#")
                .pattern("@W@")
                .unlockedBy(getHasName(Items.ENDER_EYE), this.has(Items.ENDER_EYE))
                .save(new ForwardingRecipeOutput(recipeOutput,
                        recipe -> new ShapedDyeChannelRecipe((ShapedRecipe) recipe)));
        SpecialRecipeBuilder.special(DyeChannelRecipe::new).save(recipeOutput, "dye_channel");
    }

    record ForwardingRecipeOutput(RecipeOutput recipeOutput,
                                  UnaryOperator<Recipe<?>> recipeConverter) implements RecipeOutput {

        @Override
        public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
            this.recipeOutput.accept(key, this.recipeConverter.apply(recipe), advancement);
        }

        @Override
        public Advancement.Builder advancement() {
            return this.recipeOutput.advancement();
        }

        @Override
        public void includeRootAdvancement() {
            // NO-OP
        }
    }
}
