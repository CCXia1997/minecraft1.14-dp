package net.minecraft.client.network.packet;

import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private ChunkPos chunkPos;
    private ChunkDeltaRecord[] records;
    
    public ChunkDeltaUpdateS2CPacket() {
    }
    
    public ChunkDeltaUpdateS2CPacket(final int integer, final short[] arr, final WorldChunk worldChunk) {
        this.chunkPos = worldChunk.getPos();
        this.records = new ChunkDeltaRecord[integer];
        for (int integer2 = 0; integer2 < this.records.length; ++integer2) {
            this.records[integer2] = new ChunkDeltaRecord(arr[integer2], worldChunk);
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt());
        this.records = new ChunkDeltaRecord[buf.readVarInt()];
        for (int integer2 = 0; integer2 < this.records.length; ++integer2) {
            this.records[integer2] = new ChunkDeltaRecord(buf.readShort(), Block.STATE_IDS.get(buf.readVarInt()));
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.chunkPos.x);
        buf.writeInt(this.chunkPos.z);
        buf.writeVarInt(this.records.length);
        for (final ChunkDeltaRecord chunkDeltaRecord5 : this.records) {
            buf.writeShort(chunkDeltaRecord5.getPosShort());
            buf.writeVarInt(Block.getRawIdFromState(chunkDeltaRecord5.getState()));
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onChunkDeltaUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public ChunkDeltaRecord[] getRecords() {
        return this.records;
    }
    
    public class ChunkDeltaRecord
    {
        private final short pos;
        private final BlockState state;
        
        public ChunkDeltaRecord(final short short2, final BlockState blockState) {
            this.pos = short2;
            this.state = blockState;
        }
        
        public ChunkDeltaRecord(final short short2, final WorldChunk worldChunk) {
            this.pos = short2;
            this.state = worldChunk.getBlockState(this.getBlockPos());
        }
        
        public BlockPos getBlockPos() {
            return new BlockPos(ChunkDeltaUpdateS2CPacket.this.chunkPos.toBlockPos(this.pos >> 12 & 0xF, this.pos & 0xFF, this.pos >> 8 & 0xF));
        }
        
        public short getPosShort() {
            return this.pos;
        }
        
        public BlockState getState() {
            return this.state;
        }
    }
}
