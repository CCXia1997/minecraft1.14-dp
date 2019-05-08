package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextFormatter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TagCommand
{
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION;
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tag").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(CommandManager.literal("add").then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StringArgumentType.getString(commandContext, "name")))))).then(CommandManager.literal("remove").then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(getTags(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")), suggestionsBuilder)).executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StringArgumentType.getString(commandContext, "name")))))).then(CommandManager.literal("list").executes(commandContext -> executeList((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"))))));
    }
    
    private static Collection<String> getTags(final Collection<? extends Entity> entities) {
        final Set<String> set2 = Sets.newHashSet();
        for (final Entity entity4 : entities) {
            set2.addAll(entity4.getScoreboardTags());
        }
        return set2;
    }
    
    private static int executeAdd(final ServerCommandSource source, final Collection<? extends Entity> targets, final String tag) throws CommandSyntaxException {
        int integer4 = 0;
        for (final Entity entity6 : targets) {
            if (entity6.addScoreboardTag(tag)) {
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw TagCommand.ADD_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.add.success.single", new Object[] { tag, ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.add.success.multiple", new Object[] { tag, targets.size() }), true);
        }
        return integer4;
    }
    
    private static int executeRemove(final ServerCommandSource source, final Collection<? extends Entity> targets, final String tag) throws CommandSyntaxException {
        int integer4 = 0;
        for (final Entity entity6 : targets) {
            if (entity6.removeScoreboardTag(tag)) {
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw TagCommand.REMOVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.remove.success.single", new Object[] { tag, ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.remove.success.multiple", new Object[] { tag, targets.size() }), true);
        }
        return integer4;
    }
    
    private static int executeList(final ServerCommandSource source, final Collection<? extends Entity> targets) {
        final Set<String> set3 = Sets.newHashSet();
        for (final Entity entity5 : targets) {
            set3.addAll(entity5.getScoreboardTags());
        }
        if (targets.size() == 1) {
            final Entity entity6 = (Entity)targets.iterator().next();
            if (set3.isEmpty()) {
                source.sendFeedback(new TranslatableTextComponent("commands.tag.list.single.empty", new Object[] { entity6.getDisplayName() }), false);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.tag.list.single.success", new Object[] { entity6.getDisplayName(), set3.size(), TextFormatter.sortedJoin(set3) }), false);
            }
        }
        else if (set3.isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.list.multiple.empty", new Object[] { targets.size() }), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.tag.list.multiple.success", new Object[] { targets.size(), set3.size(), TextFormatter.sortedJoin(set3) }), false);
        }
        return set3.size();
    }
    
    static {
        ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.tag.add.failed", new Object[0]));
        REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.tag.remove.failed", new Object[0]));
    }
}
