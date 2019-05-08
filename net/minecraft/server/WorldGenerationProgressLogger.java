package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.Logger;

public class WorldGenerationProgressLogger implements WorldGenerationProgressListener
{
    private static final Logger LOGGER;
    private final int totalCount;
    private int generatedCount;
    private long startTime;
    private long nextMessageTime;
    
    public WorldGenerationProgressLogger(final int radius) {
        this.nextMessageTime = Long.MAX_VALUE;
        final int integer2 = radius * 2 + 1;
        this.totalCount = integer2 * integer2;
    }
    
    @Override
    public void start(final ChunkPos spawnPos) {
        this.nextMessageTime = SystemUtil.getMeasuringTimeMs();
        this.startTime = this.nextMessageTime;
    }
    
    @Override
    public void setChunkStatus(final ChunkPos pos, @Nullable final ChunkStatus status) {
        if (status == ChunkStatus.FULL) {
            ++this.generatedCount;
        }
        final int integer3 = this.getProgressPercentage();
        if (SystemUtil.getMeasuringTimeMs() > this.nextMessageTime) {
            this.nextMessageTime += 500L;
            WorldGenerationProgressLogger.LOGGER.info(new TranslatableTextComponent("menu.preparingSpawn", new Object[] { MathHelper.clamp(integer3, 0, 100) }).getString());
        }
    }
    
    @Override
    public void stop() {
        WorldGenerationProgressLogger.LOGGER.info("Time elapsed: {} ms", (SystemUtil.getMeasuringTimeMs() - this.startTime));
        this.nextMessageTime = Long.MAX_VALUE;
    }
    
    public int getProgressPercentage() {
        return MathHelper.floor(this.generatedCount * 100.0f / this.totalCount);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
