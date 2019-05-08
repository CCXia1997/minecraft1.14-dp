package net.minecraft.client.audio;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.mob.GuardianEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuardianAttackSoundInstance extends MovingSoundInstance
{
    private final GuardianEntity guardian;
    
    public GuardianAttackSoundInstance(final GuardianEntity guardianEntity) {
        super(SoundEvents.ex, SoundCategory.f);
        this.guardian = guardianEntity;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }
    
    @Override
    public void tick() {
        if (this.guardian.removed || !this.guardian.hasBeamTarget()) {
            this.done = true;
            return;
        }
        this.x = (float)this.guardian.x;
        this.y = (float)this.guardian.y;
        this.z = (float)this.guardian.z;
        final float float1 = this.guardian.getBeamProgress(0.0f);
        this.volume = 0.0f + 1.0f * float1 * float1;
        this.pitch = 0.7f + 0.5f * float1;
    }
}
