package net.minecraft.client.render;

import net.minecraft.util.math.BoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FrustumWithOrigin implements VisibleRegion
{
    private final Frustum frustum;
    private double originX;
    private double originY;
    private double originZ;
    
    public FrustumWithOrigin() {
        this(GlMatrixFrustum.get());
    }
    
    public FrustumWithOrigin(final Frustum frustum) {
        this.frustum = frustum;
    }
    
    @Override
    public void setOrigin(final double x, final double y, final double z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }
    
    public boolean intersects(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        return this.frustum.intersects(minX - this.originX, minY - this.originY, minZ - this.originZ, maxX - this.originX, maxY - this.originY, maxZ - this.originZ);
    }
    
    @Override
    public boolean intersects(final BoundingBox boundingBox) {
        return this.intersects(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }
}
