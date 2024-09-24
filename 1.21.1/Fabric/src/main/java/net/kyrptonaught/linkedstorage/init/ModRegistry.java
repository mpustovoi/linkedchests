package net.kyrptonaught.linkedstorage.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.inventory.LinkedMenu;
import net.kyrptonaught.linkedstorage.world.item.StorageItem;
import net.kyrptonaught.linkedstorage.recipe.ColorChannelRecipe;
import net.kyrptonaught.linkedstorage.recipe.CopyDyeRecipe;
import net.kyrptonaught.linkedstorage.world.level.block.StorageBlock;
import net.kyrptonaught.linkedstorage.world.level.block.entity.StorageBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(LinkedStorageMod.MOD_ID);
    public static final Holder.Reference<Block> LINKED_CHEST_BLOCK = REGISTRIES.registerBlock("storageblock",
            () -> new StorageBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.EMERALD)
                    .requiresCorrectToolForDrops()
                    .strength(2.5F, 2.5F))
    );
    public static final Holder.Reference<BlockEntityType<StorageBlockEntity>> LINKED_CHEST_BLOCK_ENTITY = REGISTRIES.registerBlockEntityType(
            "storageblock", () -> BlockEntityType.Builder.of(StorageBlockEntity::new, LINKED_CHEST_BLOCK.value()));
    public static final Holder.Reference<Item> LINKED_CHEST_ITEM = REGISTRIES.registerBlockItem(LINKED_CHEST_BLOCK);
    public static final Holder.Reference<Item> LINKED_STORAGE_ITEM = REGISTRIES.registerItem("storageitem",
            () -> new StorageItem(new Item.Properties().stacksTo(1))
    );
    public static final Holder.Reference<MenuType<LinkedMenu>> LINKED_STORAGE_MENU_TYPE = REGISTRIES.registerExtendedMenuType(
            "linkedstorage", () -> {
                return LinkedMenu::new;
            });
    public static final Holder.Reference<RecipeSerializer<ColorChannelRecipe>> COLOR_CHANNEL_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER, "color_channel_recipe",
            () -> new SimpleCraftingRecipeSerializer<>(ColorChannelRecipe::new)
    );
    public static final Holder.Reference<RecipeSerializer<CopyDyeRecipe>> COPY_DYE_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER, "copy_dye_recipe", () -> new CopyDyeRecipe.Serializer());

    static final TagFactory TAGS = TagFactory.make(LinkedStorageMod.MOD_ID);
    public static final TagKey<Item> COLOR_CHANNEL_PROVIDER_ITEM_TAG = TAGS.registerItemTag("color_channel_provider");

    public static void touch() {
        // NO-OP
    }
}
