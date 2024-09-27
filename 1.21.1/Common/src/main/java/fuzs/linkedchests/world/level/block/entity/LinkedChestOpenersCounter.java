package fuzs.linkedchests.world.level.block.entity;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.network.UpdateLidControllerMessage;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Adapted from {@link net.minecraft.world.level.block.entity.ContainerOpenersCounter}.
 */
public class LinkedChestOpenersCounter {
    private final Predicate<NonNullList<ItemStack>> containerChecker;
    private int openCount;
    private boolean scheduleRecheck;

    public LinkedChestOpenersCounter(Predicate<NonNullList<ItemStack>> containerChecker) {
        this.containerChecker = containerChecker;
    }

    protected void openerCountChanged(DyeChannel dyeChannel, MinecraftServer server, int openCount) {
        // vanilla uses a block event to synchronize the changed openers count, we need to send this to all connected clients though,
        // not just whoever is tracking a certain block
        LinkedChests.NETWORK.sendMessage(PlayerSet.ofAll(server),
                new UpdateLidControllerMessage(dyeChannel, openCount > 0)
        );
    }

    protected boolean isOwnContainer(Player player) {
        return player.containerMenu instanceof ChestMenu chestMenu &&
                chestMenu.getContainer() instanceof ListBackedContainer container && this.containerChecker.test(
                container.getContainerItems());
    }

    public void incrementOpeners(DyeChannel dyeChannel, ServerPlayer serverPlayer) {
        this.incrementOpeners(dyeChannel, serverPlayer, serverPlayer.blockPosition(), serverPlayer.getSoundSource());
    }

    public void incrementOpeners(DyeChannel dyeChannel, ServerPlayer serverPlayer, BlockPos pos, SoundSource soundSource) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        if (this.openCount++ == 0) {
            serverLevel.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_OPEN, soundSource, 0.5F, serverLevel.random.nextFloat() * 0.1F + 0.9F
            );
            serverLevel.gameEvent(serverPlayer, GameEvent.CONTAINER_OPEN, pos);
            this.scheduleRecheck = true;
        }

        this.openerCountChanged(dyeChannel, serverLevel.getServer(), this.openCount);
    }

    public void decrementOpeners(DyeChannel dyeChannel, ServerPlayer serverPlayer) {
        this.decrementOpeners(dyeChannel, serverPlayer, serverPlayer.blockPosition(), serverPlayer.getSoundSource());
    }

    public void decrementOpeners(DyeChannel dyeChannel, ServerPlayer serverPlayer, BlockPos pos, SoundSource soundSource) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        if (--this.openCount == 0) {
            serverLevel.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_CLOSE, soundSource, 0.5F, serverLevel.random.nextFloat() * 0.1F + 0.9F
            );
            serverLevel.gameEvent(serverPlayer, GameEvent.CONTAINER_CLOSE, pos);
        }

        this.openerCountChanged(dyeChannel, serverLevel.getServer(), this.openCount);
    }

    public void recheckOpeners(DyeChannel dyeChannel, MinecraftServer server) {
        if (this.scheduleRecheck) {
            this.scheduleRecheck = false;
            int playersWithContainerOpen = this.getPlayersWithContainerOpen(server).size();
            if (this.openCount != playersWithContainerOpen) {
                this.openCount = playersWithContainerOpen;
                this.openerCountChanged(dyeChannel, server, playersWithContainerOpen);
            }
            if (this.openCount > 0) {
                this.scheduleRecheck = true;
            }
        }
    }

    private List<? extends Player> getPlayersWithContainerOpen(MinecraftServer server) {
        return server.getPlayerList().getPlayers().stream().filter(this::isOwnContainer).toList();
    }
}
