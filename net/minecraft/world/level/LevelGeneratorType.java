package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class LevelGeneratorType
{
    public static final LevelGeneratorType[] TYPES;
    public static final LevelGeneratorType DEFAULT;
    public static final LevelGeneratorType FLAT;
    public static final LevelGeneratorType LARGE_BIOMES;
    public static final LevelGeneratorType AMPLIFIED;
    public static final LevelGeneratorType CUSTOMIZED;
    public static final LevelGeneratorType BUFFET;
    public static final LevelGeneratorType DEBUG_ALL_BLOCK_STATES;
    public static final LevelGeneratorType DEFAULT_1_1;
    private final int id;
    private final String name;
    private final String storedName;
    private final int version;
    private boolean visible;
    private boolean versioned;
    private boolean info;
    private boolean customizable;
    
    private LevelGeneratorType(final int id, final String name) {
        this(id, name, name, 0);
    }
    
    private LevelGeneratorType(final int id, final String name, final int version) {
        this(id, name, name, version);
    }
    
    private LevelGeneratorType(final int id, final String name, final String storedName, final int version) {
        this.name = name;
        this.storedName = storedName;
        this.version = version;
        this.visible = true;
        this.id = id;
        LevelGeneratorType.TYPES[id] = this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getStoredName() {
        return this.storedName;
    }
    
    @Environment(EnvType.CLIENT)
    public String getTranslationKey() {
        return "generator." + this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public String getInfoTranslationKey() {
        return this.getTranslationKey() + ".info";
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public LevelGeneratorType getTypeForVersion(final int integer) {
        if (this == LevelGeneratorType.DEFAULT && integer == 0) {
            return LevelGeneratorType.DEFAULT_1_1;
        }
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isCustomizable() {
        return this.customizable;
    }
    
    public LevelGeneratorType setCustomizable(final boolean customizable) {
        this.customizable = customizable;
        return this;
    }
    
    private LevelGeneratorType setVisible(final boolean visible) {
        this.visible = visible;
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isVisible() {
        return this.visible;
    }
    
    private LevelGeneratorType setVersioned() {
        this.versioned = true;
        return this;
    }
    
    public boolean isVersioned() {
        return this.versioned;
    }
    
    @Nullable
    public static LevelGeneratorType getTypeFromName(final String name) {
        for (final LevelGeneratorType levelGeneratorType5 : LevelGeneratorType.TYPES) {
            if (levelGeneratorType5 != null && levelGeneratorType5.name.equalsIgnoreCase(name)) {
                return levelGeneratorType5;
            }
        }
        return null;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasInfo() {
        return this.info;
    }
    
    private LevelGeneratorType setHasInfo() {
        this.info = true;
        return this;
    }
    
    static {
        TYPES = new LevelGeneratorType[16];
        DEFAULT = new LevelGeneratorType(0, "default", 1).setVersioned();
        FLAT = new LevelGeneratorType(1, "flat").setCustomizable(true);
        LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes");
        AMPLIFIED = new LevelGeneratorType(3, "amplified").setHasInfo();
        CUSTOMIZED = new LevelGeneratorType(4, "customized", "normal", 0).setCustomizable(true).setVisible(false);
        BUFFET = new LevelGeneratorType(5, "buffet").setCustomizable(true);
        DEBUG_ALL_BLOCK_STATES = new LevelGeneratorType(6, "debug_all_block_states");
        DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0).setVisible(false);
    }
}
