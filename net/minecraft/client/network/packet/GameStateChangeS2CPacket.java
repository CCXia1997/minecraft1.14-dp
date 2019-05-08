package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener>
{
    public static final String[] REASON_MESSAGES;
    private int reason;
    private float value;
    
    public GameStateChangeS2CPacket() {
    }
    
    public GameStateChangeS2CPacket(final int reason, final float float2) {
        this.reason = reason;
        this.value = float2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.reason = buf.readUnsignedByte();
        this.value = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.reason);
        buf.writeFloat(this.value);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGameStateChange(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getReason() {
        return this.reason;
    }
    
    @Environment(EnvType.CLIENT)
    public float getValue() {
        return this.value;
    }
    
    static {
        REASON_MESSAGES = new String[] { "block.minecraft.bed.not_valid" };
    }
}
