package net.minecraft.structure;

import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.structure.processor.StructureProcessor;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.MutableIntBoundingBox;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;

public class StructurePlacementData
{
    private BlockMirror mirror;
    private BlockRotation rotation;
    private BlockPos position;
    private boolean ignoreEntities;
    @Nullable
    private ChunkPos chunkPosition;
    @Nullable
    private MutableIntBoundingBox boundingBox;
    private boolean placeFluids;
    @Nullable
    private Random random;
    @Nullable
    private Integer i;
    private int j;
    private final List<StructureProcessor> processors;
    private boolean l;
    
    public StructurePlacementData() {
        this.mirror = BlockMirror.NONE;
        this.rotation = BlockRotation.ROT_0;
        this.position = BlockPos.ORIGIN;
        this.placeFluids = true;
        this.processors = Lists.newArrayList();
    }
    
    public StructurePlacementData copy() {
        final StructurePlacementData structurePlacementData1 = new StructurePlacementData();
        structurePlacementData1.mirror = this.mirror;
        structurePlacementData1.rotation = this.rotation;
        structurePlacementData1.position = this.position;
        structurePlacementData1.ignoreEntities = this.ignoreEntities;
        structurePlacementData1.chunkPosition = this.chunkPosition;
        structurePlacementData1.boundingBox = this.boundingBox;
        structurePlacementData1.placeFluids = this.placeFluids;
        structurePlacementData1.random = this.random;
        structurePlacementData1.i = this.i;
        structurePlacementData1.j = this.j;
        structurePlacementData1.processors.addAll(this.processors);
        structurePlacementData1.l = this.l;
        return structurePlacementData1;
    }
    
    public StructurePlacementData setMirrored(final BlockMirror blockMirror) {
        this.mirror = blockMirror;
        return this;
    }
    
    public StructurePlacementData setRotation(final BlockRotation blockRotation) {
        this.rotation = blockRotation;
        return this;
    }
    
    public StructurePlacementData setPosition(final BlockPos position) {
        this.position = position;
        return this;
    }
    
    public StructurePlacementData setIgnoreEntities(final boolean boolean1) {
        this.ignoreEntities = boolean1;
        return this;
    }
    
    public StructurePlacementData setChunkPosition(final ChunkPos chunkPosition) {
        this.chunkPosition = chunkPosition;
        return this;
    }
    
    public StructurePlacementData setBoundingBox(final MutableIntBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }
    
    public StructurePlacementData setRandom(@Nullable final Random random) {
        this.random = random;
        return this;
    }
    
    public StructurePlacementData c(final boolean boolean1) {
        this.l = boolean1;
        return this;
    }
    
    public StructurePlacementData clearProcessors() {
        this.processors.clear();
        return this;
    }
    
    public StructurePlacementData addProcessor(final StructureProcessor processor) {
        this.processors.add(processor);
        return this;
    }
    
    public StructurePlacementData removeProcessor(final StructureProcessor processor) {
        this.processors.remove(processor);
        return this;
    }
    
    public BlockMirror getMirror() {
        return this.mirror;
    }
    
    public BlockRotation getRotation() {
        return this.rotation;
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
    
    public Random getRandom(@Nullable final BlockPos pos) {
        if (this.random != null) {
            return this.random;
        }
        if (pos == null) {
            return new Random(SystemUtil.getMeasuringTimeMs());
        }
        return new Random(MathHelper.hashCode(pos));
    }
    
    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    @Nullable
    public MutableIntBoundingBox h() {
        if (this.boundingBox == null && this.chunkPosition != null) {
            this.k();
        }
        return this.boundingBox;
    }
    
    public boolean i() {
        return this.l;
    }
    
    public List<StructureProcessor> getProcessors() {
        return this.processors;
    }
    
    void k() {
        if (this.chunkPosition != null) {
            this.boundingBox = this.b(this.chunkPosition);
        }
    }
    
    public boolean shouldPlaceFluids() {
        return this.placeFluids;
    }
    
    public List<Structure.StructureBlockInfo> a(final List<List<Structure.StructureBlockInfo>> list, @Nullable final BlockPos blockPos) {
        this.i = 8;
        if (this.i != null && this.i >= 0 && this.i < list.size()) {
            return list.get(this.i);
        }
        this.i = this.getRandom(blockPos).nextInt(list.size());
        return list.get(this.i);
    }
    
    @Nullable
    private MutableIntBoundingBox b(@Nullable final ChunkPos chunkPos) {
        if (chunkPos == null) {
            return this.boundingBox;
        }
        final int integer2 = chunkPos.x * 16;
        final int integer3 = chunkPos.z * 16;
        return new MutableIntBoundingBox(integer2, 0, integer3, integer2 + 16 - 1, 255, integer3 + 16 - 1);
    }
}
