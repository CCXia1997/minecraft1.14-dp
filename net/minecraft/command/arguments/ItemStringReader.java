package net.minecraft.command.arguments;

import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import net.minecraft.tag.ItemTags;
import net.minecraft.nbt.StringNbtReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;
import java.util.Map;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ItemStringReader
{
    public static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION;
    public static final DynamicCommandExceptionType ID_INVALID_EXCEPTION;
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> NBT_SUGGESTION_PROVIDER;
    private final StringReader reader;
    private final boolean allowTag;
    private final Map<Property<?>, Comparable<?>> f;
    private Item item;
    @Nullable
    private CompoundTag tag;
    private Identifier id;
    private int cursor;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;
    
    public ItemStringReader(final StringReader reader, final boolean allowTag) {
        this.f = Maps.newHashMap();
        this.id = new Identifier("");
        this.suggestions = ItemStringReader.NBT_SUGGESTION_PROVIDER;
        this.reader = reader;
        this.allowTag = allowTag;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    public void readItem() throws CommandSyntaxException {
        final int integer1 = this.reader.getCursor();
        final Identifier identifier2 = Identifier.parse(this.reader);
        final int cursor;
        final Identifier identifier3;
        this.item = Registry.ITEM.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> {
            this.reader.setCursor(cursor);
            return ItemStringReader.ID_INVALID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, identifier3.toString());
        });
    }
    
    public void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw ItemStringReader.TAG_DISALLOWED_EXCEPTION.create();
        }
        this.suggestions = this::suggestTag;
        this.reader.expect('#');
        this.cursor = this.reader.getCursor();
        this.id = Identifier.parse(this.reader);
    }
    
    public void readNbt() throws CommandSyntaxException {
        this.tag = new StringNbtReader(this.reader).parseCompoundTag();
    }
    
    public ItemStringReader consume() throws CommandSyntaxException {
        this.suggestions = this::suggestAny;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        }
        else {
            this.readItem();
            this.suggestions = this::suggestItem;
        }
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = ItemStringReader.NBT_SUGGESTION_PROVIDER;
            this.readNbt();
        }
        return this;
    }
    
    private CompletableFuture<Suggestions> suggestItem(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('{'));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestTag(final SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(ItemTags.getContainer().getKeys(), suggestionsBuilder.createOffset(this.cursor));
    }
    
    private CompletableFuture<Suggestions> suggestAny(final SuggestionsBuilder suggestionsBuilder) {
        if (this.allowTag) {
            CommandSource.suggestIdentifiers(ItemTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
        }
        return CommandSource.suggestIdentifiers(Registry.ITEM.getIds(), suggestionsBuilder);
    }
    
    public CompletableFuture<Suggestions> a(final SuggestionsBuilder suggestionsBuilder) {
        return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
    }
    
    static {
        TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.item.tag.disallowed", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.item.id.invalid", new Object[] { object });
            return translatableTextComponent;
        });
        NBT_SUGGESTION_PROVIDER = SuggestionsBuilder::buildFuture;
    }
}
