package fuzs.linkedchests.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.block.v1.entity.TickingEntityBlock;
import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LinkedChestBlock extends EnderChestBlock implements TickingEntityBlock<LinkedChestBlockEntity>, HighlightShapeProvider {
    public static final MapCodec<EnderChestBlock> CODEC = simpleCodec(LinkedChestBlock::new);
    protected static final VoxelShape BOX_SHAPE = EnderChestBlock.SHAPE;
    static final VoxelShape LEFT_BUTTON_SHAPE = Block.box(4.0, 14.0, 6.0, 6.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> LEFT_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(LEFT_BUTTON_SHAPE);
    static final VoxelShape MIDDLE_BUTTON_SHAPE = Block.box(7.0, 14.0, 6.0, 9.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> MIDDLE_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(MIDDLE_BUTTON_SHAPE);
    static final VoxelShape RIGHT_BUTTON_SHAPE = Block.box(10.0, 14.0, 6.0, 12.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> RIGHT_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(RIGHT_BUTTON_SHAPE);
    static final VoxelShape LATCH_SHAPE = Block.box(7.0, 7.0, 15.0, 9.0, 11.0, 16.0);
    static final Map<Direction, VoxelShape> LATCH_SHAPES = ShapesHelper.rotateHorizontally(LATCH_SHAPE);
    static final Map<Direction, VoxelShape> SHAPES = Direction.Plane.HORIZONTAL.stream().collect(
            Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(), (Direction direction) -> {
                return Shapes.or(BOX_SHAPE, LEFT_BUTTON_SHAPES.get(direction), MIDDLE_BUTTON_SHAPES.get(direction),
                        RIGHT_BUTTON_SHAPES.get(direction), LATCH_SHAPES.get(direction)
                );
            }));

    public LinkedChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<EnderChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof LinkedChestBlockEntity blockEntity) {
            DyeChannel dyeChannel = blockEntity.getDyeChannel();
            Direction direction = state.getValue(FACING);
            Vec3 hitVector = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            if (itemInHand.is(ModRegistry.COLOR_CHANNEL_DYES_ITEM_TAG)) {
                DyeColor dyeColor = DyeChannel.getDyeColor(itemInHand.getItem());
                if (LEFT_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    blockEntity.setDyeChannel(dyeChannel.withLeftColor(dyeColor));
                    itemInHand.shrink(1);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                } else if (MIDDLE_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    blockEntity.setDyeChannel(dyeChannel.withMiddleColor(dyeColor));
                    itemInHand.shrink(1);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                } else if (RIGHT_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    blockEntity.setDyeChannel(dyeChannel.withRightColor(dyeColor));
                    itemInHand.shrink(1);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            } else if (LATCH_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                if (dyeChannel.uuid().isPresent()) {
                    if (itemInHand.isEmpty() && player.isShiftKeyDown()) {
                        blockEntity.setDyeChannel(dyeChannel.withUUID(null));
                        if (!level.isClientSide) {
                            ItemStack itemStack = new ItemStack(Items.DIAMOND);
                            level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP, SoundSource.BLOCKS, 1.0F,
                                    1.0F
                            );
                            if (!player.getInventory().add(itemStack)) {
                                player.drop(itemStack, false);
                            }

                            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                } else if (itemInHand.is(Items.DIAMOND)) {
                    blockEntity.setDyeChannel(dyeChannel.withUUID(player.getUUID()));
                    level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_INSERT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    itemInHand.shrink(1);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        if (level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above())) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        } else {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return ItemInteractionResult.CONSUME;
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState blockState) {
        ItemStack itemStack = super.getCloneItemStack(level, pos, blockState);
        if (level.getBlockEntity(pos) instanceof LinkedChestBlockEntity blockEntity) {
            itemStack.set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), blockEntity.getDyeChannel());
        }
        return itemStack;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getHighlightShape(BlockState state, BlockGetter level, BlockPos pos, Vec3 hitVector) {
        Direction direction = state.getValue(FACING);
        hitVector = hitVector.subtract(pos.getX(), pos.getY(), pos.getZ());
        List<Map<Direction, VoxelShape>> shapes = List.of(LEFT_BUTTON_SHAPES, MIDDLE_BUTTON_SHAPES, RIGHT_BUTTON_SHAPES,
                LATCH_SHAPES
        );
        for (Map<Direction, VoxelShape> map : shapes) {
            VoxelShape voxelShape = map.get(direction);
            if (voxelShape.bounds().inflate(0.001).contains(hitVector)) {
                return voxelShape;
            }
        }

        return BOX_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOX_SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // NO-OP
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof LinkedChestBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(Proxy.INSTANCE.splitTooltipLines(this.getDescriptionComponent()));
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public BlockEntityType<? extends LinkedChestBlockEntity> getBlockEntityType() {
        return ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TickingEntityBlock.super.newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return TickingEntityBlock.super.getTicker(level, state, blockEntityType);
    }
}
