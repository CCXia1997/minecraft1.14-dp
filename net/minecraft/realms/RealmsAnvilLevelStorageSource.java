package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelStorageException;
import java.util.Iterator;
import net.minecraft.world.level.storage.LevelSummary;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsAnvilLevelStorageSource
{
    private final LevelStorage levelStorageSource;
    
    public RealmsAnvilLevelStorageSource(final LevelStorage levelStorage) {
        this.levelStorageSource = levelStorage;
    }
    
    public String getName() {
        return this.levelStorageSource.getName();
    }
    
    public boolean levelExists(final String string) {
        return this.levelStorageSource.levelExists(string);
    }
    
    public boolean convertLevel(final String string, final ProgressListener progressListener) {
        return this.levelStorageSource.convertLevel(string, progressListener);
    }
    
    public boolean requiresConversion(final String string) {
        return this.levelStorageSource.requiresConversion(string);
    }
    
    public boolean isNewLevelIdAcceptable(final String string) {
        return this.levelStorageSource.isLevelNameValid(string);
    }
    
    public boolean deleteLevel(final String string) {
        return this.levelStorageSource.deleteLevel(string);
    }
    
    public void renameLevel(final String string1, final String string2) {
        this.levelStorageSource.renameLevel(string1, string2);
    }
    
    public List<RealmsLevelSummary> getLevelList() throws LevelStorageException {
        final List<RealmsLevelSummary> list1 = Lists.newArrayList();
        for (final LevelSummary levelSummary3 : this.levelStorageSource.getLevelList()) {
            list1.add(new RealmsLevelSummary(levelSummary3));
        }
        return list1;
    }
}
