package net.minecraft.command.arguments;

import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import com.mojang.brigadier.arguments.ArgumentType;

public class NbtCompoundTagArgumentType implements ArgumentType<CompoundTag>
{
    private static final Collection<String> EXAMPLES;
    
    private NbtCompoundTagArgumentType() {
    }
    
    public static NbtCompoundTagArgumentType create() {
        return new NbtCompoundTagArgumentType();
    }
    
    public static <S> CompoundTag getCompoundTag(final CommandContext<S> context, final String name) {
        return (CompoundTag)context.getArgument(name, (Class)CompoundTag.class);
    }
    
    public CompoundTag a(final StringReader stringReader) throws CommandSyntaxException {
        return new StringNbtReader(stringReader).parseCompoundTag();
    }
    
    public Collection<String> getExamples() {
        return NbtCompoundTagArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("{}", "{foo=bar}");
    }
}
