package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SaveAllCommand
{
    private static final SimpleCommandExceptionType SAVE_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-all").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> saveAll((ServerCommandSource)commandContext.getSource(), false))).then(CommandManager.literal("flush").executes(commandContext -> saveAll((ServerCommandSource)commandContext.getSource(), true))));
    }
    
    private static int saveAll(final ServerCommandSource source, final boolean flush) throws CommandSyntaxException {
        source.sendFeedback(new TranslatableTextComponent("commands.save.saving", new Object[0]), false);
        final MinecraftServer minecraftServer3 = source.getMinecraftServer();
        minecraftServer3.getPlayerManager().saveAllPlayerData();
        final boolean boolean4 = minecraftServer3.save(true, flush, true);
        if (!boolean4) {
            throw SaveAllCommand.SAVE_FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableTextComponent("commands.save.success", new Object[0]), true);
        return 1;
    }
    
    static {
        SAVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.save.failed", new Object[0]));
    }
}
