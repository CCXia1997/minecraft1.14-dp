package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.world.ServerWorld;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SaveOnCommand
{
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-on").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> {
            final ServerCommandSource serverCommandSource2 = (ServerCommandSource)commandContext.getSource();
            boolean boolean3 = false;
            for (final ServerWorld serverWorld5 : serverCommandSource2.getMinecraftServer().getWorlds()) {
                if (serverWorld5 != null && serverWorld5.savingDisabled) {
                    serverWorld5.savingDisabled = false;
                    boolean3 = true;
                }
            }
            if (!boolean3) {
                throw SaveOnCommand.ALREADY_ON_EXCEPTION.create();
            }
            serverCommandSource2.sendFeedback(new TranslatableTextComponent("commands.save.enabled", new Object[0]), true);
            return 1;
        }));
    }
    
    static {
        ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.save.alreadyOn", new Object[0]));
    }
}
