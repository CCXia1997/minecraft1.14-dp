package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.text.ChatMessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import java.util.Collection;
import net.minecraft.scoreboard.AbstractTeam;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ViewableWorld;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import java.net.SocketAddress;
import net.minecraft.stat.Stats;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.scoreboard.ScoreboardObjective;
import java.util.Set;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.scoreboard.Team;
import com.google.common.collect.Sets;
import net.minecraft.scoreboard.ServerScoreboard;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.UserCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.util.PacketByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.IWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.network.ClientConnection;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.GameMode;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.stat.ServerStatHandler;
import java.util.UUID;
import java.util.Map;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.Logger;
import java.io.File;

public abstract class PlayerManager
{
    public static final File BANNED_PLAYERS_FILE;
    public static final File BANNED_IPS_FILE;
    public static final File OPERATORS_FILE;
    public static final File WHITELIST_FILE;
    private static final Logger LOGGER;
    private static final SimpleDateFormat DATE_FORMATTER;
    private final MinecraftServer server;
    private final List<ServerPlayerEntity> players;
    private final Map<UUID, ServerPlayerEntity> playerMap;
    private final BannedPlayerList bannedProfiles;
    private final BannedIpList bannedIps;
    private final OperatorList ops;
    private final Whitelist whitelist;
    private final Map<UUID, ServerStatHandler> statisticsMap;
    private final Map<UUID, PlayerAdvancementTracker> advancementManagerMap;
    private PlayerSaveHandler saveHandler;
    private boolean whitelistEnabled;
    protected final int maxPlayers;
    private int viewDistance;
    private GameMode gameMode;
    private boolean cheatsAllowed;
    private int latencyUpdateTimer;
    
    public PlayerManager(final MinecraftServer server, final int integer) {
        this.players = Lists.newArrayList();
        this.playerMap = Maps.newHashMap();
        this.bannedProfiles = new BannedPlayerList(PlayerManager.BANNED_PLAYERS_FILE);
        this.bannedIps = new BannedIpList(PlayerManager.BANNED_IPS_FILE);
        this.ops = new OperatorList(PlayerManager.OPERATORS_FILE);
        this.whitelist = new Whitelist(PlayerManager.WHITELIST_FILE);
        this.statisticsMap = Maps.newHashMap();
        this.advancementManagerMap = Maps.newHashMap();
        this.server = server;
        this.maxPlayers = integer;
        this.getUserBanList().setEnabled(true);
        this.getIpBanList().setEnabled(true);
    }
    
    public void onPlayerConnect(final ClientConnection connection, final ServerPlayerEntity serverPlayerEntity) {
        final GameProfile gameProfile3 = serverPlayerEntity.getGameProfile();
        final UserCache userCache4 = this.server.getUserCache();
        final GameProfile gameProfile4 = userCache4.getByUuid(gameProfile3.getId());
        final String string6 = (gameProfile4 == null) ? gameProfile3.getName() : gameProfile4.getName();
        userCache4.add(gameProfile3);
        final CompoundTag compoundTag7 = this.loadPlayerData(serverPlayerEntity);
        final ServerWorld serverWorld8 = this.server.getWorld(serverPlayerEntity.dimension);
        serverPlayerEntity.setWorld(serverWorld8);
        serverPlayerEntity.interactionManager.setWorld((ServerWorld)serverPlayerEntity.world);
        String string7 = "local";
        if (connection.getAddress() != null) {
            string7 = connection.getAddress().toString();
        }
        PlayerManager.LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", serverPlayerEntity.getName().getString(), string7, serverPlayerEntity.getEntityId(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
        final LevelProperties levelProperties10 = serverWorld8.getLevelProperties();
        this.setGameMode(serverPlayerEntity, null, serverWorld8);
        final ServerPlayNetworkHandler serverPlayNetworkHandler11 = new ServerPlayNetworkHandler(this.server, connection, serverPlayerEntity);
        serverPlayNetworkHandler11.sendPacket(new GameJoinS2CPacket(serverPlayerEntity.getEntityId(), serverPlayerEntity.interactionManager.getGameMode(), levelProperties10.isHardcore(), serverWorld8.dimension.getType(), this.getMaxPlayerCount(), levelProperties10.getGeneratorType(), this.viewDistance, serverWorld8.getGameRules().getBoolean("reducedDebugInfo")));
        serverPlayNetworkHandler11.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        serverPlayNetworkHandler11.sendPacket(new DifficultyS2CPacket(levelProperties10.getDifficulty(), levelProperties10.isDifficultyLocked()));
        serverPlayNetworkHandler11.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
        serverPlayNetworkHandler11.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
        serverPlayNetworkHandler11.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        serverPlayNetworkHandler11.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        this.sendCommandTree(serverPlayerEntity);
        serverPlayerEntity.getStatHandler().updateStatSet();
        serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
        this.sendScoreboard(serverWorld8.getScoreboard(), serverPlayerEntity);
        this.server.forcePlayerSampleUpdate();
        TextComponent textComponent12;
        if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string6)) {
            textComponent12 = new TranslatableTextComponent("multiplayer.player.joined", new Object[] { serverPlayerEntity.getDisplayName() });
        }
        else {
            textComponent12 = new TranslatableTextComponent("multiplayer.player.joined.renamed", new Object[] { serverPlayerEntity.getDisplayName(), string6 });
        }
        this.sendToAll(textComponent12.applyFormat(TextFormat.o));
        serverPlayNetworkHandler11.requestTeleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
        this.players.add(serverPlayerEntity);
        this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD, new ServerPlayerEntity[] { serverPlayerEntity }));
        for (int integer13 = 0; integer13 < this.players.size(); ++integer13) {
            serverPlayerEntity.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD, new ServerPlayerEntity[] { this.players.get(integer13) }));
        }
        serverWorld8.c(serverPlayerEntity);
        this.server.getBossBarManager().a(serverPlayerEntity);
        this.sendWorldInfo(serverPlayerEntity, serverWorld8);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            serverPlayerEntity.a(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }
        for (final StatusEffectInstance statusEffectInstance14 : serverPlayerEntity.getStatusEffects()) {
            serverPlayNetworkHandler11.sendPacket(new EntityPotionEffectS2CPacket(serverPlayerEntity.getEntityId(), statusEffectInstance14));
        }
        if (compoundTag7 != null && compoundTag7.containsKey("RootVehicle", 10)) {
            final CompoundTag compoundTag8 = compoundTag7.getCompound("RootVehicle");
            final Entity entity2 = EntityType.loadEntityWithPassengers(compoundTag8.getCompound("Entity"), (World)serverWorld8, entity -> {
                if (!serverWorld8.d(entity)) {
                    return null;
                }
                else {
                    return entity;
                }
            });
            if (entity2 != null) {
                final UUID uUID15 = compoundTag8.getUuid("Attach");
                if (entity2.getUuid().equals(uUID15)) {
                    serverPlayerEntity.startRiding(entity2, true);
                }
                else {
                    for (final Entity entity3 : entity2.getPassengersDeep()) {
                        if (entity3.getUuid().equals(uUID15)) {
                            serverPlayerEntity.startRiding(entity3, true);
                            break;
                        }
                    }
                }
                if (!serverPlayerEntity.hasVehicle()) {
                    PlayerManager.LOGGER.warn("Couldn't reattach entity to player");
                    serverWorld8.removeEntity(entity2);
                    for (final Entity entity3 : entity2.getPassengersDeep()) {
                        serverWorld8.removeEntity(entity3);
                    }
                }
            }
        }
        serverPlayerEntity.s_();
    }
    
    protected void sendScoreboard(final ServerScoreboard scoreboard, final ServerPlayerEntity player) {
        final Set<ScoreboardObjective> set3 = Sets.newHashSet();
        for (final Team team5 : scoreboard.getTeams()) {
            player.networkHandler.sendPacket(new TeamS2CPacket(team5, 0));
        }
        for (int integer4 = 0; integer4 < 19; ++integer4) {
            final ScoreboardObjective scoreboardObjective5 = scoreboard.getObjectiveForSlot(integer4);
            if (scoreboardObjective5 != null && !set3.contains(scoreboardObjective5)) {
                final List<Packet<?>> list6 = scoreboard.createChangePackets(scoreboardObjective5);
                for (final Packet<?> packet8 : list6) {
                    player.networkHandler.sendPacket(packet8);
                }
                set3.add(scoreboardObjective5);
            }
        }
    }
    
    public void setMainWorld(final ServerWorld world) {
        this.saveHandler = world.getSaveHandler();
        world.getWorldBorder().addListener(new WorldBorderListener() {
            @Override
            public void onSizeChange(final WorldBorder worldBorder, final double double2) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_SIZE));
            }
            
            @Override
            public void onInterpolateSize(final WorldBorder border, final double fromSize, final double toSize, final long time) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.INTERPOLATE_SIZE));
            }
            
            @Override
            public void onCenterChanged(final WorldBorder centerX, final double centerZ, final double double4) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(centerX, WorldBorderS2CPacket.Type.SET_CENTER));
            }
            
            @Override
            public void onWarningTimeChanged(final WorldBorder warningTime, final int integer) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(warningTime, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
            }
            
            @Override
            public void onWarningBlocksChanged(final WorldBorder warningBlocks, final int integer) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(warningBlocks, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
            }
            
            @Override
            public void onDamagePerBlockChanged(final WorldBorder damagePerBlock, final double double2) {
            }
            
            @Override
            public void onSafeZoneChanged(final WorldBorder safeZoneRadius, final double double2) {
            }
        });
    }
    
    @Nullable
    public CompoundTag loadPlayerData(final ServerPlayerEntity player) {
        final CompoundTag compoundTag2 = this.server.getWorld(DimensionType.a).getLevelProperties().getPlayerData();
        CompoundTag compoundTag3;
        if (player.getName().getString().equals(this.server.getUserName()) && compoundTag2 != null) {
            compoundTag3 = compoundTag2;
            player.fromTag(compoundTag3);
            PlayerManager.LOGGER.debug("loading single player");
        }
        else {
            compoundTag3 = this.saveHandler.loadPlayerData(player);
        }
        return compoundTag3;
    }
    
    protected void savePlayerData(final ServerPlayerEntity player) {
        this.saveHandler.savePlayerData(player);
        final ServerStatHandler serverStatHandler2 = this.statisticsMap.get(player.getUuid());
        if (serverStatHandler2 != null) {
            serverStatHandler2.save();
        }
        final PlayerAdvancementTracker playerAdvancementTracker3 = this.advancementManagerMap.get(player.getUuid());
        if (playerAdvancementTracker3 != null) {
            playerAdvancementTracker3.save();
        }
    }
    
    public void remove(final ServerPlayerEntity player) {
        final ServerWorld serverWorld2 = player.getServerWorld();
        player.incrementStat(Stats.j);
        this.savePlayerData(player);
        if (player.hasVehicle()) {
            final Entity entity3 = player.getTopmostVehicle();
            if (entity3.bX()) {
                PlayerManager.LOGGER.debug("Removing player mount");
                player.stopRiding();
                serverWorld2.removeEntity(entity3);
                for (final Entity entity4 : entity3.getPassengersDeep()) {
                    serverWorld2.removeEntity(entity4);
                }
                serverWorld2.getChunk(player.chunkX, player.chunkZ).markDirty();
            }
        }
        player.detach();
        serverWorld2.removePlayer(player);
        player.getAdvancementManager().clearCriterions();
        this.players.remove(player);
        this.server.getBossBarManager().b(player);
        final UUID uUID3 = player.getUuid();
        final ServerPlayerEntity serverPlayerEntity4 = this.playerMap.get(uUID3);
        if (serverPlayerEntity4 == player) {
            this.playerMap.remove(uUID3);
            this.statisticsMap.remove(uUID3);
            this.advancementManagerMap.remove(uUID3);
        }
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE, new ServerPlayerEntity[] { player }));
    }
    
    @Nullable
    public TextComponent checkCanJoin(final SocketAddress socketAddress, final GameProfile gameProfile) {
        if (this.bannedProfiles.contains(gameProfile)) {
            final BannedPlayerEntry bannedPlayerEntry3 = this.bannedProfiles.get(gameProfile);
            final TextComponent textComponent4 = new TranslatableTextComponent("multiplayer.disconnect.banned.reason", new Object[] { bannedPlayerEntry3.getReason() });
            if (bannedPlayerEntry3.getExpiryDate() != null) {
                textComponent4.append(new TranslatableTextComponent("multiplayer.disconnect.banned.expiration", new Object[] { PlayerManager.DATE_FORMATTER.format(bannedPlayerEntry3.getExpiryDate()) }));
            }
            return textComponent4;
        }
        if (!this.isWhitelisted(gameProfile)) {
            return new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted", new Object[0]);
        }
        if (this.bannedIps.isBanned(socketAddress)) {
            final BannedIpEntry bannedIpEntry3 = this.bannedIps.get(socketAddress);
            final TextComponent textComponent4 = new TranslatableTextComponent("multiplayer.disconnect.banned_ip.reason", new Object[] { bannedIpEntry3.getReason() });
            if (bannedIpEntry3.getExpiryDate() != null) {
                textComponent4.append(new TranslatableTextComponent("multiplayer.disconnect.banned_ip.expiration", new Object[] { PlayerManager.DATE_FORMATTER.format(bannedIpEntry3.getExpiryDate()) }));
            }
            return textComponent4;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            return new TranslatableTextComponent("multiplayer.disconnect.server_full", new Object[0]);
        }
        return null;
    }
    
    public ServerPlayerEntity createPlayer(final GameProfile profile) {
        final UUID uUID2 = PlayerEntity.getUuidFromProfile(profile);
        final List<ServerPlayerEntity> list3 = Lists.newArrayList();
        for (int integer4 = 0; integer4 < this.players.size(); ++integer4) {
            final ServerPlayerEntity serverPlayerEntity5 = this.players.get(integer4);
            if (serverPlayerEntity5.getUuid().equals(uUID2)) {
                list3.add(serverPlayerEntity5);
            }
        }
        final ServerPlayerEntity serverPlayerEntity6 = this.playerMap.get(profile.getId());
        if (serverPlayerEntity6 != null && !list3.contains(serverPlayerEntity6)) {
            list3.add(serverPlayerEntity6);
        }
        for (final ServerPlayerEntity serverPlayerEntity7 : list3) {
            serverPlayerEntity7.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.duplicate_login", new Object[0]));
        }
        ServerPlayerInteractionManager serverPlayerInteractionManager5;
        if (this.server.isDemo()) {
            serverPlayerInteractionManager5 = new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.a));
        }
        else {
            serverPlayerInteractionManager5 = new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.a));
        }
        return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.a), profile, serverPlayerInteractionManager5);
    }
    
    public ServerPlayerEntity respawnPlayer(final ServerPlayerEntity serverPlayerEntity, final DimensionType dimensionType, final boolean alive) {
        this.players.remove(serverPlayerEntity);
        serverPlayerEntity.getServerWorld().removePlayer(serverPlayerEntity);
        final BlockPos blockPos4 = serverPlayerEntity.getSpawnPosition();
        final boolean boolean5 = serverPlayerEntity.isSpawnForced();
        serverPlayerEntity.dimension = dimensionType;
        ServerPlayerInteractionManager serverPlayerInteractionManager6;
        if (this.server.isDemo()) {
            serverPlayerInteractionManager6 = new DemoServerPlayerInteractionManager(this.server.getWorld(serverPlayerEntity.dimension));
        }
        else {
            serverPlayerInteractionManager6 = new ServerPlayerInteractionManager(this.server.getWorld(serverPlayerEntity.dimension));
        }
        final ServerPlayerEntity serverPlayerEntity2 = new ServerPlayerEntity(this.server, this.server.getWorld(serverPlayerEntity.dimension), serverPlayerEntity.getGameProfile(), serverPlayerInteractionManager6);
        serverPlayerEntity2.networkHandler = serverPlayerEntity.networkHandler;
        serverPlayerEntity2.copyFrom(serverPlayerEntity, alive);
        serverPlayerEntity2.setEntityId(serverPlayerEntity.getEntityId());
        serverPlayerEntity2.setMainHand(serverPlayerEntity.getMainHand());
        for (final String string9 : serverPlayerEntity.getScoreboardTags()) {
            serverPlayerEntity2.addScoreboardTag(string9);
        }
        final ServerWorld serverWorld8 = this.server.getWorld(serverPlayerEntity.dimension);
        this.setGameMode(serverPlayerEntity2, serverPlayerEntity, serverWorld8);
        if (blockPos4 != null) {
            final Optional<Vec3d> optional9 = PlayerEntity.a(this.server.getWorld(serverPlayerEntity.dimension), blockPos4, boolean5);
            if (optional9.isPresent()) {
                final Vec3d vec3d10 = optional9.get();
                serverPlayerEntity2.setPositionAndAngles(vec3d10.x, vec3d10.y, vec3d10.z, 0.0f, 0.0f);
                serverPlayerEntity2.setPlayerSpawn(blockPos4, boolean5);
            }
            else {
                serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0f));
            }
        }
        while (!serverWorld8.doesNotCollide(serverPlayerEntity2) && serverPlayerEntity2.y < 256.0) {
            serverPlayerEntity2.setPosition(serverPlayerEntity2.x, serverPlayerEntity2.y + 1.0, serverPlayerEntity2.z);
        }
        final LevelProperties levelProperties9 = serverPlayerEntity2.world.getLevelProperties();
        serverPlayerEntity2.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayerEntity2.dimension, levelProperties9.getGeneratorType(), serverPlayerEntity2.interactionManager.getGameMode()));
        final BlockPos blockPos5 = serverWorld8.getSpawnPos();
        serverPlayerEntity2.networkHandler.requestTeleport(serverPlayerEntity2.x, serverPlayerEntity2.y, serverPlayerEntity2.z, serverPlayerEntity2.yaw, serverPlayerEntity2.pitch);
        serverPlayerEntity2.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos5));
        serverPlayerEntity2.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties9.getDifficulty(), levelProperties9.isDifficultyLocked()));
        serverPlayerEntity2.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity2.experienceLevelProgress, serverPlayerEntity2.experienceLevel, serverPlayerEntity2.experience));
        this.sendWorldInfo(serverPlayerEntity2, serverWorld8);
        this.sendCommandTree(serverPlayerEntity2);
        serverWorld8.d(serverPlayerEntity2);
        this.players.add(serverPlayerEntity2);
        this.playerMap.put(serverPlayerEntity2.getUuid(), serverPlayerEntity2);
        serverPlayerEntity2.s_();
        serverPlayerEntity2.setHealth(serverPlayerEntity2.getHealth());
        return serverPlayerEntity2;
    }
    
    public void sendCommandTree(final ServerPlayerEntity player) {
        final GameProfile gameProfile2 = player.getGameProfile();
        final int integer3 = this.server.getPermissionLevel(gameProfile2);
        this.sendCommandTree(player, integer3);
    }
    
    public void updatePlayerLatency() {
        if (++this.latencyUpdateTimer > 600) {
            this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
            this.latencyUpdateTimer = 0;
        }
    }
    
    public void sendToAll(final Packet<?> packet) {
        for (int integer2 = 0; integer2 < this.players.size(); ++integer2) {
            this.players.get(integer2).networkHandler.sendPacket(packet);
        }
    }
    
    public void sendToDimension(final Packet<?> packet, final DimensionType dimensionType) {
        for (int integer3 = 0; integer3 < this.players.size(); ++integer3) {
            final ServerPlayerEntity serverPlayerEntity4 = this.players.get(integer3);
            if (serverPlayerEntity4.dimension == dimensionType) {
                serverPlayerEntity4.networkHandler.sendPacket(packet);
            }
        }
    }
    
    public void sendToTeam(final PlayerEntity source, final TextComponent textComponent) {
        final AbstractTeam abstractTeam3 = source.getScoreboardTeam();
        if (abstractTeam3 == null) {
            return;
        }
        final Collection<String> collection4 = abstractTeam3.getPlayerList();
        for (final String string6 : collection4) {
            final ServerPlayerEntity serverPlayerEntity7 = this.getPlayer(string6);
            if (serverPlayerEntity7 != null) {
                if (serverPlayerEntity7 == source) {
                    continue;
                }
                serverPlayerEntity7.sendMessage(textComponent);
            }
        }
    }
    
    public void sendToOtherTeams(final PlayerEntity source, final TextComponent textComponent) {
        final AbstractTeam abstractTeam3 = source.getScoreboardTeam();
        if (abstractTeam3 == null) {
            this.sendToAll(textComponent);
            return;
        }
        for (int integer4 = 0; integer4 < this.players.size(); ++integer4) {
            final ServerPlayerEntity serverPlayerEntity5 = this.players.get(integer4);
            if (serverPlayerEntity5.getScoreboardTeam() != abstractTeam3) {
                serverPlayerEntity5.sendMessage(textComponent);
            }
        }
    }
    
    public String[] getPlayerNames() {
        final String[] arr1 = new String[this.players.size()];
        for (int integer2 = 0; integer2 < this.players.size(); ++integer2) {
            arr1[integer2] = this.players.get(integer2).getGameProfile().getName();
        }
        return arr1;
    }
    
    public BannedPlayerList getUserBanList() {
        return this.bannedProfiles;
    }
    
    public BannedIpList getIpBanList() {
        return this.bannedIps;
    }
    
    public void addToOperators(final GameProfile gameProfile) {
        ((ServerConfigList<K, OperatorEntry>)this.ops).add(new OperatorEntry(gameProfile, this.server.getOpPermissionLevel(), this.ops.isOp(gameProfile)));
        final ServerPlayerEntity serverPlayerEntity2 = this.getPlayer(gameProfile.getId());
        if (serverPlayerEntity2 != null) {
            this.sendCommandTree(serverPlayerEntity2);
        }
    }
    
    public void removeFromOperators(final GameProfile gameProfile) {
        ((ServerConfigList<GameProfile, V>)this.ops).remove(gameProfile);
        final ServerPlayerEntity serverPlayerEntity2 = this.getPlayer(gameProfile.getId());
        if (serverPlayerEntity2 != null) {
            this.sendCommandTree(serverPlayerEntity2);
        }
    }
    
    private void sendCommandTree(final ServerPlayerEntity player, final int permissionLevel) {
        if (player.networkHandler != null) {
            byte byte3;
            if (permissionLevel <= 0) {
                byte3 = 24;
            }
            else if (permissionLevel >= 4) {
                byte3 = 28;
            }
            else {
                byte3 = (byte)(24 + permissionLevel);
            }
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, byte3));
        }
        this.server.getCommandManager().sendCommandTree(player);
    }
    
    public boolean isWhitelisted(final GameProfile gameProfile) {
        return !this.whitelistEnabled || ((ServerConfigList<GameProfile, V>)this.ops).contains(gameProfile) || ((ServerConfigList<GameProfile, V>)this.whitelist).contains(gameProfile);
    }
    
    public boolean isOperator(final GameProfile gameProfile) {
        return ((ServerConfigList<GameProfile, V>)this.ops).contains(gameProfile) || (this.server.isOwner(gameProfile) && this.server.getWorld(DimensionType.a).getLevelProperties().areCommandsAllowed()) || this.cheatsAllowed;
    }
    
    @Nullable
    public ServerPlayerEntity getPlayer(final String string) {
        for (final ServerPlayerEntity serverPlayerEntity3 : this.players) {
            if (serverPlayerEntity3.getGameProfile().getName().equalsIgnoreCase(string)) {
                return serverPlayerEntity3;
            }
        }
        return null;
    }
    
    public void sendToAround(@Nullable final PlayerEntity playerEntity, final double x, final double y, final double z, final double double8, final DimensionType dimensionType10, final Packet<?> packet11) {
        for (int integer12 = 0; integer12 < this.players.size(); ++integer12) {
            final ServerPlayerEntity serverPlayerEntity13 = this.players.get(integer12);
            if (serverPlayerEntity13 != playerEntity) {
                if (serverPlayerEntity13.dimension == dimensionType10) {
                    final double double9 = x - serverPlayerEntity13.x;
                    final double double10 = y - serverPlayerEntity13.y;
                    final double double11 = z - serverPlayerEntity13.z;
                    if (double9 * double9 + double10 * double10 + double11 * double11 < double8 * double8) {
                        serverPlayerEntity13.networkHandler.sendPacket(packet11);
                    }
                }
            }
        }
    }
    
    public void saveAllPlayerData() {
        for (int integer1 = 0; integer1 < this.players.size(); ++integer1) {
            this.savePlayerData(this.players.get(integer1));
        }
    }
    
    public Whitelist getWhitelist() {
        return this.whitelist;
    }
    
    public String[] getWhitelistedNames() {
        return this.whitelist.getNames();
    }
    
    public OperatorList getOpList() {
        return this.ops;
    }
    
    public String[] getOpNames() {
        return this.ops.getNames();
    }
    
    public void reloadWhitelist() {
    }
    
    public void sendWorldInfo(final ServerPlayerEntity player, final ServerWorld world) {
        final WorldBorder worldBorder3 = this.server.getWorld(DimensionType.a).getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder3, WorldBorderS2CPacket.Type.INITIALIZE));
        player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean("doDaylightCycle")));
        final BlockPos blockPos4 = world.getSpawnPos();
        player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos4));
        if (world.isRaining()) {
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0f));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, world.getRainGradient(1.0f)));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, world.getThunderGradient(1.0f)));
        }
    }
    
    public void e(final ServerPlayerEntity player) {
        player.openContainer(player.playerContainer);
        player.p();
        player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
    }
    
    public int getCurrentPlayerCount() {
        return this.players.size();
    }
    
    public int getMaxPlayerCount() {
        return this.maxPlayers;
    }
    
    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }
    
    public void setWhitelistEnabled(final boolean boolean1) {
        this.whitelistEnabled = boolean1;
    }
    
    public List<ServerPlayerEntity> getPlayersByIp(final String string) {
        final List<ServerPlayerEntity> list2 = Lists.newArrayList();
        for (final ServerPlayerEntity serverPlayerEntity4 : this.players) {
            if (serverPlayerEntity4.getServerBrand().equals(string)) {
                list2.add(serverPlayerEntity4);
            }
        }
        return list2;
    }
    
    public int getViewDistance() {
        return this.viewDistance;
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public CompoundTag getUserData() {
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    public void setGameMode(final GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    private void setGameMode(final ServerPlayerEntity player, final ServerPlayerEntity oldPlayer, final IWorld world) {
        if (oldPlayer != null) {
            player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode());
        }
        else if (this.gameMode != null) {
            player.interactionManager.setGameMode(this.gameMode);
        }
        player.interactionManager.setGameModeIfNotPresent(world.getLevelProperties().getGameMode());
    }
    
    @Environment(EnvType.CLIENT)
    public void setCheatsAllowed(final boolean boolean1) {
        this.cheatsAllowed = boolean1;
    }
    
    public void disconnectAllPlayers() {
        for (int integer1 = 0; integer1 < this.players.size(); ++integer1) {
            this.players.get(integer1).networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.server_shutdown", new Object[0]));
        }
    }
    
    public void broadcastChatMessage(final TextComponent textComponent, final boolean boolean2) {
        this.server.sendMessage(textComponent);
        final ChatMessageType chatMessageType3 = boolean2 ? ChatMessageType.b : ChatMessageType.a;
        this.sendToAll(new ChatMessageS2CPacket(textComponent, chatMessageType3));
    }
    
    public void sendToAll(final TextComponent textComponent) {
        this.broadcastChatMessage(textComponent, true);
    }
    
    public ServerStatHandler createStatHandler(final PlayerEntity player) {
        final UUID uUID2 = player.getUuid();
        ServerStatHandler serverStatHandler3 = (uUID2 == null) ? null : this.statisticsMap.get(uUID2);
        if (serverStatHandler3 == null) {
            final File file4 = new File(this.server.getWorld(DimensionType.a).getSaveHandler().getWorldDir(), "stats");
            final File file5 = new File(file4, uUID2 + ".json");
            if (!file5.exists()) {
                final File file6 = new File(file4, player.getName().getString() + ".json");
                if (file6.exists() && file6.isFile()) {
                    file6.renameTo(file5);
                }
            }
            serverStatHandler3 = new ServerStatHandler(this.server, file5);
            this.statisticsMap.put(uUID2, serverStatHandler3);
        }
        return serverStatHandler3;
    }
    
    public PlayerAdvancementTracker getAdvancementManager(final ServerPlayerEntity serverPlayerEntity) {
        final UUID uUID2 = serverPlayerEntity.getUuid();
        PlayerAdvancementTracker playerAdvancementTracker3 = this.advancementManagerMap.get(uUID2);
        if (playerAdvancementTracker3 == null) {
            final File file4 = new File(this.server.getWorld(DimensionType.a).getSaveHandler().getWorldDir(), "advancements");
            final File file5 = new File(file4, uUID2 + ".json");
            playerAdvancementTracker3 = new PlayerAdvancementTracker(this.server, file5, serverPlayerEntity);
            this.advancementManagerMap.put(uUID2, playerAdvancementTracker3);
        }
        playerAdvancementTracker3.setOwner(serverPlayerEntity);
        return playerAdvancementTracker3;
    }
    
    public void setViewDistance(final int integer1, final int integer2) {
        this.viewDistance = integer1;
        this.sendToAll(new ChunkLoadDistanceS2CPacket(integer1));
        for (final ServerWorld serverWorld4 : this.server.getWorlds()) {
            if (serverWorld4 != null) {
                serverWorld4.getChunkManager().applyViewDistance(integer1, integer2);
            }
        }
    }
    
    public List<ServerPlayerEntity> getPlayerList() {
        return this.players;
    }
    
    @Nullable
    public ServerPlayerEntity getPlayer(final UUID uUID) {
        return this.playerMap.get(uUID);
    }
    
    public boolean canBypassPlayerLimit(final GameProfile gameProfile) {
        return false;
    }
    
    public void onDataPacksReloaded() {
        for (final PlayerAdvancementTracker playerAdvancementTracker2 : this.advancementManagerMap.values()) {
            playerAdvancementTracker2.reload();
        }
        this.sendToAll(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        final SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket1 = new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values());
        for (final ServerPlayerEntity serverPlayerEntity3 : this.players) {
            serverPlayerEntity3.networkHandler.sendPacket(synchronizeRecipesS2CPacket1);
            serverPlayerEntity3.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity3);
        }
    }
    
    public boolean areCheatsAllowed() {
        return this.cheatsAllowed;
    }
    
    static {
        BANNED_PLAYERS_FILE = new File("banned-players.json");
        BANNED_IPS_FILE = new File("banned-ips.json");
        OPERATORS_FILE = new File("ops.json");
        WHITELIST_FILE = new File("whitelist.json");
        LOGGER = LogManager.getLogger();
        DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    }
}
