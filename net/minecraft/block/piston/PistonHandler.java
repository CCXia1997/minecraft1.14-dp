package net.minecraft.block.piston;

import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonHandler
{
    private final World world;
    private final BlockPos posFrom;
    private final boolean c;
    private final BlockPos posTo;
    private final Direction direction;
    private final List<BlockPos> movedBlocks;
    private final List<BlockPos> brokenBlocks;
    private final Direction h;
    
    public PistonHandler(final World world, final BlockPos pos, final Direction dir, final boolean boolean4) {
        this.movedBlocks = Lists.newArrayList();
        this.brokenBlocks = Lists.newArrayList();
        this.world = world;
        this.posFrom = pos;
        this.h = dir;
        this.c = boolean4;
        if (boolean4) {
            this.direction = dir;
            this.posTo = pos.offset(dir);
        }
        else {
            this.direction = dir.getOpposite();
            this.posTo = pos.offset(dir, 2);
        }
    }
    
    public boolean calculatePush() {
        this.movedBlocks.clear();
        this.brokenBlocks.clear();
        final BlockState blockState1 = this.world.getBlockState(this.posTo);
        if (!PistonBlock.isMovable(blockState1, this.world, this.posTo, this.direction, false, this.h)) {
            if (this.c && blockState1.getPistonBehavior() == PistonBehavior.b) {
                this.brokenBlocks.add(this.posTo);
                return true;
            }
            return false;
        }
        else {
            if (!this.tryMove(this.posTo, this.direction)) {
                return false;
            }
            for (int integer2 = 0; integer2 < this.movedBlocks.size(); ++integer2) {
                final BlockPos blockPos3 = this.movedBlocks.get(integer2);
                if (this.world.getBlockState(blockPos3).getBlock() == Blocks.gf && !this.a(blockPos3)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private boolean tryMove(final BlockPos blockPos, final Direction direction) {
        BlockState blockState3 = this.world.getBlockState(blockPos);
        Block block4 = blockState3.getBlock();
        if (blockState3.isAir()) {
            return true;
        }
        if (!PistonBlock.isMovable(blockState3, this.world, blockPos, this.direction, false, direction)) {
            return true;
        }
        if (blockPos.equals(this.posFrom)) {
            return true;
        }
        if (this.movedBlocks.contains(blockPos)) {
            return true;
        }
        int integer5 = 1;
        if (integer5 + this.movedBlocks.size() > 12) {
            return false;
        }
        while (block4 == Blocks.gf) {
            final BlockPos blockPos2 = blockPos.offset(this.direction.getOpposite(), integer5);
            blockState3 = this.world.getBlockState(blockPos2);
            block4 = blockState3.getBlock();
            if (blockState3.isAir() || !PistonBlock.isMovable(blockState3, this.world, blockPos2, this.direction, false, this.direction.getOpposite())) {
                break;
            }
            if (blockPos2.equals(this.posFrom)) {
                break;
            }
            if (++integer5 + this.movedBlocks.size() > 12) {
                return false;
            }
        }
        int integer6 = 0;
        for (int integer7 = integer5 - 1; integer7 >= 0; --integer7) {
            this.movedBlocks.add(blockPos.offset(this.direction.getOpposite(), integer7));
            ++integer6;
        }
        int integer7 = 1;
        while (true) {
            final BlockPos blockPos3 = blockPos.offset(this.direction, integer7);
            final int integer8 = this.movedBlocks.indexOf(blockPos3);
            if (integer8 > -1) {
                this.a(integer6, integer8);
                for (int integer9 = 0; integer9 <= integer8 + integer6; ++integer9) {
                    final BlockPos blockPos4 = this.movedBlocks.get(integer9);
                    if (this.world.getBlockState(blockPos4).getBlock() == Blocks.gf && !this.a(blockPos4)) {
                        return false;
                    }
                }
                return true;
            }
            blockState3 = this.world.getBlockState(blockPos3);
            if (blockState3.isAir()) {
                return true;
            }
            if (!PistonBlock.isMovable(blockState3, this.world, blockPos3, this.direction, true, this.direction) || blockPos3.equals(this.posFrom)) {
                return false;
            }
            if (blockState3.getPistonBehavior() == PistonBehavior.b) {
                this.brokenBlocks.add(blockPos3);
                return true;
            }
            if (this.movedBlocks.size() >= 12) {
                return false;
            }
            this.movedBlocks.add(blockPos3);
            ++integer6;
            ++integer7;
        }
    }
    
    private void a(final int integer1, final int integer2) {
        final List<BlockPos> list3 = Lists.newArrayList();
        final List<BlockPos> list4 = Lists.newArrayList();
        final List<BlockPos> list5 = Lists.newArrayList();
        list3.addAll(this.movedBlocks.subList(0, integer2));
        list4.addAll(this.movedBlocks.subList(this.movedBlocks.size() - integer1, this.movedBlocks.size()));
        list5.addAll(this.movedBlocks.subList(integer2, this.movedBlocks.size() - integer1));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list3);
        this.movedBlocks.addAll(list4);
        this.movedBlocks.addAll(list5);
    }
    
    private boolean a(final BlockPos blockPos) {
        for (final Direction direction5 : Direction.values()) {
            if (direction5.getAxis() != this.direction.getAxis() && !this.tryMove(blockPos.offset(direction5), direction5)) {
                return false;
            }
        }
        return true;
    }
    
    public List<BlockPos> getMovedBlocks() {
        return this.movedBlocks;
    }
    
    public List<BlockPos> getBrokenBlocks() {
        return this.brokenBlocks;
    }
}
