package net.minecraft.client.particle;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockLeakParticle extends SpriteBillboardParticle
{
    private final Fluid fluid;
    
    private BlockLeakParticle(final World world, final double x, final double y, final double z, final Fluid fluid) {
        super(world, x, y, z);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
        this.fluid = fluid;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    public int getColorMultiplier(final float float1) {
        if (this.fluid.matches(FluidTags.b)) {
            return 240;
        }
        return super.getColorMultiplier(float1);
    }
    
    @Override
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.updateAge();
        if (this.dead) {
            return;
        }
        this.velocityY -= this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.updateVelocity();
        if (this.dead) {
            return;
        }
        this.velocityX *= 0.9800000190734863;
        this.velocityY *= 0.9800000190734863;
        this.velocityZ *= 0.9800000190734863;
        final BlockPos blockPos1 = new BlockPos(this.x, this.y, this.z);
        final FluidState fluidState2 = this.world.getFluidState(blockPos1);
        if (fluidState2.getFluid() == this.fluid && this.y < blockPos1.getY() + fluidState2.getHeight(this.world, blockPos1)) {
            this.markDead();
        }
    }
    
    protected void updateAge() {
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
    }
    
    protected void updateVelocity() {
    }
    
    @Environment(EnvType.CLIENT)
    static class DrippingParticle extends BlockLeakParticle
    {
        private final ParticleParameters nextParticle;
        
        private DrippingParticle(final World world, final double x, final double y, final double z, final Fluid fluid, final ParticleParameters nextParticle) {
            super(world, x, y, z, fluid, null);
            this.nextParticle = nextParticle;
            this.gravityStrength *= 0.02f;
            this.maxAge = 40;
        }
        
        @Override
        protected void updateAge() {
            if (this.maxAge-- <= 0) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
            }
        }
        
        @Override
        protected void updateVelocity() {
            this.velocityX *= 0.02;
            this.velocityY *= 0.02;
            this.velocityZ *= 0.02;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class DrippingLavaParticle extends DrippingParticle
    {
        private DrippingLavaParticle(final World world, final double x, final double y, final double z, final Fluid fluid, final ParticleParameters nextParticle) {
            super(world, x, y, z, fluid, nextParticle);
        }
        
        @Override
        protected void updateAge() {
            this.colorRed = 1.0f;
            this.colorGreen = 16.0f / (40 - this.maxAge + 16);
            this.colorBlue = 4.0f / (40 - this.maxAge + 8);
            super.updateAge();
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class FallingParticle extends BlockLeakParticle
    {
        private final ParticleParameters nextParticle;
        
        private FallingParticle(final World world, final double x, final double y, final double z, final Fluid fluid, final ParticleParameters nextParticle) {
            super(world, x, y, z, fluid, null);
            this.nextParticle = nextParticle;
            this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        }
        
        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class LandingParticle extends BlockLeakParticle
    {
        private LandingParticle(final World world, final double double2, final double double4, final double double6, final Fluid fluid8) {
            super(world, double2, double4, double6, fluid8, null);
            this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class DrippingWaterFactory implements ParticleFactory<DefaultParticleType>
    {
        protected final SpriteProvider spriteProvider;
        
        public DrippingWaterFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockLeakParticle blockLeakParticle15 = new DrippingParticle(world, x, y, z, (Fluid)Fluids.WATER, (ParticleParameters)ParticleTypes.n);
            blockLeakParticle15.setColor(0.2f, 0.3f, 1.0f);
            blockLeakParticle15.setSprite(this.spriteProvider);
            return blockLeakParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class FallingWaterFactory implements ParticleFactory<DefaultParticleType>
    {
        protected final SpriteProvider spriteProvider;
        
        public FallingWaterFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockLeakParticle blockLeakParticle15 = new FallingParticle(world, x, y, z, (Fluid)Fluids.WATER, (ParticleParameters)ParticleTypes.X);
            blockLeakParticle15.setColor(0.2f, 0.3f, 1.0f);
            blockLeakParticle15.setSprite(this.spriteProvider);
            return blockLeakParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class DrippingLavaFactory implements ParticleFactory<DefaultParticleType>
    {
        protected final SpriteProvider spriteProvider;
        
        public DrippingLavaFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final DrippingLavaParticle drippingLavaParticle15 = new DrippingLavaParticle(world, x, y, z, (Fluid)Fluids.LAVA, (ParticleParameters)ParticleTypes.k);
            drippingLavaParticle15.setSprite(this.spriteProvider);
            return drippingLavaParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class FallingLavaFactory implements ParticleFactory<DefaultParticleType>
    {
        protected final SpriteProvider spriteProvider;
        
        public FallingLavaFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockLeakParticle blockLeakParticle15 = new FallingParticle(world, x, y, z, (Fluid)Fluids.LAVA, (ParticleParameters)ParticleTypes.l);
            blockLeakParticle15.setColor(1.0f, 0.2857143f, 0.083333336f);
            blockLeakParticle15.setSprite(this.spriteProvider);
            return blockLeakParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class LandingLavaFactory implements ParticleFactory<DefaultParticleType>
    {
        protected final SpriteProvider spriteProvider;
        
        public LandingLavaFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockLeakParticle blockLeakParticle15 = new LandingParticle(world, x, y, z, (Fluid)Fluids.LAVA);
            blockLeakParticle15.setColor(1.0f, 0.2857143f, 0.083333336f);
            blockLeakParticle15.setSprite(this.spriteProvider);
            return blockLeakParticle15;
        }
    }
}
