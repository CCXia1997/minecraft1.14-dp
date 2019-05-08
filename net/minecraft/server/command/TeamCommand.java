package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.Lists;
import net.minecraft.text.TextFormat;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import java.util.Iterator;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Collection;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.command.arguments.ColorArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ComponentArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TeamCommand
{
    private static final SimpleCommandExceptionType ADD_DUPLICATE_EXCEPTION;
    private static final DynamicCommandExceptionType ADD_LONGNAME_EXCEPTION;
    private static final SimpleCommandExceptionType EMPTY_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_NAME_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_COLOR_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION;
    private static final SimpleCommandExceptionType SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("team").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> executeListTeams((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgumentType.create()).executes(commandContext -> executeListMembers((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "team")))).then(CommandManager.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "displayName"))))))).then(CommandManager.literal("remove").then(CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgumentType.create()).executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("empty").then(CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgumentType.create()).executes(commandContext -> executeEmpty((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("join").then(((RequiredArgumentBuilder)CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgumentType.create()).executes(commandContext -> executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), Collections.<String>singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow().getEntityName())))).then(CommandManager.argument("members", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members"))))))).then(CommandManager.literal("leave").then(CommandManager.argument("members", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> executeLeave((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members")))))).then(CommandManager.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgumentType.create()).then(CommandManager.literal("displayName").then(CommandManager.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeModifyDisplayName((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "displayName")))))).then(CommandManager.literal("color").then(CommandManager.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColorArgumentType.create()).executes(commandContext -> executeModifyColor((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ColorArgumentType.getColor((CommandContext<ServerCommandSource>)commandContext, "value")))))).then(CommandManager.literal("friendlyFire").then(CommandManager.argument("allowed", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> executeModifyFriendlyFire((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")))))).then(CommandManager.literal("seeFriendlyInvisibles").then(CommandManager.argument("allowed", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> executeModifySeeFriendlyInvisibles((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("nametagVisibility").then(CommandManager.literal("never").executes(commandContext -> executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("deathMessageVisibility").then(CommandManager.literal("never").executes(commandContext -> executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("collisionRule").then(CommandManager.literal("never").executes(commandContext -> executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.b)))).then(CommandManager.literal("pushOwnTeam").executes(commandContext -> executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.d)))).then(CommandManager.literal("pushOtherTeams").executes(commandContext -> executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.c)))).then(CommandManager.literal("always").executes(commandContext -> executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.ALWAYS))))).then(CommandManager.literal("prefix").then(CommandManager.argument("prefix", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeModifyPrefix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "prefix")))))).then(CommandManager.literal("suffix").then(CommandManager.argument("suffix", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> executeModifySuffix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "suffix"))))))));
    }
    
    private static int executeLeave(final ServerCommandSource source, final Collection<String> members) {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        for (final String string5 : members) {
            scoreboard3.clearPlayerTeam(string5);
        }
        if (members.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.team.leave.success.single", new Object[] { members.iterator().next() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.team.leave.success.multiple", new Object[] { members.size() }), true);
        }
        return members.size();
    }
    
    private static int executeJoin(final ServerCommandSource source, final Team team, final Collection<String> members) {
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        for (final String string6 : members) {
            scoreboard4.addPlayerToTeam(string6, team);
        }
        if (members.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.team.join.success.single", new Object[] { members.iterator().next(), team.getFormattedName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.team.join.success.multiple", new Object[] { members.size(), team.getFormattedName() }), true);
        }
        return members.size();
    }
    
    private static int executeModifyNametagVisibility(final ServerCommandSource source, final Team team, final AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
        if (team.getNameTagVisibilityRule() == visibility) {
            throw TeamCommand.OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        team.setNameTagVisibilityRule(visibility);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.nametagVisibility.success", new Object[] { team.getFormattedName(), visibility.getTranslationKey() }), true);
        return 0;
    }
    
    private static int executeModifyDeathMessageVisibility(final ServerCommandSource source, final Team team, final AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
        if (team.getDeathMessageVisibilityRule() == visibility) {
            throw TeamCommand.OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        team.setDeathMessageVisibilityRule(visibility);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.deathMessageVisibility.success", new Object[] { team.getFormattedName(), visibility.getTranslationKey() }), true);
        return 0;
    }
    
    private static int executeModifyCollisionRule(final ServerCommandSource source, final Team team, final AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
        if (team.getCollisionRule() == collisionRule) {
            throw TeamCommand.OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION.create();
        }
        team.setCollisionRule(collisionRule);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.collisionRule.success", new Object[] { team.getFormattedName(), collisionRule.getTranslationKey() }), true);
        return 0;
    }
    
    private static int executeModifySeeFriendlyInvisibles(final ServerCommandSource source, final Team team, final boolean allowed) throws CommandSyntaxException {
        if (team.shouldShowFriendlyInvisibles() != allowed) {
            team.setShowFriendlyInvisibles(allowed);
            source.sendFeedback(new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles." + (allowed ? "enabled" : "disabled"), new Object[] { team.getFormattedName() }), true);
            return 0;
        }
        if (allowed) {
            throw TeamCommand.OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION.create();
        }
        throw TeamCommand.SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION.create();
    }
    
    private static int executeModifyFriendlyFire(final ServerCommandSource source, final Team team, final boolean allowed) throws CommandSyntaxException {
        if (team.isFriendlyFireAllowed() != allowed) {
            team.setFriendlyFireAllowed(allowed);
            source.sendFeedback(new TranslatableTextComponent("commands.team.option.friendlyfire." + (allowed ? "enabled" : "disabled"), new Object[] { team.getFormattedName() }), true);
            return 0;
        }
        if (allowed) {
            throw TeamCommand.OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION.create();
        }
        throw TeamCommand.OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION.create();
    }
    
    private static int executeModifyDisplayName(final ServerCommandSource source, final Team team, final TextComponent displayName) throws CommandSyntaxException {
        if (team.getDisplayName().equals(displayName)) {
            throw TeamCommand.OPTION_NAME_UNCHANGED_EXCEPTION.create();
        }
        team.setDisplayName(displayName);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.name.success", new Object[] { team.getFormattedName() }), true);
        return 0;
    }
    
    private static int executeModifyColor(final ServerCommandSource source, final Team team, final TextFormat color) throws CommandSyntaxException {
        if (team.getColor() == color) {
            throw TeamCommand.OPTION_COLOR_UNCHANGED_EXCEPTION.create();
        }
        team.setColor(color);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.color.success", new Object[] { team.getFormattedName(), color.getName() }), true);
        return 0;
    }
    
    private static int executeEmpty(final ServerCommandSource source, final Team team) throws CommandSyntaxException {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        final Collection<String> collection4 = Lists.newArrayList(team.getPlayerList());
        if (collection4.isEmpty()) {
            throw TeamCommand.EMPTY_UNCHANGED_EXCEPTION.create();
        }
        for (final String string6 : collection4) {
            scoreboard3.removePlayerFromTeam(string6, team);
        }
        source.sendFeedback(new TranslatableTextComponent("commands.team.empty.success", new Object[] { collection4.size(), team.getFormattedName() }), true);
        return collection4.size();
    }
    
    private static int executeRemove(final ServerCommandSource source, final Team team) {
        final Scoreboard scoreboard3 = source.getMinecraftServer().getScoreboard();
        scoreboard3.removeTeam(team);
        source.sendFeedback(new TranslatableTextComponent("commands.team.remove.success", new Object[] { team.getFormattedName() }), true);
        return scoreboard3.getTeams().size();
    }
    
    private static int executeAdd(final ServerCommandSource source, final String team) throws CommandSyntaxException {
        return executeAdd(source, team, new StringTextComponent(team));
    }
    
    private static int executeAdd(final ServerCommandSource source, final String team, final TextComponent displayName) throws CommandSyntaxException {
        final Scoreboard scoreboard4 = source.getMinecraftServer().getScoreboard();
        if (scoreboard4.getTeam(team) != null) {
            throw TeamCommand.ADD_DUPLICATE_EXCEPTION.create();
        }
        if (team.length() > 16) {
            throw TeamCommand.ADD_LONGNAME_EXCEPTION.create(16);
        }
        final Team team2 = scoreboard4.addTeam(team);
        team2.setDisplayName(displayName);
        source.sendFeedback(new TranslatableTextComponent("commands.team.add.success", new Object[] { team2.getFormattedName() }), true);
        return scoreboard4.getTeams().size();
    }
    
    private static int executeListMembers(final ServerCommandSource source, final Team team) {
        final Collection<String> collection3 = team.getPlayerList();
        if (collection3.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.team.list.members.empty", new Object[] { team.getFormattedName() }), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.team.list.members.success", new Object[] { team.getFormattedName(), collection3.size(), TextFormatter.sortedJoin(collection3) }), false);
        }
        return collection3.size();
    }
    
    private static int executeListTeams(final ServerCommandSource source) {
        final Collection<Team> collection2 = source.getMinecraftServer().getScoreboard().getTeams();
        if (collection2.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.team.list.teams.empty", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.team.list.teams.success", new Object[] { collection2.size(), TextFormatter.<Team>join(collection2, Team::getFormattedName) }), false);
        }
        return collection2.size();
    }
    
    private static int executeModifyPrefix(final ServerCommandSource source, final Team team, final TextComponent prefix) {
        team.setPrefix(prefix);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.prefix.success", new Object[] { prefix }), false);
        return 1;
    }
    
    private static int executeModifySuffix(final ServerCommandSource source, final Team team, final TextComponent suffix) {
        team.setSuffix(suffix);
        source.sendFeedback(new TranslatableTextComponent("commands.team.option.suffix.success", new Object[] { suffix }), false);
        return 1;
    }
    
    static {
        ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.add.duplicate", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        ADD_LONGNAME_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.team.add.longName", new Object[] { object });
            return translatableTextComponent;
        });
        EMPTY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.empty.unchanged", new Object[0]));
        OPTION_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.name.unchanged", new Object[0]));
        OPTION_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.color.unchanged", new Object[0]));
        OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.friendlyfire.alreadyEnabled", new Object[0]));
        OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.friendlyfire.alreadyDisabled", new Object[0]));
        OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled", new Object[0]));
        SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled", new Object[0]));
        OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.nametagVisibility.unchanged", new Object[0]));
        OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.deathMessageVisibility.unchanged", new Object[0]));
        OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.team.option.collisionRule.unchanged", new Object[0]));
    }
}
