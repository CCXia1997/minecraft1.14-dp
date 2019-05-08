package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.stat.Stat;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.command.CommandSource;
import net.minecraft.stat.StatType;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.scoreboard.ScoreboardCriterion;
import com.mojang.brigadier.arguments.ArgumentType;

public class ObjectiveCriteriaArgumentType implements ArgumentType<ScoreboardCriterion>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_CRITERIA_EXCEPTION;
    
    private ObjectiveCriteriaArgumentType() {
    }
    
    public static ObjectiveCriteriaArgumentType create() {
        return new ObjectiveCriteriaArgumentType();
    }
    
    public static ScoreboardCriterion getCriteria(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (ScoreboardCriterion)commandContext.getArgument(string, (Class)ScoreboardCriterion.class);
    }
    
    public ScoreboardCriterion a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        final String string3 = stringReader.getString().substring(integer2, stringReader.getCursor());
        final int cursor;
        final Object o;
        return ScoreboardCriterion.createStatCriterion(string3).<Throwable>orElseThrow(() -> {
            stringReader.setCursor(cursor);
            return ObjectiveCriteriaArgumentType.INVALID_CRITERIA_EXCEPTION.create(o);
        });
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final List<String> list3 = Lists.newArrayList(ScoreboardCriterion.OBJECTIVES.keySet());
        for (final StatType<?> statType5 : Registry.STAT_TYPE) {
            for (final Object object7 : statType5.getRegistry()) {
                final String string8 = this.getStatName(statType5, object7);
                list3.add(string8);
            }
        }
        return CommandSource.suggestMatching(list3, builder);
    }
    
    public <T> String getStatName(final StatType<T> stat, final Object value) {
        return Stat.getName((StatType<Object>)stat, value);
    }
    
    public Collection<String> getExamples() {
        return ObjectiveCriteriaArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "foo.bar.baz", "minecraft:foo");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_CRITERIA_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.criteria.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
