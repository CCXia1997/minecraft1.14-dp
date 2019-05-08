package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import net.minecraft.util.SectionRelativeLevelPropagator;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import net.minecraft.util.ChunkPosLevelPropagator;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.util.math.ChunkSectionPos;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import it.unimi.dsi.fastutil.longs.LongIterator;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.MailboxProcessor;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import com.google.common.collect.Sets;
import java.util.concurrent.Executor;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.Actor;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.network.ServerPlayerEntity;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager
{
    private static final Logger LOGGER;
    private static final int NEARBY_PLAYER_TICKET_LEVEL;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkSectionPos;
    private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> ticketsByPosition;
    private final a distanceFromTicketTracker;
    private final c shouldEntityTickTracker;
    private int viewDistance;
    private final DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker;
    private final NearbyChunkTicketUpdater nearbyChunkTicketUpdater;
    private final Set<ChunkHolder> chunkHolders;
    private final ChunkHolder.LevelUpdateListener levelUpdateListener;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> playerTicketThrottler;
    private final Actor<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
    private final LongSet chunkPositions;
    private final Executor mainThreadExecutor;
    private long location;
    
    protected ChunkTicketManager(final Executor workerExecutor, final Executor mainThreadExecutor) {
        this.playersByChunkPos = (Long2ObjectMap<ObjectSet<ServerPlayerEntity>>)new Long2ObjectOpenHashMap();
        this.playersByChunkSectionPos = (Long2ObjectMap<ObjectSet<ServerPlayerEntity>>)new Long2ObjectOpenHashMap();
        this.ticketsByPosition = (Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>>)new Long2ObjectOpenHashMap();
        this.distanceFromTicketTracker = new a();
        this.shouldEntityTickTracker = new c();
        this.distanceFromNearestPlayerTracker = new DistanceFromNearestPlayerTracker(8);
        this.nearbyChunkTicketUpdater = new NearbyChunkTicketUpdater(33);
        this.chunkHolders = Sets.newHashSet();
        this.chunkPositions = (LongSet)new LongOpenHashSet();
        final MailboxProcessor<Runnable> mailboxProcessor3 = MailboxProcessor.create(mainThreadExecutor, "player ticket throttler");
        final ChunkTaskPrioritySystem chunkTaskPrioritySystem4 = new ChunkTaskPrioritySystem(ImmutableList.of(mailboxProcessor3), workerExecutor, 15);
        this.levelUpdateListener = chunkTaskPrioritySystem4;
        this.playerTicketThrottler = chunkTaskPrioritySystem4.<Runnable>createExecutingActor(mailboxProcessor3, true);
        this.playerTicketThrottlerSorter = chunkTaskPrioritySystem4.createSortingActor(mailboxProcessor3);
        this.mainThreadExecutor = mainThreadExecutor;
    }
    
    protected void setViewDistance(final int integer) {
        final int integer2 = this.e();
        this.viewDistance = integer;
        final int integer3 = this.e();
        for (final Long2ObjectMap.Entry<ObjectSet<ServerPlayerEntity>> entry5 : this.playersByChunkSectionPos.long2ObjectEntrySet()) {
            this.shouldEntityTickTracker.update(entry5.getLongKey(), integer3, integer3 < integer2);
        }
    }
    
    protected void purge() {
        ++this.location;
        final ObjectIterator<Long2ObjectMap.Entry<ObjectSortedSet<ChunkTicket<?>>>> objectIterator1 = (ObjectIterator<Long2ObjectMap.Entry<ObjectSortedSet<ChunkTicket<?>>>>)this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator1.hasNext()) {
            final Long2ObjectMap.Entry<ObjectSortedSet<ChunkTicket<?>>> entry2 = (Long2ObjectMap.Entry<ObjectSortedSet<ChunkTicket<?>>>)objectIterator1.next();
            if (((ObjectSortedSet)entry2.getValue()).removeIf(chunkTicket -> chunkTicket.getType() == ChunkTicketType.UNKNOWN && chunkTicket.getLocation() != this.location)) {
                this.distanceFromTicketTracker.update(entry2.getLongKey(), this.getLevel((ObjectSortedSet<ChunkTicket<?>>)entry2.getValue()), false);
            }
            if (((ObjectSortedSet)entry2.getValue()).isEmpty()) {
                objectIterator1.remove();
            }
        }
    }
    
    private int getLevel(final ObjectSortedSet<ChunkTicket<?>> ticketSet) {
        final ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator2 = (ObjectBidirectionalIterator<ChunkTicket<?>>)ticketSet.iterator();
        if (objectBidirectionalIterator2.hasNext()) {
            return ((ChunkTicket)objectBidirectionalIterator2.next()).getLevel();
        }
        return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
    }
    
    protected abstract boolean isUnloaded(final long arg1);
    
    @Nullable
    protected abstract ChunkHolder getChunkHolder(final long arg1);
    
    @Nullable
    protected abstract ChunkHolder setLevel(final long arg1, final int arg2, @Nullable final ChunkHolder arg3, final int arg4);
    
    public boolean tick(final ThreadedAnvilChunkStorage chunkStorage) {
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        this.shouldEntityTickTracker.a();
        final int integer2 = Integer.MAX_VALUE - this.distanceFromTicketTracker.a(Integer.MAX_VALUE);
        final boolean boolean3 = integer2 != 0;
        if (boolean3) {}
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(chunkHolder -> chunkHolder.tick(chunkStorage));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            final LongIterator longIterator4 = this.chunkPositions.iterator();
            while (longIterator4.hasNext()) {
                final long long5 = longIterator4.nextLong();
                if (this.getTicketSet(long5).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.PLAYER)) {
                    final ChunkHolder chunkHolder2 = chunkStorage.getCurrentChunkHolder(long5);
                    if (chunkHolder2 == null) {
                        throw new IllegalStateException();
                    }
                    final CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture8 = chunkHolder2.getEntityTickingFuture();
                    completableFuture8.thenAccept(either3 -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, long5, false))));
                }
            }
            this.chunkPositions.clear();
        }
        return boolean3;
    }
    
    private void addTicket(final long position, final ChunkTicket<?> ticket) {
        final ObjectSortedSet<ChunkTicket<?>> objectSortedSet4 = this.getTicketSet(position);
        final ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator6 = (ObjectBidirectionalIterator<ChunkTicket<?>>)objectSortedSet4.iterator();
        int integer5;
        if (objectBidirectionalIterator6.hasNext()) {
            integer5 = ((ChunkTicket)objectBidirectionalIterator6.next()).getLevel();
        }
        else {
            integer5 = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }
        if (objectSortedSet4.add(ticket)) {}
        if (ticket.getLevel() < integer5) {
            this.distanceFromTicketTracker.update(position, ticket.getLevel(), true);
        }
    }
    
    private void removeTicket(final long pos, final ChunkTicket<?> ticket) {
        final ObjectSortedSet<ChunkTicket<?>> objectSortedSet4 = this.getTicketSet(pos);
        if (objectSortedSet4.remove(ticket)) {}
        if (objectSortedSet4.isEmpty()) {
            this.ticketsByPosition.remove(pos);
        }
        this.distanceFromTicketTracker.update(pos, this.getLevel(objectSortedSet4), false);
    }
    
    public <T> void addTicketWithLevel(final ChunkTicketType<T> type, final ChunkPos pos, final int level, final T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<>(type, level, argument, this.location));
    }
    
    public <T> void b(final ChunkTicketType<T> chunkTicketType, final ChunkPos chunkPos, final int integer, final T object) {
        final ChunkTicket<T> chunkTicket5 = new ChunkTicket<T>(chunkTicketType, integer, object, this.location);
        this.removeTicket(chunkPos.toLong(), chunkTicket5);
    }
    
    public <T> void addTicket(final ChunkTicketType<T> type, final ChunkPos pos, final int radius, final T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<>(type, 33 - radius, argument, this.location));
    }
    
    public <T> void removeTicket(final ChunkTicketType<T> type, final ChunkPos pos, final int radius, final T argument) {
        final ChunkTicket<T> chunkTicket5 = new ChunkTicket<T>(type, 33 - radius, argument, this.location);
        this.removeTicket(pos.toLong(), chunkTicket5);
    }
    
    private ObjectSortedSet<ChunkTicket<?>> getTicketSet(final long position) {
        return (ObjectSortedSet<ChunkTicket<?>>)this.ticketsByPosition.computeIfAbsent(position, long1 -> new ObjectAVLTreeSet());
    }
    
    protected void setChunkForced(final ChunkPos chunkPos, final boolean forced) {
        final ChunkTicket<ChunkPos> chunkTicket3 = new ChunkTicket<ChunkPos>(ChunkTicketType.FORCED, 32, chunkPos, this.location);
        if (forced) {
            this.addTicket(chunkPos.toLong(), chunkTicket3);
        }
        else {
            this.removeTicket(chunkPos.toLong(), chunkTicket3);
        }
    }
    
    private int e() {
        return 16 - this.viewDistance;
    }
    
    public void handleChunkEnter(final ChunkSectionPos pos, final ServerPlayerEntity player) {
        final long long3 = pos.toChunkPos().toLong();
        player.setCameraPosition(pos);
        player.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(pos.getChunkX(), pos.getChunkZ()));
        ((ObjectSet)this.playersByChunkSectionPos.computeIfAbsent(pos.asLong(), long1 -> new ObjectOpenHashSet())).add(player);
        ((ObjectSet)this.playersByChunkPos.computeIfAbsent(long3, long1 -> new ObjectOpenHashSet())).add(player);
        this.distanceFromNearestPlayerTracker.update(long3, 0, true);
        this.nearbyChunkTicketUpdater.update(long3, 0, true);
        this.shouldEntityTickTracker.update(pos.asLong(), this.e(), true);
    }
    
    public void handleChunkLeave(final ChunkSectionPos pos, final ServerPlayerEntity player) {
        final long long3 = pos.toChunkPos().toLong();
        final ObjectSet<ServerPlayerEntity> objectSet5 = (ObjectSet<ServerPlayerEntity>)this.playersByChunkSectionPos.get(pos.asLong());
        if (objectSet5 == null) {
            return;
        }
        objectSet5.remove(player);
        if (objectSet5.isEmpty()) {
            this.playersByChunkSectionPos.remove(pos.asLong());
            this.shouldEntityTickTracker.update(pos.asLong(), Integer.MAX_VALUE, false);
        }
        final ObjectSet<ServerPlayerEntity> objectSet6 = (ObjectSet<ServerPlayerEntity>)this.playersByChunkPos.get(long3);
        objectSet6.remove(player);
        if (objectSet6.isEmpty()) {
            this.playersByChunkPos.remove(long3);
            this.distanceFromNearestPlayerTracker.update(long3, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.update(long3, Integer.MAX_VALUE, false);
        }
    }
    
    protected void setWatchDistance(final int viewDistance) {
        this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
    }
    
    public int getLevelCount() {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL) - 2;
    }
    
    class DistanceFromNearestPlayerTracker extends ChunkPosLevelPropagator
    {
        protected final Long2ByteMap distanceFromNearestPlayer;
        protected final int maxDistance;
        
        protected DistanceFromNearestPlayerTracker(final int integer) {
            super(integer + 2, 16, 256);
            this.distanceFromNearestPlayer = (Long2ByteMap)new Long2ByteOpenHashMap();
            this.maxDistance = integer;
            this.distanceFromNearestPlayer.defaultReturnValue((byte)(integer + 2));
        }
        
        @Override
        protected int getLevel(final long id) {
            return this.distanceFromNearestPlayer.get(id);
        }
        
        @Override
        protected void setLevel(final long id, final int level) {
            byte byte4;
            if (level > this.maxDistance) {
                byte4 = this.distanceFromNearestPlayer.remove(id);
            }
            else {
                byte4 = this.distanceFromNearestPlayer.put(id, (byte)level);
            }
            this.onDistanceChange(id, byte4, level);
        }
        
        protected void onDistanceChange(final long pos, final int oldDistance, final int distance) {
        }
        
        @Override
        protected int getInitialLevel(final long id) {
            return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
        }
        
        private boolean isPlayerInChunk(final long chunkPos) {
            final ObjectSet<ServerPlayerEntity> objectSet3 = (ObjectSet<ServerPlayerEntity>)ChunkTicketManager.this.playersByChunkPos.get(chunkPos);
            return objectSet3 != null && !objectSet3.isEmpty();
        }
        
        public void updateLevels() {
            this.updateAllRecursively(Integer.MAX_VALUE);
        }
    }
    
    class c extends SectionRelativeLevelPropagator
    {
        protected final Long2ByteMap a;
        
        protected c() {
            super(18, 16, 256);
            (this.a = (Long2ByteMap)new Long2ByteOpenHashMap()).defaultReturnValue((byte)18);
        }
        
        @Override
        protected int getLevel(final long id) {
            return this.a.get(id);
        }
        
        @Override
        protected void setLevel(final long id, final int level) {
            if (level > 16) {
                this.a.remove(id);
            }
            else {
                this.a.put(id, (byte)level);
            }
        }
        
        @Override
        protected int getInitialLevel(final long id) {
            return this.d(id) ? ChunkTicketManager.this.e() : Integer.MAX_VALUE;
        }
        
        private boolean d(final long long1) {
            final ObjectSet<ServerPlayerEntity> objectSet3 = (ObjectSet<ServerPlayerEntity>)ChunkTicketManager.this.playersByChunkSectionPos.get(long1);
            return objectSet3 != null && !objectSet3.isEmpty();
        }
        
        public void a() {
            this.updateAllRecursively(Integer.MAX_VALUE);
        }
    }
    
    class NearbyChunkTicketUpdater extends DistanceFromNearestPlayerTracker
    {
        private int watchDistance;
        private final Long2IntMap distances;
        private final LongSet positionsAffected;
        
        protected NearbyChunkTicketUpdater(final int integer) {
            super(integer);
            this.distances = Long2IntMaps.synchronize((Long2IntMap)new Long2IntOpenHashMap());
            this.positionsAffected = (LongSet)new LongOpenHashSet();
            this.watchDistance = 0;
            this.distances.defaultReturnValue(integer + 2);
        }
        
        @Override
        protected void onDistanceChange(final long pos, final int oldDistance, final int distance) {
            this.positionsAffected.add(pos);
        }
        
        public void setWatchDistance(final int watchDistance) {
            for (final Long2ByteMap.Entry entry3 : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                final byte byte4 = entry3.getByteValue();
                final long long5 = entry3.getLongKey();
                this.updateTicket(long5, byte4, this.isWithinViewDistance(byte4), byte4 <= watchDistance - 2);
            }
            this.watchDistance = watchDistance;
        }
        
        private void updateTicket(final long pos, final int distance, final boolean oldWithinViewDistance, final boolean withinViewDistance) {
            if (oldWithinViewDistance != withinViewDistance) {
                final ChunkTicket<?> chunkTicket6 = new ChunkTicket<>(ChunkTicketType.PLAYER, ChunkTicketManager.NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos), ChunkTicketManager.this.location);
                if (withinViewDistance) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        ChunkTicketManager.this.addTicket(pos, chunkTicket6);
                        ChunkTicketManager.this.chunkPositions.add(pos);
                    }), pos, () -> distance));
                }
                else {
                    ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, chunkTicket6)), pos, true));
                }
            }
        }
        
        @Override
        public void updateLevels() {
            super.updateLevels();
            if (!this.positionsAffected.isEmpty()) {
                final LongIterator longIterator1 = this.positionsAffected.iterator();
                while (longIterator1.hasNext()) {
                    final long long2 = longIterator1.nextLong();
                    final int integer4 = this.distances.get(long2);
                    final int integer5 = this.getLevel(long2);
                    if (integer4 != integer5) {
                        ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(long2), () -> this.distances.get(long2), integer5, integer3 -> this.distances.put(long2, integer3));
                        this.updateTicket(long2, integer5, this.isWithinViewDistance(integer4), this.isWithinViewDistance(integer5));
                    }
                }
                this.positionsAffected.clear();
            }
        }
        
        private boolean isWithinViewDistance(final int distance) {
            return distance <= this.watchDistance - 2;
        }
    }
    
    class a extends ChunkPosLevelPropagator
    {
        public a() {
            super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
        }
        
        @Override
        protected int getInitialLevel(final long id) {
            final ObjectSortedSet<ChunkTicket<?>> objectSortedSet3 = (ObjectSortedSet<ChunkTicket<?>>)ChunkTicketManager.this.ticketsByPosition.get(id);
            if (objectSortedSet3 == null) {
                return Integer.MAX_VALUE;
            }
            final ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator4 = (ObjectBidirectionalIterator<ChunkTicket<?>>)objectSortedSet3.iterator();
            if (!objectBidirectionalIterator4.hasNext()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)objectBidirectionalIterator4.next()).getLevel();
        }
        
        @Override
        protected int getLevel(final long id) {
            if (!ChunkTicketManager.this.isUnloaded(id)) {
                final ChunkHolder chunkHolder3 = ChunkTicketManager.this.getChunkHolder(id);
                if (chunkHolder3 != null) {
                    return chunkHolder3.getLevel();
                }
            }
            return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }
        
        @Override
        protected void setLevel(final long id, final int level) {
            ChunkHolder chunkHolder4 = ChunkTicketManager.this.getChunkHolder(id);
            final int integer5 = (chunkHolder4 == null) ? (ThreadedAnvilChunkStorage.MAX_LEVEL + 1) : chunkHolder4.getLevel();
            if (integer5 == level) {
                return;
            }
            chunkHolder4 = ChunkTicketManager.this.setLevel(id, level, chunkHolder4, integer5);
            if (chunkHolder4 != null) {
                ChunkTicketManager.this.chunkHolders.add(chunkHolder4);
            }
        }
        
        public int a(final int integer) {
            return this.updateAllRecursively(integer);
        }
    }
}
