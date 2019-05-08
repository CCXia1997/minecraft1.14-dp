package net.minecraft.client.audio;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance
{
    protected Sound sound;
    @Nullable
    private WeightedSoundSet soundSet;
    protected final SoundCategory category;
    protected final Identifier id;
    protected float volume;
    protected float pitch;
    protected float x;
    protected float y;
    protected float z;
    protected boolean repeat;
    protected int repeatDelay;
    protected AttenuationType attenuationType;
    protected boolean l;
    protected boolean looping;
    
    protected AbstractSoundInstance(final SoundEvent sound, final SoundCategory category) {
        this(sound.getId(), category);
    }
    
    protected AbstractSoundInstance(final Identifier soundId, final SoundCategory category) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.attenuationType = AttenuationType.LINEAR;
        this.id = soundId;
        this.category = category;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public WeightedSoundSet getSoundSet(final SoundManager soundManager) {
        this.soundSet = soundManager.get(this.id);
        if (this.soundSet == null) {
            this.sound = SoundManager.SOUND_MISSING;
        }
        else {
            this.sound = this.soundSet.getSound();
        }
        return this.soundSet;
    }
    
    @Override
    public Sound getSound() {
        return this.sound;
    }
    
    @Override
    public SoundCategory getCategory() {
        return this.category;
    }
    
    @Override
    public boolean isRepeatable() {
        return this.repeat;
    }
    
    @Override
    public int getRepeatDelay() {
        return this.repeatDelay;
    }
    
    @Override
    public float getVolume() {
        return this.volume * this.sound.getVolume();
    }
    
    @Override
    public float getPitch() {
        return this.pitch * this.sound.getPitch();
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public float getZ() {
        return this.z;
    }
    
    @Override
    public AttenuationType getAttenuationType() {
        return this.attenuationType;
    }
    
    @Override
    public boolean isLooping() {
        return this.looping;
    }
}
