package net.minecraft.structure;

import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import java.util.List;

public class EndCityGenerator
{
    private static final StructurePlacementData PLACEMENT_DATA;
    private static final StructurePlacementData IGNORE_AIR_PLACEMENT_DATA;
    private static final Part BUILDING;
    private static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS;
    private static final Part SMALL_TOWER;
    private static final Part BRIDGE_PIECE;
    private static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS;
    private static final Part FAT_TOWER;
    
    private static Piece createPiece(final StructureManager structureManager, final Piece lastPiece, final BlockPos relativePosition, final String template, final BlockRotation rotation, final boolean ignoreAir) {
        final Piece piece7 = new Piece(structureManager, template, lastPiece.pos, rotation, ignoreAir);
        final BlockPos blockPos8 = lastPiece.structure.a(lastPiece.placementData, relativePosition, piece7.placementData, BlockPos.ORIGIN);
        piece7.translate(blockPos8.getX(), blockPos8.getY(), blockPos8.getZ());
        return piece7;
    }
    
    public static void addPieces(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final List<StructurePiece> pieces, final Random random) {
        EndCityGenerator.FAT_TOWER.init();
        EndCityGenerator.BUILDING.init();
        EndCityGenerator.BRIDGE_PIECE.init();
        EndCityGenerator.SMALL_TOWER.init();
        Piece piece6 = addPiece(pieces, new Piece(structureManager, "base_floor", pos, rotation, true));
        piece6 = addPiece(pieces, createPiece(structureManager, piece6, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
        piece6 = addPiece(pieces, createPiece(structureManager, piece6, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
        piece6 = addPiece(pieces, createPiece(structureManager, piece6, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
        createPart(structureManager, EndCityGenerator.SMALL_TOWER, 1, piece6, null, pieces, random);
    }
    
    private static Piece addPiece(final List<StructurePiece> pieces, final Piece piece) {
        pieces.add(piece);
        return piece;
    }
    
    private static boolean createPart(final StructureManager manager, final Part piece, final int depth, final Piece parent, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
        if (depth > 8) {
            return false;
        }
        final List<StructurePiece> list8 = Lists.newArrayList();
        if (piece.create(manager, depth, parent, pos, list8, random)) {
            boolean boolean9 = false;
            final int integer10 = random.nextInt();
            for (final StructurePiece structurePiece12 : list8) {
                structurePiece12.o = integer10;
                final StructurePiece structurePiece13 = StructurePiece.a(pieces, structurePiece12.getBoundingBox());
                if (structurePiece13 != null && structurePiece13.o != parent.o) {
                    boolean9 = true;
                    break;
                }
            }
            if (!boolean9) {
                pieces.addAll(list8);
                return true;
            }
        }
        return false;
    }
    
    static {
        PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        IGNORE_AIR_PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        BUILDING = new Part() {
            @Override
            public void init() {
            }
            
            @Override
            public boolean create(final StructureManager manager, final int depth, final Piece root, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
                if (depth > 8) {
                    return false;
                }
                final BlockRotation blockRotation7 = root.placementData.getRotation();
                Piece piece8 = addPiece(pieces, createPiece(manager, root, pos, "base_floor", blockRotation7, true));
                final int integer9 = random.nextInt(3);
                if (integer9 == 0) {
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 4, -1), "base_roof", blockRotation7, true));
                }
                else if (integer9 == 1) {
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation7, false));
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 8, -1), "second_roof", blockRotation7, false));
                    createPart(manager, EndCityGenerator.SMALL_TOWER, depth + 1, piece8, null, pieces, random);
                }
                else if (integer9 == 2) {
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation7, false));
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation7, false));
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 8, -1), "third_roof", blockRotation7, true));
                    createPart(manager, EndCityGenerator.SMALL_TOWER, depth + 1, piece8, null, pieces, random);
                }
                return true;
            }
        };
        SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair>newArrayList(new Pair((A)BlockRotation.ROT_0, (B)new BlockPos(1, -1, 0)), new Pair((A)BlockRotation.ROT_90, (B)new BlockPos(6, -1, 1)), new Pair((A)BlockRotation.ROT_270, (B)new BlockPos(0, -1, 5)), new Pair((A)BlockRotation.ROT_180, (B)new BlockPos(5, -1, 6)));
        SMALL_TOWER = new Part() {
            @Override
            public void init() {
            }
            
            @Override
            public boolean create(final StructureManager manager, final int depth, final Piece root, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
                final BlockRotation blockRotation7 = root.placementData.getRotation();
                Piece piece8 = root;
                piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation7, true));
                piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(0, 7, 0), "tower_piece", blockRotation7, true));
                Piece piece9 = (random.nextInt(3) == 0) ? piece8 : null;
                for (int integer10 = 1 + random.nextInt(3), integer11 = 0; integer11 < integer10; ++integer11) {
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(0, 4, 0), "tower_piece", blockRotation7, true));
                    if (integer11 < integer10 - 1 && random.nextBoolean()) {
                        piece9 = piece8;
                    }
                }
                if (piece9 != null) {
                    for (final Pair<BlockRotation, BlockPos> pair12 : EndCityGenerator.SMALL_TOWER_BRIDGE_ATTACHMENTS) {
                        if (random.nextBoolean()) {
                            final Piece piece10 = addPiece(pieces, createPiece(manager, piece9, pair12.getRight(), "bridge_end", blockRotation7.rotate(pair12.getLeft()), true));
                            createPart(manager, EndCityGenerator.BRIDGE_PIECE, depth + 1, piece10, null, pieces, random);
                        }
                    }
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 4, -1), "tower_top", blockRotation7, true));
                }
                else {
                    if (depth != 7) {
                        return createPart(manager, EndCityGenerator.FAT_TOWER, depth + 1, piece8, null, pieces, random);
                    }
                    piece8 = addPiece(pieces, createPiece(manager, piece8, new BlockPos(-1, 4, -1), "tower_top", blockRotation7, true));
                }
                return true;
            }
        };
        BRIDGE_PIECE = new Part() {
            public boolean shipGenerated;
            
            @Override
            public void init() {
                this.shipGenerated = false;
            }
            
            @Override
            public boolean create(final StructureManager manager, final int depth, final Piece root, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
                final BlockRotation blockRotation7 = root.placementData.getRotation();
                final int integer8 = random.nextInt(4) + 1;
                Piece piece9 = addPiece(pieces, createPiece(manager, root, new BlockPos(0, 0, -4), "bridge_piece", blockRotation7, true));
                piece9.o = -1;
                int integer9 = 0;
                for (int integer10 = 0; integer10 < integer8; ++integer10) {
                    if (random.nextBoolean()) {
                        piece9 = addPiece(pieces, createPiece(manager, piece9, new BlockPos(0, integer9, -4), "bridge_piece", blockRotation7, true));
                        integer9 = 0;
                    }
                    else {
                        if (random.nextBoolean()) {
                            piece9 = addPiece(pieces, createPiece(manager, piece9, new BlockPos(0, integer9, -4), "bridge_steep_stairs", blockRotation7, true));
                        }
                        else {
                            piece9 = addPiece(pieces, createPiece(manager, piece9, new BlockPos(0, integer9, -8), "bridge_gentle_stairs", blockRotation7, true));
                        }
                        integer9 = 4;
                    }
                }
                if (this.shipGenerated || random.nextInt(10 - depth) != 0) {
                    if (!createPart(manager, EndCityGenerator.BUILDING, depth + 1, piece9, new BlockPos(-3, integer9 + 1, -11), pieces, random)) {
                        return false;
                    }
                }
                else {
                    addPiece(pieces, createPiece(manager, piece9, new BlockPos(-8 + random.nextInt(8), integer9, -70 + random.nextInt(10)), "ship", blockRotation7, true));
                    this.shipGenerated = true;
                }
                piece9 = addPiece(pieces, createPiece(manager, piece9, new BlockPos(4, integer9, 0), "bridge_end", blockRotation7.rotate(BlockRotation.ROT_180), true));
                piece9.o = -1;
                return true;
            }
        };
        FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair>newArrayList(new Pair((A)BlockRotation.ROT_0, (B)new BlockPos(4, -1, 0)), new Pair((A)BlockRotation.ROT_90, (B)new BlockPos(12, -1, 4)), new Pair((A)BlockRotation.ROT_270, (B)new BlockPos(0, -1, 8)), new Pair((A)BlockRotation.ROT_180, (B)new BlockPos(8, -1, 12)));
        FAT_TOWER = new Part() {
            @Override
            public void init() {
            }
            
            @Override
            public boolean create(final StructureManager manager, final int depth, final Piece root, final BlockPos pos, final List<StructurePiece> pieces, final Random random) {
                final BlockRotation blockRotation8 = root.placementData.getRotation();
                Piece piece7 = addPiece(pieces, createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation8, true));
                piece7 = addPiece(pieces, createPiece(manager, piece7, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation8, true));
                for (int integer9 = 0; integer9 < 2 && random.nextInt(3) != 0; ++integer9) {
                    piece7 = addPiece(pieces, createPiece(manager, piece7, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation8, true));
                    for (final Pair<BlockRotation, BlockPos> pair11 : EndCityGenerator.FAT_TOWER_BRIDGE_ATTACHMENTS) {
                        if (random.nextBoolean()) {
                            final Piece piece8 = addPiece(pieces, createPiece(manager, piece7, pair11.getRight(), "bridge_end", blockRotation8.rotate(pair11.getLeft()), true));
                            createPart(manager, EndCityGenerator.BRIDGE_PIECE, depth + 1, piece8, null, pieces, random);
                        }
                    }
                }
                piece7 = addPiece(pieces, createPiece(manager, piece7, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation8, true));
                return true;
            }
        };
    }
    
    public static class Piece extends SimpleStructurePiece
    {
        private final String template;
        private final BlockRotation rotation;
        private final boolean ignoreAir;
        
        public Piece(final StructureManager manager, final String template, final BlockPos pos, final BlockRotation rotation, final boolean ignoreAir) {
            super(StructurePieceType.END_CITY, 0);
            this.template = template;
            this.pos = pos;
            this.rotation = rotation;
            this.ignoreAir = ignoreAir;
            this.initializeStructureData(manager);
        }
        
        public Piece(final StructureManager manager, final CompoundTag tag) {
            super(StructurePieceType.END_CITY, tag);
            this.template = tag.getString("Template");
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.ignoreAir = tag.getBoolean("OW");
            this.initializeStructureData(manager);
        }
        
        private void initializeStructureData(final StructureManager manager) {
            final Structure structure2 = manager.getStructureOrBlank(new Identifier("end_city/" + this.template));
            final StructurePlacementData structurePlacementData3 = (this.ignoreAir ? EndCityGenerator.PLACEMENT_DATA : EndCityGenerator.IGNORE_AIR_PLACEMENT_DATA).copy().setRotation(this.rotation);
            this.setStructureData(structure2, this.pos, structurePlacementData3);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template);
            tag.putString("Rot", this.rotation.name());
            tag.putBoolean("OW", this.ignoreAir);
        }
        
        @Override
        protected void handleMetadata(final String metadata, final BlockPos pos, final IWorld world, final Random random, final MutableIntBoundingBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                final BlockPos blockPos6 = pos.down();
                if (boundingBox.contains(blockPos6)) {
                    LootableContainerBlockEntity.setLootTable(world, random, blockPos6, LootTables.CHEST_END_CITY_TREASURE);
                }
            }
            else if (metadata.startsWith("Sentry")) {
                final ShulkerEntity shulkerEntity6 = EntityType.SHULKER.create(world.getWorld());
                shulkerEntity6.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                shulkerEntity6.setAttachedBlock(pos);
                world.spawnEntity(shulkerEntity6);
            }
            else if (metadata.startsWith("Elytra")) {
                final ItemFrameEntity itemFrameEntity6 = new ItemFrameEntity(world.getWorld(), pos, this.rotation.rotate(Direction.SOUTH));
                itemFrameEntity6.setHeldItemStack(new ItemStack(Items.oX), false);
                world.spawnEntity(itemFrameEntity6);
            }
        }
    }
    
    interface Part
    {
        void init();
        
        boolean create(final StructureManager arg1, final int arg2, final Piece arg3, final BlockPos arg4, final List<StructurePiece> arg5, final Random arg6);
    }
}
