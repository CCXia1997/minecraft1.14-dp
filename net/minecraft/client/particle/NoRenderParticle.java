package net.minecraft.client.particle;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoRenderParticle extends Particle
{
    protected NoRenderParticle(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }
    
    protected NoRenderParticle(final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Override
    public final void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.NO_RENDER;
    }
}
