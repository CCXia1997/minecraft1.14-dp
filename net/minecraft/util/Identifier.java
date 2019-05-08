package net.minecraft.util;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class Identifier implements Comparable<Identifier>
{
    private static final SimpleCommandExceptionType EXCEPTION_INVALID;
    protected final String namespace;
    protected final String path;
    
    protected Identifier(final String[] arr) {
        this.namespace = (StringUtils.isEmpty((CharSequence)arr[0]) ? "minecraft" : arr[0]);
        this.path = arr[1];
        if (!isValidNamespace(this.namespace)) {
            throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
        }
        if (!isValidPath(this.path)) {
            throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
        }
    }
    
    public Identifier(final String id) {
        this(split(id, ':'));
    }
    
    public Identifier(final String namespace, final String name) {
        this(new String[] { namespace, name });
    }
    
    public static Identifier createSplit(final String str, final char chr) {
        return new Identifier(split(str, chr));
    }
    
    @Nullable
    public static Identifier create(final String s) {
        try {
            return new Identifier(s);
        }
        catch (InvalidIdentifierException invalidIdentifierException2) {
            return null;
        }
    }
    
    protected static String[] split(final String string, final char character) {
        final String[] arr3 = { "minecraft", string };
        final int integer4 = string.indexOf(character);
        if (integer4 >= 0) {
            arr3[1] = string.substring(integer4 + 1, string.length());
            if (integer4 >= 1) {
                arr3[0] = string.substring(0, integer4);
            }
        }
        return arr3;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    @Override
    public String toString() {
        return this.namespace + ':' + this.path;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Identifier) {
            final Identifier identifier2 = (Identifier)o;
            return this.namespace.equals(identifier2.namespace) && this.path.equals(identifier2.path);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }
    
    public int a(final Identifier identifier) {
        int integer2 = this.path.compareTo(identifier.path);
        if (integer2 == 0) {
            integer2 = this.namespace.compareTo(identifier.namespace);
        }
        return integer2;
    }
    
    public static Identifier parse(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        while (stringReader.canRead() && isValidChar(stringReader.peek())) {
            stringReader.skip();
        }
        final String string3 = stringReader.getString().substring(integer2, stringReader.getCursor());
        try {
            return new Identifier(string3);
        }
        catch (InvalidIdentifierException invalidIdentifierException4) {
            stringReader.setCursor(integer2);
            throw Identifier.EXCEPTION_INVALID.createWithContext((ImmutableStringReader)stringReader);
        }
    }
    
    public static boolean isValidChar(final char chr) {
        return (chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'z') || chr == '_' || chr == ':' || chr == '/' || chr == '.' || chr == '-';
    }
    
    private static boolean isValidPath(final String string) {
        return string.chars().allMatch(integer -> integer == 95 || integer == 45 || (integer >= 97 && integer <= 122) || (integer >= 48 && integer <= 57) || integer == 47 || integer == 46);
    }
    
    private static boolean isValidNamespace(final String string) {
        return string.chars().allMatch(integer -> integer == 95 || integer == 45 || (integer >= 97 && integer <= 122) || (integer >= 48 && integer <= 57) || integer == 46);
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean isValidIdentifier(final String string) {
        final String[] arr2 = split(string, ':');
        return isValidNamespace(StringUtils.isEmpty((CharSequence)arr2[0]) ? "minecraft" : arr2[0]) && isValidPath(arr2[1]);
    }
    
    static {
        EXCEPTION_INVALID = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.id.invalid", new Object[0]));
    }
    
    public static class Serializer implements JsonDeserializer<Identifier>, JsonSerializer<Identifier>
    {
        public Identifier a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            return new Identifier(JsonHelper.asString(functionJson, "location"));
        }
        
        public JsonElement a(final Identifier identifier, final Type type, final JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(identifier.toString());
        }
    }
}
