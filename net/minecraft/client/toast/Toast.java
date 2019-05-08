package net.minecraft.client.toast;

import net.minecraft.sound.SoundEvents;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Toast
{
    public static final Identifier TOASTS_TEX = new Identifier("textures/gui/toasts.png");
    public static final Object b = new Object();
    
    Visibility draw(final ToastManager arg1, final long arg2);
    
    default Object getType() {
        return Toast.b;
    }
    
    @Environment(EnvType.CLIENT)
    public enum Visibility
    {
        a(SoundEvents.mo), 
        b(SoundEvents.mp);
        
        private final SoundEvent sound;
        
        private Visibility(final SoundEvent soundEvent) {
            this.sound = soundEvent;
        }
        
        public void play(final SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(this.sound, 1.0f, 1.0f));
        }
    }
}
