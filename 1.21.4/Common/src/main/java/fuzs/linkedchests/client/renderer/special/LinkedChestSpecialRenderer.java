package fuzs.linkedchests.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.client.renderer.blockentity.LinkedChestRendererImpl;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LinkedChestSpecialRenderer implements SpecialModelRenderer<DyeChannel> {
    private final LinkedChestRendererImpl renderer;
    private final Material material;
    private final float openness;

    public LinkedChestSpecialRenderer(LinkedChestModel model, Material material, float openness) {
        this.renderer = new LinkedChestRendererImpl(model);
        this.material = material;
        this.openness = openness;
    }

    @Override
    public void render(DyeChannel dyeChannel, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        this.renderer.extractRenderState(this.material, dyeChannel);
        this.renderer.model.setupAnim(this.openness);
        this.renderer.renderModel(poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public DyeChannel extractArgument(ItemStack itemStack) {
        return itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), DyeChannel.DEFAULT);
    }

    public record Unbaked(ResourceLocation texture, float openness) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = ChestSpecialRenderer.Unbaked.MAP_CODEC.xmap((ChestSpecialRenderer.Unbaked unbaked) -> new Unbaked(
                        unbaked.texture(),
                        unbaked.openness()),
                (Unbaked unbaked) -> new ChestSpecialRenderer.Unbaked(unbaked.texture(), unbaked.openness()));

        public Unbaked(ResourceLocation texture) {
            this(texture, 0.0F);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
            LinkedChestModel model = new LinkedChestModel(entityModelSet.bakeLayer(LinkedChestRendererImpl.LINKED_CHEST_MODEL_LAYER_LOCATION));
            Material material = Sheets.chestMaterial(this.texture);
            return new LinkedChestSpecialRenderer(model, material, this.openness);
        }
    }
}
