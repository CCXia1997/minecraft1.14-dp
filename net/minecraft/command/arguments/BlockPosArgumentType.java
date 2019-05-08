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
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class BlockPosArgumentType implements ArgumentType<PosArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION;
    public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION;
    
    public static BlockPosArgumentType create() {
        return new BlockPosArgumentType();
    }
    
    public static BlockPos getLoadedBlockPos(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final BlockPos blockPos3 = ((PosArgument)context.getArgument(name, (Class)PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
        if (!((ServerCommandSource)context.getSource()).getWorld().isBlockLoaded(blockPos3)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        ((ServerCommandSource)context.getSource()).getWorld();
        if (!World.isValid(blockPos3)) {
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        }
        return blockPos3;
    }
    
    public static BlockPos getBlockPos(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return ((PosArgument)commandContext.getArgument(string, (Class)PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)commandContext.getSource());
    }
    
    public PosArgument a(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            final String string3 = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection4;
            if (!string3.isEmpty() && string3.charAt(0) == '^') {
                collection4 = Collections.<CommandSource.RelativePosition>singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            }
            else {
                collection4 = ((CommandSource)context.getSource()).getBlockPositionSuggestions();
            }
            return CommandSource.suggestPositions(string3, collection4, builder, CommandManager.getCommandValidator(this::a));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return BlockPosArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
        UNLOADED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos.unloaded", new Object[0]));
        OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.pos.outofworld", new Object[0]));
    }
}
