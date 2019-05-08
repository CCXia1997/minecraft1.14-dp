package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateFactory;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import java.util.Collections;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.context.LootContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Direction;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import com.google.common.collect.Lists;
import net.minecraft.fluid.FluidState;
import java.util.List;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.state.property.IntegerProperty;

public class FluidBlock extends Block implements FluidDrainable
{
    public static final IntegerProperty LEVEL;
    protected final BaseFluid fluid;
    private final List<FluidState> statesByLevel;
    
    protected FluidBlock(final BaseFluid baseFluid, final Settings settings) {
        super(settings);
        this.fluid = baseFluid;
        (this.statesByLevel = Lists.newArrayList()).add(baseFluid.getStill(false));
        for (int integer3 = 1; integer3 < 8; ++integer3) {
            this.statesByLevel.add(baseFluid.getFlowing(8 - integer3, false));
        }
        this.statesByLevel.add(baseFluid.getFlowing(8, true));
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)FluidBlock.LEVEL, 0));
    }
    
    @Override
    public void onRandomTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        world.getFluidState(pos).onRandomTick(world, pos, random);
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return !this.fluid.matches(FluidTags.b);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        final int integer2 = state.<Integer>get((Property<Integer>)FluidBlock.LEVEL);
        return this.statesByLevel.get(Math.min(integer2, 8));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean skipRenderingSide(final BlockState state, final BlockState neighbor, final Direction facing) {
        return neighbor.getFluidState().getFluid().matchesType(this.fluid) || super.isFullBoundsCubeForCulling(state);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    public List<ItemStack> getDroppedStacks(final BlockState state, final LootContext.Builder builder) {
        return Collections.<ItemStack>emptyList();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.empty();
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return this.fluid.getTickRate(world);
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.getFluidState().isStill() || neighborState.getFluidState().isStill()) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (this.receiveNeighborFluids(world, pos, state)) {
            world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.getTickRate(world));
        }
    }
    
    public boolean receiveNeighborFluids(final World world, final BlockPos pos, final BlockState state) {
        if (this.fluid.matches(FluidTags.b)) {
            boolean boolean4 = false;
            for (final Direction direction8 : Direction.values()) {
                if (direction8 != Direction.DOWN && world.getFluidState(pos.offset(direction8)).matches(FluidTags.a)) {
                    boolean4 = true;
                    break;
                }
            }
            if (boolean4) {
                final FluidState fluidState5 = world.getFluidState(pos);
                if (fluidState5.isStill()) {
                    world.setBlockState(pos, Blocks.bJ.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }
                if (fluidState5.getHeight(world, pos) >= 0.44444445f) {
                    world.setBlockState(pos, Blocks.m.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }
            }
        }
        return true;
    }
    
    private void playExtinguishSound(final IWorld world, final BlockPos pos) {
        world.playLevelEvent(1501, pos, 0);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FluidBlock.LEVEL);
    }
    
    @Override
    public Fluid tryDrainFluid(final IWorld world, final BlockPos pos, final BlockState state) {
        if (state.<Integer>get((Property<Integer>)FluidBlock.LEVEL) == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            return this.fluid;
        }
        return Fluids.EMPTY;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (this.fluid.matches(FluidTags.b)) {
            entity.aB();
        }
    }
    
    static {
        LEVEL = Properties.FLUID_BLOCK_LEVEL;
    }
}
