package net.minecraft.client.particle;

import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CrackParticle extends SpriteBillboardParticle
{
    private final float C;
    private final float F;
    
    private CrackParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final ItemStack itemStack14) {
        this(world, double2, double4, double6, itemStack14);
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        this.velocityX += double8;
        this.velocityY += double10;
        this.velocityZ += double12;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }
    
    protected CrackParticle(final World world, final double double2, final double double4, final double double6, final ItemStack itemStack8) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(itemStack8, world, null).getSprite());
        this.gravityStrength = 1.0f;
        this.scale /= 2.0f;
        this.C = this.random.nextFloat() * 3.0f;
        this.F = this.random.nextFloat() * 3.0f;
    }
    
    @Override
    protected float getMinU() {
        return this.sprite.getU((this.C + 1.0f) / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMaxU() {
        return this.sprite.getU(this.C / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMinV() {
        return this.sprite.getV(this.F / 4.0f * 16.0f);
    }
    
    @Override
    protected float getMaxV() {
        return this.sprite.getV((this.F + 1.0f) / 4.0f * 16.0f);
    }
    
    @Environment(EnvType.CLIENT)
    public static class ItemFactory implements ParticleFactory<ItemStackParticleParameters>
    {
        @Override
        public Particle createParticle(final ItemStackParticleParameters parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new CrackParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters.getItemStack(), null);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class SlimeballFactory implements ParticleFactory<DefaultParticleType>
    {
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new CrackParticle(world, x, y, z, new ItemStack(Items.kT));
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class SnowballFactory implements ParticleFactory<DefaultParticleType>
    {
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new CrackParticle(world, x, y, z, new ItemStack(Items.kD));
        }
    }
}
