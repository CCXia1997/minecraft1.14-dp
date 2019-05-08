package net.minecraft.world.chunk;

import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;

public interface Palette<T>
{
    int getIndex(final T arg1);
    
    boolean accepts(final T arg1);
    
    @Nullable
    T getByIndex(final int arg1);
    
    @Environment(EnvType.CLIENT)
    void fromPacket(final PacketByteBuf arg1);
    
    void toPacket(final PacketByteBuf arg1);
    
    int getPacketSize();
    
    void fromTag(final ListTag arg1);
}
