package net.minecraft.network;

import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener>
{
    void read(final PacketByteBuf arg1) throws IOException;
    
    void write(final PacketByteBuf arg1) throws IOException;
    
    void apply(final T arg1);
    
    default boolean isErrorFatal() {
        return false;
    }
}
