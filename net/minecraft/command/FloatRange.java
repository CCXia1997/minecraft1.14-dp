package net.minecraft.command;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.util.NumberRange;
import java.util.function.Function;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class FloatRange
{
    public static final FloatRange ANY;
    public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION;
    private final Float min;
    private final Float max;
    
    public FloatRange(@Nullable final Float float1, @Nullable final Float float2) {
        this.min = float1;
        this.max = float2;
    }
    
    @Nullable
    public Float getMin() {
        return this.min;
    }
    
    @Nullable
    public Float getMax() {
        return this.max;
    }
    
    public static FloatRange parse(final StringReader reader, final boolean allowFloats, final Function<Float, Float> transform) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
        }
        final int integer4 = reader.getCursor();
        final Float float5 = mapFloat(parseFloat(reader, allowFloats), transform);
        Float float6;
        if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
            reader.skip();
            reader.skip();
            float6 = mapFloat(parseFloat(reader, allowFloats), transform);
            if (float5 == null && float6 == null) {
                reader.setCursor(integer4);
                throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
            }
        }
        else {
            if (!allowFloats && reader.canRead() && reader.peek() == '.') {
                reader.setCursor(integer4);
                throw FloatRange.ONLY_INTS_EXCEPTION.createWithContext((ImmutableStringReader)reader);
            }
            float6 = float5;
        }
        if (float5 == null && float6 == null) {
            reader.setCursor(integer4);
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
        }
        return new FloatRange(float5, float6);
    }
    
    @Nullable
    private static Float parseFloat(final StringReader reader, final boolean allowFloats) throws CommandSyntaxException {
        final int integer3 = reader.getCursor();
        while (reader.canRead() && peekDigit(reader, allowFloats)) {
            reader.skip();
        }
        final String string4 = reader.getString().substring(integer3, reader.getCursor());
        if (string4.isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(string4);
        }
        catch (NumberFormatException numberFormatException5) {
            if (allowFloats) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)reader, string4);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)reader, string4);
        }
    }
    
    private static boolean peekDigit(final StringReader reader, final boolean allowFloats) {
        final char character3 = reader.peek();
        return (character3 >= '0' && character3 <= '9') || character3 == '-' || (allowFloats && character3 == '.' && (!reader.canRead(2) || reader.peek(1) != '.'));
    }
    
    @Nullable
    private static Float mapFloat(@Nullable final Float float1, final Function<Float, Float> function) {
        return (float1 == null) ? null : function.apply(float1);
    }
    
    static {
        ANY = new FloatRange(null, null);
        ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.range.ints", new Object[0]));
    }
}
