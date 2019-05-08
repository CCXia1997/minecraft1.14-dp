package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DamageParticle extends SpriteBillboardParticle
{
    private DamageParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        this.velocityX += double8 * 0.4;
        this.velocityY += double10 * 0.4;
        this.velocityZ += double12 * 0.4;
        final float float14 = (float)(Math.random() * 0.30000001192092896 + 0.6000000238418579);
        this.colorRed = float14;
        this.colorGreen = float14;
        this.colorBlue = float14;
        this.scale *= 0.75f;
        this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
        this.collidesWithWorld = false;
        this.update();
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
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.colorGreen *= (float)0.96;
        this.colorBlue *= (float)0.9;
        this.velocityX *= 0.699999988079071;
        this.velocityY *= 0.699999988079071;
        this.velocityZ *= 0.699999988079071;
        this.velocityY -= 0.019999999552965164;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    @Environment(EnvType.CLIENT)
    public static class c implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public c(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final DamageParticle damageParticle15 = new DamageParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            damageParticle15.setSprite(this.a);
            return damageParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class EnchantedHitFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public EnchantedHitFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final DamageParticle damageParticle16;
            final DamageParticle damageParticle15 = damageParticle16 = new DamageParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            damageParticle16.colorRed *= 0.3f;
            final DamageParticle damageParticle17 = damageParticle15;
            damageParticle17.colorGreen *= 0.8f;
            damageParticle15.setSprite(this.a);
            return damageParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public DefaultFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final DamageParticle damageParticle15 = new DamageParticle(world, x, y, z, velocityX, velocityY + 1.0, velocityZ, null);
            damageParticle15.setMaxAge(20);
            damageParticle15.setSprite(this.a);
            return damageParticle15;
        }
    }
}
