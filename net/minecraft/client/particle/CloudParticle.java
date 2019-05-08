package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CloudParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    private CloudParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.C = spriteProvider14;
        final float float15 = 2.5f;
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        this.velocityX += double8;
        this.velocityY += double10;
        this.velocityZ += double12;
        final float float16 = 1.0f - (float)(Math.random() * 0.30000001192092896);
        this.colorRed = float16;
        this.colorGreen = float16;
        this.colorBlue = float16;
        this.scale *= 1.875f;
        final int integer17 = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max(integer17 * 2.5f, 1.0f);
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider14);
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    public float getSize(final float tickDelta) {
        return this.scale * MathHelper.clamp((this.age + tickDelta) / this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.setSpriteForAge(this.C);
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9599999785423279;
        this.velocityY *= 0.9599999785423279;
        this.velocityZ *= 0.9599999785423279;
        final PlayerEntity playerEntity1 = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
        if (playerEntity1 != null) {
            final BoundingBox boundingBox2 = playerEntity1.getBoundingBox();
            if (this.y > boundingBox2.minY) {
                this.y += (boundingBox2.minY - this.y) * 0.2;
                this.velocityY += (playerEntity1.getVelocity().y - this.velocityY) * 0.2;
                this.setPos(this.x, this.y, this.z);
            }
        }
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class CloudFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public CloudFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new CloudParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class SneezeFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public SneezeFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final Particle particle15 = new CloudParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
            particle15.setColor(200.0f, 50.0f, 120.0f);
            particle15.setColorAlpha(0.4f);
            return particle15;
        }
    }
}
