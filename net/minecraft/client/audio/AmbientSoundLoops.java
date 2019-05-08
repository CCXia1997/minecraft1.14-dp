package net.minecraft.client.audio;

import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AmbientSoundLoops
{
    @Environment(EnvType.CLIENT)
    public static class MusicLoop extends MovingSoundInstance
    {
        private final ClientPlayerEntity player;
        
        protected MusicLoop(final ClientPlayerEntity player, final SoundEvent soundEvent) {
            super(soundEvent, SoundCategory.i);
            this.player = player;
            this.repeat = false;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.l = true;
            this.looping = true;
        }
        
        @Override
        public void tick() {
            if (this.player.removed || !this.player.isInWater()) {
                this.done = true;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Underwater extends MovingSoundInstance
    {
        private final ClientPlayerEntity player;
        private int transitionTimer;
        
        public Underwater(final ClientPlayerEntity clientPlayerEntity) {
            super(SoundEvents.d, SoundCategory.i);
            this.player = clientPlayerEntity;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.l = true;
            this.looping = true;
        }
        
        @Override
        public void tick() {
            if (this.player.removed || this.transitionTimer < 0) {
                this.done = true;
                return;
            }
            if (this.player.isInWater()) {
                ++this.transitionTimer;
            }
            else {
                this.transitionTimer -= 2;
            }
            this.transitionTimer = Math.min(this.transitionTimer, 40);
            this.volume = Math.max(0.0f, Math.min(this.transitionTimer / 40.0f, 1.0f));
        }
    }
}
