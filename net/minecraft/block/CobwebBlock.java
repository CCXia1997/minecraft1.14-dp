package net.minecraft.block;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CobwebBlock extends Block
{
    public CobwebBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        entity.slowMovement(state, new Vec3d(0.25, 0.05000000074505806, 0.25));
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
