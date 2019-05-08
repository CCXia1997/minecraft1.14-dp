package net.minecraft.client.model;

import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Vertex
{
    public final Vec3d pos;
    public final float u;
    public final float v;
    
    public Vertex(final float x, final float y, final float z, final float u, final float v) {
        this(new Vec3d(x, y, z), u, v);
    }
    
    public Vertex remap(final float u, final float v) {
        return new Vertex(this, u, v);
    }
    
    public Vertex(final Vertex vertex, final float u, final float v) {
        this.pos = vertex.pos;
        this.u = u;
        this.v = v;
    }
    
    public Vertex(final Vec3d pos, final float u, final float v) {
        this.pos = pos;
        this.u = u;
        this.v = v;
    }
}
