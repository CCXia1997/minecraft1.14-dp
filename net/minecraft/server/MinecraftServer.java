package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.GameRules;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.resource.ResourcePack;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import java.util.function.Consumer;
import java.nio.file.Path;
import joptsimple.OptionSet;
import net.minecraft.util.UncaughtExceptionLogger;
import java.awt.GraphicsEnvironment;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.datafixers.Schemas;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.Bootstrap;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import java.nio.file.Paths;
import joptsimple.OptionSpec;
import joptsimple.OptionParser;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.util.registry.Registry;
import java.util.Collections;
import java.util.Arrays;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.authlib.GameProfile;
import java.util.function.BooleanSupplier;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import io.netty.buffer.ByteBuf;
import java.util.Base64;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import io.netty.buffer.ByteBufOutputStream;
import org.apache.commons.lang3.Validate;
import javax.imageio.ImageIO;
import io.netty.buffer.Unpooled;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.SharedConstants;
import net.minecraft.text.StringTextComponent;
import net.minecraft.world.SessionLockException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.util.Void;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Collection;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.DefaultResourcePackCreator;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import java.util.Iterator;
import net.minecraft.server.world.SecondaryServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldSaveHandler;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.updater.WorldUpdater;
import net.minecraft.util.ProgressListener;
import net.minecraft.text.TranslatableTextComponent;
import java.io.IOException;
import net.minecraft.world.PersistentState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceType;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.util.SystemUtil;
import java.util.concurrent.Executor;
import net.minecraft.util.MetricsData;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.world.loot.LootManager;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.tag.TagManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.UserCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.security.KeyPair;
import javax.annotation.Nullable;
import java.net.Proxy;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import java.util.Map;
import com.mojang.datafixers.DataFixer;
import java.util.Random;
import net.minecraft.util.profiler.DisableableProfiler;
import java.util.List;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.LevelInfo;
import java.io.File;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.NonBlockingThreadExecutor;

public abstract class MinecraftServer extends NonBlockingThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable, Runnable
{
    private static final Logger LOGGER;
    public static final File USER_CACHE_FILE;
    public static final LevelInfo WORLD_INFO;
    private final LevelStorage levelStorage;
    private final Snooper snooper;
    private final File gameDir;
    private final List<Runnable> serverGuiTickables;
    private final DisableableProfiler profiler;
    private final ServerNetworkIo networkIo;
    protected final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    private final ServerMetadata metadata;
    private final Random random;
    private final DataFixer dataFixer;
    private String serverIp;
    private int serverPort;
    private final Map<DimensionType, ServerWorld> worlds;
    private PlayerManager playerManager;
    private volatile boolean running;
    private boolean stopped;
    private int ticks;
    protected final Proxy e;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean spawnAnimals;
    private boolean spawnNpcs;
    private boolean pvpEnabled;
    private boolean flightEnabled;
    @Nullable
    private String motd;
    private int worldHeight;
    private int playerIdleTimeout;
    public final long[] lastTickLengths;
    protected final Map<DimensionType, long[]> g;
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private String userName;
    private final String levelName;
    @Nullable
    @Environment(EnvType.CLIENT)
    private String displayName;
    private boolean demo;
    private boolean bonusChest;
    private String resourcePackUrl;
    private String resourcePackHash;
    private volatile boolean loading;
    private long R;
    @Nullable
    private TextComponent loadingStage;
    private boolean profilerStartQueued;
    private boolean forceGameMode;
    @Nullable
    private final YggdrasilAuthenticationService authService;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository gameProfileRepo;
    private final UserCache userCache;
    private long lastPlayerSampleUpdate;
    protected final Thread serverThread;
    private long timeReference;
    private long ab;
    private boolean ac;
    @Environment(EnvType.CLIENT)
    private boolean iconFilePresent;
    private final ReloadableResourceManager dataManager;
    private final ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager;
    @Nullable
    private FileResourcePackCreator dataPackCreator;
    private final CommandManager commandManager;
    private final RecipeManager recipeManager;
    private final TagManager tagManager;
    private final ServerScoreboard scoreboard;
    private final BossBarManager bossBarManager;
    private final LootManager lootManager;
    private final ServerAdvancementLoader advancementManager;
    private final CommandFunctionManager commandFunctionManager;
    private final MetricsData metricsData;
    private boolean whitelistEnabled;
    private boolean forceWorldUpgrade;
    private boolean eraseCache;
    private float tickTime;
    private final Executor workerExecutor;
    @Nullable
    private String serverId;
    
    public MinecraftServer(final File file, final Proxy proxy, final DataFixer dataFixer, final CommandManager commandManager, final YggdrasilAuthenticationService yggdrasilAuthenticationService, final MinecraftSessionService minecraftSessionService, final GameProfileRepository gameProfileRepository, final UserCache userCache, final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, final String string) {
        super("Server");
        this.snooper = new Snooper("server", this, SystemUtil.getMeasuringTimeMs());
        this.serverGuiTickables = Lists.newArrayList();
        this.profiler = new DisableableProfiler(this::getTicks);
        this.metadata = new ServerMetadata();
        this.random = new Random();
        this.serverPort = -1;
        this.worlds = Maps.newIdentityHashMap();
        this.running = true;
        this.lastTickLengths = new long[100];
        this.g = Maps.newIdentityHashMap();
        this.resourcePackUrl = "";
        this.resourcePackHash = "";
        this.serverThread = SystemUtil.<Thread>consume(new Thread(this, "Server thread"), thread -> thread.setUncaughtExceptionHandler((thread, throwable) -> MinecraftServer.LOGGER.error(throwable)));
        this.timeReference = SystemUtil.getMeasuringTimeMs();
        this.dataManager = new ReloadableResourceManagerImpl(ResourceType.DATA, this.serverThread);
        this.resourcePackContainerManager = new ResourcePackContainerManager<ResourcePackContainer>(ResourcePackContainer::new);
        this.recipeManager = new RecipeManager();
        this.tagManager = new TagManager();
        this.scoreboard = new ServerScoreboard(this);
        this.bossBarManager = new BossBarManager(this);
        this.lootManager = new LootManager();
        this.advancementManager = new ServerAdvancementLoader();
        this.commandFunctionManager = new CommandFunctionManager(this);
        this.metricsData = new MetricsData();
        this.e = proxy;
        this.commandManager = commandManager;
        this.authService = yggdrasilAuthenticationService;
        this.sessionService = minecraftSessionService;
        this.gameProfileRepo = gameProfileRepository;
        this.userCache = userCache;
        this.gameDir = file;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.levelStorage = new LevelStorage(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
        this.dataFixer = dataFixer;
        this.dataManager.registerListener(this.tagManager);
        this.dataManager.registerListener(this.recipeManager);
        this.dataManager.registerListener(this.lootManager);
        this.dataManager.registerListener(this.commandFunctionManager);
        this.dataManager.registerListener(this.advancementManager);
        this.workerExecutor = SystemUtil.getServerWorkerExecutor();
        this.levelName = string;
    }
    
    private void initScoreboard(final PersistentStateManager persistentStateManager) {
        final ScoreboardState scoreboardState2 = persistentStateManager.<ScoreboardState>getOrCreate(ScoreboardState::new, "scoreboard");
        scoreboardState2.setScoreboard(this.getScoreboard());
        this.getScoreboard().addUpdateListener(new ScoreboardSynchronizer(scoreboardState2));
    }
    
    protected abstract boolean setupServer() throws IOException;
    
    protected void upgradeWorld(final String string) {
        if (this.getLevelStorage().requiresConversion(string)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.setLoadingStage(new TranslatableTextComponent("menu.convertingLevel", new Object[0]));
            this.getLevelStorage().convertLevel(string, new ProgressListener() {
                private long lastProgressUpdate = SystemUtil.getMeasuringTimeMs();
                
                @Override
                public void a(final TextComponent textComponent) {
                }
                
                @Environment(EnvType.CLIENT)
                @Override
                public void b(final TextComponent textComponent) {
                }
                
                @Override
                public void progressStagePercentage(final int integer) {
                    if (SystemUtil.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
                        this.lastProgressUpdate = SystemUtil.getMeasuringTimeMs();
                        MinecraftServer.LOGGER.info("Converting... {}%", integer);
                    }
                }
                
                @Environment(EnvType.CLIENT)
                @Override
                public void setDone() {
                }
                
                @Override
                public void c(final TextComponent textComponent) {
                }
            });
        }
        if (this.forceWorldUpgrade) {
            MinecraftServer.LOGGER.info("Forcing world upgrade!");
            final LevelProperties levelProperties2 = this.getLevelStorage().getLevelProperties(this.getLevelName());
            if (levelProperties2 != null) {
                final WorldUpdater worldUpdater3 = new WorldUpdater(this.getLevelName(), this.getLevelStorage(), levelProperties2, this.eraseCache);
                TextComponent textComponent4 = null;
                while (!worldUpdater3.isDone()) {
                    final TextComponent textComponent5 = worldUpdater3.getStatus();
                    if (textComponent4 != textComponent5) {
                        textComponent4 = textComponent5;
                        MinecraftServer.LOGGER.info(worldUpdater3.getStatus().getString());
                    }
                    final int integer6 = worldUpdater3.getTotalChunkCount();
                    if (integer6 > 0) {
                        final int integer7 = worldUpdater3.getUpgradedChunkCount() + worldUpdater3.getSkippedChunkCount();
                        MinecraftServer.LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor(integer7 / (float)integer6 * 100.0f), integer7, integer6);
                    }
                    if (this.isStopped()) {
                        worldUpdater3.cancel();
                    }
                    else {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex) {}
                    }
                }
            }
        }
    }
    
    protected synchronized void setLoadingStage(final TextComponent loadingStage) {
        this.loadingStage = loadingStage;
    }
    
    protected void loadWorld(final String name, final String serverName, final long seed, final LevelGeneratorType generatorType, final JsonElement generatorSettings) {
        this.upgradeWorld(name);
        this.setLoadingStage(new TranslatableTextComponent("menu.loadingLevel", new Object[0]));
        final WorldSaveHandler worldSaveHandler7 = this.getLevelStorage().createSaveHandler(name, this);
        this.loadWorldResourcePack(this.getLevelName(), worldSaveHandler7);
        LevelProperties levelProperties9 = worldSaveHandler7.readProperties();
        LevelInfo levelInfo8;
        if (levelProperties9 == null) {
            if (this.isDemo()) {
                levelInfo8 = MinecraftServer.WORLD_INFO;
            }
            else {
                levelInfo8 = new LevelInfo(seed, this.getDefaultGameMode(), this.shouldGenerateStructures(), this.isHardcore(), generatorType);
                levelInfo8.setGeneratorOptions(generatorSettings);
                if (this.bonusChest) {
                    levelInfo8.setBonusChest();
                }
            }
            levelProperties9 = new LevelProperties(levelInfo8, serverName);
        }
        else {
            levelProperties9.setLevelName(serverName);
            levelInfo8 = new LevelInfo(levelProperties9);
        }
        this.loadWorldDataPacks(worldSaveHandler7.getWorldDir(), levelProperties9);
        final WorldGenerationProgressListener worldGenerationProgressListener10 = this.worldGenerationProgressListenerFactory.create(11);
        this.createWorlds(worldSaveHandler7, levelProperties9, levelInfo8, worldGenerationProgressListener10);
        this.setDifficulty(this.getDefaultDifficulty(), true);
        this.prepareStartRegion(worldGenerationProgressListener10);
    }
    
    protected void createWorlds(final WorldSaveHandler worldSaveHandler, final LevelProperties properties, final LevelInfo levelInfo, final WorldGenerationProgressListener worldGenerationProgressListener) {
        if (this.isDemo()) {
            properties.loadLevelInfo(MinecraftServer.WORLD_INFO);
        }
        final ServerWorld serverWorld5 = new ServerWorld(this, this.workerExecutor, worldSaveHandler, properties, DimensionType.a, this.profiler, worldGenerationProgressListener);
        this.worlds.put(DimensionType.a, serverWorld5);
        this.initScoreboard(serverWorld5.getPersistentStateManager());
        serverWorld5.getWorldBorder().load(properties);
        final ServerWorld serverWorld6 = this.getWorld(DimensionType.a);
        if (!properties.isInitialized()) {
            try {
                serverWorld6.init(levelInfo);
                if (properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
                    this.setToDebugWorldProperties(properties);
                }
                properties.setInitialized(true);
            }
            catch (Throwable throwable7) {
                final CrashReport crashReport8 = CrashReport.create(throwable7, "Exception initializing level");
                try {
                    serverWorld6.addDetailsToCrashReport(crashReport8);
                }
                catch (Throwable t) {}
                throw new CrashException(crashReport8);
            }
            properties.setInitialized(true);
        }
        this.getPlayerManager().setMainWorld(serverWorld6);
        if (properties.getCustomBossEvents() != null) {
            this.getBossBarManager().fromTag(properties.getCustomBossEvents());
        }
        for (final DimensionType dimensionType8 : DimensionType.getAll()) {
            if (dimensionType8 == DimensionType.a) {
                continue;
            }
            this.worlds.put(dimensionType8, new SecondaryServerWorld(serverWorld6, this, this.workerExecutor, worldSaveHandler, dimensionType8, this.profiler, worldGenerationProgressListener));
        }
    }
    
    private void setToDebugWorldProperties(final LevelProperties properties) {
        properties.setStructures(false);
        properties.setCommandsAllowed(true);
        properties.setRaining(false);
        properties.setThundering(false);
        properties.setClearWeatherTime(1000000000);
        properties.setTimeOfDay(6000L);
        properties.setGameMode(GameMode.e);
        properties.setHardcore(false);
        properties.setDifficulty(Difficulty.PEACEFUL);
        properties.setDifficultyLocked(true);
        properties.getGameRules().put("doDaylightCycle", "false", this);
    }
    
    protected void loadWorldDataPacks(final File worldDir, final LevelProperties levelProperties) {
        this.resourcePackContainerManager.addCreator(new DefaultResourcePackCreator());
        this.dataPackCreator = new FileResourcePackCreator(new File(worldDir, "datapacks"));
        this.resourcePackContainerManager.addCreator(this.dataPackCreator);
        this.resourcePackContainerManager.callCreators();
        final List<ResourcePackContainer> list3 = Lists.newArrayList();
        for (final String string5 : levelProperties.getEnabledDataPacks()) {
            final ResourcePackContainer resourcePackContainer6 = this.resourcePackContainerManager.getContainer(string5);
            if (resourcePackContainer6 != null) {
                list3.add(resourcePackContainer6);
            }
            else {
                MinecraftServer.LOGGER.warn("Missing data pack {}", string5);
            }
        }
        this.resourcePackContainerManager.setEnabled(list3);
        this.reloadDataPacks(levelProperties);
    }
    
    protected void prepareStartRegion(final WorldGenerationProgressListener worldGenerationProgressListener) {
        this.setLoadingStage(new TranslatableTextComponent("menu.generatingTerrain", new Object[0]));
        final ServerWorld serverWorld2 = this.getWorld(DimensionType.a);
        MinecraftServer.LOGGER.info("Preparing start region for dimension " + DimensionType.getId(serverWorld2.dimension.getType()));
        final BlockPos blockPos3 = serverWorld2.getSpawnPos();
        worldGenerationProgressListener.start(new ChunkPos(blockPos3));
        final ServerChunkManager serverChunkManager4 = serverWorld2.getChunkManager();
        serverChunkManager4.getLightingProvider().setTaskBatchSize(500);
        this.timeReference = SystemUtil.getMeasuringTimeMs();
        serverChunkManager4.<Void>addTicket(ChunkTicketType.START, new ChunkPos(blockPos3), 11, Void.INSTANCE);
        while (serverChunkManager4.getTotalChunksLoadedCount() != 441) {
            this.timeReference += 100L;
            this.o();
        }
        this.timeReference += 100L;
        this.o();
        for (final DimensionType dimensionType6 : DimensionType.getAll()) {
            final ForcedChunkState forcedChunkState7 = this.getWorld(dimensionType6).getPersistentStateManager().<ForcedChunkState>get(ForcedChunkState::new, "chunks");
            if (forcedChunkState7 != null) {
                final ServerWorld serverWorld3 = this.getWorld(dimensionType6);
                final LongIterator longIterator9 = forcedChunkState7.getChunks().iterator();
                while (longIterator9.hasNext()) {
                    final long long10 = longIterator9.nextLong();
                    final ChunkPos chunkPos12 = new ChunkPos(long10);
                    serverWorld3.getChunkManager().setChunkForced(chunkPos12, true);
                }
            }
        }
        this.timeReference += 100L;
        this.o();
        worldGenerationProgressListener.stop();
        serverChunkManager4.getLightingProvider().setTaskBatchSize(5);
    }
    
    protected void loadWorldResourcePack(final String worldName, final WorldSaveHandler worldSaveHandler) {
        final File file3 = new File(worldSaveHandler.getWorldDir(), "resources.zip");
        if (file3.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(worldName, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException4) {
                MinecraftServer.LOGGER.warn("Something went wrong url encoding {}", worldName);
            }
        }
    }
    
    public abstract boolean shouldGenerateStructures();
    
    public abstract GameMode getDefaultGameMode();
    
    public abstract Difficulty getDefaultDifficulty();
    
    public abstract boolean isHardcore();
    
    public abstract int getOpPermissionLevel();
    
    public abstract boolean shouldBroadcastRconToOps();
    
    public boolean save(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        boolean boolean4 = false;
        for (final ServerWorld serverWorld6 : this.getWorlds()) {
            if (!boolean1) {
                MinecraftServer.LOGGER.info("Saving chunks for level '{}'/{}", serverWorld6.getLevelProperties().getLevelName(), DimensionType.getId(serverWorld6.dimension.getType()));
            }
            try {
                serverWorld6.save(null, boolean2, serverWorld6.savingDisabled && !boolean3);
            }
            catch (SessionLockException sessionLockException7) {
                MinecraftServer.LOGGER.warn(sessionLockException7.getMessage());
            }
            boolean4 = true;
        }
        final ServerWorld serverWorld7 = this.getWorld(DimensionType.a);
        final LevelProperties levelProperties6 = serverWorld7.getLevelProperties();
        serverWorld7.getWorldBorder().save(levelProperties6);
        levelProperties6.setCustomBossEvents(this.getBossBarManager().toTag());
        serverWorld7.getSaveHandler().saveWorld(levelProperties6, this.getPlayerManager().getUserData());
        return boolean4;
    }
    
    @Override
    public void close() {
        this.shutdown();
    }
    
    protected void shutdown() {
        MinecraftServer.LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
        if (this.playerManager != null) {
            MinecraftServer.LOGGER.info("Saving players");
            this.playerManager.saveAllPlayerData();
            this.playerManager.disconnectAllPlayers();
        }
        MinecraftServer.LOGGER.info("Saving worlds");
        for (final ServerWorld serverWorld2 : this.getWorlds()) {
            if (serverWorld2 != null) {
                serverWorld2.savingDisabled = false;
            }
        }
        this.save(false, true, false);
        for (final ServerWorld serverWorld2 : this.getWorlds()) {
            if (serverWorld2 != null) {
                try {
                    serverWorld2.close();
                }
                catch (IOException iOException3) {
                    MinecraftServer.LOGGER.error("Exception closing the level", (Throwable)iOException3);
                }
            }
        }
        if (this.snooper.isActive()) {
            this.snooper.cancel();
        }
    }
    
    public String getServerIp() {
        return this.serverIp;
    }
    
    public void setServerIp(final String string) {
        this.serverIp = string;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void stop(final boolean boolean1) {
        this.running = false;
        if (boolean1) {
            try {
                this.serverThread.join();
            }
            catch (InterruptedException interruptedException2) {
                MinecraftServer.LOGGER.error("Error while shutting down", (Throwable)interruptedException2);
            }
        }
    }
    
    @Override
    public void run() {
        try {
            if (this.setupServer()) {
                this.timeReference = SystemUtil.getMeasuringTimeMs();
                this.metadata.setDescription(new StringTextComponent(this.motd));
                this.metadata.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
                this.setFavicon(this.metadata);
                while (this.running) {
                    final long long1 = SystemUtil.getMeasuringTimeMs() - this.timeReference;
                    if (long1 > 2000L && this.timeReference - this.R >= 15000L) {
                        final long long2 = long1 / 50L;
                        MinecraftServer.LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", long1, long2);
                        this.timeReference += long2 * 50L;
                        this.R = this.timeReference;
                    }
                    this.timeReference += 50L;
                    if (this.profilerStartQueued) {
                        this.profilerStartQueued = false;
                        this.profiler.getController().enable();
                    }
                    this.profiler.startTick();
                    this.profiler.push("tick");
                    this.tick(this::shouldKeepTicking);
                    this.profiler.swap("nextTickWait");
                    this.ac = true;
                    this.ab = Math.max(SystemUtil.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.o();
                    this.profiler.pop();
                    this.profiler.endTick();
                    this.loading = true;
                }
            }
            else {
                this.setCrashReport(null);
            }
        }
        catch (Throwable throwable1) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable1);
            CrashReport crashReport2;
            if (throwable1 instanceof CrashException) {
                crashReport2 = this.populateCrashReport(((CrashException)throwable1).getReport());
            }
            else {
                crashReport2 = this.populateCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }
            final File file3 = new File(new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (crashReport2.writeToFile(file3)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: {}", file3.getAbsolutePath());
            }
            else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.setCrashReport(crashReport2);
            try {
                this.stopped = true;
                this.shutdown();
            }
            catch (Throwable throwable1) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable1);
            }
            finally {
                this.exit();
            }
        }
        finally {
            try {
                this.stopped = true;
                this.shutdown();
            }
            catch (Throwable throwable2) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable2);
                this.exit();
            }
            finally {
                this.exit();
            }
        }
    }
    
    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || SystemUtil.getMeasuringTimeMs() < (this.ac ? this.ab : this.timeReference);
    }
    
    protected void o() {
        this.executeTaskQueue();
        this.waitFor(() -> !this.shouldKeepTicking());
    }
    
    @Override
    protected ServerTask prepareRunnable(final Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }
    
    @Override
    protected boolean canRun(final ServerTask serverTask) {
        return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }
    
    public boolean executeQueuedTask() {
        final boolean boolean1 = this.aW();
        return this.ac = boolean1;
    }
    
    private boolean aW() {
        if (super.executeQueuedTask()) {
            return true;
        }
        if (this.shouldKeepTicking()) {
            for (final ServerWorld serverWorld2 : this.getWorlds()) {
                if (serverWorld2.getChunkManager().executeQueuedTasks()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setFavicon(final ServerMetadata metadata) {
        File file2 = this.getFile("server-icon.png");
        if (!file2.exists()) {
            file2 = this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
        }
        if (file2.isFile()) {
            final ByteBuf byteBuf3 = Unpooled.buffer();
            try {
                final BufferedImage bufferedImage4 = ImageIO.read(file2);
                Validate.validState(bufferedImage4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedImage4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedImage4, "PNG", (OutputStream)new ByteBufOutputStream(byteBuf3));
                final ByteBuffer byteBuffer5 = Base64.getEncoder().encode(byteBuf3.nioBuffer());
                metadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer5));
            }
            catch (Exception exception4) {
                MinecraftServer.LOGGER.error("Couldn't load server icon", (Throwable)exception4);
            }
            finally {
                byteBuf3.release();
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasIconFile() {
        return this.iconFilePresent = (this.iconFilePresent || this.getIconFile().isFile());
    }
    
    @Environment(EnvType.CLIENT)
    public File getIconFile() {
        return this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
    }
    
    public File getRunDirectory() {
        return new File(".");
    }
    
    protected void setCrashReport(final CrashReport crashReport) {
    }
    
    protected void exit() {
    }
    
    protected void tick(final BooleanSupplier booleanSupplier) {
        final long long2 = SystemUtil.getMeasuringTimeNano();
        ++this.ticks;
        this.tickWorlds(booleanSupplier);
        if (long2 - this.lastPlayerSampleUpdate >= 5000000000L) {
            this.lastPlayerSampleUpdate = long2;
            this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
            final GameProfile[] arr4 = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            final int integer5 = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - arr4.length);
            for (int integer6 = 0; integer6 < arr4.length; ++integer6) {
                arr4[integer6] = this.playerManager.getPlayerList().get(integer5 + integer6).getGameProfile();
            }
            Collections.shuffle(Arrays.<GameProfile>asList(arr4));
            this.metadata.getPlayers().setSample(arr4);
        }
        if (this.ticks % 6000 == 0) {
            MinecraftServer.LOGGER.debug("Autosave started");
            this.profiler.push("save");
            this.playerManager.saveAllPlayerData();
            this.save(true, false, false);
            this.profiler.pop();
            MinecraftServer.LOGGER.debug("Autosave finished");
        }
        this.profiler.push("snooper");
        if (!this.snooper.isActive() && this.ticks > 100) {
            this.snooper.a();
        }
        if (this.ticks % 6000 == 0) {
            this.snooper.update();
        }
        this.profiler.pop();
        this.profiler.push("tallying");
        final long[] lastTickLengths = this.lastTickLengths;
        final int n = this.ticks % 100;
        final long n2 = SystemUtil.getMeasuringTimeNano() - long2;
        lastTickLengths[n] = n2;
        final long long3 = n2;
        this.tickTime = this.tickTime * 0.8f + long3 / 1000000.0f * 0.19999999f;
        final long long4 = SystemUtil.getMeasuringTimeNano();
        this.metricsData.pushSample(long4 - long2);
        this.profiler.pop();
    }
    
    protected void tickWorlds(final BooleanSupplier booleanSupplier) {
        this.profiler.push("commandFunctions");
        this.getCommandFunctionManager().tick();
        this.profiler.swap("levels");
        for (final ServerWorld serverWorld3 : this.getWorlds()) {
            final long long4 = SystemUtil.getMeasuringTimeNano();
            if (serverWorld3.dimension.getType() == DimensionType.a || this.isNetherAllowed()) {
                final ServerWorld serverWorld4;
                this.profiler.push(() -> serverWorld4.getLevelProperties().getLevelName() + " " + Registry.DIMENSION.getId(serverWorld4.dimension.getType()));
                if (this.ticks % 20 == 0) {
                    this.profiler.push("timeSync");
                    this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(serverWorld3.getTime(), serverWorld3.getTimeOfDay(), serverWorld3.getGameRules().getBoolean("doDaylightCycle")), serverWorld3.dimension.getType());
                    this.profiler.pop();
                }
                this.profiler.push("tick");
                try {
                    serverWorld3.tick(booleanSupplier);
                }
                catch (Throwable throwable6) {
                    final CrashReport crashReport7 = CrashReport.create(throwable6, "Exception ticking world");
                    serverWorld3.addDetailsToCrashReport(crashReport7);
                    throw new CrashException(crashReport7);
                }
                this.profiler.pop();
                this.profiler.pop();
            }
            this.g.computeIfAbsent(serverWorld3.dimension.getType(), dimensionType -> new long[100])[this.ticks % 100] = SystemUtil.getMeasuringTimeNano() - long4;
        }
        this.profiler.swap("connection");
        this.getNetworkIo().tick();
        this.profiler.swap("players");
        this.playerManager.updatePlayerLatency();
        this.profiler.swap("server gui refresh");
        for (int integer2 = 0; integer2 < this.serverGuiTickables.size(); ++integer2) {
            this.serverGuiTickables.get(integer2).run();
        }
        this.profiler.pop();
    }
    
    public boolean isNetherAllowed() {
        return true;
    }
    
    public void addServerGuiTickable(final Runnable tickable) {
        this.serverGuiTickables.add(tickable);
    }
    
    public static void main(final String[] args) {
        final OptionParser optionParser2 = new OptionParser();
        final OptionSpec<java.lang.Void> optionSpec3 = (OptionSpec<java.lang.Void>)optionParser2.accepts("nogui");
        final OptionSpec<java.lang.Void> optionSpec4 = (OptionSpec<java.lang.Void>)optionParser2.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        final OptionSpec<java.lang.Void> optionSpec5 = (OptionSpec<java.lang.Void>)optionParser2.accepts("demo");
        final OptionSpec<java.lang.Void> optionSpec6 = (OptionSpec<java.lang.Void>)optionParser2.accepts("bonusChest");
        final OptionSpec<java.lang.Void> optionSpec7 = (OptionSpec<java.lang.Void>)optionParser2.accepts("forceUpgrade");
        final OptionSpec<java.lang.Void> optionSpec8 = (OptionSpec<java.lang.Void>)optionParser2.accepts("eraseCache");
        final OptionSpec<java.lang.Void> optionSpec9 = (OptionSpec<java.lang.Void>)optionParser2.accepts("help").forHelp();
        final OptionSpec<String> optionSpec10 = (OptionSpec<String>)optionParser2.accepts("singleplayer").withRequiredArg();
        final OptionSpec<String> optionSpec11 = (OptionSpec<String>)optionParser2.accepts("universe").withRequiredArg().defaultsTo(".", (Object[])new String[0]);
        final OptionSpec<String> optionSpec12 = (OptionSpec<String>)optionParser2.accepts("world").withRequiredArg();
        final OptionSpec<Integer> optionSpec13 = (OptionSpec<Integer>)optionParser2.accepts("port").withRequiredArg().ofType((Class)Integer.class).defaultsTo((-1), (Object[])new Integer[0]);
        final OptionSpec<String> optionSpec14 = (OptionSpec<String>)optionParser2.accepts("serverId").withRequiredArg();
        final OptionSpec<String> optionSpec15 = (OptionSpec<String>)optionParser2.nonOptions();
        try {
            final OptionSet optionSet16 = optionParser2.parse(args);
            if (optionSet16.has((OptionSpec)optionSpec9)) {
                optionParser2.printHelpOn((OutputStream)System.err);
                return;
            }
            final Path path17 = Paths.get("server.properties");
            final ServerPropertiesLoader serverPropertiesLoader18 = new ServerPropertiesLoader(path17);
            serverPropertiesLoader18.store();
            final Path path18 = Paths.get("eula.txt");
            final EulaReader eulaReader20 = new EulaReader(path18);
            if (optionSet16.has((OptionSpec)optionSpec4)) {
                MinecraftServer.LOGGER.info("Initialized '" + path17.toAbsolutePath().toString() + "' and '" + path18.toAbsolutePath().toString() + "'");
                return;
            }
            if (!eulaReader20.isEulaAgreedTo()) {
                MinecraftServer.LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            Bootstrap.initialize();
            Bootstrap.logMissingTranslations();
            final String string21 = (String)optionSet16.valueOf((OptionSpec)optionSpec11);
            final YggdrasilAuthenticationService yggdrasilAuthenticationService22 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService23 = yggdrasilAuthenticationService22.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository24 = yggdrasilAuthenticationService22.createProfileRepository();
            final UserCache userCache25 = new UserCache(gameProfileRepository24, new File(string21, MinecraftServer.USER_CACHE_FILE.getName()));
            final String string22 = Optional.<String>ofNullable(optionSet16.valueOf((OptionSpec)optionSpec12)).orElse(serverPropertiesLoader18.getPropertiesHandler().levelName);
            final MinecraftDedicatedServer minecraftDedicatedServer27 = new MinecraftDedicatedServer(new File(string21), serverPropertiesLoader18, Schemas.getFixer(), yggdrasilAuthenticationService22, minecraftSessionService23, gameProfileRepository24, userCache25, WorldGenerationProgressLogger::new, string22);
            minecraftDedicatedServer27.setUserName((String)optionSet16.valueOf((OptionSpec)optionSpec10));
            minecraftDedicatedServer27.setServerPort((int)optionSet16.valueOf((OptionSpec)optionSpec13));
            minecraftDedicatedServer27.setDemo(optionSet16.has((OptionSpec)optionSpec5));
            minecraftDedicatedServer27.setBonusChest(optionSet16.has((OptionSpec)optionSpec6));
            minecraftDedicatedServer27.setForceWorldUpgrade(optionSet16.has((OptionSpec)optionSpec7));
            minecraftDedicatedServer27.setEraseCache(optionSet16.has((OptionSpec)optionSpec8));
            minecraftDedicatedServer27.setServerId((String)optionSet16.valueOf((OptionSpec)optionSpec14));
            final boolean boolean28 = !optionSet16.has((OptionSpec)optionSpec3) && !optionSet16.valuesOf((OptionSpec)optionSpec15).contains("nogui");
            if (boolean28 && !GraphicsEnvironment.isHeadless()) {
                minecraftDedicatedServer27.createGui();
            }
            minecraftDedicatedServer27.start();
            final Thread thread29 = new Thread("Server Shutdown Thread") {
                @Override
                public void run() {
                    minecraftDedicatedServer27.stop(true);
                }
            };
            thread29.setUncaughtExceptionHandler(new UncaughtExceptionLogger(MinecraftServer.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread29);
        }
        catch (Exception exception16) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception16);
        }
    }
    
    protected void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    protected void setForceWorldUpgrade(final boolean boolean1) {
        this.forceWorldUpgrade = boolean1;
    }
    
    protected void setEraseCache(final boolean eraseCache) {
        this.eraseCache = eraseCache;
    }
    
    public void start() {
        this.serverThread.start();
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isServerThreadAlive() {
        return !this.serverThread.isAlive();
    }
    
    public File getFile(final String string) {
        return new File(this.getRunDirectory(), string);
    }
    
    public void info(final String string) {
        MinecraftServer.LOGGER.info(string);
    }
    
    public void warn(final String string) {
        MinecraftServer.LOGGER.warn(string);
    }
    
    public ServerWorld getWorld(final DimensionType dimensionType) {
        return this.worlds.get(dimensionType);
    }
    
    public Iterable<ServerWorld> getWorlds() {
        return this.worlds.values();
    }
    
    public String getVersion() {
        return SharedConstants.getGameVersion().getName();
    }
    
    public int getCurrentPlayerCount() {
        return this.playerManager.getCurrentPlayerCount();
    }
    
    public int getMaxPlayerCount() {
        return this.playerManager.getMaxPlayerCount();
    }
    
    public String[] getPlayerNames() {
        return this.playerManager.getPlayerNames();
    }
    
    public boolean isDebuggingEnabled() {
        return false;
    }
    
    public void logError(final String string) {
        MinecraftServer.LOGGER.error(string);
    }
    
    public void log(final String string) {
        if (this.isDebuggingEnabled()) {
            MinecraftServer.LOGGER.info(string);
        }
    }
    
    public String getServerModName() {
        return "vanilla";
    }
    
    public CrashReport populateCrashReport(final CrashReport crashReport) {
        if (this.playerManager != null) {
            crashReport.getSystemDetailsSection().add("Player Count", () -> this.playerManager.getCurrentPlayerCount() + " / " + this.playerManager.getMaxPlayerCount() + "; " + this.playerManager.getPlayerList());
        }
        final StringBuilder stringBuilder1;
        final Iterator<ResourcePackContainer> iterator;
        ResourcePackContainer resourcePackContainer3;
        crashReport.getSystemDetailsSection().add("Data Packs", () -> {
            stringBuilder1 = new StringBuilder();
            this.resourcePackContainerManager.getEnabledContainers().iterator();
            while (iterator.hasNext()) {
                resourcePackContainer3 = iterator.next();
                if (stringBuilder1.length() > 0) {
                    stringBuilder1.append(", ");
                }
                stringBuilder1.append(resourcePackContainer3.getName());
                if (!resourcePackContainer3.getCompatibility().isCompatible()) {
                    stringBuilder1.append(" (incompatible)");
                }
            }
            return stringBuilder1.toString();
        });
        if (this.serverId != null) {
            crashReport.getSystemDetailsSection().add("Server Id", () -> this.serverId);
        }
        return crashReport;
    }
    
    public boolean E() {
        return this.gameDir != null;
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        MinecraftServer.LOGGER.info(message.getString());
    }
    
    public KeyPair getKeyPair() {
        return this.keyPair;
    }
    
    public int getServerPort() {
        return this.serverPort;
    }
    
    public void setServerPort(final int integer) {
        this.serverPort = integer;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String string) {
        this.userName = string;
    }
    
    public boolean isSinglePlayer() {
        return this.userName != null;
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    @Environment(EnvType.CLIENT)
    public void setServerName(final String string) {
        this.displayName = string;
    }
    
    @Environment(EnvType.CLIENT)
    public String getServerName() {
        return this.displayName;
    }
    
    public void setKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
    }
    
    public void setDifficulty(final Difficulty difficulty, final boolean boolean2) {
        for (final ServerWorld serverWorld4 : this.getWorlds()) {
            final LevelProperties levelProperties5 = serverWorld4.getLevelProperties();
            if (!boolean2 && levelProperties5.isDifficultyLocked()) {
                continue;
            }
            if (levelProperties5.isHardcore()) {
                levelProperties5.setDifficulty(Difficulty.HARD);
                serverWorld4.setMobSpawnOptions(true, true);
            }
            else if (this.isSinglePlayer()) {
                levelProperties5.setDifficulty(difficulty);
                serverWorld4.setMobSpawnOptions(serverWorld4.getDifficulty() != Difficulty.PEACEFUL, true);
            }
            else {
                levelProperties5.setDifficulty(difficulty);
                serverWorld4.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.spawnAnimals);
            }
        }
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }
    
    public void setDifficultyLocked(final boolean locked) {
        for (final ServerWorld serverWorld3 : this.getWorlds()) {
            final LevelProperties levelProperties4 = serverWorld3.getLevelProperties();
            levelProperties4.setDifficultyLocked(locked);
        }
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }
    
    private void sendDifficulty(final ServerPlayerEntity player) {
        final LevelProperties levelProperties2 = player.getServerWorld().getLevelProperties();
        player.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties2.getDifficulty(), levelProperties2.isDifficultyLocked()));
    }
    
    protected boolean isMonsterSpawningEnabled() {
        return true;
    }
    
    public boolean isDemo() {
        return this.demo;
    }
    
    public void setDemo(final boolean boolean1) {
        this.demo = boolean1;
    }
    
    public void setBonusChest(final boolean boolean1) {
        this.bonusChest = boolean1;
    }
    
    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }
    
    public String getResourcePackUrl() {
        return this.resourcePackUrl;
    }
    
    public String getResourcePackHash() {
        return this.resourcePackHash;
    }
    
    public void setResourcePack(final String url, final String string2) {
        this.resourcePackUrl = url;
        this.resourcePackHash = string2;
    }
    
    @Override
    public void addSnooperInfo(final Snooper snooper) {
        snooper.addInfo("whitelist_enabled", false);
        snooper.addInfo("whitelist_count", 0);
        if (this.playerManager != null) {
            snooper.addInfo("players_current", this.getCurrentPlayerCount());
            snooper.addInfo("players_max", this.getMaxPlayerCount());
            snooper.addInfo("players_seen", this.getWorld(DimensionType.a).getSaveHandler().getSavedPlayerIds().length);
        }
        snooper.addInfo("uses_auth", this.onlineMode);
        snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
        snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
        snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
        int integer2 = 0;
        for (final ServerWorld serverWorld4 : this.getWorlds()) {
            if (serverWorld4 != null) {
                final LevelProperties levelProperties5 = serverWorld4.getLevelProperties();
                snooper.addInfo("world[" + integer2 + "][dimension]", serverWorld4.dimension.getType());
                snooper.addInfo("world[" + integer2 + "][mode]", levelProperties5.getGameMode());
                snooper.addInfo("world[" + integer2 + "][difficulty]", serverWorld4.getDifficulty());
                snooper.addInfo("world[" + integer2 + "][hardcore]", levelProperties5.isHardcore());
                snooper.addInfo("world[" + integer2 + "][generator_name]", levelProperties5.getGeneratorType().getName());
                snooper.addInfo("world[" + integer2 + "][generator_version]", levelProperties5.getGeneratorType().getVersion());
                snooper.addInfo("world[" + integer2 + "][height]", this.worldHeight);
                snooper.addInfo("world[" + integer2 + "][chunks_loaded]", serverWorld4.getChunkManager().getLoadedChunkCount());
                ++integer2;
            }
        }
        snooper.addInfo("worlds", integer2);
    }
    
    public abstract boolean isDedicated();
    
    public boolean isOnlineMode() {
        return this.onlineMode;
    }
    
    public void setOnlineMode(final boolean boolean1) {
        this.onlineMode = boolean1;
    }
    
    public boolean shouldPreventProxyConnections() {
        return this.preventProxyConnections;
    }
    
    public void setPreventProxyConnections(final boolean boolean1) {
        this.preventProxyConnections = boolean1;
    }
    
    public boolean shouldSpawnAnimals() {
        return this.spawnAnimals;
    }
    
    public void setSpawnAnimals(final boolean boolean1) {
        this.spawnAnimals = boolean1;
    }
    
    public boolean shouldSpawnNpcs() {
        return this.spawnNpcs;
    }
    
    public abstract boolean isUsingNativeTransport();
    
    public void setSpawnNpcs(final boolean boolean1) {
        this.spawnNpcs = boolean1;
    }
    
    public boolean isPvpEnabled() {
        return this.pvpEnabled;
    }
    
    public void setPvpEnabled(final boolean boolean1) {
        this.pvpEnabled = boolean1;
    }
    
    public boolean isFlightEnabled() {
        return this.flightEnabled;
    }
    
    public void setFlightEnabled(final boolean boolean1) {
        this.flightEnabled = boolean1;
    }
    
    public abstract boolean areCommandBlocksEnabled();
    
    public String getServerMotd() {
        return this.motd;
    }
    
    public void setMotd(final String string) {
        this.motd = string;
    }
    
    public int getWorldHeight() {
        return this.worldHeight;
    }
    
    public void setWorldHeight(final int integer) {
        this.worldHeight = integer;
    }
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }
    
    public void setPlayerManager(final PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
    
    public abstract boolean isRemote();
    
    public void setDefaultGameMode(final GameMode gameMode) {
        for (final ServerWorld serverWorld3 : this.getWorlds()) {
            serverWorld3.getLevelProperties().setGameMode(gameMode);
        }
    }
    
    @Nullable
    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLoading() {
        return this.loading;
    }
    
    public boolean hasGui() {
        return false;
    }
    
    public abstract boolean openToLan(final GameMode arg1, final boolean arg2, final int arg3);
    
    public int getTicks() {
        return this.ticks;
    }
    
    public void enableProfiler() {
        this.profilerStartQueued = true;
    }
    
    @Environment(EnvType.CLIENT)
    public Snooper getSnooper() {
        return this.snooper;
    }
    
    public int getSpawnProtectionRadius() {
        return 16;
    }
    
    public boolean isSpawnProtected(final World world, final BlockPos blockPos, final PlayerEntity playerEntity) {
        return false;
    }
    
    public void setForceGameMode(final boolean boolean1) {
        this.forceGameMode = boolean1;
    }
    
    public boolean shouldForceGameMode() {
        return this.forceGameMode;
    }
    
    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }
    
    public void setPlayerIdleTimeout(final int integer) {
        this.playerIdleTimeout = integer;
    }
    
    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }
    
    public GameProfileRepository getGameProfileRepo() {
        return this.gameProfileRepo;
    }
    
    public UserCache getUserCache() {
        return this.userCache;
    }
    
    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
    
    public void forcePlayerSampleUpdate() {
        this.lastPlayerSampleUpdate = 0L;
    }
    
    public int getMaxWorldBorderRadius() {
        return 29999984;
    }
    
    public boolean shouldRunAsync() {
        return super.shouldRunAsync() && !this.isStopped();
    }
    
    public Thread getThread() {
        return this.serverThread;
    }
    
    public int getNetworkCompressionThreshold() {
        return 256;
    }
    
    public long getServerStartTime() {
        return this.timeReference;
    }
    
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }
    
    public int getSpawnRadius(@Nullable final ServerWorld serverWorld) {
        if (serverWorld != null) {
            return serverWorld.getGameRules().getInteger("spawnRadius");
        }
        return 10;
    }
    
    public ServerAdvancementLoader getAdvancementManager() {
        return this.advancementManager;
    }
    
    public CommandFunctionManager getCommandFunctionManager() {
        return this.commandFunctionManager;
    }
    
    public void reload() {
        if (!this.isOnThread()) {
            this.execute(this::reload);
            return;
        }
        this.getPlayerManager().saveAllPlayerData();
        this.resourcePackContainerManager.callCreators();
        this.reloadDataPacks(this.getWorld(DimensionType.a).getLevelProperties());
        this.getPlayerManager().onDataPacksReloaded();
    }
    
    private void reloadDataPacks(final LevelProperties levelProperties) {
        final List<ResourcePackContainer> list2 = Lists.newArrayList(this.resourcePackContainerManager.getEnabledContainers());
        for (final ResourcePackContainer resourcePackContainer2 : this.resourcePackContainerManager.getAlphabeticallyOrderedContainers()) {
            if (!levelProperties.getDisabledDataPacks().contains(resourcePackContainer2.getName()) && !list2.contains(resourcePackContainer2)) {
                MinecraftServer.LOGGER.info("Found new data pack {}, loading it automatically", resourcePackContainer2.getName());
                resourcePackContainer2.getSortingDirection().<ResourcePackContainer, ResourcePackContainer>locate(list2, resourcePackContainer2, resourcePackContainer -> resourcePackContainer, false);
            }
        }
        this.resourcePackContainerManager.setEnabled(list2);
        final List<ResourcePack> list3 = Lists.newArrayList();
        this.resourcePackContainerManager.getEnabledContainers().forEach(resourcePackContainer -> list3.add(resourcePackContainer.createResourcePack()));
        final CompletableFuture<Void> completableFuture4 = this.dataManager.beginReload(this.workerExecutor, this, list3, CompletableFuture.<Void>completedFuture(Void.INSTANCE));
        this.waitFor(completableFuture4::isDone);
        levelProperties.getEnabledDataPacks().clear();
        levelProperties.getDisabledDataPacks().clear();
        this.resourcePackContainerManager.getEnabledContainers().forEach(resourcePackContainer -> levelProperties.getEnabledDataPacks().add(resourcePackContainer.getName()));
        this.resourcePackContainerManager.getAlphabeticallyOrderedContainers().forEach(resourcePackContainer -> {
            if (!this.resourcePackContainerManager.getEnabledContainers().contains(resourcePackContainer)) {
                levelProperties.getDisabledDataPacks().add(resourcePackContainer.getName());
            }
        });
    }
    
    public void kickNonWhitelistedPlayers(final ServerCommandSource source) {
        if (!this.isWhitelistEnabled()) {
            return;
        }
        final PlayerManager playerManager2 = source.getMinecraftServer().getPlayerManager();
        final Whitelist whitelist3 = playerManager2.getWhitelist();
        if (!whitelist3.isEnabled()) {
            return;
        }
        final List<ServerPlayerEntity> list4 = Lists.newArrayList(playerManager2.getPlayerList());
        for (final ServerPlayerEntity serverPlayerEntity6 : list4) {
            if (!whitelist3.isAllowed(serverPlayerEntity6.getGameProfile())) {
                serverPlayerEntity6.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted", new Object[0]));
            }
        }
    }
    
    public ReloadableResourceManager getDataManager() {
        return this.dataManager;
    }
    
    public ResourcePackContainerManager<ResourcePackContainer> getResourcePackContainerManager() {
        return this.resourcePackContainerManager;
    }
    
    public CommandManager getCommandManager() {
        return this.commandManager;
    }
    
    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this, (this.getWorld(DimensionType.a) == null) ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.a).getSpawnPos()), Vec2f.ZERO, this.getWorld(DimensionType.a), 4, "Server", new StringTextComponent("Server"), this, null);
    }
    
    @Override
    public boolean sendCommandFeedback() {
        return true;
    }
    
    @Override
    public boolean shouldTrackOutput() {
        return true;
    }
    
    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }
    
    public TagManager getTagManager() {
        return this.tagManager;
    }
    
    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public LootManager getLootManager() {
        return this.lootManager;
    }
    
    public GameRules getGameRules() {
        return this.getWorld(DimensionType.a).getGameRules();
    }
    
    public BossBarManager getBossBarManager() {
        return this.bossBarManager;
    }
    
    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }
    
    public void setWhitelistEnabled(final boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }
    
    @Environment(EnvType.CLIENT)
    public float getTickTime() {
        return this.tickTime;
    }
    
    public int getPermissionLevel(final GameProfile profile) {
        if (!this.getPlayerManager().isOperator(profile)) {
            return 0;
        }
        final OperatorEntry operatorEntry2 = this.getPlayerManager().getOpList().get(profile);
        if (operatorEntry2 != null) {
            return operatorEntry2.getPermissionLevel();
        }
        if (this.isOwner(profile)) {
            return 4;
        }
        if (this.isSinglePlayer()) {
            return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
        }
        return this.getOpPermissionLevel();
    }
    
    @Environment(EnvType.CLIENT)
    public MetricsData getMetricsData() {
        return this.metricsData;
    }
    
    public DisableableProfiler getProfiler() {
        return this.profiler;
    }
    
    public Executor getWorkerExecutor() {
        return this.workerExecutor;
    }
    
    public abstract boolean isOwner(final GameProfile arg1);
    
    static {
        LOGGER = LogManager.getLogger();
        USER_CACHE_FILE = new File("usercache.json");
        WORLD_INFO = new LevelInfo("North Carolina".hashCode(), GameMode.b, true, false, LevelGeneratorType.DEFAULT).setBonusChest();
    }
}
