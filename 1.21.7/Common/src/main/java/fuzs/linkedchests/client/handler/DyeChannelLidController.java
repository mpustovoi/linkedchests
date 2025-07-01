package fuzs.linkedchests.client.handler;

import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.world.level.block.entity.ChestLidController;

import java.util.HashMap;
import java.util.Map;

public final class DyeChannelLidController {
    private static final Map<DyeChannel, ChestLidController> CHEST_LID_CONTROLLERS = new HashMap<>();

    public static ChestLidController getChestLidController(DyeChannel dyeChannel) {
        return CHEST_LID_CONTROLLERS.computeIfAbsent(dyeChannel, (DyeChannel dyeChannelX) -> new ChestLidController());
    }

    public static void onEndClientTick(Minecraft minecraft) {
        if (minecraft.level != null && !minecraft.isPaused()) {
            CHEST_LID_CONTROLLERS.values().forEach(ChestLidController::tickLid);
        }
    }

    public static void onLoggedIn(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        CHEST_LID_CONTROLLERS.clear();
    }
}