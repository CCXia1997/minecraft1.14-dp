package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Frustum
{
    public final float[][] sides;
    public final float[] projectionMatrix;
    public final float[] modelViewMatrix;
    public final float[] mvpMatrix;
    
    public Frustum() {
        this.sides = new float[6][4];
        this.projectionMatrix = new float[16];
        this.modelViewMatrix = new float[16];
        this.mvpMatrix = new float[16];
    }
    
    private double getDistanceFromPlane(final float[] plane, final double x, final double y, final double z) {
        return plane[0] * x + plane[1] * y + plane[2] * z + plane[3];
    }
    
    public boolean intersects(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        for (int integer13 = 0; integer13 < 6; ++integer13) {
            final float[] arr14 = this.sides[integer13];
            if (this.getDistanceFromPlane(arr14, minX, minY, minZ) <= 0.0) {
                if (this.getDistanceFromPlane(arr14, maxX, minY, minZ) <= 0.0) {
                    if (this.getDistanceFromPlane(arr14, minX, maxY, minZ) <= 0.0) {
                        if (this.getDistanceFromPlane(arr14, maxX, maxY, minZ) <= 0.0) {
                            if (this.getDistanceFromPlane(arr14, minX, minY, maxZ) <= 0.0) {
                                if (this.getDistanceFromPlane(arr14, maxX, minY, maxZ) <= 0.0) {
                                    if (this.getDistanceFromPlane(arr14, minX, maxY, maxZ) <= 0.0) {
                                        if (this.getDistanceFromPlane(arr14, maxX, maxY, maxZ) <= 0.0) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
