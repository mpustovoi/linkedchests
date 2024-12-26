package fuzs.linkedchests.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.handler.DyeChannelLidController;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.client.renderer.blockentity.LinkedChestRenderer;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.HighlightShapeProvider;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHighlightCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinkedChestsClient implements ClientModConstructor {
    public static final ResourceLocation ITEM_MODEL_PROPERTY_OPEN = LinkedChests.id("open");
    public static final ResourceLocation ITEM_MODEL_PROPERTY_PERSONAL = LinkedChests.id("personal");

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(DyeChannelLidController::onEndClientTick);
        ClientPlayerNetworkEvents.LOGGED_IN.register(DyeChannelLidController::onLoggedIn);
        RenderHighlightCallback.EVENT.register(
                (LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, HitResult hitResult, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource multiBufferSource, ClientLevel level) -> {
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                        BlockState blockState = level.getBlockState(blockPos);
                        if (!blockState.isAir() && level.getWorldBorder().isWithinBounds(blockPos) &&
                                blockState.getBlock() instanceof HighlightShapeProvider block) {
                            VoxelShape voxelShape = block.getHighlightShape(blockState, level, blockPos,
                                    hitResult.getLocation()
                            );
                            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
                            Vec3 cameraPosition = camera.getPosition();
                            double posX = blockPos.getX() - cameraPosition.x();
                            double posY = blockPos.getY() - cameraPosition.y();
                            double posZ = blockPos.getZ() - cameraPosition.z();
                            LevelRenderer.renderVoxelShape(poseStack, vertexConsumer, voxelShape, posX, posY, posZ,
                                    0.0F, 0.0F, 0.0F, 0.4F, true
                            );
                            return EventResult.INTERRUPT;
                        }
                    }
                    return EventResult.PASS;
                });
    }

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItemProperty(ITEM_MODEL_PROPERTY_OPEN,
                (ItemStack itemStack, ClientLevel clientLevel, LivingEntity entity, int seed) -> {
                    DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                            DyeChannel.DEFAULT
                    );
                    return Mth.ceil(DyeChannelLidController.getChestLidController(dyeChannel).getOpenness(1.0F));
                }, ModRegistry.LINKED_POUCH_ITEM.value()
        );
        context.registerItemProperty(ITEM_MODEL_PROPERTY_PERSONAL,
                (ItemStack itemStack, ClientLevel clientLevel, LivingEntity entity, int seed) -> {
                    DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                            DyeChannel.DEFAULT
                    );
                    return dyeChannel.uuid().isPresent() ? 1.0F : 0.0F;
                }, ModRegistry.LINKED_POUCH_ITEM.value()
        );
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.LINKED_CHEST_MENU_TYPE.value(), ContainerScreen::new);
        context.registerMenuScreen(ModRegistry.LINKED_POUCH_MENU_TYPE.value(), ContainerScreen::new);
        context.registerMenuScreen(ModRegistry.PERSONAL_LINKED_CHEST_MENU_TYPE.value(), ContainerScreen::new);
        context.registerMenuScreen(ModRegistry.PERSONAL_LINKED_POUCH_MENU_TYPE.value(), ContainerScreen::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(LinkedChestModel.MODEL_LAYER_LOCATION, LinkedChestModel::createSingleBodyLayer);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value(), LinkedChestRenderer::new);
    }

    @Override
    public void onRegisterItemColorProviders(ColorProvidersContext<Item, ItemColor> context) {
        context.registerColorProvider((ItemStack itemStack, int layer) -> {
            DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                    DyeChannel.DEFAULT
            );
            DyeColor dyeColor = switch (layer) {
                case 1 -> dyeChannel.leftColor();
                case 2 -> dyeChannel.middleColor();
                case 3 -> dyeChannel.rightColor();
                default -> DyeColor.WHITE;
            };
            return FastColor.ARGB32.opaque(dyeColor.getMapColor().col);
        }, ModRegistry.LINKED_CHEST_ITEM.value(), ModRegistry.LINKED_POUCH_ITEM.value());
    }

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        LinkedChestBlockEntity blockEntity = new LinkedChestBlockEntity(BlockPos.ZERO,
                ModRegistry.LINKED_CHEST_BLOCK.value().defaultBlockState()
        );
        context.registerItemRenderer(
                (ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) -> {
                    DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                            DyeChannel.DEFAULT
                    );
                    blockEntity.setDyeChannel(dyeChannel);
                    // no clue why this is rotated in the first place, but this fixes it and makes it look like vanilla chests
                    poseStack.pushPose();
                    poseStack.translate(0.5F, 0.5F, 0.5F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.translate(-0.5F, -0.5F, -0.5F);
                    Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack,
                            multiBufferSource, packedLight, packedOverlay
                    );
                    poseStack.popPose();
                }, ModRegistry.LINKED_CHEST_BLOCK.value());
    }

    public static ModelLayerLocation id(String path) {
        return new ModelLayerLocation(LinkedChests.id(path), "main");
    }
}
