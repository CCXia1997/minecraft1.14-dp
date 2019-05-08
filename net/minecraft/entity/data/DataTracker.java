package net.minecraft.entity.data;

import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.google.common.collect.Maps;
import java.util.concurrent.locks.ReadWriteLock;
import net.minecraft.entity.Entity;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class DataTracker
{
    private static final Logger LOGGER;
    private static final Map<Class<? extends Entity>, Integer> trackedEntities;
    private final Entity trackedEntity;
    private final Map<Integer, Entry<?>> entries;
    private final ReadWriteLock lock;
    private boolean empty;
    private boolean dirty;
    
    public DataTracker(final Entity entity) {
        this.entries = Maps.newHashMap();
        this.lock = new ReentrantReadWriteLock();
        this.empty = true;
        this.trackedEntity = entity;
    }
    
    public static <T> TrackedData<T> registerData(final Class<? extends Entity> entityClass, final TrackedDataHandler<T> dataHandler) {
        if (DataTracker.LOGGER.isDebugEnabled()) {
            try {
                final Class<?> class3 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
                if (!class3.equals(entityClass)) {
                    DataTracker.LOGGER.debug("defineId called for: {} from {}", entityClass, class3, new RuntimeException());
                }
            }
            catch (ClassNotFoundException ex) {}
        }
        int integer3;
        if (DataTracker.trackedEntities.containsKey(entityClass)) {
            integer3 = DataTracker.trackedEntities.get(entityClass) + 1;
        }
        else {
            int integer4 = 0;
            Class<?> class4 = entityClass;
            while (class4 != Entity.class) {
                class4 = class4.getSuperclass();
                if (DataTracker.trackedEntities.containsKey(class4)) {
                    integer4 = DataTracker.trackedEntities.get(class4) + 1;
                    break;
                }
            }
            integer3 = integer4;
        }
        if (integer3 > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + integer3 + "! (Max is " + 254 + ")");
        }
        DataTracker.trackedEntities.put(entityClass, integer3);
        return dataHandler.create(integer3);
    }
    
    public <T> void startTracking(final TrackedData<T> key, final T initialValue) {
        final int integer3 = key.getId();
        if (integer3 > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + integer3 + "! (Max is " + 254 + ")");
        }
        if (this.entries.containsKey(integer3)) {
            throw new IllegalArgumentException("Duplicate id value for " + integer3 + "!");
        }
        if (TrackedDataHandlerRegistry.getId(key.getType()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + key.getType() + " for " + integer3 + "!");
        }
        this.addTrackedData((TrackedData<Object>)key, initialValue);
    }
    
    private <T> void addTrackedData(final TrackedData<T> trackedData, final T object) {
        final Entry<T> entry3 = new Entry<T>(trackedData, object);
        this.lock.writeLock().lock();
        this.entries.put(trackedData.getId(), entry3);
        this.empty = false;
        this.lock.writeLock().unlock();
    }
    
    private <T> Entry<T> getEntry(final TrackedData<T> trackedData) {
        this.lock.readLock().lock();
        Entry<T> entry2;
        try {
            entry2 = (Entry<T>)this.entries.get(trackedData.getId());
        }
        catch (Throwable throwable3) {
            final CrashReport crashReport4 = CrashReport.create(throwable3, "Getting synched entity data");
            final CrashReportSection crashReportSection5 = crashReport4.addElement("Synched entity data");
            crashReportSection5.add("Data ID", trackedData);
            throw new CrashException(crashReport4);
        }
        finally {
            this.lock.readLock().unlock();
        }
        return entry2;
    }
    
    public <T> T get(final TrackedData<T> trackedData) {
        return this.<T>getEntry(trackedData).get();
    }
    
    public <T> void set(final TrackedData<T> key, final T object) {
        final Entry<T> entry3 = this.<T>getEntry(key);
        if (ObjectUtils.notEqual(object, entry3.get())) {
            entry3.set(object);
            this.trackedEntity.onTrackedDataSet(key);
            entry3.setDirty(true);
            this.dirty = true;
        }
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public static void entriesToPacket(final List<Entry<?>> list, final PacketByteBuf packetByteBuf) throws IOException {
        if (list != null) {
            for (int integer3 = 0, integer4 = list.size(); integer3 < integer4; ++integer3) {
                DataTracker.writeEntryToPacket(packetByteBuf, list.get(integer3));
            }
        }
        packetByteBuf.writeByte(255);
    }
    
    @Nullable
    public List<Entry<?>> getDirtyEntries() {
        List<Entry<?>> list1 = null;
        if (this.dirty) {
            this.lock.readLock().lock();
            for (final Entry<?> entry3 : this.entries.values()) {
                if (entry3.isDirty()) {
                    entry3.setDirty(false);
                    if (list1 == null) {
                        list1 = Lists.newArrayList();
                    }
                    list1.add(entry3.copy());
                }
            }
            this.lock.readLock().unlock();
        }
        this.dirty = false;
        return list1;
    }
    
    public void toPacketByteBuf(final PacketByteBuf packetByteBuf) throws IOException {
        this.lock.readLock().lock();
        for (final Entry<?> entry3 : this.entries.values()) {
            DataTracker.writeEntryToPacket(packetByteBuf, entry3);
        }
        this.lock.readLock().unlock();
        packetByteBuf.writeByte(255);
    }
    
    @Nullable
    public List<Entry<?>> getAllEntries() {
        List<Entry<?>> list1 = null;
        this.lock.readLock().lock();
        for (final Entry<?> entry3 : this.entries.values()) {
            if (list1 == null) {
                list1 = Lists.newArrayList();
            }
            list1.add(entry3.copy());
        }
        this.lock.readLock().unlock();
        return list1;
    }
    
    private static <T> void writeEntryToPacket(final PacketByteBuf packetByteBuf, final Entry<T> entry) throws IOException {
        final TrackedData<T> trackedData3 = entry.getData();
        final int integer4 = TrackedDataHandlerRegistry.getId(trackedData3.getType());
        if (integer4 < 0) {
            throw new EncoderException("Unknown serializer type " + trackedData3.getType());
        }
        packetByteBuf.writeByte(trackedData3.getId());
        packetByteBuf.writeVarInt(integer4);
        trackedData3.getType().write(packetByteBuf, entry.get());
    }
    
    @Nullable
    public static List<Entry<?>> deserializePacket(final PacketByteBuf packetByteBuf) throws IOException {
        List<Entry<?>> list2 = null;
        int integer3;
        while ((integer3 = packetByteBuf.readUnsignedByte()) != 255) {
            if (list2 == null) {
                list2 = Lists.newArrayList();
            }
            final int integer4 = packetByteBuf.readVarInt();
            final TrackedDataHandler<?> trackedDataHandler5 = TrackedDataHandlerRegistry.get(integer4);
            if (trackedDataHandler5 == null) {
                throw new DecoderException("Unknown serializer type " + integer4);
            }
            list2.add(DataTracker.entryFromPacket(packetByteBuf, integer3, trackedDataHandler5));
        }
        return list2;
    }
    
    private static <T> Entry<T> entryFromPacket(final PacketByteBuf packetByteBuf, final int integer, final TrackedDataHandler<T> trackedDataHandler) {
        return new Entry<T>(trackedDataHandler.create(integer), trackedDataHandler.read(packetByteBuf));
    }
    
    @Environment(EnvType.CLIENT)
    public void writeUpdatedEntries(final List<Entry<?>> list) {
        this.lock.writeLock().lock();
        for (final Entry<?> entry3 : list) {
            final Entry<?> entry4 = this.entries.get(entry3.getData().getId());
            if (entry4 != null) {
                this.copyToFrom(entry4, entry3);
                this.trackedEntity.onTrackedDataSet(entry3.getData());
            }
        }
        this.lock.writeLock().unlock();
        this.dirty = true;
    }
    
    @Environment(EnvType.CLIENT)
    protected <T> void copyToFrom(final Entry<T> destination, final Entry<?> origin) {
        destination.set((T)origin.get());
    }
    
    public boolean isEmpty() {
        return this.empty;
    }
    
    public void clearDirty() {
        this.dirty = false;
        this.lock.readLock().lock();
        for (final Entry<?> entry2 : this.entries.values()) {
            entry2.setDirty(false);
        }
        this.lock.readLock().unlock();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        trackedEntities = Maps.newHashMap();
    }
    
    public static class Entry<T>
    {
        private final TrackedData<T> data;
        private T value;
        private boolean dirty;
        
        public Entry(final TrackedData<T> data, final T value) {
            this.data = data;
            this.value = value;
            this.dirty = true;
        }
        
        public TrackedData<T> getData() {
            return this.data;
        }
        
        public void set(final T value) {
            this.value = value;
        }
        
        public T get() {
            return this.value;
        }
        
        public boolean isDirty() {
            return this.dirty;
        }
        
        public void setDirty(final boolean dirty) {
            this.dirty = dirty;
        }
        
        public Entry<T> copy() {
            return new Entry<T>(this.data, this.data.getType().copy(this.value));
        }
    }
}
