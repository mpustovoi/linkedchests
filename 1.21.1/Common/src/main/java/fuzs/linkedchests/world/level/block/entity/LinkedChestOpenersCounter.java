package fuzs.linkedchests.world.level.block.entity;

import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.function.Predicate;

public class LinkedChestOpenersCounter extends ContainerOpenersCounter {
    private final Predicate<NonNullList<ItemStack>> containerChecker;
    private int openCount;

    public LinkedChestOpenersCounter(Predicate<NonNullList<ItemStack>> containerChecker) {
        this.containerChecker = containerChecker;
    }

    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_OPEN,
                SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F
        );
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_CLOSE,
                SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F
        );
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
        level.blockEvent(pos, state.getBlock(), 1, openCount);
    }

    @Override
    protected boolean isOwnContainer(Player player) {
        return player.containerMenu instanceof ChestMenu chestMenu &&
                chestMenu.getContainer() instanceof ListBackedContainer container && this.containerChecker.test(
                container.getContainerItems());
    }

    public void incrementOpeners(Player player, Level level, BlockPos pos, BlockState state) {
        int i = this.openCount++;
        if (i == 0) {
            this.onOpen(level, pos, state);
            level.gameEvent(player, GameEvent.CONTAINER_OPEN, pos);
            scheduleRecheck(level, pos, state);
        }

        this.openerCountChanged(level, pos, state, i, this.openCount);
    }

    private List<? extends Player> getPlayersWithContainerOpen(Level level, BlockPos pos) {
        return ((ServerLevel) level).getServer().getPlayerList().getPlayers().stream().filter(this::isOwnContainer).toList();
    }

    public void recheckOpeners(Level level, BlockPos pos, BlockState state) {
        List<? extends Player> list = this.getPlayersWithContainerOpen(level, pos);

        int i = list.size();
        int j = this.openCount;
        if (j != i) {
            boolean bl = i != 0;
            boolean bl2 = j != 0;
            if (bl && !bl2) {
                this.onOpen(level, pos, state);
                level.gameEvent(null, GameEvent.CONTAINER_OPEN, pos);
            } else if (!bl) {
                this.onClose(level, pos, state);
                level.gameEvent(null, GameEvent.CONTAINER_CLOSE, pos);
            }

            this.openCount = i;
        }

        this.openerCountChanged(level, pos, state, j, i);
        if (i > 0) {
            scheduleRecheck(level, pos, state);
        }
    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void scheduleRecheck(Level level, BlockPos pos, BlockState state) {
        MinecraftServer server = ((ServerLevel) level).getServer();
        server.tell(new TickTask(server.getTickCount() + 5, ));
        level.scheduleTick(pos, state.getBlock(), 5);
    }
}
