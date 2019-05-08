package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoteParticle extends SpriteBillboardParticle
{
    private NoteParticle(final World world, final double x, final double y, final double z, final double double8) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.velocityX *= 0.009999999776482582;
        this.velocityY *= 0.009999999776482582;
        this.velocityZ *= 0.009999999776482582;
        this.velocityY += 0.2;
        this.colorRed = Math.max(0.0f, MathHelper.sin(((float)double8 + 0.0f) * 6.2831855f) * 0.65f + 0.35f);
        this.colorGreen = Math.max(0.0f, MathHelper.sin(((float)double8 + 0.33333334f) * 6.2831855f) * 0.65f + 0.35f);
        this.colorBlue = Math.max(0.0f, MathHelper.sin(((float)double8 + 0.6666667f) * 6.2831855f) * 0.65f + 0.35f);
        this.scale *= 1.5f;
        this.maxAge = 6;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= 0.6600000262260437;
        this.velocityY *= 0.6600000262260437;
        this.velocityZ *= 0.6600000262260437;
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
            final NoteParticle noteParticle15 = new NoteParticle(world, x, y, z, velocityX, null);
            noteParticle15.setSprite(this.a);
            return noteParticle15;
        }
    }
}
