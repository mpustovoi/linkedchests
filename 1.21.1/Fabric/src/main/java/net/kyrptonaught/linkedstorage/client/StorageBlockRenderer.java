package net.kyrptonaught.linkedstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.kyrptonaught.linkedstorage.world.level.block.StorageBlock;
import net.kyrptonaught.linkedstorage.world.level.block.entity.StorageBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StorageBlockRenderer implements BlockEntityRenderer<StorageBlockEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = LinkedStorageMod.id("block/linkedstorage");
    private static final ResourceLocation WOOL_TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/block/white_wool.png");
    private static final ResourceLocation DIAMOND_TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/block/diamond_block.png");

    private final LinkedChestModel model;

    public StorageBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new LinkedChestModel(context);
    }

    @Override
    public void render(StorageBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        byte[] dyes = blockEntity.getChannel().dyeChannel;
        int color1 = DyeColor.byId(dyes[0]).getTextureDiffuseColor();
        int color2 = DyeColor.byId(dyes[1]).getTextureDiffuseColor();
        int color3 = DyeColor.byId(dyes[2]).getTextureDiffuseColor();

        Level level = blockEntity.getLevel();
        BlockPos blockPos = blockEntity.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        // fixes crash with carpet
        if (blockState.getBlock() instanceof StorageBlock) {
            poseStack.pushPose();
            float facingRotation = blockState.getValue(StorageBlock.FACING).toYRot();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facingRotation));
            poseStack.translate(-0.5D, -0.5D, -0.5D);

            this.model.setLidPitch(blockEntity.getOpenNess(partialTick));
            Material spriteIdentifier = new Material(Sheets.CHEST_SHEET, TEXTURE_LOCATION);
            VertexConsumer vertexConsumer = spriteIdentifier.buffer(multiBufferSource, RenderType::entityCutout);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);

            this.model.button1.render(poseStack,
                    multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight,
                    packedOverlay, color1
            );
            this.model.button2.render(poseStack,
                    multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight,
                    packedOverlay, color2
            );
            this.model.button3.render(poseStack,
                    multiBufferSource.getBuffer(RenderType.entityCutout(WOOL_TEXTURE_LOCATION)), packedLight,
                    packedOverlay, color3
            );

            if (blockEntity.getChannel() instanceof PlayerDyeChannel) {
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
}
