package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.Scoreboard;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ScoreboardSlotArgumentType implements ArgumentType<Integer>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_SLOT_EXCEPTION;
    
    private ScoreboardSlotArgumentType() {
    }
    
    public static ScoreboardSlotArgumentType create() {
        return new ScoreboardSlotArgumentType();
    }
    
    public static int getScorebordSlot(final CommandContext<ServerCommandSource> context, final String name) {
        return (int)context.getArgument(name, (Class)Integer.class);
    }
    
    public Integer a(final StringReader stringReader) throws CommandSyntaxException {
        final String string2 = stringReader.readUnquotedString();
        final int integer3 = Scoreboard.getDisplaySlotId(string2);
        if (integer3 == -1) {
            throw ScoreboardSlotArgumentType.INVALID_SLOT_EXCEPTION.create(string2);
        }
        return integer3;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Scoreboard.getDisplaySlotNames(), builder);
    }
    
    public Collection<String> getExamples() {
        return ScoreboardSlotArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("sidebar", "foo.bar");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_SLOT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.scoreboardDisplaySlot.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
