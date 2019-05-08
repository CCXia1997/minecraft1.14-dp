package net.minecraft.structure;

import java.util.Iterator;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import java.util.Random;
import java.util.List;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ShipwreckGenerator
{
    private static final BlockPos a;
    private static final Identifier[] BEACHED_TEMPLATES;
    private static final Identifier[] REGULAR_TEMPLATES;
    
    public static void addParts(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final List<StructurePiece> children, final Random random, final ShipwreckFeatureConfig config) {
        final Identifier identifier7 = config.isBeached ? ShipwreckGenerator.BEACHED_TEMPLATES[random.nextInt(ShipwreckGenerator.BEACHED_TEMPLATES.length)] : ShipwreckGenerator.REGULAR_TEMPLATES[random.nextInt(ShipwreckGenerator.REGULAR_TEMPLATES.length)];
        children.add(new Piece(structureManager, identifier7, pos, rotation, config.isBeached));
    }
    
    static {
        a = new BlockPos(4, 0, 15);
        BEACHED_TEMPLATES = new Identifier[] { new Identifier("shipwreck/with_mast"), new Identifier("shipwreck/sideways_full"), new Identifier("shipwreck/sideways_fronthalf"), new Identifier("shipwreck/sideways_backhalf"), new Identifier("shipwreck/rightsideup_full"), new Identifier("shipwreck/rightsideup_fronthalf"), new Identifier("shipwreck/rightsideup_backhalf"), new Identifier("shipwreck/with_mast_degraded"), new Identifier("shipwreck/rightsideup_full_degraded"), new Identifier("shipwreck/rightsideup_fronthalf_degraded"), new Identifier("shipwreck/rightsideup_backhalf_degraded") };
        REGULAR_TEMPLATES = new Identifier[] { new Identifier("shipwreck/with_mast"), new Identifier("shipwreck/upsidedown_full"), new Identifier("shipwreck/upsidedown_fronthalf"), new Identifier("shipwreck/upsidedown_backhalf"), new Identifier("shipwreck/sideways_full"), new Identifier("shipwreck/sideways_fronthalf"), new Identifier("shipwreck/sideways_backhalf"), new Identifier("shipwreck/rightsideup_full"), new Identifier("shipwreck/rightsideup_fronthalf"), new Identifier("shipwreck/rightsideup_backhalf"), new Identifier("shipwreck/with_mast_degraded"), new Identifier("shipwreck/upsidedown_full_degraded"), new Identifier("shipwreck/upsidedown_fronthalf_degraded"), new Identifier("shipwreck/upsidedown_backhalf_degraded"), new Identifier("shipwreck/sideways_full_degraded"), new Identifier("shipwreck/sideways_fronthalf_degraded"), new Identifier("shipwreck/sideways_backhalf_degraded"), new Identifier("shipwreck/rightsideup_full_degraded"), new Identifier("shipwreck/rightsideup_fronthalf_degraded"), new Identifier("shipwreck/rightsideup_backhalf_degraded") };
    }
    
    public static class Piece extends SimpleStructurePiece
    {
        private final BlockRotation rotation;
        private final Identifier template;
        private final boolean grounded;
        
        public Piece(final StructureManager manager, final Identifier identifier, final BlockPos pos, final BlockRotation rotation, final boolean grounded) {
            super(StructurePieceType.SHIPWRECK, 0);
            this.pos = pos;
            this.rotation = rotation;
            this.template = identifier;
            this.grounded = grounded;
            this.initializeStructureData(manager);
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(StructurePieceType.SHIPWRECK, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.grounded = tag.getBoolean("isBeached");
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.initializeStructureData(manager);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putBoolean("isBeached", this.grounded);
            tag.putString("Rot", this.rotation.name());
        }
        
        private void initializeStructureData(final StructureManager manager) {
            final Structure structure2 = manager.getStructureOrBlank(this.template);
            final StructurePlacementData structurePlacementData3 = new StructurePlacementData().setRotation(this.rotation).setMirrored(BlockMirror.NONE).setPosition(ShipwreckGenerator.a).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(structure2, this.pos, structurePlacementData3);
        }
        
        @Override
        protected void handleMetadata(final String metadata, final BlockPos pos, final IWorld world, final Random random, final MutableIntBoundingBox boundingBox) {
            if ("map_chest".equals(metadata)) {
                LootableContainerBlockEntity.setLootTable(world, random, pos.down(), LootTables.H);
            }
            else if ("treasure_chest".equals(metadata)) {
                LootableContainerBlockEntity.setLootTable(world, random, pos.down(), LootTables.J);
            }
            else if ("supply_chest".equals(metadata)) {
                LootableContainerBlockEntity.setLootTable(world, random, pos.down(), LootTables.I);
            }
        }
        
        @Override
        public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            int integer5 = 256;
            int integer6 = 0;
            final BlockPos blockPos7 = this.pos.add(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1);
            for (final BlockPos blockPos8 : BlockPos.iterate(this.pos, blockPos7)) {
                final int integer7 = world.getTop(this.grounded ? Heightmap.Type.a : Heightmap.Type.c, blockPos8.getX(), blockPos8.getZ());
                integer6 += integer7;
                integer5 = Math.min(integer5, integer7);
            }
            integer6 /= this.structure.getSize().getX() * this.structure.getSize().getZ();
            final int integer8 = this.grounded ? (integer5 - this.structure.getSize().getY() / 2 - random.nextInt(3)) : integer6;
            this.pos = new BlockPos(this.pos.getX(), integer8, this.pos.getZ());
            return super.generate(world, random, boundingBox, pos);
        }
    }
}
