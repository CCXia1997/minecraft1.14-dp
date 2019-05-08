package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SuspendParticle extends SpriteBillboardParticle
{
    private SuspendParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(world, double2, double4, double6, double8, double10, double12);
        final float float14 = this.random.nextFloat() * 0.1f + 0.2f;
        this.colorRed = float14;
        this.colorGreen = float14;
        this.colorBlue = float14;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.5f;
        this.velocityX *= 0.019999999552965164;
        this.velocityY *= 0.019999999552965164;
        this.velocityZ *= 0.019999999552965164;
        this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
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
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.99;
        this.velocityY *= 0.99;
        this.velocityZ *= 0.99;
    }
    
    @Environment(EnvType.CLIENT)
    public static class MyceliumFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public MyceliumFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final SuspendParticle suspendParticle15 = new SuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            suspendParticle15.setSprite(this.a);
            return suspendParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class HappyVillagerFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public HappyVillagerFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final SuspendParticle suspendParticle15 = new SuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            suspendParticle15.setSprite(this.a);
            suspendParticle15.setColor(1.0f, 1.0f, 1.0f);
            return suspendParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class a implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public a(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final SuspendParticle suspendParticle15 = new SuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            suspendParticle15.setSprite(this.a);
            suspendParticle15.setColor(1.0f, 1.0f, 1.0f);
            suspendParticle15.setMaxAge(3 + world.getRandom().nextInt(5));
            return suspendParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class DolphinFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public DolphinFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final SuspendParticle suspendParticle15 = new SuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            suspendParticle15.setColor(0.3f, 0.5f, 1.0f);
            suspendParticle15.setSprite(this.a);
            suspendParticle15.setColorAlpha(1.0f - world.random.nextFloat() * 0.7f);
            suspendParticle15.setMaxAge(suspendParticle15.getMaxAge() / 2);
            return suspendParticle15;
        }
    }
}
