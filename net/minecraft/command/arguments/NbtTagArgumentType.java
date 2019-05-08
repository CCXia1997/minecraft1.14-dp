package net.minecraft.command.arguments;

import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import com.mojang.brigadier.arguments.ArgumentType;

public class NbtTagArgumentType implements ArgumentType<Tag>
{
    private static final Collection<String> EXAMPLES;
    
    private NbtTagArgumentType() {
    }
    
    public static NbtTagArgumentType create() {
        return new NbtTagArgumentType();
    }
    
    public static <S> Tag getTag(final CommandContext<S> context, final String name) {
        return (Tag)context.getArgument(name, (Class)Tag.class);
    }
    
    public Tag a(final StringReader stringReader) throws CommandSyntaxException {
        return new StringNbtReader(stringReader).parseTag();
    }
    
    public Collection<String> getExamples() {
        return NbtTagArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");
    }
}
