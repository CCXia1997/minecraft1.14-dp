package net.minecraft.client.audio;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RidingMinecartSoundInstance extends MovingSoundInstance
{
    private final AbstractMinecartEntity minecart;
    private float distance;
    
    public RidingMinecartSoundInstance(final AbstractMinecartEntity abstractMinecartEntity) {
        super(SoundEvents.gu, SoundCategory.g);
        this.distance = 0.0f;
        this.minecart = abstractMinecartEntity;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = (float)abstractMinecartEntity.x;
        this.y = (float)abstractMinecartEntity.y;
        this.z = (float)abstractMinecartEntity.z;
    }
    
    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }
    
    @Override
    public void tick() {
        if (this.minecart.removed) {
            this.done = true;
            return;
        }
        this.x = (float)this.minecart.x;
        this.y = (float)this.minecart.y;
        this.z = (float)this.minecart.z;
        final float float1 = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
        if (float1 >= 0.01) {
            this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
            this.volume = MathHelper.lerp(MathHelper.clamp(float1, 0.0f, 0.5f), 0.0f, 0.7f);
        }
        else {
            this.distance = 0.0f;
            this.volume = 0.0f;
        }
    }
}
