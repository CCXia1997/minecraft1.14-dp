package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import java.util.Random;
import java.util.List;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.util.Identifier;

public class IglooGenerator
{
    private static final Identifier TOP_TEMPLATE;
    private static final Identifier MIDDLE_TEMPLATE;
    private static final Identifier BOTTOM_TEMPLATE;
    private static final Map<Identifier, BlockPos> d;
    private static final Map<Identifier, BlockPos> e;
    
    public static void addPieces(final StructureManager manager, final BlockPos pos, final BlockRotation rotation, final List<StructurePiece> pieces, final Random random, final DefaultFeatureConfig defaultConfig) {
        if (random.nextDouble() < 0.5) {
            final int integer7 = random.nextInt(8) + 4;
            pieces.add(new Piece(manager, IglooGenerator.BOTTOM_TEMPLATE, pos, rotation, integer7 * 3));
            for (int integer8 = 0; integer8 < integer7 - 1; ++integer8) {
                pieces.add(new Piece(manager, IglooGenerator.MIDDLE_TEMPLATE, pos, rotation, integer8 * 3));
            }
        }
        pieces.add(new Piece(manager, IglooGenerator.TOP_TEMPLATE, pos, rotation, 0));
    }
    
    static {
        TOP_TEMPLATE = new Identifier("igloo/top");
        MIDDLE_TEMPLATE = new Identifier("igloo/middle");
        BOTTOM_TEMPLATE = new Identifier("igloo/bottom");
        d = ImmutableMap.<Identifier, BlockPos>of(IglooGenerator.TOP_TEMPLATE, new BlockPos(3, 5, 5), IglooGenerator.MIDDLE_TEMPLATE, new BlockPos(1, 3, 1), IglooGenerator.BOTTOM_TEMPLATE, new BlockPos(3, 6, 7));
        e = ImmutableMap.<Identifier, BlockPos>of(IglooGenerator.TOP_TEMPLATE, BlockPos.ORIGIN, IglooGenerator.MIDDLE_TEMPLATE, new BlockPos(2, -3, 4), IglooGenerator.BOTTOM_TEMPLATE, new BlockPos(0, -3, -2));
    }
    
    public static class Piece extends SimpleStructurePiece
    {
        private final Identifier template;
        private final BlockRotation rotation;
        
        public Piece(final StructureManager manager, final Identifier identifier, final BlockPos pos, final BlockRotation rotation, final int yOffset) {
            super(StructurePieceType.IGLOO, 0);
            this.template = identifier;
            final BlockPos blockPos6 = IglooGenerator.e.get(identifier);
            this.pos = pos.add(blockPos6.getX(), blockPos6.getY() - yOffset, blockPos6.getZ());
            this.rotation = rotation;
            this.initializeStructureData(manager);
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(StructurePieceType.IGLOO, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.initializeStructureData(manager);
        }
        
        private void initializeStructureData(final StructureManager manager) {
            final Structure structure2 = manager.getStructureOrBlank(this.template);
            final StructurePlacementData structurePlacementData3 = new StructurePlacementData().setRotation(this.rotation).setMirrored(BlockMirror.NONE).setPosition(IglooGenerator.d.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(structure2, this.pos, structurePlacementData3);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putString("Rot", this.rotation.name());
        }
        
        @Override
        protected void handleMetadata(final String metadata, final BlockPos pos, final IWorld world, final Random random, final MutableIntBoundingBox boundingBox) {
            if (!"chest".equals(metadata)) {
                return;
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            final BlockEntity blockEntity6 = world.getBlockEntity(pos.down());
            if (blockEntity6 instanceof ChestBlockEntity) {
                ((ChestBlockEntity)blockEntity6).setLootTable(LootTables.CHEST_IGLOO, random.nextLong());
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            final StructurePlacementData structurePlacementData5 = new StructurePlacementData().setRotation(this.rotation).setMirrored(BlockMirror.NONE).setPosition(IglooGenerator.d.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            final BlockPos blockPos6 = IglooGenerator.e.get(this.template);
            final BlockPos blockPos7 = this.pos.add(Structure.a(structurePlacementData5, new BlockPos(3 - blockPos6.getX(), 0, 0 - blockPos6.getZ())));
            final int integer8 = world.getTop(Heightmap.Type.a, blockPos7.getX(), blockPos7.getZ());
            final BlockPos blockPos8 = this.pos;
            this.pos = this.pos.add(0, integer8 - 90 - 1, 0);
            final boolean boolean10 = super.generate(world, random, boundingBox, pos);
            if (this.template.equals(IglooGenerator.TOP_TEMPLATE)) {
                final BlockPos blockPos9 = this.pos.add(Structure.a(structurePlacementData5, new BlockPos(3, 0, 5)));
                final BlockState blockState12 = world.getBlockState(blockPos9.down());
                if (!blockState12.isAir() && blockState12.getBlock() != Blocks.ce) {
                    world.setBlockState(blockPos9, Blocks.cC.getDefaultState(), 3);
                }
            }
            this.pos = blockPos8;
            return boolean10;
        }
    }
}
