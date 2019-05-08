package net.minecraft.server.command;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import java.util.function.ToIntFunction;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ExperienceCommand
{
    private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> literalCommandNode2 = (LiteralCommandNode<ServerCommandSource>)dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("experience").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.a))).then(CommandManager.literal("points").executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.a)))).then(CommandManager.literal("levels").executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.b))))))).then(CommandManager.literal("set").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.a))).then(CommandManager.literal("points").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.a)))).then(CommandManager.literal("levels").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.b))))))).then(CommandManager.literal("query").then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.player()).then(CommandManager.literal("points").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.a)))).then(CommandManager.literal("levels").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.b))))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("xp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode2));
    }
    
    private static int executeQuery(final ServerCommandSource source, final ServerPlayerEntity player, final Component component) {
        final int integer4 = component.getter.applyAsInt(player);
        source.sendFeedback(new TranslatableTextComponent("commands.experience.query." + component.name, new Object[] { player.getDisplayName(), integer4 }), false);
        return integer4;
    }
    
    private static int executeAdd(final ServerCommandSource source, final Collection<? extends ServerPlayerEntity> targets, final int amount, final Component component) {
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            component.adder.accept(serverPlayerEntity6, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.experience.add." + component.name + ".success.single", new Object[] { amount, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.experience.add." + component.name + ".success.multiple", new Object[] { amount, targets.size() }), true);
        }
        return targets.size();
    }
    
    private static int executeSet(final ServerCommandSource source, final Collection<? extends ServerPlayerEntity> targets, final int amount, final Component component) throws CommandSyntaxException {
        int integer5 = 0;
        for (final ServerPlayerEntity serverPlayerEntity7 : targets) {
            if (component.setter.test(serverPlayerEntity7, amount)) {
                ++integer5;
            }
        }
        if (integer5 == 0) {
            throw ExperienceCommand.SET_POINT_INVALID_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.experience.set." + component.name + ".success.single", new Object[] { amount, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.experience.set." + component.name + ".success.multiple", new Object[] { amount, targets.size() }), true);
        }
        return targets.size();
    }
    
    static {
        SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.experience.set.points.invalid", new Object[0]));
    }
    
    enum Component
    {
        a("points", PlayerEntity::addExperience, (serverPlayerEntity, integer) -> {
            if (integer >= serverPlayerEntity.getNextLevelExperience()) {
                return false;
            }
            else {
                serverPlayerEntity.setExperiencePoints(integer);
                return true;
            }
        }, serverPlayerEntity -> MathHelper.floor(serverPlayerEntity.experienceLevelProgress * serverPlayerEntity.getNextLevelExperience())), 
        b("levels", ServerPlayerEntity::c, (serverPlayerEntity, integer) -> {
            serverPlayerEntity.setExperienceLevel(integer);
            return true;
        }, serverPlayerEntity -> serverPlayerEntity.experience);
        
        public final BiConsumer<ServerPlayerEntity, Integer> adder;
        public final BiPredicate<ServerPlayerEntity, Integer> setter;
        public final String name;
        private final ToIntFunction<ServerPlayerEntity> getter;
        
        private Component(final String name, final BiConsumer<ServerPlayerEntity, Integer> adder, final BiPredicate<ServerPlayerEntity, Integer> setter, final ToIntFunction<ServerPlayerEntity> getter) {
            this.adder = adder;
            this.name = name;
            this.setter = setter;
            this.getter = getter;
        }
    }
}
