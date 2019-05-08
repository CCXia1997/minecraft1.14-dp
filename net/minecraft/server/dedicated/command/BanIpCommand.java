package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.server.BannedIpList;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Date;
import net.minecraft.server.BannedIpEntry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Pattern;

public class BanIpCommand
{
    public static final Pattern a;
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION;
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban-ip").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).executes(commandContext -> checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null))).then(CommandManager.argument("reason", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }
    
    private static int checkIp(final ServerCommandSource serverCommandSource, final String string, @Nullable final TextComponent textComponent) throws CommandSyntaxException {
        final Matcher matcher4 = BanIpCommand.a.matcher(string);
        if (matcher4.matches()) {
            return banIp(serverCommandSource, string, textComponent);
        }
        final ServerPlayerEntity serverPlayerEntity5 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(string);
        if (serverPlayerEntity5 != null) {
            return banIp(serverCommandSource, serverPlayerEntity5.getServerBrand(), textComponent);
        }
        throw BanIpCommand.INVALID_IP_EXCEPTION.create();
    }
    
    private static int banIp(final ServerCommandSource serverCommandSource, final String string, @Nullable final TextComponent textComponent) throws CommandSyntaxException {
        final BannedIpList bannedIpList4 = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
        if (bannedIpList4.isBanned(string)) {
            throw BanIpCommand.ALREADY_BANNED_EXCEPTION.create();
        }
        final List<ServerPlayerEntity> list5 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayersByIp(string);
        final BannedIpEntry bannedIpEntry6 = new BannedIpEntry(string, null, serverCommandSource.getName(), null, (textComponent == null) ? null : textComponent.getString());
        ((ServerConfigList<K, BannedIpEntry>)bannedIpList4).add(bannedIpEntry6);
        serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.banip.success", new Object[] { string, bannedIpEntry6.getReason() }), true);
        if (!list5.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.banip.info", new Object[] { list5.size(), EntitySelector.getNames(list5) }), true);
        }
        for (final ServerPlayerEntity serverPlayerEntity8 : list5) {
            serverPlayerEntity8.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.ip_banned", new Object[0]));
        }
        return list5.size();
    }
    
    static {
        a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        INVALID_IP_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.banip.invalid", new Object[0]));
        ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.banip.failed", new Object[0]));
    }
}
