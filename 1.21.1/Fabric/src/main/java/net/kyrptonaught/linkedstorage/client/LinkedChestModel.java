package net.kyrptonaught.linkedstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kyrptonaught.linkedstorage.LinkedStorageModClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

public class LinkedChestModel extends Model {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = LinkedStorageModClient.id("linkedchest");

    private final ModelPart lid;
    private final ModelPart base;
    public final ModelPart latch;
    public final ModelPart button1, button2, button3;

    public LinkedChestModel(BlockEntityRendererProvider.Context context) {
        super(RenderType::entityCutout);
        ModelPart modelPart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.base = modelPart.getChild("bottom");
        this.lid = modelPart.getChild("lid");
        this.latch = modelPart.getChild("lock");
        this.button1 = modelPart.getChild("color1");
        this.button2 = modelPart.getChild("color2");
        this.button3 = modelPart.getChild("color3");
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO
        );
        partDefinition.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partDefinition.addOrReplaceChild("lock",
                CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild("color1", CubeListBuilder.create().texOffs(0, 19).addBox(4, 5, 5, 2, 1, 4),
                PartPose.offset(0, 9f, 1f)
        );
        partDefinition.addOrReplaceChild("color2", CubeListBuilder.create().texOffs(0, 19).addBox(7, 5, 5, 2, 1, 4),
                PartPose.offset(0, 9f, 1f)
        );
        partDefinition.addOrReplaceChild("color3", CubeListBuilder.create().texOffs(0, 19).addBox(10, 5, 5, 2, 1, 4),
                PartPose.offset(0, 9f, 1f)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void setLidPitch(float pitch) {
        pitch = 1.0F - pitch;
        this.button1.xRot = this.button2.xRot = this.button3.xRot = this.latch.xRot = this.lid.xRot = -(
                (1.0F - pitch * pitch * pitch) * Mth.HALF_PI);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.base.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.lid.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}