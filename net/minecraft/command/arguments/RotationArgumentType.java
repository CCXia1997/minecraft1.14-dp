package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class RotationArgumentType implements ArgumentType<PosArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION;
    
    public static RotationArgumentType create() {
        return new RotationArgumentType();
    }
    
    public static PosArgument getRotation(final CommandContext<ServerCommandSource> context, final String name) {
        return (PosArgument)context.getArgument(name, (Class)PosArgument.class);
    }
    
    public PosArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw RotationArgumentType.INCOMPLETE_ROTATION_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        final CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer2);
            throw RotationArgumentType.INCOMPLETE_ROTATION_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final CoordinateArgument coordinateArgument4 = CoordinateArgument.parse(stringReader, false);
        return new DefaultPosArgument(coordinateArgument4, coordinateArgument3, new CoordinateArgument(true, 0.0));
    }
    
    public Collection<String> getExamples() {
        return RotationArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0 0", "~ ~", "~-5 ~5");
        INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.rotation.incomplete", new Object[0]));
    }
}
