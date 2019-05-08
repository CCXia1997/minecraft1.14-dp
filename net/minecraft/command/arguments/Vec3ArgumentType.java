package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import net.minecraft.server.command.CommandManager;
import java.util.Collections;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class Vec3ArgumentType implements ArgumentType<PosArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION;
    public static final SimpleCommandExceptionType MIXED_COORDINATE_EXCEPTION;
    private final boolean centerIntegers;
    
    public Vec3ArgumentType(final boolean centerIntegers) {
        this.centerIntegers = centerIntegers;
    }
    
    public static Vec3ArgumentType create() {
        return new Vec3ArgumentType(true);
    }
    
    public static Vec3ArgumentType create(final boolean centerIntegers) {
        return new Vec3ArgumentType(centerIntegers);
    }
    
    public static Vec3d getVec3(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((PosArgument)context.getArgument(name, (Class)PosArgument.class)).toAbsolutePos((ServerCommandSource)context.getSource());
    }
    
    public static PosArgument getPosArgument(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (PosArgument)commandContext.getArgument(string, (Class)PosArgument.class);
    }
    
    public PosArgument a(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader, this.centerIntegers);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            final String string3 = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection4;
            if (!string3.isEmpty() && string3.charAt(0) == '^') {
                collection4 = Collections.<CommandSource.RelativePosition>singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            }
            else {
                collection4 = ((CommandSource)context.getSource()).getPositionSuggestions();
            }
            return CommandSource.suggestPositions(string3, collection4, builder, CommandManager.getCommandValidator(this::a));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return Vec3ArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
        INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos3d.incomplete", new Object[0]));
        MIXED_COORDINATE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos.mixed", new Object[0]));
    }
}
