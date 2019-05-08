package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import net.minecraft.client.util.NetworkUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PublishCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("publish").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() && serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), NetworkUtils.findLocalPort()))).then(CommandManager.argument("port", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 65535)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))));
    }
    
    private static int execute(final ServerCommandSource source, final int port) throws CommandSyntaxException {
        if (source.getMinecraftServer().isRemote()) {
            throw PublishCommand.ALREADY_PUBLISHED_EXCEPTION.create(source.getMinecraftServer().getServerPort());
        }
        if (!source.getMinecraftServer().openToLan(source.getMinecraftServer().getDefaultGameMode(), false, port)) {
            throw PublishCommand.FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableTextComponent("commands.publish.success", new Object[] { port }), true);
        return port;
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.publish.failed", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.publish.alreadyPublished", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
