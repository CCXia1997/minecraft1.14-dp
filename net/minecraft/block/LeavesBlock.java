package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;

public class LeavesBlock extends Block
{
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty PERSISTENT;
    protected static boolean translucentLeaves;
    
    public LeavesBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)LeavesBlock.DISTANCE, 7)).<Comparable, Boolean>with((Property<Comparable>)LeavesBlock.PERSISTENT, false));
    }
    
    @Override
    public boolean hasRandomTicks(final BlockState state) {
        return state.<Integer>get((Property<Integer>)LeavesBlock.DISTANCE) == 7 && !state.<Boolean>get((Property<Boolean>)LeavesBlock.PERSISTENT);
    }
    
    @Override
    public void onRandomTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.<Boolean>get((Property<Boolean>)LeavesBlock.PERSISTENT) && state.<Integer>get((Property<Integer>)LeavesBlock.DISTANCE) == 7) {
            Block.dropStacks(state, world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), 3);
    }
    
    @Override
    public int getLightSubtracted(final BlockState state, final BlockView view, final BlockPos pos) {
        return 1;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final int integer7 = getDistanceFromLog(neighborState) + 1;
        if (integer7 != 1 || state.<Integer>get((Property<Integer>)LeavesBlock.DISTANCE) != integer7) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return state;
    }
    
    private static BlockState updateDistanceFromLogs(final BlockState state, final IWorld world, final BlockPos pos) {
        int integer4 = 7;
        try (final BlockPos.PooledMutable pooledMutable5 = BlockPos.PooledMutable.get()) {
            for (final Direction direction10 : Direction.values()) {
                pooledMutable5.set((Vec3i)pos).setOffset(direction10);
                integer4 = Math.min(integer4, getDistanceFromLog(world.getBlockState(pooledMutable5)) + 1);
                if (integer4 == 1) {
                    break;
                }
            }
        }
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)LeavesBlock.DISTANCE, integer4);
    }
    
    private static int getDistanceFromLog(final BlockState state) {
        if (BlockTags.o.contains(state.getBlock())) {
            return 0;
        }
        if (state.getBlock() instanceof LeavesBlock) {
            return state.<Integer>get((Property<Integer>)LeavesBlock.DISTANCE);
        }
        return 7;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!world.hasRain(pos.up())) {
            return;
        }
        if (rnd.nextInt(15) != 1) {
            return;
        }
        final BlockPos blockPos5 = pos.down();
        final BlockState blockState6 = world.getBlockState(blockPos5);
        if (blockState6.isFullBoundsCubeForCulling() && Block.isSolidFullSquare(blockState6, world, blockPos5, Direction.UP)) {
            return;
        }
        final double double7 = pos.getX() + rnd.nextFloat();
        final double double8 = pos.getY() - 0.05;
        final double double9 = pos.getZ() + rnd.nextFloat();
        world.addParticle(ParticleTypes.m, double7, double8, double9, 0.0, 0.0, 0.0);
    }
    
    @Environment(EnvType.CLIENT)
    public static void setRenderingMode(final boolean fancy) {
        LeavesBlock.translucentLeaves = fancy;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return false;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return LeavesBlock.translucentLeaves ? BlockRenderLayer.MIPPED_CUTOUT : BlockRenderLayer.SOLID;
    }
    
    @Override
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return updateDistanceFromLogs(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)LeavesBlock.PERSISTENT, true), ctx.getWorld(), ctx.getBlockPos());
    }
    
    static {
        DISTANCE = Properties.DISTANCE_1_7;
        PERSISTENT = Properties.PERSISTENT;
    }
}
