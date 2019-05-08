package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.util.Identifier;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextFormatter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.arguments.ComponentArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class BossBarCommand
{
    private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION;
    private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION;
    private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SETMAX_UNCHANGED_EXCEPTION;
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION;
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION;
    public static final SuggestionProvider<ServerCommandSource> suggestionProvider;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("bossbar").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then(CommandManager.argument("id", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> addBossBar((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "id"), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "name"))))))).then(CommandManager.literal("remove").then(CommandManager.argument("id", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)BossBarCommand.suggestionProvider).executes(commandContext -> removeBossBar((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext)))))).then(CommandManager.literal("list").executes(commandContext -> listBossBars((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)BossBarCommand.suggestionProvider).then(CommandManager.literal("name").then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgumentType.create()).executes(commandContext -> setName((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), ComponentArgumentType.getComponent((CommandContext<ServerCommandSource>)commandContext, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("color").then(CommandManager.literal("pink").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.a)))).then(CommandManager.literal("blue").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.b)))).then(CommandManager.literal("red").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.c)))).then(CommandManager.literal("green").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.d)))).then(CommandManager.literal("yellow").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.e)))).then(CommandManager.literal("purple").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.f)))).then(CommandManager.literal("white").executes(commandContext -> setColor((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.g))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("style").then(CommandManager.literal("progress").executes(commandContext -> setStyle((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.a)))).then(CommandManager.literal("notched_6").executes(commandContext -> setStyle((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.b)))).then(CommandManager.literal("notched_10").executes(commandContext -> setStyle((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.c)))).then(CommandManager.literal("notched_12").executes(commandContext -> setStyle((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.d)))).then(CommandManager.literal("notched_20").executes(commandContext -> setStyle((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.e))))).then(CommandManager.literal("value").then(CommandManager.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> setValue((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), IntegerArgumentType.getInteger(commandContext, "value")))))).then(CommandManager.literal("max").then(CommandManager.argument("max", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1)).executes(commandContext -> setMaxValue((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), IntegerArgumentType.getInteger(commandContext, "max")))))).then(CommandManager.literal("visible").then(CommandManager.argument("visible", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> setVisible((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), BoolArgumentType.getBool(commandContext, "visible")))))).then(((LiteralArgumentBuilder)CommandManager.literal("players").executes(commandContext -> setPlayers((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), Collections.emptyList()))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> setPlayers((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext), EntityArgumentType.getOptionalPlayers((CommandContext<ServerCommandSource>)commandContext, "targets")))))))).then(CommandManager.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)BossBarCommand.suggestionProvider).then(CommandManager.literal("value").executes(commandContext -> getValue((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("max").executes(commandContext -> getMaxValue((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("visible").executes(commandContext -> isVisible((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("players").executes(commandContext -> getPlayers((ServerCommandSource)commandContext.getSource(), createBossBar((CommandContext<ServerCommandSource>)commandContext)))))));
    }
    
    private static int getValue(final ServerCommandSource source, final CommandBossBar bossBar) {
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.value", new Object[] { bossBar.getTextComponent(), bossBar.getValue() }), true);
        return bossBar.getValue();
    }
    
    private static int getMaxValue(final ServerCommandSource source, final CommandBossBar bossBar) {
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.max", new Object[] { bossBar.getTextComponent(), bossBar.getMaxValue() }), true);
        return bossBar.getMaxValue();
    }
    
    private static int isVisible(final ServerCommandSource source, final CommandBossBar bossBar) {
        if (bossBar.isVisible()) {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.visible.visible", new Object[] { bossBar.getTextComponent() }), true);
            return 1;
        }
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.visible.hidden", new Object[] { bossBar.getTextComponent() }), true);
        return 0;
    }
    
    private static int getPlayers(final ServerCommandSource source, final CommandBossBar bossBar) {
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.players.none", new Object[] { bossBar.getTextComponent() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.players.some", new Object[] { bossBar.getTextComponent(), bossBar.getPlayers().size(), TextFormatter.<ServerPlayerEntity>join(bossBar.getPlayers(), PlayerEntity::getDisplayName) }), true);
        }
        return bossBar.getPlayers().size();
    }
    
    private static int setVisible(final ServerCommandSource source, final CommandBossBar bossBar, final boolean visible) throws CommandSyntaxException {
        if (bossBar.isVisible() != visible) {
            bossBar.setVisible(visible);
            if (visible) {
                source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.visible.success.visible", new Object[] { bossBar.getTextComponent() }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.visible.success.hidden", new Object[] { bossBar.getTextComponent() }), true);
            }
            return 0;
        }
        if (visible) {
            throw BossBarCommand.SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
        }
        throw BossBarCommand.SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
    }
    
    private static int setValue(final ServerCommandSource source, final CommandBossBar bossBar, final int value) throws CommandSyntaxException {
        if (bossBar.getValue() == value) {
            throw BossBarCommand.SET_VALUE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setValue(value);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.value.success", new Object[] { bossBar.getTextComponent(), value }), true);
        return value;
    }
    
    private static int setMaxValue(final ServerCommandSource source, final CommandBossBar bossBar, final int value) throws CommandSyntaxException {
        if (bossBar.getMaxValue() == value) {
            throw BossBarCommand.SETMAX_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setMaxValue(value);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.max.success", new Object[] { bossBar.getTextComponent(), value }), true);
        return value;
    }
    
    private static int setColor(final ServerCommandSource source, final CommandBossBar bossBar, final BossBar.Color color) throws CommandSyntaxException {
        if (bossBar.getColor().equals(color)) {
            throw BossBarCommand.SET_COLOR_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setColor(color);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.color.success", new Object[] { bossBar.getTextComponent() }), true);
        return 0;
    }
    
    private static int setStyle(final ServerCommandSource source, final CommandBossBar bossBar, final BossBar.Style style) throws CommandSyntaxException {
        if (bossBar.getOverlay().equals(style)) {
            throw BossBarCommand.SET_STYLE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setOverlay(style);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.style.success", new Object[] { bossBar.getTextComponent() }), true);
        return 0;
    }
    
    private static int setName(final ServerCommandSource source, final CommandBossBar bossBar, final TextComponent name) throws CommandSyntaxException {
        final TextComponent textComponent4 = TextFormatter.resolveAndStyle(source, name, null);
        if (bossBar.getName().equals(textComponent4)) {
            throw BossBarCommand.SET_NAME_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setName(textComponent4);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.name.success", new Object[] { bossBar.getTextComponent() }), true);
        return 0;
    }
    
    private static int setPlayers(final ServerCommandSource source, final CommandBossBar bossBar, final Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        final boolean boolean4 = bossBar.addPlayers(players);
        if (!boolean4) {
            throw BossBarCommand.SET_PLAYERS_UNCHANGED_EXCEPTION.create();
        }
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.players.success.none", new Object[] { bossBar.getTextComponent() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.players.success.some", new Object[] { bossBar.getTextComponent(), players.size(), TextFormatter.<ServerPlayerEntity>join(players, PlayerEntity::getDisplayName) }), true);
        }
        return bossBar.getPlayers().size();
    }
    
    private static int listBossBars(final ServerCommandSource source) {
        final Collection<CommandBossBar> collection2 = source.getMinecraftServer().getBossBarManager().getAll();
        if (collection2.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.list.bars.none", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.bossbar.list.bars.some", new Object[] { collection2.size(), TextFormatter.<CommandBossBar>join(collection2, CommandBossBar::getTextComponent) }), false);
        }
        return collection2.size();
    }
    
    private static int addBossBar(final ServerCommandSource source, final Identifier name, final TextComponent displayName) throws CommandSyntaxException {
        final BossBarManager bossBarManager4 = source.getMinecraftServer().getBossBarManager();
        if (bossBarManager4.get(name) != null) {
            throw BossBarCommand.CREATE_FAILED_EXCEPTION.create(name.toString());
        }
        final CommandBossBar commandBossBar5 = bossBarManager4.add(name, TextFormatter.resolveAndStyle(source, displayName, null));
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.create.success", new Object[] { commandBossBar5.getTextComponent() }), true);
        return bossBarManager4.getAll().size();
    }
    
    private static int removeBossBar(final ServerCommandSource source, final CommandBossBar bossBar) {
        final BossBarManager bossBarManager3 = source.getMinecraftServer().getBossBarManager();
        bossBar.clearPlayers();
        bossBarManager3.remove(bossBar);
        source.sendFeedback(new TranslatableTextComponent("commands.bossbar.remove.success", new Object[] { bossBar.getTextComponent() }), true);
        return bossBarManager3.getAll().size();
    }
    
    public static CommandBossBar createBossBar(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final Identifier identifier2 = IdentifierArgumentType.getIdentifier(context, "id");
        final CommandBossBar commandBossBar3 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getBossBarManager().get(identifier2);
        if (commandBossBar3 == null) {
            throw BossBarCommand.UNKNOWN_EXCEPTION.create(identifier2.toString());
        }
        return commandBossBar3;
    }
    
    static {
        final TranslatableTextComponent translatableTextComponent;
        CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.bossbar.create.failed", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.bossbar.unknown", new Object[] { object });
            return translatableTextComponent2;
        });
        SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.players.unchanged", new Object[0]));
        SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.name.unchanged", new Object[0]));
        SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.color.unchanged", new Object[0]));
        SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.style.unchanged", new Object[0]));
        SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.value.unchanged", new Object[0]));
        SETMAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.max.unchanged", new Object[0]));
        SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
        SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
        suggestionProvider = ((commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getBossBarManager().getIds(), suggestionsBuilder));
    }
}
