package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.Validate;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlaySoundFromEntityS2CPacket implements Packet<ClientPlayPacketListener>
{
    private SoundEvent sound;
    private SoundCategory category;
    private int entityId;
    private float volume;
    private float pitch;
    
    public PlaySoundFromEntityS2CPacket() {
    }
    
    public PlaySoundFromEntityS2CPacket(final SoundEvent sound, final SoundCategory category, final Entity entity, final float volume, final float pitch) {
        Validate.notNull(sound, "sound", new Object[0]);
        this.sound = sound;
        this.category = category;
        this.entityId = entity.getEntityId();
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.sound = Registry.SOUND_EVENT.get(buf.readVarInt());
        this.category = buf.<SoundCategory>readEnumConstant(SoundCategory.class);
        this.entityId = buf.readVarInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        buf.writeEnumConstant(this.category);
        buf.writeVarInt(this.entityId);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }
    
    @Environment(EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
    
    @Environment(EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    public float getVolume() {
        return this.volume;
    }
    
    @Environment(EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlaySoundFromEntity(this);
    }
}
