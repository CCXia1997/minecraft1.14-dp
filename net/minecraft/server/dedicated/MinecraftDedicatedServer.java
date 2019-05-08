package net.minecraft.server.dedicated;

import org.apache.logging.log4j.LogManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.snooper.Snooper;
import java.util.function.BooleanSupplier;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.Difficulty;
import com.google.common.base.Strings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.UncaughtExceptionHandler;
import java.util.Locale;
import com.google.gson.JsonElement;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.level.LevelGeneratorType;
import com.google.gson.JsonObject;
import net.minecraft.block.entity.SkullBlockEntity;
import java.util.Random;
import net.minecraft.util.SystemUtil;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.network.NetworkEncryptionUtils;
import java.net.InetAddress;
import net.minecraft.SharedConstants;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.util.UncaughtExceptionLogger;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.server.command.CommandManager;
import java.net.Proxy;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.UserCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.world.GameMode;
import net.minecraft.server.rcon.RconServer;
import net.minecraft.server.rcon.QueryResponseHandler;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;

public class MinecraftDedicatedServer extends MinecraftServer implements DedicatedServer
{
    private static final Logger LOGGER;
    private static final Pattern SHA1_PATTERN;
    private final List<PendingServerCommand> commandQueue;
    private QueryResponseHandler queryResponseHandler;
    private final ServerCommandOutput rconCommandOutput;
    private RconServer rconServer;
    private final ServerPropertiesLoader propertiesLoader;
    private GameMode defaultGameMode;
    @Nullable
    private DedicatedServerGui gui;
    
    public MinecraftDedicatedServer(final File file, final ServerPropertiesLoader serverPropertiesLoader, final DataFixer dataFixer, final YggdrasilAuthenticationService yggdrasilAuthenticationService, final MinecraftSessionService minecraftSessionService, final GameProfileRepository gameProfileRepository, final UserCache userCache, final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, final String string) {
        super(file, Proxy.NO_PROXY, dataFixer, new CommandManager(true), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory, string);
        this.commandQueue = Collections.<PendingServerCommand>synchronizedList(Lists.newArrayList());
        this.propertiesLoader = serverPropertiesLoader;
        this.rconCommandOutput = new ServerCommandOutput(this);
        new Thread("Server Infinisleeper") {
            {
                this.setDaemon(true);
                this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(MinecraftDedicatedServer.LOGGER));
                this.start();
            }
            
            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (InterruptedException ex) {
                        continue;
                    }
                    break;
                }
            }
        };
    }
    
    public boolean setupServer() throws IOException {
        final Thread thread1 = new Thread("Server console handler") {
            @Override
            public void run() {
                final BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String string2;
                    while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string2 = bufferedReader1.readLine()) != null) {
                        MinecraftDedicatedServer.this.enqueueCommand(string2, MinecraftDedicatedServer.this.getCommandSource());
                    }
                }
                catch (IOException iOException3) {
                    MinecraftDedicatedServer.LOGGER.error("Exception handling console input", (Throwable)iOException3);
                }
            }
        };
        thread1.setDaemon(true);
        thread1.setUncaughtExceptionHandler(new UncaughtExceptionLogger(MinecraftDedicatedServer.LOGGER));
        thread1.start();
        MinecraftDedicatedServer.LOGGER.info("Starting minecraft server version " + SharedConstants.getGameVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            MinecraftDedicatedServer.LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        MinecraftDedicatedServer.LOGGER.info("Loading properties");
        final ServerPropertiesHandler serverPropertiesHandler2 = this.propertiesLoader.getPropertiesHandler();
        if (this.isSinglePlayer()) {
            this.setServerIp("127.0.0.1");
        }
        else {
            this.setOnlineMode(serverPropertiesHandler2.onlineMode);
            this.setPreventProxyConnections(serverPropertiesHandler2.preventProxyConnections);
            this.setServerIp(serverPropertiesHandler2.serverIp);
        }
        this.setSpawnAnimals(serverPropertiesHandler2.spawnAnimals);
        this.setSpawnNpcs(serverPropertiesHandler2.spawnNpcs);
        this.setPvpEnabled(serverPropertiesHandler2.pvp);
        this.setFlightEnabled(serverPropertiesHandler2.allowFlight);
        this.setResourcePack(serverPropertiesHandler2.resourcePack, this.createResourcePackHash());
        this.setMotd(serverPropertiesHandler2.motd);
        this.setForceGameMode(serverPropertiesHandler2.forceGameMode);
        super.setPlayerIdleTimeout(serverPropertiesHandler2.playerIdleTimeout.get());
        this.setWhitelistEnabled(serverPropertiesHandler2.enforceWhitelist);
        this.defaultGameMode = serverPropertiesHandler2.gameMode;
        MinecraftDedicatedServer.LOGGER.info("Default game type: {}", this.defaultGameMode);
        InetAddress inetAddress3 = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress3 = InetAddress.getByName(this.getServerIp());
        }
        if (this.getServerPort() < 0) {
            this.setServerPort(serverPropertiesHandler2.serverPort);
        }
        MinecraftDedicatedServer.LOGGER.info("Generating keypair");
        this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
        MinecraftDedicatedServer.LOGGER.info("Starting Minecraft server on {}:{}", (this.getServerIp().isEmpty() ? "*" : this.getServerIp()), this.getServerPort());
        try {
            this.getNetworkIo().bind(inetAddress3, this.getServerPort());
        }
        catch (IOException iOException4) {
            MinecraftDedicatedServer.LOGGER.warn("**** FAILED TO BIND TO PORT!");
            MinecraftDedicatedServer.LOGGER.warn("The exception was: {}", iOException4.toString());
            MinecraftDedicatedServer.LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.isOnlineMode()) {
            MinecraftDedicatedServer.LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            MinecraftDedicatedServer.LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            MinecraftDedicatedServer.LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            MinecraftDedicatedServer.LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertData()) {
            this.getUserCache().save();
        }
        if (!ServerConfigHandler.checkSuccess(this)) {
            return false;
        }
        this.setPlayerManager(new DedicatedPlayerManager(this));
        final long long4 = SystemUtil.getMeasuringTimeNano();
        final String string6 = serverPropertiesHandler2.levelSeed;
        final String string7 = serverPropertiesHandler2.generatorSettings;
        long long5 = new Random().nextLong();
        if (!string6.isEmpty()) {
            try {
                final long long6 = Long.parseLong(string6);
                if (long6 != 0L) {
                    long5 = long6;
                }
            }
            catch (NumberFormatException numberFormatException10) {
                long5 = string6.hashCode();
            }
        }
        final LevelGeneratorType levelGeneratorType10 = serverPropertiesHandler2.levelType;
        this.setWorldHeight(serverPropertiesHandler2.maxBuildHeight);
        SkullBlockEntity.setUserCache(this.getUserCache());
        SkullBlockEntity.setSessionService(this.getSessionService());
        UserCache.setUseRemote(this.isOnlineMode());
        MinecraftDedicatedServer.LOGGER.info("Preparing level \"{}\"", this.getLevelName());
        JsonObject jsonObject11 = new JsonObject();
        if (levelGeneratorType10 == LevelGeneratorType.FLAT) {
            jsonObject11.addProperty("flat_world_options", string7);
        }
        else if (!string7.isEmpty()) {
            jsonObject11 = JsonHelper.deserialize(string7);
        }
        this.loadWorld(this.getLevelName(), this.getLevelName(), long5, levelGeneratorType10, jsonObject11);
        final long long7 = SystemUtil.getMeasuringTimeNano() - long4;
        final String string8 = String.format(Locale.ROOT, "%.3fs", long7 / 1.0E9);
        MinecraftDedicatedServer.LOGGER.info("Done ({})! For help, type \"help\"", string8);
        if (serverPropertiesHandler2.announcePlayerAchievements != null) {
            this.getGameRules().put("announceAdvancements", ((boolean)serverPropertiesHandler2.announcePlayerAchievements) ? "true" : "false", this);
        }
        if (serverPropertiesHandler2.enableQuery) {
            MinecraftDedicatedServer.LOGGER.info("Starting GS4 status listener");
            (this.queryResponseHandler = new QueryResponseHandler(this)).start();
        }
        if (serverPropertiesHandler2.enableRcon) {
            MinecraftDedicatedServer.LOGGER.info("Starting remote control listener");
            (this.rconServer = new RconServer(this)).start();
        }
        if (this.getMaxTickTime() > 0L) {
            final Thread thread2 = new Thread(new DedicatedServerWatchdog(this));
            thread2.setUncaughtExceptionHandler(new UncaughtExceptionHandler(MinecraftDedicatedServer.LOGGER));
            thread2.setName("Server Watchdog");
            thread2.setDaemon(true);
            thread2.start();
        }
        Items.AIR.appendItemsForGroup(ItemGroup.SEARCH, DefaultedList.<ItemStack>create());
        return true;
    }
    
    public String createResourcePackHash() {
        final ServerPropertiesHandler serverPropertiesHandler1 = this.propertiesLoader.getPropertiesHandler();
        String string2;
        if (!serverPropertiesHandler1.resourcePackSha1.isEmpty()) {
            string2 = serverPropertiesHandler1.resourcePackSha1;
            if (!Strings.isNullOrEmpty(serverPropertiesHandler1.resourcePackHash)) {
                MinecraftDedicatedServer.LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        }
        else if (!Strings.isNullOrEmpty(serverPropertiesHandler1.resourcePackHash)) {
            MinecraftDedicatedServer.LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string2 = serverPropertiesHandler1.resourcePackHash;
        }
        else {
            string2 = "";
        }
        if (!string2.isEmpty() && !MinecraftDedicatedServer.SHA1_PATTERN.matcher(string2).matches()) {
            MinecraftDedicatedServer.LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }
        if (!serverPropertiesHandler1.resourcePack.isEmpty() && string2.isEmpty()) {
            MinecraftDedicatedServer.LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }
        return string2;
    }
    
    @Override
    public void setDefaultGameMode(final GameMode gameMode) {
        super.setDefaultGameMode(gameMode);
        this.defaultGameMode = gameMode;
    }
    
    @Override
    public ServerPropertiesHandler getProperties() {
        return this.propertiesLoader.getPropertiesHandler();
    }
    
    @Override
    public boolean shouldGenerateStructures() {
        return this.getProperties().generateStructures;
    }
    
    @Override
    public GameMode getDefaultGameMode() {
        return this.defaultGameMode;
    }
    
    @Override
    public Difficulty getDefaultDifficulty() {
        return this.getProperties().difficulty;
    }
    
    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }
    
    @Override
    public CrashReport populateCrashReport(CrashReport crashReport) {
        crashReport = super.populateCrashReport(crashReport);
        final String string1;
        crashReport.getSystemDetailsSection().add("Is Modded", () -> {
            string1 = this.getServerModName();
            if (!"vanilla".equals(string1)) {
                return "Definitely; Server brand changed to '" + string1 + "'";
            }
            else {
                return "Unknown (can't tell)";
            }
        });
        crashReport.getSystemDetailsSection().add("Type", () -> "Dedicated Server (map_server.txt)");
        return crashReport;
    }
    
    public void exit() {
        if (this.gui != null) {
            this.gui.stop();
        }
        if (this.rconServer != null) {
            this.rconServer.stop();
        }
        if (this.queryResponseHandler != null) {
            this.queryResponseHandler.stop();
        }
    }
    
    public void tickWorlds(final BooleanSupplier booleanSupplier) {
        super.tickWorlds(booleanSupplier);
        this.executeQueuedCommands();
    }
    
    @Override
    public boolean isNetherAllowed() {
        return this.getProperties().allowNether;
    }
    
    public boolean isMonsterSpawningEnabled() {
        return this.getProperties().spawnMonsters;
    }
    
    @Override
    public void addSnooperInfo(final Snooper snooper) {
        snooper.addInfo("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
        snooper.addInfo("whitelist_count", this.getPlayerManager().getWhitelistedNames().length);
        super.addSnooperInfo(snooper);
    }
    
    public void enqueueCommand(final String string, final ServerCommandSource serverCommandSource) {
        this.commandQueue.add(new PendingServerCommand(string, serverCommandSource));
    }
    
    public void executeQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            final PendingServerCommand pendingServerCommand1 = this.commandQueue.remove(0);
            this.getCommandManager().execute(pendingServerCommand1.source, pendingServerCommand1.command);
        }
    }
    
    @Override
    public boolean isDedicated() {
        return true;
    }
    
    @Override
    public boolean isUsingNativeTransport() {
        return this.getProperties().useNativeTransport;
    }
    
    @Override
    public DedicatedPlayerManager getPlayerManager() {
        return (DedicatedPlayerManager)super.getPlayerManager();
    }
    
    @Override
    public boolean isRemote() {
        return true;
    }
    
    @Override
    public String getHostname() {
        return this.getServerIp();
    }
    
    @Override
    public int getPort() {
        return this.getServerPort();
    }
    
    @Override
    public String getMotd() {
        return this.getServerMotd();
    }
    
    public void createGui() {
        if (this.gui == null) {
            this.gui = DedicatedServerGui.create(this);
        }
    }
    
    @Override
    public boolean hasGui() {
        return this.gui != null;
    }
    
    @Override
    public boolean openToLan(final GameMode gameMode, final boolean cheatsAllowed, final int port) {
        return false;
    }
    
    @Override
    public boolean areCommandBlocksEnabled() {
        return this.getProperties().enableCommandBlock;
    }
    
    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }
    
    @Override
    public boolean isSpawnProtected(final World world, final BlockPos blockPos, final PlayerEntity playerEntity) {
        if (world.dimension.getType() != DimensionType.a) {
            return false;
        }
        if (this.getPlayerManager().getOpList().isEmpty()) {
            return false;
        }
        if (this.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        final BlockPos blockPos2 = world.getSpawnPos();
        final int integer5 = MathHelper.abs(blockPos.getX() - blockPos2.getX());
        final int integer6 = MathHelper.abs(blockPos.getZ() - blockPos2.getZ());
        final int integer7 = Math.max(integer5, integer6);
        return integer7 <= this.getSpawnProtectionRadius();
    }
    
    @Override
    public int getOpPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }
    
    @Override
    public void setPlayerIdleTimeout(final int integer) {
        super.setPlayerIdleTimeout(integer);
        this.propertiesLoader.apply(serverPropertiesHandler -> (ServerPropertiesHandler)serverPropertiesHandler.playerIdleTimeout.set(integer));
    }
    
    @Override
    public boolean shouldBroadcastRconToOps() {
        return this.getProperties().broadcastRconToOps;
    }
    
    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.getProperties().broadcastConsoleToOps;
    }
    
    @Override
    public int getMaxWorldBorderRadius() {
        return this.getProperties().maxWorldSize;
    }
    
    @Override
    public int getNetworkCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }
    
    protected boolean convertData() {
        boolean boolean2 = false;
        for (int integer1 = 0; !boolean2 && integer1 <= 2; boolean2 = ServerConfigHandler.convertBannedPlayers(this), ++integer1) {
            if (integer1 > 0) {
                MinecraftDedicatedServer.LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
        }
        boolean boolean3 = false;
        for (int integer1 = 0; !boolean3 && integer1 <= 2; boolean3 = ServerConfigHandler.convertBannedIps(this), ++integer1) {
            if (integer1 > 0) {
                MinecraftDedicatedServer.LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
        }
        boolean boolean4 = false;
        for (int integer1 = 0; !boolean4 && integer1 <= 2; boolean4 = ServerConfigHandler.convertOperators(this), ++integer1) {
            if (integer1 > 0) {
                MinecraftDedicatedServer.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
        }
        boolean boolean5 = false;
        for (int integer1 = 0; !boolean5 && integer1 <= 2; boolean5 = ServerConfigHandler.convertWhitelist(this), ++integer1) {
            if (integer1 > 0) {
                MinecraftDedicatedServer.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
        }
        boolean boolean6 = false;
        for (int integer1 = 0; !boolean6 && integer1 <= 2; boolean6 = ServerConfigHandler.convertPlayerFiles(this), ++integer1) {
            if (integer1 > 0) {
                MinecraftDedicatedServer.LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
        }
        return boolean2 || boolean3 || boolean4 || boolean5 || boolean6;
    }
    
    private void sleepFiveSeconds() {
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedException1) {}
    }
    
    public long getMaxTickTime() {
        return this.getProperties().maxTickTime;
    }
    
    @Override
    public String getPlugins() {
        return "";
    }
    
    @Override
    public String executeRconCommand(final String string) {
        this.rconCommandOutput.clear();
        this.getCommandManager().execute(this.rconCommandOutput.createReconCommandSource(), string);
        return this.rconCommandOutput.asString();
    }
    
    public void setUseWhitelist(final boolean boolean1) {
        this.propertiesLoader.apply(serverPropertiesHandler -> (ServerPropertiesHandler)serverPropertiesHandler.whiteList.set(boolean1));
    }
    
    public void shutdown() {
        super.shutdown();
        SystemUtil.shutdownServerWorkerExecutor();
    }
    
    @Override
    public boolean isOwner(final GameProfile profile) {
        return false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    }
}
