package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.arguments.ArgumentType;

public interface ArgumentSerializer<T extends ArgumentType<?>>
{
    void toPacket(final T arg1, final PacketByteBuf arg2);
    
    T fromPacket(final PacketByteBuf arg1);
    
    void toJson(final T arg1, final JsonObject arg2);
}
