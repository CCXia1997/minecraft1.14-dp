package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class StopSoundS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Identifier soundId;
    private SoundCategory category;
    
    public StopSoundS2CPacket() {
    }
    
    public StopSoundS2CPacket(@Nullable final Identifier identifier, @Nullable final SoundCategory soundCategory) {
        this.soundId = identifier;
        this.category = soundCategory;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final int integer2 = buf.readByte();
        if ((integer2 & 0x1) > 0) {
            this.category = buf.<SoundCategory>readEnumConstant(SoundCategory.class);
        }
        if ((integer2 & 0x2) > 0) {
            this.soundId = buf.readIdentifier();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        if (this.category != null) {
            if (this.soundId != null) {
                buf.writeByte(3);
                buf.writeEnumConstant(this.category);
                buf.writeIdentifier(this.soundId);
            }
            else {
                buf.writeByte(1);
                buf.writeEnumConstant(this.category);
            }
        }
        else if (this.soundId != null) {
            buf.writeByte(2);
            buf.writeIdentifier(this.soundId);
        }
        else {
            buf.writeByte(0);
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Identifier getSoundId() {
        return this.soundId;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onStopSound(this);
    }
}
