package net.minecraft.client.audio;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntityTrackingSoundInstance extends MovingSoundInstance
{
    private final Entity entity;
    
    public EntityTrackingSoundInstance(final SoundEvent soundEvent, final SoundCategory soundCategory, final Entity entity) {
        this(soundEvent, soundCategory, 1.0f, 1.0f, entity);
    }
    
    public EntityTrackingSoundInstance(final SoundEvent soundEvent, final SoundCategory soundCategory, final float float3, final float float4, final Entity entity) {
        super(soundEvent, soundCategory);
        this.volume = float3;
        this.pitch = float4;
        this.entity = entity;
        this.x = (float)this.entity.x;
        this.y = (float)this.entity.y;
        this.z = (float)this.entity.z;
    }
    
    @Override
    public void tick() {
        if (this.entity.removed) {
            this.done = true;
            return;
        }
        this.x = (float)this.entity.x;
        this.y = (float)this.entity.y;
        this.z = (float)this.entity.z;
    }
}
