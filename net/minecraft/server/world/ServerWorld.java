package net.minecraft.server.world;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.dimension.Dimension;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.raid.Raid;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.village.PointOfInterestStorage;
import java.util.Optional;
import net.minecraft.client.network.DebugRendererInfoManager;
import java.util.Objects;
import net.minecraft.village.PointOfInterestType;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.world.ForcedChunkState;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.Void;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.PersistentState;
import net.minecraft.item.map.MapState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.tag.TagManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.math.Position;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.structure.StructureManager;
import javax.annotation.Nonnull;
import java.io.IOException;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.damage.DamageSource;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.world.BlockView;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.block.entity.BlockEntity;
import java.util.Collection;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.entity.mob.MobEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.EntityCategory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import java.util.function.Predicate;
import net.minecraft.world.SessionLockException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.tag.BlockTags;
import java.util.Random;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.ScheduledTick;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.ServerScoreboard;
import java.util.Iterator;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.world.chunk.WorldChunk;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.Npc;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.util.math.MathHelper;
import java.util.function.BooleanSupplier;
import com.google.common.collect.Sets;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Queues;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import com.google.common.collect.Lists;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.world.WanderingTraderManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import java.util.Set;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Queue;
import java.util.UUID;
import java.util.Map;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.World;

public class ServerWorld extends World
{
    private static final Logger LOGGER;
    private final List<Entity> globalEntities;
    private final Int2ObjectMap<Entity> entitiesById;
    private final Map<UUID, Entity> entitiesByUuid;
    private final Queue<Entity> entitiesToLoad;
    private final List<ServerPlayerEntity> players;
    boolean ticking;
    private final MinecraftServer server;
    private final WorldSaveHandler worldSaveHandler;
    public boolean savingDisabled;
    private boolean allPlayersSleeping;
    private int idleTimeout;
    private final PortalForcer portalForcer;
    private final ServerTickScheduler<Block> blockTickScheduler;
    private final ServerTickScheduler<Fluid> fluidTickScheduler;
    private final Set<EntityNavigation> entityNavigations;
    protected final RaidManager raidManager;
    protected final ZombieSiegeManager siegeManager;
    private final ObjectLinkedOpenHashSet<BlockAction> pendingBlockActions;
    private boolean insideTick;
    @Nullable
    private final WanderingTraderManager wanderingTraderManager;
    
    public ServerWorld(final MinecraftServer server, final Executor workerExecutor, final WorldSaveHandler worldSaveHandler, final LevelProperties properties, final DimensionType dimensionType, final Profiler profiler, final WorldGenerationProgressListener worldGenerationProgressListener) {
        super(properties, dimensionType, (world, dimension) -> new ServerChunkManager(world, worldSaveHandler.getWorldDir(), worldSaveHandler.getDataFixer(), worldSaveHandler.getStructureManager(), workerExecutor, dimension.createChunkGenerator(), server.getPlayerManager().getViewDistance(), server.getPlayerManager().getViewDistance() - 2, worldGenerationProgressListener, () -> server.getWorld(DimensionType.a).getPersistentStateManager()), profiler, false);
        this.globalEntities = Lists.newArrayList();
        this.entitiesById = (Int2ObjectMap<Entity>)new Int2ObjectLinkedOpenHashMap();
        this.entitiesByUuid = Maps.newHashMap();
        this.entitiesToLoad = Queues.newArrayDeque();
        this.players = Lists.newArrayList();
        this.blockTickScheduler = new ServerTickScheduler<Block>(this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::tickBlock);
        this.fluidTickScheduler = new ServerTickScheduler<Fluid>(this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this::tickFluid);
        this.entityNavigations = Sets.newHashSet();
        this.siegeManager = new ZombieSiegeManager(this);
        this.pendingBlockActions = (ObjectLinkedOpenHashSet<BlockAction>)new ObjectLinkedOpenHashSet();
        this.worldSaveHandler = worldSaveHandler;
        this.server = server;
        this.portalForcer = new PortalForcer(this);
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
        this.getWorldBorder().setMaxWorldBorderRadius(server.getMaxWorldBorderRadius());
        this.raidManager = this.getPersistentStateManager().<RaidManager>getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.dimension));
        if (!server.isSinglePlayer()) {
            this.getLevelProperties().setGameMode(server.getDefaultGameMode());
        }
        this.wanderingTraderManager = ((this.dimension.getType() == DimensionType.a) ? new WanderingTraderManager(this) : null);
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        final Profiler profiler2 = this.getProfiler();
        this.insideTick = true;
        profiler2.push("world border");
        this.getWorldBorder().tick();
        profiler2.swap("weather");
        final boolean boolean3 = this.isRaining();
        if (this.dimension.hasSkyLight()) {
            if (this.getGameRules().getBoolean("doWeatherCycle")) {
                int integer4 = this.properties.getClearWeatherTime();
                int integer5 = this.properties.getThunderTime();
                int integer6 = this.properties.getRainTime();
                boolean boolean4 = this.properties.isThundering();
                boolean boolean5 = this.properties.isRaining();
                if (integer4 > 0) {
                    --integer4;
                    integer5 = (boolean4 ? 0 : 1);
                    integer6 = (boolean5 ? 0 : 1);
                    boolean4 = false;
                    boolean5 = false;
                }
                else {
                    if (integer5 > 0) {
                        if (--integer5 == 0) {
                            boolean4 = !boolean4;
                        }
                    }
                    else if (boolean4) {
                        integer5 = this.random.nextInt(12000) + 3600;
                    }
                    else {
                        integer5 = this.random.nextInt(168000) + 12000;
                    }
                    if (integer6 > 0) {
                        if (--integer6 == 0) {
                            boolean5 = !boolean5;
                        }
                    }
                    else if (boolean5) {
                        integer6 = this.random.nextInt(12000) + 12000;
                    }
                    else {
                        integer6 = this.random.nextInt(168000) + 12000;
                    }
                }
                this.properties.setThunderTime(integer5);
                this.properties.setRainTime(integer6);
                this.properties.setClearWeatherTime(integer4);
                this.properties.setThundering(boolean4);
                this.properties.setRaining(boolean5);
            }
            this.thunderGradientPrev = this.thunderGradient;
            if (this.properties.isThundering()) {
                this.thunderGradient += (float)0.01;
            }
            else {
                this.thunderGradient -= (float)0.01;
            }
            this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0f, 1.0f);
            this.rainGradientPrev = this.rainGradient;
            if (this.properties.isRaining()) {
                this.rainGradient += (float)0.01;
            }
            else {
                this.rainGradient -= (float)0.01;
            }
            this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0f, 1.0f);
        }
        if (this.rainGradientPrev != this.rainGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.getType());
        }
        if (this.thunderGradientPrev != this.thunderGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.getType());
        }
        if (boolean3 != this.isRaining()) {
            if (boolean3) {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0f));
            }
            else {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0f));
            }
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
        }
        if (this.getLevelProperties().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
            this.getLevelProperties().setDifficulty(Difficulty.HARD);
        }
        if (this.allPlayersSleeping && this.players.stream().noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough())) {
            this.allPlayersSleeping = false;
            if (this.getGameRules().getBoolean("doDaylightCycle")) {
                final long long4 = this.properties.getTimeOfDay() + 24000L;
                this.setTimeOfDay(long4 - long4 % 24000L);
            }
            this.players.stream().filter(LivingEntity::isSleeping).forEach(serverPlayerEntity -> serverPlayerEntity.wakeUp(false, false, true));
            if (this.getGameRules().getBoolean("doWeatherCycle")) {
                this.resetWeather();
            }
        }
        this.calculateAmbientDarkness();
        this.tickTime();
        profiler2.swap("chunkSource");
        this.getChunkManager().tick(booleanSupplier);
        profiler2.swap("tickPending");
        if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.blockTickScheduler.tick();
            this.fluidTickScheduler.tick();
        }
        profiler2.swap("village");
        this.siegeManager.tick();
        profiler2.swap("portalForcer");
        this.portalForcer.tick(this.getTime());
        profiler2.swap("raid");
        this.raidManager.tick();
        if (this.wanderingTraderManager != null) {
            this.wanderingTraderManager.tick();
        }
        profiler2.swap("blockEvents");
        this.sendBlockActions();
        this.insideTick = false;
        profiler2.swap("entities");
        final boolean boolean6 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if (boolean6) {
            this.resetIdleTimeout();
        }
        if (boolean6 || this.idleTimeout++ < 300) {
            this.dimension.update();
            profiler2.push("global");
            for (int integer5 = 0; integer5 < this.globalEntities.size(); ++integer5) {
                final Entity entity2 = this.globalEntities.get(integer5);
                this.tickEntity(entity -> {
                    ++entity.age;
                    entity.tick();
                    return;
                }, entity2);
                if (entity2.removed) {
                    this.globalEntities.remove(integer5--);
                }
            }
            profiler2.swap("regular");
            this.ticking = true;
            final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator5 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.entitiesById.int2ObjectEntrySet().iterator();
            while (objectIterator5.hasNext()) {
                final Int2ObjectMap.Entry<Entity> entry6 = (Int2ObjectMap.Entry<Entity>)objectIterator5.next();
                final Entity entity3 = (Entity)entry6.getValue();
                final Entity entity4 = entity3.getVehicle();
                if (!this.server.shouldSpawnAnimals() && (entity3 instanceof AnimalEntity || entity3 instanceof WaterCreatureEntity)) {
                    entity3.remove();
                }
                if (!this.server.shouldSpawnNpcs() && entity3 instanceof Npc) {
                    entity3.remove();
                }
                if (entity4 != null) {
                    if (!entity4.removed && entity4.hasPassenger(entity3)) {
                        continue;
                    }
                    entity3.stopRiding();
                }
                profiler2.push("tick");
                if (!entity3.removed && !(entity3 instanceof EnderDragonPart)) {
                    this.tickEntity(this::tickEntity, entity3);
                }
                profiler2.pop();
                profiler2.push("remove");
                if (entity3.removed) {
                    this.removeEntityFromChunk(entity3);
                    objectIterator5.remove();
                    this.unloadEntity(entity3);
                }
                profiler2.pop();
            }
            this.ticking = false;
            Entity entity2;
            while ((entity2 = this.entitiesToLoad.poll()) != null) {
                this.loadEntityUnchecked(entity2);
            }
            profiler2.pop();
            this.tickBlockEntities();
        }
        profiler2.pop();
    }
    
    public void tickChunk(final WorldChunk chunk, final int randomTickSpeed) {
        final ChunkPos chunkPos3 = chunk.getPos();
        final boolean boolean4 = this.isRaining();
        final int integer5 = chunkPos3.getStartX();
        final int integer6 = chunkPos3.getStartZ();
        final Profiler profiler7 = this.getProfiler();
        profiler7.push("thunder");
        if (boolean4 && this.isThundering() && this.random.nextInt(100000) == 0) {
            final BlockPos blockPos8 = this.a(this.getRandomPosInChunk(integer5, 0, integer6, 15));
            if (this.hasRain(blockPos8)) {
                final LocalDifficulty localDifficulty9 = this.getLocalDifficulty(blockPos8);
                final boolean boolean5 = this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < localDifficulty9.getLocalDifficulty() * 0.01;
                if (boolean5) {
                    final SkeletonHorseEntity skeletonHorseEntity11 = EntityType.SKELETON_HORSE.create(this);
                    skeletonHorseEntity11.r(true);
                    skeletonHorseEntity11.setBreedingAge(0);
                    skeletonHorseEntity11.setPosition(blockPos8.getX(), blockPos8.getY(), blockPos8.getZ());
                    this.spawnEntity(skeletonHorseEntity11);
                }
                this.addLightning(new LightningEntity(this, blockPos8.getX() + 0.5, blockPos8.getY(), blockPos8.getZ() + 0.5, boolean5));
            }
        }
        profiler7.swap("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            final BlockPos blockPos8 = this.getTopPosition(Heightmap.Type.e, this.getRandomPosInChunk(integer5, 0, integer6, 15));
            final BlockPos blockPos9 = blockPos8.down();
            final Biome biome10 = this.getBiome(blockPos8);
            if (biome10.canSetSnow(this, blockPos9)) {
                this.setBlockState(blockPos9, Blocks.cB.getDefaultState());
            }
            if (boolean4 && biome10.canSetIce(this, blockPos8)) {
                this.setBlockState(blockPos8, Blocks.cA.getDefaultState());
            }
            if (boolean4 && this.getBiome(blockPos9).getPrecipitation() == Biome.Precipitation.RAIN) {
                this.getBlockState(blockPos9).getBlock().onRainTick(this, blockPos9);
            }
        }
        profiler7.swap("tickBlocks");
        if (randomTickSpeed > 0) {
            for (final ChunkSection chunkSection11 : chunk.getSectionArray()) {
                if (chunkSection11 != WorldChunk.EMPTY_SECTION && chunkSection11.hasRandomTicks()) {
                    final int integer7 = chunkSection11.getYOffset();
                    for (int integer8 = 0; integer8 < randomTickSpeed; ++integer8) {
                        final BlockPos blockPos10 = this.getRandomPosInChunk(integer5, integer7, integer6, 15);
                        profiler7.push("randomTick");
                        final BlockState blockState15 = chunkSection11.getBlockState(blockPos10.getX() - integer5, blockPos10.getY() - integer7, blockPos10.getZ() - integer6);
                        if (blockState15.hasRandomTicks()) {
                            blockState15.onRandomTick(this, blockPos10, this.random);
                        }
                        final FluidState fluidState16 = chunkSection11.getFluidState(blockPos10.getX() - integer5, blockPos10.getY() - integer7, blockPos10.getZ() - integer6);
                        if (fluidState16.hasRandomTicks()) {
                            fluidState16.onRandomTick(this, blockPos10, this.random);
                        }
                        profiler7.pop();
                    }
                }
            }
        }
        profiler7.pop();
    }
    
    protected BlockPos a(final BlockPos blockPos) {
        BlockPos blockPos2 = this.getTopPosition(Heightmap.Type.e, blockPos);
        final BoundingBox boundingBox3 = new BoundingBox(blockPos2, new BlockPos(blockPos2.getX(), this.getHeight(), blockPos2.getZ())).expand(3.0);
        final List<LivingEntity> list4 = this.<LivingEntity>getEntities(LivingEntity.class, boundingBox3, livingEntity -> livingEntity != null && livingEntity.isAlive() && this.isSkyVisible(livingEntity.getBlockPos()));
        if (!list4.isEmpty()) {
            return list4.get(this.random.nextInt(list4.size())).getBlockPos();
        }
        if (blockPos2.getY() == -1) {
            blockPos2 = blockPos2.up(2);
        }
        return blockPos2;
    }
    
    public boolean isInsideTick() {
        return this.insideTick;
    }
    
    public void updatePlayersSleeping() {
        this.allPlayersSleeping = false;
        if (!this.players.isEmpty()) {
            int integer1 = 0;
            int integer2 = 0;
            for (final ServerPlayerEntity serverPlayerEntity4 : this.players) {
                if (serverPlayerEntity4.isSpectator()) {
                    ++integer1;
                }
                else {
                    if (!serverPlayerEntity4.isSleeping()) {
                        continue;
                    }
                    ++integer2;
                }
            }
            this.allPlayersSleeping = (integer2 > 0 && integer2 >= this.players.size() - integer1);
        }
    }
    
    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }
    
    private void resetWeather() {
        this.properties.setRainTime(0);
        this.properties.setRaining(false);
        this.properties.setThunderTime(0);
        this.properties.setThundering(false);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setDefaultSpawnClient() {
        if (this.properties.getSpawnY() <= 0) {
            this.properties.setSpawnY(this.getSeaLevel() + 1);
        }
        int integer1 = this.properties.getSpawnX();
        int integer2 = this.properties.getSpawnZ();
        int integer3 = 0;
        while (this.getTopNonAirState(new BlockPos(integer1, 0, integer2)).isAir()) {
            integer1 += this.random.nextInt(8) - this.random.nextInt(8);
            integer2 += this.random.nextInt(8) - this.random.nextInt(8);
            if (++integer3 == 10000) {
                break;
            }
        }
        this.properties.setSpawnX(integer1);
        this.properties.setSpawnZ(integer2);
    }
    
    public void resetIdleTimeout() {
        this.idleTimeout = 0;
    }
    
    private void tickFluid(final ScheduledTick<Fluid> tick) {
        final FluidState fluidState2 = this.getFluidState(tick.pos);
        if (fluidState2.getFluid() == tick.getObject()) {
            fluidState2.onScheduledTick(this, tick.pos);
        }
    }
    
    private void tickBlock(final ScheduledTick<Block> tick) {
        final BlockState blockState2 = this.getBlockState(tick.pos);
        if (blockState2.getBlock() == tick.getObject()) {
            blockState2.scheduledTick(this, tick.pos, this.random);
        }
    }
    
    public void tickEntity(final Entity entity) {
        if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
            return;
        }
        entity.prevRenderX = entity.x;
        entity.prevRenderY = entity.y;
        entity.prevRenderZ = entity.z;
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
        if (entity.Y) {
            ++entity.age;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString());
            entity.tick();
            this.getProfiler().pop();
        }
        this.checkChunk(entity);
        if (entity.Y) {
            for (final Entity entity2 : entity.getPassengerList()) {
                this.a(entity, entity2);
            }
        }
    }
    
    public void a(final Entity entity1, final Entity entity2) {
        if (entity2.removed || entity2.getVehicle() != entity1) {
            entity2.stopRiding();
            return;
        }
        if (!(entity2 instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity2)) {
            return;
        }
        entity2.prevRenderX = entity2.x;
        entity2.prevRenderY = entity2.y;
        entity2.prevRenderZ = entity2.z;
        entity2.prevYaw = entity2.yaw;
        entity2.prevPitch = entity2.pitch;
        if (entity2.Y) {
            ++entity2.age;
            entity2.tickRiding();
        }
        this.checkChunk(entity2);
        if (entity2.Y) {
            for (final Entity entity3 : entity2.getPassengerList()) {
                this.a(entity2, entity3);
            }
        }
    }
    
    public void checkChunk(final Entity entity) {
        this.getProfiler().push("chunkCheck");
        final int integer2 = MathHelper.floor(entity.x / 16.0);
        final int integer3 = MathHelper.floor(entity.y / 16.0);
        final int integer4 = MathHelper.floor(entity.z / 16.0);
        if (!entity.Y || entity.chunkX != integer2 || entity.chunkY != integer3 || entity.chunkZ != integer4) {
            if (entity.Y && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
                this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
            }
            if (entity.bT() || this.isChunkLoaded(integer2, integer4)) {
                this.getChunk(integer2, integer4).addEntity(entity);
            }
            else {
                entity.Y = false;
            }
        }
        this.getProfiler().pop();
    }
    
    @Override
    public boolean canPlayerModifyAt(final PlayerEntity player, final BlockPos blockPos) {
        return !this.server.isSpawnProtected(this, blockPos, player) && this.getWorldBorder().contains(blockPos);
    }
    
    public void init(final LevelInfo levelInfo) {
        if (!this.dimension.canPlayersSleep()) {
            this.properties.setSpawnPos(BlockPos.ORIGIN.up(this.chunkManager.getChunkGenerator().getSpawnHeight()));
            return;
        }
        if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.properties.setSpawnPos(BlockPos.ORIGIN.up());
            return;
        }
        final BiomeSource biomeSource2 = this.chunkManager.getChunkGenerator().getBiomeSource();
        final List<Biome> list3 = biomeSource2.getSpawnBiomes();
        final Random random4 = new Random(this.getSeed());
        final BlockPos blockPos5 = biomeSource2.locateBiome(0, 0, 256, list3, random4);
        final ChunkPos chunkPos6 = (blockPos5 == null) ? new ChunkPos(0, 0) : new ChunkPos(blockPos5);
        if (blockPos5 == null) {
            ServerWorld.LOGGER.warn("Unable to find spawn biome");
        }
        boolean boolean7 = false;
        for (final Block block9 : BlockTags.K.values()) {
            if (biomeSource2.getTopMaterials().contains(block9.getDefaultState())) {
                boolean7 = true;
                break;
            }
        }
        this.properties.setSpawnPos(chunkPos6.getCenterBlockPos().add(8, this.chunkManager.getChunkGenerator().getSpawnHeight(), 8));
        int integer8 = 0;
        int integer9 = 0;
        int integer10 = 0;
        int integer11 = -1;
        final int integer12 = 32;
        for (int integer13 = 0; integer13 < 1024; ++integer13) {
            if (integer8 > -16 && integer8 <= 16 && integer9 > -16 && integer9 <= 16) {
                final BlockPos blockPos6 = this.dimension.getSpawningBlockInChunk(new ChunkPos(chunkPos6.x + integer8, chunkPos6.z + integer9), boolean7);
                if (blockPos6 != null) {
                    this.properties.setSpawnPos(blockPos6);
                    break;
                }
            }
            if (integer8 == integer9 || (integer8 < 0 && integer8 == -integer9) || (integer8 > 0 && integer8 == 1 - integer9)) {
                final int integer14 = integer10;
                integer10 = -integer11;
                integer11 = integer14;
            }
            integer8 += integer10;
            integer9 += integer11;
        }
        if (levelInfo.hasBonusChest()) {
            this.placeBonusChest();
        }
    }
    
    protected void placeBonusChest() {
        final BonusChestFeature bonusChestFeature1 = Feature.BONUS_CHEST;
        for (int integer2 = 0; integer2 < 10; ++integer2) {
            final int integer3 = this.properties.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
            final int integer4 = this.properties.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
            final BlockPos blockPos5 = this.getTopPosition(Heightmap.Type.f, new BlockPos(integer3, 0, integer4)).up();
            if (bonusChestFeature1.generate(this, this.chunkManager.getChunkGenerator(), this.random, blockPos5, FeatureConfig.DEFAULT)) {
                break;
            }
        }
    }
    
    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return this.dimension.getForcedSpawnPoint();
    }
    
    public void save(@Nullable final ProgressListener progressListener, final boolean boolean2, final boolean boolean3) throws SessionLockException {
        final ServerChunkManager serverChunkManager4 = this.getChunkManager();
        if (boolean3) {
            return;
        }
        if (progressListener != null) {
            progressListener.a(new TranslatableTextComponent("menu.savingLevel", new Object[0]));
        }
        this.saveLevel();
        if (progressListener != null) {
            progressListener.c(new TranslatableTextComponent("menu.savingChunks", new Object[0]));
        }
        serverChunkManager4.save(boolean2);
    }
    
    protected void saveLevel() throws SessionLockException {
        this.checkSessionLock();
        this.dimension.saveWorldData();
        this.getChunkManager().getPersistentStateManager().save();
    }
    
    public List<Entity> getEntities(@Nullable final EntityType<?> entityType, final Predicate<? super Entity> predicate) {
        final List<Entity> list3 = Lists.newArrayList();
        final ServerChunkManager serverChunkManager4 = this.getChunkManager();
        for (final Entity entity6 : this.entitiesById.values()) {
            if ((entityType == null || entity6.getType() == entityType) && serverChunkManager4.isChunkLoaded(MathHelper.floor(entity6.x) >> 4, MathHelper.floor(entity6.z) >> 4) && predicate.test(entity6)) {
                list3.add(entity6);
            }
        }
        return list3;
    }
    
    public List<EnderDragonEntity> getAliveEnderDragons() {
        final List<EnderDragonEntity> list1 = Lists.newArrayList();
        for (final Entity entity3 : this.entitiesById.values()) {
            if (entity3 instanceof EnderDragonEntity && entity3.isAlive()) {
                list1.add((EnderDragonEntity)entity3);
            }
        }
        return list1;
    }
    
    public List<ServerPlayerEntity> getPlayers(final Predicate<? super ServerPlayerEntity> predicate) {
        final List<ServerPlayerEntity> list2 = Lists.newArrayList();
        for (final ServerPlayerEntity serverPlayerEntity4 : this.players) {
            if (predicate.test(serverPlayerEntity4)) {
                list2.add(serverPlayerEntity4);
            }
        }
        return list2;
    }
    
    @Nullable
    public ServerPlayerEntity getRandomAlivePlayer() {
        final List<ServerPlayerEntity> list1 = this.getPlayers(LivingEntity::isAlive);
        if (list1.isEmpty()) {
            return null;
        }
        return list1.get(this.random.nextInt(list1.size()));
    }
    
    public Object2IntMap<EntityCategory> getMobCountsByCategory() {
        final Object2IntMap<EntityCategory> object2IntMap1 = (Object2IntMap<EntityCategory>)new Object2IntOpenHashMap();
        for (final Entity entity3 : this.entitiesById.values()) {
            if (entity3 instanceof MobEntity && ((MobEntity)entity3).isPersistent()) {
                continue;
            }
            final EntityCategory entityCategory4 = entity3.getType().getCategory();
            if (entityCategory4 == EntityCategory.e) {
                continue;
            }
            object2IntMap1.computeInt(entityCategory4, (entityCategory, integer) -> 1 + ((integer == null) ? 0 : integer));
        }
        return object2IntMap1;
    }
    
    @Override
    public boolean spawnEntity(final Entity entity) {
        return this.addEntity(entity);
    }
    
    public boolean d(final Entity entity) {
        return this.addEntity(entity);
    }
    
    public void e(final Entity entity) {
        final boolean boolean2 = entity.teleporting;
        entity.teleporting = true;
        this.d(entity);
        entity.teleporting = boolean2;
        this.checkChunk(entity);
    }
    
    public void a(final ServerPlayerEntity serverPlayerEntity) {
        this.addPlayer(serverPlayerEntity);
        this.checkChunk(serverPlayerEntity);
    }
    
    public void b(final ServerPlayerEntity serverPlayerEntity) {
        this.addPlayer(serverPlayerEntity);
        this.checkChunk(serverPlayerEntity);
    }
    
    public void c(final ServerPlayerEntity serverPlayerEntity) {
        this.addPlayer(serverPlayerEntity);
    }
    
    public void d(final ServerPlayerEntity serverPlayerEntity) {
        this.addPlayer(serverPlayerEntity);
    }
    
    private void addPlayer(final ServerPlayerEntity player) {
        final Entity entity2 = this.entitiesByUuid.get(player.getUuid());
        if (entity2 != null) {
            ServerWorld.LOGGER.warn("Force-added player with duplicate UUID {}", player.getUuid().toString());
            entity2.detach();
            this.removePlayer((ServerPlayerEntity)entity2);
        }
        this.players.add(player);
        this.updatePlayersSleeping();
        final Chunk chunk3 = this.getChunk(MathHelper.floor(player.x / 16.0), MathHelper.floor(player.z / 16.0), ChunkStatus.FULL, true);
        if (chunk3 instanceof WorldChunk) {
            chunk3.addEntity(player);
        }
        this.loadEntityUnchecked(player);
    }
    
    private boolean addEntity(final Entity entity) {
        if (entity.removed) {
            ServerWorld.LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
            return false;
        }
        if (this.checkUuid(entity)) {
            return false;
        }
        final Chunk chunk2 = this.getChunk(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, entity.teleporting);
        if (!(chunk2 instanceof WorldChunk)) {
            return false;
        }
        chunk2.addEntity(entity);
        this.loadEntityUnchecked(entity);
        return true;
    }
    
    public boolean loadEntity(final Entity entity) {
        if (this.checkUuid(entity)) {
            return false;
        }
        this.loadEntityUnchecked(entity);
        return true;
    }
    
    private boolean checkUuid(final Entity entity) {
        final Entity entity2 = this.entitiesByUuid.get(entity.getUuid());
        if (entity2 == null) {
            return false;
        }
        ServerWorld.LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.getType()), entity.getUuid().toString());
        return true;
    }
    
    public void unloadEntities(final WorldChunk chunk) {
        this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
        for (final TypeFilterableList<Entity> typeFilterableList5 : chunk.getEntitySectionArray()) {
            for (final Entity entity7 : typeFilterableList5) {
                if (entity7 instanceof ServerPlayerEntity) {
                    continue;
                }
                if (this.ticking) {
                    throw new IllegalStateException("Removing entity while ticking!");
                }
                this.entitiesById.remove(entity7.getEntityId());
                this.unloadEntity(entity7);
            }
        }
    }
    
    public void unloadEntity(final Entity entity) {
        if (entity instanceof EnderDragonEntity) {
            for (final EnderDragonPart enderDragonPart5 : ((EnderDragonEntity)entity).dT()) {
                enderDragonPart5.remove();
            }
        }
        this.entitiesByUuid.remove(entity.getUuid());
        this.getChunkManager().unloadEntity(entity);
        if (entity instanceof ServerPlayerEntity) {
            final ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)entity;
            this.players.remove(serverPlayerEntity2);
        }
        this.getScoreboard().resetEntityScore(entity);
        if (entity instanceof MobEntity) {
            this.entityNavigations.remove(((MobEntity)entity).getNavigation());
        }
    }
    
    private void loadEntityUnchecked(final Entity entity) {
        if (this.ticking) {
            this.entitiesToLoad.add(entity);
        }
        else {
            this.entitiesById.put(entity.getEntityId(), entity);
            if (entity instanceof EnderDragonEntity) {
                for (final EnderDragonPart enderDragonPart5 : ((EnderDragonEntity)entity).dT()) {
                    this.entitiesById.put(enderDragonPart5.getEntityId(), enderDragonPart5);
                }
            }
            this.entitiesByUuid.put(entity.getUuid(), entity);
            this.getChunkManager().loadEntity(entity);
            if (entity instanceof MobEntity) {
                this.entityNavigations.add(((MobEntity)entity).getNavigation());
            }
        }
    }
    
    public void removeEntity(final Entity entity) {
        if (this.ticking) {
            throw new IllegalStateException("Removing entity while ticking!");
        }
        this.removeEntityFromChunk(entity);
        this.entitiesById.remove(entity.getEntityId());
        this.unloadEntity(entity);
    }
    
    private void removeEntityFromChunk(final Entity entity) {
        final Chunk chunk2 = this.getChunk(entity.chunkX, entity.chunkZ, ChunkStatus.FULL, false);
        if (chunk2 instanceof WorldChunk) {
            ((WorldChunk)chunk2).remove(entity);
        }
    }
    
    public void removePlayer(final ServerPlayerEntity player) {
        player.remove();
        this.updatePlayersSleeping();
        this.removeEntity(player);
    }
    
    public void addLightning(final LightningEntity lightningEntity) {
        this.globalEntities.add(lightningEntity);
        this.server.getPlayerManager().sendToAround(null, lightningEntity.x, lightningEntity.y, lightningEntity.z, 512.0, this.dimension.getType(), new EntitySpawnGlobalS2CPacket(lightningEntity));
    }
    
    @Override
    public void setBlockBreakingProgress(final int integer1, final BlockPos blockPos, final int integer3) {
        for (final ServerPlayerEntity serverPlayerEntity5 : this.server.getPlayerManager().getPlayerList()) {
            if (serverPlayerEntity5 != null && serverPlayerEntity5.world == this) {
                if (serverPlayerEntity5.getEntityId() == integer1) {
                    continue;
                }
                final double double6 = blockPos.getX() - serverPlayerEntity5.x;
                final double double7 = blockPos.getY() - serverPlayerEntity5.y;
                final double double8 = blockPos.getZ() - serverPlayerEntity5.z;
                if (double6 * double6 + double7 * double7 + double8 * double8 >= 1024.0) {
                    continue;
                }
                serverPlayerEntity5.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(integer1, blockPos, integer3));
            }
        }
    }
    
    @Override
    public void playSound(@Nullable final PlayerEntity player, final double x, final double y, final double z, final SoundEvent sound, final SoundCategory category, final float volume, final float pitch) {
        this.server.getPlayerManager().sendToAround(player, x, y, z, (volume > 1.0f) ? ((double)(16.0f * volume)) : 16.0, this.dimension.getType(), new PlaySoundS2CPacket(sound, category, x, y, z, volume, pitch));
    }
    
    @Override
    public void playSoundFromEntity(@Nullable final PlayerEntity playerEntity, final Entity entity, final SoundEvent soundEvent, final SoundCategory soundCategory, final float float5, final float float6) {
        this.server.getPlayerManager().sendToAround(playerEntity, entity.x, entity.y, entity.z, (float5 > 1.0f) ? ((double)(16.0f * float5)) : 16.0, this.dimension.getType(), new PlaySoundFromEntityS2CPacket(soundEvent, soundCategory, entity, float5, float6));
    }
    
    @Override
    public void playGlobalEvent(final int type, final BlockPos pos, final int data) {
        this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(type, pos, data, true));
    }
    
    @Override
    public void playLevelEvent(@Nullable final PlayerEntity playerEntity, final int integer2, final BlockPos blockPos, final int integer4) {
        this.server.getPlayerManager().sendToAround(playerEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 64.0, this.dimension.getType(), new WorldEventS2CPacket(integer2, blockPos, integer4, false));
    }
    
    @Override
    public void updateListeners(final BlockPos blockPos, final BlockState blockState2, final BlockState blockState3, final int integer) {
        this.getChunkManager().markForUpdate(blockPos);
        final VoxelShape voxelShape5 = blockState2.getCollisionShape(this, blockPos);
        final VoxelShape voxelShape6 = blockState3.getCollisionShape(this, blockPos);
        if (!VoxelShapes.matchesAnywhere(voxelShape5, voxelShape6, BooleanBiFunction.NOT_SAME)) {
            return;
        }
        for (final EntityNavigation entityNavigation8 : this.entityNavigations) {
            if (entityNavigation8.shouldRecalculatePath()) {
                continue;
            }
            entityNavigation8.c(blockPos);
        }
    }
    
    @Override
    public void sendEntityStatus(final Entity entity, final byte status) {
        this.getChunkManager().sendToNearbyPlayers(entity, new EntityStatusS2CPacket(entity, status));
    }
    
    @Override
    public ServerChunkManager getChunkManager() {
        return (ServerChunkManager)super.getChunkManager();
    }
    
    @Environment(EnvType.CLIENT)
    public CompletableFuture<WorldChunk> getChunkFutureSyncOnMainThread(final int x, final int z, final boolean create) {
        return this.getChunkManager().getChunkFutureSyncOnMainThread(x, z, ChunkStatus.FULL, create).<WorldChunk>thenApply(either -> (WorldChunk)either.map(chunk -> chunk, unloaded -> null));
    }
    
    @Override
    public Explosion createExplosion(@Nullable final Entity entity, final DamageSource damageSource, final double x, final double y, final double z, final float float9, final boolean boolean10, final Explosion.DestructionType destructionType11) {
        final Explosion explosion12 = new Explosion(this, entity, x, y, z, float9, boolean10, destructionType11);
        if (damageSource != null) {
            explosion12.setDamageSource(damageSource);
        }
        explosion12.collectBlocksAndDamageEntities();
        explosion12.affectWorld(false);
        if (destructionType11 == Explosion.DestructionType.a) {
            explosion12.clearAffectedBlocks();
        }
        for (final ServerPlayerEntity serverPlayerEntity14 : this.players) {
            if (serverPlayerEntity14.squaredDistanceTo(x, y, z) < 4096.0) {
                serverPlayerEntity14.networkHandler.sendPacket(new ExplosionS2CPacket(x, y, z, float9, explosion12.getAffectedBlocks(), explosion12.getAffectedPlayers().get(serverPlayerEntity14)));
            }
        }
        return explosion12;
    }
    
    @Override
    public void addBlockAction(final BlockPos pos, final Block block, final int type, final int data) {
        this.pendingBlockActions.add(new BlockAction(pos, block, type, data));
    }
    
    private void sendBlockActions() {
        while (!this.pendingBlockActions.isEmpty()) {
            final BlockAction blockAction1 = (BlockAction)this.pendingBlockActions.removeFirst();
            if (this.a(blockAction1)) {
                this.server.getPlayerManager().sendToAround(null, blockAction1.getPos().getX(), blockAction1.getPos().getY(), blockAction1.getPos().getZ(), 64.0, this.dimension.getType(), new BlockActionS2CPacket(blockAction1.getPos(), blockAction1.getBlock(), blockAction1.getType(), blockAction1.getData()));
            }
        }
    }
    
    private boolean a(final BlockAction blockAction) {
        final BlockState blockState2 = this.getBlockState(blockAction.getPos());
        return blockState2.getBlock() == blockAction.getBlock() && blockState2.onBlockAction(this, blockAction.getPos(), blockAction.getType(), blockAction.getData());
    }
    
    @Override
    public void close() throws IOException {
        this.chunkManager.close();
        super.close();
    }
    
    @Override
    public ServerTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }
    
    @Override
    public ServerTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }
    
    @Nonnull
    @Override
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }
    
    public StructureManager getStructureManager() {
        return this.worldSaveHandler.getStructureManager();
    }
    
    public <T extends ParticleParameters> int spawnParticles(final T particle, final double x, final double y, final double z, final int count, final double deltaX, final double deltaY, final double deltaZ, final double speed) {
        final ParticleS2CPacket particleS2CPacket17 = new ParticleS2CPacket((T)particle, false, (float)x, (float)y, (float)z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int integer18 = 0;
        for (int integer19 = 0; integer19 < this.players.size(); ++integer19) {
            final ServerPlayerEntity serverPlayerEntity20 = this.players.get(integer19);
            if (this.sendToPlayerIfNearby(serverPlayerEntity20, false, x, y, z, particleS2CPacket17)) {
                ++integer18;
            }
        }
        return integer18;
    }
    
    public <T extends ParticleParameters> boolean spawnParticles(final ServerPlayerEntity viewer, final T particle, final boolean force, final double x, final double y, final double z, final int count, final double deltaX, final double deltaY, final double deltaZ, final double speed) {
        final Packet<?> packet19 = new ParticleS2CPacket((T)particle, force, (float)x, (float)y, (float)z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet19);
    }
    
    private boolean sendToPlayerIfNearby(final ServerPlayerEntity player, final boolean force, final double x, final double y, final double z, final Packet<?> packet) {
        if (player.getServerWorld() != this) {
            return false;
        }
        final BlockPos blockPos10 = player.getBlockPos();
        if (blockPos10.isWithinDistance(new Vec3d(x, y, z), force ? 512.0 : 32.0)) {
            player.networkHandler.sendPacket(packet);
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public Entity getEntityById(final int integer) {
        return (Entity)this.entitiesById.get(integer);
    }
    
    @Nullable
    public Entity getEntity(final UUID uUID) {
        return this.entitiesByUuid.get(uUID);
    }
    
    @Nullable
    @Override
    public BlockPos locateStructure(final String id, final BlockPos center, final int radius, final boolean skipExistingChunks) {
        return this.getChunkManager().getChunkGenerator().locateStructure(this, id, center, radius, skipExistingChunks);
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return this.server.getRecipeManager();
    }
    
    @Override
    public TagManager getTagManager() {
        return this.server.getTagManager();
    }
    
    @Override
    public void setTime(final long long1) {
        super.setTime(long1);
        this.properties.getScheduledEvents().processEvents(this.server, long1);
    }
    
    @Override
    public boolean isSavingDisabled() {
        return this.savingDisabled;
    }
    
    public void checkSessionLock() throws SessionLockException {
        this.worldSaveHandler.checkSessionLock();
    }
    
    public WorldSaveHandler getSaveHandler() {
        return this.worldSaveHandler;
    }
    
    public PersistentStateManager getPersistentStateManager() {
        return this.getChunkManager().getPersistentStateManager();
    }
    
    @Nullable
    @Override
    public MapState getMapState(final String id) {
        return this.getServer().getWorld(DimensionType.a).getPersistentStateManager().<MapState>get(() -> new MapState(id), id);
    }
    
    @Override
    public void putMapState(final MapState mapState) {
        this.getServer().getWorld(DimensionType.a).getPersistentStateManager().set(mapState);
    }
    
    @Override
    public int getNextMapId() {
        return this.getServer().getWorld(DimensionType.a).getPersistentStateManager().<IdCountsState>getOrCreate(IdCountsState::new, "idcounts").getNextMapId();
    }
    
    @Override
    public void setSpawnPos(final BlockPos blockPos) {
        final ChunkPos chunkPos2 = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
        super.setSpawnPos(blockPos);
        this.getChunkManager().<Void>removeTicket(ChunkTicketType.START, chunkPos2, 11, Void.INSTANCE);
        this.getChunkManager().<Void>addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Void.INSTANCE);
    }
    
    public LongSet getForcedChunks() {
        final ForcedChunkState forcedChunkState1 = this.getPersistentStateManager().<ForcedChunkState>get(ForcedChunkState::new, "chunks");
        return (LongSet)((forcedChunkState1 != null) ? LongSets.unmodifiable(forcedChunkState1.getChunks()) : LongSets.EMPTY_SET);
    }
    
    public boolean setChunkForced(final int x, final int z, final boolean forced) {
        final ForcedChunkState forcedChunkState4 = this.getPersistentStateManager().<ForcedChunkState>getOrCreate(ForcedChunkState::new, "chunks");
        final ChunkPos chunkPos5 = new ChunkPos(x, z);
        final long long6 = chunkPos5.toLong();
        boolean boolean8;
        if (forced) {
            boolean8 = forcedChunkState4.getChunks().add(long6);
            if (boolean8) {
                this.getChunk(x, z);
            }
        }
        else {
            boolean8 = forcedChunkState4.getChunks().remove(long6);
        }
        forcedChunkState4.setDirty(boolean8);
        if (boolean8) {
            this.getChunkManager().setChunkForced(chunkPos5, forced);
        }
        return boolean8;
    }
    
    @Override
    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }
    
    @Override
    public void onBlockChanged(final BlockPos pos, final BlockState oldBlock, final BlockState newBlock) {
        final BlockPos blockPos4 = pos.toImmutable();
        final Optional<PointOfInterestType> optional5 = PointOfInterestType.from(oldBlock);
        final Optional<PointOfInterestType> optional6 = PointOfInterestType.from(newBlock);
        if (Objects.equals(optional5, optional6)) {
            return;
        }
        final BlockPos blockPos5;
        optional5.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().remove(blockPos5);
            DebugRendererInfoManager.b(this, blockPos5);
        }));
        final BlockPos blockPos6;
        optional6.ifPresent(pointOfInterestType -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().add(blockPos6, pointOfInterestType);
            DebugRendererInfoManager.a(this, blockPos6);
        }));
    }
    
    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.getChunkManager().getPointOfInterestStorage();
    }
    
    public boolean isNearOccupiedPointOfInterest(final BlockPos pos) {
        return this.isNearOccupiedPointOfInterest(pos, 1);
    }
    
    public boolean isNearOccupiedPointOfInterest(final BlockPos blockPos, final int maxDistance) {
        return maxDistance <= 6 && this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPos)) <= maxDistance;
    }
    
    public int getOccupiedPointOfInterestDistance(final ChunkSectionPos pos) {
        return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(pos);
    }
    
    public RaidManager getRaidManager() {
        return this.raidManager;
    }
    
    @Nullable
    public Raid getRaidAt(final BlockPos blockPos) {
        return this.raidManager.getRaidAt(blockPos);
    }
    
    public boolean hasRaidAt(final BlockPos blockPos) {
        return this.getRaidAt(blockPos) != null;
    }
    
    public void handleInteraction(final EntityInteraction interaction, final Entity entity, final InteractionObserver observer) {
        observer.onInteractionWith(interaction, entity);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
