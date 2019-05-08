package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.particle.ParticleParameters;
import com.mojang.brigadier.arguments.ArgumentType;

public class ParticleArgumentType implements ArgumentType<ParticleParameters>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION;
    
    public static ParticleArgumentType create() {
        return new ParticleArgumentType();
    }
    
    public static ParticleParameters getParticle(final CommandContext<ServerCommandSource> context, final String name) {
        return (ParticleParameters)context.getArgument(name, (Class)ParticleParameters.class);
    }
    
    public ParticleParameters a(final StringReader stringReader) throws CommandSyntaxException {
        return readParameters(stringReader);
    }
    
    public Collection<String> getExamples() {
        return ParticleArgumentType.EXAMPLES;
    }
    
    public static ParticleParameters readParameters(final StringReader reader) throws CommandSyntaxException {
        final Identifier identifier2 = Identifier.parse(reader);
        final ParticleType<?> particleType3 = Registry.PARTICLE_TYPE.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> ParticleArgumentType.UNKNOWN_PARTICLE_EXCEPTION.create(identifier2));
        return ParticleArgumentType.<ParticleParameters>readParameters(reader, particleType3);
    }
    
    private static <T extends ParticleParameters> T readParameters(final StringReader reader, final ParticleType<T> type) throws CommandSyntaxException {
        return type.getParametersFactory().read(type, reader);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registry.PARTICLE_TYPE.getIds(), builder);
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "foo:bar", "particle with options");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("particle.notFound", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
