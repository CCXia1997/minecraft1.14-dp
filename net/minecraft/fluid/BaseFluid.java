package net.minecraft.fluid;

import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.DoorBlock;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.block.FluidFillable;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.state.StateFactory;
import com.google.common.collect.Maps;
import net.minecraft.util.shape.VoxelShape;
import java.util.Map;
import net.minecraft.block.Block;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.BooleanProperty;

public abstract class BaseFluid extends Fluid
{
    public static final BooleanProperty FALLING;
    public static final IntegerProperty LEVEL;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> e;
    private final Map<FluidState, VoxelShape> shapeCache;
    
    public BaseFluid() {
        this.shapeCache = Maps.newIdentityHashMap();
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Fluid, FluidState> builder) {
        builder.add(BaseFluid.FALLING);
    }
    
    public Vec3d getVelocity(final BlockView world, final BlockPos pos, final FluidState state) {
        double double4 = 0.0;
        double double5 = 0.0;
        try (final BlockPos.PooledMutable pooledMutable8 = BlockPos.PooledMutable.get()) {
            for (final Direction direction11 : Direction.Type.HORIZONTAL) {
                pooledMutable8.set((Vec3i)pos).setOffset(direction11);
                final FluidState fluidState12 = world.getFluidState(pooledMutable8);
                if (!this.f(fluidState12)) {
                    continue;
                }
                float float13 = fluidState12.getHeight(world, pooledMutable8);
                float float14 = 0.0f;
                if (float13 == 0.0f) {
                    if (!world.getBlockState(pooledMutable8).getMaterial().blocksMovement()) {
                        final BlockPos blockPos15 = pooledMutable8.down();
                        final FluidState fluidState13 = world.getFluidState(blockPos15);
                        if (this.f(fluidState13)) {
                            float13 = fluidState13.getHeight(world, blockPos15);
                            if (float13 > 0.0f) {
                                float14 = state.getHeight(world, pos) - (float13 - 0.8888889f);
                            }
                        }
                    }
                }
                else if (float13 > 0.0f) {
                    float14 = state.getHeight(world, pos) - float13;
                }
                if (float14 == 0.0f) {
                    continue;
                }
                double4 += direction11.getOffsetX() * float14;
                double5 += direction11.getOffsetZ() * float14;
            }
            Vec3d vec3d10 = new Vec3d(double4, 0.0, double5);
            if (state.<Boolean>get((Property<Boolean>)BaseFluid.FALLING)) {
                for (final Direction direction12 : Direction.Type.HORIZONTAL) {
                    pooledMutable8.set((Vec3i)pos).setOffset(direction12);
                    if (this.a(world, pooledMutable8, direction12) || this.a(world, pooledMutable8.up(), direction12)) {
                        vec3d10 = vec3d10.normalize().add(0.0, -6.0, 0.0);
                        break;
                    }
                }
            }
            return vec3d10.normalize();
        }
    }
    
    private boolean f(final FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.getFluid().matchesType(this);
    }
    
    protected boolean a(final BlockView blockView, final BlockPos blockPos, final Direction direction) {
        final BlockState blockState4 = blockView.getBlockState(blockPos);
        final FluidState fluidState5 = blockView.getFluidState(blockPos);
        return !fluidState5.getFluid().matchesType(this) && (direction == Direction.UP || (blockState4.getMaterial() != Material.ICE && Block.isSolidFullSquare(blockState4, blockView, blockPos, direction)));
    }
    
    protected void a(final IWorld world, final BlockPos pos, final FluidState state) {
        if (state.isEmpty()) {
            return;
        }
        final BlockState blockState4 = world.getBlockState(pos);
        final BlockPos blockPos5 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos5);
        final FluidState fluidState7 = this.getUpdatedState(world, blockPos5, blockState5);
        if (this.a(world, pos, blockState4, Direction.DOWN, blockPos5, blockState5, world.getFluidState(blockPos5), fluidState7.getFluid())) {
            this.flow(world, blockPos5, blockState5, Direction.DOWN, fluidState7);
            if (this.a(world, pos) >= 3) {
                this.a(world, pos, state, blockState4);
            }
        }
        else if (state.isStill() || !this.a(world, fluidState7.getFluid(), pos, blockState4, blockPos5, blockState5)) {
            this.a(world, pos, state, blockState4);
        }
    }
    
    private void a(final IWorld iWorld, final BlockPos blockPos, final FluidState fluidState, final BlockState blockState) {
        int integer5 = fluidState.getLevel() - this.getLevelDecreasePerBlock(iWorld);
        if (fluidState.<Boolean>get((Property<Boolean>)BaseFluid.FALLING)) {
            integer5 = 7;
        }
        if (integer5 <= 0) {
            return;
        }
        final Map<Direction, FluidState> map6 = this.b(iWorld, blockPos, blockState);
        for (final Map.Entry<Direction, FluidState> entry8 : map6.entrySet()) {
            final Direction direction9 = entry8.getKey();
            final FluidState fluidState2 = entry8.getValue();
            final BlockPos blockPos2 = blockPos.offset(direction9);
            final BlockState blockState2 = iWorld.getBlockState(blockPos2);
            if (this.a(iWorld, blockPos, blockState, direction9, blockPos2, blockState2, iWorld.getFluidState(blockPos2), fluidState2.getFluid())) {
                this.flow(iWorld, blockPos2, blockState2, direction9, fluidState2);
            }
        }
    }
    
    protected FluidState getUpdatedState(final ViewableWorld world, final BlockPos pos, final BlockState state) {
        int integer4 = 0;
        int integer5 = 0;
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos8 = pos.offset(direction7);
            final BlockState blockState9 = world.getBlockState(blockPos8);
            final FluidState fluidState10 = blockState9.getFluidState();
            if (fluidState10.getFluid().matchesType(this) && this.receivesFlow(direction7, world, pos, state, blockPos8, blockState9)) {
                if (fluidState10.isStill()) {
                    ++integer5;
                }
                integer4 = Math.max(integer4, fluidState10.getLevel());
            }
        }
        if (this.isInfinite() && integer5 >= 2) {
            final BlockState blockState10 = world.getBlockState(pos.down());
            final FluidState fluidState11 = blockState10.getFluidState();
            if (blockState10.getMaterial().isSolid() || this.g(fluidState11)) {
                return this.getStill(false);
            }
        }
        final BlockPos blockPos9 = pos.up();
        final BlockState blockState11 = world.getBlockState(blockPos9);
        final FluidState fluidState12 = blockState11.getFluidState();
        if (!fluidState12.isEmpty() && fluidState12.getFluid().matchesType(this) && this.receivesFlow(Direction.UP, world, pos, state, blockPos9, blockState11)) {
            return this.getFlowing(8, true);
        }
        final int integer6 = integer4 - this.getLevelDecreasePerBlock(world);
        if (integer6 <= 0) {
            return Fluids.EMPTY.getDefaultState();
        }
        return this.getFlowing(integer6, false);
    }
    
    private boolean receivesFlow(final Direction face, final BlockView world, final BlockPos pos, final BlockState state, final BlockPos fromPos, final BlockState fromState) {
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap7;
        if (state.getBlock().hasDynamicBounds() || fromState.getBlock().hasDynamicBounds()) {
            object2ByteLinkedOpenHashMap7 = null;
        }
        else {
            object2ByteLinkedOpenHashMap7 = BaseFluid.e.get();
        }
        Block.NeighborGroup neighborGroup8;
        if (object2ByteLinkedOpenHashMap7 != null) {
            neighborGroup8 = new Block.NeighborGroup(state, fromState, face);
            final byte byte9 = object2ByteLinkedOpenHashMap7.getAndMoveToFirst(neighborGroup8);
            if (byte9 != 127) {
                return byte9 != 0;
            }
        }
        else {
            neighborGroup8 = null;
        }
        final VoxelShape voxelShape9 = state.getCollisionShape(world, pos);
        final VoxelShape voxelShape10 = fromState.getCollisionShape(world, fromPos);
        final boolean boolean11 = !VoxelShapes.b(voxelShape9, voxelShape10, face);
        if (object2ByteLinkedOpenHashMap7 != null) {
            if (object2ByteLinkedOpenHashMap7.size() == 200) {
                object2ByteLinkedOpenHashMap7.removeLastByte();
            }
            object2ByteLinkedOpenHashMap7.putAndMoveToFirst(neighborGroup8, (byte)(byte)(boolean11 ? 1 : 0));
        }
        return boolean11;
    }
    
    public abstract Fluid getFlowing();
    
    public FluidState getFlowing(final int level, final boolean falling) {
        return this.getFlowing().getDefaultState().<Comparable, Integer>with((Property<Comparable>)BaseFluid.LEVEL, level).<Comparable, Boolean>with((Property<Comparable>)BaseFluid.FALLING, falling);
    }
    
    public abstract Fluid getStill();
    
    public FluidState getStill(final boolean falling) {
        return this.getStill().getDefaultState().<Comparable, Boolean>with((Property<Comparable>)BaseFluid.FALLING, falling);
    }
    
    protected abstract boolean isInfinite();
    
    protected void flow(final IWorld world, final BlockPos pos, final BlockState state, final Direction direction, final FluidState fluidState) {
        if (state.getBlock() instanceof FluidFillable) {
            ((FluidFillable)state.getBlock()).tryFillWithFluid(world, pos, state, fluidState);
        }
        else {
            if (!state.isAir()) {
                this.beforeBreakingBlock(world, pos, state);
            }
            world.setBlockState(pos, fluidState.getBlockState(), 3);
        }
    }
    
    protected abstract void beforeBreakingBlock(final IWorld arg1, final BlockPos arg2, final BlockState arg3);
    
    private static short a(final BlockPos blockPos1, final BlockPos blockPos2) {
        final int integer3 = blockPos2.getX() - blockPos1.getX();
        final int integer4 = blockPos2.getZ() - blockPos1.getZ();
        return (short)((integer3 + 128 & 0xFF) << 8 | (integer4 + 128 & 0xFF));
    }
    
    protected int a(final ViewableWorld viewableWorld, final BlockPos blockPos2, final int integer, final Direction direction, final BlockState blockState, final BlockPos blockPos6, final Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, final Short2BooleanMap short2BooleanMap) {
        int integer2 = 1000;
        for (final Direction direction2 : Direction.Type.HORIZONTAL) {
            if (direction2 == direction) {
                continue;
            }
            final BlockPos blockPos7 = blockPos2.offset(direction2);
            final short short13 = a(blockPos6, blockPos7);
            final BlockState blockState2;
            final Pair<BlockState, FluidState> pair14 = (Pair<BlockState, FluidState>)short2ObjectMap.computeIfAbsent(short13, integer -> {
                blockState2 = viewableWorld.getBlockState(blockPos7);
                return Pair.of(blockState2, blockState2.getFluidState());
            });
            final BlockState blockState3 = (BlockState)pair14.getFirst();
            final FluidState fluidState16 = (FluidState)pair14.getSecond();
            if (!this.a(viewableWorld, this.getFlowing(), blockPos2, blockState, direction2, blockPos7, blockState3, fluidState16)) {
                continue;
            }
            final BlockPos blockPos9;
            final BlockPos blockPos8;
            final BlockState blockState4;
            final BlockState blockState5;
            final boolean boolean17 = short2BooleanMap.computeIfAbsent(short13, integer -> {
                blockPos8 = blockPos9.down();
                blockState4 = viewableWorld.getBlockState(blockPos8);
                return this.a(viewableWorld, this.getFlowing(), blockPos9, blockState5, blockPos8, blockState4);
            });
            if (boolean17) {
                return integer;
            }
            if (integer >= this.b(viewableWorld)) {
                continue;
            }
            final int integer3 = this.a(viewableWorld, blockPos7, integer + 1, direction2.getOpposite(), blockState3, blockPos6, short2ObjectMap, short2BooleanMap);
            if (integer3 >= integer2) {
                continue;
            }
            integer2 = integer3;
        }
        return integer2;
    }
    
    private boolean a(final BlockView blockView, final Fluid fluid, final BlockPos blockPos3, final BlockState blockState4, final BlockPos blockPos5, final BlockState blockState6) {
        return this.receivesFlow(Direction.DOWN, blockView, blockPos3, blockState4, blockPos5, blockState6) && (blockState6.getFluidState().getFluid().matchesType(this) || this.a(blockView, blockPos5, blockState6, fluid));
    }
    
    private boolean a(final BlockView blockView, final Fluid fluid, final BlockPos blockPos3, final BlockState blockState4, final Direction direction, final BlockPos blockPos6, final BlockState blockState7, final FluidState fluidState) {
        return !this.g(fluidState) && this.receivesFlow(direction, blockView, blockPos3, blockState4, blockPos6, blockState7) && this.a(blockView, blockPos6, blockState7, fluid);
    }
    
    private boolean g(final FluidState fluidState) {
        return fluidState.getFluid().matchesType(this) && fluidState.isStill();
    }
    
    protected abstract int b(final ViewableWorld arg1);
    
    private int a(final ViewableWorld viewableWorld, final BlockPos blockPos) {
        int integer3 = 0;
        for (final Direction direction5 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos2 = blockPos.offset(direction5);
            final FluidState fluidState7 = viewableWorld.getFluidState(blockPos2);
            if (this.g(fluidState7)) {
                ++integer3;
            }
        }
        return integer3;
    }
    
    protected Map<Direction, FluidState> b(final ViewableWorld viewableWorld, final BlockPos blockPos, final BlockState blockState) {
        int integer4 = 1000;
        final Map<Direction, FluidState> map5 = Maps.newEnumMap(Direction.class);
        final Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap6 = (Short2ObjectMap<Pair<BlockState, FluidState>>)new Short2ObjectOpenHashMap();
        final Short2BooleanMap short2BooleanMap7 = (Short2BooleanMap)new Short2BooleanOpenHashMap();
        for (final Direction direction9 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos2 = blockPos.offset(direction9);
            final short short11 = a(blockPos, blockPos2);
            final BlockState blockState2;
            final Pair<BlockState, FluidState> pair12 = (Pair<BlockState, FluidState>)short2ObjectMap6.computeIfAbsent(short11, integer -> {
                blockState2 = viewableWorld.getBlockState(blockPos2);
                return Pair.of(blockState2, blockState2.getFluidState());
            });
            final BlockState blockState3 = (BlockState)pair12.getFirst();
            final FluidState fluidState14 = (FluidState)pair12.getSecond();
            final FluidState fluidState15 = this.getUpdatedState(viewableWorld, blockPos2, blockState3);
            if (this.a(viewableWorld, fluidState15.getFluid(), blockPos, blockState, direction9, blockPos2, blockState3, fluidState14)) {
                final BlockPos blockPos3 = blockPos2.down();
                final BlockPos blockPos4;
                final BlockState blockState4;
                final BlockPos blockPos5;
                final BlockState blockState5;
                final boolean boolean18 = short2BooleanMap7.computeIfAbsent(short11, integer -> {
                    blockState4 = viewableWorld.getBlockState(blockPos4);
                    return this.a(viewableWorld, this.getFlowing(), blockPos5, blockState5, blockPos4, blockState4);
                });
                int integer5;
                if (boolean18) {
                    integer5 = 0;
                }
                else {
                    integer5 = this.a(viewableWorld, blockPos2, 1, direction9.getOpposite(), blockState3, blockPos, short2ObjectMap6, short2BooleanMap7);
                }
                if (integer5 < integer4) {
                    map5.clear();
                }
                if (integer5 > integer4) {
                    continue;
                }
                map5.put(direction9, fluidState15);
                integer4 = integer5;
            }
        }
        return map5;
    }
    
    private boolean a(final BlockView blockView, final BlockPos blockPos, final BlockState blockState, final Fluid fluid) {
        final Block block5 = blockState.getBlock();
        if (block5 instanceof FluidFillable) {
            return ((FluidFillable)block5).canFillWithFluid(blockView, blockPos, blockState, fluid);
        }
        if (block5 instanceof DoorBlock || block5.matches(BlockTags.V) || block5 == Blocks.ce || block5 == Blocks.cF || block5 == Blocks.kU) {
            return false;
        }
        final Material material6 = blockState.getMaterial();
        return material6 != Material.PORTAL && material6 != Material.STRUCTURE_VOID && material6 != Material.UNDERWATER_PLANT && material6 != Material.SEAGRASS && !material6.blocksMovement();
    }
    
    protected boolean a(final BlockView blockView, final BlockPos blockPos2, final BlockState blockState3, final Direction direction, final BlockPos blockPos5, final BlockState blockState6, final FluidState fluidState, final Fluid fluid) {
        return fluidState.a(blockView, blockPos5, fluid, direction) && this.receivesFlow(direction, blockView, blockPos2, blockState3, blockPos5, blockState6) && this.a(blockView, blockPos5, blockState6, fluid);
    }
    
    protected abstract int getLevelDecreasePerBlock(final ViewableWorld arg1);
    
    protected int getNextTickDelay(final World world, final BlockPos pos, final FluidState oldState, final FluidState newState) {
        return this.getTickRate(world);
    }
    
    public void onScheduledTick(final World world, final BlockPos pos, FluidState state) {
        if (!state.isStill()) {
            final FluidState fluidState4 = this.getUpdatedState(world, pos, world.getBlockState(pos));
            final int integer5 = this.getNextTickDelay(world, pos, state, fluidState4);
            if (fluidState4.isEmpty()) {
                state = fluidState4;
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            else if (!fluidState4.equals(state)) {
                state = fluidState4;
                final BlockState blockState6 = state.getBlockState();
                world.setBlockState(pos, blockState6, 2);
                world.getFluidTickScheduler().schedule(pos, state.getFluid(), integer5);
                world.updateNeighborsAlways(pos, blockState6.getBlock());
            }
        }
        this.a(world, pos, state);
    }
    
    protected static int d(final FluidState fluidState) {
        if (fluidState.isStill()) {
            return 0;
        }
        return 8 - Math.min(fluidState.getLevel(), 8) + (fluidState.<Boolean>get((Property<Boolean>)BaseFluid.FALLING) ? 8 : 0);
    }
    
    private static boolean isFluidAboveEqual(final FluidState state, final BlockView view, final BlockPos pos) {
        return state.getFluid().matchesType(view.getFluidState(pos.up()).getFluid());
    }
    
    @Override
    public float getHeight(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos) {
        if (isFluidAboveEqual(fluidState, blockView, blockPos)) {
            return 1.0f;
        }
        return fluidState.getLevel() / 9.0f;
    }
    
    @Override
    public VoxelShape getShape(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos) {
        if (fluidState.getLevel() == 9 && isFluidAboveEqual(fluidState, blockView, blockPos)) {
            return VoxelShapes.fullCube();
        }
        return this.shapeCache.computeIfAbsent(fluidState, fluidState -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, fluidState.getHeight(blockView, blockPos), 1.0));
    }
    
    static {
        FALLING = Properties.FALLING;
        LEVEL = Properties.FLUID_LEVEL;
        final Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap1;
        e = ThreadLocal.<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>>withInitial(() -> {
            object2ByteLinkedOpenHashMap1 = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
                protected void rehash(final int integer) {
                }
            };
            object2ByteLinkedOpenHashMap1.defaultReturnValue((byte)127);
            return object2ByteLinkedOpenHashMap1;
        });
    }
}
