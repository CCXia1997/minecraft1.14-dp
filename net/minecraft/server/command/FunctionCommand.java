package net.minecraft.server.command;

import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.function.CommandFunction;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.FunctionArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class FunctionCommand
{
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("function").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)FunctionArgumentType.create()).suggests((SuggestionProvider)FunctionCommand.SUGGESTION_PROVIDER).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)commandContext, "name")))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<CommandFunction> functions) {
        int integer3 = 0;
        for (final CommandFunction commandFunction5 : functions) {
            integer3 += source.getMinecraftServer().getCommandFunctionManager().execute(commandFunction5, source.withSilent().withMaxLevel(2));
        }
        if (functions.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.function.success.single", new Object[] { integer3, functions.iterator().next().getId() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.function.success.multiple", new Object[] { integer3, functions.size() }), true);
        }
        return integer3;
    }
    
    static {
        SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> {
            final CommandFunctionManager commandFunctionManager3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getCommandFunctionManager();
            CommandSource.suggestIdentifiers(commandFunctionManager3.getTags().getKeys(), suggestionsBuilder, "#");
            return CommandSource.suggestIdentifiers(commandFunctionManager3.getFunctions().keySet(), suggestionsBuilder);
        });
    }
}
