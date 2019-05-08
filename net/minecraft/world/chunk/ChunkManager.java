package net.minecraft.world.chunk;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.light.LightingProvider;
import java.io.IOException;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.BooleanSupplier;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;

public abstract class ChunkManager implements ChunkProvider, AutoCloseable
{
    @Nullable
    public WorldChunk getWorldChunk(final int integer1, final int integer2, final boolean boolean3) {
        return (WorldChunk)this.getChunk(integer1, integer2, ChunkStatus.FULL, boolean3);
    }
    
    @Nullable
    @Override
    public BlockView getChunk(final int chunkX, final int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }
    
    public boolean isChunkLoaded(final int x, final int z) {
        return this.getChunk(x, z, ChunkStatus.FULL, false) != null;
    }
    
    @Nullable
    public abstract Chunk getChunk(final int arg1, final int arg2, final ChunkStatus arg3, final boolean arg4);
    
    @Environment(EnvType.CLIENT)
    public abstract void tick(final BooleanSupplier arg1);
    
    public abstract String getStatus();
    
    public abstract ChunkGenerator<?> getChunkGenerator();
    
    @Override
    public void close() throws IOException {
    }
    
    public abstract LightingProvider getLightingProvider();
    
    public void setMobSpawnOptions(final boolean spawnMonsters, final boolean spawnAnimals) {
    }
    
    public void setChunkForced(final ChunkPos pos, final boolean forced) {
    }
    
    public boolean shouldTickEntity(final Entity entity) {
        return true;
    }
}
