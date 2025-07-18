package fuzs.linkedchests.client.color.item;

import com.mojang.serialization.MapCodec;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public record DyeChannelTintSource(DyeSlot dyeSlot) implements ItemTintSource {
    public static final MapCodec<DyeChannelTintSource> MAP_CODEC = DyeSlot.CODEC.xmap(DyeChannelTintSource::new,
            DyeChannelTintSource::dyeSlot).fieldOf("dye_slot");

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                DyeChannel.DEFAULT);
        return this.dyeSlot.getColor(dyeChannel);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }

    public enum DyeSlot implements StringRepresentable {
        LEFT(DyeChannel::leftColor),
        MIDDLE(DyeChannel::middleColor),
        RIGHT(DyeChannel::rightColor);

        public static final StringRepresentableCodec<DyeSlot> CODEC = StringRepresentable.fromEnum(DyeSlot::values);

        private final Function<DyeChannel, DyeColor> colorGetter;

        DyeSlot(Function<DyeChannel, DyeColor> colorGetter) {
            this.colorGetter = colorGetter;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public int getColor(DyeChannel dyeChannel) {
            return ARGB.opaque(this.colorGetter.apply(dyeChannel).getMapColor().col);
        }
    }
}
