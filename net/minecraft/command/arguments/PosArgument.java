package net.minecraft.command.arguments;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.ServerCommandSource;

public interface PosArgument
{
    Vec3d toAbsolutePos(final ServerCommandSource arg1);
    
    Vec2f toAbsoluteRotation(final ServerCommandSource arg1);
    
    default BlockPos toAbsoluteBlockPos(final ServerCommandSource source) {
        return new BlockPos(this.toAbsolutePos(source));
    }
    
    boolean isXRelative();
    
    boolean isYRelative();
    
    boolean isZRelative();
}
