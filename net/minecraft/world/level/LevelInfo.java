package net.minecraft.world.level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.world.GameMode;

public final class LevelInfo
{
    private final long seed;
    private final GameMode gameMode;
    private final boolean structures;
    private final boolean hardcore;
    private final LevelGeneratorType generatorType;
    private boolean commands;
    private boolean bonusChest;
    private JsonElement generatorOptions;
    
    public LevelInfo(final long long1, final GameMode gameMode, final boolean boolean4, final boolean boolean5, final LevelGeneratorType levelGeneratorType6) {
        this.generatorOptions = new JsonObject();
        this.seed = long1;
        this.gameMode = gameMode;
        this.structures = boolean4;
        this.hardcore = boolean5;
        this.generatorType = levelGeneratorType6;
    }
    
    public LevelInfo(final LevelProperties levelProperties) {
        this(levelProperties.getSeed(), levelProperties.getGameMode(), levelProperties.hasStructures(), levelProperties.isHardcore(), levelProperties.getGeneratorType());
    }
    
    public LevelInfo setBonusChest() {
        this.bonusChest = true;
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public LevelInfo enableCommands() {
        this.commands = true;
        return this;
    }
    
    public LevelInfo setGeneratorOptions(final JsonElement jsonElement) {
        this.generatorOptions = jsonElement;
        return this;
    }
    
    public boolean hasBonusChest() {
        return this.bonusChest;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public boolean hasStructures() {
        return this.structures;
    }
    
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
    
    public boolean allowCommands() {
        return this.commands;
    }
    
    public JsonElement getGeneratorOptions() {
        return this.generatorOptions;
    }
}
