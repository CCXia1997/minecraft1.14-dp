package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class WeatherCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("rain").executes(commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("thunder").executes(commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)))));
    }
    
    private static int executeClear(final ServerCommandSource source, final int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(duration);
        source.getWorld().getLevelProperties().setRainTime(0);
        source.getWorld().getLevelProperties().setThunderTime(0);
        source.getWorld().getLevelProperties().setRaining(false);
        source.getWorld().getLevelProperties().setThundering(false);
        source.sendFeedback(new TranslatableTextComponent("commands.weather.set.clear", new Object[0]), true);
        return duration;
    }
    
    private static int executeRain(final ServerCommandSource source, final int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(0);
        source.getWorld().getLevelProperties().setRainTime(duration);
        source.getWorld().getLevelProperties().setThunderTime(duration);
        source.getWorld().getLevelProperties().setRaining(true);
        source.getWorld().getLevelProperties().setThundering(false);
        source.sendFeedback(new TranslatableTextComponent("commands.weather.set.rain", new Object[0]), true);
        return duration;
    }
    
    private static int executeThunder(final ServerCommandSource source, final int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(0);
        source.getWorld().getLevelProperties().setRainTime(duration);
        source.getWorld().getLevelProperties().setThunderTime(duration);
        source.getWorld().getLevelProperties().setRaining(true);
        source.getWorld().getLevelProperties().setThundering(true);
        source.sendFeedback(new TranslatableTextComponent("commands.weather.set.thunder", new Object[0]), true);
        return duration;
    }
}
