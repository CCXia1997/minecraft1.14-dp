package net.minecraft.structure;

import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.IWorld;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.List;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.pool.StructurePoolElement;

public abstract class PoolStructurePiece extends StructurePiece
{
    protected final StructurePoolElement poolElement;
    protected BlockPos pos;
    private final int groundLevelDelta;
    protected final BlockRotation rotation;
    private final List<JigsawJunction> junctions;
    private final StructureManager structureManager;
    
    public PoolStructurePiece(final StructurePieceType type, final StructureManager manager, final StructurePoolElement poolElement, final BlockPos pos, final int groundLevelDelta, final BlockRotation rotation, final MutableIntBoundingBox boundingBox) {
        super(type, 0);
        this.junctions = Lists.newArrayList();
        this.structureManager = manager;
        this.poolElement = poolElement;
        this.pos = pos;
        this.groundLevelDelta = groundLevelDelta;
        this.rotation = rotation;
        this.boundingBox = boundingBox;
    }
    
    public PoolStructurePiece(final StructureManager manager, final CompoundTag tag, final StructurePieceType type) {
        super(type, tag);
        this.junctions = Lists.newArrayList();
        this.structureManager = manager;
        this.pos = new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
        this.groundLevelDelta = tag.getInt("ground_level_delta");
        this.poolElement = DynamicDeserializer.<Object, EmptyPoolElement, StructurePoolElementType>deserialize((com.mojang.datafixers.Dynamic<Object>)new Dynamic((DynamicOps)NbtOps.INSTANCE, tag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
        this.rotation = BlockRotation.valueOf(tag.getString("rotation"));
        this.boundingBox = this.poolElement.getBoundingBox(manager, this.pos, this.rotation);
        final ListTag listTag4 = tag.getList("junctions", 10);
        this.junctions.clear();
        listTag4.forEach(tag -> this.junctions.add(JigsawJunction.deserialize((com.mojang.datafixers.Dynamic<Object>)new Dynamic((DynamicOps)NbtOps.INSTANCE, tag))));
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        tag.putInt("PosX", this.pos.getX());
        tag.putInt("PosY", this.pos.getY());
        tag.putInt("PosZ", this.pos.getZ());
        tag.putInt("ground_level_delta", this.groundLevelDelta);
        tag.put("pool_element", (Tag)this.poolElement.b((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        tag.putString("rotation", this.rotation.name());
        final ListTag listTag2 = new ListTag();
        for (final JigsawJunction jigsawJunction4 : this.junctions) {
            listTag2.add((Tag)jigsawJunction4.serialize((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        }
        tag.put("junctions", listTag2);
    }
    
    @Override
    public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        return this.poolElement.generate(this.structureManager, world, this.pos, this.rotation, boundingBox, random);
    }
    
    @Override
    public void translate(final int x, final int y, final int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }
    
    @Override
    public BlockRotation getRotation() {
        return this.rotation;
    }
    
    @Override
    public String toString() {
        return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement);
    }
    
    public StructurePoolElement getPoolElement() {
        return this.poolElement;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }
    
    public void addJunction(final JigsawJunction junction) {
        this.junctions.add(junction);
    }
    
    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}
