package net.minecraft.world.chunk.light;

import net.minecraft.world.chunk.ChunkNibbleArray;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkProvider;
import javax.annotation.Nullable;

public class LightingProvider implements LightingView
{
    @Nullable
    private final ChunkLightProvider<?, ?> blockLightProvider;
    @Nullable
    private final ChunkLightProvider<?, ?> skyLightProvider;
    
    public LightingProvider(final ChunkProvider chunkProvider, final boolean hasBlockLight, final boolean hasSkyLight) {
        this.blockLightProvider = (hasBlockLight ? new ChunkBlockLightProvider(chunkProvider) : null);
        this.skyLightProvider = (hasSkyLight ? new ChunkSkyLightProvider(chunkProvider) : null);
    }
    
    public void enqueueLightUpdate(final BlockPos pos) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.queueLightCheck(pos);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.queueLightCheck(pos);
        }
    }
    
    public void a(final BlockPos pos, final int integer) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.a(pos, integer);
        }
    }
    
    public boolean hasUpdates() {
        return (this.skyLightProvider != null && this.skyLightProvider.hasUpdates()) || (this.blockLightProvider != null && this.blockLightProvider.hasUpdates());
    }
    
    public int doLightUpdates(final int maxUpdateCount, final boolean boolean2, final boolean boolean3) {
        if (this.blockLightProvider != null && this.skyLightProvider != null) {
            final int integer4 = maxUpdateCount / 2;
            final int integer5 = this.blockLightProvider.doLightUpdates(integer4, boolean2, boolean3);
            final int integer6 = maxUpdateCount - integer4 + integer5;
            final int integer7 = this.skyLightProvider.doLightUpdates(integer6, boolean2, boolean3);
            if (integer5 == 0 && integer7 > 0) {
                return this.blockLightProvider.doLightUpdates(integer7, boolean2, boolean3);
            }
            return integer7;
        }
        else {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.doLightUpdates(maxUpdateCount, boolean2, boolean3);
            }
            if (this.skyLightProvider != null) {
                return this.skyLightProvider.doLightUpdates(maxUpdateCount, boolean2, boolean3);
            }
            return maxUpdateCount;
        }
    }
    
    @Override
    public void updateSectionStatus(final ChunkSectionPos pos, final boolean status) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.updateSectionStatus(pos, status);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.updateSectionStatus(pos, status);
        }
    }
    
    public void suppressLight(final ChunkPos chunkPos, final boolean boolean2) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.a(chunkPos, boolean2);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.a(chunkPos, boolean2);
        }
    }
    
    public ChunkLightingView get(final LightType lightType) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider == null) {
                return ChunkLightingView.Empty.a;
            }
            return this.blockLightProvider;
        }
        else {
            if (this.skyLightProvider == null) {
                return ChunkLightingView.Empty.a;
            }
            return this.skyLightProvider;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public String a(final LightType lightType, final ChunkSectionPos chunkSectionPos) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.b(chunkSectionPos.asLong());
            }
        }
        else if (this.skyLightProvider != null) {
            return this.skyLightProvider.b(chunkSectionPos.asLong());
        }
        return "n/a";
    }
    
    public void queueData(final LightType lightType, final ChunkSectionPos chunkSectionPos, final ChunkNibbleArray chunkNibbleArray) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                this.blockLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
            }
        }
        else if (this.skyLightProvider != null) {
            this.skyLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
        }
    }
}
