package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class RecipeCommand
{
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("recipe").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("give").then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.argument("recipe", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.ALL_RECIPES).executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Collections.<Recipe<?>>singleton(IdentifierArgumentType.getRecipeArgument((CommandContext<ServerCommandSource>)commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values())))))).then(CommandManager.literal("take").then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.argument("recipe", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.ALL_RECIPES).executes(commandContext -> executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Collections.<Recipe<?>>singleton(IdentifierArgumentType.getRecipeArgument((CommandContext<ServerCommandSource>)commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values()))))));
    }
    
    private static int executeGive(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Collection<Recipe<?>> recipes) throws CommandSyntaxException {
        int integer4 = 0;
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            integer4 += serverPlayerEntity6.unlockRecipes(recipes);
        }
        if (integer4 == 0) {
            throw RecipeCommand.GIVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.recipe.give.success.single", new Object[] { recipes.size(), targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.recipe.give.success.multiple", new Object[] { recipes.size(), targets.size() }), true);
        }
        return integer4;
    }
    
    private static int executeTake(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Collection<Recipe<?>> recipes) throws CommandSyntaxException {
        int integer4 = 0;
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            integer4 += serverPlayerEntity6.lockRecipes(recipes);
        }
        if (integer4 == 0) {
            throw RecipeCommand.TAKE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.recipe.take.success.single", new Object[] { recipes.size(), targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.recipe.take.success.multiple", new Object[] { recipes.size(), targets.size() }), true);
        }
        return integer4;
    }
    
    static {
        GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.recipe.give.failed", new Object[0]));
        TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.recipe.take.failed", new Object[0]));
    }
}
