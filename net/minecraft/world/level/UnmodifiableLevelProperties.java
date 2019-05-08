package net.minecraft.world.level;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class UnmodifiableLevelProperties extends LevelProperties
{
    private final LevelProperties properties;
    
    public UnmodifiableLevelProperties(final LevelProperties levelProperties) {
        this.properties = levelProperties;
    }
    
    @Override
    public CompoundTag cloneWorldTag(@Nullable final CompoundTag playerTag) {
        return this.properties.cloneWorldTag(playerTag);
    }
    
    @Override
    public long getSeed() {
        return this.properties.getSeed();
    }
    
    @Override
    public int getSpawnX() {
        return this.properties.getSpawnX();
    }
    
    @Override
    public int getSpawnY() {
        return this.properties.getSpawnY();
    }
    
    @Override
    public int getSpawnZ() {
        return this.properties.getSpawnZ();
    }
    
    @Override
    public long getTime() {
        return this.properties.getTime();
    }
    
    @Override
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }
    
    @Override
    public CompoundTag getPlayerData() {
        return this.properties.getPlayerData();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getDimension() {
        return this.properties.getDimension();
    }
    
    @Override
    public String getLevelName() {
        return this.properties.getLevelName();
    }
    
    @Override
    public int getVersion() {
        return this.properties.getVersion();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public long getLastPlayed() {
        return this.properties.getLastPlayed();
    }
    
    @Override
    public boolean isThundering() {
        return this.properties.isThundering();
    }
    
    @Override
    public int getThunderTime() {
        return this.properties.getThunderTime();
    }
    
    @Override
    public boolean isRaining() {
        return this.properties.isRaining();
    }
    
    @Override
    public int getRainTime() {
        return this.properties.getRainTime();
    }
    
    @Override
    public GameMode getGameMode() {
        return this.properties.getGameMode();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setSpawnX(final int spawnX) {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setSpawnY(final int spawnY) {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setSpawnZ(final int spawnZ) {
    }
    
    @Override
    public void setTime(final long time) {
    }
    
    @Override
    public void setTimeOfDay(final long timeOfDay) {
    }
    
    @Override
    public void setSpawnPos(final BlockPos blockPos) {
    }
    
    @Override
    public void setLevelName(final String levelName) {
    }
    
    @Override
    public void setVersion(final int version) {
    }
    
    @Override
    public void setThundering(final boolean thundering) {
    }
    
    @Override
    public void setThunderTime(final int thunderTime) {
    }
    
    @Override
    public void setRaining(final boolean raining) {
    }
    
    @Override
    public void setRainTime(final int rainTime) {
    }
    
    @Override
    public boolean hasStructures() {
        return this.properties.hasStructures();
    }
    
    @Override
    public boolean isHardcore() {
        return this.properties.isHardcore();
    }
    
    @Override
    public LevelGeneratorType getGeneratorType() {
        return this.properties.getGeneratorType();
    }
    
    @Override
    public void setGeneratorType(final LevelGeneratorType levelGeneratorType) {
    }
    
    @Override
    public boolean areCommandsAllowed() {
        return this.properties.areCommandsAllowed();
    }
    
    @Override
    public void setCommandsAllowed(final boolean commandsAllowed) {
    }
    
    @Override
    public boolean isInitialized() {
        return this.properties.isInitialized();
    }
    
    @Override
    public void setInitialized(final boolean initialized) {
    }
    
    @Override
    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }
    
    @Override
    public Difficulty getDifficulty() {
        return this.properties.getDifficulty();
    }
    
    @Override
    public void setDifficulty(final Difficulty difficulty) {
    }
    
    @Override
    public boolean isDifficultyLocked() {
        return this.properties.isDifficultyLocked();
    }
    
    @Override
    public void setDifficultyLocked(final boolean difficultyLocked) {
    }
    
    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.properties.getScheduledEvents();
    }
    
    @Override
    public void setWorldData(final DimensionType type, final CompoundTag compoundTag) {
        this.properties.setWorldData(type, compoundTag);
    }
    
    @Override
    public CompoundTag getWorldData(final DimensionType dimensionType) {
        return this.properties.getWorldData(dimensionType);
    }
}
