package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.text.TextFormat;
import com.mojang.brigadier.arguments.ArgumentType;

public class ColorArgumentType implements ArgumentType<TextFormat>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION;
    
    private ColorArgumentType() {
    }
    
    public static ColorArgumentType create() {
        return new ColorArgumentType();
    }
    
    public static TextFormat getColor(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (TextFormat)commandContext.getArgument(string, (Class)TextFormat.class);
    }
    
    public TextFormat a(final StringReader stringReader) throws CommandSyntaxException {
        final String string2 = stringReader.readUnquotedString();
        final TextFormat textFormat3 = TextFormat.getFormatByName(string2);
        if (textFormat3 == null || textFormat3.isModifier()) {
            throw ColorArgumentType.INVALID_COLOR_EXCEPTION.create(string2);
        }
        return textFormat3;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(TextFormat.getNames(true, false), builder);
    }
    
    public Collection<String> getExamples() {
        return ColorArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("red", "green");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_COLOR_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.color.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
