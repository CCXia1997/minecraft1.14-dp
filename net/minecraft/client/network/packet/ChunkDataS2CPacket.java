package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.util.registry.Registry;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.world.Heightmap;
import java.util.Map;
import net.minecraft.world.chunk.WorldChunk;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int chunkX;
    private int chunkZ;
    private int verticalStripBitmask;
    private CompoundTag heightmaps;
    private byte[] data;
    private List<CompoundTag> blockEntities;
    private boolean isFullChunk;
    
    public ChunkDataS2CPacket() {
    }
    
    public ChunkDataS2CPacket(final WorldChunk chunk, final int includedSectionsMask) {
        final ChunkPos chunkPos3 = chunk.getPos();
        this.chunkX = chunkPos3.x;
        this.chunkZ = chunkPos3.z;
        this.isFullChunk = (includedSectionsMask == 65535);
        this.heightmaps = new CompoundTag();
        for (final Map.Entry<Heightmap.Type, Heightmap> entry5 : chunk.getHeightmaps()) {
            if (!entry5.getKey().shouldSendToClient()) {
                continue;
            }
            this.heightmaps.put(entry5.getKey().getName(), new LongArrayTag(entry5.getValue().asLongArray()));
        }
        this.data = new byte[this.getDataSize(chunk, includedSectionsMask)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, includedSectionsMask);
        this.blockEntities = Lists.newArrayList();
        for (final Map.Entry<BlockPos, BlockEntity> entry6 : chunk.getBlockEntities().entrySet()) {
            final BlockPos blockPos6 = entry6.getKey();
            final BlockEntity blockEntity7 = entry6.getValue();
            final int integer8 = blockPos6.getY() >> 4;
            if (!this.isFullChunk() && (includedSectionsMask & 1 << integer8) == 0x0) {
                continue;
            }
            final CompoundTag compoundTag9 = blockEntity7.toInitialChunkDataTag();
            this.blockEntities.add(compoundTag9);
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.isFullChunk = buf.readBoolean();
        this.verticalStripBitmask = buf.readVarInt();
        this.heightmaps = buf.readCompoundTag();
        final int integer2 = buf.readVarInt();
        if (integer2 > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        buf.readBytes(this.data = new byte[integer2]);
        final int integer3 = buf.readVarInt();
        this.blockEntities = Lists.newArrayList();
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            this.blockEntities.add(buf.readCompoundTag());
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBoolean(this.isFullChunk);
        buf.writeVarInt(this.verticalStripBitmask);
        buf.writeCompoundTag(this.heightmaps);
        buf.writeVarInt(this.data.length);
        buf.writeBytes(this.data);
        buf.writeVarInt(this.blockEntities.size());
        for (final CompoundTag compoundTag3 : this.blockEntities) {
            buf.writeCompoundTag(compoundTag3);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onChunkData(this);
    }
    
    @Environment(EnvType.CLIENT)
    public PacketByteBuf getReadBuffer() {
        return new PacketByteBuf(Unpooled.wrappedBuffer(this.data));
    }
    
    private ByteBuf getWriteBuffer() {
        final ByteBuf byteBuf1 = Unpooled.wrappedBuffer(this.data);
        byteBuf1.writerIndex(0);
        return byteBuf1;
    }
    
    public int writeData(final PacketByteBuf packetByteBuf, final WorldChunk chunk, final int includedSectionsMask) {
        int integer4 = 0;
        final ChunkSection[] arr5 = chunk.getSectionArray();
        for (int integer5 = 0, integer6 = arr5.length; integer5 < integer6; ++integer5) {
            final ChunkSection chunkSection8 = arr5[integer5];
            if (chunkSection8 != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection8.isEmpty())) {
                if ((includedSectionsMask & 1 << integer5) != 0x0) {
                    integer4 |= 1 << integer5;
                    chunkSection8.toPacket(packetByteBuf);
                }
            }
        }
        if (this.isFullChunk()) {
            final Biome[] arr6 = chunk.getBiomeArray();
            for (int integer6 = 0; integer6 < arr6.length; ++integer6) {
                packetByteBuf.writeInt(Registry.BIOME.getRawId(arr6[integer6]));
            }
        }
        return integer4;
    }
    
    protected int getDataSize(final WorldChunk worldChunk, final int integer) {
        int integer2 = 0;
        final ChunkSection[] arr4 = worldChunk.getSectionArray();
        for (int integer3 = 0, integer4 = arr4.length; integer3 < integer4; ++integer3) {
            final ChunkSection chunkSection7 = arr4[integer3];
            if (chunkSection7 != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection7.isEmpty())) {
                if ((integer & 1 << integer3) != 0x0) {
                    integer2 += chunkSection7.getPacketSize();
                }
            }
        }
        if (this.isFullChunk()) {
            integer2 += worldChunk.getBiomeArray().length * 4;
        }
        return integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public int getX() {
        return this.chunkX;
    }
    
    @Environment(EnvType.CLIENT)
    public int getZ() {
        return this.chunkZ;
    }
    
    @Environment(EnvType.CLIENT)
    public int getVerticalStripBitmask() {
        return this.verticalStripBitmask;
    }
    
    public boolean isFullChunk() {
        return this.isFullChunk;
    }
    
    @Environment(EnvType.CLIENT)
    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }
    
    @Environment(EnvType.CLIENT)
    public List<CompoundTag> getBlockEntityTagList() {
        return this.blockEntities;
    }
}
