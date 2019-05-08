package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import java.util.Iterator;
import net.minecraft.scoreboard.Scoreboard;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TriggerCommand
{
    private static final SimpleCommandExceptionType FAILED_UMPRIMED_EXCEPTION;
    private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> suggestObjectives((ServerCommandSource)commandContext.getSource(), suggestionsBuilder)).executes(commandContext -> executeSimple((ServerCommandSource)commandContext.getSource(), getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))).then(CommandManager.literal("add").then(CommandManager.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value")))))).then(CommandManager.literal("set").then(CommandManager.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value")))))));
    }
    
    public static CompletableFuture<Suggestions> suggestObjectives(final ServerCommandSource source, final SuggestionsBuilder suggestionsBuilder) {
        final Entity entity3 = source.getEntity();
        final List<String> list4 = Lists.newArrayList();
        if (entity3 != null) {
            final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
            final String string6 = entity3.getEntityName();
            for (final ScoreboardObjective scoreboardObjective8 : scoreboard5.getObjectives()) {
                if (scoreboardObjective8.getCriterion() == ScoreboardCriterion.TRIGGER && scoreboard5.playerHasObjective(string6, scoreboardObjective8)) {
                    final ScoreboardPlayerScore scoreboardPlayerScore9 = scoreboard5.getPlayerScore(string6, scoreboardObjective8);
                    if (scoreboardPlayerScore9.isLocked()) {
                        continue;
                    }
                    list4.add(scoreboardObjective8.getName());
                }
            }
        }
        return CommandSource.suggestMatching(list4, suggestionsBuilder);
    }
    
    private static int executeAdd(final ServerCommandSource source, final ScoreboardPlayerScore score, final int value) {
        score.incrementScore(value);
        source.sendFeedback(new TranslatableTextComponent("commands.trigger.add.success", new Object[] { score.getObjective().getTextComponent(), value }), true);
        return score.getScore();
    }
    
    private static int executeSet(final ServerCommandSource serverCommandSource, final ScoreboardPlayerScore scoreboardPlayerScore, final int value) {
        scoreboardPlayerScore.setScore(value);
        serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.trigger.set.success", new Object[] { scoreboardPlayerScore.getObjective().getTextComponent(), value }), true);
        return value;
    }
    
    private static int executeSimple(final ServerCommandSource source, final ScoreboardPlayerScore score) {
        score.incrementScore(1);
        source.sendFeedback(new TranslatableTextComponent("commands.trigger.simple.success", new Object[] { score.getObjective().getTextComponent() }), true);
        return score.getScore();
    }
    
    private static ScoreboardPlayerScore getScore(final ServerPlayerEntity serverPlayerEntity, final ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
        if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw TriggerCommand.FAILED_INVALID_EXCEPTION.create();
        }
        final Scoreboard scoreboard3 = serverPlayerEntity.getScoreboard();
        final String string4 = serverPlayerEntity.getEntityName();
        if (!scoreboard3.playerHasObjective(string4, scoreboardObjective)) {
            throw TriggerCommand.FAILED_UMPRIMED_EXCEPTION.create();
        }
        final ScoreboardPlayerScore scoreboardPlayerScore5 = scoreboard3.getPlayerScore(string4, scoreboardObjective);
        if (scoreboardPlayerScore5.isLocked()) {
            throw TriggerCommand.FAILED_UMPRIMED_EXCEPTION.create();
        }
        scoreboardPlayerScore5.setLocked(true);
        return scoreboardPlayerScore5;
    }
    
    static {
        FAILED_UMPRIMED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.trigger.failed.unprimed", new Object[0]));
        FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.trigger.failed.invalid", new Object[0]));
    }
}
