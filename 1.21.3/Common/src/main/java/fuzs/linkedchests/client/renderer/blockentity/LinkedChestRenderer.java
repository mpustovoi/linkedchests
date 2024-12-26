package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;

import java.util.List;

public class LinkedChestRenderer extends SingleChestRenderer<LinkedChestBlockEntity, LinkedChestModel> {
    static final ModelLayerFactory FACTORY = ModelLayerFactory.from(LinkedChests.MOD_ID);
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = FACTORY.register("linked_chest");
    private static final Material LINKED_CHEST_LOCATION = new Material(Sheets.CHEST_SHEET,
            LinkedChests.id("entity/chest/linked"));
    private static final Material LINKED_CHEST_BUTTONS_LOCATION = new Material(Sheets.CHEST_SHEET,
            LinkedChests.id("entity/chest/buttons"));

    public LinkedChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LinkedChestModel(context.bakeLayer(MODEL_LAYER_LOCATION)));
    }

    @Override
    protected void renderModel(LinkedChestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderModelParts(this.model.getBaseModelParts(),
                this.getChestMaterial(blockEntity, this.xmasTextures),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay);
        boolean isPersonalChannel = blockEntity.getDyeChannel().uuid().isPresent();
        this.renderModelParts(this.model.getLockModelParts(isPersonalChannel),
                isPersonalChannel ? LINKED_CHEST_BUTTONS_LOCATION :
                        this.getChestMaterial(blockEntity, this.xmasTextures),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay);
        int[] dyeColors = blockEntity.getDyeChannel().dyeColors();
        for (int i = 0; i < dyeColors.length; i++) {
            this.renderModelParts(this.model.getButtonModelParts(i),
                    LINKED_CHEST_BUTTONS_LOCATION,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    dyeColors[i]);
        }
    }

    private void renderModelParts(List<ModelPart> modelParts, Material material, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderModelParts(modelParts, material, poseStack, bufferSource, packedLight, packedOverlay, -1);
    }

    private void renderModelParts(List<ModelPart> modelParts, Material material, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int color) {
        VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);
        this.onlyDrawSelectedParts(modelParts);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.resetDrawForAllParts();
    }

    private void onlyDrawSelectedParts(List<ModelPart> modelParts) {
        this.model.allParts().forEach(modelPart -> modelPart.skipDraw = true);
        modelParts.forEach(modelPart -> modelPart.skipDraw = false);
    }

    private void resetDrawForAllParts() {
        this.model.allParts().forEach(part -> part.skipDraw = false);
    }

    @Override
    protected Material getChestMaterial(LinkedChestBlockEntity blockEntity, boolean holiday) {
        return holiday ? Sheets.CHEST_XMAS_LOCATION : LINKED_CHEST_LOCATION;
    }
}
