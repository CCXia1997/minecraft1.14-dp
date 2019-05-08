package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.entity.effect.StatusEffect;
import com.mojang.brigadier.arguments.ArgumentType;

public class MobEffectArgumentType implements ArgumentType<StatusEffect>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_EFFECT_EXCEPTION;
    
    public static MobEffectArgumentType create() {
        return new MobEffectArgumentType();
    }
    
    public static StatusEffect getMobEffect(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return (StatusEffect)commandContext.getArgument(string, (Class)StatusEffect.class);
    }
    
    public StatusEffect a(final StringReader stringReader) throws CommandSyntaxException {
        final Identifier identifier2 = Identifier.parse(stringReader);
        return Registry.STATUS_EFFECT.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> MobEffectArgumentType.INVALID_EFFECT_EXCEPTION.create(identifier2));
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registry.STATUS_EFFECT.getIds(), builder);
    }
    
    public Collection<String> getExamples() {
        return MobEffectArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("spooky", "effect");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_EFFECT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("effect.effectNotFound", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
