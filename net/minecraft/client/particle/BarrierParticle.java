package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.block.Blocks;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemProvider;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BarrierParticle extends SpriteBillboardParticle
{
    private BarrierParticle(final World world, final double x, final double y, final double double6, final ItemProvider itemProvider8) {
        super(world, x, y, double6);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModels().getSprite(itemProvider8));
        this.gravityStrength = 0.0f;
        this.maxAge = 80;
        this.collidesWithWorld = false;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }
    
    @Override
    public float getSize(final float tickDelta) {
        return 0.5f;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new BarrierParticle(world, x, y, z, Blocks.gg.getItem(), null);
        }
    }
}
