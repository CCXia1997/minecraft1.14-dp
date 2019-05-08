package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CurrentDownParticle extends SpriteBillboardParticle
{
    private float C;
    
    private CurrentDownParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4, double6);
        this.maxAge = (int)(Math.random() * 60.0) + 30;
        this.collidesWithWorld = false;
        this.velocityX = 0.0;
        this.velocityY = -0.05;
        this.velocityZ = 0.0;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.gravityStrength = 0.002f;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
        final float float1 = 0.6f;
        this.velocityX += 0.6f * MathHelper.cos(this.C);
        this.velocityZ += 0.6f * MathHelper.sin(this.C);
        this.velocityX *= 0.07;
        this.velocityZ *= 0.07;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).matches(FluidTags.a) || this.onGround) {
            this.markDead();
        }
        this.C += (float)0.08;
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
            final CurrentDownParticle currentDownParticle15 = new CurrentDownParticle(world, x, y, z, null);
            currentDownParticle15.setSprite(this.a);
            return currentDownParticle15;
        }
    }
}
