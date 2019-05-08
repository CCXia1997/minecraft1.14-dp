package net.minecraft.client.audio;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PositionedSoundInstance extends AbstractSoundInstance
{
    public PositionedSoundInstance(final SoundEvent sound, final SoundCategory category, final float volume, final float pitch, final BlockPos blockPos) {
        this(sound, category, volume, pitch, blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }
    
    public static PositionedSoundInstance master(final SoundEvent soundEvent, final float float2) {
        return master(soundEvent, float2, 0.25f);
    }
    
    public static PositionedSoundInstance master(final SoundEvent sound, final float float2, final float pitch) {
        return new PositionedSoundInstance(sound.getId(), SoundCategory.a, pitch, float2, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }
    
    public static PositionedSoundInstance music(final SoundEvent sound) {
        return new PositionedSoundInstance(sound.getId(), SoundCategory.b, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }
    
    public static PositionedSoundInstance record(final SoundEvent sound, final float x, final float y, final float z) {
        return new PositionedSoundInstance(sound, SoundCategory.c, 4.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
    }
    
    public PositionedSoundInstance(final SoundEvent sound, final SoundCategory category, final float volume, final float pitch, final float x, final float y, final float float7) {
        this(sound, category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, float7);
    }
    
    private PositionedSoundInstance(final SoundEvent sound, final SoundCategory category, final float volume, final float pitch, final boolean repeat, final int repeatDelay, final SoundInstance.AttenuationType attenuationType, final float x, final float y, final float float10) {
        this(sound.getId(), category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, float10, false);
    }
    
    public PositionedSoundInstance(final Identifier id, final SoundCategory category, final float volume, final float pitch, final boolean repeat, final int repeatDelay, final SoundInstance.AttenuationType attenuationType, final float x, final float y, final float z, final boolean boolean11) {
        super(id, category);
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
        this.looping = boolean11;
    }
}
