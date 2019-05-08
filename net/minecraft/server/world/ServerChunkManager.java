package net.minecraft.server.world;

import net.minecraft.util.registry.Registry;
import java.util.concurrent.CompletionStage;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.village.PointOfInterestStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.SpawnHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.level.LevelGeneratorType;
import java.util.function.BooleanSupplier;
import java.io.IOException;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import java.util.Optional;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkPos;
import java.util.concurrent.CompletableFuture;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.chunk.Chunk;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.world.World;
import java.util.function.Supplier;
import net.minecraft.server.WorldGenerationProgressListener;
import java.util.concurrent.Executor;
import net.minecraft.structure.StructureManager;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.chunk.ChunkStatus;
import java.util.List;
import net.minecraft.world.chunk.ChunkManager;

public class ServerChunkManager extends ChunkManager
{
    private static final int CHUNKS_ELIGIBLE_FOR_SPAWNING;
    private static final List<ChunkStatus> CHUNK_STATUSES;
    private final ChunkTicketManager ticketManager;
    private final ChunkGenerator<?> chunkGenerator;
    private final ServerWorld world;
    private final Thread serverThread;
    private final ServerLightingProvider lightProvider;
    private final MainThreadExecutor mainThreadExecutor;
    public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
    private final PersistentStateManager persistentStateManager;
    private long lastMobSpawningTime;
    private boolean spawnMonsters;
    private boolean spawnAnimals;
    
    public ServerChunkManager(final ServerWorld serverWorld, final File file, final DataFixer dataFixer, final StructureManager structureManager, final Executor workerExecutor, final ChunkGenerator<?> chunkGenerator, final int integer7, final int integer8, final WorldGenerationProgressListener worldGenerationProgressListener, final Supplier<PersistentStateManager> supplier) {
        this.spawnMonsters = true;
        this.spawnAnimals = true;
        this.world = serverWorld;
        this.mainThreadExecutor = new MainThreadExecutor((World)serverWorld);
        this.chunkGenerator = chunkGenerator;
        this.serverThread = Thread.currentThread();
        final File file2 = serverWorld.getDimension().getType().getFile(file);
        final File file3 = new File(file2, "data");
        file3.mkdirs();
        this.persistentStateManager = new PersistentStateManager(file3, dataFixer);
        this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(serverWorld, file, dataFixer, structureManager, workerExecutor, this.mainThreadExecutor, this, this.getChunkGenerator(), worldGenerationProgressListener, supplier, integer7, integer8);
        this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
        this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
    }
    
    @Override
    public ServerLightingProvider getLightingProvider() {
        return this.lightProvider;
    }
    
    @Nullable
    private ChunkHolder getChunkHolder(final long pos) {
        return this.threadedAnvilChunkStorage.getChunkHolder(pos);
    }
    
    public int getTotalChunksLoadedCount() {
        return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
    }
    
    @Nullable
    @Override
    public Chunk getChunk(final int x, final int z, final ChunkStatus leastStatus, final boolean create) {
        final CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture5 = this.getChunkFutureSyncOnMainThread(x, z, leastStatus, create);
        final IllegalStateException ex;
        return (Chunk)completableFuture5.join().map(chunk -> chunk, unloaded -> {
            if (create) {
                new IllegalStateException("Chunk not there when requested: " + unloaded);
                throw ex;
            }
            else {
                return null;
            }
        });
    }
    
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(final int chunkX, final int chunkZ, final ChunkStatus leastStatus, final boolean create) {
        final boolean boolean5 = Thread.currentThread() == this.serverThread;
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2;
        if (boolean5) {
            completableFuture2 = this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
            this.mainThreadExecutor.waitFor(completableFuture2::isDone);
        }
        else {
            completableFuture2 = CompletableFuture.<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>>supplyAsync(() -> this.getChunkFuture(chunkX, chunkZ, leastStatus, create), this.mainThreadExecutor).<Either<Chunk, ChunkHolder.Unloaded>>thenCompose(completableFuture -> completableFuture);
        }
        return completableFuture2;
    }
    
    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(final int chunkX, final int chunkZ, final ChunkStatus leastStatus, final boolean create) {
        final ChunkPos chunkPos5 = new ChunkPos(chunkX, chunkZ);
        final long long6 = chunkPos5.toLong();
        final int integer8 = 33 + ChunkStatus.getTargetGenerationRadius(leastStatus);
        ChunkHolder chunkHolder9 = this.getChunkHolder(long6);
        if (create) {
            this.ticketManager.<ChunkPos>addTicketWithLevel(ChunkTicketType.UNKNOWN, chunkPos5, integer8, chunkPos5);
            if (this.isMissingForLevel(chunkHolder9, integer8)) {
                this.tick();
                chunkHolder9 = this.getChunkHolder(long6);
                if (this.isMissingForLevel(chunkHolder9, integer8)) {
                    throw new IllegalStateException("No chunk holder after ticket has been added");
                }
            }
        }
        if (this.isMissingForLevel(chunkHolder9, integer8)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return chunkHolder9.getFutureChecked(leastStatus);
    }
    
    private boolean isMissingForLevel(@Nullable final ChunkHolder holder, final int maxLevel) {
        return holder == null || holder.getLevel() > maxLevel;
    }
    
    @Override
    public boolean isChunkLoaded(final int x, final int z) {
        final ChunkHolder chunkHolder3 = this.getChunkHolder(new ChunkPos(x, z).toLong());
        final int integer4 = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL);
        return chunkHolder3 != null && chunkHolder3.getLevel() <= integer4 && chunkHolder3.getFutureChecked(ChunkStatus.FULL).getNow(ChunkHolder.UNLOADED_CHUNK).left().isPresent();
    }
    
    @Override
    public BlockView getChunk(final int chunkX, final int chunkZ) {
        final long long3 = ChunkPos.toLong(chunkX, chunkZ);
        final ChunkHolder chunkHolder5 = this.getChunkHolder(long3);
        if (chunkHolder5 == null) {
            return null;
        }
        int integer6 = ServerChunkManager.CHUNK_STATUSES.size() - 1;
        while (true) {
            final ChunkStatus chunkStatus7 = ServerChunkManager.CHUNK_STATUSES.get(integer6);
            final Optional<Chunk> optional8 = (Optional<Chunk>)chunkHolder5.getFuture(chunkStatus7).getNow(ChunkHolder.UNLOADED_CHUNK).left();
            if (optional8.isPresent()) {
                return optional8.get();
            }
            if (chunkStatus7 == ChunkStatus.LIGHT.getPrevious()) {
                return null;
            }
            --integer6;
        }
    }
    
    @Override
    public World getWorld() {
        return this.world;
    }
    
    public boolean executeQueuedTasks() {
        return this.mainThreadExecutor.executeQueuedTask();
    }
    
    private boolean tick() {
        if (this.ticketManager.tick(this.threadedAnvilChunkStorage)) {
            this.threadedAnvilChunkStorage.updateHolderMap();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean shouldTickEntity(final Entity entity) {
        final ChunkHolder chunkHolder2 = this.getChunkHolder(ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4));
        if (chunkHolder2 == null) {
            return false;
        }
        final Either<WorldChunk, ChunkHolder.Unloaded> either3 = chunkHolder2.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
        return either3.left().isPresent();
    }
    
    public void save(final boolean flush) {
        this.threadedAnvilChunkStorage.save(flush);
    }
    
    @Override
    public void close() throws IOException {
        this.lightProvider.close();
        this.threadedAnvilChunkStorage.close();
    }
    
    @Override
    public void tick(final BooleanSupplier booleanSupplier) {
        this.world.getProfiler().push("purge");
        this.ticketManager.purge();
        this.tick();
        this.world.getProfiler().swap("chunks");
        this.tickChunks();
        this.world.getProfiler().swap("unload");
        this.threadedAnvilChunkStorage.tick(booleanSupplier);
        this.world.getProfiler().pop();
    }
    
    private void tickChunks() {
        final long long1 = this.world.getTime();
        final long long2 = long1 - this.lastMobSpawningTime;
        this.lastMobSpawningTime = long1;
        final LevelProperties levelProperties5 = this.world.getLevelProperties();
        final boolean boolean6 = levelProperties5.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
        final boolean boolean7 = this.world.getGameRules().getBoolean("doMobSpawning");
        if (!boolean6) {
            this.world.getProfiler().push("pollingChunks");
            final int integer8 = this.ticketManager.getLevelCount();
            final int integer9 = this.world.getGameRules().getInteger("randomTickSpeed");
            final BlockPos blockPos10 = this.world.getSpawnPos();
            final boolean boolean8 = levelProperties5.getTime() % 400L == 0L;
            final EntityCategory[] arr12 = EntityCategory.values();
            final Object2IntMap<EntityCategory> object2IntMap13 = this.world.getMobCountsByCategory();
            final ObjectBidirectionalIterator<Long2ObjectMap.Entry<ChunkHolder>> objectBidirectionalIterator14 = this.threadedAnvilChunkStorage.entryIterator();
            while (objectBidirectionalIterator14.hasNext()) {
                final Long2ObjectMap.Entry<ChunkHolder> entry16 = (Long2ObjectMap.Entry<ChunkHolder>)objectBidirectionalIterator14.next();
                final ChunkHolder chunkHolder15 = (ChunkHolder)entry16.getValue();
                final Optional<WorldChunk> optional17 = (Optional<WorldChunk>)chunkHolder15.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
                if (!optional17.isPresent()) {
                    continue;
                }
                final WorldChunk worldChunk18 = optional17.get();
                chunkHolder15.flushUpdates(worldChunk18);
                final ChunkPos chunkPos19 = chunkHolder15.getPos();
                if (this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(chunkPos19)) {
                    continue;
                }
                worldChunk18.setInhabitedTime(worldChunk18.getInhabitedTime() + long2);
                if (boolean7 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk18.getPos())) {
                    this.world.getProfiler().push("spawner");
                    for (final EntityCategory entityCategory23 : arr12) {
                        if (entityCategory23 != EntityCategory.e) {
                            if (!entityCategory23.isPeaceful() || this.spawnAnimals) {
                                if (entityCategory23.isPeaceful() || this.spawnMonsters) {
                                    if (!entityCategory23.isAnimal() || boolean8) {
                                        final int integer10 = entityCategory23.getSpawnCap() * integer8 / ServerChunkManager.CHUNKS_ELIGIBLE_FOR_SPAWNING;
                                        if (object2IntMap13.getInt(entityCategory23) <= integer10) {
                                            SpawnHelper.spawnEntitiesInChunk(entityCategory23, this.world, worldChunk18, blockPos10);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.world.getProfiler().pop();
                }
                this.world.tickChunk(worldChunk18, integer9);
            }
            this.world.getProfiler().pop();
            if (boolean7) {
                this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
            }
        }
        this.threadedAnvilChunkStorage.tickPlayerMovement();
    }
    
    @Override
    public String getStatus() {
        return "ServerChunkCache: " + this.getLoadedChunkCount();
    }
    
    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        return this.chunkGenerator;
    }
    
    public int getLoadedChunkCount() {
        return this.threadedAnvilChunkStorage.getLoadedChunkCount();
    }
    
    public void markForUpdate(final BlockPos blockPos) {
        final int integer2 = blockPos.getX() >> 4;
        final int integer3 = blockPos.getZ() >> 4;
        final ChunkHolder chunkHolder4 = this.getChunkHolder(ChunkPos.toLong(integer2, integer3));
        if (chunkHolder4 != null) {
            chunkHolder4.markForBlockUpdate(blockPos.getX() & 0xF, blockPos.getY(), blockPos.getZ() & 0xF);
        }
    }
    
    @Override
    public void onLightUpdate(final LightType type, final ChunkSectionPos chunkSectionPos) {
        final ChunkHolder chunkHolder3;
        this.mainThreadExecutor.execute(() -> {
            chunkHolder3 = this.getChunkHolder(chunkSectionPos.toChunkPos().toLong());
            if (chunkHolder3 != null) {
                chunkHolder3.markForLightUpdate(type, chunkSectionPos.getChunkY());
            }
        });
    }
    
    public <T> void addTicket(final ChunkTicketType<T> chunkTicketType, final ChunkPos chunkPos, final int integer, final T object) {
        this.ticketManager.<T>addTicket(chunkTicketType, chunkPos, integer, object);
    }
    
    public <T> void removeTicket(final ChunkTicketType<T> chunkTicketType, final ChunkPos chunkPos, final int integer, final T object) {
        this.ticketManager.<T>removeTicket(chunkTicketType, chunkPos, integer, object);
    }
    
    @Override
    public void setChunkForced(final ChunkPos pos, final boolean forced) {
        this.ticketManager.setChunkForced(pos, forced);
    }
    
    public void updateCameraPosition(final ServerPlayerEntity player) {
        this.threadedAnvilChunkStorage.updateCameraPosition(player);
    }
    
    public void unloadEntity(final Entity entity) {
        this.threadedAnvilChunkStorage.unloadEntity(entity);
    }
    
    public void loadEntity(final Entity entity) {
        this.threadedAnvilChunkStorage.loadEntity(entity);
    }
    
    public void sendToNearbyPlayers(final Entity entity, final Packet<?> packet) {
        this.threadedAnvilChunkStorage.sendToNearbyPlayers(entity, packet);
    }
    
    public void sendToOtherNearbyPlayers(final Entity entity, final Packet<?> packet) {
        this.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(entity, packet);
    }
    
    public void applyViewDistance(final int watchDistance, final int viewDistance) {
        this.threadedAnvilChunkStorage.setViewDistance(watchDistance, viewDistance);
    }
    
    @Override
    public void setMobSpawnOptions(final boolean spawnMonsters, final boolean spawnAnimals) {
        this.spawnMonsters = spawnMonsters;
        this.spawnAnimals = spawnAnimals;
    }
    
    @Environment(EnvType.CLIENT)
    public String getDebugString(final ChunkPos pos) {
        return this.threadedAnvilChunkStorage.getDebugString(pos);
    }
    
    public PersistentStateManager getPersistentStateManager() {
        return this.persistentStateManager;
    }
    
    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
    }
    
    static {
        CHUNKS_ELIGIBLE_FOR_SPAWNING = (int)Math.pow(17.0, 2.0);
        CHUNK_STATUSES = ChunkStatus.createOrderedList();
    }
    
    final class MainThreadExecutor extends ThreadExecutor<Runnable>
    {
        private MainThreadExecutor(final World world) {
            super("Chunk source main thread executor for " + Registry.DIMENSION.getId(world.getDimension().getType()));
        }
        
        @Override
        protected Runnable prepareRunnable(final Runnable runnable) {
            return runnable;
        }
        
        @Override
        protected boolean canRun(final Runnable runnable) {
            return true;
        }
        
        @Override
        protected boolean shouldRunAsync() {
            return true;
        }
        
        @Override
        protected Thread getThread() {
            return ServerChunkManager.this.serverThread;
        }
        
        @Override
        protected boolean executeQueuedTask() {
            if (ServerChunkManager.this.tick()) {
                return true;
            }
            ServerChunkManager.this.lightProvider.tick();
            return super.executeQueuedTask();
        }
    }
}
