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
import java.util.stream.Stream;

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
                TextureMapping.particle(Blocks.NETHER_BRICKS), builder.output
        );
        createLinkedStorageItem(builder, ModRegistry.LINKED_STORAGE_ITEM.value(), LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, false);
        createLinkedStorageItem(builder, ModRegistry.LINKED_STORAGE_ITEM.value(), LinkedChestsClient.ITEM_MODEL_PROPERTY_OPEN, true);
    }

    private static void createLinkedStorageItem(ItemModelGenerators builder, Item item, ResourceLocation itemModelProperty, boolean isOverride) {
        String suffix = "_" + itemModelProperty.getPath();
        ResourceLocation modelLocation = getModelLocation(item).withSuffix(isOverride ? suffix : "");
        Stream<String> stream = Stream.of("_button1", "_button2", "_button3", "_latch");
        if (isOverride) stream = stream.map(s -> s + suffix);
        TextureMapping textureMapping = layered(modelLocation, stream.toArray(String[]::new));
        if (isOverride) {
            ModelTemplates.FLAT_ITEM.create(modelLocation, textureMapping, builder.output);
        } else {
            ItemModelProperties itemModelProperties = ItemModelProperties.singleOverride(modelLocation.withSuffix(suffix),
                    itemModelProperty, 1.0F
            );
            ModelTemplate.JsonFactory jsonFactory = ItemModelProperties.overridesFactory(ModelTemplates.FLAT_ITEM,
                    itemModelProperties
            );
            ModelTemplates.FLAT_ITEM.create(modelLocation, textureMapping, builder.output, jsonFactory);
        }
    }

    public static TextureMapping layered(ResourceLocation resourceLocation, String... layerSuffixes) {
        TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, resourceLocation);
        for (int i = 0; i < layerSuffixes.length; i++) {
            textureMapping.put(TextureSlot.create("layer" + ++i), resourceLocation.withSuffix(layerSuffixes[i]));
        }
        return textureMapping;
    }
}
