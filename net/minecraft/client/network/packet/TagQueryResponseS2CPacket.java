package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class TagQueryResponseS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int transactionId;
    @Nullable
    private CompoundTag tag;
    
    public TagQueryResponseS2CPacket() {
    }
    
    public TagQueryResponseS2CPacket(final int integer, @Nullable final CompoundTag compoundTag) {
        this.transactionId = integer;
        this.tag = compoundTag;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.tag = buf.readCompoundTag();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeCompoundTag(this.tag);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onTagQuery(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getTransactionId() {
        return this.transactionId;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public CompoundTag getTag() {
        return this.tag;
    }
    
    @Override
    public boolean isErrorFatal() {
        return true;
    }
}
