package net.minecraft.client.audio;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance
{
    protected boolean done;
    
    protected MovingSoundInstance(final SoundEvent sound, final SoundCategory category) {
        super(sound, category);
    }
    
    @Override
    public boolean isDone() {
        return this.done;
    }
}
