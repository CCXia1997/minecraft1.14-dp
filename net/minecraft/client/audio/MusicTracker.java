package net.minecraft.client.audio;

import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MusicTracker
{
    private final Random random;
    private final MinecraftClient client;
    private SoundInstance current;
    private int timeUntilNextSong;
    
    public MusicTracker(final MinecraftClient client) {
        this.random = new Random();
        this.timeUntilNextSong = 100;
        this.client = client;
    }
    
    public void tick() {
        final MusicType musicType1 = this.client.getMusicType();
        if (this.current != null) {
            if (!musicType1.getSound().getId().equals(this.current.getId())) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicType1.getMinDelay() / 2);
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, musicType1.getMinDelay(), musicType1.getMaxDelay()), this.timeUntilNextSong);
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicType1.getMaxDelay());
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(musicType1);
        }
    }
    
    public void play(final MusicType musicType) {
        this.current = PositionedSoundInstance.music(musicType.getSound());
        this.client.getSoundManager().play(this.current);
        this.timeUntilNextSong = Integer.MAX_VALUE;
    }
    
    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
            this.timeUntilNextSong = 0;
        }
    }
    
    public boolean isPlayingType(final MusicType musicType) {
        return this.current != null && musicType.getSound().getId().equals(this.current.getId());
    }
    
    @Environment(EnvType.CLIENT)
    public enum MusicType
    {
        a(SoundEvents.gJ, 20, 600), 
        b(SoundEvents.gI, 12000, 24000), 
        c(SoundEvents.gE, 1200, 3600), 
        d(SoundEvents.gF, 0, 0), 
        e(SoundEvents.gK, 1200, 3600), 
        f(SoundEvents.gG, 0, 0), 
        g(SoundEvents.gH, 6000, 24000), 
        h(SoundEvents.gL, 12000, 24000);
        
        private final SoundEvent sound;
        private final int minDelay;
        private final int maxDelay;
        
        private MusicType(final SoundEvent soundEvent, final int minDelay, final int maxDelay) {
            this.sound = soundEvent;
            this.minDelay = minDelay;
            this.maxDelay = maxDelay;
        }
        
        public SoundEvent getSound() {
            return this.sound;
        }
        
        public int getMinDelay() {
            return this.minDelay;
        }
        
        public int getMaxDelay() {
            return this.maxDelay;
        }
    }
}
