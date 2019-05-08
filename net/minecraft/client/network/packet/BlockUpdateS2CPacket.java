package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class BlockUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private BlockPos pos;
    private BlockState state;
    
    public BlockUpdateS2CPacket() {
    }
    
    public BlockUpdateS2CPacket(final BlockView world, final BlockPos blockPos) {
        this.pos = blockPos;
        this.state = world.getBlockState(blockPos);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.state = Block.STATE_IDS.get(buf.readVarInt());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeVarInt(Block.getRawIdFromState(this.state));
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onBlockUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockState getState() {
        return this.state;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}
