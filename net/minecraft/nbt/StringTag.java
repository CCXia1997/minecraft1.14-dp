package net.minecraft.nbt;

import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Objects;

public class StringTag implements Tag
{
    private String value;
    
    public StringTag() {
        this("");
    }
    
    public StringTag(final String string) {
        Objects.<String>requireNonNull(string, "Null string not allowed");
        this.value = string;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.value);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(288L);
        this.value = input.readUTF();
        positionTracker.add(16 * this.value.length());
    }
    
    @Override
    public byte getType() {
        return 8;
    }
    
    @Override
    public String toString() {
        return escape(this.value);
    }
    
    @Override
    public StringTag copy() {
        return new StringTag(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof StringTag && Objects.equals(this.value, ((StringTag)o).value));
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public String asString() {
        return this.value;
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        final String string3 = escape(this.value);
        final String string4 = string3.substring(0, 1);
        final TextComponent textComponent5 = new StringTextComponent(string3.substring(1, string3.length() - 1)).applyFormat(StringTag.GREEN);
        return new StringTextComponent(string4).append(textComponent5).append(string4);
    }
    
    public static String escape(final String string) {
        final StringBuilder stringBuilder2 = new StringBuilder(" ");
        char character3 = '\0';
        for (int integer4 = 0; integer4 < string.length(); ++integer4) {
            final char character4 = string.charAt(integer4);
            if (character4 == '\\') {
                stringBuilder2.append('\\');
            }
            else if (character4 == '\"' || character4 == '\'') {
                if (character3 == '\0') {
                    character3 = ((character4 == '\"') ? '\'' : '\"');
                }
                if (character3 == character4) {
                    stringBuilder2.append('\\');
                }
            }
            stringBuilder2.append(character4);
        }
        if (character3 == '\0') {
            character3 = '\"';
        }
        stringBuilder2.setCharAt(0, character3);
        stringBuilder2.append(character3);
        return stringBuilder2.toString();
    }
}
