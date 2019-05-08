package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.shape.VoxelShape;

public class BambooBlock extends Block implements Fertilizable
{
    protected static final VoxelShape SMALL_LEAVES_SHAPE;
    protected static final VoxelShape LARGE_LEAVES_SHAPE;
    protected static final VoxelShape NO_LEAVES_SHAPE;
    public static final IntegerProperty AGE;
    public static final EnumProperty<BambooLeaves> LEAVES;
    public static final IntegerProperty STAGE;
    
    public BambooBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)BambooBlock.AGE, 0)).with(BambooBlock.LEAVES, BambooLeaves.a)).<Comparable, Integer>with((Property<Comparable>)BambooBlock.STAGE, 0));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BambooBlock.AGE, BambooBlock.LEAVES, BambooBlock.STAGE);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final VoxelShape voxelShape5 = (state.<BambooLeaves>get(BambooBlock.LEAVES) == BambooLeaves.c) ? BambooBlock.LARGE_LEAVES_SHAPE : BambooBlock.SMALL_LEAVES_SHAPE;
        final Vec3d vec3d6 = state.getOffsetPos(view, pos);
        return voxelShape5.offset(vec3d6.x, vec3d6.y, vec3d6.z);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        final Vec3d vec3d5 = state.getOffsetPos(view, pos);
        return BambooBlock.NO_LEAVES_SHAPE.offset(vec3d5.x, vec3d5.y, vec3d5.z);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (!fluidState2.isEmpty()) {
            return null;
        }
        final BlockState blockState3 = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        if (!blockState3.matches(BlockTags.R)) {
            return null;
        }
        final Block block4 = blockState3.getBlock();
        if (block4 == Blocks.kP) {
            return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)BambooBlock.AGE, 0);
        }
        if (block4 == Blocks.kQ) {
            final int integer5 = (blockState3.<Integer>get((Property<Integer>)BambooBlock.AGE) > 0) ? 1 : 0;
            return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)BambooBlock.AGE, integer5);
        }
        return Blocks.kP.getDefaultState();
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }
        if (state.<Integer>get((Property<Integer>)BambooBlock.STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getLightLevel(pos.up(), 0) >= 9) {
            final int integer5 = this.countBambooBelow(world, pos) + 1;
            if (integer5 < 16) {
                this.updateLeaves(state, world, pos, random, integer5);
            }
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.down()).matches(BlockTags.R);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (facing == Direction.UP && neighborState.getBlock() == Blocks.kQ && neighborState.<Integer>get((Property<Integer>)BambooBlock.AGE) > state.<Integer>get((Property<Integer>)BambooBlock.AGE)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)BambooBlock.AGE), 2);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        final int integer5 = this.countBambooAbove(world, pos);
        final int integer6 = this.countBambooBelow(world, pos);
        return integer5 + integer6 + 1 < 16 && world.getBlockState(pos.up(integer5)).<Integer>get((Property<Integer>)BambooBlock.STAGE) != 1;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        int integer5 = this.countBambooAbove(world, pos);
        final int integer6 = this.countBambooBelow(world, pos);
        int integer7 = integer5 + integer6 + 1;
        for (int integer8 = 1 + random.nextInt(2), integer9 = 0; integer9 < integer8; ++integer9) {
            final BlockPos blockPos10 = pos.up(integer5);
            final BlockState blockState11 = world.getBlockState(blockPos10);
            if (integer7 >= 16 || blockState11.<Integer>get((Property<Integer>)BambooBlock.STAGE) == 1 || !world.isAir(blockPos10.up())) {
                return;
            }
            this.updateLeaves(blockState11, world, blockPos10, random, integer7);
            ++integer5;
            ++integer7;
        }
    }
    
    @Override
    public float calcBlockBreakingDelta(final BlockState state, final PlayerEntity player, final BlockView world, final BlockPos pos) {
        if (player.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    protected void updateLeaves(final BlockState blockState, final World world, final BlockPos blockPos, final Random random, final int integer) {
        final BlockState blockState2 = world.getBlockState(blockPos.down());
        final BlockPos blockPos2 = blockPos.down(2);
        final BlockState blockState3 = world.getBlockState(blockPos2);
        BambooLeaves bambooLeaves9 = BambooLeaves.a;
        if (integer >= 1) {
            if (blockState2.getBlock() != Blocks.kQ || blockState2.<BambooLeaves>get(BambooBlock.LEAVES) == BambooLeaves.a) {
                bambooLeaves9 = BambooLeaves.b;
            }
            else if (blockState2.getBlock() == Blocks.kQ && blockState2.<BambooLeaves>get(BambooBlock.LEAVES) != BambooLeaves.a) {
                bambooLeaves9 = BambooLeaves.c;
                if (blockState3.getBlock() == Blocks.kQ) {
                    world.setBlockState(blockPos.down(), ((AbstractPropertyContainer<O, BlockState>)blockState2).<BambooLeaves, BambooLeaves>with(BambooBlock.LEAVES, BambooLeaves.b), 3);
                    world.setBlockState(blockPos2, ((AbstractPropertyContainer<O, BlockState>)blockState3).<BambooLeaves, BambooLeaves>with(BambooBlock.LEAVES, BambooLeaves.a), 3);
                }
            }
        }
        final int integer2 = (blockState.<Integer>get((Property<Integer>)BambooBlock.AGE) == 1 || blockState3.getBlock() == Blocks.kQ) ? 1 : 0;
        final int integer3 = ((integer >= 11 && random.nextFloat() < 0.25f) || integer == 15) ? 1 : 0;
        world.setBlockState(blockPos.up(), ((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)BambooBlock.AGE, integer2)).with(BambooBlock.LEAVES, bambooLeaves9)).<Comparable, Integer>with((Property<Comparable>)BambooBlock.STAGE, integer3), 3);
    }
    
    protected int countBambooAbove(final BlockView world, final BlockPos pos) {
        int integer3;
        for (integer3 = 0; integer3 < 16 && world.getBlockState(pos.up(integer3 + 1)).getBlock() == Blocks.kQ; ++integer3) {}
        return integer3;
    }
    
    protected int countBambooBelow(final BlockView world, final BlockPos pos) {
        int integer3;
        for (integer3 = 0; integer3 < 16 && world.getBlockState(pos.down(integer3 + 1)).getBlock() == Blocks.kQ; ++integer3) {}
        return integer3;
    }
    
    static {
        SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
        LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
        AGE = Properties.AGE_1;
        LEAVES = Properties.BAMBOO_LEAVES;
        STAGE = Properties.SAPLING_STAGE;
    }
}
