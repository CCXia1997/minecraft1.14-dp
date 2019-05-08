package net.minecraft.server.dedicated.command;

import net.minecraft.server.ServerConfigList;
import net.minecraft.server.PlayerManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.BanEntry;
import java.util.Collection;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;

public class BanListCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("banlist").requires(serverCommandSource -> (serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled() || serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled()) && serverCommandSource.hasPermissionLevel(3))).executes(commandContext -> {
            final PlayerManager playerManager2 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return execute((ServerCommandSource)commandContext.getSource(), Lists.newArrayList(Iterables.concat(playerManager2.getUserBanList().values(), playerManager2.getIpBanList().values())));
        })).then(CommandManager.literal("ips").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), (((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList()).values())))).then(CommandManager.literal("players").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), (((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList()).values()))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<? extends BanEntry<?>> targets) {
        if (targets.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.banlist.none", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.banlist.list", new Object[] { targets.size() }), false);
            for (final BanEntry<?> banEntry4 : targets) {
                source.sendFeedback(new TranslatableTextComponent("commands.banlist.entry", new Object[] { banEntry4.asTextComponent(), banEntry4.getSource(), banEntry4.getReason() }), false);
            }
        }
        return targets.size();
    }
}
