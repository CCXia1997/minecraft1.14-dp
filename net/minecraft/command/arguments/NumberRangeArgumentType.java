package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;
import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.util.NumberRange;

public interface NumberRangeArgumentType<T extends NumberRange<?>> extends ArgumentType<T>
{
    default IntRangeArgumentType create() {
        return new IntRangeArgumentType();
    }
    
    public static class IntRangeArgumentType implements NumberRangeArgumentType<NumberRange.IntRange>
    {
        private static final Collection<String> EXAMPLES;
        
        public static NumberRange.IntRange getRangeArgument(final CommandContext<ServerCommandSource> commandContext, final String string) {
            return (NumberRange.IntRange)commandContext.getArgument(string, (Class)NumberRange.IntRange.class);
        }
        
        public NumberRange.IntRange a(final StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.IntRange.parse(stringReader);
        }
        
        public Collection<String> getExamples() {
            return IntRangeArgumentType.EXAMPLES;
        }
        
        static {
            EXAMPLES = Arrays.<String>asList("0..5", "0", "-5", "-100..", "..100");
        }
        
        public static class Serializer extends NumberSerializer<IntRangeArgumentType>
        {
            @Override
            public IntRangeArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
                return new IntRangeArgumentType();
            }
        }
    }
    
    public static class FloatRangeArgumentType implements NumberRangeArgumentType<NumberRange.FloatRange>
    {
        private static final Collection<String> EXAMPLES;
        
        public NumberRange.FloatRange a(final StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.FloatRange.parse(stringReader);
        }
        
        public Collection<String> getExamples() {
            return FloatRangeArgumentType.EXAMPLES;
        }
        
        static {
            EXAMPLES = Arrays.<String>asList("0..5.2", "0", "-5.4", "-100.76..", "..100");
        }
        
        public static class Serializer extends NumberSerializer<FloatRangeArgumentType>
        {
            @Override
            public FloatRangeArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
                return new FloatRangeArgumentType();
            }
        }
    }
    
    public abstract static class NumberSerializer<T extends NumberRangeArgumentType<?>> implements ArgumentSerializer<T>
    {
        @Override
        public void toPacket(final T numberRangeArgumentType, final PacketByteBuf packetByteBuf) {
        }
        
        @Override
        public void toJson(final T numberRangeArgumentType, final JsonObject jsonObject) {
        }
    }
}
