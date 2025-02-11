package fuzs.linkedchests.client.model;

import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.Collections;
import java.util.List;

public class LinkedChestModel extends ChestModel {
    private final ModelPart bottom;
    private final ModelPart lid;
    private final ModelPart lock, personalLock;
    private final ModelPart[] buttons = new ModelPart[3];

    public LinkedChestModel(ModelPart root) {
        super(root);
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
        this.personalLock = root.getChild("personal_lock");
        this.buttons[0] = root.getChild("left_button");
        this.buttons[1] = root.getChild("middle_button");
        this.buttons[2] = root.getChild("right_button");
    }

    public static LayerDefinition createSingleBodyLayer() {
        return ChestModel.createSingleBodyLayer().apply(LinkedChestModel::applyLinkedChestTransformation);
    }

    private static MeshDefinition applyLinkedChestTransformation(MeshDefinition meshDefinition) {
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("personal_lock",
                CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_button",
                CubeListBuilder.create().texOffs(0, 5).addBox(4.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        partDefinition.addOrReplaceChild("middle_button",
                CubeListBuilder.create().texOffs(0, 10).addBox(7.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        partDefinition.addOrReplaceChild("right_button",
                CubeListBuilder.create().texOffs(0, 15).addBox(10.0F, 5.0F, 5.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        return meshDefinition;
    }

    @Override
    public void setupAnim(float openness) {
        super.setupAnim(openness);
        this.personalLock.xRot = this.lid.xRot;
        for (ModelPart modelPart : this.buttons) {
            modelPart.xRot = this.lid.xRot;
        }
    }

    public List<ModelPart> getBaseModelParts() {
        return List.of(this.bottom, this.lid);
    }

    public List<ModelPart> getLockModelParts(boolean isPersonalChannel) {
        return Collections.singletonList(isPersonalChannel ? this.personalLock : this.lock);
    }

    public List<ModelPart> getButtonModelParts(int index) {
        return Collections.singletonList(this.buttons[index]);
    }
}
