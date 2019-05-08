package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import java.util.function.Supplier;
import com.mojang.brigadier.arguments.ArgumentType;

public class ConstantArgumentSerializer<T extends ArgumentType<?>> implements ArgumentSerializer<T>
{
    private final Supplier<T> supplier;
    
    public ConstantArgumentSerializer(final Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public void toPacket(final T argumentType, final PacketByteBuf packetByteBuf) {
    }
    
    @Override
    public T fromPacket(final PacketByteBuf packetByteBuf) {
        return this.supplier.get();
    }
    
    @Override
    public void toJson(final T argumentType, final JsonObject jsonObject) {
    }
}
