package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import net.minecraft.util.math.Direction;
import javax.annotation.Nullable;
import net.minecraft.block.enums.RailShape;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailPlacementHelper
{
    private final World world;
    private final BlockPos pos;
    private final AbstractRailBlock block;
    private BlockState state;
    private final boolean allowCurves;
    private final List<BlockPos> neighbors;
    
    public RailPlacementHelper(final World world, final BlockPos blockPos, final BlockState blockState) {
        this.neighbors = Lists.newArrayList();
        this.world = world;
        this.pos = blockPos;
        this.state = blockState;
        this.block = (AbstractRailBlock)blockState.getBlock();
        final RailShape railShape4 = blockState.<RailShape>get(this.block.getShapeProperty());
        this.allowCurves = this.block.canMakeCurves();
        this.computeNeighbors(railShape4);
    }
    
    public List<BlockPos> getNeighbors() {
        return this.neighbors;
    }
    
    private void computeNeighbors(final RailShape shape) {
        this.neighbors.clear();
        switch (shape) {
            case a: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south());
                break;
            }
            case b: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east());
                break;
            }
            case c: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east().up());
                break;
            }
            case d: {
                this.neighbors.add(this.pos.west().up());
                this.neighbors.add(this.pos.east());
                break;
            }
            case e: {
                this.neighbors.add(this.pos.north().up());
                this.neighbors.add(this.pos.south());
                break;
            }
            case f: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south().up());
                break;
            }
            case g: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.south());
                break;
            }
            case h: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.south());
                break;
            }
            case i: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.north());
                break;
            }
            case j: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.north());
                break;
            }
        }
    }
    
    private void d() {
        for (int integer1 = 0; integer1 < this.neighbors.size(); ++integer1) {
            final RailPlacementHelper railPlacementHelper2 = this.b(this.neighbors.get(integer1));
            if (railPlacementHelper2 == null || !railPlacementHelper2.a(this)) {
                this.neighbors.remove(integer1--);
            }
            else {
                this.neighbors.set(integer1, railPlacementHelper2.pos);
            }
        }
    }
    
    private boolean a(final BlockPos blockPos) {
        return AbstractRailBlock.isRail(this.world, blockPos) || AbstractRailBlock.isRail(this.world, blockPos.up()) || AbstractRailBlock.isRail(this.world, blockPos.down());
    }
    
    @Nullable
    private RailPlacementHelper b(final BlockPos blockPos) {
        BlockPos blockPos2 = blockPos;
        BlockState blockState3 = this.world.getBlockState(blockPos2);
        if (AbstractRailBlock.isRail(blockState3)) {
            return new RailPlacementHelper(this.world, blockPos2, blockState3);
        }
        blockPos2 = blockPos.up();
        blockState3 = this.world.getBlockState(blockPos2);
        if (AbstractRailBlock.isRail(blockState3)) {
            return new RailPlacementHelper(this.world, blockPos2, blockState3);
        }
        blockPos2 = blockPos.down();
        blockState3 = this.world.getBlockState(blockPos2);
        if (AbstractRailBlock.isRail(blockState3)) {
            return new RailPlacementHelper(this.world, blockPos2, blockState3);
        }
        return null;
    }
    
    private boolean a(final RailPlacementHelper railPlacementHelper) {
        return this.c(railPlacementHelper.pos);
    }
    
    private boolean c(final BlockPos blockPos) {
        for (int integer2 = 0; integer2 < this.neighbors.size(); ++integer2) {
            final BlockPos blockPos2 = this.neighbors.get(integer2);
            if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
                return true;
            }
        }
        return false;
    }
    
    protected int b() {
        int integer1 = 0;
        for (final Direction direction3 : Direction.Type.HORIZONTAL) {
            if (this.a(this.pos.offset(direction3))) {
                ++integer1;
            }
        }
        return integer1;
    }
    
    private boolean b(final RailPlacementHelper railPlacementHelper) {
        return this.a(railPlacementHelper) || this.neighbors.size() != 2;
    }
    
    private void c(final RailPlacementHelper railPlacementHelper) {
        this.neighbors.add(railPlacementHelper.pos);
        final BlockPos blockPos2 = this.pos.north();
        final BlockPos blockPos3 = this.pos.south();
        final BlockPos blockPos4 = this.pos.west();
        final BlockPos blockPos5 = this.pos.east();
        final boolean boolean6 = this.c(blockPos2);
        final boolean boolean7 = this.c(blockPos3);
        final boolean boolean8 = this.c(blockPos4);
        final boolean boolean9 = this.c(blockPos5);
        RailShape railShape10 = null;
        if (boolean6 || boolean7) {
            railShape10 = RailShape.a;
        }
        if (boolean8 || boolean9) {
            railShape10 = RailShape.b;
        }
        if (!this.allowCurves) {
            if (boolean7 && boolean9 && !boolean6 && !boolean8) {
                railShape10 = RailShape.g;
            }
            if (boolean7 && boolean8 && !boolean6 && !boolean9) {
                railShape10 = RailShape.h;
            }
            if (boolean6 && boolean8 && !boolean7 && !boolean9) {
                railShape10 = RailShape.i;
            }
            if (boolean6 && boolean9 && !boolean7 && !boolean8) {
                railShape10 = RailShape.j;
            }
        }
        if (railShape10 == RailShape.a) {
            if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
                railShape10 = RailShape.e;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
                railShape10 = RailShape.f;
            }
        }
        if (railShape10 == RailShape.b) {
            if (AbstractRailBlock.isRail(this.world, blockPos5.up())) {
                railShape10 = RailShape.c;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
                railShape10 = RailShape.d;
            }
        }
        if (railShape10 == null) {
            railShape10 = RailShape.a;
        }
        this.state = ((AbstractPropertyContainer<O, BlockState>)this.state).<RailShape, RailShape>with(this.block.getShapeProperty(), railShape10);
        this.world.setBlockState(this.pos, this.state, 3);
    }
    
    private boolean d(final BlockPos blockPos) {
        final RailPlacementHelper railPlacementHelper2 = this.b(blockPos);
        if (railPlacementHelper2 == null) {
            return false;
        }
        railPlacementHelper2.d();
        return railPlacementHelper2.b(this);
    }
    
    public RailPlacementHelper updateBlockState(final boolean powered, final boolean forceUpdate) {
        final BlockPos blockPos3 = this.pos.north();
        final BlockPos blockPos4 = this.pos.south();
        final BlockPos blockPos5 = this.pos.west();
        final BlockPos blockPos6 = this.pos.east();
        final boolean boolean7 = this.d(blockPos3);
        final boolean boolean8 = this.d(blockPos4);
        final boolean boolean9 = this.d(blockPos5);
        final boolean boolean10 = this.d(blockPos6);
        RailShape railShape11 = null;
        if ((boolean7 || boolean8) && !boolean9 && !boolean10) {
            railShape11 = RailShape.a;
        }
        if ((boolean9 || boolean10) && !boolean7 && !boolean8) {
            railShape11 = RailShape.b;
        }
        if (!this.allowCurves) {
            if (boolean8 && boolean10 && !boolean7 && !boolean9) {
                railShape11 = RailShape.g;
            }
            if (boolean8 && boolean9 && !boolean7 && !boolean10) {
                railShape11 = RailShape.h;
            }
            if (boolean7 && boolean9 && !boolean8 && !boolean10) {
                railShape11 = RailShape.i;
            }
            if (boolean7 && boolean10 && !boolean8 && !boolean9) {
                railShape11 = RailShape.j;
            }
        }
        if (railShape11 == null) {
            if (boolean7 || boolean8) {
                railShape11 = RailShape.a;
            }
            if (boolean9 || boolean10) {
                railShape11 = RailShape.b;
            }
            if (!this.allowCurves) {
                if (powered) {
                    if (boolean8 && boolean10) {
                        railShape11 = RailShape.g;
                    }
                    if (boolean9 && boolean8) {
                        railShape11 = RailShape.h;
                    }
                    if (boolean10 && boolean7) {
                        railShape11 = RailShape.j;
                    }
                    if (boolean7 && boolean9) {
                        railShape11 = RailShape.i;
                    }
                }
                else {
                    if (boolean7 && boolean9) {
                        railShape11 = RailShape.i;
                    }
                    if (boolean10 && boolean7) {
                        railShape11 = RailShape.j;
                    }
                    if (boolean9 && boolean8) {
                        railShape11 = RailShape.h;
                    }
                    if (boolean8 && boolean10) {
                        railShape11 = RailShape.g;
                    }
                }
            }
        }
        if (railShape11 == RailShape.a) {
            if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
                railShape11 = RailShape.e;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
                railShape11 = RailShape.f;
            }
        }
        if (railShape11 == RailShape.b) {
            if (AbstractRailBlock.isRail(this.world, blockPos6.up())) {
                railShape11 = RailShape.c;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos5.up())) {
                railShape11 = RailShape.d;
            }
        }
        if (railShape11 == null) {
            railShape11 = RailShape.a;
        }
        this.computeNeighbors(railShape11);
        this.state = ((AbstractPropertyContainer<O, BlockState>)this.state).<RailShape, RailShape>with(this.block.getShapeProperty(), railShape11);
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);
            for (int integer12 = 0; integer12 < this.neighbors.size(); ++integer12) {
                final RailPlacementHelper railPlacementHelper13 = this.b(this.neighbors.get(integer12));
                if (railPlacementHelper13 != null) {
                    railPlacementHelper13.d();
                    if (railPlacementHelper13.b(this)) {
                        railPlacementHelper13.c(this);
                    }
                }
            }
        }
        return this;
    }
    
    public BlockState getBlockState() {
        return this.state;
    }
}
