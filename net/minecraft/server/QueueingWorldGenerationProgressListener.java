package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkPos;
import java.util.concurrent.Executor;
import net.minecraft.util.MailboxProcessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QueueingWorldGenerationProgressListener implements WorldGenerationProgressListener
{
    private final WorldGenerationProgressListener progressListener;
    private final MailboxProcessor<Runnable> queue;
    
    public QueueingWorldGenerationProgressListener(final WorldGenerationProgressListener progressListener, final Executor executor) {
        this.progressListener = progressListener;
        this.queue = MailboxProcessor.create(executor, "progressListener");
    }
    
    @Override
    public void start(final ChunkPos spawnPos) {
        this.queue.send(() -> this.progressListener.start(spawnPos));
    }
    
    @Override
    public void setChunkStatus(final ChunkPos pos, @Nullable final ChunkStatus status) {
        this.queue.send(() -> this.progressListener.setChunkStatus(pos, status));
    }
    
    @Override
    public void stop() {
        this.queue.send(this.progressListener::stop);
    }
}
