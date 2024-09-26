package fuzs.linkedchests.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface HighlightShapeProvider {

    VoxelShape getHighlightShape(BlockState state, BlockGetter level, BlockPos pos, Vec3 hitVector);
}
