package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class LinkedChestRenderer implements BlockEntityRenderer<LinkedChestBlockEntity> {
    private static final Material TEXTURE_LOCATION = new Material(Sheets.CHEST_SHEET,
            LinkedChests.id("entity/chest/linked")
    );
    private static final ResourceLocation WOOL_TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/block/white_wool.png");
    private static final ResourceLocation DIAMOND_TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/block/diamond_block.png");

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

        this.model.setLidPitch(blockEntity.getOpenNess(partialTick));
        VertexConsumer vertexConsumer = TEXTURE_LOCATION.buffer(multiBufferSource, RenderType::entityCutout);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);

        DyeChannel dyeChannel = blockEntity.getDyeChannel();
        this.model.leftButton.render(poseStack,
                multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight, packedOverlay,
                dyeChannel.leftColor().getTextureDiffuseColor()
        );
        this.model.middleButton.render(poseStack,
                multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight, packedOverlay,
                dyeChannel.middleColor().getTextureDiffuseColor()
        );
        this.model.rightButton.render(poseStack,
                multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight, packedOverlay,
                dyeChannel.rightColor().getTextureDiffuseColor()
        );

        if (dyeChannel.uuid().isPresent()) {
            this.model.latch.render(poseStack,
                    multiBufferSource.getBuffer(RenderType.entityCutout(DIAMOND_TEXTURE_LOCATION)), packedLight,
                    packedOverlay
            );
        } else {
            this.model.latch.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }
}
