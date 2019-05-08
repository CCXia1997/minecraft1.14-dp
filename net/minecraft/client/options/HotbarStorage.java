package net.minecraft.client.options;

import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.NbtIo;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HotbarStorage
{
    private static final Logger LOGGER;
    private final File file;
    private final DataFixer dataFixer;
    private final HotbarStorageEntry[] entries;
    private boolean loaded;
    
    public HotbarStorage(final File file, final DataFixer dataFixer) {
        this.entries = new HotbarStorageEntry[9];
        this.file = new File(file, "hotbar.nbt");
        this.dataFixer = dataFixer;
        for (int integer3 = 0; integer3 < 9; ++integer3) {
            this.entries[integer3] = new HotbarStorageEntry();
        }
    }
    
    private void load() {
        try {
            CompoundTag compoundTag1 = NbtIo.read(this.file);
            if (compoundTag1 == null) {
                return;
            }
            if (!compoundTag1.containsKey("DataVersion", 99)) {
                compoundTag1.putInt("DataVersion", 1343);
            }
            compoundTag1 = TagHelper.update(this.dataFixer, DataFixTypes.d, compoundTag1, compoundTag1.getInt("DataVersion"));
            for (int integer2 = 0; integer2 < 9; ++integer2) {
                this.entries[integer2].fromListTag(compoundTag1.getList(String.valueOf(integer2), 10));
            }
        }
        catch (Exception exception1) {
            HotbarStorage.LOGGER.error("Failed to load creative mode options", (Throwable)exception1);
        }
    }
    
    public void save() {
        try {
            final CompoundTag compoundTag1 = new CompoundTag();
            compoundTag1.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            for (int integer2 = 0; integer2 < 9; ++integer2) {
                compoundTag1.put(String.valueOf(integer2), this.getSavedHotbar(integer2).toListTag());
            }
            NbtIo.write(compoundTag1, this.file);
        }
        catch (Exception exception1) {
            HotbarStorage.LOGGER.error("Failed to save creative mode options", (Throwable)exception1);
        }
    }
    
    public HotbarStorageEntry getSavedHotbar(final int i) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.entries[i];
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
