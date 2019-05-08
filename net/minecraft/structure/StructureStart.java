package net.minecraft.structure;

import java.util.AbstractList;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.google.common.collect.Lists;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.List;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart
{
    public static final StructureStart DEFAULT;
    private final StructureFeature<?> feature;
    protected final List<StructurePiece> children;
    protected MutableIntBoundingBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private final Biome biome;
    private int references;
    protected final ChunkRandom random;
    
    public StructureStart(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
        this.children = Lists.newArrayList();
        this.feature = structureFeature;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.references = integer6;
        this.biome = biome;
        (this.random = new ChunkRandom()).setStructureSeed(long7, chunkX, chunkZ);
        this.boundingBox = mutableIntBoundingBox;
    }
    
    public abstract void initialize(final ChunkGenerator<?> arg1, final StructureManager arg2, final int arg3, final int arg4, final Biome arg5);
    
    public MutableIntBoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public List<StructurePiece> getChildren() {
        return this.children;
    }
    
    public void generateStructure(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        synchronized (this.children) {
            final Iterator<StructurePiece> iterator6 = this.children.iterator();
            while (iterator6.hasNext()) {
                final StructurePiece structurePiece7 = iterator6.next();
                if (structurePiece7.getBoundingBox().intersects(boundingBox) && !structurePiece7.generate(world, random, boundingBox, pos)) {
                    iterator6.remove();
                }
            }
            this.setBoundingBoxFromChildren();
        }
    }
    
    protected void setBoundingBoxFromChildren() {
        this.boundingBox = MutableIntBoundingBox.empty();
        for (final StructurePiece structurePiece2 : this.children) {
            this.boundingBox.setFrom(structurePiece2.getBoundingBox());
        }
    }
    
    public CompoundTag toTag(final int chunkX, final int chunkZ) {
        final CompoundTag compoundTag3 = new CompoundTag();
        if (this.hasChildren()) {
            compoundTag3.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
            compoundTag3.putString("biome", Registry.BIOME.getId(this.biome).toString());
            compoundTag3.putInt("ChunkX", chunkX);
            compoundTag3.putInt("ChunkZ", chunkZ);
            compoundTag3.putInt("references", this.references);
            compoundTag3.put("BB", this.boundingBox.toNbt());
            final ListTag listTag4 = new ListTag();
            synchronized (this.children) {
                for (final StructurePiece structurePiece7 : this.children) {
                    ((AbstractList<CompoundTag>)listTag4).add(structurePiece7.getTag());
                }
            }
            compoundTag3.put("Children", listTag4);
            return compoundTag3;
        }
        compoundTag3.putString("id", "INVALID");
        return compoundTag3;
    }
    
    protected void a(final int integer1, final Random random, final int integer3) {
        final int integer4 = integer1 - integer3;
        int integer5 = this.boundingBox.getBlockCountY() + 1;
        if (integer5 < integer4) {
            integer5 += random.nextInt(integer4 - integer5);
        }
        final int integer6 = integer5 - this.boundingBox.maxY;
        this.boundingBox.translate(0, integer6, 0);
        for (final StructurePiece structurePiece8 : this.children) {
            structurePiece8.translate(0, integer6, 0);
        }
    }
    
    protected void a(final Random random, final int integer2, final int integer3) {
        final int integer4 = integer3 - integer2 + 1 - this.boundingBox.getBlockCountY();
        int integer5;
        if (integer4 > 1) {
            integer5 = integer2 + random.nextInt(integer4);
        }
        else {
            integer5 = integer2;
        }
        final int integer6 = integer5 - this.boundingBox.minY;
        this.boundingBox.translate(0, integer6, 0);
        for (final StructurePiece structurePiece8 : this.children) {
            structurePiece8.translate(0, integer6, 0);
        }
    }
    
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    public BlockPos getPos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }
    
    public boolean isInExistingChunk() {
        return this.references < this.getReferenceCountToBeInExistingChunk();
    }
    
    public void incrementReferences() {
        ++this.references;
    }
    
    protected int getReferenceCountToBeInExistingChunk() {
        return 1;
    }
    
    public StructureFeature<?> getFeature() {
        return this.feature;
    }
    
    static {
        DEFAULT = new StructureStart(Feature.MINESHAFT, 0, 0, Biomes.c, MutableIntBoundingBox.empty(), 0, 0L) {
            @Override
            public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            }
        };
    }
}
