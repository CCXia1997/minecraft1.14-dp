package net.minecraft.client.audio;

import net.minecraft.util.math.MathHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ElytraSoundInstance extends MovingSoundInstance
{
    private final ClientPlayerEntity player;
    private int tickCount;
    
    public ElytraSoundInstance(final ClientPlayerEntity player) {
        super(SoundEvents.cs, SoundCategory.h);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1f;
    }
    
    @Override
    public void tick() {
        ++this.tickCount;
        if (this.player.removed || (this.tickCount > 20 && !this.player.isFallFlying())) {
            this.done = true;
            return;
        }
        this.x = (float)this.player.x;
        this.y = (float)this.player.y;
        this.z = (float)this.player.z;
        final float float1 = (float)this.player.getVelocity().lengthSquared();
        if (float1 >= 1.0E-7) {
            this.volume = MathHelper.clamp(float1 / 4.0f, 0.0f, 1.0f);
        }
        else {
            this.volume = 0.0f;
        }
        if (this.tickCount < 20) {
            this.volume = 0.0f;
        }
        else if (this.tickCount < 40) {
            this.volume *= (float)((this.tickCount - 20) / 20.0);
        }
        final float float2 = 0.8f;
        if (this.volume > 0.8f) {
            this.pitch = 1.0f + (this.volume - 0.8f);
        }
        else {
            this.pitch = 1.0f;
        }
    }
}
