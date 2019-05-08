package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormatter;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.ComponentArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class TellRawCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tellraw").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> {
            int integer2 = 0;
            for (final ServerPlayerEntity serverPlayerEntity4 : EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                serverPlayerEntity4.sendMessage(TextFormatter.resolveAndStyle((ServerCommandSource)commandContext.getSource(), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "message"), serverPlayerEntity4));
                ++integer2;
            }
            return integer2;
        }))));
    }
}
