package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnchantGlyphParticle extends SpriteBillboardParticle
{
    private final double startX;
    private final double startY;
    private final double startZ;
    
    private EnchantGlyphParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(world, double2, double4, double6);
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
        this.startX = double2;
        this.startY = double4;
        this.startZ = double6;
        this.prevPosX = double2 + double8;
        this.prevPosY = double4 + double10;
        this.prevPosZ = double6 + double12;
        this.x = this.prevPosX;
        this.y = this.prevPosY;
        this.z = this.prevPosZ;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        final float float14 = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorRed = 0.9f * float14;
        this.colorGreen = 0.9f * float14;
        this.colorBlue = float14;
        this.collidesWithWorld = false;
        this.maxAge = (int)(Math.random() * 10.0) + 30;
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
        float float1 = this.age / (float)this.maxAge;
        float1 = 1.0f - float1;
        float float2 = 1.0f - float1;
        float2 *= float2;
        float2 *= float2;
        this.x = this.startX + this.velocityX * float1;
        this.y = this.startY + this.velocityY * float1 - float2 * 1.2f;
        this.z = this.startZ + this.velocityZ * float1;
    }
    
    @Environment(EnvType.CLIENT)
    public static class EnchantFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public EnchantFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final EnchantGlyphParticle enchantGlyphParticle15 = new EnchantGlyphParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            enchantGlyphParticle15.setSprite(this.a);
            return enchantGlyphParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class NautilusFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public NautilusFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final EnchantGlyphParticle enchantGlyphParticle15 = new EnchantGlyphParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            enchantGlyphParticle15.setSprite(this.a);
            return enchantGlyphParticle15;
        }
    }
}
