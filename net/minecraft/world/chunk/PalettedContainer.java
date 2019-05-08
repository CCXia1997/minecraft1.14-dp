package net.minecraft.world.chunk;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.util.IdList;

public class PalettedContainer<T> implements PaletteResizeListener<T>
{
    private final Palette<T> fallbackPalette;
    private final PaletteResizeListener<T> noOpPaletteResizeHandler;
    private final IdList<T> idList;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final T g;
    protected PackedIntegerArray data;
    private Palette<T> palette;
    private int paletteSize;
    private final ReentrantLock writeLock;
    
    public void lock() {
        if (this.writeLock.isLocked() && !this.writeLock.isHeldByCurrentThread()) {
            final String string1 = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.<StackTraceElement>stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            final CrashReport crashReport2 = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            final CrashReportSection crashReportSection3 = crashReport2.addElement("Thread dumps");
            crashReportSection3.add("Thread dumps", string1);
            throw new CrashException(crashReport2);
        }
        this.writeLock.lock();
    }
    
    public void unlock() {
        this.writeLock.unlock();
    }
    
    public PalettedContainer(final Palette<T> fallbackPalette, final IdList<T> idList, final Function<CompoundTag, T> elementDeserializer, final Function<T, CompoundTag> elementSerializer, final T defaultElement) {
        this.noOpPaletteResizeHandler = ((integer, object) -> 0);
        this.writeLock = new ReentrantLock();
        this.fallbackPalette = fallbackPalette;
        this.idList = idList;
        this.elementDeserializer = elementDeserializer;
        this.elementSerializer = elementSerializer;
        this.g = defaultElement;
        this.setPaletteSize(4);
    }
    
    private static int toIndex(final int x, final int y, final int z) {
        return y << 8 | z << 4 | x;
    }
    
    private void setPaletteSize(final int size) {
        if (size == this.paletteSize) {
            return;
        }
        this.paletteSize = size;
        if (this.paletteSize <= 4) {
            this.paletteSize = 4;
            this.palette = new ArrayPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer);
        }
        else if (this.paletteSize < 9) {
            this.palette = new BiMapPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
        }
        else {
            this.palette = this.fallbackPalette;
            this.paletteSize = MathHelper.log2DeBrujin(this.idList.size());
        }
        this.palette.getIndex(this.g);
        this.data = new PackedIntegerArray(this.paletteSize, 4096);
    }
    
    @Override
    public int onResize(final int newSize, final T objectAdded) {
        this.lock();
        final PackedIntegerArray packedIntegerArray3 = this.data;
        final Palette<T> palette4 = this.palette;
        this.setPaletteSize(newSize);
        for (int integer5 = 0; integer5 < packedIntegerArray3.getSize(); ++integer5) {
            final T object6 = palette4.getByIndex(packedIntegerArray3.get(integer5));
            if (object6 != null) {
                this.set(integer5, object6);
            }
        }
        int integer5 = this.palette.getIndex(objectAdded);
        this.unlock();
        return integer5;
    }
    
    public T setSync(final int x, final int y, final int z, final T value) {
        this.lock();
        final T object5 = this.setAndGetOldValue(toIndex(x, y, z), value);
        this.unlock();
        return object5;
    }
    
    public T set(final int x, final int y, final int z, final T value) {
        return this.setAndGetOldValue(toIndex(x, y, z), value);
    }
    
    protected T setAndGetOldValue(final int index, final T value) {
        final int integer3 = this.palette.getIndex(value);
        final int integer4 = this.data.setAndGetOldValue(index, integer3);
        final T object5 = this.palette.getByIndex(integer4);
        return (object5 == null) ? this.g : object5;
    }
    
    protected void set(final int integer, final T object) {
        final int integer2 = this.palette.getIndex(object);
        this.data.set(integer, integer2);
    }
    
    public T get(final int x, final int y, final int z) {
        return this.get(toIndex(x, y, z));
    }
    
    protected T get(final int index) {
        final T object2 = this.palette.getByIndex(this.data.get(index));
        return (object2 == null) ? this.g : object2;
    }
    
    @Environment(EnvType.CLIENT)
    public void fromPacket(final PacketByteBuf buf) {
        this.lock();
        final int integer2 = buf.readByte();
        if (this.paletteSize != integer2) {
            this.setPaletteSize(integer2);
        }
        this.palette.fromPacket(buf);
        buf.readLongArray(this.data.getStorage());
        this.unlock();
    }
    
    public void toPacket(final PacketByteBuf buf) {
        this.lock();
        buf.writeByte(this.paletteSize);
        this.palette.toPacket(buf);
        buf.writeLongArray(this.data.getStorage());
        this.unlock();
    }
    
    public void read(final ListTag paletteTag, final long[] data) {
        this.lock();
        final int integer3 = Math.max(4, MathHelper.log2DeBrujin(paletteTag.size()));
        if (integer3 != this.paletteSize) {
            this.setPaletteSize(integer3);
        }
        this.palette.fromTag(paletteTag);
        final int integer4 = data.length * 64 / 4096;
        if (this.palette == this.fallbackPalette) {
            final Palette<T> palette5 = new BiMapPalette<T>(this.idList, integer3, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
            palette5.fromTag(paletteTag);
            final PackedIntegerArray packedIntegerArray6 = new PackedIntegerArray(integer3, 4096, data);
            for (int integer5 = 0; integer5 < 4096; ++integer5) {
                this.data.set(integer5, this.fallbackPalette.getIndex(palette5.getByIndex(packedIntegerArray6.get(integer5))));
            }
        }
        else if (integer4 == this.paletteSize) {
            System.arraycopy(data, 0, this.data.getStorage(), 0, data.length);
        }
        else {
            final PackedIntegerArray packedIntegerArray7 = new PackedIntegerArray(integer4, 4096, data);
            for (int integer6 = 0; integer6 < 4096; ++integer6) {
                this.data.set(integer6, packedIntegerArray7.get(integer6));
            }
        }
        this.unlock();
    }
    
    public void write(final CompoundTag compoundTag, final String string2, final String string3) {
        this.lock();
        final BiMapPalette<T> biMapPalette4 = new BiMapPalette<T>(this.idList, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
        biMapPalette4.getIndex(this.g);
        final int[] arr5 = new int[4096];
        for (int integer6 = 0; integer6 < 4096; ++integer6) {
            arr5[integer6] = biMapPalette4.getIndex(this.get(integer6));
        }
        final ListTag listTag6 = new ListTag();
        biMapPalette4.toTag(listTag6);
        compoundTag.put(string2, listTag6);
        final int integer7 = Math.max(4, MathHelper.log2DeBrujin(listTag6.size()));
        final PackedIntegerArray packedIntegerArray8 = new PackedIntegerArray(integer7, 4096);
        for (int integer8 = 0; integer8 < arr5.length; ++integer8) {
            packedIntegerArray8.set(integer8, arr5[integer8]);
        }
        compoundTag.putLongArray(string3, packedIntegerArray8.getStorage());
        this.unlock();
    }
    
    public int getPacketSize() {
        return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntSizeBytes(this.data.getSize()) + this.data.getStorage().length * 8;
    }
    
    public boolean a(final T object) {
        return this.palette.accepts(object);
    }
}
