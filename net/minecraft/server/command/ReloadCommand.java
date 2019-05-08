package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class ReloadCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.reload.success", new Object[0]), true);
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().reload();
            return 0;
        }));
    }
}
