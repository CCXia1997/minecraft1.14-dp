package net.minecraft.client.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LavaEmberParticle extends SpriteBillboardParticle
{
    private LavaEmberParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.velocityX *= 0.800000011920929;
        this.velocityY *= 0.800000011920929;
        this.velocityZ *= 0.800000011920929;
        this.velocityY = this.random.nextFloat() * 0.4f + 0.05f;
        this.scale *= this.random.nextFloat() * 2.0f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    public int getColorMultiplier(final float float1) {
        final int integer2 = super.getColorMultiplier(float1);
        final int integer3 = 240;
        final int integer4 = integer2 >> 16 & 0xFF;
        return 0xF0 | integer4 << 16;
    }
    
    @Override
    public float getSize(final float tickDelta) {
        final float float2 = (this.age + tickDelta) / this.maxAge;
        return this.scale * (1.0f - float2 * float2);
    }
    
    @Override
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        final float float1 = this.age / (float)this.maxAge;
        if (this.random.nextFloat() > float1) {
            this.world.addParticle(ParticleTypes.Q, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
        }
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.velocityY -= 0.03;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9990000128746033;
        this.velocityY *= 0.9990000128746033;
        this.velocityZ *= 0.9990000128746033;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
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
            final LavaEmberParticle lavaEmberParticle15 = new LavaEmberParticle(world, x, y, z, null);
            lavaEmberParticle15.setSprite(this.a);
            return lavaEmberParticle15;
        }
    }
}
