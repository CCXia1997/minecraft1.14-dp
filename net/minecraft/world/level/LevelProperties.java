package net.minecraft.world.level;

import java.util.AbstractList;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.nbt.Tag;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.NbtOps;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.nbt.ListTag;
import java.util.Iterator;
import net.minecraft.SharedConstants;
import net.minecraft.world.timer.TimerCallbackSerializer;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.GameRules;
import java.util.UUID;
import net.minecraft.world.dimension.DimensionType;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.GameMode;
import com.mojang.datafixers.DataFixer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;

public class LevelProperties
{
    private String versionName;
    private int versionId;
    private boolean versionSnapshot;
    public static final Difficulty DEFAULT_DIFFICULTY;
    private long randomSeed;
    private LevelGeneratorType generatorType;
    private CompoundTag generatorOptions;
    @Nullable
    private String legacyCustomOptions;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long time;
    private long timeOfDay;
    private long lastPlayed;
    private long sizeOnDisk;
    @Nullable
    private final DataFixer dataFixer;
    private final int playerWorldId;
    private boolean playerDataLoaded;
    private CompoundTag playerData;
    private int dimension;
    private String levelName;
    private int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private GameMode gameMode;
    private boolean structures;
    private boolean hardcore;
    private boolean commandsAllowed;
    private boolean initialized;
    private Difficulty difficulty;
    private boolean difficultyLocked;
    private double borderCenterX;
    private double borderCenterZ;
    private double borderSize;
    private long borderSizeLerpTime;
    private double borderSizeLerpTarget;
    private double borderSafeZone;
    private double borderDamagePerBlock;
    private int borderWarningBlocks;
    private int borderWarningTime;
    private final Set<String> disabledDataPacks;
    private final Set<String> enabledDataPacks;
    private final Map<DimensionType, CompoundTag> worldData;
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    private UUID wanderingTraderId;
    private final GameRules gameRules;
    private final Timer<MinecraftServer> scheduledEvents;
    
    protected LevelProperties() {
        this.generatorType = LevelGeneratorType.DEFAULT;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = Sets.newHashSet();
        this.enabledDataPacks = Sets.newLinkedHashSet();
        this.worldData = Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE);
        this.dataFixer = null;
        this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
        this.setGeneratorOptions(new CompoundTag());
    }
    
    public LevelProperties(final CompoundTag compoundTag1, final DataFixer dataFixer, final int integer, @Nullable final CompoundTag compoundTag4) {
        this.generatorType = LevelGeneratorType.DEFAULT;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = Sets.newHashSet();
        this.enabledDataPacks = Sets.newLinkedHashSet();
        this.worldData = Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE);
        this.dataFixer = dataFixer;
        if (compoundTag1.containsKey("Version", 10)) {
            final CompoundTag compoundTag5 = compoundTag1.getCompound("Version");
            this.versionName = compoundTag5.getString("Name");
            this.versionId = compoundTag5.getInt("Id");
            this.versionSnapshot = compoundTag5.getBoolean("Snapshot");
        }
        this.randomSeed = compoundTag1.getLong("RandomSeed");
        if (compoundTag1.containsKey("generatorName", 8)) {
            final String string5 = compoundTag1.getString("generatorName");
            this.generatorType = LevelGeneratorType.getTypeFromName(string5);
            if (this.generatorType == null) {
                this.generatorType = LevelGeneratorType.DEFAULT;
            }
            else if (this.generatorType == LevelGeneratorType.CUSTOMIZED) {
                this.legacyCustomOptions = compoundTag1.getString("generatorOptions");
            }
            else if (this.generatorType.isVersioned()) {
                int integer2 = 0;
                if (compoundTag1.containsKey("generatorVersion", 99)) {
                    integer2 = compoundTag1.getInt("generatorVersion");
                }
                this.generatorType = this.generatorType.getTypeForVersion(integer2);
            }
            this.setGeneratorOptions(compoundTag1.getCompound("generatorOptions"));
        }
        this.gameMode = GameMode.byId(compoundTag1.getInt("GameType"));
        if (compoundTag1.containsKey("legacy_custom_options", 8)) {
            this.legacyCustomOptions = compoundTag1.getString("legacy_custom_options");
        }
        if (compoundTag1.containsKey("MapFeatures", 99)) {
            this.structures = compoundTag1.getBoolean("MapFeatures");
        }
        else {
            this.structures = true;
        }
        this.spawnX = compoundTag1.getInt("SpawnX");
        this.spawnY = compoundTag1.getInt("SpawnY");
        this.spawnZ = compoundTag1.getInt("SpawnZ");
        this.time = compoundTag1.getLong("Time");
        if (compoundTag1.containsKey("DayTime", 99)) {
            this.timeOfDay = compoundTag1.getLong("DayTime");
        }
        else {
            this.timeOfDay = this.time;
        }
        this.lastPlayed = compoundTag1.getLong("LastPlayed");
        this.sizeOnDisk = compoundTag1.getLong("SizeOnDisk");
        this.levelName = compoundTag1.getString("LevelName");
        this.version = compoundTag1.getInt("version");
        this.clearWeatherTime = compoundTag1.getInt("clearWeatherTime");
        this.rainTime = compoundTag1.getInt("rainTime");
        this.raining = compoundTag1.getBoolean("raining");
        this.thunderTime = compoundTag1.getInt("thunderTime");
        this.thundering = compoundTag1.getBoolean("thundering");
        this.hardcore = compoundTag1.getBoolean("hardcore");
        if (compoundTag1.containsKey("initialized", 99)) {
            this.initialized = compoundTag1.getBoolean("initialized");
        }
        else {
            this.initialized = true;
        }
        if (compoundTag1.containsKey("allowCommands", 99)) {
            this.commandsAllowed = compoundTag1.getBoolean("allowCommands");
        }
        else {
            this.commandsAllowed = (this.gameMode == GameMode.c);
        }
        this.playerWorldId = integer;
        if (compoundTag4 != null) {
            this.playerData = compoundTag4;
        }
        if (compoundTag1.containsKey("GameRules", 10)) {
            this.gameRules.deserialize(compoundTag1.getCompound("GameRules"));
        }
        if (compoundTag1.containsKey("Difficulty", 99)) {
            this.difficulty = Difficulty.getDifficulty(compoundTag1.getByte("Difficulty"));
        }
        if (compoundTag1.containsKey("DifficultyLocked", 1)) {
            this.difficultyLocked = compoundTag1.getBoolean("DifficultyLocked");
        }
        if (compoundTag1.containsKey("BorderCenterX", 99)) {
            this.borderCenterX = compoundTag1.getDouble("BorderCenterX");
        }
        if (compoundTag1.containsKey("BorderCenterZ", 99)) {
            this.borderCenterZ = compoundTag1.getDouble("BorderCenterZ");
        }
        if (compoundTag1.containsKey("BorderSize", 99)) {
            this.borderSize = compoundTag1.getDouble("BorderSize");
        }
        if (compoundTag1.containsKey("BorderSizeLerpTime", 99)) {
            this.borderSizeLerpTime = compoundTag1.getLong("BorderSizeLerpTime");
        }
        if (compoundTag1.containsKey("BorderSizeLerpTarget", 99)) {
            this.borderSizeLerpTarget = compoundTag1.getDouble("BorderSizeLerpTarget");
        }
        if (compoundTag1.containsKey("BorderSafeZone", 99)) {
            this.borderSafeZone = compoundTag1.getDouble("BorderSafeZone");
        }
        if (compoundTag1.containsKey("BorderDamagePerBlock", 99)) {
            this.borderDamagePerBlock = compoundTag1.getDouble("BorderDamagePerBlock");
        }
        if (compoundTag1.containsKey("BorderWarningBlocks", 99)) {
            this.borderWarningBlocks = compoundTag1.getInt("BorderWarningBlocks");
        }
        if (compoundTag1.containsKey("BorderWarningTime", 99)) {
            this.borderWarningTime = compoundTag1.getInt("BorderWarningTime");
        }
        if (compoundTag1.containsKey("DimensionData", 10)) {
            final CompoundTag compoundTag5 = compoundTag1.getCompound("DimensionData");
            for (final String string6 : compoundTag5.getKeys()) {
                this.worldData.put(DimensionType.byRawId(Integer.parseInt(string6)), compoundTag5.getCompound(string6));
            }
        }
        if (compoundTag1.containsKey("DataPacks", 10)) {
            final CompoundTag compoundTag5 = compoundTag1.getCompound("DataPacks");
            final ListTag listTag6 = compoundTag5.getList("Disabled", 8);
            for (int integer3 = 0; integer3 < listTag6.size(); ++integer3) {
                this.disabledDataPacks.add(listTag6.getString(integer3));
            }
            final ListTag listTag7 = compoundTag5.getList("Enabled", 8);
            for (int integer4 = 0; integer4 < listTag7.size(); ++integer4) {
                this.enabledDataPacks.add(listTag7.getString(integer4));
            }
        }
        if (compoundTag1.containsKey("CustomBossEvents", 10)) {
            this.customBossEvents = compoundTag1.getCompound("CustomBossEvents");
        }
        if (compoundTag1.containsKey("ScheduledEvents", 9)) {
            this.scheduledEvents.fromTag(compoundTag1.getList("ScheduledEvents", 10));
        }
        if (compoundTag1.containsKey("WanderingTraderSpawnDelay", 99)) {
            this.wanderingTraderSpawnDelay = compoundTag1.getInt("WanderingTraderSpawnDelay");
        }
        if (compoundTag1.containsKey("WanderingTraderSpawnChance", 99)) {
            this.wanderingTraderSpawnChance = compoundTag1.getInt("WanderingTraderSpawnChance");
        }
        if (compoundTag1.containsKey("WanderingTraderId", 8)) {
            this.wanderingTraderId = UUID.fromString(compoundTag1.getString("WanderingTraderId"));
        }
    }
    
    public LevelProperties(final LevelInfo levelInfo, final String levelName) {
        this.generatorType = LevelGeneratorType.DEFAULT;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = Sets.newHashSet();
        this.enabledDataPacks = Sets.newLinkedHashSet();
        this.worldData = Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE);
        this.dataFixer = null;
        this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
        this.loadLevelInfo(levelInfo);
        this.levelName = levelName;
        this.difficulty = LevelProperties.DEFAULT_DIFFICULTY;
        this.initialized = false;
    }
    
    public void loadLevelInfo(final LevelInfo levelInfo) {
        this.randomSeed = levelInfo.getSeed();
        this.gameMode = levelInfo.getGameMode();
        this.structures = levelInfo.hasStructures();
        this.hardcore = levelInfo.isHardcore();
        this.generatorType = levelInfo.getGeneratorType();
        this.setGeneratorOptions((CompoundTag)Dynamic.convert((DynamicOps)JsonOps.INSTANCE, (DynamicOps)NbtOps.INSTANCE, levelInfo.getGeneratorOptions()));
        this.commandsAllowed = levelInfo.allowCommands();
    }
    
    public CompoundTag cloneWorldTag(@Nullable CompoundTag playerTag) {
        this.loadPlayerData();
        if (playerTag == null) {
            playerTag = this.playerData;
        }
        final CompoundTag compoundTag2 = new CompoundTag();
        this.updateProperties(compoundTag2, playerTag);
        return compoundTag2;
    }
    
    private void updateProperties(final CompoundTag levelTag, final CompoundTag playerTag) {
        final CompoundTag compoundTag3 = new CompoundTag();
        compoundTag3.putString("Name", SharedConstants.getGameVersion().getName());
        compoundTag3.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag3.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        levelTag.put("Version", compoundTag3);
        levelTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        levelTag.putLong("RandomSeed", this.randomSeed);
        levelTag.putString("generatorName", this.generatorType.getStoredName());
        levelTag.putInt("generatorVersion", this.generatorType.getVersion());
        if (!this.generatorOptions.isEmpty()) {
            levelTag.put("generatorOptions", this.generatorOptions);
        }
        if (this.legacyCustomOptions != null) {
            levelTag.putString("legacy_custom_options", this.legacyCustomOptions);
        }
        levelTag.putInt("GameType", this.gameMode.getId());
        levelTag.putBoolean("MapFeatures", this.structures);
        levelTag.putInt("SpawnX", this.spawnX);
        levelTag.putInt("SpawnY", this.spawnY);
        levelTag.putInt("SpawnZ", this.spawnZ);
        levelTag.putLong("Time", this.time);
        levelTag.putLong("DayTime", this.timeOfDay);
        levelTag.putLong("SizeOnDisk", this.sizeOnDisk);
        levelTag.putLong("LastPlayed", SystemUtil.getEpochTimeMs());
        levelTag.putString("LevelName", this.levelName);
        levelTag.putInt("version", this.version);
        levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
        levelTag.putInt("rainTime", this.rainTime);
        levelTag.putBoolean("raining", this.raining);
        levelTag.putInt("thunderTime", this.thunderTime);
        levelTag.putBoolean("thundering", this.thundering);
        levelTag.putBoolean("hardcore", this.hardcore);
        levelTag.putBoolean("allowCommands", this.commandsAllowed);
        levelTag.putBoolean("initialized", this.initialized);
        levelTag.putDouble("BorderCenterX", this.borderCenterX);
        levelTag.putDouble("BorderCenterZ", this.borderCenterZ);
        levelTag.putDouble("BorderSize", this.borderSize);
        levelTag.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
        levelTag.putDouble("BorderSafeZone", this.borderSafeZone);
        levelTag.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
        levelTag.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
        levelTag.putDouble("BorderWarningBlocks", this.borderWarningBlocks);
        levelTag.putDouble("BorderWarningTime", this.borderWarningTime);
        if (this.difficulty != null) {
            levelTag.putByte("Difficulty", (byte)this.difficulty.getId());
        }
        levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
        levelTag.put("GameRules", this.gameRules.serialize());
        final CompoundTag compoundTag4 = new CompoundTag();
        for (final Map.Entry<DimensionType, CompoundTag> entry6 : this.worldData.entrySet()) {
            compoundTag4.put(String.valueOf(entry6.getKey().getRawId()), entry6.getValue());
        }
        levelTag.put("DimensionData", compoundTag4);
        if (playerTag != null) {
            levelTag.put("Player", playerTag);
        }
        final CompoundTag compoundTag5 = new CompoundTag();
        final ListTag listTag6 = new ListTag();
        for (final String string8 : this.enabledDataPacks) {
            ((AbstractList<StringTag>)listTag6).add(new StringTag(string8));
        }
        compoundTag5.put("Enabled", listTag6);
        final ListTag listTag7 = new ListTag();
        for (final String string9 : this.disabledDataPacks) {
            ((AbstractList<StringTag>)listTag7).add(new StringTag(string9));
        }
        compoundTag5.put("Disabled", listTag7);
        levelTag.put("DataPacks", compoundTag5);
        if (this.customBossEvents != null) {
            levelTag.put("CustomBossEvents", this.customBossEvents);
        }
        levelTag.put("ScheduledEvents", this.scheduledEvents.toTag());
        levelTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        levelTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            levelTag.putString("WanderingTraderId", this.wanderingTraderId.toString());
        }
    }
    
    public long getSeed() {
        return this.randomSeed;
    }
    
    public int getSpawnX() {
        return this.spawnX;
    }
    
    public int getSpawnY() {
        return this.spawnY;
    }
    
    public int getSpawnZ() {
        return this.spawnZ;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public long getTimeOfDay() {
        return this.timeOfDay;
    }
    
    private void loadPlayerData() {
        if (this.playerDataLoaded || this.playerData == null) {
            return;
        }
        if (this.playerWorldId < SharedConstants.getGameVersion().getWorldVersion()) {
            if (this.dataFixer == null) {
                throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }
            this.playerData = TagHelper.update(this.dataFixer, DataFixTypes.b, this.playerData, this.playerWorldId);
        }
        this.dimension = this.playerData.getInt("Dimension");
        this.playerDataLoaded = true;
    }
    
    public CompoundTag getPlayerData() {
        this.loadPlayerData();
        return this.playerData;
    }
    
    @Environment(EnvType.CLIENT)
    public int getDimension() {
        this.loadPlayerData();
        return this.dimension;
    }
    
    @Environment(EnvType.CLIENT)
    public void setSpawnX(final int spawnX) {
        this.spawnX = spawnX;
    }
    
    @Environment(EnvType.CLIENT)
    public void setSpawnY(final int spawnY) {
        this.spawnY = spawnY;
    }
    
    @Environment(EnvType.CLIENT)
    public void setSpawnZ(final int spawnZ) {
        this.spawnZ = spawnZ;
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public void setTimeOfDay(final long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
    
    public void setSpawnPos(final BlockPos blockPos) {
        this.spawnX = blockPos.getX();
        this.spawnY = blockPos.getY();
        this.spawnZ = blockPos.getZ();
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public void setLevelName(final String levelName) {
        this.levelName = levelName;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    @Environment(EnvType.CLIENT)
    public long getLastPlayed() {
        return this.lastPlayed;
    }
    
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }
    
    public void setClearWeatherTime(final int clearWeatherTime) {
        this.clearWeatherTime = clearWeatherTime;
    }
    
    public boolean isThundering() {
        return this.thundering;
    }
    
    public void setThundering(final boolean thundering) {
        this.thundering = thundering;
    }
    
    public int getThunderTime() {
        return this.thunderTime;
    }
    
    public void setThunderTime(final int thunderTime) {
        this.thunderTime = thunderTime;
    }
    
    public boolean isRaining() {
        return this.raining;
    }
    
    public void setRaining(final boolean raining) {
        this.raining = raining;
    }
    
    public int getRainTime() {
        return this.rainTime;
    }
    
    public void setRainTime(final int rainTime) {
        this.rainTime = rainTime;
    }
    
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    public boolean hasStructures() {
        return this.structures;
    }
    
    public void setStructures(final boolean structures) {
        this.structures = structures;
    }
    
    public void setGameMode(final GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public void setHardcore(final boolean hardcore) {
        this.hardcore = hardcore;
    }
    
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
    
    public void setGeneratorType(final LevelGeneratorType levelGeneratorType) {
        this.generatorType = levelGeneratorType;
    }
    
    public CompoundTag getGeneratorOptions() {
        return this.generatorOptions;
    }
    
    public void setGeneratorOptions(final CompoundTag compoundTag) {
        this.generatorOptions = compoundTag;
    }
    
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }
    
    public void setCommandsAllowed(final boolean commandsAllowed) {
        this.commandsAllowed = commandsAllowed;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }
    
    public GameRules getGameRules() {
        return this.gameRules;
    }
    
    public double getBorderCenterX() {
        return this.borderCenterX;
    }
    
    public double getBorderCenterZ() {
        return this.borderCenterZ;
    }
    
    public double getBorderSize() {
        return this.borderSize;
    }
    
    public void setBorderSize(final double borderSize) {
        this.borderSize = borderSize;
    }
    
    public long getBorderSizeLerpTime() {
        return this.borderSizeLerpTime;
    }
    
    public void setBorderSizeLerpTime(final long borderSizeLerpTime) {
        this.borderSizeLerpTime = borderSizeLerpTime;
    }
    
    public double getBorderSizeLerpTarget() {
        return this.borderSizeLerpTarget;
    }
    
    public void setBorderSizeLerpTarget(final double borderSizeLerpTarget) {
        this.borderSizeLerpTarget = borderSizeLerpTarget;
    }
    
    public void borderCenterZ(final double borderCenterZ) {
        this.borderCenterZ = borderCenterZ;
    }
    
    public void setBorderCenterX(final double borderCenterX) {
        this.borderCenterX = borderCenterX;
    }
    
    public double getBorderSafeZone() {
        return this.borderSafeZone;
    }
    
    public void setBorderSafeZone(final double borderSafeZone) {
        this.borderSafeZone = borderSafeZone;
    }
    
    public double getBorderDamagePerBlock() {
        return this.borderDamagePerBlock;
    }
    
    public void setBorderDamagePerBlock(final double borderDamagePerBlock) {
        this.borderDamagePerBlock = borderDamagePerBlock;
    }
    
    public int getBorderWarningBlocks() {
        return this.borderWarningBlocks;
    }
    
    public int getBorderWarningTime() {
        return this.borderWarningTime;
    }
    
    public void setBorderWarningBlocks(final int borderWarningBlocks) {
        this.borderWarningBlocks = borderWarningBlocks;
    }
    
    public void setBorderWarningTime(final int borderWarningTime) {
        this.borderWarningTime = borderWarningTime;
    }
    
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
    
    public void setDifficultyLocked(final boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }
    
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }
    
    public void populateCrashReport(final CrashReportSection crashReportSection) {
        crashReportSection.add("Level seed", () -> String.valueOf(this.getSeed()));
        crashReportSection.add("Level generator", () -> String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generatorType.getId(), this.generatorType.getName(), this.generatorType.getVersion(), this.structures));
        crashReportSection.add("Level generator options", () -> this.generatorOptions.toString());
        crashReportSection.add("Level spawn location", () -> CrashReportSection.createPositionString(this.spawnX, this.spawnY, this.spawnZ));
        crashReportSection.add("Level time", () -> String.format("%d game time, %d day time", this.time, this.timeOfDay));
        crashReportSection.add("Level dimension", () -> String.valueOf(this.dimension));
        String string1;
        crashReportSection.add("Level storage version", () -> {
            string1 = "Unknown?";
            try {
                switch (this.version) {
                    case 19133: {
                        string1 = "Anvil";
                        break;
                    }
                    case 19132: {
                        string1 = "McRegion";
                        break;
                    }
                }
            }
            catch (Throwable t) {}
            return String.format("0x%05X - %s", this.version, string1);
        });
        crashReportSection.add("Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering));
        crashReportSection.add("Level game mode", () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameMode.getName(), this.gameMode.getId(), this.hardcore, this.commandsAllowed));
    }
    
    public CompoundTag getWorldData(final DimensionType dimensionType) {
        final CompoundTag compoundTag2 = this.worldData.get(dimensionType);
        if (compoundTag2 == null) {
            return new CompoundTag();
        }
        return compoundTag2;
    }
    
    public void setWorldData(final DimensionType type, final CompoundTag compoundTag) {
        this.worldData.put(type, compoundTag);
    }
    
    @Environment(EnvType.CLIENT)
    public int getVersionId() {
        return this.versionId;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isVersionSnapshot() {
        return this.versionSnapshot;
    }
    
    @Environment(EnvType.CLIENT)
    public String getVersionName() {
        return this.versionName;
    }
    
    public Set<String> getDisabledDataPacks() {
        return this.disabledDataPacks;
    }
    
    public Set<String> getEnabledDataPacks() {
        return this.enabledDataPacks;
    }
    
    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }
    
    public void setCustomBossEvents(@Nullable final CompoundTag compoundTag) {
        this.customBossEvents = compoundTag;
    }
    
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }
    
    public void setWanderingTraderSpawnDelay(final int wanderingTraderSpawnDelay) {
        this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
    }
    
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }
    
    public void setWanderingTraderSpawnChance(final int integer) {
        this.wanderingTraderSpawnChance = integer;
    }
    
    public void setWanderingTraderId(final UUID wanderingTraderId) {
        this.wanderingTraderId = wanderingTraderId;
    }
    
    static {
        DEFAULT_DIFFICULTY = Difficulty.NORMAL;
    }
}
