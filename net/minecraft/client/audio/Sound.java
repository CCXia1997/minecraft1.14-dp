package net.minecraft.client.audio;

import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Sound implements SoundContainer<Sound>
{
    private final Identifier id;
    private final float volume;
    private final float pitch;
    private final int weight;
    private final RegistrationType registrationType;
    private final boolean stream;
    private final boolean preload;
    private final int attenuation;
    
    public Sound(final String id, final float volume, final float pitch, final int weight, final RegistrationType registrationType, final boolean stream, final boolean preload, final int attenuation) {
        this.id = new Identifier(id);
        this.volume = volume;
        this.pitch = pitch;
        this.weight = weight;
        this.registrationType = registrationType;
        this.stream = stream;
        this.preload = preload;
        this.attenuation = attenuation;
    }
    
    public Identifier getIdentifier() {
        return this.id;
    }
    
    public Identifier getLocation() {
        return new Identifier(this.id.getNamespace(), "sounds/" + this.id.getPath() + ".ogg");
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    @Override
    public int getWeight() {
        return this.weight;
    }
    
    @Override
    public Sound getSound() {
        return this;
    }
    
    @Override
    public void preload(final SoundSystem soundSystem) {
        if (this.preload) {
            soundSystem.addPreloadedSound(this);
        }
    }
    
    public RegistrationType getRegistrationType() {
        return this.registrationType;
    }
    
    public boolean isStreamed() {
        return this.stream;
    }
    
    public boolean isPreloaded() {
        return this.preload;
    }
    
    public int getAttenuation() {
        return this.attenuation;
    }
    
    @Environment(EnvType.CLIENT)
    public enum RegistrationType
    {
        FILE("file"), 
        EVENT("event");
        
        private final String name;
        
        private RegistrationType(final String name) {
            this.name = name;
        }
        
        public static RegistrationType getByName(final String string) {
            for (final RegistrationType registrationType5 : values()) {
                if (registrationType5.name.equals(string)) {
                    return registrationType5;
                }
            }
            return null;
        }
    }
}
