package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import net.minecraft.server.command.CommandSource;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import com.mojang.brigadier.StringReader;
import java.util.function.Supplier;
import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.ArgumentType;

public class ScoreHolderArgumentType implements ArgumentType<ScoreHolder>
{
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER;
    private static final Collection<String> EXAMPLES;
    private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION;
    private final boolean multiple;
    
    public ScoreHolderArgumentType(final boolean multiple) {
        this.multiple = multiple;
    }
    
    public static String getScoreHolder(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return getScoreHolders(context, name).iterator().next();
    }
    
    public static Collection<String> getScoreHolders(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return getScoreHolders(context, name, (Supplier<Collection<String>>)Collections::emptyList);
    }
    
    public static Collection<String> getScoreboardScoreHolders(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return getScoreHolders(context, name, ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard()::getKnownPlayers);
    }
    
    public static Collection<String> getScoreHolders(final CommandContext<ServerCommandSource> context, final String name, final Supplier<Collection<String>> players) throws CommandSyntaxException {
        final Collection<String> collection4 = ((ScoreHolder)context.getArgument(name, (Class)ScoreHolder.class)).getNames((ServerCommandSource)context.getSource(), players);
        if (collection4.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection4;
    }
    
    public static ScoreHolderArgumentType scoreHolder() {
        return new ScoreHolderArgumentType(false);
    }
    
    public static ScoreHolderArgumentType scoreHolders() {
        return new ScoreHolderArgumentType(true);
    }
    
    public ScoreHolder a(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            final EntitySelectorReader entitySelectorReader2 = new EntitySelectorReader(stringReader);
            final EntitySelector entitySelector3 = entitySelectorReader2.read();
            if (!this.multiple && entitySelector3.getCount() > 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return new SelectorScoreHolder(entitySelector3);
        }
        else {
            final int integer2 = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            final String string3 = stringReader.getString().substring(integer2, stringReader.getCursor());
            if (string3.equals("*")) {
                final Collection<String> collection3;
                return (serverCommandSource, supplier) -> {
                    collection3 = supplier.get();
                    if (collection3.isEmpty()) {
                        throw ScoreHolderArgumentType.EMPTY_SCORE_HOLDER_EXCEPTION.create();
                    }
                    else {
                        return collection3;
                    }
                };
            }
            final Collection<String> collection4 = Collections.<String>singleton(string3);
            return (serverCommandSource, supplier) -> collection4;
        }
    }
    
    public Collection<String> getExamples() {
        return ScoreHolderArgumentType.EXAMPLES;
    }
    
    static {
        SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> {
            final StringReader stringReader3 = new StringReader(suggestionsBuilder.getInput());
            stringReader3.setCursor(suggestionsBuilder.getStart());
            final EntitySelectorReader entitySelectorReader4 = new EntitySelectorReader(stringReader3);
            try {
                entitySelectorReader4.read();
            }
            catch (CommandSyntaxException ex) {}
            return entitySelectorReader4.listSuggestions(suggestionsBuilder, suggestionsBuilder -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getPlayerNames(), suggestionsBuilder));
        });
        EXAMPLES = Arrays.<String>asList("Player", "0123", "*", "@e");
        EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.scoreHolder.empty", new Object[0]));
    }
    
    public static class SelectorScoreHolder implements ScoreHolder
    {
        private final EntitySelector selector;
        
        public SelectorScoreHolder(final EntitySelector entitySelector) {
            this.selector = entitySelector;
        }
        
        @Override
        public Collection<String> getNames(final ServerCommandSource source, final Supplier<Collection<String>> supplier) throws CommandSyntaxException {
            final List<? extends Entity> list3 = this.selector.getEntities(source);
            if (list3.isEmpty()) {
                throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
            }
            final List<String> list4 = Lists.newArrayList();
            for (final Entity entity6 : list3) {
                list4.add(entity6.getEntityName());
            }
            return list4;
        }
    }
    
    public static class Serializer implements ArgumentSerializer<ScoreHolderArgumentType>
    {
        @Override
        public void toPacket(final ScoreHolderArgumentType scoreHolderArgumentType, final PacketByteBuf packetByteBuf) {
            byte byte3 = 0;
            if (scoreHolderArgumentType.multiple) {
                byte3 |= 0x1;
            }
            packetByteBuf.writeByte(byte3);
        }
        
        @Override
        public ScoreHolderArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
            final byte byte2 = packetByteBuf.readByte();
            final boolean boolean3 = (byte2 & 0x1) != 0x0;
            return new ScoreHolderArgumentType(boolean3);
        }
        
        @Override
        public void toJson(final ScoreHolderArgumentType scoreHolderArgumentType, final JsonObject jsonObject) {
            jsonObject.addProperty("amount", scoreHolderArgumentType.multiple ? "multiple" : "single");
        }
    }
    
    @FunctionalInterface
    public interface ScoreHolder
    {
        Collection<String> getNames(final ServerCommandSource arg1, final Supplier<Collection<String>> arg2) throws CommandSyntaxException;
    }
}
