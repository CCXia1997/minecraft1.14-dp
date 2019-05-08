package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class MeCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then(CommandManager.argument("action", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().sendToAll(new TranslatableTextComponent("chat.type.emote", new Object[] { ((ServerCommandSource)commandContext.getSource()).getDisplayName(), StringArgumentType.getString(commandContext, "action") }));
            return 1;
        })));
    }
}
