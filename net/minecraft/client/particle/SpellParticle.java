package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpellParticle extends SpriteBillboardParticle
{
    private static final Random RANDOM;
    private final SpriteProvider F;
    
    private SpellParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, double2, double4, double6, 0.5 - SpellParticle.RANDOM.nextDouble(), double10, 0.5 - SpellParticle.RANDOM.nextDouble());
        this.F = spriteProvider14;
        this.velocityY *= 0.20000000298023224;
        if (double8 == 0.0 && double12 == 0.0) {
            this.velocityX *= 0.10000000149011612;
            this.velocityZ *= 0.10000000149011612;
        }
        this.scale *= 0.75f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider14);
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
        this.velocityY += 0.004;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= 0.9599999785423279;
        this.velocityY *= 0.9599999785423279;
        this.velocityZ *= 0.9599999785423279;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    static {
        RANDOM = new Random();
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
            return new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class EntityFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public EntityFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final Particle particle15 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
            particle15.setColor((float)velocityX, (float)velocityY, (float)velocityZ);
            return particle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class EntityAmbientFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public EntityAmbientFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final Particle particle15 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
            particle15.setColorAlpha(0.15f);
            particle15.setColor((float)velocityX, (float)velocityY, (float)velocityZ);
            return particle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class WitchFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public WitchFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final SpellParticle spellParticle15 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
            final float float16 = world.random.nextFloat() * 0.5f + 0.35f;
            spellParticle15.setColor(1.0f * float16, 0.0f * float16, 1.0f * float16);
            return spellParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class InstantFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public InstantFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
