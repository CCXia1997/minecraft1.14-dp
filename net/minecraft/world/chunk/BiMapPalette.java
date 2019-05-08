package net.minecraft.world.chunk;

import java.util.AbstractList;
import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.IdList;

public class BiMapPalette<T> implements Palette<T>
{
    private final IdList<T> idList;
    private final Int2ObjectBiMap<T> map;
    private final PaletteResizeListener<T> resizeHandler;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final int indexBits;
    
    public BiMapPalette(final IdList<T> idList, final int indexBits, final PaletteResizeListener<T> resizeHandler, final Function<CompoundTag, T> elementDeserializer, final Function<T, CompoundTag> elementSerializer) {
        this.idList = idList;
        this.indexBits = indexBits;
        this.resizeHandler = resizeHandler;
        this.elementDeserializer = elementDeserializer;
        this.elementSerializer = elementSerializer;
        this.map = new Int2ObjectBiMap<T>(1 << indexBits);
    }
    
    @Override
    public int getIndex(final T object) {
        int integer2 = this.map.getId(object);
        if (integer2 == -1) {
            integer2 = this.map.add(object);
            if (integer2 >= 1 << this.indexBits) {
                integer2 = this.resizeHandler.onResize(this.indexBits + 1, object);
            }
        }
        return integer2;
    }
    
    @Override
    public boolean accepts(final T object) {
        return this.map.getId(object) != -1;
    }
    
    @Nullable
    @Override
    public T getByIndex(final int index) {
        return this.map.get(index);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void fromPacket(final PacketByteBuf buf) {
        this.map.clear();
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            this.map.add(this.idList.get(buf.readVarInt()));
        }
    }
    
    @Override
    public void toPacket(final PacketByteBuf buf) {
        final int integer2 = this.getIndexBits();
        buf.writeVarInt(integer2);
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            buf.writeVarInt(this.idList.getId(this.map.get(integer3)));
        }
    }
    
    @Override
    public int getPacketSize() {
        int integer1 = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());
        for (int integer2 = 0; integer2 < this.getIndexBits(); ++integer2) {
            integer1 += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.map.get(integer2)));
        }
        return integer1;
    }
    
    public int getIndexBits() {
        return this.map.size();
    }
    
    @Override
    public void fromTag(final ListTag tag) {
        this.map.clear();
        for (int integer2 = 0; integer2 < tag.size(); ++integer2) {
            this.map.add(this.elementDeserializer.apply(tag.getCompoundTag(integer2)));
        }
    }
    
    public void toTag(final ListTag tag) {
        for (int integer2 = 0; integer2 < this.getIndexBits(); ++integer2) {
            ((AbstractList<CompoundTag>)tag).add(this.elementSerializer.apply(this.map.get(integer2)));
        }
    }
}
