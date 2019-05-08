package net.minecraft.client.render.chunk;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.math.Vec3d;
import java.util.concurrent.CancellationException;
import java.util.List;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import net.minecraft.block.BlockRenderLayer;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderWorker implements Runnable
{
    private static final Logger LOGGER;
    private final ChunkBatcher batcher;
    private final BlockLayeredBufferBuilder bufferBuilders;
    private boolean running;
    
    public ChunkRenderWorker(final ChunkBatcher chunkBatcher) {
        this(chunkBatcher, null);
    }
    
    public ChunkRenderWorker(final ChunkBatcher batcher, @Nullable final BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        this.running = true;
        this.batcher = batcher;
        this.bufferBuilders = blockLayeredBufferBuilder;
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                this.runTask(this.batcher.getNextChunkRenderDataTask());
                continue;
            }
            catch (InterruptedException interruptedException1) {
                ChunkRenderWorker.LOGGER.debug("Stopping chunk worker due to interrupt");
                return;
            }
            catch (Throwable throwable1) {
                final CrashReport crashReport2 = CrashReport.create(throwable1, "Batching chunks");
                MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().populateCrashReport(crashReport2));
                return;
            }
            break;
        }
    }
    
    protected void runTask(final ChunkRenderTask chunkRenderTask) throws InterruptedException {
        chunkRenderTask.getLock().lock();
        try {
            if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.a) {
                if (!chunkRenderTask.isCancelled()) {
                    ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", chunkRenderTask.getStage());
                }
                return;
            }
            if (!chunkRenderTask.getChunkRenderer().b()) {
                chunkRenderTask.cancel();
                return;
            }
            chunkRenderTask.setStage(ChunkRenderTask.Stage.b);
        }
        finally {
            chunkRenderTask.getLock().unlock();
        }
        chunkRenderTask.setBufferBuilders(this.getBufferBuilders());
        final Vec3d vec3d2 = this.batcher.b();
        final float float3 = (float)vec3d2.x;
        final float float4 = (float)vec3d2.y;
        final float float5 = (float)vec3d2.z;
        final ChunkRenderTask.Mode mode6 = chunkRenderTask.getMode();
        if (mode6 == ChunkRenderTask.Mode.a) {
            chunkRenderTask.getChunkRenderer().rebuildChunk(float3, float4, float5, chunkRenderTask);
        }
        else if (mode6 == ChunkRenderTask.Mode.b) {
            chunkRenderTask.getChunkRenderer().resortTransparency(float3, float4, float5, chunkRenderTask);
        }
        chunkRenderTask.getLock().lock();
        try {
            if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.b) {
                if (!chunkRenderTask.isCancelled()) {
                    ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", chunkRenderTask.getStage());
                }
                this.freeRenderTask(chunkRenderTask);
                return;
            }
            chunkRenderTask.setStage(ChunkRenderTask.Stage.c);
        }
        finally {
            chunkRenderTask.getLock().unlock();
        }
        final ChunkRenderData chunkRenderData7 = chunkRenderTask.getRenderData();
        final List<ListenableFuture<Object>> list8 = Lists.newArrayList();
        if (mode6 == ChunkRenderTask.Mode.a) {
            for (final BlockRenderLayer blockRenderLayer12 : BlockRenderLayer.values()) {
                if (chunkRenderData7.isBufferInitialized(blockRenderLayer12)) {
                    list8.add(this.batcher.a(blockRenderLayer12, chunkRenderTask.getBufferBuilders().get(blockRenderLayer12), chunkRenderTask.getChunkRenderer(), chunkRenderData7, chunkRenderTask.getDistanceToPlayerSquared()));
                }
            }
        }
        else if (mode6 == ChunkRenderTask.Mode.b) {
            list8.add(this.batcher.a(BlockRenderLayer.TRANSLUCENT, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), chunkRenderTask.getChunkRenderer(), chunkRenderData7, chunkRenderTask.getDistanceToPlayerSquared()));
        }
        final ListenableFuture<List<Object>> listenableFuture9 = Futures.allAsList(list8);
        chunkRenderTask.add(() -> listenableFuture9.cancel(false));
        Futures.addCallback((ListenableFuture)listenableFuture9, (FutureCallback)new FutureCallback<List<Object>>() {
            public void a(@Nullable final List<Object> list) {
                ChunkRenderWorker.this.freeRenderTask(chunkRenderTask);
                chunkRenderTask.getLock().lock();
                try {
                    if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.c) {
                        if (!chunkRenderTask.isCancelled()) {
                            ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", chunkRenderTask.getStage());
                        }
                        return;
                    }
                    chunkRenderTask.setStage(ChunkRenderTask.Stage.d);
                }
                finally {
                    chunkRenderTask.getLock().unlock();
                }
                chunkRenderTask.getChunkRenderer().setChunkRenderData(chunkRenderData7);
            }
            
            @Override
            public void onFailure(final Throwable throwable) {
                ChunkRenderWorker.this.freeRenderTask(chunkRenderTask);
                if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                    MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                }
            }
        });
    }
    
    private BlockLayeredBufferBuilder getBufferBuilders() throws InterruptedException {
        return (this.bufferBuilders != null) ? this.bufferBuilders : this.batcher.getNextAvailableBuffer();
    }
    
    private void freeRenderTask(final ChunkRenderTask chunkRenderTask) {
        if (this.bufferBuilders == null) {
            this.batcher.addAvailableBuffer(chunkRenderTask.getBufferBuilders());
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
