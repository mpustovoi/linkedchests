package fuzs.linkedchests.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends ChestModel> extends ChestRenderer<T> {
    protected final M model;
    @Nullable
    private T blockEntity;
    @Nullable
    private Float partialTick;
    @Nullable
    private MultiBufferSource bufferSource;

    protected SingleChestRenderer(BlockEntityRendererProvider.Context context, M model) {
        super(context);
        this.model = model;
    }

    @Override
    public final void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.blockEntity = blockEntity;
        this.partialTick = partialTick;
        this.bufferSource = bufferSource;
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        this.blockEntity = null;
        this.partialTick = null;
        this.bufferSource = null;
    }

    @Override
    protected final void render(PoseStack poseStack, VertexConsumer buffer, ChestModel model, float openness, int packedLight, int packedOverlay) {
        Objects.requireNonNull(this.blockEntity, "block entity is null");
        Objects.requireNonNull(this.partialTick, "partial tick is null");
        Objects.requireNonNull(this.bufferSource, "buffer source is null");
        this.model.setupAnim(openness);
        this.renderModel(this.blockEntity, this.partialTick, poseStack, this.bufferSource, packedLight, packedOverlay);
    }

    protected void renderModel(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = this.getChestMaterial(blockEntity, this.xmasTextures)
                .buffer(bufferSource, RenderType::entityCutout);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    protected abstract Material getChestMaterial(T blockEntity, boolean holiday);
}
