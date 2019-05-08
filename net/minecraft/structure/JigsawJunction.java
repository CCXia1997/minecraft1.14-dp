package net.minecraft.structure;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.structure.pool.StructurePool;

public class JigsawJunction
{
    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final StructurePool.Projection destProjection;
    
    public JigsawJunction(final int sourceX, final int sourceGroundY, final int sourceZ, final int deltaY, final StructurePool.Projection destProjection) {
        this.sourceX = sourceX;
        this.sourceGroundY = sourceGroundY;
        this.sourceZ = sourceZ;
        this.deltaY = deltaY;
        this.destProjection = destProjection;
    }
    
    public int getSourceX() {
        return this.sourceX;
    }
    
    public int getSourceGroundY() {
        return this.sourceGroundY;
    }
    
    public int getSourceZ() {
        return this.sourceZ;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final ImmutableMap.Builder<T, T> builder2 = ImmutableMap.<T, T>builder();
        builder2.put((T)dynamicOps.createString("source_x"), (T)dynamicOps.createInt(this.sourceX)).put((T)dynamicOps.createString("source_ground_y"), (T)dynamicOps.createInt(this.sourceGroundY)).put((T)dynamicOps.createString("source_z"), (T)dynamicOps.createInt(this.sourceZ)).put((T)dynamicOps.createString("delta_y"), (T)dynamicOps.createInt(this.deltaY)).put((T)dynamicOps.createString("dest_proj"), (T)dynamicOps.createString(this.destProjection.getId()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)builder2.build()));
    }
    
    public static <T> JigsawJunction deserialize(final Dynamic<T> dynamic) {
        return new JigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), StructurePool.Projection.getById(dynamic.get("dest_proj").asString("")));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final JigsawJunction jigsawJunction2 = (JigsawJunction)o;
        return this.sourceX == jigsawJunction2.sourceX && this.sourceZ == jigsawJunction2.sourceZ && this.deltaY == jigsawJunction2.deltaY && this.destProjection == jigsawJunction2.destProjection;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.sourceX;
        integer1 = 31 * integer1 + this.sourceGroundY;
        integer1 = 31 * integer1 + this.sourceZ;
        integer1 = 31 * integer1 + this.deltaY;
        integer1 = 31 * integer1 + this.destProjection.hashCode();
        return integer1;
    }
    
    @Override
    public String toString() {
        return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
    }
}
