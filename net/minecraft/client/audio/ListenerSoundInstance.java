package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ListenerSoundInstance
{
    void onSoundPlayed(final SoundInstance arg1, final WeightedSoundSet arg2);
}
