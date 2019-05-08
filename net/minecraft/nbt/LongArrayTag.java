package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;
import it.unimi.dsi.fastutil.longs.LongSet;

public class LongArrayTag extends AbstractListTag<LongTag>
{
    private long[] value;
    
    LongArrayTag() {
    }
    
    public LongArrayTag(final long[] arr) {
        this.value = arr;
    }
    
    public LongArrayTag(final LongSet longSet) {
        this.value = longSet.toLongArray();
    }
    
    public LongArrayTag(final List<Long> list) {
        this(toArray(list));
    }
    
    private static long[] toArray(final List<Long> list) {
        final long[] arr2 = new long[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Long long4 = list.get(integer3);
            arr2[integer3] = ((long4 == null) ? 0L : long4);
        }
        return arr2;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        for (final long long5 : this.value) {
            dataOutput.writeLong(long5);
        }
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        final int integer4 = input.readInt();
        positionTracker.add(64 * integer4);
        this.value = new long[integer4];
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            this.value[integer5] = input.readLong();
        }
    }
    
    @Override
    public byte getType() {
        return 12;
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder("[L;");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            if (integer2 != 0) {
                stringBuilder1.append(',');
            }
            stringBuilder1.append(this.value[integer2]).append('L');
        }
        return stringBuilder1.append(']').toString();
    }
    
    @Override
    public LongArrayTag copy() {
        final long[] arr1 = new long[this.value.length];
        System.arraycopy(this.value, 0, arr1, 0, this.value.length);
        return new LongArrayTag(arr1);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)o).value));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("L").applyFormat(LongArrayTag.RED);
        final TextComponent textComponent4 = new StringTextComponent("[").append(textComponent3).append(";");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            final TextComponent textComponent5 = new StringTextComponent(String.valueOf(this.value[integer2])).applyFormat(LongArrayTag.GOLD);
            textComponent4.append(" ").append(textComponent5).append(textComponent3);
            if (integer2 != this.value.length - 1) {
                textComponent4.append(",");
            }
        }
        textComponent4.append("]");
        return textComponent4;
    }
    
    public long[] getLongArray() {
        return this.value;
    }
    
    @Override
    public int size() {
        return this.value.length;
    }
    
    public LongTag a(final int integer) {
        return new LongTag(this.value[integer]);
    }
    
    public LongTag a(final int index, final LongTag longTag) {
        final long long3 = this.value[index];
        this.value[index] = longTag.getLong();
        return new LongTag(long3);
    }
    
    public void b(final int value, final LongTag longTag) {
        this.value = ArrayUtils.add(this.value, value, longTag.getLong());
    }
    
    @Override
    public boolean setTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getLong();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getLong());
            return true;
        }
        return false;
    }
    
    public LongTag b(final int integer) {
        final long long2 = this.value[integer];
        this.value = ArrayUtils.remove(this.value, integer);
        return new LongTag(long2);
    }
    
    @Override
    public void clear() {
        this.value = new long[0];
    }
}
