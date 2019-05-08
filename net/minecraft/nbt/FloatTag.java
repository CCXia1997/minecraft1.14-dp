package net.minecraft.nbt;

import net.minecraft.util.math.MathHelper;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class FloatTag extends AbstractNumberTag
{
    private float value;
    
    FloatTag() {
    }
    
    public FloatTag(final float float1) {
        this.value = float1;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(96L);
        this.value = input.readFloat();
    }
    
    @Override
    public byte getType() {
        return 5;
    }
    
    @Override
    public String toString() {
        return this.value + "f";
    }
    
    @Override
    public FloatTag copy() {
        return new FloatTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof FloatTag && this.value == ((FloatTag)o).value);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("f").applyFormat(FloatTag.RED);
        return new StringTextComponent(String.valueOf(this.value)).append(textComponent3).applyFormat(FloatTag.GOLD);
    }
    
    @Override
    public long getLong() {
        return (long)this.value;
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
        return this.value;
    }
    
    @Override
    public Number getNumber() {
        return this.value;
    }
}
