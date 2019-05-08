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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ColumnPosArgumentType implements ArgumentType<PosArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION;
    
    public static ColumnPosArgumentType create() {
        return new ColumnPosArgumentType();
    }
    
    public static ColumnPos getColumnPos(final CommandContext<ServerCommandSource> context, final String name) {
        final BlockPos blockPos3 = ((PosArgument)context.getArgument(name, (Class)PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
        return new ColumnPos(blockPos3.getX(), blockPos3.getZ());
    }
    
    public PosArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw ColumnPosArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        final CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer2);
            throw ColumnPosArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final CoordinateArgument coordinateArgument4 = CoordinateArgument.parse(stringReader);
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
                collection4 = ((CommandSource)context.getSource()).getBlockPositionSuggestions();
            }
            return CommandSource.suggestColumnPositions(string3, collection4, builder, CommandManager.getCommandValidator(this::a));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return ColumnPosArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
        INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos2d.incomplete", new Object[0]));
    }
}
