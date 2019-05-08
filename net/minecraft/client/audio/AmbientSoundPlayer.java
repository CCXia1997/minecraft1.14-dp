package net.minecraft.client.audio;

import net.minecraft.sound.SoundEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClientPlayerTickable;

@Environment(EnvType.CLIENT)
public class AmbientSoundPlayer implements ClientPlayerTickable
{
    private final ClientPlayerEntity player;
    private final SoundManager soundManager;
    private int ticksUntilPlay;
    
    public AmbientSoundPlayer(final ClientPlayerEntity clientPlayerEntity, final SoundManager soundManager) {
        this.ticksUntilPlay = 0;
        this.player = clientPlayerEntity;
        this.soundManager = soundManager;
    }
    
    @Override
    public void tick() {
        --this.ticksUntilPlay;
        if (this.ticksUntilPlay <= 0 && this.player.isInWater()) {
            final float float1 = this.player.world.random.nextFloat();
            if (float1 < 1.0E-4f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.g));
            }
            else if (float1 < 0.001f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.f));
            }
            else if (float1 < 0.01f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.e));
            }
        }
    }
}
