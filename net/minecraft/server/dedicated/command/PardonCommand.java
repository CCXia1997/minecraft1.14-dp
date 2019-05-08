package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormatter;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PardonCommand
{
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pardon").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList().getNames(), suggestionsBuilder)).executes(commandContext -> pardon((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }
    
    private static int pardon(final ServerCommandSource serverCommandSource, final Collection<GameProfile> collection) throws CommandSyntaxException {
        final BannedPlayerList bannedPlayerList3 = serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : collection) {
            if (bannedPlayerList3.contains(gameProfile6)) {
                ((ServerConfigList<GameProfile, V>)bannedPlayerList3).remove(gameProfile6);
                ++integer4;
                serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.pardon.success", new Object[] { TextFormatter.profile(gameProfile6) }), true);
            }
        }
        if (integer4 == 0) {
            throw PardonCommand.ALREADY_UNBANNED_EXCEPTION.create();
        }
        return integer4;
    }
    
    static {
        ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.pardon.failed", new Object[0]));
    }
}
