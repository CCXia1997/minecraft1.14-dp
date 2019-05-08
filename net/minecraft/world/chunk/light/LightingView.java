package net.minecraft.world.chunk.light;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockPos;

public interface LightingView
{
    default void updateSectionStatus(final BlockPos pos, final boolean status) {
        this.updateSectionStatus(ChunkSectionPos.from(pos), status);
    }
    
    void updateSectionStatus(final ChunkSectionPos arg1, final boolean arg2);
}
