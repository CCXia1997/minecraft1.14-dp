package net.minecraft.client.render.chunk;

import com.google.common.primitives.Doubles;
import java.util.Iterator;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.world.SafeWorldView;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask>
{
    private final ChunkRenderer chunkRenderer;
    private final ReentrantLock lock;
    private final List<Runnable> runnables;
    private final Mode mode;
    private final double distanceToPlayerSquared;
    @Nullable
    private SafeWorldView worldView;
    private BlockLayeredBufferBuilder bufferBuilder;
    private ChunkRenderData renderData;
    private Stage stage;
    private boolean cancelled;
    
    public ChunkRenderTask(final ChunkRenderer chunkRenderer, final Mode mode, final double distanceToPlayerSquared, @Nullable final SafeWorldView safeWorldView5) {
        this.lock = new ReentrantLock();
        this.runnables = Lists.newArrayList();
        this.stage = Stage.a;
        this.chunkRenderer = chunkRenderer;
        this.mode = mode;
        this.distanceToPlayerSquared = distanceToPlayerSquared;
        this.worldView = safeWorldView5;
    }
    
    public Stage getStage() {
        return this.stage;
    }
    
    public ChunkRenderer getChunkRenderer() {
        return this.chunkRenderer;
    }
    
    @Nullable
    public SafeWorldView getAndInvalidateWorldView() {
        final SafeWorldView safeWorldView1 = this.worldView;
        this.worldView = null;
        return safeWorldView1;
    }
    
    public ChunkRenderData getRenderData() {
        return this.renderData;
    }
    
    public void setRenderData(final ChunkRenderData renderData) {
        this.renderData = renderData;
    }
    
    public BlockLayeredBufferBuilder getBufferBuilders() {
        return this.bufferBuilder;
    }
    
    public void setBufferBuilders(final BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        this.bufferBuilder = blockLayeredBufferBuilder;
    }
    
    public void setStage(final Stage stage) {
        this.lock.lock();
        try {
            this.stage = stage;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void cancel() {
        this.lock.lock();
        try {
            this.worldView = null;
            if (this.mode == Mode.a && this.stage != Stage.d) {
                this.chunkRenderer.scheduleRender(false);
            }
            this.cancelled = true;
            this.stage = Stage.d;
            for (final Runnable runnable2 : this.runnables) {
                runnable2.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void add(final Runnable runnable) {
        this.lock.lock();
        try {
            this.runnables.add(runnable);
            if (this.cancelled) {
                runnable.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public ReentrantLock getLock() {
        return this.lock;
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public int a(final ChunkRenderTask chunkRenderTask) {
        return Doubles.compare(this.distanceToPlayerSquared, chunkRenderTask.distanceToPlayerSquared);
    }
    
    public double getDistanceToPlayerSquared() {
        return this.distanceToPlayerSquared;
    }
    
    @Environment(EnvType.CLIENT)
    public enum Mode
    {
        a, 
        b;
    }
    
    @Environment(EnvType.CLIENT)
    public enum Stage
    {
        a, 
        b, 
        c, 
        d;
    }
}
