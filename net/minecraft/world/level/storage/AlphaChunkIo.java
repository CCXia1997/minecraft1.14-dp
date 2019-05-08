package net.minecraft.world.level.storage;

import java.util.AbstractList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.nbt.CompoundTag;

public class AlphaChunkIo
{
    public static AlphaChunk readAlphaChunk(final CompoundTag tag) {
        final int integer2 = tag.getInt("xPos");
        final int integer3 = tag.getInt("zPos");
        final AlphaChunk alphaChunk4 = new AlphaChunk(integer2, integer3);
        alphaChunk4.blocks = tag.getByteArray("Blocks");
        alphaChunk4.data = new AlphaChunkDataArray(tag.getByteArray("Data"), 7);
        alphaChunk4.skyLight = new AlphaChunkDataArray(tag.getByteArray("SkyLight"), 7);
        alphaChunk4.blockLight = new AlphaChunkDataArray(tag.getByteArray("BlockLight"), 7);
        alphaChunk4.heightMap = tag.getByteArray("HeightMap");
        alphaChunk4.terrainPopulated = tag.getBoolean("TerrainPopulated");
        alphaChunk4.entities = tag.getList("Entities", 10);
        alphaChunk4.blockEntities = tag.getList("TileEntities", 10);
        alphaChunk4.blockTicks = tag.getList("TileTicks", 10);
        try {
            alphaChunk4.lastUpdate = tag.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException5) {
            alphaChunk4.lastUpdate = tag.getInt("LastUpdate");
        }
        return alphaChunk4;
    }
    
    public static void convertAlphaChunk(final AlphaChunk alphaChunk, final CompoundTag tag, final BiomeSource biomeSource) {
        tag.putInt("xPos", alphaChunk.x);
        tag.putInt("zPos", alphaChunk.z);
        tag.putLong("LastUpdate", alphaChunk.lastUpdate);
        final int[] arr4 = new int[alphaChunk.heightMap.length];
        for (int integer5 = 0; integer5 < alphaChunk.heightMap.length; ++integer5) {
            arr4[integer5] = alphaChunk.heightMap[integer5];
        }
        tag.putIntArray("HeightMap", arr4);
        tag.putBoolean("TerrainPopulated", alphaChunk.terrainPopulated);
        final ListTag listTag5 = new ListTag();
        for (int integer6 = 0; integer6 < 8; ++integer6) {
            boolean boolean7 = true;
            for (int integer7 = 0; integer7 < 16 && boolean7; ++integer7) {
                for (int integer8 = 0; integer8 < 16 && boolean7; ++integer8) {
                    for (int integer9 = 0; integer9 < 16; ++integer9) {
                        final int integer10 = integer7 << 11 | integer9 << 7 | integer8 + (integer6 << 4);
                        final int integer11 = alphaChunk.blocks[integer10];
                        if (integer11 != 0) {
                            boolean7 = false;
                            break;
                        }
                    }
                }
            }
            if (!boolean7) {
                final byte[] arr5 = new byte[4096];
                final ChunkNibbleArray chunkNibbleArray9 = new ChunkNibbleArray();
                final ChunkNibbleArray chunkNibbleArray10 = new ChunkNibbleArray();
                final ChunkNibbleArray chunkNibbleArray11 = new ChunkNibbleArray();
                for (int integer11 = 0; integer11 < 16; ++integer11) {
                    for (int integer12 = 0; integer12 < 16; ++integer12) {
                        for (int integer13 = 0; integer13 < 16; ++integer13) {
                            final int integer14 = integer11 << 11 | integer13 << 7 | integer12 + (integer6 << 4);
                            final int integer15 = alphaChunk.blocks[integer14];
                            arr5[integer12 << 8 | integer13 << 4 | integer11] = (byte)(integer15 & 0xFF);
                            chunkNibbleArray9.set(integer11, integer12, integer13, alphaChunk.data.get(integer11, integer12 + (integer6 << 4), integer13));
                            chunkNibbleArray10.set(integer11, integer12, integer13, alphaChunk.skyLight.get(integer11, integer12 + (integer6 << 4), integer13));
                            chunkNibbleArray11.set(integer11, integer12, integer13, alphaChunk.blockLight.get(integer11, integer12 + (integer6 << 4), integer13));
                        }
                    }
                }
                final CompoundTag compoundTag12 = new CompoundTag();
                compoundTag12.putByte("Y", (byte)(integer6 & 0xFF));
                compoundTag12.putByteArray("Blocks", arr5);
                compoundTag12.putByteArray("Data", chunkNibbleArray9.asByteArray());
                compoundTag12.putByteArray("SkyLight", chunkNibbleArray10.asByteArray());
                compoundTag12.putByteArray("BlockLight", chunkNibbleArray11.asByteArray());
                ((AbstractList<CompoundTag>)listTag5).add(compoundTag12);
            }
        }
        tag.put("Sections", listTag5);
        final byte[] arr6 = new byte[256];
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                mutable7.set(alphaChunk.x << 4 | integer7, 0, alphaChunk.z << 4 | integer8);
                arr6[integer8 << 4 | integer7] = (byte)(Registry.BIOME.getRawId(biomeSource.getBiome(mutable7)) & 0xFF);
            }
        }
        tag.putByteArray("Biomes", arr6);
        tag.put("Entities", alphaChunk.entities);
        tag.put("TileEntities", alphaChunk.blockEntities);
        if (alphaChunk.blockTicks != null) {
            tag.put("TileTicks", alphaChunk.blockTicks);
        }
        tag.putBoolean("convertedFromAlphaFormat", true);
    }
    
    public static class AlphaChunk
    {
        public long lastUpdate;
        public boolean terrainPopulated;
        public byte[] heightMap;
        public AlphaChunkDataArray blockLight;
        public AlphaChunkDataArray skyLight;
        public AlphaChunkDataArray data;
        public byte[] blocks;
        public ListTag entities;
        public ListTag blockEntities;
        public ListTag blockTicks;
        public final int x;
        public final int z;
        
        public AlphaChunk(final int x, final int z) {
            this.x = x;
            this.z = z;
        }
    }
}
