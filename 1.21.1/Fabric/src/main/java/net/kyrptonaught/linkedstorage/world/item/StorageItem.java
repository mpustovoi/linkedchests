package net.kyrptonaught.linkedstorage.world.item;

import net.kyrptonaught.linkedstorage.inventory.LinkedMenu;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.kyrptonaught.linkedstorage.world.level.block.StorageBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class StorageItem extends Item {

    public StorageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Player player = context.getPlayer();
            if (player.isShiftKeyDown() && level.getBlockState(context.getClickedPos())
                    .getBlock() instanceof StorageBlock) {
                DyeChannel channel = LinkedInventoryHelper.getBlockChannel(level, context.getClickedPos());
                LinkedInventoryHelper.setItemChannel(channel, context.getItemInHand());
            } else {
                this.use(level, player, context.getHand());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (!level.isClientSide) {
            DyeChannel dyeChannel = LinkedInventoryHelper.getItemChannel(itemInHand);
            LinkedMenu.openMenu((ServerPlayer) player, dyeChannel);
        }
        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DyeChannel channel = LinkedInventoryHelper.getItemChannel(stack);
        for (Component component : channel.getCleanName()) {
            tooltipComponents.add(((MutableComponent) component).withStyle(ChatFormatting.GRAY));
        }
    }
}
