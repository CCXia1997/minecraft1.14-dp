package net.minecraft.block.pattern;

import java.util.Iterator;
import java.lang.reflect.Array;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.function.Predicate;
import java.util.Map;
import java.util.List;
import com.google.common.base.Joiner;

public class BlockPatternBuilder
{
    private static final Joiner JOINER;
    private final List<String[]> aisles;
    private final Map<Character, Predicate<CachedBlockPosition>> charMap;
    private int height;
    private int width;
    
    private BlockPatternBuilder() {
        this.aisles = Lists.newArrayList();
        (this.charMap = Maps.newHashMap()).put(' ', Predicates.alwaysTrue());
    }
    
    public BlockPatternBuilder aisle(final String... arr) {
        if (ArrayUtils.isEmpty((Object[])arr) || StringUtils.isEmpty((CharSequence)arr[0])) {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
        if (this.aisles.isEmpty()) {
            this.height = arr.length;
            this.width = arr[0].length();
        }
        if (arr.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + arr.length + ")");
        }
        for (final String string5 : arr) {
            if (string5.length() != this.width) {
                throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + string5.length() + ")");
            }
            for (final char character9 : string5.toCharArray()) {
                if (!this.charMap.containsKey(character9)) {
                    this.charMap.put(character9, null);
                }
            }
        }
        this.aisles.add(arr);
        return this;
    }
    
    public static BlockPatternBuilder start() {
        return new BlockPatternBuilder();
    }
    
    public BlockPatternBuilder where(final char c, final Predicate<CachedBlockPosition> predicate) {
        this.charMap.put(c, predicate);
        return this;
    }
    
    public BlockPattern build() {
        return new BlockPattern(this.bakePredicates());
    }
    
    private Predicate<CachedBlockPosition>[][][] bakePredicates() {
        this.validate();
        final Predicate<CachedBlockPosition>[][][] arr1 = (Predicate<CachedBlockPosition>[][][])Array.newInstance(Predicate.class, this.aisles.size(), this.height, this.width);
        for (int integer2 = 0; integer2 < this.aisles.size(); ++integer2) {
            for (int integer3 = 0; integer3 < this.height; ++integer3) {
                for (int integer4 = 0; integer4 < this.width; ++integer4) {
                    arr1[integer2][integer3][integer4] = this.charMap.get(this.aisles.get(integer2)[integer3].charAt(integer4));
                }
            }
        }
        return arr1;
    }
    
    private void validate() {
        final List<Character> list1 = Lists.newArrayList();
        for (final Map.Entry<Character, Predicate<CachedBlockPosition>> entry3 : this.charMap.entrySet()) {
            if (entry3.getValue() == null) {
                list1.add(entry3.getKey());
            }
        }
        if (!list1.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + BlockPatternBuilder.JOINER.join(list1) + " are missing");
        }
    }
    
    static {
        JOINER = Joiner.on(",");
    }
}
