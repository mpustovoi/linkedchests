package fuzs.linkedchests.init;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.world.inventory.LinkedMenu;
import fuzs.linkedchests.world.item.LinkedStorageItem;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.linkedchests.world.item.crafting.DyeChannelRecipe;
import fuzs.linkedchests.world.item.crafting.ShapedDyeChannelRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(LinkedChests.MOD_ID);
    public static final Holder.Reference<DataComponentType<DyeChannel>> DYE_CHANNEL_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "dye_channel",
            builder -> builder.persistent(DyeChannel.CODEC).networkSynchronized(DyeChannel.STREAM_CODEC)
    );
    public static final Holder.Reference<Block> LINKED_CHEST_BLOCK = REGISTRIES.registerBlock("linked_chest",
            () -> new LinkedChestBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE).mapColor(MapColor.COLOR_GREEN))
    );
    public static final Holder.Reference<BlockEntityType<LinkedChestBlockEntity>> LINKED_CHEST_BLOCK_ENTITY = REGISTRIES.registerBlockEntityType(
            "linked_chest", () -> BlockEntityType.Builder.of(LinkedChestBlockEntity::new, LINKED_CHEST_BLOCK.value()));
    public static final Holder.Reference<Item> LINKED_CHEST_ITEM = REGISTRIES.registerItem(
            LINKED_CHEST_BLOCK.unwrapKey().orElseThrow().location().getPath(),
            () -> new BlockItem(LINKED_CHEST_BLOCK.value(),
                    new Item.Properties().component(DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT_CHANNEL)
            )
    );
    public static final Holder.Reference<Item> LINKED_STORAGE_ITEM = REGISTRIES.registerItem("linked_storage",
            () -> new LinkedStorageItem(new Item.Properties().stacksTo(1)
                    .component(DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT_CHANNEL))
    );
    public static final Holder.Reference<MenuType<LinkedMenu>> LINKED_CHEST_MENU_TYPE = REGISTRIES.registerMenuType(
            "linked_chest", () -> {
                return LinkedMenu.createSupplier(false, false);
            });
    public static final Holder.Reference<MenuType<LinkedMenu>> LINKED_STORAGE_MENU_TYPE = REGISTRIES.registerMenuType(
            "linked_storage", () -> {
                return LinkedMenu.createSupplier(false, true);
            });
    public static final Holder.Reference<MenuType<LinkedMenu>> PERSONAL_LINKED_CHEST_MENU_TYPE = REGISTRIES.registerMenuType(
            "personal_linked_chest", () -> {
                return LinkedMenu.createSupplier(true, false);
            });
    public static final Holder.Reference<MenuType<LinkedMenu>> PERSONAL_LINKED_STORAGE_MENU_TYPE = REGISTRIES.registerMenuType(
            "personal_linked_storage", () -> {
                return LinkedMenu.createSupplier(true, true);
            });
    public static final Holder.Reference<RecipeSerializer<DyeChannelRecipe>> DYE_CHANNEL_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER, "crafting_special_dye_channel",
            () -> new SimpleCraftingRecipeSerializer<>(DyeChannelRecipe::new)
    );
    public static final Holder.Reference<RecipeSerializer<ShapedDyeChannelRecipe>> SHAPED_DYE_CHANNEL_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER, "crafting_shaped_dye_channel", () -> new ShapedDyeChannelRecipe.Serializer());

    public static void touch() {
        // NO-OP
    }
}
