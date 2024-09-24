package net.kyrptonaught.linkedstorage.world.level.block.entity;

import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OpenableBlockEntity extends BlockEntity implements LidBlockEntity, TickingBlockEntity {
    private float animationAngle;
    private float lastAnimationAngle;

    OpenableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected int countViewers() {
        return 0;
    }

    @Override
    public float getOpenNess(float f) {
        return Mth.lerp(f, this.lastAnimationAngle, this.animationAngle);
    }

    @Override
    public void clientTick() {
        int viewerCount = this.countViewers();
        this.lastAnimationAngle = this.animationAngle;
        if (viewerCount > 0 && this.animationAngle == 0.0F) this.playSound(SoundEvents.ENDER_CHEST_OPEN);
        if (viewerCount == 0 && this.animationAngle > 0.0F || viewerCount > 0 && this.animationAngle < 1.0F) {
            float float_2 = this.animationAngle;
            if (viewerCount > 0) {
                this.animationAngle += 0.1F;
            } else {
                this.animationAngle -= 0.1F;
            }
            this.animationAngle = Mth.clamp(this.animationAngle, 0, 1);
            if (this.animationAngle < 0.5F && float_2 >= 0.5F) this.playSound(SoundEvents.ENDER_CHEST_CLOSE);
        }
    }

    private void playSound(SoundEvent soundEvent) {
        double posX = (double) this.worldPosition.getX() + 0.5D;
        double posY = (double) this.worldPosition.getY() + 0.5D;
        double posZ = (double) this.worldPosition.getZ() + 0.5D;
        this.level.playLocalSound(posX, posY, posZ, soundEvent, SoundSource.BLOCKS, 0.5F,
                this.level.random.nextFloat() * 0.1F + 0.9F, false
        );
    }
}
