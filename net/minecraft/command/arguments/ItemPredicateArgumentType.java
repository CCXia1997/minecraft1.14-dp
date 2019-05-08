package net.minecraft.command.arguments;

import net.minecraft.util.TagHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgument>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION;
    
    public static ItemPredicateArgumentType create() {
        return new ItemPredicateArgumentType();
    }
    
    public ItemPredicateArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final ItemStringReader itemStringReader2 = new ItemStringReader(stringReader, true).consume();
        if (itemStringReader2.getItem() != null) {
            final ItemPredicate itemPredicate3 = new ItemPredicate(itemStringReader2.getItem(), itemStringReader2.getTag());
            return commandContext -> itemPredicate3;
        }
        final Identifier identifier3 = itemStringReader2.getId();
        final Identifier id;
        final Tag<Object> tag4;
        final ItemStringReader itemStringReader3;
        return commandContext -> {
            tag4 = (Tag<Object>)((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager().items().get(id);
            if (tag4 == null) {
                throw ItemPredicateArgumentType.UNKNOWN_TAG_EXCEPTION.create(id.toString());
            }
            else {
                return new TagPredicate((Tag<Item>)tag4, itemStringReader3.getTag());
            }
        };
    }
    
    public static Predicate<ItemStack> getItemPredicate(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((ItemPredicateArgument)context.getArgument(name, (Class)ItemPredicateArgument.class)).create(context);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringReader3 = new StringReader(builder.getInput());
        stringReader3.setCursor(builder.getStart());
        final ItemStringReader itemStringReader4 = new ItemStringReader(stringReader3, true);
        try {
            itemStringReader4.consume();
        }
        catch (CommandSyntaxException ex) {}
        return itemStringReader4.a(builder);
    }
    
    public Collection<String> getExamples() {
        return ItemPredicateArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.item.tag.unknown", new Object[] { object });
            return translatableTextComponent;
        });
    }
    
    static class ItemPredicate implements Predicate<ItemStack>
    {
        private final Item item;
        @Nullable
        private final CompoundTag compound;
        
        public ItemPredicate(final Item item, @Nullable final CompoundTag compoundTag) {
            this.item = item;
            this.compound = compoundTag;
        }
        
        public boolean a(final ItemStack itemStack) {
            return itemStack.getItem() == this.item && TagHelper.areTagsEqual(this.compound, itemStack.getTag(), true);
        }
    }
    
    static class TagPredicate implements Predicate<ItemStack>
    {
        private final Tag<Item> tag;
        @Nullable
        private final CompoundTag compound;
        
        public TagPredicate(final Tag<Item> tag, @Nullable final CompoundTag compoundTag) {
            this.tag = tag;
            this.compound = compoundTag;
        }
        
        public boolean a(final ItemStack context) {
            return this.tag.contains(context.getItem()) && TagHelper.areTagsEqual(this.compound, context.getTag(), true);
        }
    }
    
    public interface ItemPredicateArgument
    {
        Predicate<ItemStack> create(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
    }
}
