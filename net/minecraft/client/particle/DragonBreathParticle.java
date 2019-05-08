package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DragonBreathParticle extends SpriteBillboardParticle
{
    private boolean C;
    private final SpriteProvider F;
    
    private DragonBreathParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, x, y, z);
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
        this.colorRed = MathHelper.nextFloat(this.random, 0.7176471f, 0.8745098f);
        this.colorGreen = MathHelper.nextFloat(this.random, 0.0f, 0.0f);
        this.colorBlue = MathHelper.nextFloat(this.random, 0.8235294f, 0.9764706f);
        this.scale *= 0.75f;
        this.maxAge = (int)(20.0 / (this.random.nextFloat() * 0.8 + 0.2));
        this.C = false;
        this.collidesWithWorld = false;
        this.setSpriteForAge(this.F = spriteProvider14);
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
        this.setSpriteForAge(this.F);
        if (this.onGround) {
            this.velocityY = 0.0;
            this.C = true;
        }
        if (this.C) {
            this.velocityY += 0.002;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= 0.9599999785423279;
        this.velocityZ *= 0.9599999785423279;
        if (this.C) {
            this.velocityY *= 0.9599999785423279;
        }
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public float getSize(final float tickDelta) {
        return this.scale * MathHelper.clamp((this.age + tickDelta) / this.maxAge * 32.0f, 0.0f, 1.0f);
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
            return new DragonBreathParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
