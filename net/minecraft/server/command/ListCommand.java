package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Collection;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class ListCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> executeNames((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("uuids").executes(commandContext -> executeUuids((ServerCommandSource)commandContext.getSource()))));
    }
    
    private static int executeNames(final ServerCommandSource serverCommandSource) {
        return execute(serverCommandSource, PlayerEntity::getDisplayName);
    }
    
    private static int executeUuids(final ServerCommandSource serverCommandSource) {
        return execute(serverCommandSource, PlayerEntity::getNameAndUuid);
    }
    
    private static int execute(final ServerCommandSource source, final Function<ServerPlayerEntity, TextComponent> nameProvider) {
        final PlayerManager playerManager3 = source.getMinecraftServer().getPlayerManager();
        final List<ServerPlayerEntity> list4 = playerManager3.getPlayerList();
        final TextComponent textComponent5 = TextFormatter.<ServerPlayerEntity>join(list4, nameProvider);
        source.sendFeedback(new TranslatableTextComponent("commands.list.players", new Object[] { list4.size(), playerManager3.getMaxPlayerCount(), textComponent5 }), false);
        return list4.size();
    }
}
