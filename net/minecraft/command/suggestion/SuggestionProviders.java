package net.minecraft.command.suggestion;

import com.mojang.brigadier.suggestion.Suggestions;
import com.google.common.collect.Maps;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.util.Identifier;
import java.util.Map;

public class SuggestionProviders
{
    private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY;
    private static final Identifier ASK_SERVER_NAME;
    public static final SuggestionProvider<CommandSource> ASK_SERVER;
    public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES;
    public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS;
    public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES;
    
    public static <S extends CommandSource> SuggestionProvider<S> register(final Identifier name, final SuggestionProvider<CommandSource> provider) {
        if (SuggestionProviders.REGISTRY.containsKey(name)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + name);
        }
        SuggestionProviders.REGISTRY.put(name, provider);
        return (SuggestionProvider<S>)new LocalProvider(name, provider);
    }
    
    public static SuggestionProvider<CommandSource> byId(final Identifier name) {
        return SuggestionProviders.REGISTRY.getOrDefault(name, SuggestionProviders.ASK_SERVER);
    }
    
    public static Identifier computeName(final SuggestionProvider<CommandSource> provider) {
        if (provider instanceof LocalProvider) {
            return ((LocalProvider)provider).name;
        }
        return SuggestionProviders.ASK_SERVER_NAME;
    }
    
    public static SuggestionProvider<CommandSource> getLocalProvider(final SuggestionProvider<CommandSource> provider) {
        if (provider instanceof LocalProvider) {
            return provider;
        }
        return SuggestionProviders.ASK_SERVER;
    }
    
    static {
        REGISTRY = Maps.newHashMap();
        ASK_SERVER_NAME = new Identifier("ask_server");
        ASK_SERVER = SuggestionProviders.<CommandSource>register(SuggestionProviders.ASK_SERVER_NAME, (SuggestionProvider<CommandSource>)((commandContext, suggestionsBuilder) -> ((CommandSource)commandContext.getSource()).getCompletions((CommandContext<CommandSource>)commandContext, suggestionsBuilder)));
        ALL_RECIPES = SuggestionProviders.<ServerCommandSource>register(new Identifier("all_recipes"), (SuggestionProvider<CommandSource>)((commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getRecipeIds(), suggestionsBuilder)));
        AVAILABLE_SOUNDS = SuggestionProviders.<ServerCommandSource>register(new Identifier("available_sounds"), (SuggestionProvider<CommandSource>)((commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getSoundIds(), suggestionsBuilder)));
        SUMMONABLE_ENTITIES = SuggestionProviders.<ServerCommandSource>register(new Identifier("summonable_entities"), (SuggestionProvider<CommandSource>)((commandContext, suggestionsBuilder) -> CommandSource.<EntityType<?>>suggestFromIdentifier(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), suggestionsBuilder, EntityType::getId, entityType -> new TranslatableTextComponent(SystemUtil.createTranslationKey("entity", EntityType.getId(entityType)), new Object[0]))));
    }
    
    public static class LocalProvider implements SuggestionProvider<CommandSource>
    {
        private final SuggestionProvider<CommandSource> provider;
        private final Identifier name;
        
        public LocalProvider(final Identifier name, final SuggestionProvider<CommandSource> suggestionProvider) {
            this.provider = suggestionProvider;
            this.name = name;
        }
        
        public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSource> commandContext, final SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            return (CompletableFuture<Suggestions>)this.provider.getSuggestions((CommandContext)commandContext, suggestionsBuilder);
        }
    }
}
