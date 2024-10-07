package fuzs.linkedchests.data.client;

import fuzs.linkedchests.client.LinkedChestsClient;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.client.data.v2.ItemModelProperties;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModModelProvider extends AbstractModelProvider {
    public static final ModelTemplate CHEST_TEMPLATE = new ModelTemplate(
            Optional.of(decorateItemModelLocation(ResourceLocationHelper.withDefaultNamespace("chest"))),
            Optional.empty(), TextureSlot.PARTICLE
    );

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators builder) {
        builder.blockEntityModels(ModelLocationUtils.getModelLocation(ModRegistry.LINKED_CHEST_BLOCK.value()),
                Blocks.END_STONE
        ).createWithoutBlockItem(ModRegistry.LINKED_CHEST_BLOCK.value());
    }

    @Override
    public void addItemModels(ItemModelGenerators builder) {
        CHEST_TEMPLATE.create(ModelLocationUtils.getModelLocation(ModRegistry.LINKED_CHEST_ITEM.value()),
                TextureMapping.particle(Blocks.END_STONE), builder.output
        );
        createLinkedPouchItem(builder, ModRegistry.LINKED_POUCH_ITEM.value());
        createLinkedPouchItem(builder, ModRegistry.LINKED_POUCH_ITEM.value(), LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, null,
                LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL
        );
        createLinkedPouchItem(builder, ModRegistry.LINKED_POUCH_ITEM.value(), null, null,
                LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL
        );
        createLinkedPouchItem(builder, ModRegistry.LINKED_POUCH_ITEM.value(), LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, null,
                LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN
        );
    }

    private static void createLinkedPouchItem(ItemModelGenerators builder, Item item) {
        ResourceLocation modelLocation = getModelLocation(item);
        ItemModelProperties[] itemModelProperties = new ItemModelProperties[3];
        itemModelProperties[2] = ItemModelProperties.twoOverrides(
                getModelLocationWithSuffix(modelLocation, LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN,
                        LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL
                ), LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, 1.0F, LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL,
                1.0F
        );
        itemModelProperties[1] = ItemModelProperties.singleOverride(
                getModelLocationWithSuffix(modelLocation, LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL),
                LinkedChestsClient.ITEM_MODEL_PROPERTY_PERSONAL, 1.0F
        );
        itemModelProperties[0] = ItemModelProperties.singleOverride(
                getModelLocationWithSuffix(modelLocation, LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN),
                LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, 1.0F
        );
        ModelTemplate.JsonFactory jsonFactory = ItemModelProperties.overridesFactory(ModelTemplates.FLAT_ITEM,
                itemModelProperties
        );
        createLinkedPouchItem(builder, item, null, jsonFactory);
    }

    private static void createLinkedPouchItem(ItemModelGenerators builder, Item item, @Nullable ResourceLocation layerLocation, @Nullable ModelTemplate.JsonFactory jsonFactory, ResourceLocation... itemModelProperties) {
        if (layerLocation == null) {
            layerLocation = getModelLocation(item);
        } else {
            layerLocation = getModelLocation(item).withSuffix("_" + layerLocation.getPath());
        }
        ResourceLocation modelLocation = getModelLocationWithSuffix(getModelLocation(item), itemModelProperties);
        TextureSlot[] textureSlots = createTextureSlotLayers(4);
        TextureMapping textureMapping = layered(modelLocation, layerLocation, textureSlots, "_button1", "_button2", "_button3");
        ModelTemplate modelTemplate = ModelTemplates.createItem("generated", textureSlots);
        if (jsonFactory != null) {
            modelTemplate.create(modelLocation, textureMapping, builder.output, jsonFactory);
        } else {
            modelTemplate.create(modelLocation, textureMapping, builder.output);
        }
    }

    public static ResourceLocation getModelLocationWithSuffix(ResourceLocation modelLocation, ResourceLocation... itemModelProperties) {
        String suffix = Arrays.stream(itemModelProperties).map(ResourceLocation::getPath).collect(
                Collectors.joining("_"));
        if (!suffix.isEmpty()) {
            return modelLocation.withSuffix("_" + suffix);
        } else {
            return modelLocation;
        }
    }

    public static TextureSlot[] createTextureSlotLayers(int size) {
        // just dynamically creates layer texture slots for a specified size, Minecraft is supposed to support up to 5
        TextureSlot[] textureSlots = new TextureSlot[size];
        for (int i = 0; i < textureSlots.length; i++) {
            textureSlots[i] = TextureSlot.create("layer" + i);
        }
        return textureSlots;
    }

    public static TextureMapping layered(ResourceLocation initialLayerLocation, ResourceLocation layerLocation, TextureSlot[] textureSlots, String... layerSuffixes) {
        // add the suffixes to the resource location for all layers past zero
        TextureMapping textureMapping = new TextureMapping();
        for (int i = 0; i < textureSlots.length; i++) {
            ResourceLocation textureLocation = i == 0 ? initialLayerLocation : layerLocation.withSuffix(
                    layerSuffixes[i - 1]);
            textureMapping.put(textureSlots[i], textureLocation);
        }
        return textureMapping;
    }
}
