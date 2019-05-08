package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;

public class EndTag implements Tag
{
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(64L);
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
    }
    
    @Override
    public byte getType() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "END";
    }
    
    @Override
    public EndTag copy() {
        return new EndTag();
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        return new StringTextComponent("");
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof EndTag;
    }
    
    @Override
    public int hashCode() {
        return this.getType();
    }
}
