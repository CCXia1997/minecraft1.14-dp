package net.minecraft.server.command;

import net.minecraft.advancement.AdvancementProgress;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import net.minecraft.command.CommandException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class AdvancementCommand
{
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.a)))).then(CommandManager.argument("criterion", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.c)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.d)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.b)))))).then(CommandManager.literal("everything").executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements())))))).then(CommandManager.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.a)))).then(CommandManager.argument("criterion", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.c)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.d)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)AdvancementCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.b)))))).then(CommandManager.literal("everything").executes(commandContext -> executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements()))))));
    }
    
    private static int executeAdvancement(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Operation operation, final Collection<Advancement> selection) {
        int integer5 = 0;
        for (final ServerPlayerEntity serverPlayerEntity7 : targets) {
            integer5 += operation.processAll(serverPlayerEntity7, selection);
        }
        if (integer5 != 0) {
            if (selection.size() == 1) {
                if (targets.size() == 1) {
                    source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".one.to.one.success", new Object[] { selection.iterator().next().getTextComponent(), targets.iterator().next().getDisplayName() }), true);
                }
                else {
                    source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".one.to.many.success", new Object[] { selection.iterator().next().getTextComponent(), targets.size() }), true);
                }
            }
            else if (targets.size() == 1) {
                source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.one.success", new Object[] { selection.size(), targets.iterator().next().getDisplayName() }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.many.success", new Object[] { selection.size(), targets.size() }), true);
            }
            return integer5;
        }
        if (selection.size() == 1) {
            if (targets.size() == 1) {
                throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".one.to.one.failure", new Object[] { selection.iterator().next().getTextComponent(), targets.iterator().next().getDisplayName() }));
            }
            throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".one.to.many.failure", new Object[] { selection.iterator().next().getTextComponent(), targets.size() }));
        }
        else {
            if (targets.size() == 1) {
                throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.one.failure", new Object[] { selection.size(), targets.iterator().next().getDisplayName() }));
            }
            throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.many.failure", new Object[] { selection.size(), targets.size() }));
        }
    }
    
    private static int executeCriterion(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Operation operation, final Advancement advancement, final String criterion) {
        int integer6 = 0;
        if (!advancement.getCriteria().containsKey(criterion)) {
            throw new CommandException(new TranslatableTextComponent("commands.advancement.criterionNotFound", new Object[] { advancement.getTextComponent(), criterion }));
        }
        for (final ServerPlayerEntity serverPlayerEntity8 : targets) {
            if (operation.processEachCriterion(serverPlayerEntity8, advancement, criterion)) {
                ++integer6;
            }
        }
        if (integer6 != 0) {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".criterion.to.one.success", new Object[] { criterion, advancement.getTextComponent(), targets.iterator().next().getDisplayName() }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent(operation.getCommandPrefix() + ".criterion.to.many.success", new Object[] { criterion, advancement.getTextComponent(), targets.size() }), true);
            }
            return integer6;
        }
        if (targets.size() == 1) {
            throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".criterion.to.one.failure", new Object[] { criterion, advancement.getTextComponent(), targets.iterator().next().getDisplayName() }));
        }
        throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".criterion.to.many.failure", new Object[] { criterion, advancement.getTextComponent(), targets.size() }));
    }
    
    private static List<Advancement> select(final Advancement advancement, final Selection selection) {
        final List<Advancement> list3 = Lists.newArrayList();
        if (selection.before) {
            for (Advancement advancement2 = advancement.getParent(); advancement2 != null; advancement2 = advancement2.getParent()) {
                list3.add(advancement2);
            }
        }
        list3.add(advancement);
        if (selection.after) {
            addChildrenRecursivelyToList(advancement, list3);
        }
        return list3;
    }
    
    private static void addChildrenRecursivelyToList(final Advancement parent, final List<Advancement> childList) {
        for (final Advancement advancement4 : parent.getChildren()) {
            childList.add(advancement4);
            addChildrenRecursivelyToList(advancement4, childList);
        }
    }
    
    static {
        SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> {
            final Collection<Advancement> collection3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements();
            return CommandSource.suggestIdentifiers(collection3.stream().<Identifier>map(Advancement::getId), suggestionsBuilder);
        });
    }
    
    enum Operation
    {
        GRANT("grant") {
            @Override
            protected boolean processEach(final ServerPlayerEntity serverPlayerEntity, final Advancement advancement) {
                final AdvancementProgress advancementProgress3 = serverPlayerEntity.getAdvancementManager().getProgress(advancement);
                if (advancementProgress3.isDone()) {
                    return false;
                }
                for (final String string5 : advancementProgress3.getUnobtainedCriteria()) {
                    serverPlayerEntity.getAdvancementManager().grantCriterion(advancement, string5);
                }
                return true;
            }
            
            @Override
            protected boolean processEachCriterion(final ServerPlayerEntity serverPlayerEntity, final Advancement advancement, final String criterion) {
                return serverPlayerEntity.getAdvancementManager().grantCriterion(advancement, criterion);
            }
        }, 
        REVOKE("revoke") {
            @Override
            protected boolean processEach(final ServerPlayerEntity serverPlayerEntity, final Advancement advancement) {
                final AdvancementProgress advancementProgress3 = serverPlayerEntity.getAdvancementManager().getProgress(advancement);
                if (!advancementProgress3.isAnyObtained()) {
                    return false;
                }
                for (final String string5 : advancementProgress3.getObtainedCriteria()) {
                    serverPlayerEntity.getAdvancementManager().revokeCriterion(advancement, string5);
                }
                return true;
            }
            
            @Override
            protected boolean processEachCriterion(final ServerPlayerEntity serverPlayerEntity, final Advancement advancement, final String criterion) {
                return serverPlayerEntity.getAdvancementManager().revokeCriterion(advancement, criterion);
            }
        };
        
        private final String commandPrefix;
        
        private Operation(final String string1) {
            this.commandPrefix = "commands.advancement." + string1;
        }
        
        public int processAll(final ServerPlayerEntity serverPlayerEntity, final Iterable<Advancement> iterable) {
            int integer3 = 0;
            for (final Advancement advancement5 : iterable) {
                if (this.processEach(serverPlayerEntity, advancement5)) {
                    ++integer3;
                }
            }
            return integer3;
        }
        
        protected abstract boolean processEach(final ServerPlayerEntity arg1, final Advancement arg2);
        
        protected abstract boolean processEachCriterion(final ServerPlayerEntity arg1, final Advancement arg2, final String arg3);
        
        protected String getCommandPrefix() {
            return this.commandPrefix;
        }
    }
    
    enum Selection
    {
        a(false, false), 
        b(true, true), 
        c(false, true), 
        d(true, false), 
        e(true, true);
        
        private final boolean before;
        private final boolean after;
        
        private Selection(final boolean boolean1, final boolean boolean2) {
            this.before = boolean1;
            this.after = boolean2;
        }
    }
}
