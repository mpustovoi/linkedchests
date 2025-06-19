package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;

public class LinkedChestBlockEntityRenderer extends SingleChestRenderer<LinkedChestBlockEntity, LinkedChestModel> {
    private final LinkedChestRendererImpl renderer;

    public LinkedChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LinkedChestModel(context.bakeLayer(LinkedChestRendererImpl.LINKED_CHEST_MODEL_LAYER_LOCATION)));
        this.renderer = new LinkedChestRendererImpl(this.model);
    }

    @Override
    protected void renderModel(LinkedChestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Material material = this.getChestMaterial(blockEntity, this.xmasTextures);
        this.renderer.extractRenderState(material, blockEntity.getDyeChannel());
        this.renderer.renderModel(poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    protected Material getChestMaterial(LinkedChestBlockEntity blockEntity, boolean holiday) {
        return holiday ? Sheets.CHEST_XMAS_LOCATION : LinkedChestRendererImpl.LINKED_CHEST_LOCATION;
    }
}
