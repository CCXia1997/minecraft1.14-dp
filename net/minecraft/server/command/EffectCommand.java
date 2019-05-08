package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class EffectCommand
{
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("clear").then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))).then(CommandManager.argument("effect", (com.mojang.brigadier.arguments.ArgumentType<Object>)MobEffectArgumentType.create()).executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"))))))).then(CommandManager.literal("give").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("effect", (com.mojang.brigadier.arguments.ArgumentType<Object>)MobEffectArgumentType.create()).executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("seconds", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 1000000)).executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("amplifier", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 255)).executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), true))).then(CommandManager.argument("hideParticles", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), !BoolArgumentType.getBool(commandContext, "hideParticles"))))))))));
    }
    
    private static int executeGive(final ServerCommandSource source, final Collection<? extends Entity> targets, final StatusEffect effect, @Nullable final Integer seconds, final int amplifier, final boolean showParticles) throws CommandSyntaxException {
        int integer7 = 0;
        int integer8;
        if (seconds != null) {
            if (effect.isInstant()) {
                integer8 = seconds;
            }
            else {
                integer8 = seconds * 20;
            }
        }
        else if (effect.isInstant()) {
            integer8 = 1;
        }
        else {
            integer8 = 600;
        }
        for (final Entity entity10 : targets) {
            if (entity10 instanceof LivingEntity) {
                final StatusEffectInstance statusEffectInstance11 = new StatusEffectInstance(effect, integer8, amplifier, false, showParticles);
                if (!((LivingEntity)entity10).addPotionEffect(statusEffectInstance11)) {
                    continue;
                }
                ++integer7;
            }
        }
        if (integer7 == 0) {
            throw EffectCommand.GIVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.give.success.single", new Object[] { effect.d(), ((Entity)targets.iterator().next()).getDisplayName(), integer8 / 20 }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.give.success.multiple", new Object[] { effect.d(), targets.size(), integer8 / 20 }), true);
        }
        return integer7;
    }
    
    private static int executeClear(final ServerCommandSource source, final Collection<? extends Entity> targets) throws CommandSyntaxException {
        int integer3 = 0;
        for (final Entity entity5 : targets) {
            if (entity5 instanceof LivingEntity && ((LivingEntity)entity5).clearPotionEffects()) {
                ++integer3;
            }
        }
        if (integer3 == 0) {
            throw EffectCommand.CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.clear.everything.success.single", new Object[] { ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.clear.everything.success.multiple", new Object[] { targets.size() }), true);
        }
        return integer3;
    }
    
    private static int executeClear(final ServerCommandSource source, final Collection<? extends Entity> targets, final StatusEffect effect) throws CommandSyntaxException {
        int integer4 = 0;
        for (final Entity entity6 : targets) {
            if (entity6 instanceof LivingEntity && ((LivingEntity)entity6).removeStatusEffect(effect)) {
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw EffectCommand.CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.clear.specific.success.single", new Object[] { effect.d(), ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.effect.clear.specific.success.multiple", new Object[] { effect.d(), targets.size() }), true);
        }
        return integer4;
    }
    
    static {
        GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.effect.give.failed", new Object[0]));
        CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.effect.clear.everything.failed", new Object[0]));
        CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.effect.clear.specific.failed", new Object[0]));
    }
}
