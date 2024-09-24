package net.kyrptonaught.linkedstorage;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.kyrptonaught.linkedstorage.client.LinkedChestModel;
import net.kyrptonaught.linkedstorage.client.StorageBlockRenderer;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LinkedStorageModClient implements ClientModConstructor {

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItemProperty(ResourceLocationHelper.withDefaultNamespace("open"),
                (stack, world, entity, seed) -> {
                    String channel = LinkedInventoryHelper.getItemChannel(stack).getChannelName();
                    return ChannelViewers.getViewersFor(channel) ? 1.0F : 0.0F;
                }, ModRegistry.LINKED_STORAGE_ITEM.value()
        );
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.LINKED_STORAGE_MENU_TYPE.value(), ContainerScreen::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(LinkedChestModel.MODEL_LAYER_LOCATION, LinkedChestModel::createSingleBodyLayer);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value(), StorageBlockRenderer::new);
    }

    @Override
    public void onRegisterItemColorProviders(ColorProvidersContext<Item, ItemColor> context) {
        context.registerColorProvider((ItemStack itemStack, int layer) -> {
            DyeChannel dyeChannel = LinkedInventoryHelper.getItemChannel(itemStack);
            DyeColor dyeColor;
            if (layer > 0 && layer < 4) {
                byte[] colors = dyeChannel.dyeChannel;
                dyeColor = DyeColor.byId(colors[layer - 1]);
            } else if (layer == 4 && dyeChannel instanceof PlayerDyeChannel) {
                dyeColor = DyeColor.LIGHT_BLUE;
            } else {
                dyeColor = DyeColor.WHITE;
            }
            return FastColor.ARGB32.opaque(dyeColor.getMapColor().col);
        }, ModRegistry.LINKED_CHEST_ITEM.value(), ModRegistry.LINKED_STORAGE_ITEM.value());
    }

    public static ModelLayerLocation id(String path) {
        return new ModelLayerLocation(LinkedStorageMod.id(path), "main");
    }
}
