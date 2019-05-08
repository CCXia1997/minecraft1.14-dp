package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WaterSuspendParticle extends SpriteBillboardParticle
{
    private WaterSuspendParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4 - 0.125, double6);
        this.colorRed = 0.4f;
        this.colorGreen = 0.4f;
        this.colorBlue = 0.7f;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
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
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).matches(FluidTags.a)) {
            this.markDead();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class UnderwaterFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public UnderwaterFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final WaterSuspendParticle waterSuspendParticle15 = new WaterSuspendParticle(world, x, y, z, null);
            waterSuspendParticle15.setSprite(this.a);
            return waterSuspendParticle15;
        }
    }
}
