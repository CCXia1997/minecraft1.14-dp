package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int primaryEffectId;
    private int secondaryEffectId;
    
    public UpdateBeaconC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateBeaconC2SPacket(final int integer1, final int integer2) {
        this.primaryEffectId = integer1;
        this.secondaryEffectId = integer2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.primaryEffectId = buf.readVarInt();
        this.secondaryEffectId = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.primaryEffectId);
        buf.writeVarInt(this.secondaryEffectId);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateBeacon(this);
    }
    
    public int getPrimaryEffectId() {
        return this.primaryEffectId;
    }
    
    public int getSecondaryEffectId() {
        return this.secondaryEffectId;
    }
}
