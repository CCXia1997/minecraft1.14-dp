package net.minecraft.nbt;

import net.minecraft.util.math.MathHelper;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class DoubleTag extends AbstractNumberTag
{
    private double value;
    
    DoubleTag() {
    }
    
    public DoubleTag(final double double1) {
        this.value = double1;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(128L);
        this.value = input.readDouble();
    }
    
    @Override
    public byte getType() {
        return 6;
    }
    
    @Override
    public String toString() {
        return this.value + "d";
    }
    
    @Override
    public DoubleTag copy() {
        return new DoubleTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof DoubleTag && this.value == ((DoubleTag)o).value);
    }
    
    @Override
    public int hashCode() {
        final long long1 = Double.doubleToLongBits(this.value);
        return (int)(long1 ^ long1 >>> 32);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("d").applyFormat(DoubleTag.RED);
        return new StringTextComponent(String.valueOf(this.value)).append(textComponent3).applyFormat(DoubleTag.GOLD);
    }
    
    @Override
    public long getLong() {
        return (long)Math.floor(this.value);
    }
    
    @Override
    public int getInt() {
        return MathHelper.floor(this.value);
    }
    
    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }
    
    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
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
