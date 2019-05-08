package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ConfirmGuiActionS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private short actionId;
    private boolean accepted;
    
    public ConfirmGuiActionS2CPacket() {
    }
    
    public ConfirmGuiActionS2CPacket(final int id, final short actionId, final boolean boolean3) {
        this.id = id;
        this.actionId = actionId;
        this.accepted = boolean3;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGuiActionConfirm(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readUnsignedByte();
        this.actionId = buf.readShort();
        this.accepted = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
        buf.writeShort(this.actionId);
        buf.writeBoolean(this.accepted);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public short getActionId() {
        return this.actionId;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean wasAccepted() {
        return this.accepted;
    }
}
