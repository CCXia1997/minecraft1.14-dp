package net.minecraft.nbt;

import com.google.common.base.Strings;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Objects;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutput;
import com.google.common.collect.Lists;
import java.util.List;

public class ListTag extends AbstractListTag<Tag>
{
    private List<Tag> value;
    private byte type;
    
    public ListTag() {
        this.value = Lists.newArrayList();
        this.type = 0;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        if (this.value.isEmpty()) {
            this.type = 0;
        }
        else {
            this.type = this.value.get(0).getType();
        }
        dataOutput.writeByte(this.type);
        dataOutput.writeInt(this.value.size());
        for (final Tag tag3 : this.value) {
            tag3.write(dataOutput);
        }
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(296L);
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.type = input.readByte();
        final int integer4 = input.readInt();
        if (this.type == 0 && integer4 > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        positionTracker.add(32L * integer4);
        this.value = Lists.newArrayListWithCapacity(integer4);
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            final Tag tag6 = Tag.createTag(this.type);
            tag6.read(input, depth + 1, positionTracker);
            this.value.add(tag6);
        }
    }
    
    @Override
    public byte getType() {
        return 9;
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder("[");
        for (int integer2 = 0; integer2 < this.value.size(); ++integer2) {
            if (integer2 != 0) {
                stringBuilder1.append(',');
            }
            stringBuilder1.append(this.value.get(integer2));
        }
        return stringBuilder1.append(']').toString();
    }
    
    private void forgetTypeIfEmpty() {
        if (this.value.isEmpty()) {
            this.type = 0;
        }
    }
    
    @Override
    public Tag c(final int integer) {
        final Tag tag2 = this.value.remove(integer);
        this.forgetTypeIfEmpty();
        return tag2;
    }
    
    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }
    
    public CompoundTag getCompoundTag(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 10) {
                return (CompoundTag)tag2;
            }
        }
        return new CompoundTag();
    }
    
    public ListTag getListTag(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 9) {
                return (ListTag)tag2;
            }
        }
        return new ListTag();
    }
    
    public short getShort(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 2) {
                return ((ShortTag)tag2).getShort();
            }
        }
        return 0;
    }
    
    public int getInt(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 3) {
                return ((IntTag)tag2).getInt();
            }
        }
        return 0;
    }
    
    public int[] getIntArray(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 11) {
                return ((IntArrayTag)tag2).getIntArray();
            }
        }
        return new int[0];
    }
    
    public double getDouble(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 6) {
                return ((DoubleTag)tag2).getDouble();
            }
        }
        return 0.0;
    }
    
    public float getFloat(final int integer) {
        if (integer >= 0 && integer < this.value.size()) {
            final Tag tag2 = this.value.get(integer);
            if (tag2.getType() == 5) {
                return ((FloatTag)tag2).getFloat();
            }
        }
        return 0.0f;
    }
    
    public String getString(final int integer) {
        if (integer < 0 || integer >= this.value.size()) {
            return "";
        }
        final Tag tag2 = this.value.get(integer);
        if (tag2.getType() == 8) {
            return tag2.asString();
        }
        return tag2.toString();
    }
    
    @Override
    public int size() {
        return this.value.size();
    }
    
    public Tag k(final int integer) {
        return this.value.get(integer);
    }
    
    @Override
    public Tag d(final int index, final Tag tag) {
        final Tag tag2 = this.k(index);
        if (!this.setTag(index, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
        return tag2;
    }
    
    @Override
    public void c(final int value, final Tag tag) {
        if (!this.addTag(value, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
    }
    
    @Override
    public boolean setTag(final int index, final Tag tag) {
        if (this.canAdd(tag)) {
            this.value.set(index, tag);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int index, final Tag tag) {
        if (this.canAdd(tag)) {
            this.value.add(index, tag);
            return true;
        }
        return false;
    }
    
    private boolean canAdd(final Tag tag) {
        if (tag.getType() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = tag.getType();
            return true;
        }
        return this.type == tag.getType();
    }
    
    @Override
    public ListTag copy() {
        final ListTag listTag1 = new ListTag();
        listTag1.type = this.type;
        for (final Tag tag3 : this.value) {
            final Tag tag4 = tag3.copy();
            listTag1.value.add(tag4);
        }
        return listTag1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value));
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        if (this.isEmpty()) {
            return new StringTextComponent("[]");
        }
        final TextComponent textComponent3 = new StringTextComponent("[");
        if (!indent.isEmpty()) {
            textComponent3.append("\n");
        }
        for (int integer2 = 0; integer2 < this.value.size(); ++integer2) {
            final TextComponent textComponent4 = new StringTextComponent(Strings.repeat(indent, integer + 1));
            textComponent4.append(this.value.get(integer2).toTextComponent(indent, integer + 1));
            if (integer2 != this.value.size() - 1) {
                textComponent4.append(String.valueOf(',')).append(indent.isEmpty() ? " " : "\n");
            }
            textComponent3.append(textComponent4);
        }
        if (!indent.isEmpty()) {
            textComponent3.append("\n").append(Strings.repeat(indent, integer));
        }
        textComponent3.append("]");
        return textComponent3;
    }
    
    public int getListType() {
        return this.type;
    }
    
    @Override
    public void clear() {
        this.value.clear();
        this.type = 0;
    }
}
