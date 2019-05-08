package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;

public interface ChunkLightingView extends LightingView
{
    @Nullable
    ChunkNibbleArray getChunkLightArray(final ChunkSectionPos arg1);
    
    int getLightLevel(final BlockPos arg1);
    
    public enum Empty implements ChunkLightingView
    {
        a;
        
        @Nullable
        @Override
        public ChunkNibbleArray getChunkLightArray(final ChunkSectionPos chunkSectionPos) {
            return null;
        }
        
        @Override
        public int getLightLevel(final BlockPos blockPos) {
            return 0;
        }
        
        @Override
        public void updateSectionStatus(final ChunkSectionPos pos, final boolean status) {
        }
    }
}
