package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import com.mojang.brigadier.ParseResults;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class HelpCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("help").executes(commandContext -> {
            final Map<CommandNode<ServerCommandSource>, String> map3 = (Map<CommandNode<ServerCommandSource>, String>)dispatcher.getSmartUsage((CommandNode)dispatcher.getRoot(), commandContext.getSource());
            for (final String string5 : map3.values()) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new StringTextComponent("/" + string5), false);
            }
            return map3.size();
        })).then(CommandManager.argument("command", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).executes(commandContext -> {
            final ParseResults<ServerCommandSource> parseResults3 = (ParseResults<ServerCommandSource>)dispatcher.parse(StringArgumentType.getString(commandContext, "command"), commandContext.getSource());
            if (parseResults3.getContext().getNodes().isEmpty()) {
                throw HelpCommand.FAILED_EXCEPTION.create();
            }
            final Map<CommandNode<ServerCommandSource>, String> map4 = (Map<CommandNode<ServerCommandSource>, String>)dispatcher.getSmartUsage(Iterables.<ParsedCommandNode>getLast((Iterable<ParsedCommandNode>)parseResults3.getContext().getNodes()).getNode(), commandContext.getSource());
            for (final String string6 : map4.values()) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new StringTextComponent("/" + parseResults3.getReader().getString() + " " + string6), false);
            }
            return map4.size();
        })));
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.help.failed", new Object[0]));
    }
}
