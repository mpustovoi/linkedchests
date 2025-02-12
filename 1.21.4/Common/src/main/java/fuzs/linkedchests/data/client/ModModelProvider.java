package fuzs.linkedchests.data.client;

import fuzs.linkedchests.client.color.item.DyeChannelTintSource;
import fuzs.linkedchests.client.renderer.blockentity.LinkedChestRendererImpl;
import fuzs.linkedchests.client.renderer.item.properties.conditional.LinkedPouchOpenModelProperty;
import fuzs.linkedchests.client.renderer.item.properties.conditional.LinkedPouchPersonalModelProperty;
import fuzs.linkedchests.client.renderer.special.LinkedChestSpecialRenderer;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.client.data.v2.models.ModelLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

public class ModModelProvider extends AbstractModelProvider {
    public static final TextureSlot LAYER3_TEXTURE_SLOT = TextureSlot.create("layer3");
    public static final ModelTemplate FOUR_LAYERED_ITEM = ModelTemplates.createItem("generated",
            TextureSlot.LAYER0,
            TextureSlot.LAYER1,
            TextureSlot.LAYER2,
            LAYER3_TEXTURE_SLOT);

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        this.createChest(ModRegistry.LINKED_CHEST_BLOCK.value(),
                Blocks.END_STONE,
                LinkedChestRendererImpl.LINKED_CHEST_TEXTURE,
                true,
                LinkedChestSpecialRenderer.Unbaked::new,
                blockModelGenerators);
    }

    public final void createChest(Block chestBlock, Block particleBlock, ResourceLocation texture, boolean useGiftTexture, Function<ResourceLocation, SpecialModelRenderer.Unbaked> unbakedRendererFactory, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(chestBlock, particleBlock);
        Item item = chestBlock.asItem();
        ResourceLocation resourceLocation = ModelTemplates.CHEST_INVENTORY.create(item,
                TextureMapping.particle(particleBlock),
                blockModelGenerators.modelOutput);
        ItemModel.Unbaked unbaked = ItemModelUtils.specialModel(resourceLocation,
                unbakedRendererFactory.apply(texture));
        if (useGiftTexture) {
            ItemModel.Unbaked unbaked2 = ItemModelUtils.specialModel(resourceLocation,
                    unbakedRendererFactory.apply(ChestSpecialRenderer.GIFT_CHEST_TEXTURE));
            blockModelGenerators.itemModelOutput.accept(item, ItemModelUtils.isXmas(unbaked2, unbaked));
        } else {
            blockModelGenerators.itemModelOutput.accept(item, unbaked);
        }
    }

    @Override
    public void addItemModels(ItemModelGenerators itemModelGenerators) {
        this.generateLinkedPouch(ModRegistry.LINKED_POUCH_ITEM.value(), itemModelGenerators);
    }

    public final void generateLinkedPouch(Item item, ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked itemModel = this.createLinkedPouch(ModelLocationHelper.getItemModel(item),
                ModelLocationHelper.getItemTexture(item),
                ModelLocationHelper.getItemTexture(item),
                itemModelGenerators);
        ItemModel.Unbaked openModel = this.createLinkedPouch(ModelLocationHelper.getItemModel(item, "_open"),
                ModelLocationHelper.getItemTexture(item, "_open"),
                ModelLocationHelper.getItemTexture(item, "_open"),
                itemModelGenerators);
        ItemModel.Unbaked personalModel = this.createLinkedPouch(ModelLocationHelper.getItemModel(item, "_personal"),
                ModelLocationHelper.getItemTexture(item, "_personal"),
                ModelLocationHelper.getItemTexture(item),
                itemModelGenerators);
        ItemModel.Unbaked openPersonalModel = this.createLinkedPouch(ModelLocationHelper.getItemModel(item,
                        "_open_personal"),
                ModelLocationHelper.getItemTexture(item, "_open_personal"),
                ModelLocationHelper.getItemTexture(item, "_open"),
                itemModelGenerators);
        ItemModel.Unbaked falseModel = ItemModelUtils.conditional(new LinkedPouchOpenModelProperty(),
                openModel,
                itemModel);
        ItemModel.Unbaked trueModel = ItemModelUtils.conditional(new LinkedPouchOpenModelProperty(),
                openPersonalModel,
                personalModel);
        itemModelGenerators.generateBooleanDispatch(item,
                new LinkedPouchPersonalModelProperty(),
                trueModel,
                falseModel);
    }

    public final ItemModel.Unbaked createLinkedPouch(ResourceLocation itemModel, ResourceLocation baseLocation, ResourceLocation dyeSlotLocation, ItemModelGenerators itemModelGenerators) {
        TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, baseLocation)
                .put(TextureSlot.LAYER1, dyeSlotLocation.withSuffix("_left_dye_slot"))
                .put(TextureSlot.LAYER2, dyeSlotLocation.withSuffix("_middle_dye_slot"))
                .put(LAYER3_TEXTURE_SLOT, dyeSlotLocation.withSuffix("_right_dye_slot"));
        return ItemModelUtils.tintedModel(FOUR_LAYERED_ITEM.create(itemModel,
                        textureMapping,
                        itemModelGenerators.modelOutput),
                ItemModelUtils.constantTint(-1),
                new DyeChannelTintSource(DyeChannelTintSource.DyeSlot.LEFT),
                new DyeChannelTintSource(DyeChannelTintSource.DyeSlot.MIDDLE),
                new DyeChannelTintSource(DyeChannelTintSource.DyeSlot.RIGHT));
    }
}
