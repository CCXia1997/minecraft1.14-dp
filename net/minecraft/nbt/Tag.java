package net.minecraft.nbt;

import net.minecraft.text.TextComponent;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import net.minecraft.text.TextFormat;

public interface Tag
{
    public static final String[] TYPES = { "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]" };
    public static final TextFormat AQUA = TextFormat.l;
    public static final TextFormat GREEN = TextFormat.k;
    public static final TextFormat GOLD = TextFormat.g;
    public static final TextFormat RED = TextFormat.m;
    
    void write(final DataOutput arg1) throws IOException;
    
    void read(final DataInput arg1, final int arg2, final PositionTracker arg3) throws IOException;
    
    String toString();
    
    byte getType();
    
    default Tag createTag(final byte id) {
        switch (id) {
            case 0: {
                return new EndTag();
            }
            case 1: {
                return new ByteTag();
            }
            case 2: {
                return new ShortTag();
            }
            case 3: {
                return new IntTag();
            }
            case 4: {
                return new LongTag();
            }
            case 5: {
                return new FloatTag();
            }
            case 6: {
                return new DoubleTag();
            }
            case 7: {
                return new ByteArrayTag();
            }
            case 11: {
                return new IntArrayTag();
            }
            case 12: {
                return new LongArrayTag();
            }
            case 8: {
                return new StringTag();
            }
            case 9: {
                return new ListTag();
            }
            case 10: {
                return new CompoundTag();
            }
            default: {
                return null;
            }
        }
    }
    
    default String idToString(final int id) {
        switch (id) {
            case 0: {
                return "TAG_End";
            }
            case 1: {
                return "TAG_Byte";
            }
            case 2: {
                return "TAG_Short";
            }
            case 3: {
                return "TAG_Int";
            }
            case 4: {
                return "TAG_Long";
            }
            case 5: {
                return "TAG_Float";
            }
            case 6: {
                return "TAG_Double";
            }
            case 7: {
                return "TAG_Byte_Array";
            }
            case 11: {
                return "TAG_Int_Array";
            }
            case 12: {
                return "TAG_Long_Array";
            }
            case 8: {
                return "TAG_String";
            }
            case 9: {
                return "TAG_List";
            }
            case 10: {
                return "TAG_Compound";
            }
            case 99: {
                return "Any Numeric Tag";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    Tag copy();
    
    default String asString() {
        return this.toString();
    }
    
    default TextComponent toTextComponent() {
        return this.toTextComponent("", 0);
    }
    
    TextComponent toTextComponent(final String arg1, final int arg2);
}
