package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.Validate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlaySoundS2CPacket implements Packet<ClientPlayPacketListener>
{
    private SoundEvent sound;
    private SoundCategory category;
    private int fixedX;
    private int fixedY;
    private int fixedZ;
    private float volume;
    private float pitch;
    
    public PlaySoundS2CPacket() {
    }
    
    public PlaySoundS2CPacket(final SoundEvent sound, final SoundCategory category, final double x, final double y, final double z, final float volume, final float pitch) {
        Validate.notNull(sound, "sound", new Object[0]);
        this.sound = sound;
        this.category = category;
        this.fixedX = (int)(x * 8.0);
        this.fixedY = (int)(y * 8.0);
        this.fixedZ = (int)(z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.sound = Registry.SOUND_EVENT.get(buf.readVarInt());
        this.category = buf.<SoundCategory>readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        buf.writeEnumConstant(this.category);
        buf.writeInt(this.fixedX);
        buf.writeInt(this.fixedY);
        buf.writeInt(this.fixedZ);
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
    public double getX() {
        return this.fixedX / 8.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public double getY() {
        return this.fixedY / 8.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public double getZ() {
        return this.fixedZ / 8.0f;
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
        listener.onPlaySound(this);
    }
}
