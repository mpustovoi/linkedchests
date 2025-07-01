package fuzs.linkedchests.world.item.crafting;

import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.loot.packs.LootData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShapedDyeChannelRecipe extends ShapedRecipe {
    private static final Map<Item, DyeColor> DYE_BY_ITEM = LootData.WOOL_ITEM_BY_DYE.entrySet()
            .stream()
            .collect(Collectors.toMap((Map.Entry<DyeColor, ItemLike> entry) -> entry.getValue().asItem(),
                    Map.Entry::getKey,
                    (o1, o2) -> o1,
                    IdentityHashMap::new));

    public ShapedDyeChannelRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.group(),
                shapedRecipe.category(),
                shapedRecipe.pattern,
                shapedRecipe.assemble(CraftingInput.EMPTY, RegistryAccess.EMPTY),
                shapedRecipe.showNotification());
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack itemStack = super.assemble(craftingInput, registries);
        // if there is a wool block somewhere in here copy the color from that for the dye channel data
        for (ItemStack input : craftingInput.items()) {
            DyeColor dyeColor = DYE_BY_ITEM.get(input.getItem());
            if (dyeColor != null) {
                itemStack.set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), new DyeChannel(dyeColor));
                break;
            }
        }
        return itemStack;
    }

    @Override
    public RecipeSerializer<? extends ShapedDyeChannelRecipe> getSerializer() {
        return ModRegistry.SHAPED_DYE_CHANNEL_RECIPE_SERIALIZER.value();
    }

    public static class Serializer implements RecipeSerializer<ShapedDyeChannelRecipe> {

        @Override
        public MapCodec<ShapedDyeChannelRecipe> codec() {
            return ShapedRecipe.Serializer.SHAPED_RECIPE.codec().xmap(ShapedDyeChannelRecipe::new, Function.identity());
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedDyeChannelRecipe> streamCodec() {
            return ShapedRecipe.Serializer.SHAPED_RECIPE.streamCodec()
                    .map(ShapedDyeChannelRecipe::new, Function.identity());
        }
    }
}
