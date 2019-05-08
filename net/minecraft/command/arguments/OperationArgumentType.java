package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class OperationArgumentType implements ArgumentType<Operation>
{
    private static final Collection<String> EXAMPLES;
    private static final SimpleCommandExceptionType INVALID_OPERATION;
    private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION;
    
    public static OperationArgumentType create() {
        return new OperationArgumentType();
    }
    
    public static Operation getOperation(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return (Operation)commandContext.getArgument(string, (Class)Operation.class);
    }
    
    public Operation a(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead()) {
            final int integer2 = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            return getOperator(stringReader.getString().substring(integer2, stringReader.getCursor()));
        }
        throw OperationArgumentType.INVALID_OPERATION.create();
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(new String[] { "=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><" }, builder);
    }
    
    public Collection<String> getExamples() {
        return OperationArgumentType.EXAMPLES;
    }
    
    private static Operation getOperator(final String string) throws CommandSyntaxException {
        if (string.equals("><")) {
            final int integer3;
            return (scoreboardPlayerScore1, scoreboardPlayerScore2) -> {
                integer3 = scoreboardPlayerScore1.getScore();
                scoreboardPlayerScore1.setScore(scoreboardPlayerScore2.getScore());
                scoreboardPlayerScore2.setScore(integer3);
                return;
            };
        }
        return getIntOperator(string);
    }
    
    private static IntOperator getIntOperator(final String string) throws CommandSyntaxException {
        switch (string) {
            case "=": {
                return (integer1, integer2) -> integer2;
            }
            case "+=": {
                return (integer1, integer2) -> integer1 + integer2;
            }
            case "-=": {
                return (integer1, integer2) -> integer1 - integer2;
            }
            case "*=": {
                return (integer1, integer2) -> integer1 * integer2;
            }
            case "/=": {
                return (integer1, integer2) -> {
                    if (integer2 == 0) {
                        throw OperationArgumentType.DIVISION_ZERO_EXCEPTION.create();
                    }
                    else {
                        return MathHelper.floorDiv(integer1, integer2);
                    }
                };
            }
            case "%=": {
                return (integer1, integer2) -> {
                    if (integer2 == 0) {
                        throw OperationArgumentType.DIVISION_ZERO_EXCEPTION.create();
                    }
                    else {
                        return MathHelper.floorMod(integer1, integer2);
                    }
                };
            }
            case "<": {
                return Math::min;
            }
            case ">": {
                return Math::max;
            }
            default: {
                throw OperationArgumentType.INVALID_OPERATION.create();
            }
        }
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("=", ">", "<");
        INVALID_OPERATION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("arguments.operation.invalid", new Object[0]));
        DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("arguments.operation.div0", new Object[0]));
    }
    
    @FunctionalInterface
    interface IntOperator extends Operation
    {
        int apply(final int arg1, final int arg2) throws CommandSyntaxException;
        
        default void apply(final ScoreboardPlayerScore scoreboardPlayerScore1, final ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException {
            scoreboardPlayerScore1.setScore(this.apply(scoreboardPlayerScore1.getScore(), scoreboardPlayerScore2.getScore()));
        }
    }
    
    @FunctionalInterface
    public interface Operation
    {
        void apply(final ScoreboardPlayerScore arg1, final ScoreboardPlayerScore arg2) throws CommandSyntaxException;
    }
}
