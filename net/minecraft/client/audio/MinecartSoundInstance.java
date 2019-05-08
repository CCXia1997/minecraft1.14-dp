package net.minecraft.client.audio;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MinecartSoundInstance extends MovingSoundInstance
{
    private final PlayerEntity player;
    private final AbstractMinecartEntity minecart;
    
    public MinecartSoundInstance(final PlayerEntity player, final AbstractMinecartEntity minecart) {
        super(SoundEvents.gt, SoundCategory.g);
        this.player = player;
        this.minecart = minecart;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
    }
    
    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }
    
    @Override
    public void tick() {
        if (this.minecart.removed || !this.player.hasVehicle() || this.player.getVehicle() != this.minecart) {
            this.done = true;
            return;
        }
        final float float1 = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
        if (float1 >= 0.01) {
            this.volume = 0.0f + MathHelper.clamp(float1, 0.0f, 1.0f) * 0.75f;
        }
        else {
            this.volume = 0.0f;
        }
    }
}
