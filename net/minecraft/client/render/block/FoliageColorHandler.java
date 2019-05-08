package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FoliageColorHandler
{
    private static int[] colorMap;
    
    public static void setColorMap(final int[] pixels) {
        FoliageColorHandler.colorMap = pixels;
    }
    
    public static int getColor(final double temperature, double double3) {
        double3 *= temperature;
        final int integer5 = (int)((1.0 - temperature) * 255.0);
        final int integer6 = (int)((1.0 - double3) * 255.0);
        return FoliageColorHandler.colorMap[integer6 << 8 | integer5];
    }
    
    public static int getSpruceColor() {
        return 6396257;
    }
    
    public static int getBirchColor() {
        return 8431445;
    }
    
    public static int getDefaultColor() {
        return 4764952;
    }
    
    static {
        FoliageColorHandler.colorMap = new int[65536];
    }
}
