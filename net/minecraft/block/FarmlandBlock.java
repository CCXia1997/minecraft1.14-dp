package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import java.util.Iterator;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class FarmlandBlock extends Block
{
    public static final IntegerProperty MOISTURE;
    protected static final VoxelShape SHAPE;
    
    protected FarmlandBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)FarmlandBlock.MOISTURE, 0));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.UP && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos.up());
        return !blockState4.getMaterial().isSolid() || blockState4.getBlock() instanceof FenceGateBlock;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        if (!this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return Blocks.j.getDefaultState();
        }
        return super.getPlacementState(ctx);
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return FarmlandBlock.SHAPE;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            setToDirt(state, world, pos);
            return;
        }
        final int integer5 = state.<Integer>get((Property<Integer>)FarmlandBlock.MOISTURE);
        if (isWaterNearby(world, pos) || world.hasRain(pos.up())) {
            if (integer5 < 7) {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)FarmlandBlock.MOISTURE, 7), 2);
            }
        }
        else if (integer5 > 0) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)FarmlandBlock.MOISTURE, integer5 - 1), 2);
        }
        else if (!hasCrop(world, pos)) {
            setToDirt(state, world, pos);
        }
    }
    
    @Override
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        if (!world.isClient && world.random.nextFloat() < distance - 0.5f && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean("mobGriefing")) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512f) {
            setToDirt(world.getBlockState(pos), world, pos);
        }
        super.onLandedUpon(world, pos, entity, distance);
    }
    
    public static void setToDirt(final BlockState state, final World world, final BlockPos pos) {
        world.setBlockState(pos, Block.pushEntitiesUpBeforeBlockChange(state, Blocks.j.getDefaultState(), world, pos));
    }
    
    private static boolean hasCrop(final BlockView world, final BlockPos pos) {
        final Block block3 = world.getBlockState(pos.up()).getBlock();
        return block3 instanceof CropBlock || block3 instanceof StemBlock || block3 instanceof AttachedStemBlock;
    }
    
    private static boolean isWaterNearby(final ViewableWorld world, final BlockPos pos) {
        for (final BlockPos blockPos4 : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getFluidState(blockPos4).matches(FluidTags.a)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FarmlandBlock.MOISTURE);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        MOISTURE = Properties.MOISTURE;
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    }
}
