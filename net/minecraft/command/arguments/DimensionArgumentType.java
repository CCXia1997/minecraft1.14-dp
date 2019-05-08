package net.minecraft.command.arguments;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandSource;
import java.util.function.Function;
import com.google.common.collect.Streams;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.world.dimension.DimensionType;
import com.mojang.brigadier.arguments.ArgumentType;

public class DimensionArgumentType implements ArgumentType<DimensionType>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION;
    
    public DimensionType a(final StringReader stringReader) throws CommandSyntaxException {
        final Identifier identifier2 = Identifier.parse(stringReader);
        return Registry.DIMENSION.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> DimensionArgumentType.INVALID_DIMENSION_EXCEPTION.create(identifier2));
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Streams.<DimensionType>stream(DimensionType.getAll()).<Identifier>map(DimensionType::getId), builder);
    }
    
    public Collection<String> getExamples() {
        return DimensionArgumentType.EXAMPLES;
    }
    
    public static DimensionArgumentType create() {
        return new DimensionArgumentType();
    }
    
    public static DimensionType getDimensionArgument(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (DimensionType)commandContext.getArgument(string, (Class)DimensionType.class);
    }
    
    static {
        EXAMPLES = Stream.<DimensionType>of(new DimensionType[] { DimensionType.a, DimensionType.b }).map(dimensionType -> DimensionType.getId(dimensionType).toString()).collect(Collectors.toList());
        final TranslatableTextComponent translatableTextComponent;
        INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.dimension.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
