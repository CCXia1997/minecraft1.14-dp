package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.io.OutputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.Tag;
import java.io.File;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Logger;

public abstract class PersistentState
{
    private static final Logger LOGGER;
    private final String key;
    private boolean dirty;
    
    public PersistentState(final String key) {
        this.key = key;
    }
    
    public abstract void fromTag(final CompoundTag arg1);
    
    public abstract CompoundTag toTag(final CompoundTag arg1);
    
    public void markDirty() {
        this.setDirty(true);
    }
    
    public void setDirty(final boolean boolean1) {
        this.dirty = boolean1;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public String getId() {
        return this.key;
    }
    
    public void a(final File file) {
        if (!this.isDirty()) {
            return;
        }
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.put("data", this.toTag(new CompoundTag()));
        compoundTag2.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        try (final FileOutputStream fileOutputStream3 = new FileOutputStream(file)) {
            NbtIo.writeCompressed(compoundTag2, fileOutputStream3);
        }
        catch (IOException iOException3) {
            PersistentState.LOGGER.error("Could not save data {}", this, iOException3);
        }
        this.setDirty(false);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
