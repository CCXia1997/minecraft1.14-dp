package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.fluid.FluidState;
import java.util.Iterator;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.BlockMirror;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import java.util.List;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.util.Identifier;

public class OceanRuinGenerator
{
    private static final Identifier[] WARM_RUINS;
    private static final Identifier[] BRICK_RUINS;
    private static final Identifier[] CRACKED_RUINS;
    private static final Identifier[] MOSSY_RUINS;
    private static final Identifier[] BIG_BRICK_RUINS;
    private static final Identifier[] BIG_MOSSY_RUINS;
    private static final Identifier[] BIG_CRACKED_RUINS;
    private static final Identifier[] BIG_WARM_RUINS;
    
    private static Identifier getRandomWarmRuin(final Random random) {
        return OceanRuinGenerator.WARM_RUINS[random.nextInt(OceanRuinGenerator.WARM_RUINS.length)];
    }
    
    private static Identifier getRandomBigWarmRuin(final Random random) {
        return OceanRuinGenerator.BIG_WARM_RUINS[random.nextInt(OceanRuinGenerator.BIG_WARM_RUINS.length)];
    }
    
    public static void addPieces(final StructureManager manager, final BlockPos pos, final BlockRotation rotation, final List<StructurePiece> pieces, final Random random, final OceanRuinFeatureConfig config) {
        final boolean boolean7 = random.nextFloat() <= config.largeProbability;
        final float float8 = boolean7 ? 0.9f : 0.8f;
        a(manager, pos, rotation, pieces, random, config, boolean7, float8);
        if (boolean7 && random.nextFloat() <= config.clusterProbability) {
            a(manager, random, rotation, pos, config, pieces);
        }
    }
    
    private static void a(final StructureManager manager, final Random random, final BlockRotation rotation, final BlockPos pos, final OceanRuinFeatureConfig config, final List<StructurePiece> pieces) {
        final int integer7 = pos.getX();
        final int integer8 = pos.getZ();
        final BlockPos blockPos9 = Structure.a(new BlockPos(15, 0, 15), BlockMirror.NONE, rotation, BlockPos.ORIGIN).add(integer7, 0, integer8);
        final MutableIntBoundingBox mutableIntBoundingBox10 = MutableIntBoundingBox.create(integer7, 0, integer8, blockPos9.getX(), 0, blockPos9.getZ());
        final BlockPos blockPos10 = new BlockPos(Math.min(integer7, blockPos9.getX()), 0, Math.min(integer8, blockPos9.getZ()));
        final List<BlockPos> list12 = getRoomPositions(random, blockPos10.getX(), blockPos10.getZ());
        for (int integer9 = MathHelper.nextInt(random, 4, 8), integer10 = 0; integer10 < integer9; ++integer10) {
            if (!list12.isEmpty()) {
                final int integer11 = random.nextInt(list12.size());
                final BlockPos blockPos11 = list12.remove(integer11);
                final int integer12 = blockPos11.getX();
                final int integer13 = blockPos11.getZ();
                final BlockRotation blockRotation19 = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
                final BlockPos blockPos12 = Structure.a(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation19, BlockPos.ORIGIN).add(integer12, 0, integer13);
                final MutableIntBoundingBox mutableIntBoundingBox11 = MutableIntBoundingBox.create(integer12, 0, integer13, blockPos12.getX(), 0, blockPos12.getZ());
                if (!mutableIntBoundingBox11.intersects(mutableIntBoundingBox10)) {
                    a(manager, blockPos11, blockRotation19, pieces, random, config, false, 0.8f);
                }
            }
        }
    }
    
    private static List<BlockPos> getRoomPositions(final Random random, final int x, final int z) {
        final List<BlockPos> list4 = Lists.newArrayList();
        list4.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
        list4.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + MathHelper.nextInt(random, 1, 7)));
        list4.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
        list4.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
        list4.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 6)));
        list4.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 3, 8)));
        list4.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + MathHelper.nextInt(random, 1, 7)));
        list4.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
        return list4;
    }
    
    private static void a(final StructureManager manager, final BlockPos pos, final BlockRotation rotation, final List<StructurePiece> pieces, final Random random, final OceanRuinFeatureConfig config, final boolean large, final float integrity) {
        if (config.biomeType == OceanRuinFeature.BiomeType.WARM) {
            final Identifier identifier9 = large ? getRandomBigWarmRuin(random) : getRandomWarmRuin(random);
            pieces.add(new Piece(manager, identifier9, pos, rotation, integrity, config.biomeType, large));
        }
        else if (config.biomeType == OceanRuinFeature.BiomeType.COLD) {
            final Identifier[] arr9 = large ? OceanRuinGenerator.BIG_BRICK_RUINS : OceanRuinGenerator.BRICK_RUINS;
            final Identifier[] arr10 = large ? OceanRuinGenerator.BIG_CRACKED_RUINS : OceanRuinGenerator.CRACKED_RUINS;
            final Identifier[] arr11 = large ? OceanRuinGenerator.BIG_MOSSY_RUINS : OceanRuinGenerator.MOSSY_RUINS;
            final int integer12 = random.nextInt(arr9.length);
            pieces.add(new Piece(manager, arr9[integer12], pos, rotation, integrity, config.biomeType, large));
            pieces.add(new Piece(manager, arr10[integer12], pos, rotation, 0.7f, config.biomeType, large));
            pieces.add(new Piece(manager, arr11[integer12], pos, rotation, 0.5f, config.biomeType, large));
        }
    }
    
    static {
        WARM_RUINS = new Identifier[] { new Identifier("underwater_ruin/warm_1"), new Identifier("underwater_ruin/warm_2"), new Identifier("underwater_ruin/warm_3"), new Identifier("underwater_ruin/warm_4"), new Identifier("underwater_ruin/warm_5"), new Identifier("underwater_ruin/warm_6"), new Identifier("underwater_ruin/warm_7"), new Identifier("underwater_ruin/warm_8") };
        BRICK_RUINS = new Identifier[] { new Identifier("underwater_ruin/brick_1"), new Identifier("underwater_ruin/brick_2"), new Identifier("underwater_ruin/brick_3"), new Identifier("underwater_ruin/brick_4"), new Identifier("underwater_ruin/brick_5"), new Identifier("underwater_ruin/brick_6"), new Identifier("underwater_ruin/brick_7"), new Identifier("underwater_ruin/brick_8") };
        CRACKED_RUINS = new Identifier[] { new Identifier("underwater_ruin/cracked_1"), new Identifier("underwater_ruin/cracked_2"), new Identifier("underwater_ruin/cracked_3"), new Identifier("underwater_ruin/cracked_4"), new Identifier("underwater_ruin/cracked_5"), new Identifier("underwater_ruin/cracked_6"), new Identifier("underwater_ruin/cracked_7"), new Identifier("underwater_ruin/cracked_8") };
        MOSSY_RUINS = new Identifier[] { new Identifier("underwater_ruin/mossy_1"), new Identifier("underwater_ruin/mossy_2"), new Identifier("underwater_ruin/mossy_3"), new Identifier("underwater_ruin/mossy_4"), new Identifier("underwater_ruin/mossy_5"), new Identifier("underwater_ruin/mossy_6"), new Identifier("underwater_ruin/mossy_7"), new Identifier("underwater_ruin/mossy_8") };
        BIG_BRICK_RUINS = new Identifier[] { new Identifier("underwater_ruin/big_brick_1"), new Identifier("underwater_ruin/big_brick_2"), new Identifier("underwater_ruin/big_brick_3"), new Identifier("underwater_ruin/big_brick_8") };
        BIG_MOSSY_RUINS = new Identifier[] { new Identifier("underwater_ruin/big_mossy_1"), new Identifier("underwater_ruin/big_mossy_2"), new Identifier("underwater_ruin/big_mossy_3"), new Identifier("underwater_ruin/big_mossy_8") };
        BIG_CRACKED_RUINS = new Identifier[] { new Identifier("underwater_ruin/big_cracked_1"), new Identifier("underwater_ruin/big_cracked_2"), new Identifier("underwater_ruin/big_cracked_3"), new Identifier("underwater_ruin/big_cracked_8") };
        BIG_WARM_RUINS = new Identifier[] { new Identifier("underwater_ruin/big_warm_4"), new Identifier("underwater_ruin/big_warm_5"), new Identifier("underwater_ruin/big_warm_6"), new Identifier("underwater_ruin/big_warm_7") };
    }
    
    public static class Piece extends SimpleStructurePiece
    {
        private final OceanRuinFeature.BiomeType biomeType;
        private final float integrity;
        private final Identifier template;
        private final BlockRotation rotation;
        private final boolean large;
        
        public Piece(final StructureManager structureManager, final Identifier template, final BlockPos pos, final BlockRotation rotation, final float integrity, final OceanRuinFeature.BiomeType biomeType, final boolean large) {
            super(StructurePieceType.OCEAN_TEMPLE, 0);
            this.template = template;
            this.pos = pos;
            this.rotation = rotation;
            this.integrity = integrity;
            this.biomeType = biomeType;
            this.large = large;
            this.initialize(structureManager);
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(StructurePieceType.OCEAN_TEMPLE, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.integrity = tag.getFloat("Integrity");
            this.biomeType = OceanRuinFeature.BiomeType.valueOf(tag.getString("BiomeType"));
            this.large = tag.getBoolean("IsLarge");
            this.initialize(manager);
        }
        
        private void initialize(final StructureManager structureManager) {
            final Structure structure2 = structureManager.getStructureOrBlank(this.template);
            final StructurePlacementData structurePlacementData3 = new StructurePlacementData().setRotation(this.rotation).setMirrored(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(structure2, this.pos, structurePlacementData3);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putString("Rot", this.rotation.name());
            tag.putFloat("Integrity", this.integrity);
            tag.putString("BiomeType", this.biomeType.toString());
            tag.putBoolean("IsLarge", this.large);
        }
        
        @Override
        protected void handleMetadata(final String metadata, final BlockPos pos, final IWorld world, final Random random, final MutableIntBoundingBox boundingBox) {
            if ("chest".equals(metadata)) {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.bP.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)ChestBlock.WATERLOGGED, world.getFluidState(pos).matches(FluidTags.a)), 2);
                final BlockEntity blockEntity6 = world.getBlockEntity(pos);
                if (blockEntity6 instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)blockEntity6).setLootTable(this.large ? LootTables.F : LootTables.E, random.nextLong());
                }
            }
            else if ("drowned".equals(metadata)) {
                final DrownedEntity drownedEntity6 = EntityType.DROWNED.create(world.getWorld());
                drownedEntity6.setPersistent();
                drownedEntity6.setPositionAndAngles(pos, 0.0f, 0.0f);
                drownedEntity6.initialize(world, world.getLocalDifficulty(pos), SpawnType.d, null, null);
                world.spawnEntity(drownedEntity6);
                if (pos.getY() > world.getSeaLevel()) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                }
                else {
                    world.setBlockState(pos, Blocks.A.getDefaultState(), 2);
                }
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            this.placementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            final int integer5 = world.getTop(Heightmap.Type.c, this.pos.getX(), this.pos.getZ());
            this.pos = new BlockPos(this.pos.getX(), integer5, this.pos.getZ());
            final BlockPos blockPos6 = Structure.a(new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.rotation, BlockPos.ORIGIN).add(this.pos);
            this.pos = new BlockPos(this.pos.getX(), this.a(this.pos, world, blockPos6), this.pos.getZ());
            return super.generate(world, random, boundingBox, pos);
        }
        
        private int a(final BlockPos blockPos1, final BlockView blockView, final BlockPos blockPos3) {
            int integer4 = blockPos1.getY();
            int integer5 = 512;
            final int integer6 = integer4 - 1;
            int integer7 = 0;
            for (final BlockPos blockPos4 : BlockPos.iterate(blockPos1, blockPos3)) {
                final int integer8 = blockPos4.getX();
                final int integer9 = blockPos4.getZ();
                int integer10 = blockPos1.getY() - 1;
                final BlockPos.Mutable mutable13 = new BlockPos.Mutable(integer8, integer10, integer9);
                BlockState blockState14 = blockView.getBlockState(mutable13);
                for (FluidState fluidState15 = blockView.getFluidState(mutable13); (blockState14.isAir() || fluidState15.matches(FluidTags.a) || blockState14.getBlock().matches(BlockTags.J)) && integer10 > 1; blockState14 = blockView.getBlockState(mutable13), fluidState15 = blockView.getFluidState(mutable13)) {
                    --integer10;
                    mutable13.set(integer8, integer10, integer9);
                }
                integer5 = Math.min(integer5, integer10);
                if (integer10 < integer6 - 2) {
                    ++integer7;
                }
            }
            final int integer11 = Math.abs(blockPos1.getX() - blockPos3.getX());
            if (integer6 - integer5 > 2 && integer7 > integer11 - 2) {
                integer4 = integer5 + 1;
            }
            return integer4;
        }
    }
}
