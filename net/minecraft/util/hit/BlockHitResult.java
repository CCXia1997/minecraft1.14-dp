package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockHitResult extends HitResult
{
    private final Direction side;
    private final BlockPos blockPos;
    private final boolean missed;
    private final boolean e;
    
    public static BlockHitResult createMissed(final Vec3d pos, final Direction side, final BlockPos blockPos) {
        return new BlockHitResult(true, pos, side, blockPos, false);
    }
    
    public BlockHitResult(final Vec3d pos, final Direction side, final BlockPos blockPos, final boolean boolean4) {
        this(false, pos, side, blockPos, boolean4);
    }
    
    private BlockHitResult(final boolean missed, final Vec3d pos, final Direction side, final BlockPos blockPos, final boolean boolean5) {
        super(pos);
        this.missed = missed;
        this.side = side;
        this.blockPos = blockPos;
        this.e = boolean5;
    }
    
    public BlockHitResult withSide(final Direction side) {
        return new BlockHitResult(this.missed, this.pos, side, this.blockPos, this.e);
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public Direction getSide() {
        return this.side;
    }
    
    @Override
    public Type getType() {
        return this.missed ? Type.NONE : Type.BLOCK;
    }
    
    public boolean d() {
        return this.e;
    }
}
