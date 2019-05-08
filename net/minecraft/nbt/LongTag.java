package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class LongTag extends AbstractNumberTag
{
    private long value;
    
    LongTag() {
    }
    
    public LongTag(final long long1) {
        this.value = long1;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(128L);
        this.value = input.readLong();
    }
    
    @Override
    public byte getType() {
        return 4;
    }
    
    @Override
    public String toString() {
        return this.value + "L";
    }
    
    @Override
    public LongTag copy() {
        return new LongTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof LongTag && this.value == ((LongTag)o).value);
    }
    
    @Override
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("L").applyFormat(LongTag.RED);
        return new StringTextComponent(String.valueOf(this.value)).append(textComponent3).applyFormat(LongTag.GOLD);
    }
    
    @Override
    public long getLong() {
        return this.value;
    }
    
    @Override
    public int getInt() {
        return (int)(this.value & -1L);
    }
    
    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFFL);
    }
    
    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFFL);
    }
    
    @Override
    public double getDouble() {
        return (double)this.value;
    }
    
    @Override
    public float getFloat() {
        return (float)this.value;
    }
    
    @Override
    public Number getNumber() {
        return this.value;
    }
}
