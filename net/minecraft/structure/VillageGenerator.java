package net.minecraft.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.pool.StructurePoolElement;
import java.util.Random;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.ChunkRandom;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VillageGenerator
{
    public static void addPieces(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BlockPos pos, final List<StructurePiece> pieces, final ChunkRandom random, final VillageFeatureConfig config) {
        PlainsVillageData.initialize();
        SnowyVillageData.initialize();
        SavannaVillageData.initialize();
        DesertVillageData.initialize();
        TaigaVillageData.initialize();
        StructurePoolBasedGenerator.addPieces(config.startPool, config.size, Piece::new, chunkGenerator, structureManager, pos, pieces, random);
    }
    
    public static class Piece extends PoolStructurePiece
    {
        public Piece(final StructureManager structureManager, final StructurePoolElement structurePoolElement, final BlockPos blockPos, final int integer, final BlockRotation blockRotation, final MutableIntBoundingBox mutableIntBoundingBox) {
            super(StructurePieceType.VILLAGE, structureManager, structurePoolElement, blockPos, integer, blockRotation, mutableIntBoundingBox);
        }
        
        public Piece(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(structureManager, compoundTag, StructurePieceType.VILLAGE);
        }
    }
}
