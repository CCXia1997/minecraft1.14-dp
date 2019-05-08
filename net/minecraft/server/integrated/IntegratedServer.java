package net.minecraft.server.integrated;

import org.apache.logging.log4j.LogManager;
import java.util.List;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.server.network.ServerPlayerEntity;
import java.net.InetAddress;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.util.profiler.DisableableProfiler;
import java.util.function.BooleanSupplier;
import java.io.IOException;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.SharedConstants;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import java.io.File;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.UserCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import net.minecraft.server.LanServerPinger;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.CLIENT)
public class IntegratedServer extends MinecraftServer
{
    private static final Logger LOGGER;
    private final MinecraftClient client;
    private final LevelInfo levelInfo;
    private boolean k;
    private int lanPort;
    private LanServerPinger lanPinger;
    private UUID localPlayerUuid;
    
    public IntegratedServer(final MinecraftClient client, final String levelName, final String displayName, final LevelInfo levelInfo, final YggdrasilAuthenticationService authService, final MinecraftSessionService sessionService, final GameProfileRepository profileRepo, final UserCache userCache, final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(new File(client.runDirectory, "saves"), client.getNetworkProxy(), client.getDataFixer(), new CommandManager(false), authService, sessionService, profileRepo, userCache, worldGenerationProgressListenerFactory, levelName);
        this.lanPort = -1;
        this.setUserName(client.getSession().getUsername());
        this.setServerName(displayName);
        this.setDemo(client.isDemo());
        this.setBonusChest(levelInfo.hasBonusChest());
        this.setWorldHeight(256);
        this.setPlayerManager(new IntegratedPlayerManager(this));
        this.client = client;
        this.levelInfo = (this.isDemo() ? MinecraftServer.WORLD_INFO : levelInfo);
    }
    
    public void loadWorld(final String name, final String serverName, final long seed, final LevelGeneratorType generatorType, final JsonElement generatorSettings) {
        this.upgradeWorld(name);
        final WorldSaveHandler worldSaveHandler7 = this.getLevelStorage().createSaveHandler(name, this);
        this.loadWorldResourcePack(this.getLevelName(), worldSaveHandler7);
        LevelProperties levelProperties8 = worldSaveHandler7.readProperties();
        if (levelProperties8 == null) {
            levelProperties8 = new LevelProperties(this.levelInfo, serverName);
        }
        else {
            levelProperties8.setLevelName(serverName);
        }
        this.loadWorldDataPacks(worldSaveHandler7.getWorldDir(), levelProperties8);
        final WorldGenerationProgressListener worldGenerationProgressListener9 = this.worldGenerationProgressListenerFactory.create(11);
        this.createWorlds(worldSaveHandler7, levelProperties8, this.levelInfo, worldGenerationProgressListener9);
        if (this.getWorld(DimensionType.a).getLevelProperties().getDifficulty() == null) {
            this.setDifficulty(this.client.options.difficulty, true);
        }
        this.prepareStartRegion(worldGenerationProgressListener9);
    }
    
    public boolean setupServer() throws IOException {
        IntegratedServer.LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getGameVersion().getName());
        this.setOnlineMode(true);
        this.setSpawnAnimals(true);
        this.setSpawnNpcs(true);
        this.setPvpEnabled(true);
        this.setFlightEnabled(true);
        IntegratedServer.LOGGER.info("Generating keypair");
        this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
        this.loadWorld(this.getLevelName(), this.getServerName(), this.levelInfo.getSeed(), this.levelInfo.getGeneratorType(), this.levelInfo.getGeneratorOptions());
        this.setMotd(this.getUserName() + " - " + this.getWorld(DimensionType.a).getLevelProperties().getLevelName());
        return true;
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        final boolean boolean2 = this.k;
        this.k = (MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused());
        final DisableableProfiler disableableProfiler3 = this.getProfiler();
        if (!boolean2 && this.k) {
            disableableProfiler3.push("autoSave");
            IntegratedServer.LOGGER.info("Saving and pausing game...");
            this.getPlayerManager().saveAllPlayerData();
            this.save(false, false, false);
            disableableProfiler3.pop();
        }
        if (this.k) {
            return;
        }
        super.tick(booleanSupplier);
        final int integer4 = Math.max(2, this.client.options.viewDistance - 2);
        if (integer4 != this.getPlayerManager().getViewDistance()) {
            IntegratedServer.LOGGER.info("Changing view distance to {}, from {}", integer4, this.getPlayerManager().getViewDistance());
            this.getPlayerManager().setViewDistance(integer4, integer4 - 2);
        }
    }
    
    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
    
    @Override
    public GameMode getDefaultGameMode() {
        return this.levelInfo.getGameMode();
    }
    
    @Override
    public Difficulty getDefaultDifficulty() {
        return this.client.world.getLevelProperties().getDifficulty();
    }
    
    @Override
    public boolean isHardcore() {
        return this.levelInfo.isHardcore();
    }
    
    @Override
    public boolean shouldBroadcastRconToOps() {
        return true;
    }
    
    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }
    
    @Override
    public File getRunDirectory() {
        return this.client.runDirectory;
    }
    
    @Override
    public boolean isDedicated() {
        return false;
    }
    
    @Override
    public boolean isUsingNativeTransport() {
        return false;
    }
    
    public void setCrashReport(final CrashReport crashReport) {
        this.client.setCrashReport(crashReport);
    }
    
    @Override
    public CrashReport populateCrashReport(CrashReport crashReport) {
        crashReport = super.populateCrashReport(crashReport);
        crashReport.getSystemDetailsSection().add("Type", "Integrated Server (map_client.txt)");
        final String string1;
        String string2;
        crashReport.getSystemDetailsSection().add("Is Modded", () -> {
            string1 = ClientBrandRetriever.getClientModName();
            if (!string1.equals("vanilla")) {
                return "Definitely; Client brand changed to '" + string1 + "'";
            }
            else {
                string2 = this.getServerModName();
                if (!"vanilla".equals(string2)) {
                    return "Definitely; Server brand changed to '" + string2 + "'";
                }
                else if (MinecraftClient.class.getSigners() == null) {
                    return "Very likely; Jar signature invalidated";
                }
                else {
                    return "Probably not. Jar signature remains and both client + server brands are untouched.";
                }
            }
        });
        return crashReport;
    }
    
    @Override
    public void addSnooperInfo(final Snooper snooper) {
        super.addSnooperInfo(snooper);
        snooper.addInfo("snooper_partner", this.client.getSnooper().getToken());
    }
    
    @Override
    public boolean openToLan(final GameMode gameMode, final boolean cheatsAllowed, final int port) {
        try {
            this.getNetworkIo().bind(null, port);
            IntegratedServer.LOGGER.info("Started serving on {}", port);
            this.lanPort = port;
            (this.lanPinger = new LanServerPinger(this.getServerMotd(), port + "")).start();
            this.getPlayerManager().setGameMode(gameMode);
            this.getPlayerManager().setCheatsAllowed(cheatsAllowed);
            final int integer4 = this.getPermissionLevel(this.client.player.getGameProfile());
            this.client.player.setClientPermissionLevel(integer4);
            for (final ServerPlayerEntity serverPlayerEntity6 : this.getPlayerManager().getPlayerList()) {
                this.getCommandManager().sendCommandTree(serverPlayerEntity6);
            }
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    public void shutdown() {
        super.shutdown();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }
    
    @Override
    public void stop(final boolean boolean1) {
        final ArrayList<Object> list1;
        final Iterator<ServerPlayerEntity> iterator;
        ServerPlayerEntity serverPlayerEntity3;
        this.executeSync(() -> {
            list1 = Lists.newArrayList(this.getPlayerManager().getPlayerList());
            list1.iterator();
            while (iterator.hasNext()) {
                serverPlayerEntity3 = iterator.next();
                if (!serverPlayerEntity3.getUuid().equals(this.localPlayerUuid)) {
                    this.getPlayerManager().remove(serverPlayerEntity3);
                }
            }
            return;
        });
        super.stop(boolean1);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }
    
    @Override
    public boolean isRemote() {
        return this.lanPort > -1;
    }
    
    @Override
    public int getServerPort() {
        return this.lanPort;
    }
    
    @Override
    public void setDefaultGameMode(final GameMode gameMode) {
        super.setDefaultGameMode(gameMode);
        this.getPlayerManager().setGameMode(gameMode);
    }
    
    @Override
    public boolean areCommandBlocksEnabled() {
        return true;
    }
    
    @Override
    public int getOpPermissionLevel() {
        return 2;
    }
    
    public void setLocalPlayerUuid(final UUID localPlayerUuid) {
        this.localPlayerUuid = localPlayerUuid;
    }
    
    @Override
    public boolean isOwner(final GameProfile profile) {
        return profile.getName().equalsIgnoreCase(this.getUserName());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
