package net.minecraft.entity.data;

import net.minecraft.util.PacketByteBuf;

public interface TrackedDataHandler<T>
{
    void write(final PacketByteBuf arg1, final T arg2);
    
    T read(final PacketByteBuf arg1);
    
    default TrackedData<T> create(final int integer) {
        return new TrackedData<T>(integer, this);
    }
    
    T copy(final T arg1);
}
