package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelSummary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsLevelSummary implements Comparable<RealmsLevelSummary>
{
    private final LevelSummary levelSummary;
    
    public RealmsLevelSummary(final LevelSummary levelSummary) {
        this.levelSummary = levelSummary;
    }
    
    public int getGameMode() {
        return this.levelSummary.getGameMode().getId();
    }
    
    public String getLevelId() {
        return this.levelSummary.getName();
    }
    
    public boolean hasCheats() {
        return this.levelSummary.hasCheats();
    }
    
    public boolean isHardcore() {
        return this.levelSummary.isHardcore();
    }
    
    public boolean isRequiresConversion() {
        return this.levelSummary.requiresConversion();
    }
    
    public String getLevelName() {
        return this.levelSummary.getDisplayName();
    }
    
    public long getLastPlayed() {
        return this.levelSummary.getLastPlayed();
    }
    
    public int compareTo(final LevelSummary levelSummary) {
        return this.levelSummary.a(levelSummary);
    }
    
    public long getSizeOnDisk() {
        return this.levelSummary.getSizeOnDisk();
    }
    
    @Override
    public int compareTo(final RealmsLevelSummary realmsLevelSummary) {
        if (this.levelSummary.getLastPlayed() < realmsLevelSummary.getLastPlayed()) {
            return 1;
        }
        if (this.levelSummary.getLastPlayed() > realmsLevelSummary.getLastPlayed()) {
            return -1;
        }
        return this.levelSummary.getName().compareTo(realmsLevelSummary.getLevelId());
    }
}
