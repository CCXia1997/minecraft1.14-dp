package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class MessageCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> literalCommandNode2 = (LiteralCommandNode<ServerCommandSource>)dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message"))))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect((CommandNode)literalCommandNode2));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect((CommandNode)literalCommandNode2));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final TextComponent message) {
        for (final ServerPlayerEntity serverPlayerEntity5 : targets) {
            serverPlayerEntity5.sendMessage(new TranslatableTextComponent("commands.message.display.incoming", new Object[] { source.getDisplayName(), message.copy() }).applyFormat(TextFormat.h, TextFormat.u));
            source.sendFeedback(new TranslatableTextComponent("commands.message.display.outgoing", new Object[] { serverPlayerEntity5.getDisplayName(), message.copy() }).applyFormat(TextFormat.h, TextFormat.u), false);
        }
        return targets.size();
    }
}
