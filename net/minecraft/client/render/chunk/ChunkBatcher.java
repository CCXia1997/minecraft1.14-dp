package net.minecraft.client.render.chunk;

import com.google.common.primitives.Doubles;
import net.minecraft.util.UncaughtExceptionLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.client.gl.GlBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.Futures;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.MinecraftClient;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.util.SystemUtil;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;
import java.util.Queue;
import net.minecraft.client.gl.GlBufferRenderer;
import net.minecraft.client.render.BufferRenderer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkBatcher
{
    private static final Logger LOGGER;
    private static final ThreadFactory THREAD_FACTORY;
    private final int c;
    private final List<Thread> workerThreads;
    private final List<ChunkRenderWorker> workers;
    private final PriorityBlockingQueue<ChunkRenderTask> pendingChunks;
    private final BlockingQueue<BlockLayeredBufferBuilder> availableBuffers;
    private final BufferRenderer bufferRenderer;
    private final GlBufferRenderer i;
    private final Queue<ChunkUploadTask> pendingUploads;
    private final ChunkRenderWorker activeWorker;
    private Vec3d l;
    
    public ChunkBatcher() {
        this.workerThreads = Lists.newArrayList();
        this.workers = Lists.newArrayList();
        this.pendingChunks = Queues.<ChunkRenderTask>newPriorityBlockingQueue();
        this.bufferRenderer = new BufferRenderer();
        this.i = new GlBufferRenderer();
        this.pendingUploads = Queues.newPriorityQueue();
        this.l = Vec3d.ZERO;
        final int integer1 = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
        final int integer2 = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, integer1 / 5));
        final int integer3 = MathHelper.clamp(integer2 * 10, 1, integer1);
        if (integer2 > 1) {
            for (int integer4 = 0; integer4 < integer2; ++integer4) {
                final ChunkRenderWorker chunkRenderWorker5 = new ChunkRenderWorker(this);
                final Thread thread6 = ChunkBatcher.THREAD_FACTORY.newThread(chunkRenderWorker5);
                thread6.start();
                this.workers.add(chunkRenderWorker5);
                this.workerThreads.add(thread6);
            }
        }
        final List<BlockLayeredBufferBuilder> list4 = Lists.newArrayListWithExpectedSize(integer3);
        try {
            list4.add(new BlockLayeredBufferBuilder());
        }
        catch (OutOfMemoryError outOfMemoryError5) {
            ChunkBatcher.LOGGER.error("Allocated only {}/{} buffers", list4.size(), integer3);
            list4.remove(list4.size() - 1);
            System.gc();
        }
        this.c = list4.size();
        (this.availableBuffers = Queues.newArrayBlockingQueue(this.c)).addAll(list4);
        this.activeWorker = new ChunkRenderWorker(this, new BlockLayeredBufferBuilder());
    }
    
    public String getDebugString() {
        if (this.workerThreads.isEmpty()) {
            return String.format("pC: %03d, single-threaded", this.pendingChunks.size());
        }
        return String.format("pC: %03d, pU: %1d, aB: %1d", this.pendingChunks.size(), this.pendingUploads.size(), this.availableBuffers.size());
    }
    
    public void a(final Vec3d vec3d) {
        this.l = vec3d;
    }
    
    public Vec3d b() {
        return this.l;
    }
    
    public boolean a(final long long1) {
        boolean boolean3 = false;
        do {
            boolean boolean4 = false;
            if (this.workerThreads.isEmpty()) {
                final ChunkRenderTask chunkRenderTask5 = this.pendingChunks.poll();
                if (chunkRenderTask5 != null) {
                    try {
                        this.activeWorker.runTask(chunkRenderTask5);
                        boolean4 = true;
                    }
                    catch (InterruptedException interruptedException6) {
                        ChunkBatcher.LOGGER.warn("Skipped task due to interrupt");
                    }
                }
            }
            synchronized (this.pendingUploads) {
                if (!this.pendingUploads.isEmpty()) {
                    this.pendingUploads.poll().task.run();
                    boolean4 = true;
                    boolean3 = true;
                }
            }
            if (long1 == 0L) {
                break;
            }
            if (!boolean4) {
                break;
            }
        } while (long1 >= SystemUtil.getMeasuringTimeNano());
        return boolean3;
    }
    
    public boolean a(final ChunkRenderer chunkRenderer) {
        chunkRenderer.getChunkRenderLock().lock();
        try {
            final ChunkRenderTask chunkRenderTask2 = chunkRenderer.e();
            chunkRenderTask2.add(() -> this.pendingChunks.remove(chunkRenderTask2));
            final boolean boolean3 = this.pendingChunks.offer(chunkRenderTask2);
            if (!boolean3) {
                chunkRenderTask2.cancel();
            }
            return boolean3;
        }
        finally {
            chunkRenderer.getChunkRenderLock().unlock();
        }
    }
    
    public boolean b(final ChunkRenderer chunkRenderer) {
        chunkRenderer.getChunkRenderLock().lock();
        try {
            final ChunkRenderTask chunkRenderTask2 = chunkRenderer.e();
            try {
                this.activeWorker.runTask(chunkRenderTask2);
            }
            catch (InterruptedException ex) {}
            return true;
        }
        finally {
            chunkRenderer.getChunkRenderLock().unlock();
        }
    }
    
    public void c() {
        this.f();
        final List<BlockLayeredBufferBuilder> list1 = Lists.newArrayList();
        while (list1.size() != this.c) {
            this.a(Long.MAX_VALUE);
            try {
                list1.add(this.getNextAvailableBuffer());
            }
            catch (InterruptedException ex) {}
        }
        this.availableBuffers.addAll(list1);
    }
    
    public void addAvailableBuffer(final BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        this.availableBuffers.add(blockLayeredBufferBuilder);
    }
    
    public BlockLayeredBufferBuilder getNextAvailableBuffer() throws InterruptedException {
        return this.availableBuffers.take();
    }
    
    public ChunkRenderTask getNextChunkRenderDataTask() throws InterruptedException {
        return this.pendingChunks.take();
    }
    
    public boolean c(final ChunkRenderer chunkRenderer) {
        chunkRenderer.getChunkRenderLock().lock();
        try {
            final ChunkRenderTask chunkRenderTask2 = chunkRenderer.getResortTransparencyTask();
            if (chunkRenderTask2 != null) {
                chunkRenderTask2.add(() -> this.pendingChunks.remove(chunkRenderTask2));
                return this.pendingChunks.offer(chunkRenderTask2);
            }
            return true;
        }
        finally {
            chunkRenderer.getChunkRenderLock().unlock();
        }
    }
    
    public ListenableFuture<Object> a(final BlockRenderLayer blockRenderLayer, final BufferBuilder bufferBuilder, final ChunkRenderer chunkRenderer, final ChunkRenderData chunkRenderData, final double double5) {
        if (MinecraftClient.getInstance().isOnThread()) {
            if (GLX.useVbo()) {
                this.a(bufferBuilder, chunkRenderer.getGlBuffer(blockRenderLayer.ordinal()));
            }
            else {
                this.a(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).a(blockRenderLayer, chunkRenderData));
            }
            bufferBuilder.setOffset(0.0, 0.0, 0.0);
            return Futures.immediateFuture(null);
        }
        final ListenableFutureTask<Object> listenableFutureTask7 = ListenableFutureTask.create(() -> this.a(blockRenderLayer, bufferBuilder, chunkRenderer, chunkRenderData, double5), null);
        synchronized (this.pendingUploads) {
            this.pendingUploads.add(new ChunkUploadTask(listenableFutureTask7, double5));
        }
        return listenableFutureTask7;
    }
    
    private void a(final BufferBuilder bufferBuilder, final int integer) {
        GlStateManager.newList(integer, 4864);
        this.bufferRenderer.draw(bufferBuilder);
        GlStateManager.endList();
    }
    
    private void a(final BufferBuilder bufferBuilder, final GlBuffer glBuffer) {
        this.i.setGlBuffer(glBuffer);
        this.i.draw(bufferBuilder);
    }
    
    public void f() {
        while (!this.pendingChunks.isEmpty()) {
            final ChunkRenderTask chunkRenderTask1 = this.pendingChunks.poll();
            if (chunkRenderTask1 != null) {
                chunkRenderTask1.cancel();
            }
        }
    }
    
    public boolean isEmpty() {
        return this.pendingChunks.isEmpty() && this.pendingUploads.isEmpty();
    }
    
    public void h() {
        this.f();
        for (final ChunkRenderWorker chunkRenderWorker2 : this.workers) {
            chunkRenderWorker2.stop();
        }
        for (final Thread thread2 : this.workerThreads) {
            try {
                thread2.interrupt();
                thread2.join();
            }
            catch (InterruptedException interruptedException3) {
                ChunkBatcher.LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)interruptedException3);
            }
        }
        this.availableBuffers.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(ChunkBatcher.LOGGER)).build();
    }
    
    @Environment(EnvType.CLIENT)
    class ChunkUploadTask implements Comparable<ChunkUploadTask>
    {
        private final ListenableFutureTask<Object> task;
        private final double priority;
        
        public ChunkUploadTask(final ListenableFutureTask<Object> listenableFutureTask, final double double3) {
            this.task = listenableFutureTask;
            this.priority = double3;
        }
        
        public int a(final ChunkUploadTask chunkUploadTask) {
            return Doubles.compare(this.priority, chunkUploadTask.priority);
        }
    }
}
