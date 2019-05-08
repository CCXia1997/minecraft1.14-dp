package net.minecraft.client.render.block;

import net.minecraft.world.biome.Biome;
import java.util.Iterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BiomeColors
{
    private static final Proxy GRASS_COLOR;
    private static final Proxy FOLIAGE_COLOR;
    private static final Proxy WATER_COLOR;
    private static final Proxy WATER_FOG_COLOR;
    
    private static int colorAt(final ExtendedBlockView extendedBlockView, final BlockPos blockPos, final Proxy proxy) {
        int integer4 = 0;
        int integer5 = 0;
        int integer6 = 0;
        final int integer7 = MinecraftClient.getInstance().options.biomeBlendRadius;
        final int integer8 = (integer7 * 2 + 1) * (integer7 * 2 + 1);
        for (final BlockPos blockPos2 : BlockPos.iterate(blockPos.getX() - integer7, blockPos.getY(), blockPos.getZ() - integer7, blockPos.getX() + integer7, blockPos.getY(), blockPos.getZ() + integer7)) {
            final int integer9 = proxy.getColor(extendedBlockView.getBiome(blockPos2), blockPos2);
            integer4 += (integer9 & 0xFF0000) >> 16;
            integer5 += (integer9 & 0xFF00) >> 8;
            integer6 += (integer9 & 0xFF);
        }
        return (integer4 / integer8 & 0xFF) << 16 | (integer5 / integer8 & 0xFF) << 8 | (integer6 / integer8 & 0xFF);
    }
    
    public static int grassColorAt(final ExtendedBlockView extendedBlockView, final BlockPos blockPos) {
        return colorAt(extendedBlockView, blockPos, BiomeColors.GRASS_COLOR);
    }
    
    public static int foliageColorAt(final ExtendedBlockView extendedBlockView, final BlockPos blockPos) {
        return colorAt(extendedBlockView, blockPos, BiomeColors.FOLIAGE_COLOR);
    }
    
    public static int waterColorAt(final ExtendedBlockView extendedBlockView, final BlockPos blockPos) {
        return colorAt(extendedBlockView, blockPos, BiomeColors.WATER_COLOR);
    }
    
    static {
        GRASS_COLOR = Biome::getGrassColorAt;
        FOLIAGE_COLOR = Biome::getFoliageColorAt;
        WATER_COLOR = ((biome, blockPos) -> biome.getWaterColor());
        WATER_FOG_COLOR = ((biome, blockPos) -> biome.getWaterFogColor());
    }
    
    @Environment(EnvType.CLIENT)
    interface Proxy
    {
        int getColor(final Biome arg1, final BlockPos arg2);
    }
}
