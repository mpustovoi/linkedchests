package fuzs.linkedchests.world.item;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.inventory.DyeChannelContainer;
import fuzs.linkedchests.world.inventory.LinkedMenu;
import fuzs.linkedchests.world.level.block.LinkedChestBlock;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.LinkedChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class LinkedStorageItem extends Item {

    public LinkedStorageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (context.getPlayer().isShiftKeyDown() && level.getBlockEntity(
                context.getClickedPos()) instanceof LinkedChestBlockEntity blockEntity) {
            if (!level.isClientSide) {
                context.getItemInHand().set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                        blockEntity.getDyeChannel()
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useOn(context);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (!level.isClientSide) {
            DyeChannel dyeChannel = itemInHand.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                    DyeChannel.DEFAULT
            );
            player.openMenu(new SimpleMenuProvider((int containerId, Inventory inventory, Player player1) -> {
                DyeChannelContainer container = new DyeChannelContainer(dyeChannel);
                return new LinkedMenu(containerId, inventory, container, dyeChannel.uuid().isPresent(), true);
            }, this.getDescription()));
        }
        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Component component = ((LinkedChestBlock) ModRegistry.LINKED_CHEST_BLOCK.value()).getDescriptionComponent();
        tooltipComponents.addAll(Proxy.INSTANCE.splitTooltipLines(component));
    }
}
