package net.minecraft.block;

import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class SoulSandBlock extends Block
{
    protected static final VoxelShape COLLISION_SHAPE;
    
    public SoulSandBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return SoulSandBlock.COLLISION_SHAPE;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        entity.setVelocity(entity.getVelocity().multiply(0.4, 1.0, 0.4));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        BubbleColumnBlock.update(world, pos.up(), false);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
    }
    
    @Override
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 20;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    }
}
