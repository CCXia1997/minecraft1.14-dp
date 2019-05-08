package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;

public class IntArrayTag extends AbstractListTag<IntTag>
{
    private int[] value;
    
    IntArrayTag() {
    }
    
    public IntArrayTag(final int[] arr) {
        this.value = arr;
    }
    
    public IntArrayTag(final List<Integer> list) {
        this(toArray(list));
    }
    
    private static int[] toArray(final List<Integer> list) {
        final int[] arr2 = new int[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Integer integer4 = list.get(integer3);
            arr2[integer3] = ((integer4 == null) ? 0 : integer4);
        }
        return arr2;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        for (final int integer5 : this.value) {
            dataOutput.writeInt(integer5);
        }
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        final int integer4 = input.readInt();
        positionTracker.add(32 * integer4);
        this.value = new int[integer4];
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            this.value[integer5] = input.readInt();
        }
    }
    
    @Override
    public byte getType() {
        return 11;
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder("[I;");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            if (integer2 != 0) {
                stringBuilder1.append(',');
            }
            stringBuilder1.append(this.value[integer2]);
        }
        return stringBuilder1.append(']').toString();
    }
    
    @Override
    public IntArrayTag copy() {
        final int[] arr1 = new int[this.value.length];
        System.arraycopy(this.value, 0, arr1, 0, this.value.length);
        return new IntArrayTag(arr1);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof IntArrayTag && Arrays.equals(this.value, ((IntArrayTag)o).value));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
    
    public int[] getIntArray() {
        return this.value;
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("I").applyFormat(IntArrayTag.RED);
        final TextComponent textComponent4 = new StringTextComponent("[").append(textComponent3).append(";");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            textComponent4.append(" ").append(new StringTextComponent(String.valueOf(this.value[integer2])).applyFormat(IntArrayTag.GOLD));
            if (integer2 != this.value.length - 1) {
                textComponent4.append(",");
            }
        }
        textComponent4.append("]");
        return textComponent4;
    }
    
    @Override
    public int size() {
        return this.value.length;
    }
    
    public IntTag a(final int integer) {
        return new IntTag(this.value[integer]);
    }
    
    public IntTag a(final int index, final IntTag intTag) {
        final int integer3 = this.value[index];
        this.value[index] = intTag.getInt();
        return new IntTag(integer3);
    }
    
    public void b(final int value, final IntTag intTag) {
        this.value = ArrayUtils.add(this.value, value, intTag.getInt());
    }
    
    @Override
    public boolean setTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getInt();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getInt());
            return true;
        }
        return false;
    }
    
    public IntTag b(final int integer) {
        final int integer2 = this.value[integer];
        this.value = ArrayUtils.remove(this.value, integer);
        return new IntTag(integer2);
    }
    
    @Override
    public void clear() {
        this.value = new int[0];
    }
}
