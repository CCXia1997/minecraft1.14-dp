package net.minecraft.world.chunk;

import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import java.util.Arrays;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.util.IdList;

public class ArrayPalette<T> implements Palette<T>
{
    private final IdList<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final Function<CompoundTag, T> valueDeserializer;
    private final int indexBits;
    private int size;
    
    public ArrayPalette(final IdList<T> idList, final int integer, final PaletteResizeListener<T> resizeListener, final Function<CompoundTag, T> valueDeserializer) {
        this.idList = idList;
        this.array = (T[])new Object[1 << integer];
        this.indexBits = integer;
        this.resizeListener = resizeListener;
        this.valueDeserializer = valueDeserializer;
    }
    
    @Override
    public int getIndex(final T object) {
        for (int integer2 = 0; integer2 < this.size; ++integer2) {
            if (this.array[integer2] == object) {
                return integer2;
            }
        }
        int integer2 = this.size;
        if (integer2 < this.array.length) {
            this.array[integer2] = object;
            ++this.size;
            return integer2;
        }
        return this.resizeListener.onResize(this.indexBits + 1, object);
    }
    
    @Override
    public boolean accepts(final T object) {
        return Arrays.<T>stream(this.array).anyMatch(object2 -> object2 == object);
    }
    
    @Nullable
    @Override
    public T getByIndex(final int index) {
        if (index >= 0 && index < this.size) {
            return this.array[index];
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void fromPacket(final PacketByteBuf buf) {
        this.size = buf.readVarInt();
        for (int integer2 = 0; integer2 < this.size; ++integer2) {
            this.array[integer2] = this.idList.get(buf.readVarInt());
        }
    }
    
    @Override
    public void toPacket(final PacketByteBuf buf) {
        buf.writeVarInt(this.size);
        for (int integer2 = 0; integer2 < this.size; ++integer2) {
            buf.writeVarInt(this.idList.getId(this.array[integer2]));
        }
    }
    
    @Override
    public int getPacketSize() {
        int integer1 = PacketByteBuf.getVarIntSizeBytes(this.getSize());
        for (int integer2 = 0; integer2 < this.getSize(); ++integer2) {
            integer1 += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.array[integer2]));
        }
        return integer1;
    }
    
    public int getSize() {
        return this.size;
    }
    
    @Override
    public void fromTag(final ListTag tag) {
        for (int integer2 = 0; integer2 < tag.size(); ++integer2) {
            this.array[integer2] = this.valueDeserializer.apply(tag.getCompoundTag(integer2));
        }
        this.size = tag.size();
    }
}
