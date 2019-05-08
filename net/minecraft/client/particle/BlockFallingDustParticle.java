package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.BlockRenderType;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockFallingDustParticle extends SpriteBillboardParticle
{
    private final float C;
    private final SpriteProvider F;
    
    private BlockFallingDustParticle(final World world, final double x, final double y, final double z, final float float8, final float float9, final float float10, final SpriteProvider spriteProvider11) {
        super(world, x, y, z);
        this.F = spriteProvider11;
        this.colorRed = float8;
        this.colorGreen = float9;
        this.colorBlue = float10;
        final float float11 = 0.9f;
        this.scale *= 0.67499995f;
        final int integer13 = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max(integer13 * 0.9f, 1.0f);
        this.setSpriteForAge(spriteProvider11);
        this.C = ((float)Math.random() - 0.5f) * 0.1f;
        this.angle = (float)Math.random() * 6.2831855f;
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
        this.setSpriteForAge(this.F);
        this.prevAngle = this.angle;
        this.angle += 3.1415927f * this.C * 2.0f;
        if (this.onGround) {
            final float n = 0.0f;
            this.angle = n;
            this.prevAngle = n;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityY -= 0.003000000026077032;
        this.velocityY = Math.max(this.velocityY, -0.14000000059604645);
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<BlockStateParticleParameters>
    {
        private final SpriteProvider a;
        
        public Factory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Nullable
        @Override
        public Particle createParticle(final BlockStateParticleParameters parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockState blockState15 = parameters.getBlockState();
            if (!blockState15.isAir() && blockState15.getRenderType() == BlockRenderType.a) {
                return null;
            }
            int integer16 = MinecraftClient.getInstance().getBlockColorMap().a(blockState15, world, new BlockPos(x, y, z));
            if (blockState15.getBlock() instanceof FallingBlock) {
                integer16 = ((FallingBlock)blockState15.getBlock()).getColor(blockState15);
            }
            final float float17 = (integer16 >> 16 & 0xFF) / 255.0f;
            final float float18 = (integer16 >> 8 & 0xFF) / 255.0f;
            final float float19 = (integer16 & 0xFF) / 255.0f;
            return new BlockFallingDustParticle(world, x, y, z, float17, float18, float19, this.a, null);
        }
    }
}
