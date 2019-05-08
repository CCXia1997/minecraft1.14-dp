package net.minecraft.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.world.ServerWorld;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class TimeCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set").then(CommandManager.literal("day").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 1000)))).then(CommandManager.literal("noon").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 6000)))).then(CommandManager.literal("night").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 13000)))).then(CommandManager.literal("midnight").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 18000)))).then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)TimeArgumentType.create()).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))).then(CommandManager.literal("add").then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)TimeArgumentType.create()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("query").then(CommandManager.literal("daytime").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), getDayTime(((ServerCommandSource)commandContext.getSource()).getWorld()))))).then(CommandManager.literal("gametime").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), (int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTime() % 2147483647L))))).then(CommandManager.literal("day").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), (int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTimeOfDay() / 24000L % 2147483647L))))));
    }
    
    private static int getDayTime(final ServerWorld world) {
        return (int)(world.getTimeOfDay() % 24000L);
    }
    
    private static int executeQuery(final ServerCommandSource source, final int time) {
        source.sendFeedback(new TranslatableTextComponent("commands.time.query", new Object[] { time }), false);
        return time;
    }
    
    public static int executeSet(final ServerCommandSource source, final int time) {
        for (final ServerWorld serverWorld4 : source.getMinecraftServer().getWorlds()) {
            serverWorld4.setTimeOfDay(time);
        }
        source.sendFeedback(new TranslatableTextComponent("commands.time.set", new Object[] { time }), true);
        return getDayTime(source.getWorld());
    }
    
    public static int executeAdd(final ServerCommandSource source, final int time) {
        for (final ServerWorld serverWorld4 : source.getMinecraftServer().getWorlds()) {
            serverWorld4.setTimeOfDay(serverWorld4.getTimeOfDay() + time);
        }
        final int integer3 = getDayTime(source.getWorld());
        source.sendFeedback(new TranslatableTextComponent("commands.time.set", new Object[] { integer3 }), true);
        return integer3;
    }
}
