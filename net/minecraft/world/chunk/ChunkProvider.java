package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import javax.annotation.Nullable;
import net.minecraft.world.BlockView;

public interface ChunkProvider
{
    @Nullable
    BlockView getChunk(final int arg1, final int arg2);
    
    default void onLightUpdate(final LightType type, final ChunkSectionPos chunkSectionPos) {
    }
    
    BlockView getWorld();
}
