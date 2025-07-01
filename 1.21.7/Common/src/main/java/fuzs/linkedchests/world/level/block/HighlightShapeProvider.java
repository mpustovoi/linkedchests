package fuzs.linkedchests.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Allows for returning a custom highlight shape while the player is looking at the block. Usually the level renderer
 * then calls
 * {@link net.minecraft.world.level.block.Block#getShape(BlockState, BlockGetter, BlockPos, CollisionContext)}, this
 * method allows for overriding that.
 */
public interface HighlightShapeProvider {

    /**
     * @param state     the block state
     * @param level     the level
     * @param pos       the block position
     * @param hitVector the hit location from the {@link net.minecraft.world.phys.BlockHitResult}
     * @return the outline shape
     */
    VoxelShape getHighlightShape(BlockState state, BlockGetter level, BlockPos pos, Vec3 hitVector);
}
