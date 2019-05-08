package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.world.GameRules;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class GameRuleCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("gamerule").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (final Map.Entry<String, GameRules.Key> entry4 : GameRules.getKeys().entrySet()) {
            literalArgumentBuilder2.then(((LiteralArgumentBuilder)CommandManager.literal(entry4.getKey()).executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), entry4.getKey()))).then(entry4.getValue().getType().argument("value").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), entry4.getKey(), (CommandContext<ServerCommandSource>)commandContext))));
        }
        dispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static int executeSet(final ServerCommandSource source, final String gameRule, final CommandContext<ServerCommandSource> context) {
        final GameRules.Value value4 = source.getMinecraftServer().getGameRules().get(gameRule);
        value4.getType().set(context, "value", value4);
        source.sendFeedback(new TranslatableTextComponent("commands.gamerule.set", new Object[] { gameRule, value4.getString() }), true);
        return value4.getInteger();
    }
    
    private static int executeQuery(final ServerCommandSource source, final String gameRule) {
        final GameRules.Value value3 = source.getMinecraftServer().getGameRules().get(gameRule);
        source.sendFeedback(new TranslatableTextComponent("commands.gamerule.query", new Object[] { gameRule, value3.getString() }), false);
        return value3.getInteger();
    }
}
