package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ObjectiveArgumentType implements ArgumentType<String>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION;
    private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION;
    public static final DynamicCommandExceptionType LONG_NAME_EXCEPTION;
    
    public static ObjectiveArgumentType create() {
        return new ObjectiveArgumentType();
    }
    
    public static ScoreboardObjective getObjective(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final String string3 = (String)context.getArgument(name, (Class)String.class);
        final Scoreboard scoreboard4 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        final ScoreboardObjective scoreboardObjective5 = scoreboard4.getNullableObjective(string3);
        if (scoreboardObjective5 == null) {
            throw ObjectiveArgumentType.UNKNOWN_OBJECTIVE_EXCEPTION.create(string3);
        }
        return scoreboardObjective5;
    }
    
    public static ScoreboardObjective getWritableObjective(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final ScoreboardObjective scoreboardObjective3 = getObjective(context, name);
        if (scoreboardObjective3.getCriterion().isReadOnly()) {
            throw ObjectiveArgumentType.READONLY_OBJECTIVE_EXCEPTION.create(scoreboardObjective3.getName());
        }
        return scoreboardObjective3;
    }
    
    public String a(final StringReader stringReader) throws CommandSyntaxException {
        final String string2 = stringReader.readUnquotedString();
        if (string2.length() > 16) {
            throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create(16);
        }
        return string2;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof ServerCommandSource) {
            return CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard().getObjectiveNames(), builder);
        }
        if (context.getSource() instanceof CommandSource) {
            final CommandSource commandSource3 = (CommandSource)context.getSource();
            return commandSource3.getCompletions((CommandContext<CommandSource>)context, builder);
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return ObjectiveArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "*", "012");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.objective.notFound", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.objective.readonly", new Object[] { object });
            return translatableTextComponent2;
        });
        final TranslatableTextComponent translatableTextComponent3;
        LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.scoreboard.objectives.add.longName", new Object[] { object });
            return translatableTextComponent3;
        });
    }
}
