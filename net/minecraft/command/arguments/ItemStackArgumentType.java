package net.minecraft.command.arguments;

import java.util.Arrays;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ItemStackArgumentType implements ArgumentType<ItemStackArgument>
{
    private static final Collection<String> EXAMPLES;
    
    public static ItemStackArgumentType create() {
        return new ItemStackArgumentType();
    }
    
    public ItemStackArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final ItemStringReader itemStringReader2 = new ItemStringReader(stringReader, false).consume();
        return new ItemStackArgument(itemStringReader2.getItem(), itemStringReader2.getTag());
    }
    
    public static <S> ItemStackArgument getItemStackArgument(final CommandContext<S> context, final String name) {
        return (ItemStackArgument)context.getArgument(name, (Class)ItemStackArgument.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringReader3 = new StringReader(builder.getInput());
        stringReader3.setCursor(builder.getStart());
        final ItemStringReader itemStringReader4 = new ItemStringReader(stringReader3, false);
        try {
            itemStringReader4.consume();
        }
        catch (CommandSyntaxException ex) {}
        return itemStringReader4.a(builder);
    }
    
    public Collection<String> getExamples() {
        return ItemStackArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("stick", "minecraft:stick", "stick{foo=bar}");
    }
}
