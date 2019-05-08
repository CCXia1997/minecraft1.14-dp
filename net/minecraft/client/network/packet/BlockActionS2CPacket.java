package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class BlockActionS2CPacket implements Packet<ClientPlayPacketListener>
{
    private BlockPos pos;
    private int type;
    private int data;
    private Block block;
    
    public BlockActionS2CPacket() {
    }
    
    public BlockActionS2CPacket(final BlockPos pos, final Block block, final int type, final int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.type = buf.readUnsignedByte();
        this.data = buf.readUnsignedByte();
        this.block = Registry.BLOCK.get(buf.readVarInt());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.type);
        buf.writeByte(this.data);
        buf.writeVarInt(Registry.BLOCK.getRawId(this.block));
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onBlockAction(this);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
    
    @Environment(EnvType.CLIENT)
    public int getType() {
        return this.type;
    }
    
    @Environment(EnvType.CLIENT)
    public int getData() {
        return this.data;
    }
    
    @Environment(EnvType.CLIENT)
    public Block getBlock() {
        return this.block;
    }
}
