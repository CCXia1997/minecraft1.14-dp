package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;

@Environment(EnvType.CLIENT)
public class WorldGenerationProgressTracker implements WorldGenerationProgressListener
{
    private final WorldGenerationProgressLogger progressLogger;
    private final Long2ObjectOpenHashMap<ChunkStatus> chunkStatuses;
    private ChunkPos spawnPos;
    private final int centerSize;
    private final int radius;
    private final int size;
    private boolean isRunning;
    
    public WorldGenerationProgressTracker(final int radius) {
        this.spawnPos = new ChunkPos(0, 0);
        this.progressLogger = new WorldGenerationProgressLogger(radius);
        this.centerSize = radius * 2 + 1;
        this.radius = radius + ChunkStatus.getMaxTargetGenerationRadius();
        this.size = this.radius * 2 + 1;
        this.chunkStatuses = (Long2ObjectOpenHashMap<ChunkStatus>)new Long2ObjectOpenHashMap();
    }
    
    @Override
    public void start(final ChunkPos spawnPos) {
        if (!this.isRunning) {
            return;
        }
        this.progressLogger.start(spawnPos);
        this.spawnPos = spawnPos;
    }
    
    @Override
    public void setChunkStatus(final ChunkPos pos, @Nullable final ChunkStatus status) {
        if (!this.isRunning) {
            return;
        }
        this.progressLogger.setChunkStatus(pos, status);
        if (status == null) {
            this.chunkStatuses.remove(pos.toLong());
        }
        else {
            this.chunkStatuses.put(pos.toLong(), status);
        }
    }
    
    public void start() {
        this.isRunning = true;
        this.chunkStatuses.clear();
    }
    
    @Override
    public void stop() {
        this.isRunning = false;
        this.progressLogger.stop();
    }
    
    public int getCenterSize() {
        return this.centerSize;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getProgressPercentage() {
        return this.progressLogger.getProgressPercentage();
    }
    
    @Nullable
    public ChunkStatus getChunkStatus(final int x, final int z) {
        return (ChunkStatus)this.chunkStatuses.get(ChunkPos.toLong(x + this.spawnPos.x - this.radius, z + this.spawnPos.z - this.radius));
    }
}
