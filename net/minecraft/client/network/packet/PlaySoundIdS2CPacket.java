package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlaySoundIdS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Identifier id;
    private SoundCategory category;
    private int fixedX;
    private int fixedY;
    private int fixedZ;
    private float volume;
    private float pitch;
    
    public PlaySoundIdS2CPacket() {
        this.fixedY = Integer.MAX_VALUE;
    }
    
    public PlaySoundIdS2CPacket(final Identifier sound, final SoundCategory category, final Vec3d pos, final float volume, final float pitch) {
        this.fixedY = Integer.MAX_VALUE;
        this.id = sound;
        this.category = category;
        this.fixedX = (int)(pos.x * 8.0);
        this.fixedY = (int)(pos.y * 8.0);
        this.fixedZ = (int)(pos.z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readIdentifier();
        this.category = buf.<SoundCategory>readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.id);
        buf.writeEnumConstant(this.category);
        buf.writeInt(this.fixedX);
        buf.writeInt(this.fixedY);
        buf.writeInt(this.fixedZ);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getSoundId() {
        return this.id;
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
        listener.onPlaySoundId(this);
    }
}
