package net.minecraft.client.particle;

import net.minecraft.world.World;
import net.minecraft.client.texture.Sprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SpriteBillboardParticle extends BillboardParticle
{
    protected Sprite sprite;
    
    protected SpriteBillboardParticle(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }
    
    protected SpriteBillboardParticle(final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    protected void setSprite(final Sprite sprite) {
        this.sprite = sprite;
    }
    
    @Override
    protected float getMinU() {
        return this.sprite.getMinU();
    }
    
    @Override
    protected float getMaxU() {
        return this.sprite.getMaxU();
    }
    
    @Override
    protected float getMinV() {
        return this.sprite.getMinV();
    }
    
    @Override
    protected float getMaxV() {
        return this.sprite.getMaxV();
    }
    
    public void setSprite(final SpriteProvider spriteProvider) {
        this.setSprite(spriteProvider.getSprite(this.random));
    }
    
    public void setSpriteForAge(final SpriteProvider spriteProvider) {
        this.setSprite(spriteProvider.getSprite(this.age, this.maxAge));
    }
}
