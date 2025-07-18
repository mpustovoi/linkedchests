package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LinkedChestRendererImpl {
    static final ModelLayerFactory MODEL_LAYERS = ModelLayerFactory.from(LinkedChests.MOD_ID);
    public static final ModelLayerLocation LINKED_CHEST_MODEL_LAYER_LOCATION = MODEL_LAYERS.registerModelLayer(
            "linked_chest");
    public static final ResourceLocation LINKED_CHEST_TEXTURE = LinkedChests.id("linked");
    public static final Material LINKED_CHEST_LOCATION = Sheets.CHEST_MAPPER.apply(LINKED_CHEST_TEXTURE);
    private static final Material LINKED_CHEST_BUTTONS_LOCATION = Sheets.CHEST_MAPPER.apply(LinkedChests.id(
            "linked_buttons"));

    public final LinkedChestModel model;
    private final RenderState renderState = new RenderState();

    public LinkedChestRendererImpl(LinkedChestModel model) {
        this.model = model;
    }

    public void extractRenderState(Material chestMaterial, DyeChannel dyeChannel) {
        this.renderState.baseMaterial = chestMaterial;
        this.renderState.lockMaterial = dyeChannel.uuid().isPresent() ? LINKED_CHEST_BUTTONS_LOCATION : chestMaterial;
        this.renderState.slotMaterial = LINKED_CHEST_BUTTONS_LOCATION;
        this.renderState.slotColors = dyeChannel.dyeColors();
    }

    public void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderModelParts(this.model.getBaseModelParts(),
                this.renderState.baseMaterial,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay);
        this.renderModelParts(this.model.getLockModelParts(),
                this.renderState.lockMaterial,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay);
        for (int i = 0; i < this.renderState.slotColors.length; i++) {
            this.renderModelParts(this.model.getButtonModelParts(i),
                    this.renderState.slotMaterial,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    this.renderState.slotColors[i]);
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
        this.model.allParts().forEach((ModelPart modelPart) -> modelPart.skipDraw = false);
    }

    private static class RenderState {
        public Material baseMaterial = LINKED_CHEST_LOCATION;
        public Material lockMaterial = LINKED_CHEST_LOCATION;
        public Material slotMaterial = LINKED_CHEST_BUTTONS_LOCATION;
        public int[] slotColors = new int[3];
    }
}
