package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

public class AutomaticItemPlacementContext extends ItemPlacementContext
{
    private final Direction direction;
    
    public AutomaticItemPlacementContext(final World world, final BlockPos blockPos, final Direction direction3, final ItemStack itemStack, final Direction direction5) {
        super(world, null, Hand.a, itemStack, new BlockHitResult(new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5), direction5, blockPos, false));
        this.direction = direction3;
    }
    
    @Override
    public BlockPos getBlockPos() {
        return this.hitResult.getBlockPos();
    }
    
    @Override
    public boolean canPlace() {
        return this.world.getBlockState(this.hitResult.getBlockPos()).canReplace(this);
    }
    
    @Override
    public boolean c() {
        return this.canPlace();
    }
    
    @Override
    public Direction getPlayerFacing() {
        return Direction.DOWN;
    }
    
    @Override
    public Direction[] getPlacementFacings() {
        switch (this.direction) {
            default: {
                return new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP };
            }
            case UP: {
                return new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
            }
            case NORTH: {
                return new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH };
            }
            case SOUTH: {
                return new Direction[] { Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH };
            }
            case WEST: {
                return new Direction[] { Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST };
            }
            case EAST: {
                return new Direction[] { Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST };
            }
        }
    }
    
    @Override
    public Direction getPlayerHorizontalFacing() {
        return (this.direction.getAxis() == Direction.Axis.Y) ? Direction.NORTH : this.direction;
    }
    
    @Override
    public boolean isPlayerSneaking() {
        return false;
    }
    
    @Override
    public float getPlayerYaw() {
        return (float)(this.direction.getHorizontal() * 90);
    }
}
