package net.minecraft.client.audio;

import net.minecraft.sound.SoundCategory;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SoundInstance
{
    Identifier getId();
    
    @Nullable
    WeightedSoundSet getSoundSet(final SoundManager arg1);
    
    Sound getSound();
    
    SoundCategory getCategory();
    
    boolean isRepeatable();
    
    boolean isLooping();
    
    int getRepeatDelay();
    
    float getVolume();
    
    float getPitch();
    
    float getX();
    
    float getY();
    
    float getZ();
    
    AttenuationType getAttenuationType();
    
    default boolean shouldAlwaysPlay() {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public enum AttenuationType
    {
        NONE, 
        LINEAR;
    }
}
