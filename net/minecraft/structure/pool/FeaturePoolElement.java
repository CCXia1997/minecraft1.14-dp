package net.minecraft.structure.pool;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.registry.Registry;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import com.google.common.collect.Lists;
import net.minecraft.structure.Structure;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.StructureManager;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement
{
    private final ConfiguredFeature<?> feature;
    private final CompoundTag tag;
    
    @Deprecated
    public FeaturePoolElement(final ConfiguredFeature<?> feature) {
        this(feature, StructurePool.Projection.RIGID);
    }
    
    public FeaturePoolElement(final ConfiguredFeature<?> configuredFeature, final StructurePool.Projection projection) {
        super(projection);
        this.feature = configuredFeature;
        this.tag = this.b();
    }
    
    public <T> FeaturePoolElement(final Dynamic<T> dynamic) {
        super(dynamic);
        this.feature = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature").orElseEmptyMap());
        this.tag = this.b();
    }
    
    public CompoundTag b() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.putString("target_pool", "minecraft:empty");
        compoundTag1.putString("attachement_type", "minecraft:bottom");
        compoundTag1.putString("final_state", "minecraft:air");
        return compoundTag1;
    }
    
    public BlockPos a(final StructureManager structureManager, final BlockRotation blockRotation) {
        return BlockPos.ORIGIN;
    }
    
    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final Random random) {
        final List<Structure.StructureBlockInfo> list5 = Lists.newArrayList();
        list5.add(new Structure.StructureBlockInfo(pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.lY.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)JigsawBlock.FACING, Direction.DOWN), this.tag));
        return list5;
    }
    
    @Override
    public MutableIntBoundingBox getBoundingBox(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation) {
        final BlockPos blockPos4 = this.a(structureManager, rotation);
        return new MutableIntBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockPos4.getX(), pos.getY() + blockPos4.getY(), pos.getZ() + blockPos4.getZ());
    }
    
    @Override
    public boolean generate(final StructureManager structureManager, final IWorld world, final BlockPos pos, final BlockRotation rotation, final MutableIntBoundingBox boundingBox, final Random random) {
        final ChunkGenerator<?> chunkGenerator7 = world.getChunkManager().getChunkGenerator();
        return this.feature.generate(world, chunkGenerator7, random, pos);
    }
    
    public <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("feature"), this.feature.<T>serialize(dynamicOps).getValue())));
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.FEATURE_POOL_ELEMENT;
    }
    
    @Override
    public String toString() {
        return "Feature[" + Registry.FEATURE.getId(this.feature.feature) + "]";
    }
}
