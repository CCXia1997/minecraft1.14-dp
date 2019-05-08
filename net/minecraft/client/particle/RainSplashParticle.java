package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RainSplashParticle extends SpriteBillboardParticle
{
    protected RainSplashParticle(final World world, final double x, final double y, final double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.velocityX *= 0.30000001192092896;
        this.velocityY = Math.random() * 0.20000000298023224 + 0.10000000149011612;
        this.velocityZ *= 0.30000001192092896;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
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
        this.velocityY -= this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9800000190734863;
        this.velocityY *= 0.9800000190734863;
        this.velocityZ *= 0.9800000190734863;
        if (this.onGround) {
            if (Math.random() < 0.5) {
                this.markDead();
            }
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
        final BlockPos blockPos1 = new BlockPos(this.x, this.y, this.z);
        final double double2 = Math.max(this.world.getBlockState(blockPos1).getCollisionShape(this.world, blockPos1).b(Direction.Axis.Y, this.x - blockPos1.getX(), this.z - blockPos1.getZ()), this.world.getFluidState(blockPos1).getHeight(this.world, blockPos1));
        if (double2 > 0.0 && this.y < blockPos1.getY() + double2) {
            this.markDead();
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
            final RainSplashParticle rainSplashParticle15 = new RainSplashParticle(world, x, y, z);
            rainSplashParticle15.setSprite(this.a);
            return rainSplashParticle15;
        }
    }
}
