package net.kyrptonaught.linkedstorage.world.level.block;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.block.v1.entity.TickingEntityBlock;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.network.client.OpenStoragePacket;
import net.kyrptonaught.linkedstorage.network.client.SetDyePacket;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.kyrptonaught.linkedstorage.world.level.block.entity.StorageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class StorageBlock extends HorizontalDirectionalBlock implements TickingEntityBlock<StorageBlockEntity>, WorldlyContainerHolder {
    public static final MapCodec<StorageBlock> CODEC = simpleCodec(StorageBlock::new);

    public StorageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    private boolean didHitButton(VoxelShape button, BlockPos pos, Vec3 hit) {
        return button.bounds().inflate(.001).move(pos.getX(), pos.getY(), pos.getZ()).contains(hit);
    }

    private boolean checkButtons(BlockState state, BlockPos pos, BlockHitResult hit) {
        VoxelShape[] buttons = NORTH_SOUTH_BUTTONS;
        if (state.getValue(FACING).equals(Direction.EAST) || state.getValue(FACING).equals(Direction.WEST)) {
            buttons = EAST_WEST_BUTTONS;
        }
        for (int i = 0; i < buttons.length; i++)
            if (this.didHitButton(buttons[i], pos, hit.getLocation())) {
                if (state.getValue(FACING).equals(Direction.NORTH) || state.getValue(FACING).equals(Direction.EAST)) {
                    LinkedStorageMod.NETWORK.sendMessage(new SetDyePacket(2 - i, pos));
                    return true;
                } else {
                    LinkedStorageMod.NETWORK.sendMessage(new SetDyePacket(i, pos));
                    return true;
                }
            }
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        DyeChannel channel = LinkedInventoryHelper.getBlockChannel(level, pos);
        if (itemInHand.getItem().equals(Items.DIAMOND)) {
            if (channel instanceof PlayerDyeChannel) {
                channel = new DyeChannel(channel.dyeChannel.clone());
                LinkedInventoryHelper.setBlockChannel(channel, level, pos);
                if (!player.isCreative()) itemInHand.grow(1);
            } else {
                LinkedInventoryHelper.setBlockChannel(channel.toPlayerDyeChannel(player.getUUID()), level, pos);
                if (!player.isCreative()) itemInHand.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            if (itemInHand.getItem() instanceof DyeItem) {
                if (!this.checkButtons(state, pos, hitResult)) {
                    LinkedStorageMod.NETWORK.sendMessage(new OpenStoragePacket(pos));
                }
            } else {
                LinkedStorageMod.NETWORK.sendMessage(new OpenStoragePacket(pos));
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState blockState_1, LivingEntity livingEntity_1, ItemStack stack) {
        if (!world.isClientSide()) {
            LinkedInventoryHelper.setBlockChannel(LinkedInventoryHelper.getItemChannel(stack), world, pos);
        }
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor world, BlockPos pos) {
        return ((StorageBlockEntity) world.getBlockEntity(pos)).getLinkedInventory();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        DyeChannel dyechannel = LinkedInventoryHelper.getBlockChannel((Level) level, pos);
        ItemStack itemStack = new ItemStack(this);
        LinkedInventoryHelper.setItemChannel(dyechannel, itemStack);
        return itemStack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState_1) {
        return RenderShape.MODEL;
    }

    protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape[] NORTH_SOUTH_BUTTONS = new VoxelShape[]{
            Block.box(4, 14, 6, 6, 15, 10), Block.box(7, 14, 6, 9, 15, 10), Block.box(10, 14, 6, 12, 15, 10)
    };
    private static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 14, 15), NORTH_SOUTH_BUTTONS);
    private static final VoxelShape[] EAST_WEST_BUTTONS = new VoxelShape[]{
            Block.box(6, 14, 4, 10, 15, 6), Block.box(6, 14, 7, 10, 15, 9), Block.box(6, 14, 10, 10, 15, 12)
    };
    private static final VoxelShape EAST_WEST_SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 14, 15), EAST_WEST_BUTTONS);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        if (state.getValue(FACING).equals(Direction.EAST) || state.getValue(FACING).equals(Direction.WEST)) {
            return EAST_WEST_SHAPE;
        } else {
            return NORTH_SOUTH_SHAPE;
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(this.getContainer(state, world, pos));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DyeChannel channel = LinkedInventoryHelper.getItemChannel(stack);
        for (Component text : channel.getCleanName()) {
            tooltipComponents.add(((MutableComponent) text).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntityType<? extends StorageBlockEntity> getBlockEntityType() {
        return ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value();
    }
}
