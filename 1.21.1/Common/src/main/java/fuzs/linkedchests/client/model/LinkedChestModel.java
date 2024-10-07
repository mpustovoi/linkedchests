package fuzs.linkedchests.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.linkedchests.client.LinkedChestsClient;
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
import net.minecraft.world.item.DyeColor;

public class LinkedChestModel extends Model {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = LinkedChestsClient.id("linked_chest");

    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart lock, personalLock;
    private final ModelPart[] buttons = new ModelPart[3];
    private final int[] buttonColors = new int[3];

    public LinkedChestModel(BlockEntityRendererProvider.Context context) {
        super(RenderType::entityCutout);
        ModelPart modelPart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.base = modelPart.getChild("bottom");
        this.lid = modelPart.getChild("lid");
        this.lock = modelPart.getChild("lock");
        this.personalLock = modelPart.getChild("personal_lock");
        this.buttons[0] = modelPart.getChild("left_button");
        this.buttons[1] = modelPart.getChild("middle_button");
        this.buttons[2] = modelPart.getChild("right_button");
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
        partDefinition.addOrReplaceChild("personal_lock",
                CubeListBuilder.create().texOffs(6, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild("left_button",
                CubeListBuilder.create().texOffs(0, 5).addBox(4.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partDefinition.addOrReplaceChild("middle_button",
                CubeListBuilder.create().texOffs(0, 5).addBox(7.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partDefinition.addOrReplaceChild("right_button",
                CubeListBuilder.create().texOffs(0, 5).addBox(10.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void setLidPitch(float pitch) {
        pitch = 1.0F - pitch;
        this.lid.xRot = -((1.0F - pitch * pitch * pitch) * Mth.HALF_PI);
        this.lock.xRot = this.personalLock.xRot = this.lid.xRot;
        for (ModelPart modelPart : this.buttons) {
            modelPart.xRot = this.lid.xRot;
        }
    }

    public void setPersonalChannel(boolean isPersonalChannel) {
        this.lock.visible = !isPersonalChannel;
        this.personalLock.visible = isPersonalChannel;
    }

    public void setButtonColors(DyeColor leftColor, DyeColor middleColor, DyeColor rightColor) {
        this.buttonColors[0] = leftColor.getTextureDiffuseColor();
        this.buttonColors[1] = middleColor.getTextureDiffuseColor();
        this.buttonColors[2] = rightColor.getTextureDiffuseColor();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.base.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.lid.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.lock.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.personalLock.render(poseStack, buffer, packedLight, packedOverlay, color);
        for (int i = 0; i < this.buttons.length; i++) {
            this.buttons[i].render(poseStack, buffer, packedLight, packedOverlay, this.buttonColors[i]);
        }
    }
}