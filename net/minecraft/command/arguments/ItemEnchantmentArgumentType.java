package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.enchantment.Enchantment;
import com.mojang.brigadier.arguments.ArgumentType;

public class ItemEnchantmentArgumentType implements ArgumentType<Enchantment>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType UNKNOWN_ENCHANTMENT_EXCEPTION;
    
    public static ItemEnchantmentArgumentType create() {
        return new ItemEnchantmentArgumentType();
    }
    
    public static Enchantment getEnchantment(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (Enchantment)commandContext.getArgument(string, (Class)Enchantment.class);
    }
    
    public Enchantment a(final StringReader stringReader) throws CommandSyntaxException {
        final Identifier identifier2 = Identifier.parse(stringReader);
        return Registry.ENCHANTMENT.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> ItemEnchantmentArgumentType.UNKNOWN_ENCHANTMENT_EXCEPTION.create(identifier2));
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registry.ENCHANTMENT.getIds(), builder);
    }
    
    public Collection<String> getExamples() {
        return ItemEnchantmentArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("unbreaking", "silk_touch");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_ENCHANTMENT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("enchantment.unknown", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
