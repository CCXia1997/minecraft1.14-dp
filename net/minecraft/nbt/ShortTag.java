package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class ShortTag extends AbstractNumberTag
{
    private short value;
    
    public ShortTag() {
    }
    
    public ShortTag(final short short1) {
        this.value = short1;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeShort(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(80L);
        this.value = input.readShort();
    }
    
    @Override
    public byte getType() {
        return 2;
    }
    
    @Override
    public String toString() {
        return this.value + "s";
    }
    
    @Override
    public ShortTag copy() {
        return new ShortTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ShortTag && this.value == ((ShortTag)o).value);
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final TextComponent textComponent3 = new StringTextComponent("s").applyFormat(ShortTag.RED);
        return new StringTextComponent(String.valueOf(this.value)).append(textComponent3).applyFormat(ShortTag.GOLD);
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
        return (byte)(this.value & 0xFF);
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
