package net.minecraft.world.chunk;

import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.IdList;

public class IdListPalette<T> implements Palette<T>
{
    private final IdList<T> idList;
    private final T fallback;
    
    public IdListPalette(final IdList<T> idList, final T defaultValue) {
        this.idList = idList;
        this.fallback = defaultValue;
    }
    
    @Override
    public int getIndex(final T object) {
        final int integer2 = this.idList.getId(object);
        return (integer2 == -1) ? 0 : integer2;
    }
    
    @Override
    public boolean accepts(final T object) {
        return true;
    }
    
    @Override
    public T getByIndex(final int index) {
        final T object2 = this.idList.get(index);
        return (object2 == null) ? this.fallback : object2;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void fromPacket(final PacketByteBuf buf) {
    }
    
    @Override
    public void toPacket(final PacketByteBuf buf) {
    }
    
    @Override
    public int getPacketSize() {
        return PacketByteBuf.getVarIntSizeBytes(0);
    }
    
    @Override
    public void fromTag(final ListTag tag) {
    }
}
