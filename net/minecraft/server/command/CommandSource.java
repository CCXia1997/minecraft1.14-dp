package net.minecraft.server.command;

import java.util.List;
import com.google.common.collect.Lists;
import java.util.function.Predicate;
import com.mojang.brigadier.Message;
import java.util.Locale;
import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import java.util.Collections;
import java.util.Collection;

public interface CommandSource
{
    Collection<String> getPlayerNames();
    
    default Collection<String> getEntitySuggestions() {
        return Collections.emptyList();
    }
    
    Collection<String> getTeamNames();
    
    Collection<Identifier> getSoundIds();
    
    Stream<Identifier> getRecipeIds();
    
    CompletableFuture<Suggestions> getCompletions(final CommandContext<CommandSource> arg1, final SuggestionsBuilder arg2);
    
    default Collection<RelativePosition> getBlockPositionSuggestions() {
        return Collections.<RelativePosition>singleton(RelativePosition.ZERO_WORLD);
    }
    
    default Collection<RelativePosition> getPositionSuggestions() {
        return Collections.<RelativePosition>singleton(RelativePosition.ZERO_WORLD);
    }
    
    boolean hasPermissionLevel(final int arg1);
    
    default <T> void forEachMatching(final Iterable<T> candidates, final String string, final Function<T, Identifier> identifier, final Consumer<T> action) {
        final boolean boolean5 = string.indexOf(58) > -1;
        for (final T object7 : candidates) {
            final Identifier identifier2 = identifier.apply(object7);
            if (boolean5) {
                final String string2 = identifier2.toString();
                if (!string2.startsWith(string)) {
                    continue;
                }
                action.accept(object7);
            }
            else {
                if (!identifier2.getNamespace().startsWith(string) && (!identifier2.getNamespace().equals("minecraft") || !identifier2.getPath().startsWith(string))) {
                    continue;
                }
                action.accept(object7);
            }
        }
    }
    
    default <T> void forEachMatching(final Iterable<T> candidates, final String string2, final String string3, final Function<T, Identifier> identifier, final Consumer<T> action) {
        if (string2.isEmpty()) {
            candidates.forEach(action);
        }
        else {
            final String string4 = Strings.commonPrefix(string2, string3);
            if (!string4.isEmpty()) {
                final String string5 = string2.substring(string4.length());
                CommandSource.<T>forEachMatching(candidates, string5, identifier, action);
            }
        }
    }
    
    default CompletableFuture<Suggestions> suggestIdentifiers(final Iterable<Identifier> candiates, final SuggestionsBuilder builder, final String string) {
        final String string2 = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.<Identifier>forEachMatching(candiates, string2, string, identifier -> identifier, identifier -> builder.suggest(string + identifier));
        return (CompletableFuture<Suggestions>)builder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestIdentifiers(final Iterable<Identifier> candidates, final SuggestionsBuilder builder) {
        final String string3 = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.<Identifier>forEachMatching(candidates, string3, identifier -> identifier, identifier -> builder.suggest(identifier.toString()));
        return (CompletableFuture<Suggestions>)builder.buildFuture();
    }
    
    default <T> CompletableFuture<Suggestions> suggestFromIdentifier(final Iterable<T> candidates, final SuggestionsBuilder builder, final Function<T, Identifier> identifier, final Function<T, Message> tooltip) {
        final String string5 = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.<T>forEachMatching(candidates, string5, identifier, object -> builder.suggest(identifier.apply(object).toString(), (Message)tooltip.apply(object)));
        return (CompletableFuture<Suggestions>)builder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestIdentifiers(final Stream<Identifier> stream, final SuggestionsBuilder suggestionsBuilder) {
        return suggestIdentifiers(stream::iterator, suggestionsBuilder);
    }
    
    default <T> CompletableFuture<Suggestions> suggestFromIdentifier(final Stream<T> candidates, final SuggestionsBuilder builder, final Function<T, Identifier> identifier, final Function<T, Message> tooltip) {
        return CommandSource.<T>suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
    }
    
    default CompletableFuture<Suggestions> suggestPositions(final String string, final Collection<RelativePosition> candidates, final SuggestionsBuilder suggestionsBuilder, final Predicate<String> predicate) {
        final List<String> list5 = Lists.newArrayList();
        if (Strings.isNullOrEmpty(string)) {
            for (final RelativePosition relativePosition7 : candidates) {
                final String string2 = relativePosition7.x + " " + relativePosition7.y + " " + relativePosition7.z;
                if (predicate.test(string2)) {
                    list5.add(relativePosition7.x);
                    list5.add(relativePosition7.x + " " + relativePosition7.y);
                    list5.add(string2);
                }
            }
        }
        else {
            final String[] arr6 = string.split(" ");
            if (arr6.length == 1) {
                for (final RelativePosition relativePosition8 : candidates) {
                    final String string3 = arr6[0] + " " + relativePosition8.y + " " + relativePosition8.z;
                    if (predicate.test(string3)) {
                        list5.add(arr6[0] + " " + relativePosition8.y);
                        list5.add(string3);
                    }
                }
            }
            else if (arr6.length == 2) {
                for (final RelativePosition relativePosition8 : candidates) {
                    final String string3 = arr6[0] + " " + arr6[1] + " " + relativePosition8.z;
                    if (predicate.test(string3)) {
                        list5.add(string3);
                    }
                }
            }
        }
        return suggestMatching(list5, suggestionsBuilder);
    }
    
    default CompletableFuture<Suggestions> suggestColumnPositions(final String string, final Collection<RelativePosition> collection, final SuggestionsBuilder suggestionsBuilder, final Predicate<String> predicate) {
        final List<String> list5 = Lists.newArrayList();
        if (Strings.isNullOrEmpty(string)) {
            for (final RelativePosition relativePosition7 : collection) {
                final String string2 = relativePosition7.x + " " + relativePosition7.z;
                if (predicate.test(string2)) {
                    list5.add(relativePosition7.x);
                    list5.add(string2);
                }
            }
        }
        else {
            final String[] arr6 = string.split(" ");
            if (arr6.length == 1) {
                for (final RelativePosition relativePosition8 : collection) {
                    final String string3 = arr6[0] + " " + relativePosition8.z;
                    if (predicate.test(string3)) {
                        list5.add(string3);
                    }
                }
            }
        }
        return suggestMatching(list5, suggestionsBuilder);
    }
    
    default CompletableFuture<Suggestions> suggestMatching(final Iterable<String> iterable, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final String string4 : iterable) {
            if (string4.toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest(string4);
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestMatching(final Stream<String> stream, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string3)).forEach(suggestionsBuilder::suggest);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestMatching(final String[] arr, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final String string4 : arr) {
            if (string4.toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest(string4);
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    public static class RelativePosition
    {
        public static final RelativePosition ZERO_LOCAL;
        public static final RelativePosition ZERO_WORLD;
        public final String x;
        public final String y;
        public final String z;
        
        public RelativePosition(final String x, final String y, final String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        static {
            ZERO_LOCAL = new RelativePosition("^", "^", "^");
            ZERO_WORLD = new RelativePosition("~", "~", "~");
        }
    }
}
