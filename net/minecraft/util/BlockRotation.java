package net.minecraft.util;

import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum BlockRotation
{
    ROT_0, 
    ROT_90, 
    ROT_180, 
    ROT_270;
    
    public BlockRotation rotate(final BlockRotation rotation) {
        Label_0148: {
            switch (rotation) {
                case ROT_180: {
                    switch (this) {
                        case ROT_0: {
                            return BlockRotation.ROT_180;
                        }
                        case ROT_90: {
                            return BlockRotation.ROT_270;
                        }
                        case ROT_180: {
                            return BlockRotation.ROT_0;
                        }
                        case ROT_270: {
                            return BlockRotation.ROT_90;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
                case ROT_270: {
                    switch (this) {
                        case ROT_0: {
                            return BlockRotation.ROT_270;
                        }
                        case ROT_90: {
                            return BlockRotation.ROT_0;
                        }
                        case ROT_180: {
                            return BlockRotation.ROT_90;
                        }
                        case ROT_270: {
                            return BlockRotation.ROT_180;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
                case ROT_90: {
                    switch (this) {
                        case ROT_0: {
                            return BlockRotation.ROT_90;
                        }
                        case ROT_90: {
                            return BlockRotation.ROT_180;
                        }
                        case ROT_180: {
                            return BlockRotation.ROT_270;
                        }
                        case ROT_270: {
                            return BlockRotation.ROT_0;
                        }
                        default: {
                            break Label_0148;
                        }
                    }
                    break;
                }
            }
        }
        return this;
    }
    
    public Direction rotate(final Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction;
        }
        switch (this) {
            case ROT_180: {
                return direction.getOpposite();
            }
            case ROT_270: {
                return direction.rotateYCounterclockwise();
            }
            case ROT_90: {
                return direction.rotateYClockwise();
            }
            default: {
                return direction;
            }
        }
    }
    
    public int rotate(final int rotation, final int fullTurn) {
        switch (this) {
            case ROT_180: {
                return (rotation + fullTurn / 2) % fullTurn;
            }
            case ROT_270: {
                return (rotation + fullTurn * 3 / 4) % fullTurn;
            }
            case ROT_90: {
                return (rotation + fullTurn / 4) % fullTurn;
            }
            default: {
                return rotation;
            }
        }
    }
    
    public static BlockRotation random(final Random random) {
        final BlockRotation[] arr2 = values();
        return arr2[random.nextInt(arr2.length)];
    }
    
    public static List<BlockRotation> randomRotationOrder(final Random random) {
        final List<BlockRotation> list2 = Lists.<BlockRotation>newArrayList(values());
        Collections.shuffle(list2, random);
        return list2;
    }
}
