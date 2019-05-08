package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.BannedIpList;
import java.util.regex.Matcher;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PardonIpCommand
{
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION;
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pardon-ip").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList().getNames(), suggestionsBuilder)).executes(commandContext -> pardonIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))));
    }
    
    private static int pardonIp(final ServerCommandSource serverCommandSource, final String string) throws CommandSyntaxException {
        final Matcher matcher3 = BanIpCommand.a.matcher(string);
        if (!matcher3.matches()) {
            throw PardonIpCommand.INVALID_IP_EXCEPTION.create();
        }
        final BannedIpList bannedIpList4 = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
        if (!bannedIpList4.isBanned(string)) {
            throw PardonIpCommand.ALREADY_UNBANNED_EXCEPTION.create();
        }
        ((ServerConfigList<String, V>)bannedIpList4).remove(string);
        serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.pardonip.success", new Object[] { string }), true);
        return 1;
    }
    
    static {
        INVALID_IP_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.pardonip.invalid", new Object[0]));
        ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.pardonip.failed", new Object[0]));
    }
}
