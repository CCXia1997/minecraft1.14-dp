package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormatter;
import java.util.Iterator;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.Packet;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ComponentArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class TitleCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("title").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.literal("clear").executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"))))).then(CommandManager.literal("reset").executes(commandContext -> executeReset((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"))))).then(CommandManager.literal("title").then(CommandManager.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.a))))).then(CommandManager.literal("subtitle").then(CommandManager.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.b))))).then(CommandManager.literal("actionbar").then(CommandManager.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "title"), TitleS2CPacket.Action.c))))).then(CommandManager.literal("times").then(CommandManager.argument("fadeIn", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).then(CommandManager.argument("stay", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).then(CommandManager.argument("fadeOut", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeTimes((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut")))))))));
    }
    
    private static int executeClear(final ServerCommandSource serverCommandSource, final Collection<ServerPlayerEntity> collection) {
        final TitleS2CPacket titleS2CPacket3 = new TitleS2CPacket(TitleS2CPacket.Action.HIDE, null);
        for (final ServerPlayerEntity serverPlayerEntity5 : collection) {
            serverPlayerEntity5.networkHandler.sendPacket(titleS2CPacket3);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.title.cleared.single", new Object[] { collection.iterator().next().getDisplayName() }), true);
        }
        else {
            serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.title.cleared.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int executeReset(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets) {
        final TitleS2CPacket titleS2CPacket3 = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);
        for (final ServerPlayerEntity serverPlayerEntity5 : targets) {
            serverPlayerEntity5.networkHandler.sendPacket(titleS2CPacket3);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.title.reset.single", new Object[] { targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.title.reset.multiple", new Object[] { targets.size() }), true);
        }
        return targets.size();
    }
    
    private static int executeTitle(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final TextComponent title, final TitleS2CPacket.Action type) throws CommandSyntaxException {
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            serverPlayerEntity6.networkHandler.sendPacket(new TitleS2CPacket(type, TextFormatter.resolveAndStyle(source, title, serverPlayerEntity6)));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", new Object[] { targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[] { targets.size() }), true);
        }
        return targets.size();
    }
    
    private static int executeTimes(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final int fadeIn, final int stay, final int fadeOut) {
        final TitleS2CPacket titleS2CPacket6 = new TitleS2CPacket(fadeIn, stay, fadeOut);
        for (final ServerPlayerEntity serverPlayerEntity8 : targets) {
            serverPlayerEntity8.networkHandler.sendPacket(titleS2CPacket6);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.title.times.single", new Object[] { targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.title.times.multiple", new Object[] { targets.size() }), true);
        }
        return targets.size();
    }
}
