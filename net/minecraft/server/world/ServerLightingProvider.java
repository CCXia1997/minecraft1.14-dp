package net.minecraft.server.world;

import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.chunk.ChunkSection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.chunk.Chunk;
import java.util.function.IntSupplier;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.LightType;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.chunk.ChunkProvider;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.Actor;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.MailboxProcessor;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.chunk.light.LightingProvider;

public class ServerLightingProvider extends LightingProvider implements AutoCloseable
{
    private static final Logger LOGGER;
    private final MailboxProcessor<Runnable> processor;
    private final ObjectList<Pair<a, Runnable>> pendingTasks;
    private final ThreadedAnvilChunkStorage chunkStorage;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor;
    private volatile int taskBatchSize;
    private final AtomicBoolean g;
    
    public ServerLightingProvider(final ChunkProvider chunkProvider, final ThreadedAnvilChunkStorage chunkStorage, final boolean boolean3, final MailboxProcessor<Runnable> processor, final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor) {
        super(chunkProvider, true, boolean3);
        this.pendingTasks = (ObjectList<Pair<a, Runnable>>)new ObjectArrayList();
        this.taskBatchSize = 5;
        this.g = new AtomicBoolean();
        this.chunkStorage = chunkStorage;
        this.actor = actor;
        this.processor = processor;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public int doLightUpdates(final int maxUpdateCount, final boolean boolean2, final boolean boolean3) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }
    
    @Override
    public void a(final BlockPos pos, final int integer) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }
    
    @Override
    public void enqueueLightUpdate(final BlockPos pos) {
        final BlockPos blockPos2 = pos.toImmutable();
        this.enqueue(pos.getX() >> 4, pos.getZ() >> 4, a.b, SystemUtil.debugRunnable(() -> super.enqueueLightUpdate(blockPos2), () -> "checkBlock " + blockPos2));
    }
    
    protected void a(final ChunkPos chunkPos) {
        int integer2;
        this.enqueue(chunkPos.x, chunkPos.z, () -> 0, a.a, SystemUtil.debugRunnable(() -> {
            for (integer2 = 0; integer2 < 16; ++integer2) {
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, integer2), true);
            }
        }, () -> "updateChunkStatus " + chunkPos + " " + true));
    }
    
    @Override
    public void updateSectionStatus(final ChunkSectionPos pos, final boolean status) {
        this.enqueue(pos.getChunkX(), pos.getChunkZ(), () -> 0, a.a, SystemUtil.debugRunnable(() -> super.updateSectionStatus(pos, status), () -> "updateSectionStatus " + pos + " " + status));
    }
    
    @Override
    public void suppressLight(final ChunkPos chunkPos, final boolean boolean2) {
        this.enqueue(chunkPos.x, chunkPos.z, a.a, SystemUtil.debugRunnable(() -> super.suppressLight(chunkPos, boolean2), () -> "enableLight " + chunkPos + " " + boolean2));
    }
    
    @Override
    public void queueData(final LightType lightType, final ChunkSectionPos chunkSectionPos, final ChunkNibbleArray chunkNibbleArray) {
        this.enqueue(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkZ(), a.a, SystemUtil.debugRunnable(() -> super.queueData(lightType, chunkSectionPos, chunkNibbleArray), () -> "queueData " + chunkSectionPos));
    }
    
    private void enqueue(final int x, final int z, final a stage, final Runnable task) {
        this.enqueue(x, z, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
    }
    
    private void enqueue(final int x, final int z, final IntSupplier completedLevelSupplier, final a stage, final Runnable task) {
        this.actor.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> {
            this.pendingTasks.add(Pair.of((Object)stage, (Object)task));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(x, z), completedLevelSupplier));
    }
    
    public CompletableFuture<Chunk> light(final Chunk chunk, final boolean boolean2) {
        final ChunkPos chunkPos3 = chunk.getPos();
        final ChunkSection[] arr4;
        int integer5;
        ChunkSection chunkSection6;
        final ChunkPos chunkPos4;
        this.enqueue(chunkPos3.x, chunkPos3.z, a.a, SystemUtil.debugRunnable(() -> {
            arr4 = chunk.getSectionArray();
            for (integer5 = 0; integer5 < 16; ++integer5) {
                chunkSection6 = arr4[integer5];
                if (!ChunkSection.isEmpty(chunkSection6)) {
                    super.updateSectionStatus(ChunkSectionPos.from(chunkPos4, integer5), false);
                }
            }
            super.suppressLight(chunkPos4, true);
            if (!boolean2) {
                chunk.getLightSourcesStream().forEach(blockPos -> super.a(blockPos, chunk.getLuminance(blockPos)));
            }
            chunk.setLightOn(true);
            this.chunkStorage.c(chunkPos4);
            return;
        }, () -> "lightChunk " + chunkPos3 + " " + boolean2));
        final ChunkPos chunkPos5;
        return CompletableFuture.<Chunk>supplyAsync(() -> chunk, runnable -> this.enqueue(chunkPos5.x, chunkPos5.z, a.b, runnable));
    }
    
    public void tick() {
        if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.g.compareAndSet(false, true)) {
            this.processor.send(() -> {
                this.runTasks();
                this.g.set(false);
            });
        }
    }
    
    private void runTasks() {
        int integer1;
        ObjectListIterator<Pair<a, Runnable>> objectListIterator2;
        int integer2;
        Pair<a, Runnable> pair4;
        for (integer1 = Math.min(this.pendingTasks.size(), this.taskBatchSize), objectListIterator2 = (ObjectListIterator<Pair<a, Runnable>>)this.pendingTasks.iterator(), integer2 = 0; objectListIterator2.hasNext() && integer2 < integer1; ++integer2) {
            pair4 = (Pair<a, Runnable>)objectListIterator2.next();
            if (pair4.getFirst() == a.a) {
                ((Runnable)pair4.getSecond()).run();
            }
        }
        objectListIterator2.back(integer2);
        super.doLightUpdates(Integer.MAX_VALUE, true, true);
        for (integer2 = 0; objectListIterator2.hasNext() && integer2 < integer1; ++integer2) {
            pair4 = (Pair<a, Runnable>)objectListIterator2.next();
            if (pair4.getFirst() == a.b) {
                ((Runnable)pair4.getSecond()).run();
            }
            objectListIterator2.remove();
        }
    }
    
    public void setTaskBatchSize(final int taskBatchSize) {
        this.taskBatchSize = taskBatchSize;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    enum a
    {
        a, 
        b;
    }
}
