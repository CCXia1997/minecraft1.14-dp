package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import net.minecraft.server.command.CommandManager;
import java.util.Collections;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class Vec2ArgumentType implements ArgumentType<PosArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION;
    private final boolean centerIntegers;
    
    public Vec2ArgumentType(final boolean centerIntegers) {
        this.centerIntegers = centerIntegers;
    }
    
    public static Vec2ArgumentType create() {
        return new Vec2ArgumentType(true);
    }
    
    public static Vec2f getVec2(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final Vec3d vec3d3 = ((PosArgument)context.getArgument(name, (Class)PosArgument.class)).toAbsolutePos((ServerCommandSource)context.getSource());
        return new Vec2f((float)vec3d3.x, (float)vec3d3.z);
    }
    
    public PosArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw Vec2ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        final CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(stringReader, this.centerIntegers);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer2);
            throw Vec2ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final CoordinateArgument coordinateArgument4 = CoordinateArgument.parse(stringReader, this.centerIntegers);
        return new DefaultPosArgument(coordinateArgument3, new CoordinateArgument(true, 0.0), coordinateArgument4);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            final String string3 = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection4;
            if (!string3.isEmpty() && string3.charAt(0) == '^') {
                collection4 = Collections.<CommandSource.RelativePosition>singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            }
            else {
                collection4 = ((CommandSource)context.getSource()).getPositionSuggestions();
            }
            return CommandSource.suggestColumnPositions(string3, collection4, builder, CommandManager.getCommandValidator(this::a));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return Vec2ArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
        INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos2d.incomplete", new Object[0]));
    }
}
