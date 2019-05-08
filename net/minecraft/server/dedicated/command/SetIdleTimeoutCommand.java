package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;

public class SetIdleTimeoutCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setidletimeout").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("minutes", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes")))));
    }
    
    private static int execute(final ServerCommandSource source, final int minutes) {
        source.getMinecraftServer().setPlayerIdleTimeout(minutes);
        source.sendFeedback(new TranslatableTextComponent("commands.setidletimeout.success", new Object[] { minutes }), true);
        return minutes;
    }
}
