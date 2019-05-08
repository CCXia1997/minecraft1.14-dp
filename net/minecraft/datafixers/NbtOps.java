package net.minecraft.datafixers;

import java.util.Objects;
import java.util.List;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterators;
import net.minecraft.nbt.LongArrayTag;
import java.util.stream.LongStream;
import java.util.Arrays;
import net.minecraft.nbt.IntArrayTag;
import java.util.stream.IntStream;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.nbt.ByteArrayTag;
import java.nio.ByteBuffer;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.AbstractNumberTag;
import java.util.Optional;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.Tag;
import com.mojang.datafixers.types.DynamicOps;

public class NbtOps implements DynamicOps<Tag>
{
    public static final NbtOps INSTANCE;
    
    protected NbtOps() {
    }
    
    public Tag a() {
        return new EndTag();
    }
    
    public Type<?> a(final Tag tag) {
        switch (tag.getType()) {
            case 0: {
                return DSL.nilType();
            }
            case 1: {
                return DSL.byteType();
            }
            case 2: {
                return DSL.shortType();
            }
            case 3: {
                return DSL.intType();
            }
            case 4: {
                return DSL.longType();
            }
            case 5: {
                return DSL.floatType();
            }
            case 6: {
                return DSL.doubleType();
            }
            case 7: {
                return DSL.list(DSL.byteType());
            }
            case 8: {
                return DSL.string();
            }
            case 9: {
                return DSL.list(DSL.remainderType());
            }
            case 10: {
                return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
            }
            case 11: {
                return DSL.list(DSL.intType());
            }
            case 12: {
                return DSL.list(DSL.longType());
            }
            default: {
                return DSL.remainderType();
            }
        }
    }
    
    public Optional<Number> b(final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            return Optional.<Number>of(((AbstractNumberTag)tag).getNumber());
        }
        return Optional.<Number>empty();
    }
    
    public Tag a(final Number number) {
        return new DoubleTag(number.doubleValue());
    }
    
    public Tag a(final byte byte1) {
        return new ByteTag(byte1);
    }
    
    public Tag a(final short short1) {
        return new ShortTag(short1);
    }
    
    public Tag a(final int integer) {
        return new IntTag(integer);
    }
    
    public Tag a(final long long1) {
        return new LongTag(long1);
    }
    
    public Tag a(final float float1) {
        return new FloatTag(float1);
    }
    
    public Tag a(final double double1) {
        return new DoubleTag(double1);
    }
    
    public Optional<String> c(final Tag tag) {
        if (tag instanceof StringTag) {
            return Optional.<String>of(tag.asString());
        }
        return Optional.<String>empty();
    }
    
    public Tag a(final String string) {
        return new StringTag(string);
    }
    
    public Tag a(final Tag tag1, final Tag tag2) {
        if (tag2 instanceof EndTag) {
            return tag1;
        }
        if (tag1 instanceof CompoundTag) {
            if (tag2 instanceof CompoundTag) {
                final CompoundTag compoundTag4 = new CompoundTag();
                final CompoundTag compoundTag5 = (CompoundTag)tag1;
                for (final String string7 : compoundTag5.getKeys()) {
                    compoundTag4.put(string7, compoundTag5.getTag(string7));
                }
                final CompoundTag compoundTag6 = (CompoundTag)tag2;
                for (final String string8 : compoundTag6.getKeys()) {
                    compoundTag4.put(string8, compoundTag6.getTag(string8));
                }
                return compoundTag4;
            }
            return tag1;
        }
        else {
            if (tag1 instanceof EndTag) {
                throw new IllegalArgumentException("mergeInto called with a null input.");
            }
            if (tag1 instanceof AbstractListTag) {
                final AbstractListTag<Tag> abstractListTag3 = new ListTag();
                final AbstractListTag<?> abstractListTag4 = tag1;
                abstractListTag3.addAll(abstractListTag4);
                abstractListTag3.add(tag2);
                return abstractListTag3;
            }
            return tag1;
        }
    }
    
    public Tag a(final Tag tag1, final Tag tag2, final Tag tag3) {
        CompoundTag compoundTag4;
        if (tag1 instanceof EndTag) {
            compoundTag4 = new CompoundTag();
        }
        else {
            if (!(tag1 instanceof CompoundTag)) {
                return tag1;
            }
            final CompoundTag compoundTag5 = (CompoundTag)tag1;
            compoundTag4 = new CompoundTag();
            compoundTag5.getKeys().forEach(string -> compoundTag4.put(string, compoundTag5.getTag(string)));
        }
        compoundTag4.put(tag2.asString(), tag3);
        return compoundTag4;
    }
    
    public Tag b(final Tag tag1, final Tag tag2) {
        if (tag1 instanceof EndTag) {
            return tag2;
        }
        if (tag2 instanceof EndTag) {
            return tag1;
        }
        if (tag1 instanceof CompoundTag && tag2 instanceof CompoundTag) {
            final CompoundTag compoundTag3 = (CompoundTag)tag1;
            final CompoundTag compoundTag4 = (CompoundTag)tag2;
            final CompoundTag compoundTag5 = new CompoundTag();
            compoundTag3.getKeys().forEach(string -> compoundTag5.put(string, compoundTag3.getTag(string)));
            compoundTag4.getKeys().forEach(string -> compoundTag5.put(string, compoundTag4.getTag(string)));
        }
        if (tag1 instanceof AbstractListTag && tag2 instanceof AbstractListTag) {
            final ListTag listTag3 = new ListTag();
            listTag3.addAll(tag1);
            listTag3.addAll(tag2);
            return listTag3;
        }
        throw new IllegalArgumentException("Could not merge " + tag1 + " and " + tag2);
    }
    
    public Optional<Map<Tag, Tag>> d(final Tag tag) {
        if (tag instanceof CompoundTag) {
            final CompoundTag compoundTag2 = (CompoundTag)tag;
            return Optional.<Map<Tag, Tag>>of(compoundTag2.getKeys().stream().map(string -> Pair.of(this.a(string), compoundTag2.getTag(string))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        }
        return Optional.<Map<Tag, Tag>>empty();
    }
    
    public Tag a(final Map<Tag, Tag> map) {
        final CompoundTag compoundTag2 = new CompoundTag();
        for (final Map.Entry<Tag, Tag> entry4 : map.entrySet()) {
            compoundTag2.put(entry4.getKey().asString(), entry4.getValue());
        }
        return compoundTag2;
    }
    
    public Optional<Stream<Tag>> e(final Tag tag) {
        if (tag instanceof AbstractListTag) {
            return Optional.<Stream<Tag>>of(((AbstractListTag)tag).stream().map(tag -> tag));
        }
        return Optional.<Stream<Tag>>empty();
    }
    
    public Optional<ByteBuffer> f(final Tag tag) {
        if (tag instanceof ByteArrayTag) {
            return Optional.<ByteBuffer>of(ByteBuffer.wrap(((ByteArrayTag)tag).getByteArray()));
        }
        return (Optional<ByteBuffer>)super.getByteBuffer(tag);
    }
    
    public Tag a(final ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray(byteBuffer));
    }
    
    public Optional<IntStream> g(final Tag tag) {
        if (tag instanceof IntArrayTag) {
            return Optional.<IntStream>of(Arrays.stream(((IntArrayTag)tag).getIntArray()));
        }
        return (Optional<IntStream>)super.getIntStream(tag);
    }
    
    public Tag a(final IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }
    
    public Optional<LongStream> h(final Tag tag) {
        if (tag instanceof LongArrayTag) {
            return Optional.<LongStream>of(Arrays.stream(((LongArrayTag)tag).getLongArray()));
        }
        return (Optional<LongStream>)super.getLongStream(tag);
    }
    
    public Tag a(final LongStream longStream) {
        return new LongArrayTag(longStream.toArray());
    }
    
    public Tag a(final Stream<Tag> stream) {
        final PeekingIterator<Tag> peekingIterator2 = Iterators.<Tag>peekingIterator(stream.iterator());
        if (!peekingIterator2.hasNext()) {
            return new ListTag();
        }
        final Tag tag2 = peekingIterator2.peek();
        if (tag2 instanceof ByteTag) {
            final List<Byte> list4 = Lists.newArrayList(Iterators.transform((Iterator<Tag>)peekingIterator2, tag -> tag.getByte()));
            return new ByteArrayTag(list4);
        }
        if (tag2 instanceof IntTag) {
            final List<Integer> list5 = Lists.newArrayList(Iterators.transform((Iterator<Tag>)peekingIterator2, tag -> tag.getInt()));
            return new IntArrayTag(list5);
        }
        if (tag2 instanceof LongTag) {
            final List<Long> list6 = Lists.newArrayList(Iterators.transform((Iterator<Tag>)peekingIterator2, tag -> tag.getLong()));
            return new LongArrayTag(list6);
        }
        final ListTag listTag4 = new ListTag();
        while (peekingIterator2.hasNext()) {
            final Tag tag3 = peekingIterator2.next();
            if (tag3 instanceof EndTag) {
                continue;
            }
            listTag4.add(tag3);
        }
        return listTag4;
    }
    
    public Tag a(final Tag tag, final String string) {
        if (tag instanceof CompoundTag) {
            final CompoundTag compoundTag3 = (CompoundTag)tag;
            final CompoundTag compoundTag4 = new CompoundTag();
            compoundTag3.getKeys().stream().filter(string2 -> !Objects.equals(string2, string)).forEach(string -> compoundTag4.put(string, compoundTag3.getTag(string)));
            return compoundTag4;
        }
        return tag;
    }
    
    @Override
    public String toString() {
        return "NBT";
    }
    
    static {
        INSTANCE = new NbtOps();
    }
}
