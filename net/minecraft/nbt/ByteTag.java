package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class ByteTag extends AbstractNumberTag
{
    private byte value;
    
    ByteTag() {
    }
    
    public ByteTag(final byte byte1) {
        this.value = byte1;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(72L);
        this.value = input.readByte();
    }
    
    @Override
    public byte getType() {
        return 1;
    }
    
    @Override
    public String toString() {
        return this.value + "b";
    }
    
    @Override
    public ByteTag copy() {
        return new ByteTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ByteTag && this.value == ((ByteTag)o).value);
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("b").applyFormat(ByteTag.RED);
        return new StringTextComponent(String.valueOf(this.value)).append(textComponent3).applyFormat(ByteTag.GOLD);
    }
    
    @Override
    public long getLong() {
        return this.value;
    }
    
    @Override
    public int getInt() {
        return this.value;
    }
    
    @Override
    public short getShort() {
        return this.value;
    }
    
    @Override
    public byte getByte() {
        return this.value;
    }
    
    @Override
    public double getDouble() {
        return this.value;
    }
    
    @Override
    public float getFloat() {
        return this.value;
    }
    
    @Override
    public Number getNumber() {
        return this.value;
    }
}
