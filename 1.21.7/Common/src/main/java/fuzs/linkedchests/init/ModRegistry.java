package fuzs.linkedchests.init;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.world.inventory.LinkedMenu;
import fuzs.linkedchests.world.item.LinkedPouchItem;
import fuzs.linkedchests.world.item.crafting.DyeChannelRecipe;
import fuzs.linkedchests.world.item.crafting.ShapedDyeChannelRecipe;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(LinkedChests.MOD_ID);
    public static final Holder.Reference<DataComponentType<DyeChannel>> DYE_CHANNEL_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "dye_channel",
            builder -> builder.persistent(DyeChannel.CODEC).networkSynchronized(DyeChannel.STREAM_CODEC));
    public static final Holder.Reference<Block> LINKED_CHEST_BLOCK = REGISTRIES.registerBlock("linked_chest",
            LinkedChestBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE).mapColor(MapColor.COLOR_GREEN));
    public static final Holder.Reference<BlockEntityType<LinkedChestBlockEntity>> LINKED_CHEST_BLOCK_ENTITY = REGISTRIES.registerBlockEntityType(
            "linked_chest",
            LinkedChestBlockEntity::new,
            LINKED_CHEST_BLOCK);
    public static final Holder.Reference<Item> LINKED_CHEST_ITEM = REGISTRIES.registerBlockItem(LINKED_CHEST_BLOCK,
            () -> new Item.Properties().component(DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT));
    public static final Holder.Reference<Item> LINKED_POUCH_ITEM = REGISTRIES.registerItem("linked_pouch",
            LinkedPouchItem::new,
            () -> new Item.Properties().stacksTo(1)
                    .component(DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT));
    public static final Holder.Reference<MenuType<LinkedMenu>> LINKED_STORAGE_MENU_TYPE = REGISTRIES.registerMenuType(
            "linked_storage",
            LinkedMenu::new,
            LinkedMenu.LinkedData.STREAM_CODEC);
    public static final Holder.Reference<RecipeSerializer<DyeChannelRecipe>> DYE_CHANNEL_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER,
            "crafting_special_dye_channel",
            () -> new CustomRecipe.Serializer<>(DyeChannelRecipe::new));
    public static final Holder.Reference<RecipeSerializer<ShapedDyeChannelRecipe>> SHAPED_DYE_CHANNEL_RECIPE_SERIALIZER = REGISTRIES.register(
            Registries.RECIPE_SERIALIZER,
            "crafting_shaped_dye_channel",
            ShapedDyeChannelRecipe.Serializer::new);

    static final TagFactory TAGS = TagFactory.make(LinkedChests.MOD_ID);
    public static final TagKey<Item> DYE_CHANNEL_COLOR_PROVIDERS_ITEM_TAG = TAGS.registerItemTag(
            "dye_channel_color_providers");
    public static final TagKey<Item> PERSONAL_CHANNEL_PROVIDERS_ITEM_TAG = TAGS.registerItemTag(
            "personal_channel_providers");

    public static void bootstrap() {
        // NO-OP
    }
}
