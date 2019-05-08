package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class SayCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> {
            final TextComponent textComponent2 = MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message");
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().sendToAll(new TranslatableTextComponent("chat.type.announcement", new Object[] { ((ServerCommandSource)commandContext.getSource()).getDisplayName(), textComponent2 }));
            return 1;
        })));
    }
}
