package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SquidInkParticle extends AnimatedParticle
{
    private SquidInkParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, double2, double4, double6, spriteProvider14, 0.0f);
        this.scale = 0.5f;
        this.setColorAlpha(1.0f);
        this.setColor(0.0f, 0.0f, 0.0f);
        this.maxAge = (int)(this.scale * 12.0f / (Math.random() * 0.800000011920929 + 0.20000000298023224));
        this.setSpriteForAge(spriteProvider14);
        this.collidesWithWorld = false;
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
        this.setResistance(0.0f);
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
        this.setSpriteForAge(this.spriteProvider);
        if (this.age > this.maxAge / 2) {
            this.setColorAlpha(1.0f - (this.age - (float)(this.maxAge / 2)) / this.maxAge);
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
            this.velocityY -= 0.00800000037997961;
        }
        this.velocityX *= 0.9200000166893005;
        this.velocityY *= 0.9200000166893005;
        this.velocityZ *= 0.9200000166893005;
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
            return new SquidInkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
