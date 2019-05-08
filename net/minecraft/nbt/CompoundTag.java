package net.minecraft.nbt;

import org.apache.logging.log4j.LogManager;
import com.google.common.base.Strings;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Objects;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import java.util.Collection;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.util.crash.CrashException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import java.util.Set;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutput;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

public class CompoundTag implements Tag
{
    private static final Logger LOGGER;
    private static final Pattern PATTERN;
    private final Map<String, Tag> tags;
    
    public CompoundTag() {
        this.tags = Maps.newHashMap();
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        for (final String string3 : this.tags.keySet()) {
            final Tag tag4 = this.tags.get(string3);
            write(string3, tag4, dataOutput);
        }
        dataOutput.writeByte(0);
    }
    
    @Override
    public void read(final DataInput input, final int depth, final PositionTracker positionTracker) throws IOException {
        positionTracker.add(384L);
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.tags.clear();
        byte byte4;
        while ((byte4 = readByte(input, positionTracker)) != 0) {
            final String string5 = readString(input, positionTracker);
            positionTracker.add(224 + 16 * string5.length());
            final Tag tag6 = createTag(byte4, string5, input, depth + 1, positionTracker);
            if (this.tags.put(string5, tag6) != null) {
                positionTracker.add(288L);
            }
        }
    }
    
    public Set<String> getKeys() {
        return this.tags.keySet();
    }
    
    @Override
    public byte getType() {
        return 10;
    }
    
    public int getSize() {
        return this.tags.size();
    }
    
    @Nullable
    public Tag put(final String key, final Tag tag) {
        return this.tags.put(key, tag);
    }
    
    public void putByte(final String key, final byte byte2) {
        this.tags.put(key, new ByteTag(byte2));
    }
    
    public void putShort(final String key, final short short2) {
        this.tags.put(key, new ShortTag(short2));
    }
    
    public void putInt(final String key, final int integer) {
        this.tags.put(key, new IntTag(integer));
    }
    
    public void putLong(final String key, final long long2) {
        this.tags.put(key, new LongTag(long2));
    }
    
    public void putUuid(final String key, final UUID uUID) {
        this.putLong(key + "Most", uUID.getMostSignificantBits());
        this.putLong(key + "Least", uUID.getLeastSignificantBits());
    }
    
    public UUID getUuid(final String string) {
        return new UUID(this.getLong(string + "Most"), this.getLong(string + "Least"));
    }
    
    public boolean hasUuid(final String string) {
        return this.containsKey(string + "Most", 99) && this.containsKey(string + "Least", 99);
    }
    
    public void putFloat(final String key, final float float2) {
        this.tags.put(key, new FloatTag(float2));
    }
    
    public void putDouble(final String key, final double double2) {
        this.tags.put(key, new DoubleTag(double2));
    }
    
    public void putString(final String key, final String string2) {
        this.tags.put(key, new StringTag(string2));
    }
    
    public void putByteArray(final String key, final byte[] arr) {
        this.tags.put(key, new ByteArrayTag(arr));
    }
    
    public void putIntArray(final String key, final int[] arr) {
        this.tags.put(key, new IntArrayTag(arr));
    }
    
    public void putIntArray(final String key, final List<Integer> list) {
        this.tags.put(key, new IntArrayTag(list));
    }
    
    public void putLongArray(final String key, final long[] arr) {
        this.tags.put(key, new LongArrayTag(arr));
    }
    
    public void putLongArray(final String key, final List<Long> list) {
        this.tags.put(key, new LongArrayTag(list));
    }
    
    public void putBoolean(final String key, final boolean boolean2) {
        this.putByte(key, (byte)(boolean2 ? 1 : 0));
    }
    
    @Nullable
    public Tag getTag(final String string) {
        return this.tags.get(string);
    }
    
    public byte getType(final String string) {
        final Tag tag2 = this.tags.get(string);
        if (tag2 == null) {
            return 0;
        }
        return tag2.getType();
    }
    
    public boolean containsKey(final String string) {
        return this.tags.containsKey(string);
    }
    
    public boolean containsKey(final String key, final int integer) {
        final int integer2 = this.getType(key);
        return integer2 == integer || (integer == 99 && (integer2 == 1 || integer2 == 2 || integer2 == 3 || integer2 == 4 || integer2 == 5 || integer2 == 6));
    }
    
    public byte getByte(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getByte();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public short getShort(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getShort();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public int getInt(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getInt();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public long getLong(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getLong();
            }
        }
        catch (ClassCastException ex) {}
        return 0L;
    }
    
    public float getFloat(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getFloat();
            }
        }
        catch (ClassCastException ex) {}
        return 0.0f;
    }
    
    public double getDouble(final String string) {
        try {
            if (this.containsKey(string, 99)) {
                return this.tags.get(string).getDouble();
            }
        }
        catch (ClassCastException ex) {}
        return 0.0;
    }
    
    public String getString(final String string) {
        try {
            if (this.containsKey(string, 8)) {
                return this.tags.get(string).asString();
            }
        }
        catch (ClassCastException ex) {}
        return "";
    }
    
    public byte[] getByteArray(final String string) {
        try {
            if (this.containsKey(string, 7)) {
                return this.tags.get(string).getByteArray();
            }
        }
        catch (ClassCastException classCastException2) {
            throw new CrashException(this.createCrashReport(string, 7, classCastException2));
        }
        return new byte[0];
    }
    
    public int[] getIntArray(final String string) {
        try {
            if (this.containsKey(string, 11)) {
                return this.tags.get(string).getIntArray();
            }
        }
        catch (ClassCastException classCastException2) {
            throw new CrashException(this.createCrashReport(string, 11, classCastException2));
        }
        return new int[0];
    }
    
    public long[] getLongArray(final String string) {
        try {
            if (this.containsKey(string, 12)) {
                return this.tags.get(string).getLongArray();
            }
        }
        catch (ClassCastException classCastException2) {
            throw new CrashException(this.createCrashReport(string, 12, classCastException2));
        }
        return new long[0];
    }
    
    public CompoundTag getCompound(final String string) {
        try {
            if (this.containsKey(string, 10)) {
                return this.tags.get(string);
            }
        }
        catch (ClassCastException classCastException2) {
            throw new CrashException(this.createCrashReport(string, 10, classCastException2));
        }
        return new CompoundTag();
    }
    
    public ListTag getList(final String key, final int integer) {
        try {
            if (this.getType(key) == 9) {
                final ListTag listTag3 = this.tags.get(key);
                if (listTag3.isEmpty() || listTag3.getListType() == integer) {
                    return listTag3;
                }
                return new ListTag();
            }
        }
        catch (ClassCastException classCastException3) {
            throw new CrashException(this.createCrashReport(key, 9, classCastException3));
        }
        return new ListTag();
    }
    
    public boolean getBoolean(final String string) {
        return this.getByte(string) != 0;
    }
    
    public void remove(final String string) {
        this.tags.remove(string);
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder("{");
        Collection<String> collection2 = this.tags.keySet();
        if (CompoundTag.LOGGER.isDebugEnabled()) {
            final List<String> list3 = Lists.newArrayList(this.tags.keySet());
            Collections.<String>sort(list3);
            collection2 = list3;
        }
        for (final String string4 : collection2) {
            if (stringBuilder1.length() != 1) {
                stringBuilder1.append(',');
            }
            stringBuilder1.append(escapeTagKey(string4)).append(':').append(this.tags.get(string4));
        }
        return stringBuilder1.append('}').toString();
    }
    
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }
    
    private CrashReport createCrashReport(final String key, final int type, final ClassCastException classCastException) {
        final CrashReport crashReport4 = CrashReport.create(classCastException, "Reading NBT data");
        final CrashReportSection crashReportSection5 = crashReport4.addElement("Corrupt NBT tag", 1);
        crashReportSection5.add("Tag type found", () -> CompoundTag.TYPES[this.tags.get(key).getType()]);
        crashReportSection5.add("Tag type expected", () -> CompoundTag.TYPES[type]);
        crashReportSection5.add("Tag name", key);
        return crashReport4;
    }
    
    @Override
    public CompoundTag copy() {
        final CompoundTag compoundTag1 = new CompoundTag();
        for (final String string3 : this.tags.keySet()) {
            compoundTag1.put(string3, this.tags.get(string3).copy());
        }
        return compoundTag1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)o).tags));
    }
    
    @Override
    public int hashCode() {
        return this.tags.hashCode();
    }
    
    private static void write(final String key, final Tag tag, final DataOutput output) throws IOException {
        output.writeByte(tag.getType());
        if (tag.getType() == 0) {
            return;
        }
        output.writeUTF(key);
        tag.write(output);
    }
    
    private static byte readByte(final DataInput input, final PositionTracker tracker) throws IOException {
        return input.readByte();
    }
    
    private static String readString(final DataInput input, final PositionTracker tracker) throws IOException {
        return input.readUTF();
    }
    
    static Tag createTag(final byte type, final String key, final DataInput input, final int depth, final PositionTracker tracker) throws IOException {
        final Tag tag6 = Tag.createTag(type);
        try {
            tag6.read(input, depth, tracker);
        }
        catch (IOException iOException7) {
            final CrashReport crashReport8 = CrashReport.create(iOException7, "Loading NBT data");
            final CrashReportSection crashReportSection9 = crashReport8.addElement("NBT Tag");
            crashReportSection9.add("Tag name", key);
            crashReportSection9.add("Tag type", type);
            throw new CrashException(crashReport8);
        }
        return tag6;
    }
    
    public CompoundTag copyFrom(final CompoundTag compoundTag) {
        for (final String string3 : compoundTag.tags.keySet()) {
            final Tag tag4 = compoundTag.tags.get(string3);
            if (tag4.getType() == 10) {
                if (this.containsKey(string3, 10)) {
                    final CompoundTag compoundTag2 = this.getCompound(string3);
                    compoundTag2.copyFrom((CompoundTag)tag4);
                }
                else {
                    this.put(string3, tag4.copy());
                }
            }
            else {
                this.put(string3, tag4.copy());
            }
        }
        return this;
    }
    
    protected static String escapeTagKey(final String key) {
        if (CompoundTag.PATTERN.matcher(key).matches()) {
            return key;
        }
        return StringTag.escape(key);
    }
    
    protected static TextComponent prettyPrintTagKey(final String key) {
        if (CompoundTag.PATTERN.matcher(key).matches()) {
            return new StringTextComponent(key).applyFormat(CompoundTag.AQUA);
        }
        final String string2 = StringTag.escape(key);
        final String string3 = string2.substring(0, 1);
        final TextComponent textComponent4 = new StringTextComponent(string2.substring(1, string2.length() - 1)).applyFormat(CompoundTag.AQUA);
        return new StringTextComponent(string3).append(textComponent4).append(string3);
    }
    
    @Override
    public TextComponent toTextComponent(final String indent, final int integer) {
        if (this.tags.isEmpty()) {
            return new StringTextComponent("{}");
        }
        final TextComponent textComponent3 = new StringTextComponent("{");
        Collection<String> collection4 = this.tags.keySet();
        if (CompoundTag.LOGGER.isDebugEnabled()) {
            final List<String> list5 = Lists.newArrayList(this.tags.keySet());
            Collections.<String>sort(list5);
            collection4 = list5;
        }
        if (!indent.isEmpty()) {
            textComponent3.append("\n");
        }
        final Iterator<String> iterator5 = collection4.iterator();
        while (iterator5.hasNext()) {
            final String string6 = iterator5.next();
            final TextComponent textComponent4 = new StringTextComponent(Strings.repeat(indent, integer + 1)).append(prettyPrintTagKey(string6)).append(String.valueOf(':')).append(" ").append(this.tags.get(string6).toTextComponent(indent, integer + 1));
            if (iterator5.hasNext()) {
                textComponent4.append(String.valueOf(',')).append(indent.isEmpty() ? " " : "\n");
            }
            textComponent3.append(textComponent4);
        }
        if (!indent.isEmpty()) {
            textComponent3.append("\n").append(Strings.repeat(indent, integer));
        }
        textComponent3.append("}");
        return textComponent3;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    }
}
