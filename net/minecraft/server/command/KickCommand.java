package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class KickCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kick").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), new TranslatableTextComponent("multiplayer.disconnect.kicked", new Object[0])))).then(CommandManager.argument("reason", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final TextComponent reason) {
        for (final ServerPlayerEntity serverPlayerEntity5 : targets) {
            serverPlayerEntity5.networkHandler.disconnect(reason);
            source.sendFeedback(new TranslatableTextComponent("commands.kick.success", new Object[] { serverPlayerEntity5.getDisplayName(), reason }), true);
        }
        return targets.size();
    }
}
