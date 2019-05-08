package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;

public interface ExtendedBlockView extends BlockView
{
    Biome getBiome(final BlockPos arg1);
    
    int getLightLevel(final LightType arg1, final BlockPos arg2);
    
    default boolean isSkyVisible(final BlockPos blockPos) {
        return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
    }
    
    @Environment(EnvType.CLIENT)
    default int getLightmapIndex(final BlockPos pos, final int integer) {
        final int integer2 = this.getLightLevel(LightType.SKY, pos);
        int integer3 = this.getLightLevel(LightType.BLOCK, pos);
        if (integer3 < integer) {
            integer3 = integer;
        }
        return integer2 << 20 | integer3 << 4;
    }
}
