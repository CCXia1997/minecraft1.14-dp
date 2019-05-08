package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private BlockPos pos;
    private int actionId;
    private CompoundTag tag;
    
    public BlockEntityUpdateS2CPacket() {
    }
    
    public BlockEntityUpdateS2CPacket(final BlockPos pos, final int action, final CompoundTag compoundTag) {
        this.pos = pos;
        this.actionId = action;
        this.tag = compoundTag;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.actionId = buf.readUnsignedByte();
        this.tag = buf.readCompoundTag();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte((byte)this.actionId);
        buf.writeCompoundTag(this.tag);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onBlockEntityUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
    
    @Environment(EnvType.CLIENT)
    public int getActionId() {
        return this.actionId;
    }
    
    @Environment(EnvType.CLIENT)
    public CompoundTag getCompoundTag() {
        return this.tag;
    }
}
