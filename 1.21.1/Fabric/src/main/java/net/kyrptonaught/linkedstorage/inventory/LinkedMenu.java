package net.kyrptonaught.linkedstorage.inventory;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.init.ModRegistry;
import net.kyrptonaught.linkedstorage.world.item.StorageItem;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;

public class LinkedMenu extends ChestMenu {
    private final DyeChannel dyeChannel;

    public LinkedMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this(containerId, inventory, new SimpleContainer(27), DyeChannel.fromBuf(registryFriendlyByteBuf));
    }

    public LinkedMenu(int syncId, Inventory playerInventory, DyeChannel dyeChannel) {
        this(syncId, playerInventory, LinkedStorageMod.getInventory(dyeChannel), dyeChannel);
    }

    private LinkedMenu(int containerId, Inventory inventory, Container container, DyeChannel dyeChannel) {
        super(ModRegistry.LINKED_STORAGE_MENU_TYPE.value(), containerId, inventory, container, 3);
        this.dyeChannel = dyeChannel;
    }

    @Override
    public void clicked(int slotId, int clickData, ClickType actionType, Player player) {
        if (slotId > -1 && this.getSlot(slotId).getItem().getItem() instanceof StorageItem && this.getSlot(slotId).container instanceof Inventory) {
            if (this.dyeChannel.equals(LinkedInventoryHelper.getItemChannel(this.getSlot(slotId).getItem()))) {
                return;
            }
        }

        super.clicked(slotId, clickData, actionType, player);
    }

    public static void openMenu(ServerPlayer player, DyeChannel dyeChannel) {
        SimpleMenuProvider simpleMenuProvider = new SimpleMenuProvider(
                (int containerId, Inventory inventory, Player player1) -> {
                    ChannelViewers.addViewerFor(dyeChannel.getChannelName(), player);
                    return new LinkedMenu(containerId, inventory, dyeChannel);
                }, Component.translatable("container.linkedstorage"));
        CommonAbstractions.INSTANCE.openMenu(
                player, simpleMenuProvider, (ServerPlayer serverPlayer, RegistryFriendlyByteBuf buf) -> {
                    dyeChannel.toBuf(buf);
                });
    }
}
