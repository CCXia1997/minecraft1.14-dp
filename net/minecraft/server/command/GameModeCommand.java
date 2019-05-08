package net.minecraft.server.command;

import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.Collection;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.world.GameMode;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class GameModeCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("gamemode").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (final GameMode gameMode6 : GameMode.values()) {
            if (gameMode6 != GameMode.INVALID) {
                literalArgumentBuilder2.then(((LiteralArgumentBuilder)CommandManager.literal(gameMode6.getName()).executes(commandContext -> execute((CommandContext<ServerCommandSource>)commandContext, Collections.<ServerPlayerEntity>singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), gameMode6))).then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "target"), gameMode6))));
            }
        }
        dispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static void setGameMode(final ServerCommandSource source, final ServerPlayerEntity player, final GameMode gameMode) {
        final TextComponent textComponent4 = new TranslatableTextComponent("gameMode." + gameMode.getName(), new Object[0]);
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableTextComponent("commands.gamemode.success.self", new Object[] { textComponent4 }), true);
        }
        else {
            if (source.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
                player.sendMessage(new TranslatableTextComponent("gameMode.changed", new Object[] { textComponent4 }));
            }
            source.sendFeedback(new TranslatableTextComponent("commands.gamemode.success.other", new Object[] { player.getDisplayName(), textComponent4 }), true);
        }
    }
    
    private static int execute(final CommandContext<ServerCommandSource> context, final Collection<ServerPlayerEntity> targets, final GameMode gameMode) {
        int integer4 = 0;
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            if (serverPlayerEntity6.interactionManager.getGameMode() != gameMode) {
                serverPlayerEntity6.setGameMode(gameMode);
                setGameMode((ServerCommandSource)context.getSource(), serverPlayerEntity6, gameMode);
                ++integer4;
            }
        }
        return integer4;
    }
}
