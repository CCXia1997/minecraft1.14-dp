package net.minecraft.command.arguments;

import java.util.AbstractList;
import org.apache.commons.lang3.mutable.MutableBoolean;
import java.util.function.Consumer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.AbstractListTag;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Collections;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.Tag;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class NbtPathArgumentType implements ArgumentType<NbtPath>
{
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION;
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION;
    
    public static NbtPathArgumentType create() {
        return new NbtPathArgumentType();
    }
    
    public static NbtPath getNbtPath(final CommandContext<ServerCommandSource> context, final String name) {
        return (NbtPath)context.getArgument(name, (Class)NbtPath.class);
    }
    
    public NbtPath a(final StringReader stringReader) throws CommandSyntaxException {
        final List<NbtPathNode> list2 = Lists.newArrayList();
        final int integer3 = stringReader.getCursor();
        final Object2IntMap<NbtPathNode> object2IntMap4 = (Object2IntMap<NbtPathNode>)new Object2IntOpenHashMap();
        boolean boolean5 = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            final NbtPathNode nbtPathNode6 = parseNode(stringReader, boolean5);
            list2.add(nbtPathNode6);
            object2IntMap4.put(nbtPathNode6, stringReader.getCursor() - integer3);
            boolean5 = false;
            if (stringReader.canRead()) {
                final char character7 = stringReader.peek();
                if (character7 == ' ' || character7 == '[' || character7 == '{') {
                    continue;
                }
                stringReader.expect('.');
            }
        }
        return new NbtPath(stringReader.getString().substring(integer3, stringReader.getCursor()), list2.<NbtPathNode>toArray(new NbtPathNode[0]), object2IntMap4);
    }
    
    private static NbtPathNode parseNode(final StringReader reader, final boolean first) throws CommandSyntaxException {
        switch (reader.peek()) {
            case '{': {
                if (!first) {
                    throw NbtPathArgumentType.INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
                }
                final CompoundTag compoundTag3 = new StringNbtReader(reader).parseCompoundTag();
                return new EqualCompoundNode(compoundTag3);
            }
            case '[': {
                reader.skip();
                final int integer3 = reader.peek();
                if (integer3 == 123) {
                    final CompoundTag compoundTag4 = new StringNbtReader(reader).parseCompoundTag();
                    reader.expect(']');
                    return new EqualListElementNode(compoundTag4);
                }
                if (integer3 == 93) {
                    reader.skip();
                    return AllListElementsNode.INSTANCE;
                }
                final int integer4 = reader.readInt();
                reader.expect(']');
                return new ListIndexNode(integer4);
            }
            case '\"': {
                final String string3 = reader.readString();
                return readCompoundChildNode(reader, string3);
            }
            default: {
                final String string3 = readName(reader);
                return readCompoundChildNode(reader, string3);
            }
        }
    }
    
    private static NbtPathNode readCompoundChildNode(final StringReader reader, final String name) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '{') {
            final CompoundTag compoundTag3 = new StringNbtReader(reader).parseCompoundTag();
            return new EqualCompundChildNode(name, compoundTag3);
        }
        return new CompoundChildNode(name);
    }
    
    private static String readName(final StringReader reader) throws CommandSyntaxException {
        final int integer2 = reader.getCursor();
        while (reader.canRead() && isNameCharacter(reader.peek())) {
            reader.skip();
        }
        if (reader.getCursor() == integer2) {
            throw NbtPathArgumentType.INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        return reader.getString().substring(integer2, reader.getCursor());
    }
    
    public Collection<String> getExamples() {
        return NbtPathArgumentType.EXAMPLES;
    }
    
    private static boolean isNameCharacter(final char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }
    
    private static Predicate<Tag> getEqualityPredicate(final CompoundTag tag) {
        return tag -> TagHelper.areTagsEqual(tag, tag, true);
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
        INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("arguments.nbtpath.node.invalid", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.nbtpath.nothing_found", new Object[] { object });
            return translatableTextComponent;
        });
    }
    
    public static class NbtPath
    {
        private final String string;
        private final Object2IntMap<NbtPathNode> nodeEndIndices;
        private final NbtPathNode[] nodes;
        
        public NbtPath(final String string, final NbtPathNode[] nodes, final Object2IntMap<NbtPathNode> nodeEndIndices) {
            this.string = string;
            this.nodes = nodes;
            this.nodeEndIndices = nodeEndIndices;
        }
        
        public List<Tag> get(final Tag tag) throws CommandSyntaxException {
            List<Tag> list2 = Collections.<Tag>singletonList(tag);
            for (final NbtPathNode nbtPathNode6 : this.nodes) {
                list2 = nbtPathNode6.get(list2);
                if (list2.isEmpty()) {
                    throw this.createNothingFoundException(nbtPathNode6);
                }
            }
            return list2;
        }
        
        public int count(final Tag tag) {
            List<Tag> list2 = Collections.<Tag>singletonList(tag);
            for (final NbtPathNode nbtPathNode6 : this.nodes) {
                list2 = nbtPathNode6.get(list2);
                if (list2.isEmpty()) {
                    return 0;
                }
            }
            return list2.size();
        }
        
        private List<Tag> getParents(final Tag tag) throws CommandSyntaxException {
            List<Tag> list2 = Collections.<Tag>singletonList(tag);
            for (int integer3 = 0; integer3 < this.nodes.length - 1; ++integer3) {
                final NbtPathNode nbtPathNode4 = this.nodes[integer3];
                final int integer4 = integer3 + 1;
                list2 = nbtPathNode4.putIfAbsent(list2, this.nodes[integer4]::createParent);
                if (list2.isEmpty()) {
                    throw this.createNothingFoundException(nbtPathNode4);
                }
            }
            return list2;
        }
        
        public List<Tag> putIfAbsent(final Tag tag, final Supplier<Tag> supplier) throws CommandSyntaxException {
            final List<Tag> list3 = this.getParents(tag);
            final NbtPathNode nbtPathNode4 = this.nodes[this.nodes.length - 1];
            return nbtPathNode4.putIfAbsent(list3, supplier);
        }
        
        private static int forEach(final List<Tag> tags, final Function<Tag, Integer> function) {
            return tags.stream().<Integer>map(function).reduce(Integer.valueOf(0), (integer1, integer2) -> integer1 + integer2);
        }
        
        public int put(final Tag tag, final Supplier<Tag> supplier) throws CommandSyntaxException {
            final List<Tag> list3 = this.getParents(tag);
            final NbtPathNode nbtPathNode4 = this.nodes[this.nodes.length - 1];
            return forEach(list3, tag -> nbtPathNode4.put(tag, supplier));
        }
        
        public int remove(final Tag tag) {
            List<Tag> list2 = Collections.<Tag>singletonList(tag);
            for (int integer3 = 0; integer3 < this.nodes.length - 1; ++integer3) {
                list2 = this.nodes[integer3].get(list2);
            }
            final NbtPathNode nbtPathNode3 = this.nodes[this.nodes.length - 1];
            return forEach(list2, nbtPathNode3::remove);
        }
        
        private CommandSyntaxException createNothingFoundException(final NbtPathNode node) {
            final int integer2 = this.nodeEndIndices.getInt(node);
            return NbtPathArgumentType.NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, integer2));
        }
        
        @Override
        public String toString() {
            return this.string;
        }
    }
    
    interface NbtPathNode
    {
        void get(final Tag arg1, final List<Tag> arg2);
        
        void putIfAbsent(final Tag arg1, final Supplier<Tag> arg2, final List<Tag> arg3);
        
        Tag createParent();
        
        int put(final Tag arg1, final Supplier<Tag> arg2);
        
        int remove(final Tag arg1);
        
        default List<Tag> get(final List<Tag> tags) {
            return this.get(tags, this::get);
        }
        
        default List<Tag> putIfAbsent(final List<Tag> tags, final Supplier<Tag> supplier) {
            return this.get(tags, (tag, list) -> this.putIfAbsent(tag, supplier, list));
        }
        
        default List<Tag> get(final List<Tag> tags, final BiConsumer<Tag, List<Tag>> getter) {
            final List<Tag> list3 = Lists.newArrayList();
            for (final Tag tag5 : tags) {
                getter.accept(tag5, list3);
            }
            return list3;
        }
    }
    
    static class CompoundChildNode implements NbtPathNode
    {
        private final String name;
        
        public CompoundChildNode(final String string) {
            this.name = string;
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof CompoundTag) {
                final Tag tag2 = ((CompoundTag)tag).getTag(this.name);
                if (tag2 != null) {
                    results.add(tag2);
                }
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag4 = (CompoundTag)tag;
                Tag tag2;
                if (compoundTag4.containsKey(this.name)) {
                    tag2 = compoundTag4.getTag(this.name);
                }
                else {
                    tag2 = supplier.get();
                    compoundTag4.put(this.name, tag2);
                }
                results.add(tag2);
            }
        }
        
        @Override
        public Tag createParent() {
            return new CompoundTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag3 = (CompoundTag)tag;
                final Tag tag2 = supplier.get();
                final Tag tag3 = compoundTag3.put(this.name, tag2);
                if (!tag2.equals(tag3)) {
                    return 1;
                }
            }
            return 0;
        }
        
        @Override
        public int remove(final Tag tag) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag2 = (CompoundTag)tag;
                if (compoundTag2.containsKey(this.name)) {
                    compoundTag2.remove(this.name);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class ListIndexNode implements NbtPathNode
    {
        private final int index;
        
        public ListIndexNode(final int index) {
            this.index = index;
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                final AbstractListTag<?> abstractListTag3 = tag;
                final int integer4 = abstractListTag3.size();
                final int integer5 = (this.index < 0) ? (integer4 + this.index) : this.index;
                if (0 <= integer5 && integer5 < integer4) {
                    results.add(abstractListTag3.get(integer5));
                }
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            this.get(tag, results);
        }
        
        @Override
        public Tag createParent() {
            return new ListTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            if (tag instanceof AbstractListTag) {
                final AbstractListTag<?> abstractListTag3 = tag;
                final int integer4 = abstractListTag3.size();
                final int integer5 = (this.index < 0) ? (integer4 + this.index) : this.index;
                if (0 <= integer5 && integer5 < integer4) {
                    final Tag tag2 = abstractListTag3.get(integer5);
                    final Tag tag3 = supplier.get();
                    if (!tag3.equals(tag2) && abstractListTag3.setTag(integer5, tag3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public int remove(final Tag tag) {
            if (tag instanceof AbstractListTag) {
                final AbstractListTag<?> abstractListTag2 = tag;
                final int integer3 = abstractListTag2.size();
                final int integer4 = (this.index < 0) ? (integer3 + this.index) : this.index;
                if (0 <= integer4 && integer4 < integer3) {
                    abstractListTag2.c(integer4);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class EqualListElementNode implements NbtPathNode
    {
        private final CompoundTag tag;
        private final Predicate<Tag> predicate;
        
        public EqualListElementNode(final CompoundTag compoundTag) {
            this.tag = compoundTag;
            this.predicate = getEqualityPredicate(compoundTag);
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof ListTag) {
                final ListTag listTag3 = (ListTag)tag;
                listTag3.stream().filter(this.predicate).forEach(results::add);
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            final MutableBoolean mutableBoolean4 = new MutableBoolean();
            if (tag instanceof ListTag) {
                final ListTag listTag5 = (ListTag)tag;
                final MutableBoolean mutableBoolean5;
                listTag5.stream().filter(this.predicate).forEach(tag -> {
                    results.add(tag);
                    mutableBoolean5.setTrue();
                    return;
                });
                if (mutableBoolean4.isFalse()) {
                    final CompoundTag compoundTag6 = this.tag.copy();
                    ((AbstractList<CompoundTag>)listTag5).add(compoundTag6);
                    results.add(compoundTag6);
                }
            }
        }
        
        @Override
        public Tag createParent() {
            return new ListTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            int integer3 = 0;
            if (tag instanceof ListTag) {
                final ListTag listTag4 = (ListTag)tag;
                final int integer4 = listTag4.size();
                if (integer4 == 0) {
                    listTag4.add(supplier.get());
                    ++integer3;
                }
                else {
                    for (int integer5 = 0; integer5 < integer4; ++integer5) {
                        final Tag tag2 = listTag4.k(integer5);
                        if (this.predicate.test(tag2)) {
                            final Tag tag3 = supplier.get();
                            if (!tag3.equals(tag2) && listTag4.setTag(integer5, tag3)) {
                                ++integer3;
                            }
                        }
                    }
                }
            }
            return integer3;
        }
        
        @Override
        public int remove(final Tag tag) {
            int integer2 = 0;
            if (tag instanceof ListTag) {
                final ListTag listTag3 = (ListTag)tag;
                for (int integer3 = listTag3.size() - 1; integer3 >= 0; --integer3) {
                    if (this.predicate.test(listTag3.k(integer3))) {
                        listTag3.c(integer3);
                        ++integer2;
                    }
                }
            }
            return integer2;
        }
    }
    
    static class AllListElementsNode implements NbtPathNode
    {
        public static final AllListElementsNode INSTANCE;
        
        private AllListElementsNode() {
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                results.addAll(tag);
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                final AbstractListTag<?> abstractListTag4 = tag;
                if (abstractListTag4.isEmpty()) {
                    final Tag tag2 = supplier.get();
                    if (abstractListTag4.addTag(0, tag2)) {
                        results.add(tag2);
                    }
                }
                else {
                    results.addAll(abstractListTag4);
                }
            }
        }
        
        @Override
        public Tag createParent() {
            return new ListTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            if (!(tag instanceof AbstractListTag)) {
                return 0;
            }
            final AbstractListTag<?> abstractListTag3 = tag;
            final int integer4 = abstractListTag3.size();
            if (integer4 == 0) {
                abstractListTag3.addTag(0, supplier.get());
                return 1;
            }
            final Tag tag2 = supplier.get();
            final int integer5 = integer4 - (int)abstractListTag3.stream().filter(tag2::equals).count();
            if (integer5 == 0) {
                return 0;
            }
            abstractListTag3.clear();
            if (!abstractListTag3.addTag(0, tag2)) {
                return 0;
            }
            for (int integer6 = 1; integer6 < integer4; ++integer6) {
                abstractListTag3.addTag(integer6, supplier.get());
            }
            return integer5;
        }
        
        @Override
        public int remove(final Tag tag) {
            if (tag instanceof AbstractListTag) {
                final AbstractListTag<?> abstractListTag2 = tag;
                final int integer3 = abstractListTag2.size();
                if (integer3 > 0) {
                    abstractListTag2.clear();
                    return integer3;
                }
            }
            return 0;
        }
        
        static {
            INSTANCE = new AllListElementsNode();
        }
    }
    
    static class EqualCompundChildNode implements NbtPathNode
    {
        private final String name;
        private final CompoundTag tag;
        private final Predicate<Tag> predicate;
        
        public EqualCompundChildNode(final String name, final CompoundTag tag) {
            this.name = name;
            this.tag = tag;
            this.predicate = getEqualityPredicate(tag);
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof CompoundTag) {
                final Tag tag2 = ((CompoundTag)tag).getTag(this.name);
                if (this.predicate.test(tag2)) {
                    results.add(tag2);
                }
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag4 = (CompoundTag)tag;
                Tag tag2 = compoundTag4.getTag(this.name);
                if (tag2 == null) {
                    tag2 = this.tag.copy();
                    compoundTag4.put(this.name, tag2);
                    results.add(tag2);
                }
                else if (this.predicate.test(tag2)) {
                    results.add(tag2);
                }
            }
        }
        
        @Override
        public Tag createParent() {
            return new CompoundTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag3 = (CompoundTag)tag;
                final Tag tag2 = compoundTag3.getTag(this.name);
                if (this.predicate.test(tag2)) {
                    final Tag tag3 = supplier.get();
                    if (!tag3.equals(tag2)) {
                        compoundTag3.put(this.name, tag3);
                        return 1;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public int remove(final Tag tag) {
            if (tag instanceof CompoundTag) {
                final CompoundTag compoundTag2 = (CompoundTag)tag;
                final Tag tag2 = compoundTag2.getTag(this.name);
                if (this.predicate.test(tag2)) {
                    compoundTag2.remove(this.name);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class EqualCompoundNode implements NbtPathNode
    {
        private final Predicate<Tag> predicate;
        
        public EqualCompoundNode(final CompoundTag tag) {
            this.predicate = getEqualityPredicate(tag);
        }
        
        @Override
        public void get(final Tag tag, final List<Tag> results) {
            if (tag instanceof CompoundTag && this.predicate.test(tag)) {
                results.add(tag);
            }
        }
        
        @Override
        public void putIfAbsent(final Tag tag, final Supplier<Tag> supplier, final List<Tag> results) {
            this.get(tag, results);
        }
        
        @Override
        public Tag createParent() {
            return new CompoundTag();
        }
        
        @Override
        public int put(final Tag tag, final Supplier<Tag> supplier) {
            return 0;
        }
        
        @Override
        public int remove(final Tag tag) {
            return 0;
        }
    }
}
