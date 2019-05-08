package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.function.Function;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextFormatter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.LevelProperties;
import java.util.List;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.dimension.DimensionType;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.resource.ResourcePackContainer;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class DatapackCommand
{
    private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION;
    private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION;
    private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION;
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER;
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DatapackCommand.DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, resourcePackContainer) -> resourcePackContainer.getSortingDirection().<ResourcePackContainer, ResourcePackContainer>locate(list, resourcePackContainer, resourcePackContainer -> resourcePackContainer, false)))).then(CommandManager.literal("after").then(CommandManager.argument("existing", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DatapackCommand.ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer((CommandContext<ServerCommandSource>)commandContext, "existing", false)) + 1, resourcePackContainer)))))).then(CommandManager.literal("before").then(CommandManager.argument("existing", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DatapackCommand.ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer((CommandContext<ServerCommandSource>)commandContext, "existing", false)), resourcePackContainer)))))).then(CommandManager.literal("last").executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), List::add)))).then(CommandManager.literal("first").executes(commandContext -> executeEnable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, resourcePackContainer) -> list.add(0, resourcePackContainer))))))).then(CommandManager.literal("disable").then(CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DatapackCommand.ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> executeDisable((ServerCommandSource)commandContext.getSource(), getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> executeList((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("available").executes(commandContext -> executeListAvailable((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("enabled").executes(commandContext -> executeListEnabled((ServerCommandSource)commandContext.getSource())))));
    }
    
    private static int executeEnable(final ServerCommandSource source, final ResourcePackContainer container, final PackAdder packAdder) throws CommandSyntaxException {
        final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager4 = source.getMinecraftServer().getResourcePackContainerManager();
        final List<ResourcePackContainer> list5 = Lists.newArrayList(resourcePackContainerManager4.getEnabledContainers());
        packAdder.apply(list5, container);
        resourcePackContainerManager4.setEnabled(list5);
        final LevelProperties levelProperties6 = source.getMinecraftServer().getWorld(DimensionType.a).getLevelProperties();
        levelProperties6.getEnabledDataPacks().clear();
        resourcePackContainerManager4.getEnabledContainers().forEach(resourcePackContainer -> levelProperties6.getEnabledDataPacks().add(resourcePackContainer.getName()));
        levelProperties6.getDisabledDataPacks().remove(container.getName());
        source.sendFeedback(new TranslatableTextComponent("commands.datapack.enable.success", new Object[] { container.getInformationText(true) }), true);
        source.getMinecraftServer().reload();
        return resourcePackContainerManager4.getEnabledContainers().size();
    }
    
    private static int executeDisable(final ServerCommandSource source, final ResourcePackContainer container) {
        final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager3 = source.getMinecraftServer().getResourcePackContainerManager();
        final List<ResourcePackContainer> list4 = Lists.newArrayList(resourcePackContainerManager3.getEnabledContainers());
        list4.remove(container);
        resourcePackContainerManager3.setEnabled(list4);
        final LevelProperties levelProperties5 = source.getMinecraftServer().getWorld(DimensionType.a).getLevelProperties();
        levelProperties5.getEnabledDataPacks().clear();
        resourcePackContainerManager3.getEnabledContainers().forEach(resourcePackContainer -> levelProperties5.getEnabledDataPacks().add(resourcePackContainer.getName()));
        levelProperties5.getDisabledDataPacks().add(container.getName());
        source.sendFeedback(new TranslatableTextComponent("commands.datapack.disable.success", new Object[] { container.getInformationText(true) }), true);
        source.getMinecraftServer().reload();
        return resourcePackContainerManager3.getEnabledContainers().size();
    }
    
    private static int executeList(final ServerCommandSource source) {
        return executeListEnabled(source) + executeListAvailable(source);
    }
    
    private static int executeListAvailable(final ServerCommandSource source) {
        final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager2 = source.getMinecraftServer().getResourcePackContainerManager();
        if (resourcePackContainerManager2.getDisabledContainers().isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.datapack.list.available.none", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.datapack.list.available.success", new Object[] { resourcePackContainerManager2.getDisabledContainers().size(), TextFormatter.<ResourcePackContainer>join(resourcePackContainerManager2.getDisabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(false)) }), false);
        }
        return resourcePackContainerManager2.getDisabledContainers().size();
    }
    
    private static int executeListEnabled(final ServerCommandSource source) {
        final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager2 = source.getMinecraftServer().getResourcePackContainerManager();
        if (resourcePackContainerManager2.getEnabledContainers().isEmpty()) {
            source.sendFeedback(new TranslatableTextComponent("commands.datapack.list.enabled.none", new Object[0]), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.datapack.list.enabled.success", new Object[] { resourcePackContainerManager2.getEnabledContainers().size(), TextFormatter.<ResourcePackContainer>join(resourcePackContainerManager2.getEnabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(true)) }), false);
        }
        return resourcePackContainerManager2.getEnabledContainers().size();
    }
    
    private static ResourcePackContainer getPackContainer(final CommandContext<ServerCommandSource> context, final String name, final boolean enable) throws CommandSyntaxException {
        final String string4 = StringArgumentType.getString((CommandContext)context, name);
        final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager5 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getResourcePackContainerManager();
        final ResourcePackContainer resourcePackContainer6 = resourcePackContainerManager5.getContainer(string4);
        if (resourcePackContainer6 == null) {
            throw DatapackCommand.UNKNOWN_DATAPACK_EXCEPTION.create(string4);
        }
        final boolean boolean7 = resourcePackContainerManager5.getEnabledContainers().contains(resourcePackContainer6);
        if (enable && boolean7) {
            throw DatapackCommand.ALREADY_ENABLED_EXCEPTION.create(string4);
        }
        if (!enable && !boolean7) {
            throw DatapackCommand.ALREADY_DISABLED_EXCEPTION.create(string4);
        }
        return resourcePackContainer6;
    }
    
    static {
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.datapack.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.datapack.enable.failed", new Object[] { object });
            return translatableTextComponent2;
        });
        final TranslatableTextComponent translatableTextComponent3;
        ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.datapack.disable.failed", new Object[] { object });
            return translatableTextComponent3;
        });
        ENABLED_CONTAINERS_SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getResourcePackContainerManager().getEnabledContainers().stream().map(ResourcePackContainer::getName).<String>map(StringArgumentType::escapeIfRequired), suggestionsBuilder));
        DISABLED_CONTAINERS_SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getResourcePackContainerManager().getDisabledContainers().stream().map(ResourcePackContainer::getName).<String>map(StringArgumentType::escapeIfRequired), suggestionsBuilder));
    }
    
    interface PackAdder
    {
        void apply(final List<ResourcePackContainer> arg1, final ResourcePackContainer arg2) throws CommandSyntaxException;
    }
}
