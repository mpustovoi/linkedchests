package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;

public class LinkedChestRenderer implements BlockEntityRenderer<LinkedChestBlockEntity> {
    private static final Material TEXTURE_LOCATION = new Material(Sheets.CHEST_SHEET,
            LinkedChests.id("entity/chest/linked")
    );

    private final LinkedChestModel model;

    public LinkedChestRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new LinkedChestModel(context);
    }

    @Override
    public void render(LinkedChestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        float facingRotation = blockEntity.getBlockState().getValue(LinkedChestBlock.FACING).toYRot();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facingRotation));
        poseStack.translate(-0.5, -0.5, -0.5);

        DyeChannel dyeChannel = blockEntity.getDyeChannel();
        this.model.setLidPitch(blockEntity.getOpenNess(partialTick));
        this.model.setPersonalChannel(dyeChannel.uuid().isPresent());
        this.model.setButtonColors(dyeChannel.leftColor(), dyeChannel.middleColor(), dyeChannel.rightColor());

        VertexConsumer vertexConsumer = TEXTURE_LOCATION.buffer(multiBufferSource, this.model::renderType);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
