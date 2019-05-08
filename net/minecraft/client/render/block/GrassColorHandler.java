package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GrassColorHandler
{
    private static int[] colorMap;
    
    public static void setColorMap(final int[] map) {
        GrassColorHandler.colorMap = map;
    }
    
    public static int getColor(final double temperature, double double3) {
        double3 *= temperature;
        final int integer5 = (int)((1.0 - temperature) * 255.0);
        final int integer6 = (int)((1.0 - double3) * 255.0);
        final int integer7 = integer6 << 8 | integer5;
        if (integer7 > GrassColorHandler.colorMap.length) {
            return -65281;
        }
        return GrassColorHandler.colorMap[integer7];
    }
    
    static {
        GrassColorHandler.colorMap = new int[65536];
    }
}
