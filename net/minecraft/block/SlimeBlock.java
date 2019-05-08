package net.minecraft.block;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.BlockView;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeBlock extends TransparentBlock
{
    public SlimeBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        if (entity.isSneaking()) {
            super.onLandedUpon(world, pos, entity, distance);
        }
        else {
            entity.handleFallDamage(distance, 0.0f);
        }
    }
    
    @Override
    public void onEntityLand(final BlockView world, final Entity entity) {
        if (entity.isSneaking()) {
            super.onEntityLand(world, entity);
        }
        else {
            final Vec3d vec3d3 = entity.getVelocity();
            if (vec3d3.y < 0.0) {
                final double double4 = (entity instanceof LivingEntity) ? 1.0 : 0.8;
                entity.setVelocity(vec3d3.x, -vec3d3.y * double4, vec3d3.z);
            }
        }
    }
    
    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
        final double double4 = Math.abs(entity.getVelocity().y);
        if (double4 < 0.1 && !entity.isSneaking()) {
            final double double5 = 0.4 + double4 * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(double5, 1.0, double5));
        }
        super.onSteppedOn(world, pos, entity);
    }
}
