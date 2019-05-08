package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import java.io.DataInputStream;
import net.minecraft.nbt.NbtIo;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.FileInputStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.SharedConstants;
import java.util.function.Supplier;
import com.google.common.collect.Maps;
import java.io.File;
import com.mojang.datafixers.DataFixer;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class PersistentStateManager
{
    private static final Logger LOGGER;
    private final Map<String, PersistentState> loadedStates;
    private final DataFixer dataFixer;
    private final File directory;
    
    public PersistentStateManager(final File directory, final DataFixer dataFixer) {
        this.loadedStates = Maps.newHashMap();
        this.dataFixer = dataFixer;
        this.directory = directory;
    }
    
    private File getFile(final String id) {
        return new File(this.directory, id + ".dat");
    }
    
    public <T extends PersistentState> T getOrCreate(final Supplier<T> factory, final String string) {
        final T persistentState3 = (T)this.<PersistentState>get((Supplier<PersistentState>)factory, string);
        if (persistentState3 != null) {
            return persistentState3;
        }
        final T persistentState4 = factory.get();
        this.set(persistentState4);
        return persistentState4;
    }
    
    @Nullable
    public <T extends PersistentState> T get(final Supplier<T> factory, final String id) {
        PersistentState persistentState3 = this.loadedStates.get(id);
        if (persistentState3 == null) {
            try {
                final File file4 = this.getFile(id);
                if (file4.exists()) {
                    persistentState3 = factory.get();
                    final CompoundTag compoundTag5 = this.a(id, SharedConstants.getGameVersion().getWorldVersion());
                    persistentState3.fromTag(compoundTag5.getCompound("data"));
                    this.loadedStates.put(id, persistentState3);
                }
            }
            catch (Exception exception4) {
                PersistentStateManager.LOGGER.error("Error loading saved data: {}", id, exception4);
            }
        }
        return (T)persistentState3;
    }
    
    public void set(final PersistentState persistentState) {
        this.loadedStates.put(persistentState.getId(), persistentState);
    }
    
    public CompoundTag a(final String string, final int integer) throws IOException {
        final File file3 = this.getFile(string);
        try (final PushbackInputStream pushbackInputStream4 = new PushbackInputStream(new FileInputStream(file3), 2)) {
            CompoundTag compoundTag6;
            if (this.a(pushbackInputStream4)) {
                compoundTag6 = NbtIo.readCompressed(pushbackInputStream4);
            }
            else {
                try (final DataInputStream dataInputStream7 = new DataInputStream(pushbackInputStream4)) {
                    compoundTag6 = NbtIo.read(dataInputStream7);
                }
            }
            final int integer2 = compoundTag6.containsKey("DataVersion", 99) ? compoundTag6.getInt("DataVersion") : 1343;
            return TagHelper.update(this.dataFixer, DataFixTypes.h, compoundTag6, integer2, integer);
        }
    }
    
    private boolean a(final PushbackInputStream pushbackInputStream) throws IOException {
        final byte[] arr2 = new byte[2];
        boolean boolean3 = false;
        final int integer4 = pushbackInputStream.read(arr2, 0, 2);
        if (integer4 == 2) {
            final int integer5 = (arr2[1] & 0xFF) << 8 | (arr2[0] & 0xFF);
            if (integer5 == 35615) {
                boolean3 = true;
            }
        }
        if (integer4 != 0) {
            pushbackInputStream.unread(arr2, 0, integer4);
        }
        return boolean3;
    }
    
    public void save() {
        for (final PersistentState persistentState2 : this.loadedStates.values()) {
            persistentState2.a(this.getFile(persistentState2.getId()));
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
