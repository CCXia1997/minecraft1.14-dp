package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Iterator;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormatter;
import java.util.Date;
import net.minecraft.server.BannedPlayerEntry;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BanCommand
{
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgumentType.create()).executes(commandContext -> ban((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets"), null))).then(CommandManager.argument("reason", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> ban((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }
    
    private static int ban(final ServerCommandSource serverCommandSource, final Collection<GameProfile> collection, @Nullable final TextComponent textComponent) throws CommandSyntaxException {
        final BannedPlayerList bannedPlayerList4 = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
        int integer5 = 0;
        for (final GameProfile gameProfile7 : collection) {
            if (!bannedPlayerList4.contains(gameProfile7)) {
                final BannedPlayerEntry bannedPlayerEntry8 = new BannedPlayerEntry(gameProfile7, null, serverCommandSource.getName(), null, (textComponent == null) ? null : textComponent.getString());
                ((ServerConfigList<K, BannedPlayerEntry>)bannedPlayerList4).add(bannedPlayerEntry8);
                ++integer5;
                serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.ban.success", new Object[] { TextFormatter.profile(gameProfile7), bannedPlayerEntry8.getReason() }), true);
                final ServerPlayerEntity serverPlayerEntity9 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(gameProfile7.getId());
                if (serverPlayerEntity9 == null) {
                    continue;
                }
                serverPlayerEntity9.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.banned", new Object[0]));
            }
        }
        if (integer5 == 0) {
            throw BanCommand.ALREADY_BANNED_EXCEPTION.create();
        }
        return integer5;
    }
    
    static {
        ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.ban.failed", new Object[0]));
    }
}
