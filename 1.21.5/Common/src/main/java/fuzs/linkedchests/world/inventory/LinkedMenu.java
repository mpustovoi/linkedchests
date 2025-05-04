package fuzs.linkedchests.world.inventory;

import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class LinkedMenu extends ChestMenu {

    public LinkedMenu(int containerId, Inventory inventory, LinkedData linkedData) {
        this(containerId, inventory, linkedData.createContainer(), linkedData.isOpenedFromPouch());
    }

    public LinkedMenu(int containerId, Inventory inventory, Container container, boolean isOpenedFromPouch) {
        super(ModRegistry.LINKED_STORAGE_MENU_TYPE.value(),
                containerId,
                inventory,
                container,
                container.getContainerSize() / 9);
        if (isOpenedFromPouch) ContainerMenuHelper.setSelectedSlotLocked(this);
    }

    public record LinkedData(boolean isPersonalChannel, boolean isOpenedFromPouch) {
        public static final StreamCodec<ByteBuf, LinkedData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL,
                LinkedData::isPersonalChannel,
                ByteBufCodecs.BOOL,
                LinkedData::isOpenedFromPouch,
                LinkedData::new);

        Container createContainer() {
            return new SimpleContainer(DyeChannel.getContainerSize(this.isPersonalChannel));
        }
    }
}
