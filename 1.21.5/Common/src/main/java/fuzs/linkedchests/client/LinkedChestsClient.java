package fuzs.linkedchests.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.client.color.item.DyeChannelTintSource;
import fuzs.linkedchests.client.handler.DyeChannelLidController;
import fuzs.linkedchests.client.model.LinkedChestModel;
import fuzs.linkedchests.client.renderer.blockentity.LinkedChestBlockEntityRenderer;
import fuzs.linkedchests.client.renderer.blockentity.LinkedChestRendererImpl;
import fuzs.linkedchests.client.renderer.item.properties.conditional.LinkedPouchOpenModelProperty;
import fuzs.linkedchests.client.renderer.item.properties.conditional.LinkedPouchPersonalModelProperty;
import fuzs.linkedchests.client.renderer.special.LinkedChestSpecialRenderer;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.item.LinkedPouchItem;
import fuzs.linkedchests.world.level.block.HighlightShapeProvider;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelsContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHighlightCallback;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinkedChestsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(DyeChannelLidController::onEndClientTick);
        ClientPlayerNetworkEvents.LOGGED_IN.register(DyeChannelLidController::onLoggedIn);
        RenderHighlightCallback.EVENT.register((LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, HitResult hitResult, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource multiBufferSource, ClientLevel level) -> {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.isAir() && level.getWorldBorder().isWithinBounds(blockPos) &&
                        blockState.getBlock() instanceof HighlightShapeProvider block) {
                    VoxelShape voxelShape = block.getHighlightShape(blockState,
                            level,
                            blockPos,
                            hitResult.getLocation());
                    VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
                    Vec3 cameraPosition = camera.getPosition();
                    double posX = blockPos.getX() - cameraPosition.x();
                    double posY = blockPos.getY() - cameraPosition.y();
                    double posZ = blockPos.getZ() - cameraPosition.z();
                    ShapeRenderer.renderShape(poseStack,
                            vertexConsumer,
                            voxelShape,
                            posX,
                            posY,
                            posZ,
                            ARGB.color(102, 0));
                    return EventResult.INTERRUPT;
                }
            }
            return EventResult.PASS;
        });
    }

    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.registerItemTooltip(LinkedChestBlock.class, LinkedChestBlock::getDescriptionComponent);
        ItemTooltipRegistry.registerItemTooltip(LinkedPouchItem.class, LinkedPouchItem::getDescriptionComponent);
    }

    @Override
    public void onRegisterItemModels(ItemModelsContext context) {
        context.registerItemTintSource(LinkedChests.id("dye_channel"), DyeChannelTintSource.MAP_CODEC);
        context.registerConditionalItemModelProperty(LinkedChests.id("linked_pouch/open"),
                LinkedPouchOpenModelProperty.MAP_CODEC);
        context.registerConditionalItemModelProperty(LinkedChests.id("linked_pouch/personal"),
                LinkedPouchPersonalModelProperty.MAP_CODEC);
        context.registerSpecialModelRenderer(LinkedChests.id("linked_chest"),
                LinkedChestSpecialRenderer.Unbaked.MAP_CODEC);
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
        context.registerLayerDefinition(LinkedChestRendererImpl.LINKED_CHEST_MODEL_LAYER_LOCATION,
                LinkedChestModel::createSingleBodyLayer);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value(),
                LinkedChestBlockEntityRenderer::new);
    }
}
