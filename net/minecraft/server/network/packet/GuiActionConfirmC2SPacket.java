package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiActionConfirmC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int windowId;
    private short actionId;
    private boolean accepted;
    
    public GuiActionConfirmC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public GuiActionConfirmC2SPacket(final int windowId, final short actionId, final boolean accepted) {
        this.windowId = windowId;
        this.actionId = actionId;
        this.accepted = accepted;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onConfirmTransaction(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.windowId = buf.readByte();
        this.actionId = buf.readShort();
        this.accepted = (buf.readByte() != 0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.actionId);
        buf.writeByte(this.accepted ? 1 : 0);
    }
    
    public int getWindowId() {
        return this.windowId;
    }
    
    public short getSyncId() {
        return this.actionId;
    }
}
