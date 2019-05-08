package net.minecraft.command.arguments;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class TimeArgumentType implements ArgumentType<Integer>
{
    private static final Collection<String> EXAMPLES;
    private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_COUNT_EXCEPTION;
    private static final Object2IntMap<String> units;
    
    public static TimeArgumentType create() {
        return new TimeArgumentType();
    }
    
    public Integer a(final StringReader stringReader) throws CommandSyntaxException {
        final float float2 = stringReader.readFloat();
        final String string3 = stringReader.readUnquotedString();
        final int integer4 = TimeArgumentType.units.getOrDefault(string3, 0);
        if (integer4 == 0) {
            throw TimeArgumentType.INVALID_UNIT_EXCEPTION.create();
        }
        final int integer5 = Math.round(float2 * integer4);
        if (integer5 < 0) {
            throw TimeArgumentType.INVALID_COUNT_EXCEPTION.create(integer5);
        }
        return integer5;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringReader3 = new StringReader(builder.getRemaining());
        try {
            stringReader3.readFloat();
        }
        catch (CommandSyntaxException commandSyntaxException4) {
            return (CompletableFuture<Suggestions>)builder.buildFuture();
        }
        return CommandSource.suggestMatching((Iterable<String>)TimeArgumentType.units.keySet(), builder.createOffset(builder.getStart() + stringReader3.getCursor()));
    }
    
    public Collection<String> getExamples() {
        return TimeArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0d", "0s", "0t", "0");
        INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.time.invalid_unit", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        INVALID_COUNT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.time.invalid_tick_count", new Object[] { object });
            return translatableTextComponent;
        });
        (units = (Object2IntMap)new Object2IntOpenHashMap()).put("d", 24000);
        TimeArgumentType.units.put("s", 20);
        TimeArgumentType.units.put("t", 1);
        TimeArgumentType.units.put("", 1);
    }
}
