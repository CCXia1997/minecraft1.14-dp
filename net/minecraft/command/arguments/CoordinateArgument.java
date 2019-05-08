package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CoordinateArgument
{
    public static final SimpleCommandExceptionType MISSING_COORDINATE;
    public static final SimpleCommandExceptionType MISSING_BLOCK_POSITION;
    private final boolean relative;
    private final double value;
    
    public CoordinateArgument(final boolean relative, final double value) {
        this.relative = relative;
        this.value = value;
    }
    
    public double toAbsoluteCoordinate(final double offset) {
        if (this.relative) {
            return this.value + offset;
        }
        return this.value;
    }
    
    public static CoordinateArgument parse(final StringReader reader, final boolean centerIntegers) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_COORDINATE.createWithContext((ImmutableStringReader)reader);
        }
        final boolean boolean3 = isRelative(reader);
        final int integer4 = reader.getCursor();
        double double5 = (reader.canRead() && reader.peek() != ' ') ? reader.readDouble() : 0.0;
        final String string7 = reader.getString().substring(integer4, reader.getCursor());
        if (boolean3 && string7.isEmpty()) {
            return new CoordinateArgument(true, 0.0);
        }
        if (!string7.contains(".") && !boolean3 && centerIntegers) {
            double5 += 0.5;
        }
        return new CoordinateArgument(boolean3, double5);
    }
    
    public static CoordinateArgument parse(final StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_BLOCK_POSITION.createWithContext((ImmutableStringReader)reader);
        }
        final boolean boolean2 = isRelative(reader);
        double double3;
        if (reader.canRead() && reader.peek() != ' ') {
            double3 = (boolean2 ? reader.readDouble() : reader.readInt());
        }
        else {
            double3 = 0.0;
        }
        return new CoordinateArgument(boolean2, double3);
    }
    
    private static boolean isRelative(final StringReader reader) {
        boolean boolean2;
        if (reader.peek() == '~') {
            boolean2 = true;
            reader.skip();
        }
        else {
            boolean2 = false;
        }
        return boolean2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinateArgument)) {
            return false;
        }
        final CoordinateArgument coordinateArgument2 = (CoordinateArgument)o;
        return this.relative == coordinateArgument2.relative && Double.compare(coordinateArgument2.value, this.value) == 0;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.relative ? 1 : 0;
        final long long2 = Double.doubleToLongBits(this.value);
        integer1 = 31 * integer1 + (int)(long2 ^ long2 >>> 32);
        return integer1;
    }
    
    public boolean isRelative() {
        return this.relative;
    }
    
    static {
        MISSING_COORDINATE = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos.missing.double", new Object[0]));
        MISSING_BLOCK_POSITION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos.missing.int", new Object[0]));
    }
}
