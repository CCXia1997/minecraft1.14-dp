package net.minecraft.command.arguments;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import net.minecraft.nbt.StringNbtReader;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.command.CommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.tag.BlockTags;
import java.util.Iterator;
import java.util.Locale;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;
import net.minecraft.state.property.Property;
import java.util.Map;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BlockArgumentParser
{
    public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION;
    public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION;
    public static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION;
    public static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION;
    public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION;
    public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION;
    public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION;
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT;
    private final StringReader reader;
    private final boolean allowTag;
    private final Map<Property<?>, Comparable<?>> blockProperties;
    private final Map<String, String> tagProperties;
    private Identifier blockId;
    private StateFactory<Block, BlockState> stateFactory;
    private BlockState blockState;
    @Nullable
    private CompoundTag data;
    private Identifier tagId;
    private int cursorPos;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;
    
    public BlockArgumentParser(final StringReader stringReader, final boolean boolean2) {
        this.blockProperties = Maps.newHashMap();
        this.tagProperties = Maps.newHashMap();
        this.blockId = new Identifier("");
        this.tagId = new Identifier("");
        this.suggestions = BlockArgumentParser.SUGGEST_DEFAULT;
        this.reader = stringReader;
        this.allowTag = boolean2;
    }
    
    public Map<Property<?>, Comparable<?>> getBlockProperties() {
        return this.blockProperties;
    }
    
    @Nullable
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    @Nullable
    public CompoundTag getNbtData() {
        return this.data;
    }
    
    @Nullable
    public Identifier getTagId() {
        return this.tagId;
    }
    
    public BlockArgumentParser parse(final boolean boolean1) throws CommandSyntaxException {
        this.suggestions = this::suggestBlockOrTagId;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.parseTagId();
            this.suggestions = this::suggestSnbtOrTagProperties;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.parseTagProperties();
                this.suggestions = this::suggestSnbt;
            }
        }
        else {
            this.parseBlockId();
            this.suggestions = this::suggestSnbtOrBlockProperties;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.parseBlockProperties();
                this.suggestions = this::suggestSnbt;
            }
        }
        if (boolean1 && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = BlockArgumentParser.SUGGEST_DEFAULT;
            this.parseSnbt();
        }
        return this;
    }
    
    private CompletableFuture<Suggestions> suggestBlockPropertiesOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestBlockProperties(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestTagPropertiesOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestTagProperties(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestBlockProperties(final SuggestionsBuilder suggestionsBuilder) {
        final String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final Property<?> property4 : this.blockState.getProperties()) {
            if (!this.blockProperties.containsKey(property4) && property4.getName().startsWith(string2)) {
                suggestionsBuilder.suggest(property4.getName() + '=');
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestTagProperties(final SuggestionsBuilder suggestionsBuilder) {
        final String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        if (this.tagId != null && !this.tagId.getPath().isEmpty()) {
            final Tag<Block> tag3 = BlockTags.getContainer().get(this.tagId);
            if (tag3 != null) {
                for (final Block block5 : tag3.values()) {
                    for (final Property<?> property7 : block5.getStateFactory().getProperties()) {
                        if (!this.tagProperties.containsKey(property7.getName()) && property7.getName().startsWith(string2)) {
                            suggestionsBuilder.suggest(property7.getName() + '=');
                        }
                    }
                }
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestSnbt(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty() && this.hasTileEntity()) {
            suggestionsBuilder.suggest(String.valueOf('{'));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private boolean hasTileEntity() {
        if (this.blockState != null) {
            return this.blockState.getBlock().hasBlockEntity();
        }
        if (this.tagId != null) {
            final Tag<Block> tag1 = BlockTags.getContainer().get(this.tagId);
            if (tag1 != null) {
                for (final Block block3 : tag1.values()) {
                    if (block3.hasBlockEntity()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private CompletableFuture<Suggestions> suggestEqualsCharacter(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('='));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestCommaOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        if (suggestionsBuilder.getRemaining().isEmpty() && this.blockProperties.size() < this.blockState.getProperties().size()) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private static <T extends Comparable<T>> SuggestionsBuilder suggestPropertyValues(final SuggestionsBuilder suggestionsBuilder, final Property<T> property) {
        for (final T comparable4 : property.getValues()) {
            if (comparable4 instanceof Integer) {
                suggestionsBuilder.suggest((int)comparable4);
            }
            else {
                suggestionsBuilder.suggest(property.getValueAsString(comparable4));
            }
        }
        return suggestionsBuilder;
    }
    
    private CompletableFuture<Suggestions> suggestTagPropertyValues(final SuggestionsBuilder suggestionsBuilder, final String string) {
        boolean boolean3 = false;
        if (this.tagId != null && !this.tagId.getPath().isEmpty()) {
            final Tag<Block> tag4 = BlockTags.getContainer().get(this.tagId);
            if (tag4 != null) {
                for (final Block block6 : tag4.values()) {
                    final Property<?> property7 = block6.getStateFactory().getProperty(string);
                    if (property7 != null) {
                        BlockArgumentParser.suggestPropertyValues(suggestionsBuilder, property7);
                    }
                    if (!boolean3) {
                        for (final Property<?> property8 : block6.getStateFactory().getProperties()) {
                            if (!this.tagProperties.containsKey(property8.getName())) {
                                boolean3 = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (boolean3) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        suggestionsBuilder.suggest(String.valueOf(']'));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestSnbtOrTagProperties(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            final Tag<Block> tag2 = BlockTags.getContainer().get(this.tagId);
            if (tag2 != null) {
                boolean boolean3 = false;
                boolean boolean4 = false;
                for (final Block block6 : tag2.values()) {
                    boolean3 |= !block6.getStateFactory().getProperties().isEmpty();
                    boolean4 |= block6.hasBlockEntity();
                    if (boolean3 && boolean4) {
                        break;
                    }
                }
                if (boolean3) {
                    suggestionsBuilder.suggest(String.valueOf('['));
                }
                if (boolean4) {
                    suggestionsBuilder.suggest(String.valueOf('{'));
                }
            }
        }
        return this.suggestIdentifiers(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestSnbtOrBlockProperties(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            if (!this.blockState.getBlock().getStateFactory().getProperties().isEmpty()) {
                suggestionsBuilder.suggest(String.valueOf('['));
            }
            if (this.blockState.getBlock().hasBlockEntity()) {
                suggestionsBuilder.suggest(String.valueOf('{'));
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestIdentifiers(final SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(BlockTags.getContainer().getKeys(), suggestionsBuilder.createOffset(this.cursorPos).add(suggestionsBuilder));
    }
    
    private CompletableFuture<Suggestions> suggestBlockOrTagId(final SuggestionsBuilder suggestionsBuilder) {
        if (this.allowTag) {
            CommandSource.suggestIdentifiers(BlockTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
        }
        CommandSource.suggestIdentifiers(Registry.BLOCK.getIds(), suggestionsBuilder);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    public void parseBlockId() throws CommandSyntaxException {
        final int integer1 = this.reader.getCursor();
        this.blockId = Identifier.parse(this.reader);
        final Block block2 = Registry.BLOCK.getOrEmpty(this.blockId).<Throwable>orElseThrow(() -> {
            this.reader.setCursor(integer1);
            return BlockArgumentParser.INVALID_BLOCK_ID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString());
        });
        this.stateFactory = block2.getStateFactory();
        this.blockState = block2.getDefaultState();
    }
    
    public void parseTagId() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw BlockArgumentParser.DISALLOWED_TAG_EXCEPTION.create();
        }
        this.suggestions = this::suggestIdentifiers;
        this.reader.expect('#');
        this.cursorPos = this.reader.getCursor();
        this.tagId = Identifier.parse(this.reader);
    }
    
    public void parseBlockProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestBlockPropertiesOrEnd;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            final int integer1 = this.reader.getCursor();
            final String string2 = this.reader.readString();
            final Property<?> property3 = this.stateFactory.getProperty(string2);
            if (property3 == null) {
                this.reader.setCursor(integer1);
                throw BlockArgumentParser.UNKNOWN_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), string2);
            }
            if (this.blockProperties.containsKey(property3)) {
                this.reader.setCursor(integer1);
                throw BlockArgumentParser.DUPLICATE_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), string2);
            }
            this.reader.skipWhitespace();
            this.suggestions = this::suggestEqualsCharacter;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                throw BlockArgumentParser.EMPTY_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), string2);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)(suggestionsBuilder -> BlockArgumentParser.<Comparable>suggestPropertyValues(suggestionsBuilder, property3).buildFuture());
            final int integer2 = this.reader.getCursor();
            this.parsePropertyValue(property3, this.reader.readString(), integer2);
            this.suggestions = this::suggestCommaOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
                continue;
            }
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = this::suggestBlockProperties;
            }
            else {
                if (this.reader.peek() == ']') {
                    break;
                }
                throw BlockArgumentParser.UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        if (this.reader.canRead()) {
            this.reader.skip();
            return;
        }
        throw BlockArgumentParser.UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
    }
    
    public void parseTagProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestTagPropertiesOrEnd;
        int integer1 = -1;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            final int integer2 = this.reader.getCursor();
            final String string3 = this.reader.readString();
            if (this.tagProperties.containsKey(string3)) {
                this.reader.setCursor(integer2);
                throw BlockArgumentParser.DUPLICATE_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), string3);
            }
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(integer2);
                throw BlockArgumentParser.EMPTY_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), string3);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)(suggestionsBuilder -> this.suggestTagPropertyValues(suggestionsBuilder, string3));
            integer1 = this.reader.getCursor();
            final String string4 = this.reader.readString();
            this.tagProperties.put(string3, string4);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
                continue;
            }
            integer1 = -1;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = this::suggestTagProperties;
            }
            else {
                if (this.reader.peek() == ']') {
                    break;
                }
                throw BlockArgumentParser.UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        if (this.reader.canRead()) {
            this.reader.skip();
            return;
        }
        if (integer1 >= 0) {
            this.reader.setCursor(integer1);
        }
        throw BlockArgumentParser.UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
    }
    
    public void parseSnbt() throws CommandSyntaxException {
        this.data = new StringNbtReader(this.reader).parseCompoundTag();
    }
    
    private <T extends Comparable<T>> void parsePropertyValue(final Property<T> property, final String string, final int integer) throws CommandSyntaxException {
        final Optional<T> optional4 = property.getValue(string);
        if (optional4.isPresent()) {
            this.blockState = ((AbstractPropertyContainer<O, BlockState>)this.blockState).<T, T>with(property, optional4.get());
            this.blockProperties.put(property, optional4.get());
            return;
        }
        this.reader.setCursor(integer);
        throw BlockArgumentParser.INVALID_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, this.blockId.toString(), property.getName(), string);
    }
    
    public static String stringifyBlockState(final BlockState blockState) {
        final StringBuilder stringBuilder2 = new StringBuilder(Registry.BLOCK.getId(blockState.getBlock()).toString());
        if (!blockState.getProperties().isEmpty()) {
            stringBuilder2.append('[');
            boolean boolean3 = false;
            for (final Map.Entry<Property<?>, Comparable<?>> entry5 : blockState.getEntries().entrySet()) {
                if (boolean3) {
                    stringBuilder2.append(',');
                }
                BlockArgumentParser.stringifyProperty(stringBuilder2, entry5.getKey(), entry5.getValue());
                boolean3 = true;
            }
            stringBuilder2.append(']');
        }
        return stringBuilder2.toString();
    }
    
    private static <T extends Comparable<T>> void stringifyProperty(final StringBuilder stringBuilder, final Property<T> property, final Comparable<?> comparable) {
        stringBuilder.append(property.getName());
        stringBuilder.append('=');
        stringBuilder.append(property.getValueAsString((T)comparable));
    }
    
    public CompletableFuture<Suggestions> getSuggestions(final SuggestionsBuilder suggestionsBuilder) {
        return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
    }
    
    public Map<String, String> getProperties() {
        return this.tagProperties;
    }
    
    static {
        DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.block.tag.disallowed", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.block.id.invalid", new Object[] { object });
            return translatableTextComponent;
        });
        UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.block.property.unknown", new Object[] { object1, object2 }));
        DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.block.property.duplicate", new Object[] { object2, object1 }));
        INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType((object1, object2, object3) -> new TranslatableTextComponent("argument.block.property.invalid", new Object[] { object1, object3, object2 }));
        EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.block.property.novalue", new Object[] { object1, object2 }));
        UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.block.property.unclosed", new Object[0]));
        SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
    }
}
