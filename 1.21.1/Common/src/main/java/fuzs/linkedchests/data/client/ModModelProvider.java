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

import java.util.Optional;

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
        createLinkedStorageItem(builder, ModRegistry.LINKED_STORAGE_ITEM.value(),
                LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, false
        );
        createLinkedStorageItem(builder, ModRegistry.LINKED_STORAGE_ITEM.value(),
                LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, true
        );
    }

    private static void createLinkedStorageItem(ItemModelGenerators builder, Item item, ResourceLocation itemModelProperty, boolean isOverride) {
        String suffix = "_" + itemModelProperty.getPath();
        ResourceLocation modelLocation = getModelLocation(item).withSuffix(isOverride ? suffix : "");
        TextureSlot[] textureSlots = createTextureSlotLayers(5);
        TextureMapping textureMapping = layered(modelLocation, textureSlots, "_button1", "_button2", "_button3",
                "_latch"
        );
        ModelTemplate modelTemplate = ModelTemplates.createItem("generated", textureSlots);
        if (isOverride) {
            modelTemplate.create(modelLocation, textureMapping, builder.output);
        } else {
            ItemModelProperties itemModelProperties = ItemModelProperties.singleOverride(
                    modelLocation.withSuffix(suffix), itemModelProperty, 1.0F);
            ModelTemplate.JsonFactory jsonFactory = ItemModelProperties.overridesFactory(ModelTemplates.FLAT_ITEM,
                    itemModelProperties
            );
            modelTemplate.create(modelLocation, textureMapping, builder.output, jsonFactory);
        }
    }

    public static TextureSlot[] createTextureSlotLayers(int size) {
        TextureSlot[] textureSlots = new TextureSlot[size];
        for (int i = 0; i < textureSlots.length; i++) {
            textureSlots[i] = TextureSlot.create("layer" + i);
        }
        return textureSlots;
    }

    public static TextureMapping layered(ResourceLocation resourceLocation, TextureSlot[] textureSlots, String... layerSuffixes) {
        TextureMapping textureMapping = new TextureMapping();
        for (int i = 0; i < textureSlots.length; i++) {
            ResourceLocation textureLocation = i == 0 ? resourceLocation : resourceLocation.withSuffix(
                    layerSuffixes[i - 1]);
            textureMapping.put(textureSlots[i], textureLocation);
        }
        return textureMapping;
    }
}
