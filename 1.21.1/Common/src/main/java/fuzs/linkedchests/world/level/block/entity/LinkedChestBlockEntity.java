package fuzs.linkedchests.world.level.block.entity;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import fuzs.linkedchests.world.inventory.LinkedMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LinkedChestBlockEntity extends BlockEntity implements ListBackedContainer, MenuProvider, LidBlockEntity, TickingBlockEntity {
    static final String KEY_DYE_CHANNEL = LinkedChests.id("dye_channel").toString();

    private final ChestLidController chestLidController = new ChestLidController();
    private DyeChannel dyeChannel = DyeChannel.DEFAULT_CHANNEL;
    @Nullable
    private DyeChannelStorage storage;

    public LinkedChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.LINKED_CHEST_BLOCK_ENTITY.value(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.loadAdditional(compoundTag, registries);
        this.dyeChannel = DyeChannel.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE),
                compoundTag.getCompound(KEY_DYE_CHANNEL)
        ).resultOrPartial(LinkedChests.LOGGER::error).orElse(DyeChannel.DEFAULT_CHANNEL);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.saveAdditional(compoundTag, registries);
        DyeChannel.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.dyeChannel)
                .resultOrPartial(LinkedChests.LOGGER::error)
                .ifPresent(tag -> compoundTag.put(KEY_DYE_CHANNEL, tag));
    }

    @Override
    public void clientTick() {
        this.chestLidController.tickLid();
    }

    public void setDyeChannel(DyeChannel dyeChannel) {
        Objects.requireNonNull(dyeChannel, "dye channel is null");
        if (!Objects.equals(dyeChannel, this.dyeChannel)) {
            this.dyeChannel = dyeChannel;
            this.storage = null;
            this.markUpdated();
        }
    }

    private DyeChannelStorage getStorage() {
        DyeChannelStorage storage = this.storage;
        if (storage == null) {
            return this.storage = DyeChannelManager.getStorage(this.dyeChannel,
                    !this.hasLevel() || this.getLevel().isClientSide
            );
        } else {
            return storage;
        }
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public DyeChannel getDyeChannel() {
        return this.dyeChannel;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.getStorage().openersCounter().incrementOpeners(player, this.getLevel(), this.getBlockPos(),
                    this.getBlockState()
            );
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.getStorage().openersCounter().decrementOpeners(player, this.getLevel(), this.getBlockPos(),
                    this.getBlockState()
            );
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.getStorage().openersCounter().recheckOpeners(this.getLevel(), this.getBlockPos(),
                    this.getBlockState()
            );
        }
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    @Override
    public NonNullList<ItemStack> getContainerItems() {
        return this.getStorage().items();
    }

    @Override
    public Component getDisplayName() {
        return ModRegistry.LINKED_CHEST_BLOCK.value().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new LinkedMenu(containerId, inventory, this, this.dyeChannel.uuid().isPresent(), false);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.dyeChannel = componentInput.get(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(), this.dyeChannel);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove(KEY_DYE_CHANNEL);
    }
}