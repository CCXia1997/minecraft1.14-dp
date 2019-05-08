package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class DeOpCommand
{
    private static final SimpleCommandExceptionType ALREADY_DEOPPED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("deop").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getOpNames(), suggestionsBuilder)).executes(commandContext -> deop((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }
    
    private static int deop(final ServerCommandSource serverCommandSource, final Collection<GameProfile> collection) throws CommandSyntaxException {
        final PlayerManager playerManager3 = serverCommandSource.getMinecraftServer().getPlayerManager();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : collection) {
            if (playerManager3.isOperator(gameProfile6)) {
                playerManager3.removeFromOperators(gameProfile6);
                ++integer4;
                serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.deop.success", new Object[] { collection.iterator().next().getName() }), true);
            }
        }
        if (integer4 == 0) {
            throw DeOpCommand.ALREADY_DEOPPED_EXCEPTION.create();
        }
        serverCommandSource.getMinecraftServer().kickNonWhitelistedPlayers(serverCommandSource);
        return integer4;
    }
    
    static {
        ALREADY_DEOPPED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.deop.failed", new Object[0]));
    }
}
