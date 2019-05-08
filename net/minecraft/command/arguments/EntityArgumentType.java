package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Arrays;
import com.google.common.collect.Iterables;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.command.EntitySelectorReader;
import com.mojang.brigadier.StringReader;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.EntitySelector;
import com.mojang.brigadier.arguments.ArgumentType;

public class EntityArgumentType implements ArgumentType<EntitySelector>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION;
    public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION;
    public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION;
    public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION;
    public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION;
    public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION;
    private final boolean singleTarget;
    private final boolean playersOnly;
    
    protected EntityArgumentType(final boolean boolean1, final boolean boolean2) {
        this.singleTarget = boolean1;
        this.playersOnly = boolean2;
    }
    
    public static EntityArgumentType entity() {
        return new EntityArgumentType(true, false);
    }
    
    public static Entity getEntity(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, (Class)EntitySelector.class)).getEntity((ServerCommandSource)context.getSource());
    }
    
    public static EntityArgumentType entities() {
        return new EntityArgumentType(false, false);
    }
    
    public static Collection<? extends Entity> getEntities(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        final Collection<? extends Entity> collection3 = getOptionalEntities(context, name);
        if (collection3.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection3;
    }
    
    public static Collection<? extends Entity> getOptionalEntities(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).getEntities((ServerCommandSource)commandContext.getSource());
    }
    
    public static Collection<ServerPlayerEntity> getOptionalPlayers(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).getPlayers((ServerCommandSource)commandContext.getSource());
    }
    
    public static EntityArgumentType player() {
        return new EntityArgumentType(true, true);
    }
    
    public static ServerPlayerEntity getPlayer(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).getPlayer((ServerCommandSource)commandContext.getSource());
    }
    
    public static EntityArgumentType players() {
        return new EntityArgumentType(false, true);
    }
    
    public static Collection<ServerPlayerEntity> getPlayers(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        final List<ServerPlayerEntity> list3 = ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).getPlayers((ServerCommandSource)commandContext.getSource());
        if (list3.isEmpty()) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list3;
    }
    
    public EntitySelector a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = 0;
        final EntitySelectorReader entitySelectorReader3 = new EntitySelectorReader(stringReader);
        final EntitySelector entitySelector4 = entitySelectorReader3.read();
        if (entitySelector4.getCount() > 1 && this.singleTarget) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
            }
            stringReader.setCursor(0);
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        else {
            if (entitySelector4.includesNonPlayers() && this.playersOnly && !entitySelector4.isSenderOnly()) {
                stringReader.setCursor(0);
                throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
            }
            return entitySelector4;
        }
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            final StringReader stringReader3 = new StringReader(builder.getInput());
            stringReader3.setCursor(builder.getStart());
            final CommandSource commandSource4 = (CommandSource)context.getSource();
            final EntitySelectorReader entitySelectorReader5 = new EntitySelectorReader(stringReader3, commandSource4.hasPermissionLevel(2));
            try {
                entitySelectorReader5.read();
            }
            catch (CommandSyntaxException ex) {}
            final CommandSource commandSource5;
            final Collection<String> collection3;
            final Iterable<String> iterable4;
            return entitySelectorReader5.listSuggestions(builder, suggestionsBuilder -> {
                collection3 = commandSource5.getPlayerNames();
                iterable4 = (Iterable<String>)(this.playersOnly ? collection3 : Iterables.concat(collection3, commandSource5.getEntitySuggestions()));
                CommandSource.suggestMatching(iterable4, suggestionsBuilder);
                return;
            });
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return EntityArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
        TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.toomany", new Object[0]));
        TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.player.toomany", new Object[0]));
        PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.player.entities", new Object[0]));
        ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.notfound.entity", new Object[0]));
        PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.notfound.player", new Object[0]));
        NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.selector.not_allowed", new Object[0]));
    }
    
    public static class Serializer implements ArgumentSerializer<EntityArgumentType>
    {
        @Override
        public void toPacket(final EntityArgumentType entityArgumentType, final PacketByteBuf packetByteBuf) {
            byte byte3 = 0;
            if (entityArgumentType.singleTarget) {
                byte3 |= 0x1;
            }
            if (entityArgumentType.playersOnly) {
                byte3 |= 0x2;
            }
            packetByteBuf.writeByte(byte3);
        }
        
        @Override
        public EntityArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
            final byte byte2 = packetByteBuf.readByte();
            return new EntityArgumentType((byte2 & 0x1) != 0x0, (byte2 & 0x2) != 0x0);
        }
        
        @Override
        public void toJson(final EntityArgumentType entityArgumentType, final JsonObject jsonObject) {
            jsonObject.addProperty("amount", entityArgumentType.singleTarget ? "single" : "multiple");
            jsonObject.addProperty("type", entityArgumentType.playersOnly ? "players" : "entities");
        }
    }
}
