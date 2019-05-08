package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class IntTag extends AbstractNumberTag
{
    private int value;
    
    IntTag() {
    }
    
    public IntTag(final int integer) {
        this.value = integer;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(96L);
        this.value = input.readInt();
    }
    
    @Override
    public byte getType() {
        return 3;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public IntTag copy() {
        return new IntTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof IntTag && this.value == ((IntTag)o).value);
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        return new StringTextComponent(String.valueOf(this.value)).applyFormat(IntTag.GOLD);
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
        return (short)(this.value & 0xFFFF);
    }
    
    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFF);
    }
    
    @Override
    public double getDouble() {
        return this.value;
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
