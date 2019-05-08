package net.minecraft.command.arguments;

import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.util.TagHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import java.util.Set;
import net.minecraft.block.BlockState;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.block.Block;
import net.minecraft.tag.TagManager;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import java.util.function.Predicate;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.tag.Tag;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class BlockPredicateArgumentType implements ArgumentType<BlockPredicate>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION;
    
    public static BlockPredicateArgumentType create() {
        return new BlockPredicateArgumentType();
    }
    
    public BlockPredicate a(final StringReader stringReader) throws CommandSyntaxException {
        final BlockArgumentParser blockArgumentParser2 = new BlockArgumentParser(stringReader, true).parse(true);
        if (blockArgumentParser2.getBlockState() != null) {
            final StatePredicate statePredicate3 = new StatePredicate(blockArgumentParser2.getBlockState(), blockArgumentParser2.getBlockProperties().keySet(), blockArgumentParser2.getNbtData());
            return tagManager -> statePredicate3;
        }
        final Identifier identifier3 = blockArgumentParser2.getTagId();
        final Identifier id;
        final Tag<Object> tag4;
        final BlockArgumentParser blockArgumentParser3;
        return tagManager -> {
            tag4 = tagManager.blocks().get(id);
            if (tag4 == null) {
                throw BlockPredicateArgumentType.UNKNOWN_TAG_EXCEPTION.create(id.toString());
            }
            else {
                return new TagPredicate((Tag)tag4, (Map)blockArgumentParser3.getProperties(), blockArgumentParser3.getNbtData());
            }
        };
    }
    
    public static Predicate<CachedBlockPosition> getBlockPredicate(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((BlockPredicate)context.getArgument(name, (Class)BlockPredicate.class)).create(((ServerCommandSource)context.getSource()).getMinecraftServer().getTagManager());
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringReader3 = new StringReader(builder.getInput());
        stringReader3.setCursor(builder.getStart());
        final BlockArgumentParser blockArgumentParser4 = new BlockArgumentParser(stringReader3, true);
        try {
            blockArgumentParser4.parse(true);
        }
        catch (CommandSyntaxException ex) {}
        return blockArgumentParser4.getSuggestions(builder);
    }
    
    public Collection<String> getExamples() {
        return BlockPredicateArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.block.tag.unknown", new Object[] { object });
            return translatableTextComponent;
        });
    }
    
    static class StatePredicate implements Predicate<CachedBlockPosition>
    {
        private final BlockState state;
        private final Set<Property<?>> properties;
        @Nullable
        private final CompoundTag nbt;
        
        public StatePredicate(final BlockState state, final Set<Property<?>> properties, @Nullable final CompoundTag nbt) {
            this.state = state;
            this.properties = properties;
            this.nbt = nbt;
        }
        
        public boolean a(final CachedBlockPosition cachedBlockPosition) {
            final BlockState blockState2 = cachedBlockPosition.getBlockState();
            if (blockState2.getBlock() != this.state.getBlock()) {
                return false;
            }
            for (final Property<?> property4 : this.properties) {
                if (blockState2.get(property4) != this.state.get(property4)) {
                    return false;
                }
            }
            if (this.nbt != null) {
                final BlockEntity blockEntity3 = cachedBlockPosition.getBlockEntity();
                return blockEntity3 != null && TagHelper.areTagsEqual(this.nbt, blockEntity3.toTag(new CompoundTag()), true);
            }
            return true;
        }
    }
    
    static class TagPredicate implements Predicate<CachedBlockPosition>
    {
        private final Tag<Block> tag;
        @Nullable
        private final CompoundTag nbt;
        private final Map<String, String> properties;
        
        private TagPredicate(final Tag<Block> tag, final Map<String, String> map, @Nullable final CompoundTag nbt) {
            this.tag = tag;
            this.properties = map;
            this.nbt = nbt;
        }
        
        public boolean a(final CachedBlockPosition cachedBlockPosition) {
            final BlockState blockState2 = cachedBlockPosition.getBlockState();
            if (!blockState2.matches(this.tag)) {
                return false;
            }
            for (final Map.Entry<String, String> entry4 : this.properties.entrySet()) {
                final Property<?> property5 = blockState2.getBlock().getStateFactory().getProperty(entry4.getKey());
                if (property5 == null) {
                    return false;
                }
                final Comparable<?> comparable6 = property5.getValue(entry4.getValue()).orElse(null);
                if (comparable6 == null) {
                    return false;
                }
                if (blockState2.get(property5) != comparable6) {
                    return false;
                }
            }
            if (this.nbt != null) {
                final BlockEntity blockEntity3 = cachedBlockPosition.getBlockEntity();
                return blockEntity3 != null && TagHelper.areTagsEqual(this.nbt, blockEntity3.toTag(new CompoundTag()), true);
            }
            return true;
        }
    }
    
    public interface BlockPredicate
    {
        Predicate<CachedBlockPosition> create(final TagManager arg1) throws CommandSyntaxException;
    }
}
