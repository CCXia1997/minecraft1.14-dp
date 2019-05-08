package net.minecraft.util;

import net.minecraft.util.math.Direction;

public enum BlockMirror
{
    NONE, 
    LEFT_RIGHT, 
    FRONT_BACK;
    
    public int mirror(final int rotation, final int fullTurn) {
        final int integer3 = fullTurn / 2;
        final int integer4 = (rotation > integer3) ? (rotation - fullTurn) : rotation;
        switch (this) {
            case FRONT_BACK: {
                return (fullTurn - integer4) % fullTurn;
            }
            case LEFT_RIGHT: {
                return (integer3 - integer4 + fullTurn) % fullTurn;
            }
            default: {
                return rotation;
            }
        }
    }
    
    public BlockRotation getRotation(final Direction direction) {
        final Direction.Axis axis2 = direction.getAxis();
        return ((this == BlockMirror.LEFT_RIGHT && axis2 == Direction.Axis.Z) || (this == BlockMirror.FRONT_BACK && axis2 == Direction.Axis.X)) ? BlockRotation.ROT_180 : BlockRotation.ROT_0;
    }
    
    public Direction apply(final Direction direction) {
        if (this == BlockMirror.FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
            return direction.getOpposite();
        }
        if (this == BlockMirror.LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z) {
            return direction.getOpposite();
        }
        return direction;
    }
}
