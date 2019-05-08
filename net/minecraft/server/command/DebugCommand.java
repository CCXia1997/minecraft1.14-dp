package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.math.MathHelper;
import java.util.Locale;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class DebugCommand
{
    private static final SimpleCommandExceptionType NORUNNING_EXCPETION;
    private static final SimpleCommandExceptionType ALREADYRUNNING_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("start").executes(commandContext -> executeStart((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> executeStop((ServerCommandSource)commandContext.getSource()))));
    }
    
    private static int executeStart(final ServerCommandSource source) throws CommandSyntaxException {
        final MinecraftServer minecraftServer2 = source.getMinecraftServer();
        final DisableableProfiler disableableProfiler3 = minecraftServer2.getProfiler();
        if (disableableProfiler3.getController().isEnabled()) {
            throw DebugCommand.ALREADYRUNNING_EXCEPTION.create();
        }
        minecraftServer2.enableProfiler();
        source.sendFeedback(new TranslatableTextComponent("commands.debug.started", new Object[] { "Started the debug profiler. Type '/debug stop' to stop it." }), true);
        return 0;
    }
    
    private static int executeStop(final ServerCommandSource source) throws CommandSyntaxException {
        final MinecraftServer minecraftServer2 = source.getMinecraftServer();
        final DisableableProfiler disableableProfiler3 = minecraftServer2.getProfiler();
        if (!disableableProfiler3.getController().isEnabled()) {
            throw DebugCommand.NORUNNING_EXCPETION.create();
        }
        final ProfileResult profileResult4 = disableableProfiler3.getController().disable();
        final File file5 = new File(minecraftServer2.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
        profileResult4.saveToFile(file5);
        final float float6 = profileResult4.getTimeSpan() / 1.0E9f;
        final float float7 = profileResult4.getTickSpan() / float6;
        source.sendFeedback(new TranslatableTextComponent("commands.debug.stopped", new Object[] { String.format(Locale.ROOT, "%.2f", float6), profileResult4.getTickSpan(), String.format("%.2f", float7) }), true);
        return MathHelper.floor(float7);
    }
    
    static {
        NORUNNING_EXCPETION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.debug.notRunning", new Object[0]));
        ALREADYRUNNING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.debug.alreadyRunning", new Object[0]));
    }
}
