package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int propertyId;
    private int value;
    
    public GuiUpdateS2CPacket() {
    }
    
    public GuiUpdateS2CPacket(final int window, final int propertyId, final int integer3) {
        this.id = window;
        this.propertyId = propertyId;
        this.value = integer3;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGuiUpdate(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readUnsignedByte();
        this.propertyId = buf.readShort();
        this.value = buf.readShort();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
        buf.writeShort(this.propertyId);
        buf.writeShort(this.value);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int getPropertyId() {
        return this.propertyId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getValue() {
        return this.value;
    }
}
