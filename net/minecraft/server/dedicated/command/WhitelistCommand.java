package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import com.mojang.brigadier.Message;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandSource;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigEntry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.server.Whitelist;
import net.minecraft.text.TextFormatter;
import net.minecraft.server.WhitelistEntry;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class WhitelistCommand
{
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION;
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION;
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("on").executes(commandContext -> executeOn((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("off").executes(commandContext -> executeOff((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("list").executes(commandContext -> executeList((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> {
            final PlayerManager playerManager3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager3.getPlayerList().stream().filter(serverPlayerEntity -> !playerManager3.getWhitelist().isAllowed(serverPlayerEntity.getGameProfile())).<String>map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getWhitelistedNames(), suggestionsBuilder)).executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))))).then(CommandManager.literal("reload").executes(commandContext -> executeReload((ServerCommandSource)commandContext.getSource()))));
    }
    
    private static int executeReload(final ServerCommandSource source) {
        source.getMinecraftServer().getPlayerManager().reloadWhitelist();
        source.sendFeedback(new TranslatableTextComponent("commands.whitelist.reloaded", new Object[0]), true);
        source.getMinecraftServer().kickNonWhitelistedPlayers(source);
        return 1;
    }
    
    private static int executeAdd(final ServerCommandSource source, final Collection<GameProfile> targets) throws CommandSyntaxException {
        final Whitelist whitelist3 = source.getMinecraftServer().getPlayerManager().getWhitelist();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : targets) {
            if (!whitelist3.isAllowed(gameProfile6)) {
                final WhitelistEntry whitelistEntry7 = new WhitelistEntry(gameProfile6);
                ((ServerConfigList<K, WhitelistEntry>)whitelist3).add(whitelistEntry7);
                source.sendFeedback(new TranslatableTextComponent("commands.whitelist.add.success", new Object[] { TextFormatter.profile(gameProfile6) }), true);
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw WhitelistCommand.ADD_FAILED_EXCEPTION.create();
        }
        return integer4;
    }
    
    private static int executeRemove(final ServerCommandSource source, final Collection<GameProfile> targets) throws CommandSyntaxException {
        final Whitelist whitelist3 = source.getMinecraftServer().getPlayerManager().getWhitelist();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : targets) {
            if (whitelist3.isAllowed(gameProfile6)) {
                final WhitelistEntry whitelistEntry7 = new WhitelistEntry(gameProfile6);
                ((ServerConfigList<GameProfile, V>)whitelist3).removeEntry(whitelistEntry7);
                source.sendFeedback(new TranslatableTextComponent("commands.whitelist.remove.success", new Object[] { TextFormatter.profile(gameProfile6) }), true);
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw WhitelistCommand.REMOVE_FAILED_EXCEPTION.create();
        }
        source.getMinecraftServer().kickNonWhitelistedPlayers(source);
        return integer4;
    }
    
    private static int executeOn(final ServerCommandSource source) throws CommandSyntaxException {
        final PlayerManager playerManager2 = source.getMinecraftServer().getPlayerManager();
        if (playerManager2.isWhitelistEnabled()) {
            throw WhitelistCommand.ALREADY_ON_EXCEPTION.create();
        }
        playerManager2.setWhitelistEnabled(true);
        source.sendFeedback(new TranslatableTextComponent("commands.whitelist.enabled", new Object[0]), true);
        source.getMinecraftServer().kickNonWhitelistedPlayers(source);
        return 1;
    }
    
    private static int executeOff(final ServerCommandSource source) throws CommandSyntaxException {
        final PlayerManager playerManager2 = source.getMinecraftServer().getPlayerManager();
        if (!playerManager2.isWhitelistEnabled()) {
            throw WhitelistCommand.ALREADY_OFF_EXCEPTION.create();
        }
        playerManager2.setWhitelistEnabled(false);
        source.sendFeedback(new TranslatableTextComponent("commands.whitelist.disabled", new Object[0]), true);
        return 1;
    }
    
    private static int executeList(final ServerCommandSource source) {
        final String[] arr2 = source.getMinecraftServer().getPlayerManager().getWhitelistedNames();
        if (arr2.length == 0) {
            source.sendFeedback(new TranslatableTextComponent("commands.whitelist.none", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.whitelist.list", new Object[] { arr2.length, String.join(", ", (CharSequence[])arr2) }), false);
        }
        return arr2.length;
    }
    
    static {
        ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.whitelist.alreadyOn", new Object[0]));
        ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.whitelist.alreadyOff", new Object[0]));
        ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.whitelist.add.failed", new Object[0]));
        REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.whitelist.remove.failed", new Object[0]));
    }
}
