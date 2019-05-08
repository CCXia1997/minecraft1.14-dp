package net.minecraft.command.arguments;

import java.util.Objects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.ServerCommandSource;

public class LookingPosArgument implements PosArgument
{
    private final double x;
    private final double y;
    private final double z;
    
    public LookingPosArgument(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public Vec3d toAbsolutePos(final ServerCommandSource source) {
        final Vec2f vec2f2 = source.getRotation();
        final Vec3d vec3d3 = source.getEntityAnchor().positionAt(source);
        final float float4 = MathHelper.cos((vec2f2.y + 90.0f) * 0.017453292f);
        final float float5 = MathHelper.sin((vec2f2.y + 90.0f) * 0.017453292f);
        final float float6 = MathHelper.cos(-vec2f2.x * 0.017453292f);
        final float float7 = MathHelper.sin(-vec2f2.x * 0.017453292f);
        final float float8 = MathHelper.cos((-vec2f2.x + 90.0f) * 0.017453292f);
        final float float9 = MathHelper.sin((-vec2f2.x + 90.0f) * 0.017453292f);
        final Vec3d vec3d4 = new Vec3d(float4 * float6, float7, float5 * float6);
        final Vec3d vec3d5 = new Vec3d(float4 * float8, float9, float5 * float8);
        final Vec3d vec3d6 = vec3d4.crossProduct(vec3d5).multiply(-1.0);
        final double double13 = vec3d4.x * this.z + vec3d5.x * this.y + vec3d6.x * this.x;
        final double double14 = vec3d4.y * this.z + vec3d5.y * this.y + vec3d6.y * this.x;
        final double double15 = vec3d4.z * this.z + vec3d5.z * this.y + vec3d6.z * this.x;
        return new Vec3d(vec3d3.x + double13, vec3d3.y + double14, vec3d3.z + double15);
    }
    
    @Override
    public Vec2f toAbsoluteRotation(final ServerCommandSource source) {
        return Vec2f.ZERO;
    }
    
    @Override
    public boolean isXRelative() {
        return true;
    }
    
    @Override
    public boolean isYRelative() {
        return true;
    }
    
    @Override
    public boolean isZRelative() {
        return true;
    }
    
    public static LookingPosArgument parse(final StringReader reader) throws CommandSyntaxException {
        final int integer2 = reader.getCursor();
        final double double3 = readCoordinate(reader, integer2);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer2);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final double double4 = readCoordinate(reader, integer2);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer2);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final double double5 = readCoordinate(reader, integer2);
        return new LookingPosArgument(double3, double4, double5);
    }
    
    private static double readCoordinate(final StringReader reader, final int startingCursorPos) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_COORDINATE.createWithContext((ImmutableStringReader)reader);
        }
        if (reader.peek() != '^') {
            reader.setCursor(startingCursorPos);
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        return (reader.canRead() && reader.peek() != ' ') ? reader.readDouble() : 0.0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LookingPosArgument)) {
            return false;
        }
        final LookingPosArgument lookingPosArgument2 = (LookingPosArgument)o;
        return this.x == lookingPosArgument2.x && this.y == lookingPosArgument2.y && this.z == lookingPosArgument2.z;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
}
