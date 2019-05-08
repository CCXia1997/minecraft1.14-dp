package net.minecraft.nbt;

import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import java.util.regex.Pattern;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StringNbtReader
{
    public static final SimpleCommandExceptionType TRAILING;
    public static final SimpleCommandExceptionType EXPECTED_KEY;
    public static final SimpleCommandExceptionType EXPECTED_VALUE;
    public static final Dynamic2CommandExceptionType LIST_MIXED;
    public static final Dynamic2CommandExceptionType ARRAY_MIXED;
    public static final DynamicCommandExceptionType ARRAY_INVALID;
    private static final Pattern DOUBLE_PATTERN_IMPLICIT;
    private static final Pattern DOUBLE_PATTERN;
    private static final Pattern FLOAT_PATTERN;
    private static final Pattern BYTE_PATTERN;
    private static final Pattern LONG_PATTERN;
    private static final Pattern SHORT_PATTERN;
    private static final Pattern INT_PATTERN;
    private final StringReader reader;
    
    public static CompoundTag parse(final String string) throws CommandSyntaxException {
        return new StringNbtReader(new StringReader(string)).readCompoundTag();
    }
    
    @VisibleForTesting
    CompoundTag readCompoundTag() throws CommandSyntaxException {
        final CompoundTag compoundTag1 = this.parseCompoundTag();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw StringNbtReader.TRAILING.createWithContext((ImmutableStringReader)this.reader);
        }
        return compoundTag1;
    }
    
    public StringNbtReader(final StringReader stringReader) {
        this.reader = stringReader;
    }
    
    protected String readString() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw StringNbtReader.EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.reader.readString();
    }
    
    protected Tag parseTagPrimitive() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        final int integer1 = this.reader.getCursor();
        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return new StringTag(this.reader.readQuotedString());
        }
        final String string2 = this.reader.readUnquotedString();
        if (string2.isEmpty()) {
            this.reader.setCursor(integer1);
            throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.parsePrimitive(string2);
    }
    
    private Tag parsePrimitive(final String string) {
        try {
            if (StringNbtReader.FLOAT_PATTERN.matcher(string).matches()) {
                return new FloatTag(Float.parseFloat(string.substring(0, string.length() - 1)));
            }
            if (StringNbtReader.BYTE_PATTERN.matcher(string).matches()) {
                return new ByteTag(Byte.parseByte(string.substring(0, string.length() - 1)));
            }
            if (StringNbtReader.LONG_PATTERN.matcher(string).matches()) {
                return new LongTag(Long.parseLong(string.substring(0, string.length() - 1)));
            }
            if (StringNbtReader.SHORT_PATTERN.matcher(string).matches()) {
                return new ShortTag(Short.parseShort(string.substring(0, string.length() - 1)));
            }
            if (StringNbtReader.INT_PATTERN.matcher(string).matches()) {
                return new IntTag(Integer.parseInt(string));
            }
            if (StringNbtReader.DOUBLE_PATTERN.matcher(string).matches()) {
                return new DoubleTag(Double.parseDouble(string.substring(0, string.length() - 1)));
            }
            if (StringNbtReader.DOUBLE_PATTERN_IMPLICIT.matcher(string).matches()) {
                return new DoubleTag(Double.parseDouble(string));
            }
            if ("true".equalsIgnoreCase(string)) {
                return new ByteTag((byte)1);
            }
            if ("false".equalsIgnoreCase(string)) {
                return new ByteTag((byte)0);
            }
        }
        catch (NumberFormatException ex) {}
        return new StringTag(string);
    }
    
    public Tag parseTag() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        final char character1 = this.reader.peek();
        if (character1 == '{') {
            return this.parseCompoundTag();
        }
        if (character1 == '[') {
            return this.parseTagArray();
        }
        return this.parseTagPrimitive();
    }
    
    protected Tag parseTagArray() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';') {
            return this.parseTagPrimitiveArray();
        }
        return this.parseListTag();
    }
    
    public CompoundTag parseCompoundTag() throws CommandSyntaxException {
        this.expect('{');
        final CompoundTag compoundTag1 = new CompoundTag();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            final int integer2 = this.reader.getCursor();
            final String string3 = this.readString();
            if (string3.isEmpty()) {
                this.reader.setCursor(integer2);
                throw StringNbtReader.EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
            this.expect(':');
            compoundTag1.put(string3, this.parseTag());
            if (!this.readComma()) {
                break;
            }
            if (!this.reader.canRead()) {
                throw StringNbtReader.EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        this.expect('}');
        return compoundTag1;
    }
    
    private Tag parseListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        final ListTag listTag1 = new ListTag();
        int integer2 = -1;
        while (this.reader.peek() != ']') {
            final int integer3 = this.reader.getCursor();
            final Tag tag4 = this.parseTag();
            final int integer4 = tag4.getType();
            if (integer2 < 0) {
                integer2 = integer4;
            }
            else if (integer4 != integer2) {
                this.reader.setCursor(integer3);
                throw StringNbtReader.LIST_MIXED.createWithContext((ImmutableStringReader)this.reader, Tag.idToString(integer4), Tag.idToString(integer2));
            }
            listTag1.add(tag4);
            if (!this.readComma()) {
                break;
            }
            if (!this.reader.canRead()) {
                throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        this.expect(']');
        return listTag1;
    }
    
    private Tag parseTagPrimitiveArray() throws CommandSyntaxException {
        this.expect('[');
        final int integer1 = this.reader.getCursor();
        final char character2 = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        if (character2 == 'B') {
            return new ByteArrayTag(this.<Byte>readArray((byte)7, (byte)1));
        }
        if (character2 == 'L') {
            return new LongArrayTag(this.<Long>readArray((byte)12, (byte)4));
        }
        if (character2 == 'I') {
            return new IntArrayTag(this.<Integer>readArray((byte)11, (byte)3));
        }
        this.reader.setCursor(integer1);
        throw StringNbtReader.ARRAY_INVALID.createWithContext((ImmutableStringReader)this.reader, String.valueOf(character2));
    }
    
    private <T extends Number> List<T> readArray(final byte byte1, final byte byte2) throws CommandSyntaxException {
        final List<T> list3 = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            final int integer4 = this.reader.getCursor();
            final Tag tag5 = this.parseTag();
            final int integer5 = tag5.getType();
            if (integer5 != byte2) {
                this.reader.setCursor(integer4);
                throw StringNbtReader.ARRAY_MIXED.createWithContext((ImmutableStringReader)this.reader, Tag.idToString(integer5), Tag.idToString(byte1));
            }
            if (byte2 == 1) {
                list3.add((T)((AbstractNumberTag)tag5).getByte());
            }
            else if (byte2 == 4) {
                list3.add((T)((AbstractNumberTag)tag5).getLong());
            }
            else {
                list3.add((T)((AbstractNumberTag)tag5).getInt());
            }
            if (!this.readComma()) {
                break;
            }
            if (!this.reader.canRead()) {
                throw StringNbtReader.EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        this.expect(']');
        return list3;
    }
    
    private boolean readComma() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }
    
    private void expect(final char character) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(character);
    }
    
    static {
        TRAILING = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.nbt.trailing", new Object[0]));
        EXPECTED_KEY = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.nbt.expected.key", new Object[0]));
        EXPECTED_VALUE = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.nbt.expected.value", new Object[0]));
        LIST_MIXED = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.nbt.list.mixed", new Object[] { object1, object2 }));
        ARRAY_MIXED = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.nbt.array.mixed", new Object[] { object1, object2 }));
        final TranslatableTextComponent translatableTextComponent;
        ARRAY_INVALID = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.nbt.array.invalid", new Object[] { object });
            return translatableTextComponent;
        });
        DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
        DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
        FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
        BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
        LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
        SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
        INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    }
}
