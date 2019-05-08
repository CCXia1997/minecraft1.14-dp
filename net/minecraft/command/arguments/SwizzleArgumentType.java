package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.util.math.Direction;
import java.util.EnumSet;
import com.mojang.brigadier.arguments.ArgumentType;

public class SwizzleArgumentType implements ArgumentType<EnumSet<Direction.Axis>>
{
    private static final Collection<String> EXAMPLES;
    private static final SimpleCommandExceptionType INVALID_SWIZZLE_EXCEPTION;
    
    public static SwizzleArgumentType create() {
        return new SwizzleArgumentType();
    }
    
    public static EnumSet<Direction.Axis> getSwizzle(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (EnumSet<Direction.Axis>)commandContext.getArgument(string, (Class)EnumSet.class);
    }
    
    public EnumSet<Direction.Axis> a(final StringReader stringReader) throws CommandSyntaxException {
        final EnumSet<Direction.Axis> enumSet2 = EnumSet.<Direction.Axis>noneOf(Direction.Axis.class);
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            final char character3 = stringReader.read();
            Direction.Axis axis4 = null;
            switch (character3) {
                case 'x': {
                    axis4 = Direction.Axis.X;
                    break;
                }
                case 'y': {
                    axis4 = Direction.Axis.Y;
                    break;
                }
                case 'z': {
                    axis4 = Direction.Axis.Z;
                    break;
                }
                default: {
                    throw SwizzleArgumentType.INVALID_SWIZZLE_EXCEPTION.create();
                }
            }
            if (enumSet2.contains(axis4)) {
                throw SwizzleArgumentType.INVALID_SWIZZLE_EXCEPTION.create();
            }
            enumSet2.add(axis4);
        }
        return enumSet2;
    }
    
    public Collection<String> getExamples() {
        return SwizzleArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("xyz", "x");
        INVALID_SWIZZLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("arguments.swizzle.invalid", new Object[0]));
    }
}
