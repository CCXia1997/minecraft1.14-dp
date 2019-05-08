package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.Properties;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.GameMode;
import net.minecraft.world.Difficulty;

public class ServerPropertiesHandler extends AbstractPropertiesHandler<ServerPropertiesHandler>
{
    public final boolean onlineMode;
    public final boolean preventProxyConnections;
    public final String serverIp;
    public final boolean spawnAnimals;
    public final boolean spawnNpcs;
    public final boolean pvp;
    public final boolean allowFlight;
    public final String resourcePack;
    public final String motd;
    public final boolean forceGameMode;
    public final boolean enforceWhitelist;
    public final boolean generateStructures;
    public final Difficulty difficulty;
    public final GameMode gameMode;
    public final String levelName;
    public final String levelSeed;
    public final LevelGeneratorType levelType;
    public final String generatorSettings;
    public final int serverPort;
    public final int maxBuildHeight;
    public final Boolean announcePlayerAchievements;
    public final boolean enableQuery;
    public final int queryPort;
    public final boolean enableRcon;
    public final int rconPort;
    public final String rconPassword;
    public final String resourcePackHash;
    public final String resourcePackSha1;
    public final boolean hardcore;
    public final boolean allowNether;
    public final boolean spawnMonsters;
    public final boolean snooperEnabled;
    public final boolean useNativeTransport;
    public final boolean enableCommandBlock;
    public final int spawnProtection;
    public final int opPermissionLevel;
    public final long maxTickTime;
    public final int viewDistance;
    public final int maxPlayers;
    public final int networkCompressionThreshold;
    public final boolean broadcastRconToOps;
    public final boolean broadcastConsoleToOps;
    public final int maxWorldSize;
    public final PropertyAccessor<Integer> playerIdleTimeout;
    public final PropertyAccessor<Boolean> whiteList;
    
    public ServerPropertiesHandler(final Properties properties) {
        super(properties);
        this.onlineMode = this.parseBoolean("online-mode", true);
        this.preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
        this.serverIp = this.getString("server-ip", "");
        this.spawnAnimals = this.parseBoolean("spawn-animals", true);
        this.spawnNpcs = this.parseBoolean("spawn-npcs", true);
        this.pvp = this.parseBoolean("pvp", true);
        this.allowFlight = this.parseBoolean("allow-flight", false);
        this.resourcePack = this.getString("resource-pack", "");
        this.motd = this.getString("motd", "A Minecraft Server");
        this.forceGameMode = this.parseBoolean("force-gamemode", false);
        this.enforceWhitelist = this.parseBoolean("enforce-whitelist", false);
        this.generateStructures = this.parseBoolean("generate-structures", true);
        this.difficulty = this.<Difficulty>get("difficulty", AbstractPropertiesHandler.wrapIntParsingFunction((IntFunction<V>)Difficulty::getDifficulty, (Function<String, V>)Difficulty::getDifficulty), Difficulty::getTranslationKey, Difficulty.EASY);
        this.gameMode = this.<GameMode>get("gamemode", AbstractPropertiesHandler.wrapIntParsingFunction((IntFunction<V>)GameMode::byId, (Function<String, V>)GameMode::byName), GameMode::getName, GameMode.b);
        this.levelName = this.getString("level-name", "world");
        this.levelSeed = this.getString("level-seed", "");
        this.levelType = this.<LevelGeneratorType>get("level-type", LevelGeneratorType::getTypeFromName, LevelGeneratorType::getName, LevelGeneratorType.DEFAULT);
        this.generatorSettings = this.getString("generator-settings", "");
        this.serverPort = this.getInt("server-port", 25565);
        this.maxBuildHeight = this.parseIntWithOperation("max-build-height", integer -> MathHelper.clamp((integer + 8) / 16 * 16, 64, 256), 256);
        this.announcePlayerAchievements = this.getDeprecatedBoolean("announce-player-achievements");
        this.enableQuery = this.parseBoolean("enable-query", false);
        this.queryPort = this.getInt("query.port", 25565);
        this.enableRcon = this.parseBoolean("enable-rcon", false);
        this.rconPort = this.getInt("rcon.port", 25575);
        this.rconPassword = this.getString("rcon.password", "");
        this.resourcePackHash = this.getDeprecatedString("resource-pack-hash");
        this.resourcePackSha1 = this.getString("resource-pack-sha1", "");
        this.hardcore = this.parseBoolean("hardcore", false);
        this.allowNether = this.parseBoolean("allow-nether", true);
        this.spawnMonsters = this.parseBoolean("spawn-monsters", true);
        if (this.parseBoolean("snooper-enabled", true)) {}
        this.snooperEnabled = false;
        this.useNativeTransport = this.parseBoolean("use-native-transport", true);
        this.enableCommandBlock = this.parseBoolean("enable-command-block", false);
        this.spawnProtection = this.getInt("spawn-protection", 16);
        this.opPermissionLevel = this.getInt("op-permission-level", 4);
        this.maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
        this.viewDistance = this.getInt("view-distance", 10);
        this.maxPlayers = this.getInt("max-players", 20);
        this.networkCompressionThreshold = this.getInt("network-compression-threshold", 256);
        this.broadcastRconToOps = this.parseBoolean("broadcast-rcon-to-ops", true);
        this.broadcastConsoleToOps = this.parseBoolean("broadcast-console-to-ops", true);
        this.maxWorldSize = this.parseIntWithOperation("max-world-size", integer -> MathHelper.clamp(integer, 1, 29999984), 29999984);
        this.playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
        this.whiteList = this.booleanAccessor("white-list", false);
    }
    
    public static ServerPropertiesHandler load(final Path path) {
        return new ServerPropertiesHandler(AbstractPropertiesHandler.load(path));
    }
    
    @Override
    protected ServerPropertiesHandler create(final Properties properties) {
        return new ServerPropertiesHandler(properties);
    }
}
