package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PortalParticle extends SpriteBillboardParticle
{
    private final double startX;
    private final double startY;
    private final double startZ;
    
    private PortalParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(world, double2, double4, double6);
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
        this.x = double2;
        this.y = double4;
        this.z = double6;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
        this.scale = 0.1f * (this.random.nextFloat() * 0.2f + 0.5f);
        final float float14 = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorRed = float14 * 0.9f;
        this.colorGreen = float14 * 0.3f;
        this.colorBlue = float14;
        this.maxAge = (int)(Math.random() * 10.0) + 40;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public void move(final double dx, final double dy, final double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }
    
    @Override
    public float getSize(final float tickDelta) {
        float float2 = (this.age + tickDelta) / this.maxAge;
        float2 = 1.0f - float2;
        float2 *= float2;
        float2 = 1.0f - float2;
        return this.scale * float2;
    }
    
    public int getColorMultiplier(final float float1) {
        final int integer2 = super.getColorMultiplier(float1);
        float float2 = this.age / (float)this.maxAge;
        float2 *= float2;
        float2 *= float2;
        final int integer3 = integer2 & 0xFF;
        int integer4 = integer2 >> 16 & 0xFF;
        integer4 += (int)(float2 * 15.0f * 16.0f);
        if (integer4 > 240) {
            integer4 = 240;
        }
        return integer3 | integer4 << 16;
    }
    
    @Override
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        final float float2;
        float float1 = float2 = this.age / (float)this.maxAge;
        float1 = -float1 + float1 * float1 * 2.0f;
        float1 = 1.0f - float1;
        this.x = this.startX + this.velocityX * float1;
        this.y = this.startY + this.velocityY * float1 + (1.0f - float2);
        this.z = this.startZ + this.velocityZ * float1;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public Factory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final PortalParticle portalParticle15 = new PortalParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            portalParticle15.setSprite(this.a);
            return portalParticle15;
        }
    }
}
