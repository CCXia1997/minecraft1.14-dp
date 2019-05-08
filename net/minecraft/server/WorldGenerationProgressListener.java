package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkPos;

public interface WorldGenerationProgressListener
{
    void start(final ChunkPos arg1);
    
    void setChunkStatus(final ChunkPos arg1, @Nullable final ChunkStatus arg2);
    
    void stop();
}
