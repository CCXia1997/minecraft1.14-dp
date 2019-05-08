package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.dimension.DimensionType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.Difficulty;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class DifficultyCommand
{
    private static final DynamicCommandExceptionType FAILURE_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = CommandManager.literal("difficulty");
        for (final Difficulty difficulty6 : Difficulty.values()) {
            literalArgumentBuilder2.then(CommandManager.literal(difficulty6.getTranslationKey()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), difficulty6)));
        }
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder2.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> {
            final Difficulty difficulty2 = ((ServerCommandSource)commandContext.getSource()).getWorld().getDifficulty();
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.difficulty.query", new Object[] { difficulty2.toTextComponent() }), false);
            return difficulty2.getId();
        }));
    }
    
    public static int execute(final ServerCommandSource source, final Difficulty difficulty) throws CommandSyntaxException {
        final MinecraftServer minecraftServer3 = source.getMinecraftServer();
        if (minecraftServer3.getWorld(DimensionType.a).getDifficulty() == difficulty) {
            throw DifficultyCommand.FAILURE_EXCEPTION.create(difficulty.getTranslationKey());
        }
        minecraftServer3.setDifficulty(difficulty, true);
        source.sendFeedback(new TranslatableTextComponent("commands.difficulty.success", new Object[] { difficulty.toTextComponent() }), true);
        return 0;
    }
    
    static {
        final TranslatableTextComponent translatableTextComponent;
        FAILURE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.difficulty.failure", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
