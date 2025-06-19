package fuzs.linkedchests.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.client.handler.DyeChannelLidController;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record LinkedPouchOpenModelProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<LinkedPouchOpenModelProperty> MAP_CODEC = MapCodec.unit(new LinkedPouchOpenModelProperty());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                DyeChannel.DEFAULT);
        return DyeChannelLidController.getChestLidController(dyeChannel).getOpenness(1.0F) > 0.0F;
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
