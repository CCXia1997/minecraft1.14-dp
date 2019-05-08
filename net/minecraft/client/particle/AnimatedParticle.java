package net.minecraft.client.particle;

import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimatedParticle extends SpriteBillboardParticle
{
    protected final SpriteProvider spriteProvider;
    private final float upwardsAcceleration;
    private float resistance;
    private float targetColorRed;
    private float targetColorGreen;
    private float targetColorBlue;
    private boolean changesColor;
    
    protected AnimatedParticle(final World world, final double x, final double y, final double z, final SpriteProvider spriteProvider, final float upwardsAcceleration) {
        super(world, x, y, z);
        this.resistance = 0.91f;
        this.spriteProvider = spriteProvider;
        this.upwardsAcceleration = upwardsAcceleration;
    }
    
    public void setColor(final int integer) {
        final float float2 = ((integer & 0xFF0000) >> 16) / 255.0f;
        final float float3 = ((integer & 0xFF00) >> 8) / 255.0f;
        final float float4 = ((integer & 0xFF) >> 0) / 255.0f;
        final float float5 = 1.0f;
        this.setColor(float2 * 1.0f, float3 * 1.0f, float4 * 1.0f);
    }
    
    public void setTargetColor(final int integer) {
        this.targetColorRed = ((integer & 0xFF0000) >> 16) / 255.0f;
        this.targetColorGreen = ((integer & 0xFF00) >> 8) / 255.0f;
        this.targetColorBlue = ((integer & 0xFF) >> 0) / 255.0f;
        this.changesColor = true;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
        this.setSpriteForAge(this.spriteProvider);
        if (this.age > this.maxAge / 2) {
            this.setColorAlpha(1.0f - (this.age - (float)(this.maxAge / 2)) / this.maxAge);
            if (this.changesColor) {
                this.colorRed += (this.targetColorRed - this.colorRed) * 0.2f;
                this.colorGreen += (this.targetColorGreen - this.colorGreen) * 0.2f;
                this.colorBlue += (this.targetColorBlue - this.colorBlue) * 0.2f;
            }
        }
        this.velocityY += this.upwardsAcceleration;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= this.resistance;
        this.velocityY *= this.resistance;
        this.velocityZ *= this.resistance;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    public int getColorMultiplier(final float float1) {
        return 15728880;
    }
    
    protected void setResistance(final float resistance) {
        this.resistance = resistance;
    }
}
