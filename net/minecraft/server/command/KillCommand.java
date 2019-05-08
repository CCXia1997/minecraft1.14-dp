package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class KillCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<? extends Entity> targets) {
        for (final Entity entity4 : targets) {
            entity4.kill();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.kill.success.single", new Object[] { ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.kill.success.multiple", new Object[] { targets.size() }), true);
        }
        return targets.size();
    }
}
