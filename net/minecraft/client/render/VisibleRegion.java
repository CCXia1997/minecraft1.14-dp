package net.minecraft.client.render;

import net.minecraft.util.math.BoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface VisibleRegion
{
    boolean intersects(final BoundingBox arg1);
    
    void setOrigin(final double arg1, final double arg2, final double arg3);
}
