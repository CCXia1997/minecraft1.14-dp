package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import net.minecraft.text.StringTextComponent;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.text.TextFormatter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Iterator;
import net.minecraft.scoreboard.Scoreboard;
import java.util.List;
import net.minecraft.scoreboard.ScoreboardObjective;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.command.arguments.OperationArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ScoreboardCommand
{
    private static final SimpleCommandExceptionType OBJECTIVES_ADD_DUPLICATE_EXCEPTION;
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION;
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION;
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_INVALID_EXCEPTION;
    private static final Dynamic2CommandExceptionType PLAYERS_GET_NULL_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("scoreboard").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("objectives").then(CommandManager.literal("list").executes(commandContext -> executeListObjectives((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).then(((RequiredArgumentBuilder)CommandManager.argument("criteria", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveCriteriaArgumentType.create()).executes(commandContext -> executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgumentType.getCriteria((CommandContext<ServerCommandSource>)commandContext, "criteria"), new StringTextComponent(StringArgumentType.getString(commandContext, "objective"))))).then(CommandManager.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgumentType.getCriteria((CommandContext<ServerCommandSource>)commandContext, "criteria"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "displayName")))))))).then(CommandManager.literal("modify").then(((RequiredArgumentBuilder)CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.literal("displayname").then(CommandManager.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeModifyObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "displayName")))))).then((ArgumentBuilder)makeRenderTypeArguments())))).then(CommandManager.literal("remove").then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).executes(commandContext -> executeRemoveObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")))))).then(CommandManager.literal("setdisplay").then(((RequiredArgumentBuilder)CommandManager.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreboardSlotArgumentType.create()).executes(commandContext -> executeClearDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlot((CommandContext<ServerCommandSource>)commandContext, "slot")))).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).executes(commandContext -> executeSetDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("players").then(((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> executeListPlayers((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> executeListScores((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder((CommandContext<ServerCommandSource>)commandContext, "target")))))).then(CommandManager.literal("set").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("get").then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).executes(commandContext -> executeGet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder((CommandContext<ServerCommandSource>)commandContext, "target"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("add").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("reset").then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets")))).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).executes(commandContext -> executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("enable").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> suggestDisabled((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), suggestionsBuilder)).executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("operation").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("targetObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.argument("operation", (com.mojang.brigadier.arguments.ArgumentType<Object>)OperationArgumentType.create()).then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).executes(commandContext -> executeOperation((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "targetObjective"), OperationArgumentType.getOperation((CommandContext<ServerCommandSource>)commandContext, "operation"), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "source"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "sourceObjective")))))))))));
    }
    
    private static LiteralArgumentBuilder<ServerCommandSource> makeRenderTypeArguments() {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder1 = CommandManager.literal("rendertype");
        for (final ScoreboardCriterion.RenderType renderType5 : ScoreboardCriterion.RenderType.values()) {
            literalArgumentBuilder1.then(CommandManager.literal(renderType5.getName()).executes(commandContext -> executeModifyRenderType((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), renderType5)));
        }
        return literalArgumentBuilder1;
    }
    
    private static CompletableFuture<Suggestions> suggestDisabled(final ServerCommandSource source, final Collection<String> targets, final SuggestionsBuilder suggestionsBuilder) {
        final List<String> list4 = Lists.newArrayList();
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        for (final ScoreboardObjective scoreboardObjective7 : scoreboard5.getObjectives()) {
            if (scoreboardObjective7.getCriterion() == ScoreboardCriterion.TRIGGER) {
                boolean boolean8 = false;
                for (final String string10 : targets) {
                    if (!scoreboard5.playerHasObjective(string10, scoreboardObjective7) || scoreboard5.getPlayerScore(string10, scoreboardObjective7).isLocked()) {
                        boolean8 = true;
                        break;
                    }
                }
                if (!boolean8) {
                    continue;
                }
                list4.add(scoreboardObjective7.getName());
            }
        }
        return CommandSource.suggestMatching(list4, suggestionsBuilder);
    }
    
    private static int executeGet(final ServerCommandSource source, final String target, final ScoreboardObjective objective) throws CommandSyntaxException {
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        if (!scoreboard4.playerHasObjective(target, objective)) {
            throw ScoreboardCommand.PLAYERS_GET_NULL_EXCEPTION.create(objective.getName(), target);
        }
        final ScoreboardPlayerScore scoreboardPlayerScore5 = scoreboard4.getPlayerScore(target, objective);
        source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.get.success", new Object[] { target, scoreboardPlayerScore5.getScore(), objective.getTextComponent() }), false);
        return scoreboardPlayerScore5.getScore();
    }
    
    private static int executeOperation(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective targetObjective, final OperationArgumentType.Operation operation, final Collection<String> sources, final ScoreboardObjective sourceObjectives) throws CommandSyntaxException {
        final Scoreboard scoreboard7 = source.getMinecraftServer().getScoreboard();
        int integer8 = 0;
        for (final String string10 : targets) {
            final ScoreboardPlayerScore scoreboardPlayerScore11 = scoreboard7.getPlayerScore(string10, targetObjective);
            for (final String string11 : sources) {
                final ScoreboardPlayerScore scoreboardPlayerScore12 = scoreboard7.getPlayerScore(string11, sourceObjectives);
                operation.apply(scoreboardPlayerScore11, scoreboardPlayerScore12);
            }
            integer8 += scoreboardPlayerScore11.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.operation.success.single", new Object[] { targetObjective.getTextComponent(), targets.iterator().next(), integer8 }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.operation.success.multiple", new Object[] { targetObjective.getTextComponent(), targets.size() }), true);
        }
        return integer8;
    }
    
    private static int executeEnable(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective) throws CommandSyntaxException {
        if (objective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw ScoreboardCommand.PLAYERS_ENABLE_INVALID_EXCEPTION.create();
        }
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        int integer5 = 0;
        for (final String string7 : targets) {
            final ScoreboardPlayerScore scoreboardPlayerScore8 = scoreboard4.getPlayerScore(string7, objective);
            if (scoreboardPlayerScore8.isLocked()) {
                scoreboardPlayerScore8.setLocked(false);
                ++integer5;
            }
        }
        if (integer5 == 0) {
            throw ScoreboardCommand.PLAYERS_ENABLE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.enable.success.single", new Object[] { objective.getTextComponent(), targets.iterator().next() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.enable.success.multiple", new Object[] { objective.getTextComponent(), targets.size() }), true);
        }
        return integer5;
    }
    
    private static int executeReset(final ServerCommandSource source, final Collection<String> targets) {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        for (final String string5 : targets) {
            scoreboard3.resetPlayerScore(string5, null);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.reset.all.single", new Object[] { targets.iterator().next() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.reset.all.multiple", new Object[] { targets.size() }), true);
        }
        return targets.size();
    }
    
    private static int executeReset(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective) {
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        for (final String string6 : targets) {
            scoreboard4.resetPlayerScore(string6, objective);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.reset.specific.single", new Object[] { objective.getTextComponent(), targets.iterator().next() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.reset.specific.multiple", new Object[] { objective.getTextComponent(), targets.size() }), true);
        }
        return targets.size();
    }
    
    private static int executeSet(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective, final int score) {
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        for (final String string7 : targets) {
            final ScoreboardPlayerScore scoreboardPlayerScore8 = scoreboard5.getPlayerScore(string7, objective);
            scoreboardPlayerScore8.setScore(score);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.set.success.single", new Object[] { objective.getTextComponent(), targets.iterator().next(), score }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.set.success.multiple", new Object[] { objective.getTextComponent(), targets.size(), score }), true);
        }
        return score * targets.size();
    }
    
    private static int executeAdd(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective, final int score) {
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        int integer6 = 0;
        for (final String string8 : targets) {
            final ScoreboardPlayerScore scoreboardPlayerScore9 = scoreboard5.getPlayerScore(string8, objective);
            scoreboardPlayerScore9.setScore(scoreboardPlayerScore9.getScore() + score);
            integer6 += scoreboardPlayerScore9.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.add.success.single", new Object[] { score, objective.getTextComponent(), targets.iterator().next(), integer6 }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.add.success.multiple", new Object[] { score, objective.getTextComponent(), targets.size() }), true);
        }
        return integer6;
    }
    
    private static int executeRemove(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective, final int score) {
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        int integer6 = 0;
        for (final String string8 : targets) {
            final ScoreboardPlayerScore scoreboardPlayerScore9 = scoreboard5.getPlayerScore(string8, objective);
            scoreboardPlayerScore9.setScore(scoreboardPlayerScore9.getScore() - score);
            integer6 += scoreboardPlayerScore9.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.remove.success.single", new Object[] { score, objective.getTextComponent(), targets.iterator().next(), integer6 }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.remove.success.multiple", new Object[] { score, objective.getTextComponent(), targets.size() }), true);
        }
        return integer6;
    }
    
    private static int executeListPlayers(final ServerCommandSource source) {
        final Collection<String> collection2 = source.getMinecraftServer().getScoreboard().getKnownPlayers();
        if (collection2.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.list.empty", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.list.success", new Object[] { collection2.size(), TextFormatter.sortedJoin(collection2) }), false);
        }
        return collection2.size();
    }
    
    private static int executeListScores(final ServerCommandSource source, final String target) {
        final Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = source.getMinecraftServer().getScoreboard().getPlayerObjectives(target);
        if (map3.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.list.entity.empty", new Object[] { target }), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.list.entity.success", new Object[] { target, map3.size() }), false);
            for (final Map.Entry<ScoreboardObjective, ScoreboardPlayerScore> entry5 : map3.entrySet()) {
                source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.players.list.entity.entry", new Object[] { entry5.getKey().getTextComponent(), entry5.getValue().getScore() }), false);
            }
        }
        return map3.size();
    }
    
    private static int executeClearDisplay(final ServerCommandSource source, final int slot) throws CommandSyntaxException {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        if (scoreboard3.getObjectiveForSlot(slot) == null) {
            throw ScoreboardCommand.OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION.create();
        }
        scoreboard3.setObjectiveSlot(slot, null);
        source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.display.cleared", new Object[] { Scoreboard.getDisplaySlotNames()[slot] }), true);
        return 0;
    }
    
    private static int executeSetDisplay(final ServerCommandSource source, final int slot, final ScoreboardObjective objective) throws CommandSyntaxException {
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        if (scoreboard4.getObjectiveForSlot(slot) == objective) {
            throw ScoreboardCommand.OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION.create();
        }
        scoreboard4.setObjectiveSlot(slot, objective);
        source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.display.set", new Object[] { Scoreboard.getDisplaySlotNames()[slot], objective.getDisplayName() }), true);
        return 0;
    }
    
    private static int executeModifyObjective(final ServerCommandSource source, final ScoreboardObjective objective, final TextComponent displayName) {
        if (!objective.getDisplayName().equals(displayName)) {
            objective.setDisplayName(displayName);
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.modify.displayname", new Object[] { objective.getName(), objective.getTextComponent() }), true);
        }
        return 0;
    }
    
    private static int executeModifyRenderType(final ServerCommandSource source, final ScoreboardObjective objective, final ScoreboardCriterion.RenderType type) {
        if (objective.getRenderType() != type) {
            objective.setRenderType(type);
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.modify.rendertype", new Object[] { objective.getTextComponent() }), true);
        }
        return 0;
    }
    
    private static int executeRemoveObjective(final ServerCommandSource source, final ScoreboardObjective objective) {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        scoreboard3.removeObjective(objective);
        source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.remove.success", new Object[] { objective.getTextComponent() }), true);
        return scoreboard3.getObjectives().size();
    }
    
    private static int executeAddObjective(final ServerCommandSource source, final String objective, final ScoreboardCriterion criteria, final TextComponent displayName) throws CommandSyntaxException {
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        if (scoreboard5.getNullableObjective(objective) != null) {
            throw ScoreboardCommand.OBJECTIVES_ADD_DUPLICATE_EXCEPTION.create();
        }
        if (objective.length() > 16) {
            throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create(16);
        }
        scoreboard5.addObjective(objective, criteria, displayName, criteria.getCriterionType());
        final ScoreboardObjective scoreboardObjective6 = scoreboard5.getNullableObjective(objective);
        source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.add.success", new Object[] { scoreboardObjective6.getTextComponent() }), true);
        return scoreboard5.getObjectives().size();
    }
    
    private static int executeListObjectives(final ServerCommandSource source) {
        final Collection<ScoreboardObjective> collection2 = source.getMinecraftServer().getScoreboard().getObjectives();
        if (collection2.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.list.empty", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.scoreboard.objectives.list.success", new Object[] { collection2.size(), TextFormatter.<ScoreboardObjective>join(collection2, ScoreboardObjective::getTextComponent) }), false);
        }
        return collection2.size();
    }
    
    static {
        OBJECTIVES_ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.scoreboard.objectives.add.duplicate", new Object[0]));
        OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
        OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
        PLAYERS_ENABLE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.scoreboard.players.enable.failed", new Object[0]));
        PLAYERS_ENABLE_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.scoreboard.players.enable.invalid", new Object[0]));
        PLAYERS_GET_NULL_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.scoreboard.players.get.null", new Object[] { object1, object2 }));
    }
}
