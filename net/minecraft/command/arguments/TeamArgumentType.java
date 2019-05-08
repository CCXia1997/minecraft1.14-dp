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
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class TeamArgumentType implements ArgumentType<String>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_TEAM_EXCEPTION;
    
    public static TeamArgumentType create() {
        return new TeamArgumentType();
    }
    
    public static Team getTeam(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final String string3 = (String)context.getArgument(name, (Class)String.class);
        final Scoreboard scoreboard4 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        final Team team5 = scoreboard4.getTeam(string3);
        if (team5 == null) {
            throw TeamArgumentType.UNKNOWN_TEAM_EXCEPTION.create(string3);
        }
        return team5;
    }
    
    public String a(final StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readUnquotedString();
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            return CommandSource.suggestMatching(((CommandSource)context.getSource()).getTeamNames(), builder);
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return TeamArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "123");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_TEAM_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("team.notFound", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
