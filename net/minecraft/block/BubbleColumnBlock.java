package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.StateFactory;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.world.BlockView;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class BubbleColumnBlock extends Block implements FluidDrainable
{
    public static final BooleanProperty DRAG;
    
    public BubbleColumnBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)BubbleColumnBlock.DRAG, true));
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        final BlockState blockState5 = world.getBlockState(pos.up());
        if (blockState5.isAir()) {
            entity.j(state.<Boolean>get((Property<Boolean>)BubbleColumnBlock.DRAG));
            if (!world.isClient) {
                final ServerWorld serverWorld6 = (ServerWorld)world;
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    serverWorld6.<DefaultParticleType>spawnParticles(ParticleTypes.X, pos.getX() + world.random.nextFloat(), pos.getY() + 1, pos.getZ() + world.random.nextFloat(), 1, 0.0, 0.0, 0.0, 1.0);
                    serverWorld6.<DefaultParticleType>spawnParticles(ParticleTypes.e, pos.getX() + world.random.nextFloat(), pos.getY() + 1, pos.getZ() + world.random.nextFloat(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        }
        else {
            entity.k(state.<Boolean>get((Property<Boolean>)BubbleColumnBlock.DRAG));
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        update(world, pos.up(), calculateDrag(world, pos.down()));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        update(world, pos.up(), calculateDrag(world, pos));
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        return Fluids.WATER.getStill(false);
    }
    
    public static void update(final IWorld world, final BlockPos pos, final boolean drag) {
        if (isStillWater(world, pos)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.kU.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)BubbleColumnBlock.DRAG, drag), 2);
        }
    }
    
    public static boolean isStillWater(final IWorld world, final BlockPos pos) {
        final FluidState fluidState3 = world.getFluidState(pos);
        return world.getBlockState(pos).getBlock() == Blocks.A && fluidState3.getLevel() >= 8 && fluidState3.isStill();
    }
    
    private static boolean calculateDrag(final BlockView world, final BlockPos pos) {
        final BlockState blockState3 = world.getBlockState(pos);
        final Block block4 = blockState3.getBlock();
        if (block4 == Blocks.kU) {
            return blockState3.<Boolean>get((Property<Boolean>)BubbleColumnBlock.DRAG);
        }
        return block4 != Blocks.cK;
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 5;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final double double5 = pos.getX();
        final double double6 = pos.getY();
        final double double7 = pos.getZ();
        if (state.<Boolean>get((Property<Boolean>)BubbleColumnBlock.DRAG)) {
            world.addImportantParticle(ParticleTypes.aa, double5 + 0.5, double6 + 0.8, double7, 0.0, 0.0, 0.0);
            if (rnd.nextInt(200) == 0) {
                world.playSound(double5, double6, double7, SoundEvents.au, SoundCategory.e, 0.2f + rnd.nextFloat() * 0.2f, 0.9f + rnd.nextFloat() * 0.15f, false);
            }
        }
        else {
            world.addImportantParticle(ParticleTypes.ab, double5 + 0.5, double6, double7 + 0.5, 0.0, 0.04, 0.0);
            world.addImportantParticle(ParticleTypes.ab, double5 + rnd.nextFloat(), double6 + rnd.nextFloat(), double7 + rnd.nextFloat(), 0.0, 0.04, 0.0);
            if (rnd.nextInt(200) == 0) {
                world.playSound(double5, double6, double7, SoundEvents.as, SoundCategory.e, 0.2f + rnd.nextFloat() * 0.2f, 0.9f + rnd.nextFloat() * 0.15f, false);
            }
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.A.getDefaultState();
        }
        if (facing == Direction.DOWN) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.kU.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)BubbleColumnBlock.DRAG, calculateDrag(world, neighborPos)), 2);
        }
        else if (facing == Direction.UP && neighborState.getBlock() != Blocks.kU && isStillWater(world, neighborPos)) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        }
        world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Block block4 = world.getBlockState(pos.down()).getBlock();
        return block4 == Blocks.kU || block4 == Blocks.iB || block4 == Blocks.cK;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.empty();
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BubbleColumnBlock.DRAG);
    }
    
    @Override
    public Fluid tryDrainFluid(final IWorld world, final BlockPos pos, final BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }
    
    static {
        DRAG = Properties.DRAG;
    }
}
