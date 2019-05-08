package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.world.ExtendedBlockView;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockCrackParticle extends SpriteBillboardParticle
{
    private final BlockState block;
    private BlockPos blockPos;
    private final float G;
    private final float H;
    
    public BlockCrackParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12, final BlockState blockState14) {
        super(world, x, y, z, double8, double10, double12);
        this.block = blockState14;
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(blockState14));
        this.gravityStrength = 1.0f;
        this.colorRed = 0.6f;
        this.colorGreen = 0.6f;
        this.colorBlue = 0.6f;
        this.scale /= 2.0f;
        this.G = this.random.nextFloat() * 3.0f;
        this.H = this.random.nextFloat() * 3.0f;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }
    
    public BlockCrackParticle setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
        if (this.block.getBlock() == Blocks.i) {
            return this;
        }
        this.updateColor(blockPos);
        return this;
    }
    
    public BlockCrackParticle setBlockPosFromPosition() {
        this.blockPos = new BlockPos(this.x, this.y, this.z);
        final Block block1 = this.block.getBlock();
        if (block1 == Blocks.i) {
            return this;
        }
        this.updateColor(this.blockPos);
        return this;
    }
    
    protected void updateColor(@Nullable final BlockPos blockPos) {
        final int integer2 = MinecraftClient.getInstance().getBlockColorMap().getRenderColor(this.block, this.world, blockPos, 0);
        this.colorRed *= (integer2 >> 16 & 0xFF) / 255.0f;
        this.colorGreen *= (integer2 >> 8 & 0xFF) / 255.0f;
        this.colorBlue *= (integer2 & 0xFF) / 255.0f;
    }
    
    @Override
    protected float getMinU() {
        return this.sprite.getU((this.G + 1.0f) / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMaxU() {
        return this.sprite.getU(this.G / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMinV() {
        return this.sprite.getV(this.H / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMaxV() {
        return this.sprite.getV((this.H + 1.0f) / 4.0f * 16.0f);
    }
    
    public int getColorMultiplier(final float float1) {
        final int integer2 = super.getColorMultiplier(float1);
        int integer3 = 0;
        if (this.world.isBlockLoaded(this.blockPos)) {
            integer3 = this.world.getLightmapIndex(this.blockPos, 0);
        }
        return (integer2 == 0) ? integer3 : integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<BlockStateParticleParameters>
    {
        @Override
        public Particle createParticle(final BlockStateParticleParameters parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final BlockState blockState15 = parameters.getBlockState();
            if (blockState15.isAir() || blockState15.getBlock() == Blocks.bn) {
                return null;
            }
            return new BlockCrackParticle(world, x, y, z, velocityX, velocityY, velocityZ, blockState15).setBlockPosFromPosition();
        }
    }
}
