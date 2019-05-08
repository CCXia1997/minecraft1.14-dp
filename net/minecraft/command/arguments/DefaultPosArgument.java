package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.ServerCommandSource;

public class DefaultPosArgument implements PosArgument
{
    private final CoordinateArgument x;
    private final CoordinateArgument y;
    private final CoordinateArgument z;
    
    public DefaultPosArgument(final CoordinateArgument x, final CoordinateArgument y, final CoordinateArgument z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public Vec3d toAbsolutePos(final ServerCommandSource source) {
        final Vec3d vec3d2 = source.getPosition();
        return new Vec3d(this.x.toAbsoluteCoordinate(vec3d2.x), this.y.toAbsoluteCoordinate(vec3d2.y), this.z.toAbsoluteCoordinate(vec3d2.z));
    }
    
    @Override
    public Vec2f toAbsoluteRotation(final ServerCommandSource source) {
        final Vec2f vec2f2 = source.getRotation();
        return new Vec2f((float)this.x.toAbsoluteCoordinate(vec2f2.x), (float)this.y.toAbsoluteCoordinate(vec2f2.y));
    }
    
    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }
    
    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }
    
    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPosArgument)) {
            return false;
        }
        final DefaultPosArgument defaultPosArgument2 = (DefaultPosArgument)o;
        return this.x.equals(defaultPosArgument2.x) && this.y.equals(defaultPosArgument2.y) && this.z.equals(defaultPosArgument2.z);
    }
    
    public static DefaultPosArgument parse(final StringReader reader) throws CommandSyntaxException {
        final int integer2 = reader.getCursor();
        final CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer2);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final CoordinateArgument coordinateArgument4 = CoordinateArgument.parse(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer2);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final CoordinateArgument coordinateArgument5 = CoordinateArgument.parse(reader);
        return new DefaultPosArgument(coordinateArgument3, coordinateArgument4, coordinateArgument5);
    }
    
    public static DefaultPosArgument parse(final StringReader reader, final boolean centerIntegers) throws CommandSyntaxException {
        final int integer3 = reader.getCursor();
        final CoordinateArgument coordinateArgument4 = CoordinateArgument.parse(reader, centerIntegers);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer3);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final CoordinateArgument coordinateArgument5 = CoordinateArgument.parse(reader, false);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(integer3);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        final CoordinateArgument coordinateArgument6 = CoordinateArgument.parse(reader, centerIntegers);
        return new DefaultPosArgument(coordinateArgument4, coordinateArgument5, coordinateArgument6);
    }
    
    public static DefaultPosArgument zero() {
        return new DefaultPosArgument(new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0));
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.x.hashCode();
        integer1 = 31 * integer1 + this.y.hashCode();
        integer1 = 31 * integer1 + this.z.hashCode();
        return integer1;
    }
}
