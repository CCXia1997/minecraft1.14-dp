package net.minecraft.realms;

import net.minecraft.client.render.Tessellator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tezzelator
{
    public static final Tessellator t;
    public static final Tezzelator instance;
    
    public void end() {
        Tezzelator.t.draw();
    }
    
    public Tezzelator vertex(final double double1, final double double3, final double double5) {
        Tezzelator.t.getBufferBuilder().vertex(double1, double3, double5);
        return this;
    }
    
    public void color(final float float1, final float float2, final float float3, final float float4) {
        Tezzelator.t.getBufferBuilder().color(float1, float2, float3, float4);
    }
    
    public void tex2(final short short1, final short short2) {
        Tezzelator.t.getBufferBuilder().texture(short1, short2);
    }
    
    public void normal(final float float1, final float float2, final float float3) {
        Tezzelator.t.getBufferBuilder().normal(float1, float2, float3);
    }
    
    public void begin(final int integer, final RealmsVertexFormat realmsVertexFormat) {
        Tezzelator.t.getBufferBuilder().begin(integer, realmsVertexFormat.getVertexFormat());
    }
    
    public void endVertex() {
        Tezzelator.t.getBufferBuilder().next();
    }
    
    public void offset(final double double1, final double double3, final double double5) {
        Tezzelator.t.getBufferBuilder().setOffset(double1, double3, double5);
    }
    
    public RealmsBufferBuilder color(final int integer1, final int integer2, final int integer3, final int integer4) {
        return new RealmsBufferBuilder(Tezzelator.t.getBufferBuilder().color(integer1, integer2, integer3, integer4));
    }
    
    public Tezzelator tex(final double double1, final double double3) {
        Tezzelator.t.getBufferBuilder().texture(double1, double3);
        return this;
    }
    
    static {
        t = Tessellator.getInstance();
        instance = new Tezzelator();
    }
}
