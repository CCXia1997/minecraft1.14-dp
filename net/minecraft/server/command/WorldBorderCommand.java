package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.MathHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Locale;
import net.minecraft.command.arguments.Vec2ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class WorldBorderCommand
{
    private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_FAILED_NOCHANGE_EXCEPTION;
    private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION;
    private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION;
    private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("worldborder").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("distance", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + FloatArgumentType.getFloat(commandContext, "distance"), 0L))).then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + FloatArgumentType.getFloat(commandContext, "distance"), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getTargetRemainingTime() + IntegerArgumentType.getInteger(commandContext, "time") * 1000L)))))).then(CommandManager.literal("set").then(((RequiredArgumentBuilder)CommandManager.argument("distance", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"), 0L))).then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"), IntegerArgumentType.getInteger(commandContext, "time") * 1000L)))))).then(CommandManager.literal("center").then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec2ArgumentType.create()).executes(commandContext -> executeCenter((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "pos")))))).then(((LiteralArgumentBuilder)CommandManager.literal("damage").then(CommandManager.literal("amount").then(CommandManager.argument("damagePerBlock", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).executes(commandContext -> executeDamage((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))))).then(CommandManager.literal("buffer").then(CommandManager.argument("distance", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).executes(commandContext -> executeBuffer((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"))))))).then(CommandManager.literal("get").executes(commandContext -> executeGet((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("warning").then(CommandManager.literal("distance").then(CommandManager.argument("distance", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeWarningDistance((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))))).then(CommandManager.literal("time").then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeWarningTime((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))));
    }
    
    private static int executeBuffer(final ServerCommandSource source, final float distance) throws CommandSyntaxException {
        final WorldBorder worldBorder3 = source.getWorld().getWorldBorder();
        if (worldBorder3.getBuffer() == distance) {
            throw WorldBorderCommand.DAMAGE_BUFFER_FAILED_EXCEPTION.create();
        }
        worldBorder3.setBuffer(distance);
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.damage.buffer.success", new Object[] { String.format(Locale.ROOT, "%.2f", distance) }), true);
        return (int)distance;
    }
    
    private static int executeDamage(final ServerCommandSource source, final float damagePerBlock) throws CommandSyntaxException {
        final WorldBorder worldBorder3 = source.getWorld().getWorldBorder();
        if (worldBorder3.getDamagePerBlock() == damagePerBlock) {
            throw WorldBorderCommand.DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
        }
        worldBorder3.setDamagePerBlock(damagePerBlock);
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.damage.amount.success", new Object[] { String.format(Locale.ROOT, "%.2f", damagePerBlock) }), true);
        return (int)damagePerBlock;
    }
    
    private static int executeWarningTime(final ServerCommandSource source, final int time) throws CommandSyntaxException {
        final WorldBorder worldBorder3 = source.getWorld().getWorldBorder();
        if (worldBorder3.getWarningTime() == time) {
            throw WorldBorderCommand.WARNING_TIME_FAILED_EXCEPTION.create();
        }
        worldBorder3.setWarningTime(time);
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.warning.time.success", new Object[] { time }), true);
        return time;
    }
    
    private static int executeWarningDistance(final ServerCommandSource source, final int distance) throws CommandSyntaxException {
        final WorldBorder worldBorder3 = source.getWorld().getWorldBorder();
        if (worldBorder3.getWarningBlocks() == distance) {
            throw WorldBorderCommand.WARNING_DISTANCE_FAILED_EXCEPTION.create();
        }
        worldBorder3.setWarningBlocks(distance);
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.warning.distance.success", new Object[] { distance }), true);
        return distance;
    }
    
    private static int executeGet(final ServerCommandSource source) {
        final double double2 = source.getWorld().getWorldBorder().getSize();
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.get", new Object[] { String.format(Locale.ROOT, "%.0f", double2) }), false);
        return MathHelper.floor(double2 + 0.5);
    }
    
    private static int executeCenter(final ServerCommandSource source, final Vec2f pos) throws CommandSyntaxException {
        final WorldBorder worldBorder3 = source.getWorld().getWorldBorder();
        if (worldBorder3.getCenterX() == pos.x && worldBorder3.getCenterZ() == pos.y) {
            throw WorldBorderCommand.CENTER_FAILED_EXCEPTION.create();
        }
        worldBorder3.setCenter(pos.x, pos.y);
        source.sendFeedback(new TranslatableTextComponent("commands.worldborder.center.success", new Object[] { String.format(Locale.ROOT, "%.2f", pos.x), String.format("%.2f", pos.y) }), true);
        return 0;
    }
    
    private static int executeSet(final ServerCommandSource source, final double distance, final long time) throws CommandSyntaxException {
        final WorldBorder worldBorder6 = source.getWorld().getWorldBorder();
        final double double7 = worldBorder6.getSize();
        if (double7 == distance) {
            throw WorldBorderCommand.SET_FAILED_NOCHANGE_EXCEPTION.create();
        }
        if (distance < 1.0) {
            throw WorldBorderCommand.SET_FAILED_SMALL_EXCEPTION.create();
        }
        if (distance > 6.0E7) {
            throw WorldBorderCommand.SET_FAILED_BIG_EXCEPTION.create();
        }
        if (time > 0L) {
            worldBorder6.interpolateSize(double7, distance, time);
            if (distance > double7) {
                source.sendFeedback(new TranslatableTextComponent("commands.worldborder.set.grow", new Object[] { String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L) }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.worldborder.set.shrink", new Object[] { String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L) }), true);
            }
        }
        else {
            worldBorder6.setSize(distance);
            source.sendFeedback(new TranslatableTextComponent("commands.worldborder.set.immediate", new Object[] { String.format(Locale.ROOT, "%.1f", distance) }), true);
        }
        return (int)(distance - double7);
    }
    
    static {
        CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.center.failed", new Object[0]));
        SET_FAILED_NOCHANGE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.set.failed.nochange", new Object[0]));
        SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.set.failed.small.", new Object[0]));
        SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.set.failed.big.", new Object[0]));
        WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.warning.time.failed", new Object[0]));
        WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.warning.distance.failed", new Object[0]));
        DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.damage.buffer.failed", new Object[0]));
        DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.worldborder.damage.amount.failed", new Object[0]));
    }
}
