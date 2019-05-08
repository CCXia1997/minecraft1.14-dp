package net.minecraft.command.arguments;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import java.util.Collections;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class GameProfileArgumentType implements ArgumentType<GameProfileArgument>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType UNKNOWN_PLAYER_EXCEPTION;
    
    public static Collection<GameProfile> getProfileArgument(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return ((GameProfileArgument)commandContext.getArgument(string, (Class)GameProfileArgument.class)).getNames((ServerCommandSource)commandContext.getSource());
    }
    
    public static GameProfileArgumentType create() {
        return new GameProfileArgumentType();
    }
    
    public GameProfileArgument a(final StringReader stringReader) throws CommandSyntaxException {
        if (!stringReader.canRead() || stringReader.peek() != '@') {
            final int integer2 = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            final String string3 = stringReader.getString().substring(integer2, stringReader.getCursor());
            final GameProfile gameProfile3;
            return serverCommandSource -> {
                gameProfile3 = serverCommandSource.getMinecraftServer().getUserCache().findByName(string3);
                if (gameProfile3 == null) {
                    throw GameProfileArgumentType.UNKNOWN_PLAYER_EXCEPTION.create();
                }
                else {
                    return Collections.<GameProfile>singleton(gameProfile3);
                }
            };
        }
        final EntitySelectorReader entitySelectorReader2 = new EntitySelectorReader(stringReader);
        final EntitySelector entitySelector3 = entitySelectorReader2.read();
        if (entitySelector3.includesNonPlayers()) {
            throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.create();
        }
        return new b(entitySelector3);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            final StringReader stringReader3 = new StringReader(builder.getInput());
            stringReader3.setCursor(builder.getStart());
            final EntitySelectorReader entitySelectorReader4 = new EntitySelectorReader(stringReader3);
            try {
                entitySelectorReader4.read();
            }
            catch (CommandSyntaxException ex) {}
            return entitySelectorReader4.listSuggestions(builder, suggestionsBuilder -> CommandSource.suggestMatching(((CommandSource)context.getSource()).getPlayerNames(), suggestionsBuilder));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return GameProfileArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
        UNKNOWN_PLAYER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.player.unknown", new Object[0]));
    }
    
    public static class b implements GameProfileArgument
    {
        private final EntitySelector a;
        
        public b(final EntitySelector entitySelector) {
            this.a = entitySelector;
        }
        
        @Override
        public Collection<GameProfile> getNames(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            final List<ServerPlayerEntity> list2 = this.a.getPlayers(serverCommandSource);
            if (list2.isEmpty()) {
                throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
            }
            final List<GameProfile> list3 = Lists.newArrayList();
            for (final ServerPlayerEntity serverPlayerEntity5 : list2) {
                list3.add(serverPlayerEntity5.getGameProfile());
            }
            return list3;
        }
    }
    
    @FunctionalInterface
    public interface GameProfileArgument
    {
        Collection<GameProfile> getNames(final ServerCommandSource arg1) throws CommandSyntaxException;
    }
}
