package fuzs.linkedchests.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import fuzs.puzzleslib.api.util.v1.ShapesHelper;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LinkedChestBlock extends EnderChestBlock implements HighlightShapeProvider {
    public static final MapCodec<EnderChestBlock> CODEC = simpleCodec(LinkedChestBlock::new);
    static final VoxelShape LEFT_BUTTON_SHAPE = Block.box(4.0, 14.0, 6.0, 6.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> LEFT_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(LEFT_BUTTON_SHAPE);
    static final VoxelShape MIDDLE_BUTTON_SHAPE = Block.box(7.0, 14.0, 6.0, 9.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> MIDDLE_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(MIDDLE_BUTTON_SHAPE);
    static final VoxelShape RIGHT_BUTTON_SHAPE = Block.box(10.0, 14.0, 6.0, 12.0, 15.0, 10.0);
    static final Map<Direction, VoxelShape> RIGHT_BUTTON_SHAPES = ShapesHelper.rotateHorizontally(RIGHT_BUTTON_SHAPE);
    static final VoxelShape LATCH_SHAPE = Block.box(7.0, 7.0, 15.0, 9.0, 11.0, 16.0);
    static final Map<Direction, VoxelShape> LATCH_SHAPES = ShapesHelper.rotateHorizontally(LATCH_SHAPE);
    static final Map<Direction, VoxelShape> SHAPES = Direction.Plane.HORIZONTAL.stream()
            .collect(Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(),
                    (Direction direction) -> {
                        return Shapes.or(EnderChestBlock.SHAPE,
                                LEFT_BUTTON_SHAPES.get(direction),
                                MIDDLE_BUTTON_SHAPES.get(direction),
                                RIGHT_BUTTON_SHAPES.get(direction),
                                LATCH_SHAPES.get(direction));
                    }));

    public LinkedChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<EnderChestBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return EnderChestBlock.SHAPE;
    }

    @Override
    public VoxelShape getHighlightShape(BlockState state, BlockGetter level, BlockPos pos, Vec3 hitVector) {
        Direction direction = state.getValue(FACING);
        hitVector = hitVector.subtract(pos.getX(), pos.getY(), pos.getZ());
        List<Map<Direction, VoxelShape>> shapes = List.of(LEFT_BUTTON_SHAPES,
                MIDDLE_BUTTON_SHAPES,
                RIGHT_BUTTON_SHAPES,
                LATCH_SHAPES);
        for (Map<Direction, VoxelShape> map : shapes) {
            VoxelShape voxelShape = map.get(direction);
            if (voxelShape.bounds().inflate(0.001).contains(hitVector)) {
                return voxelShape;
            }
        }

        return EnderChestBlock.SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof LinkedChestBlockEntity blockEntity) {
            DyeChannel dyeChannel = blockEntity.getDyeChannel();
            Direction direction = state.getValue(FACING);
            Vec3 hitVector = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            if (itemInHand.is(ModRegistry.DYE_CHANNEL_COLOR_PROVIDERS_ITEM_TAG)) {
                DyeColor dyeColor = DyeChannel.getDyeColor(itemInHand.getItem());
                DyeChannel newDyeChannel = dyeChannel;
                if (LEFT_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    newDyeChannel = dyeChannel.withLeftColor(dyeColor);
                } else if (MIDDLE_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    newDyeChannel = dyeChannel.withMiddleColor(dyeColor);
                } else if (RIGHT_BUTTON_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                    newDyeChannel = dyeChannel.withRightColor(dyeColor);
                }
                if (dyeChannel != newDyeChannel) {
                    if (!level.isClientSide) {
                        blockEntity.setDyeChannel(newDyeChannel);
                        level.playSound(null,
                                pos,
                                SoundEvents.CHISELED_BOOKSHELF_INSERT,
                                SoundSource.BLOCKS,
                                1.0F,
                                1.0F);
                        itemInHand.consume(1, player);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                    return InteractionResultHelper.sidedSuccess(level.isClientSide);
                }
            } else if (LATCH_SHAPES.get(direction).bounds().inflate(0.001).contains(hitVector)) {
                if (dyeChannel.uuid().isPresent()) {
                    if (itemInHand.isEmpty() && player.isShiftKeyDown()) {
                        if (!level.isClientSide) {
                            blockEntity.setDyeChannel(dyeChannel.withUUID(null));
                            level.playSound(null,
                                    pos,
                                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                                    SoundSource.BLOCKS,
                                    1.0F,
                                    1.0F);
                            ItemStack itemStack = blockEntity.removeLatchItem();
                            if (!player.getInventory().add(itemStack)) {
                                player.drop(itemStack, false);
                            }

                            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                        return InteractionResultHelper.sidedSuccess(level.isClientSide);
                    }
                } else if (itemInHand.is(ModRegistry.PERSONAL_CHANNEL_PROVIDERS_ITEM_TAG)) {
                    if (!level.isClientSide) {
                        blockEntity.setDyeChannel(dyeChannel.withUUID(player.getUUID()));
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                        // we must store the used item to return it when removed as the tag can allow anything, so we would not know what was used initially
                        blockEntity.setLatchItem(itemInHand.copyWithCount(1));
                        itemInHand.consume(1, player);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                    return InteractionResultHelper.sidedSuccess(level.isClientSide);
                }
            }
        }

        if (level instanceof ServerLevel serverLevel &&
                !level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above())) {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
                PiglinAi.angerNearbyPiglins(serverLevel, player, true);
            }
        }

        return InteractionResultHelper.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState blockState, boolean includeData) {
        ItemStack itemStack = super.getCloneItemStack(level, pos, blockState, includeData);
        if (level.getBlockEntity(pos) instanceof LinkedChestBlockEntity blockEntity) {
            itemStack.set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), blockEntity.getDyeChannel());
        }
        return itemStack;
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(Proxy.INSTANCE.splitTooltipLines(this.getDescriptionComponent()));
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LinkedChestBlockEntity(pos, state);
    }
}
