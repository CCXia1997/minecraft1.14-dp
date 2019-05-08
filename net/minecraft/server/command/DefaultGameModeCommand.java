package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class DefaultGameModeCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("defaultgamemode").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (final GameMode gameMode6 : GameMode.values()) {
            if (gameMode6 != GameMode.INVALID) {
                literalArgumentBuilder2.then(CommandManager.literal(gameMode6.getName()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), gameMode6)));
            }
        }
        dispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static int execute(final ServerCommandSource source, final GameMode defaultGameMode) {
        int integer3 = 0;
        final MinecraftServer minecraftServer4 = source.getMinecraftServer();
        minecraftServer4.setDefaultGameMode(defaultGameMode);
        if (minecraftServer4.shouldForceGameMode()) {
            for (final ServerPlayerEntity serverPlayerEntity6 : minecraftServer4.getPlayerManager().getPlayerList()) {
                if (serverPlayerEntity6.interactionManager.getGameMode() != defaultGameMode) {
                    serverPlayerEntity6.setGameMode(defaultGameMode);
                    ++integer3;
                }
            }
        }
        source.sendFeedback(new TranslatableTextComponent("commands.defaultgamemode.success", new Object[] { defaultGameMode.getTextComponent() }), true);
        return integer3;
    }
}
