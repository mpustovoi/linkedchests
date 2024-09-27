package fuzs.linkedchests.world.inventory;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class LinkedMenu extends ChestMenu {

    public static MenuType.MenuSupplier<LinkedMenu> createSupplier(boolean personalChannel, boolean lockSelectedSlot) {
        return (int containerId, Inventory inventory) -> {
            Container container = new SimpleContainer(DyeChannel.getContainerSize(personalChannel));
            return new LinkedMenu(containerId, inventory, container, personalChannel, lockSelectedSlot);
        };
    }

    public LinkedMenu(int containerId, Inventory inventory, Container container, boolean personalChannel, boolean lockSelectedSlot) {
        super(selectMenuType(personalChannel, lockSelectedSlot), containerId, inventory, container,
                container.getContainerSize() / 9
        );
        if (lockSelectedSlot) ContainerMenuHelper.setSelectedSlotLocked(this);
    }

    private static MenuType<LinkedMenu> selectMenuType(boolean personalChannel, boolean lockSelectedSlot) {
        if (personalChannel) {
            return lockSelectedSlot ? ModRegistry.PERSONAL_LINKED_STORAGE_MENU_TYPE.value() :
                    ModRegistry.PERSONAL_LINKED_CHEST_MENU_TYPE.value();
        } else {
            return lockSelectedSlot ? ModRegistry.LINKED_STORAGE_MENU_TYPE.value() :
                    ModRegistry.LINKED_CHEST_MENU_TYPE.value();
        }
    }
}
