package net.minecraft.server.world;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.util.math.MathHelper;
import java.util.function.Consumer;
import java.util.concurrent.CompletionStage;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.world.BlockView;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.world.LightType;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.ChunkPos;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.world.chunk.ChunkStatus;
import java.util.List;
import net.minecraft.world.chunk.WorldChunk;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.chunk.Chunk;
import com.mojang.datafixers.util.Either;

public class ChunkHolder
{
    public static final Either<Chunk, Unloaded> UNLOADED_CHUNK;
    public static final CompletableFuture<Either<Chunk, Unloaded>> UNLOADED_CHUNK_FUTURE;
    public static final Either<WorldChunk, Unloaded> UNLOADED_WORLD_CHUNK;
    private static final CompletableFuture<Either<WorldChunk, Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE;
    private static final List<ChunkStatus> CHUNK_STATUSES;
    private static final LevelType[] LEVEL_TYPES;
    private final AtomicReferenceArray<CompletableFuture<Either<Chunk, Unloaded>>> futuresByStatus;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> tickingFuture;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> entityTickingFuture;
    private CompletableFuture<Chunk> future;
    private int lastTickLevel;
    private int level;
    private int completedLevel;
    private final ChunkPos pos;
    private final short[] blockUpdatePositions;
    private int blockUpdateCount;
    private int sectionsNeedingUpdateMask;
    private int lightSentWithBlocksBits;
    private int blockLightUpdateBits;
    private int skyLightUpdateBits;
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean x;
    
    public ChunkHolder(final ChunkPos pos, final int level, final LightingProvider lightingProvider, final LevelUpdateListener levelUpdateListener, final PlayersWatchingChunkProvider playersWatchingChunkProvider) {
        this.futuresByStatus = new AtomicReferenceArray<CompletableFuture<Either<Chunk, Unloaded>>>(ChunkHolder.CHUNK_STATUSES.size());
        this.tickingFuture = ChunkHolder.UNLOADED_WORLD_CHUNK_FUTURE;
        this.entityTickingFuture = ChunkHolder.UNLOADED_WORLD_CHUNK_FUTURE;
        this.future = CompletableFuture.<Chunk>completedFuture((Chunk)null);
        this.blockUpdatePositions = new short[64];
        this.pos = pos;
        this.lightingProvider = lightingProvider;
        this.levelUpdateListener = levelUpdateListener;
        this.playersWatchingChunkProvider = playersWatchingChunkProvider;
        this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        this.level = this.lastTickLevel;
        this.completedLevel = this.lastTickLevel;
        this.setLevel(level);
    }
    
    public CompletableFuture<Either<Chunk, Unloaded>> getFuture(final ChunkStatus leastStatus) {
        final CompletableFuture<Either<Chunk, Unloaded>> completableFuture2 = this.futuresByStatus.get(leastStatus.getIndex());
        return (completableFuture2 == null) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : completableFuture2;
    }
    
    public CompletableFuture<Either<Chunk, Unloaded>> getFutureChecked(final ChunkStatus leastStatus) {
        if (getTargetGenerationStatus(this.level).isAtLeast(leastStatus)) {
            return this.getFuture(leastStatus);
        }
        return ChunkHolder.UNLOADED_CHUNK_FUTURE;
    }
    
    public CompletableFuture<Either<WorldChunk, Unloaded>> getTickingFuture() {
        return this.tickingFuture;
    }
    
    public CompletableFuture<Either<WorldChunk, Unloaded>> getEntityTickingFuture() {
        return this.entityTickingFuture;
    }
    
    @Nullable
    public WorldChunk getWorldChunk() {
        final CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture1 = this.getTickingFuture();
        final Either<WorldChunk, Unloaded> either2 = completableFuture1.getNow(null);
        if (either2 == null) {
            return null;
        }
        return either2.left().orElse(null);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public ChunkStatus getCompletedStatus() {
        for (int integer1 = ChunkHolder.CHUNK_STATUSES.size() - 1; integer1 >= 0; --integer1) {
            final ChunkStatus chunkStatus2 = ChunkHolder.CHUNK_STATUSES.get(integer1);
            final CompletableFuture<Either<Chunk, Unloaded>> completableFuture3 = this.getFuture(chunkStatus2);
            if (completableFuture3.getNow(ChunkHolder.UNLOADED_CHUNK).left().isPresent()) {
                return chunkStatus2;
            }
        }
        return null;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Chunk getCompletedChunk() {
        for (int integer1 = ChunkHolder.CHUNK_STATUSES.size() - 1; integer1 >= 0; --integer1) {
            final ChunkStatus chunkStatus2 = ChunkHolder.CHUNK_STATUSES.get(integer1);
            final CompletableFuture<Either<Chunk, Unloaded>> completableFuture3 = this.getFuture(chunkStatus2);
            if (!completableFuture3.isCompletedExceptionally()) {
                final Optional<Chunk> optional4 = (Optional<Chunk>)completableFuture3.getNow(ChunkHolder.UNLOADED_CHUNK).left();
                if (optional4.isPresent()) {
                    return optional4.get();
                }
            }
        }
        return null;
    }
    
    public CompletableFuture<Chunk> getFuture() {
        return this.future;
    }
    
    public void markForBlockUpdate(final int x, final int y, final int z) {
        final WorldChunk worldChunk4 = this.getWorldChunk();
        if (worldChunk4 == null) {
            return;
        }
        this.sectionsNeedingUpdateMask |= 1 << (y >> 4);
        if (this.blockUpdateCount < 64) {
            final short short5 = (short)(x << 12 | z << 8 | y);
            for (int integer6 = 0; integer6 < this.blockUpdateCount; ++integer6) {
                if (this.blockUpdatePositions[integer6] == short5) {
                    return;
                }
            }
            this.blockUpdatePositions[this.blockUpdateCount++] = short5;
        }
    }
    
    public void markForLightUpdate(final LightType type, final int y) {
        final WorldChunk worldChunk3 = this.getWorldChunk();
        if (worldChunk3 == null) {
            return;
        }
        worldChunk3.setShouldSave(true);
        if (type == LightType.SKY) {
            this.skyLightUpdateBits |= 1 << y + 1;
        }
        else {
            this.blockLightUpdateBits |= 1 << y + 1;
        }
    }
    
    public void flushUpdates(final WorldChunk worldChunk) {
        if (this.blockUpdateCount == 0 && this.skyLightUpdateBits == 0 && this.blockLightUpdateBits == 0) {
            return;
        }
        final World world2 = worldChunk.getWorld();
        if (this.blockUpdateCount == 64) {
            this.lightSentWithBlocksBits = -1;
        }
        if (this.skyLightUpdateBits != 0 || this.blockLightUpdateBits != 0) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, this.skyLightUpdateBits & ~this.lightSentWithBlocksBits, this.blockLightUpdateBits & ~this.lightSentWithBlocksBits), true);
            final int integer3 = this.skyLightUpdateBits & this.lightSentWithBlocksBits;
            final int integer4 = this.blockLightUpdateBits & this.lightSentWithBlocksBits;
            if (integer3 != 0 || integer4 != 0) {
                this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, integer3, integer4), false);
            }
            this.skyLightUpdateBits = 0;
            this.blockLightUpdateBits = 0;
            this.lightSentWithBlocksBits &= ~(this.skyLightUpdateBits & this.blockLightUpdateBits);
        }
        if (this.blockUpdateCount == 1) {
            final int integer3 = (this.blockUpdatePositions[0] >> 12 & 0xF) + this.pos.x * 16;
            final int integer4 = this.blockUpdatePositions[0] & 0xFF;
            final int integer5 = (this.blockUpdatePositions[0] >> 8 & 0xF) + this.pos.z * 16;
            final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
            this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(world2, blockPos6), false);
            if (world2.getBlockState(blockPos6).getBlock().hasBlockEntity()) {
                this.sendBlockEntityUpdatePacket(world2, blockPos6);
            }
        }
        else if (this.blockUpdateCount == 64) {
            this.sendPacketToPlayersWatching(new ChunkDataS2CPacket(worldChunk, this.sectionsNeedingUpdateMask), false);
        }
        else if (this.blockUpdateCount != 0) {
            this.sendPacketToPlayersWatching(new ChunkDeltaUpdateS2CPacket(this.blockUpdateCount, this.blockUpdatePositions, worldChunk), false);
            for (int integer3 = 0; integer3 < this.blockUpdateCount; ++integer3) {
                final int integer4 = (this.blockUpdatePositions[integer3] >> 12 & 0xF) + this.pos.x * 16;
                final int integer5 = this.blockUpdatePositions[integer3] & 0xFF;
                final int integer6 = (this.blockUpdatePositions[integer3] >> 8 & 0xF) + this.pos.z * 16;
                final BlockPos blockPos7 = new BlockPos(integer4, integer5, integer6);
                if (world2.getBlockState(blockPos7).getBlock().hasBlockEntity()) {
                    this.sendBlockEntityUpdatePacket(world2, blockPos7);
                }
            }
        }
        this.blockUpdateCount = 0;
        this.sectionsNeedingUpdateMask = 0;
    }
    
    private void sendBlockEntityUpdatePacket(final World world, final BlockPos pos) {
        final BlockEntity blockEntity3 = world.getBlockEntity(pos);
        if (blockEntity3 != null) {
            final BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket4 = blockEntity3.toUpdatePacket();
            if (blockEntityUpdateS2CPacket4 != null) {
                this.sendPacketToPlayersWatching(blockEntityUpdateS2CPacket4, false);
            }
        }
    }
    
    private void sendPacketToPlayersWatching(final Packet<?> packet, final boolean onlyOnWatchDistanceEdge) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, onlyOnWatchDistanceEdge).forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
    }
    
    public CompletableFuture<Either<Chunk, Unloaded>> createFuture(final ChunkStatus targetStatus, final ThreadedAnvilChunkStorage chunkStorage) {
        final int integer3 = targetStatus.getIndex();
        final CompletableFuture<Either<Chunk, Unloaded>> completableFuture4 = this.futuresByStatus.get(integer3);
        if (completableFuture4 != null) {
            final Either<Chunk, Unloaded> either5 = completableFuture4.getNow(null);
            if (either5 == null || either5.left().isPresent()) {
                return completableFuture4;
            }
        }
        if (getTargetGenerationStatus(this.level).isAtLeast(targetStatus)) {
            final CompletableFuture<Either<Chunk, Unloaded>> completableFuture5 = chunkStorage.createChunkFuture(this, targetStatus);
            this.updateFuture(completableFuture5);
            this.futuresByStatus.set(integer3, completableFuture5);
            return completableFuture5;
        }
        return (completableFuture4 == null) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : completableFuture4;
    }
    
    private void updateFuture(final CompletableFuture<? extends Either<? extends Chunk, Unloaded>> newChunkFuture) {
        this.future = this.future.<Object, Chunk>thenCombine(newChunkFuture, (chunk, either) -> (Chunk)either.map(chunk -> chunk, unloaded -> chunk));
    }
    
    @Environment(EnvType.CLIENT)
    public LevelType getLevelType() {
        return getLevelType(this.level);
    }
    
    public ChunkPos getPos() {
        return this.pos;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getCompletedLevel() {
        return this.completedLevel;
    }
    
    private void setCompletedLevel(final int level) {
        this.completedLevel = level;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    protected void tick(final ThreadedAnvilChunkStorage chunkStorage) {
        final ChunkStatus chunkStatus2 = getTargetGenerationStatus(this.lastTickLevel);
        final ChunkStatus chunkStatus3 = getTargetGenerationStatus(this.level);
        this.x |= chunkStatus3.isAtLeast(ChunkStatus.FULL);
        final boolean boolean4 = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        final boolean boolean5 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        final LevelType levelType6 = getLevelType(this.lastTickLevel);
        final LevelType levelType7 = getLevelType(this.level);
        final boolean boolean6 = levelType6.isAfter(LevelType.TICKING);
        final boolean boolean7 = levelType7.isAfter(LevelType.TICKING);
        if (boolean5) {
            for (int integer10 = boolean4 ? (chunkStatus2.getIndex() + 1) : 0; integer10 <= chunkStatus3.getIndex(); ++integer10) {
                this.createFuture(ChunkHolder.CHUNK_STATUSES.get(integer10), chunkStorage);
            }
        }
        if (boolean4) {
            final Either<Chunk, Unloaded> either2 = (Either<Chunk, Unloaded>)Either.right(new Unloaded() {
                @Override
                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
                }
            });
            for (int integer11 = boolean5 ? (chunkStatus3.getIndex() + 1) : 0; integer11 <= chunkStatus2.getIndex(); ++integer11) {
                final CompletableFuture<Either<Chunk, Unloaded>> completableFuture12 = this.futuresByStatus.get(integer11);
                if (completableFuture12 != null) {
                    completableFuture12.complete(either2);
                }
                else {
                    this.futuresByStatus.set(integer11, CompletableFuture.<Either<Chunk, Unloaded>>completedFuture(either2));
                }
            }
        }
        if (!boolean6 && boolean7) {
            this.updateFuture(this.tickingFuture = chunkStorage.createTickingFuture(this));
        }
        if (boolean6 && !boolean7) {
            final CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture13 = this.tickingFuture;
            this.tickingFuture = ChunkHolder.UNLOADED_WORLD_CHUNK_FUTURE;
            completableFuture13.thenAccept(either -> either.ifLeft((Consumer)chunkStorage::a));
        }
        final boolean boolean8 = levelType6.isAfter(LevelType.ENTITY_TICKING);
        final boolean boolean9 = levelType7.isAfter(LevelType.ENTITY_TICKING);
        if (!boolean8 && boolean9) {
            if (this.entityTickingFuture != ChunkHolder.UNLOADED_WORLD_CHUNK_FUTURE) {
                throw new IllegalStateException();
            }
            this.updateFuture(this.entityTickingFuture = chunkStorage.createEntityTickingChunkFuture(this.pos));
        }
        if (boolean8 && !boolean9) {
            this.entityTickingFuture.complete(ChunkHolder.UNLOADED_WORLD_CHUNK);
            this.entityTickingFuture = ChunkHolder.UNLOADED_WORLD_CHUNK_FUTURE;
        }
        this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
        this.lastTickLevel = this.level;
    }
    
    public static ChunkStatus getTargetGenerationStatus(final int level) {
        if (level <= 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getTargetGenerationStatus(level - 33 - 1);
    }
    
    public static LevelType getLevelType(final int distance) {
        return ChunkHolder.LEVEL_TYPES[MathHelper.clamp(33 - distance, 0, ChunkHolder.LEVEL_TYPES.length - 1)];
    }
    
    public boolean k() {
        return this.x;
    }
    
    public void l() {
        this.x = getTargetGenerationStatus(this.level).isAtLeast(ChunkStatus.FULL);
    }
    
    public void a(final ReadOnlyChunk readOnlyChunk) {
        for (int integer2 = 0; integer2 < this.futuresByStatus.length(); ++integer2) {
            final CompletableFuture<Either<Chunk, Unloaded>> completableFuture3 = this.futuresByStatus.get(integer2);
            if (completableFuture3 != null) {
                final Optional<Chunk> optional4 = (Optional<Chunk>)completableFuture3.getNow(ChunkHolder.UNLOADED_CHUNK).left();
                if (optional4.isPresent()) {
                    if (optional4.get() instanceof ProtoChunk) {
                        this.futuresByStatus.set(integer2, CompletableFuture.<Either<Chunk, Unloaded>>completedFuture(Either.left(readOnlyChunk)));
                    }
                }
            }
        }
        this.updateFuture(CompletableFuture.<Either>completedFuture(Either.left(readOnlyChunk.getWrappedChunk())));
    }
    
    static {
        UNLOADED_CHUNK = Either.right(Unloaded.INSTANCE);
        UNLOADED_CHUNK_FUTURE = CompletableFuture.<Either<Chunk, Unloaded>>completedFuture(ChunkHolder.UNLOADED_CHUNK);
        UNLOADED_WORLD_CHUNK = Either.right(Unloaded.INSTANCE);
        UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.<Either<WorldChunk, Unloaded>>completedFuture(ChunkHolder.UNLOADED_WORLD_CHUNK);
        CHUNK_STATUSES = ChunkStatus.createOrderedList();
        LEVEL_TYPES = LevelType.values();
    }
    
    public enum LevelType
    {
        BORDER, 
        TICKING, 
        ENTITY_TICKING;
        
        public boolean isAfter(final LevelType levelType) {
            return this.ordinal() >= levelType.ordinal();
        }
    }
    
    public interface Unloaded
    {
        public static final Unloaded INSTANCE = new Unloaded() {
            @Override
            public String toString() {
                return "UNLOADED";
            }
        };
    }
    
    public interface PlayersWatchingChunkProvider
    {
        Stream<ServerPlayerEntity> getPlayersWatchingChunk(final ChunkPos arg1, final boolean arg2);
    }
    
    public interface LevelUpdateListener
    {
        void updateLevel(final ChunkPos arg1, final IntSupplier arg2, final int arg3, final IntConsumer arg4);
    }
}
