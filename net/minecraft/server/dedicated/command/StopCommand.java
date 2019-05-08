package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;

public class StopCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stop").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.stop.stopping", new Object[0]), true);
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().stop(false);
            return 1;
        }));
    }
}
