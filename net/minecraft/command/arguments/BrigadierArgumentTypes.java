package net.minecraft.command.arguments;

import net.minecraft.command.arguments.serialize.StringArgumentSerializer;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.serialize.IntegerArgumentSerializer;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.serialize.DoubleArgumentSerializer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.serialize.FloatArgumentSerializer;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import java.util.function.Supplier;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import com.mojang.brigadier.arguments.BoolArgumentType;

public class BrigadierArgumentTypes
{
    public static void register() {
        ArgumentTypes.<ArgumentType>register("brigadier:bool", (Class<ArgumentType>)BoolArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)BoolArgumentType::bool));
        ArgumentTypes.<ArgumentType>register("brigadier:float", (Class<ArgumentType>)FloatArgumentType.class, (ArgumentSerializer<ArgumentType>)new FloatArgumentSerializer());
        ArgumentTypes.<ArgumentType>register("brigadier:double", (Class<ArgumentType>)DoubleArgumentType.class, (ArgumentSerializer<ArgumentType>)new DoubleArgumentSerializer());
        ArgumentTypes.<ArgumentType>register("brigadier:integer", (Class<ArgumentType>)IntegerArgumentType.class, (ArgumentSerializer<ArgumentType>)new IntegerArgumentSerializer());
        ArgumentTypes.<ArgumentType>register("brigadier:string", (Class<ArgumentType>)StringArgumentType.class, (ArgumentSerializer<ArgumentType>)new StringArgumentSerializer());
    }
    
    public static byte createFlag(final boolean hasMin, final boolean hasMax) {
        byte byte3 = 0;
        if (hasMin) {
            byte3 |= 0x1;
        }
        if (hasMax) {
            byte3 |= 0x2;
        }
        return byte3;
    }
    
    public static boolean hasMin(final byte rangeFlag) {
        return (rangeFlag & 0x1) != 0x0;
    }
    
    public static boolean hasMax(final byte rangeFlag) {
        return (rangeFlag & 0x2) != 0x0;
    }
}
