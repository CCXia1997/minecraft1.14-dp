package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BoundingBox;
import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.EnumProperty;

public class DetectorRailBlock extends AbstractRailBlock
{
    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;
    
    public DetectorRailBlock(final Settings settings) {
        super(true, settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)DetectorRailBlock.POWERED, false)).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.a));
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 20;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return;
        }
        this.a(world, pos, state);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient || !state.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return;
        }
        this.a(world, pos, state);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (!state.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return 0;
        }
        return (facing == Direction.UP) ? 15 : 0;
    }
    
    private void a(final World world, final BlockPos blockPos, final BlockState blockState) {
        final boolean boolean4 = blockState.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED);
        boolean boolean5 = false;
        final List<AbstractMinecartEntity> list6 = this.<AbstractMinecartEntity>a(world, blockPos, AbstractMinecartEntity.class, null);
        if (!list6.isEmpty()) {
            boolean5 = true;
        }
        if (boolean5 && !boolean4) {
            world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)DetectorRailBlock.POWERED, true), 3);
            this.b(world, blockPos, blockState, true);
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.down(), this);
            world.scheduleBlockRender(blockPos);
        }
        if (!boolean5 && boolean4) {
            world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Boolean>with((Property<Comparable>)DetectorRailBlock.POWERED, false), 3);
            this.b(world, blockPos, blockState, false);
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.down(), this);
            world.scheduleBlockRender(blockPos);
        }
        if (boolean5) {
            world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
        }
        world.updateHorizontalAdjacent(blockPos, this);
    }
    
    protected void b(final World world, final BlockPos blockPos, final BlockState blockState, final boolean boolean4) {
        final RailPlacementHelper railPlacementHelper5 = new RailPlacementHelper(world, blockPos, blockState);
        final List<BlockPos> list6 = railPlacementHelper5.getNeighbors();
        for (final BlockPos blockPos2 : list6) {
            final BlockState blockState2 = world.getBlockState(blockPos2);
            blockState2.neighborUpdate(world, blockPos2, blockState2.getBlock(), blockPos, false);
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        super.onBlockAdded(state, world, pos, oldState, boolean5);
        this.a(world, pos, state);
    }
    
    @Override
    public Property<RailShape> getShapeProperty() {
        return DetectorRailBlock.SHAPE;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        if (state.<Boolean>get((Property<Boolean>)DetectorRailBlock.POWERED)) {
            final List<CommandBlockMinecartEntity> list4 = this.<CommandBlockMinecartEntity>a(world, pos, CommandBlockMinecartEntity.class, null);
            if (!list4.isEmpty()) {
                return list4.get(0).getCommandExecutor().getSuccessCount();
            }
            final List<AbstractMinecartEntity> list5 = this.<AbstractMinecartEntity>a(world, pos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
            if (!list5.isEmpty()) {
                return Container.calculateComparatorOutput((Inventory)list5.get(0));
            }
        }
        return 0;
    }
    
    protected <T extends AbstractMinecartEntity> List<T> a(final World world, final BlockPos blockPos, final Class<T> class3, @Nullable final Predicate<Entity> predicate) {
        return world.<T>getEntities(class3, this.a(blockPos), predicate);
    }
    
    private BoundingBox a(final BlockPos blockPos) {
        final float float2 = 0.2f;
        return new BoundingBox(blockPos.getX() + 0.2f, blockPos.getY(), blockPos.getZ() + 0.2f, blockPos.getX() + 1 - 0.2f, blockPos.getY() + 1 - 0.2f, blockPos.getZ() + 1 - 0.2f);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        Label_0424: {
            switch (rotation) {
                case ROT_180: {
                    switch (state.<RailShape>get(DetectorRailBlock.SHAPE)) {
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.c);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.i);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.j);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.g);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.h);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_270: {
                    switch (state.<RailShape>get(DetectorRailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.e);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.f);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.d);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.i);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_90: {
                    switch (state.<RailShape>get(DetectorRailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.f);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.e);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.c);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.d);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.g);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
            }
        }
        return state;
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        final RailShape railShape3 = state.<RailShape>get(DetectorRailBlock.SHAPE);
        Label_0319: {
            switch (mirror) {
                case LEFT_RIGHT: {
                    switch (railShape3) {
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.g);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    switch (railShape3) {
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(DetectorRailBlock.SHAPE, RailShape.i);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(state, mirror);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(DetectorRailBlock.SHAPE, DetectorRailBlock.POWERED);
    }
    
    static {
        SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
        POWERED = Properties.POWERED;
    }
}
