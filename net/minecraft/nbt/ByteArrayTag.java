package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;

public class ByteArrayTag extends AbstractListTag<ByteTag>
{
    private byte[] value;
    
    ByteArrayTag() {
    }
    
    public ByteArrayTag(final byte[] arr) {
        this.value = arr;
    }
    
    public ByteArrayTag(final List<Byte> list) {
        this(toArray(list));
    }
    
    private static byte[] toArray(final List<Byte> list) {
        final byte[] arr2 = new byte[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Byte byte4 = list.get(integer3);
            arr2[integer3] = (byte)((byte4 == null) ? 0 : ((byte)byte4));
        }
        return arr2;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        dataOutput.write(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        final int integer4 = input.readInt();
        positionTracker.add(8 * integer4);
        input.readFully(this.value = new byte[integer4]);
    }
    
    @Override
    public byte getType() {
        return 7;
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder("[B;");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            if (integer2 != 0) {
                stringBuilder1.append(',');
            }
            stringBuilder1.append(this.value[integer2]).append('B');
        }
        return stringBuilder1.append(']').toString();
    }
    
    @Override
    public Tag copy() {
        final byte[] arr1 = new byte[this.value.length];
        System.arraycopy(this.value, 0, arr1, 0, this.value.length);
        return new ByteArrayTag(arr1);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)o).value));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("B").applyFormat(ByteArrayTag.RED);
        final TextComponent textComponent4 = new StringTextComponent("[").append(textComponent3).append(";");
        for (int integer2 = 0; integer2 < this.value.length; ++integer2) {
            final TextComponent textComponent5 = new StringTextComponent(String.valueOf(this.value[integer2])).applyFormat(ByteArrayTag.GOLD);
            textComponent4.append(" ").append(textComponent5).append(textComponent3);
            if (integer2 != this.value.length - 1) {
                textComponent4.append(",");
            }
        }
        textComponent4.append("]");
        return textComponent4;
    }
    
    public byte[] getByteArray() {
        return this.value;
    }
    
    @Override
    public int size() {
        return this.value.length;
    }
    
    public ByteTag a(final int integer) {
        return new ByteTag(this.value[integer]);
    }
    
    public ByteTag a(final int index, final ByteTag byteTag) {
        final byte byte3 = this.value[index];
        this.value[index] = byteTag.getByte();
        return new ByteTag(byte3);
    }
    
    public void b(final int value, final ByteTag byteTag) {
        this.value = ArrayUtils.add(this.value, value, byteTag.getByte());
    }
    
    @Override
    public boolean setTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[index] = ((AbstractNumberTag)tag).getByte();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int index, final Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getByte());
            return true;
        }
        return false;
    }
    
    public ByteTag b(final int integer) {
        final byte byte2 = this.value[integer];
        this.value = ArrayUtils.remove(this.value, integer);
        return new ByteTag(byte2);
    }
    
    @Override
    public void clear() {
        this.value = new byte[0];
    }
}
