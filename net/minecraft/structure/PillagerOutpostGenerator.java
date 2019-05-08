package net.minecraft.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePool;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.SinglePoolElement;
import java.util.Random;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.ChunkRandom;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostGenerator
{
    public static void addPieces(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BlockPos pos, final List<StructurePiece> pieces, final ChunkRandom random) {
        StructurePoolBasedGenerator.addPieces(new Identifier("pillager_outpost/base_plates"), 7, Piece::new, chunkGenerator, structureManager, pos, pieces, random);
    }
    
    static {
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/base_plates"), new Identifier("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/base_plate"), 1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/towers"), new Identifier("empty"), ImmutableList.of(Pair.of(new ListPoolElement(ImmutableList.of(new SinglePoolElement("pillager_outpost/watchtower"), new SinglePoolElement("pillager_outpost/watchtower_overgrown", ImmutableList.of(new BlockRotStructureProcessor(0.05f))))), 1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/feature_plates"), new Identifier("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/feature_plate"), 1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("pillager_outpost/features"), new Identifier("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/feature_cage1"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_cage2"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_logs"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_tent1"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_tent2"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_targets"), 1), Pair.of(EmptyPoolElement.INSTANCE, 6)), StructurePool.Projection.RIGID));
    }
    
    public static class Piece extends PoolStructurePiece
    {
        public Piece(final StructureManager manager, final StructurePoolElement element, final BlockPos pos, final int groundLevelDelta, final BlockRotation rotation, final MutableIntBoundingBox boundingBox) {
            super(StructurePieceType.PILLAGER_OUTPOST, manager, element, pos, groundLevelDelta, rotation, boundingBox);
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(manager, tag, StructurePieceType.PILLAGER_OUTPOST);
        }
    }
}
