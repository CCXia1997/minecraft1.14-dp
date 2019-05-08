package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiOpenS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int slotCount;
    private int entityHorseId;
    
    public GuiOpenS2CPacket() {
    }
    
    public GuiOpenS2CPacket(final int integer1, final int integer2, final int integer3) {
        this.id = integer1;
        this.slotCount = integer2;
        this.entityHorseId = integer3;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGuiOpen(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readUnsignedByte();
        this.slotCount = buf.readVarInt();
        this.entityHorseId = buf.readInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
        buf.writeVarInt(this.slotCount);
        buf.writeInt(this.entityHorseId);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSlotCount() {
        return this.slotCount;
    }
    
    @Environment(EnvType.CLIENT)
    public int getHorseId() {
        return this.entityHorseId;
    }
}
