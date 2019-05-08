package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import com.google.common.collect.Lists;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.ChunkPos;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class LightUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int chunkX;
    private int chunkZ;
    private int skyLightMask;
    private int blockLightMask;
    private int filledSkyLightMask;
    private int filledBlockLightMask;
    private List<byte[]> skyLightUpdates;
    private List<byte[]> blockLightUpdates;
    
    public LightUpdateS2CPacket() {
    }
    
    public LightUpdateS2CPacket(final ChunkPos chunkPos, final LightingProvider lightingProvider) {
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int integer3 = 0; integer3 < 18; ++integer3) {
            final ChunkNibbleArray chunkNibbleArray4 = lightingProvider.get(LightType.SKY).getChunkLightArray(ChunkSectionPos.from(chunkPos, -1 + integer3));
            final ChunkNibbleArray chunkNibbleArray5 = lightingProvider.get(LightType.BLOCK).getChunkLightArray(ChunkSectionPos.from(chunkPos, -1 + integer3));
            if (chunkNibbleArray4 != null) {
                if (chunkNibbleArray4.isUninitialized()) {
                    this.filledSkyLightMask |= 1 << integer3;
                }
                else {
                    this.skyLightMask |= 1 << integer3;
                    this.skyLightUpdates.add(chunkNibbleArray4.asByteArray().clone());
                }
            }
            if (chunkNibbleArray5 != null) {
                if (chunkNibbleArray5.isUninitialized()) {
                    this.filledBlockLightMask |= 1 << integer3;
                }
                else {
                    this.blockLightMask |= 1 << integer3;
                    this.blockLightUpdates.add(chunkNibbleArray5.asByteArray().clone());
                }
            }
        }
    }
    
    public LightUpdateS2CPacket(final ChunkPos pos, final LightingProvider lightProvider, final int skyLightMask, final int blockLightMask) {
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
        this.skyLightMask = skyLightMask;
        this.blockLightMask = blockLightMask;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int integer5 = 0; integer5 < 18; ++integer5) {
            if ((this.skyLightMask & 1 << integer5) != 0x0) {
                final ChunkNibbleArray chunkNibbleArray6 = lightProvider.get(LightType.SKY).getChunkLightArray(ChunkSectionPos.from(pos, -1 + integer5));
                if (chunkNibbleArray6 == null || chunkNibbleArray6.isUninitialized()) {
                    this.skyLightMask &= ~(1 << integer5);
                    if (chunkNibbleArray6 != null) {
                        this.filledSkyLightMask |= 1 << integer5;
                    }
                }
                else {
                    this.skyLightUpdates.add(chunkNibbleArray6.asByteArray().clone());
                }
            }
            if ((this.blockLightMask & 1 << integer5) != 0x0) {
                final ChunkNibbleArray chunkNibbleArray6 = lightProvider.get(LightType.BLOCK).getChunkLightArray(ChunkSectionPos.from(pos, -1 + integer5));
                if (chunkNibbleArray6 == null || chunkNibbleArray6.isUninitialized()) {
                    this.blockLightMask &= ~(1 << integer5);
                    if (chunkNibbleArray6 != null) {
                        this.filledBlockLightMask |= 1 << integer5;
                    }
                }
                else {
                    this.blockLightUpdates.add(chunkNibbleArray6.asByteArray().clone());
                }
            }
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.chunkX = buf.readVarInt();
        this.chunkZ = buf.readVarInt();
        this.skyLightMask = buf.readVarInt();
        this.blockLightMask = buf.readVarInt();
        this.filledSkyLightMask = buf.readVarInt();
        this.filledBlockLightMask = buf.readVarInt();
        this.skyLightUpdates = Lists.newArrayList();
        for (int integer2 = 0; integer2 < 18; ++integer2) {
            if ((this.skyLightMask & 1 << integer2) != 0x0) {
                this.skyLightUpdates.add(buf.readByteArray(2048));
            }
        }
        this.blockLightUpdates = Lists.newArrayList();
        for (int integer2 = 0; integer2 < 18; ++integer2) {
            if ((this.blockLightMask & 1 << integer2) != 0x0) {
                this.blockLightUpdates.add(buf.readByteArray(2048));
            }
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.chunkX);
        buf.writeVarInt(this.chunkZ);
        buf.writeVarInt(this.skyLightMask);
        buf.writeVarInt(this.blockLightMask);
        buf.writeVarInt(this.filledSkyLightMask);
        buf.writeVarInt(this.filledBlockLightMask);
        for (final byte[] arr3 : this.skyLightUpdates) {
            buf.writeByteArray(arr3);
        }
        for (final byte[] arr3 : this.blockLightUpdates) {
            buf.writeByteArray(arr3);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onLightUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getChunkX() {
        return this.chunkX;
    }
    
    @Environment(EnvType.CLIENT)
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSkyLightMask() {
        return this.skyLightMask;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFilledSkyLightMask() {
        return this.filledSkyLightMask;
    }
    
    @Environment(EnvType.CLIENT)
    public List<byte[]> getSkyLightUpdates() {
        return this.skyLightUpdates;
    }
    
    @Environment(EnvType.CLIENT)
    public int getBlockLightMask() {
        return this.blockLightMask;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFilledBlockLightMask() {
        return this.filledBlockLightMask;
    }
    
    @Environment(EnvType.CLIENT)
    public List<byte[]> getBlockLightUpdates() {
        return this.blockLightUpdates;
    }
}
