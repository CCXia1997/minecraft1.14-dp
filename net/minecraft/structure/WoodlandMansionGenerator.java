package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Collections;
import net.minecraft.util.Pair;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockMirror;
import java.util.Random;
import java.util.List;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class WoodlandMansionGenerator
{
    public static void addPieces(final StructureManager manager, final BlockPos pos, final BlockRotation rotation, final List<Piece> pieces, final Random random) {
        final c c6 = new c(random);
        final LayoutGenerator layoutGenerator7 = new LayoutGenerator(manager, random);
        layoutGenerator7.generate(pos, rotation, pieces, c6);
    }
    
    public static class Piece extends SimpleStructurePiece
    {
        private final String template;
        private final BlockRotation rotation;
        private final BlockMirror mirror;
        
        public Piece(final StructureManager structureManager, final String string, final BlockPos blockPos, final BlockRotation blockRotation) {
            this(structureManager, string, blockPos, blockRotation, BlockMirror.NONE);
        }
        
        public Piece(final StructureManager structureManager, final String string, final BlockPos blockPos, final BlockRotation blockRotation, final BlockMirror blockMirror) {
            super(StructurePieceType.WOODLAND_MANSION, 0);
            this.template = string;
            this.pos = blockPos;
            this.rotation = blockRotation;
            this.mirror = blockMirror;
            this.a(structureManager);
        }
        
        public Piece(final StructureManager structureManager, final CompoundTag compoundTag) {
            super(StructurePieceType.WOODLAND_MANSION, compoundTag);
            this.template = compoundTag.getString("Template");
            this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
            this.mirror = BlockMirror.valueOf(compoundTag.getString("Mi"));
            this.a(structureManager);
        }
        
        private void a(final StructureManager structureManager) {
            final Structure structure2 = structureManager.getStructureOrBlank(new Identifier("woodland_mansion/" + this.template));
            final StructurePlacementData structurePlacementData3 = new StructurePlacementData().setIgnoreEntities(true).setRotation(this.rotation).setMirrored(this.mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(structure2, this.pos, structurePlacementData3);
        }
        
        @Override
        protected void toNbt(final CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template);
            tag.putString("Rot", this.placementData.getRotation().name());
            tag.putString("Mi", this.placementData.getMirror().name());
        }
        
        @Override
        protected void handleMetadata(final String metadata, final BlockPos pos, final IWorld world, final Random random, final MutableIntBoundingBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                final BlockRotation blockRotation6 = this.placementData.getRotation();
                BlockState blockState7 = Blocks.bP.getDefaultState();
                if ("ChestWest".equals(metadata)) {
                    blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState7).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, blockRotation6.rotate(Direction.WEST));
                }
                else if ("ChestEast".equals(metadata)) {
                    blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState7).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, blockRotation6.rotate(Direction.EAST));
                }
                else if ("ChestSouth".equals(metadata)) {
                    blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState7).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, blockRotation6.rotate(Direction.SOUTH));
                }
                else if ("ChestNorth".equals(metadata)) {
                    blockState7 = ((AbstractPropertyContainer<O, BlockState>)blockState7).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, blockRotation6.rotate(Direction.NORTH));
                }
                this.addChest(world, boundingBox, random, pos, LootTables.CHEST_WOODLAND_MANSION, blockState7);
            }
            else {
                IllagerEntity illagerEntity6 = null;
                switch (metadata) {
                    case "Mage": {
                        illagerEntity6 = EntityType.EVOKER.create(world.getWorld());
                        break;
                    }
                    case "Warrior": {
                        illagerEntity6 = EntityType.VINDICATOR.create(world.getWorld());
                        break;
                    }
                    default: {
                        return;
                    }
                }
                illagerEntity6.setPersistent();
                illagerEntity6.setPositionAndAngles(pos, 0.0f, 0.0f);
                illagerEntity6.initialize(world, world.getLocalDifficulty(new BlockPos(illagerEntity6)), SpawnType.d, null, null);
                world.spawnEntity(illagerEntity6);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }
    
    static class GenerationPiece
    {
        public BlockRotation rotation;
        public BlockPos position;
        public String template;
        
        private GenerationPiece() {
        }
    }
    
    static class LayoutGenerator
    {
        private final StructureManager manager;
        private final Random random;
        private int c;
        private int d;
        
        public LayoutGenerator(final StructureManager manager, final Random random) {
            this.manager = manager;
            this.random = random;
        }
        
        public void generate(final BlockPos pos, final BlockRotation rotation, final List<Piece> pieces, final c c) {
            final GenerationPiece generationPiece5 = new GenerationPiece();
            generationPiece5.position = pos;
            generationPiece5.rotation = rotation;
            generationPiece5.template = "wall_flat";
            final GenerationPiece generationPiece6 = new GenerationPiece();
            this.addEntrance(pieces, generationPiece5);
            generationPiece6.position = generationPiece5.position.up(8);
            generationPiece6.rotation = generationPiece5.rotation;
            generationPiece6.template = "wall_window";
            if (!pieces.isEmpty()) {}
            final g g7 = c.b;
            final g g8 = c.c;
            this.c = c.e + 1;
            this.d = c.f + 1;
            final int integer9 = c.e + 1;
            final int integer10 = c.f;
            this.addRoof(pieces, generationPiece5, g7, Direction.SOUTH, this.c, this.d, integer9, integer10);
            this.addRoof(pieces, generationPiece6, g7, Direction.SOUTH, this.c, this.d, integer9, integer10);
            final GenerationPiece generationPiece7 = new GenerationPiece();
            generationPiece7.position = generationPiece5.position.up(19);
            generationPiece7.rotation = generationPiece5.rotation;
            generationPiece7.template = "wall_window";
            boolean boolean12 = false;
            for (int integer11 = 0; integer11 < g8.c && !boolean12; ++integer11) {
                for (int integer12 = g8.b - 1; integer12 >= 0 && !boolean12; --integer12) {
                    if (WoodlandMansionGenerator.c.a(g8, integer12, integer11)) {
                        generationPiece7.position = generationPiece7.position.offset(rotation.rotate(Direction.SOUTH), 8 + (integer11 - this.d) * 8);
                        generationPiece7.position = generationPiece7.position.offset(rotation.rotate(Direction.EAST), (integer12 - this.c) * 8);
                        this.b(pieces, generationPiece7);
                        this.addRoof(pieces, generationPiece7, g8, Direction.SOUTH, integer12, integer11, integer12, integer11);
                        boolean12 = true;
                    }
                }
            }
            this.a(pieces, pos.up(16), rotation, g7, g8);
            this.a(pieces, pos.up(27), rotation, g8, null);
            if (!pieces.isEmpty()) {}
            final RoomPool[] arr13 = { new FirstFloorRoomPool(), new SecondFloorRoomPool(), new ThirdFloorRoomPool() };
            for (int integer12 = 0; integer12 < 3; ++integer12) {
                final BlockPos blockPos15 = pos.up(8 * integer12 + ((integer12 == 2) ? 3 : 0));
                final g g9 = c.d[integer12];
                final g g10 = (integer12 == 2) ? g8 : g7;
                final String string18 = (integer12 == 0) ? "carpet_south_1" : "carpet_south_2";
                final String string19 = (integer12 == 0) ? "carpet_west_1" : "carpet_west_2";
                for (int integer13 = 0; integer13 < g10.c; ++integer13) {
                    for (int integer14 = 0; integer14 < g10.b; ++integer14) {
                        if (g10.a(integer14, integer13) == 1) {
                            BlockPos blockPos16 = blockPos15.offset(rotation.rotate(Direction.SOUTH), 8 + (integer13 - this.d) * 8);
                            blockPos16 = blockPos16.offset(rotation.rotate(Direction.EAST), (integer14 - this.c) * 8);
                            pieces.add(new Piece(this.manager, "corridor_floor", blockPos16, rotation));
                            if (g10.a(integer14, integer13 - 1) == 1 || (g9.a(integer14, integer13 - 1) & 0x800000) == 0x800000) {
                                pieces.add(new Piece(this.manager, "carpet_north", blockPos16.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
                            }
                            if (g10.a(integer14 + 1, integer13) == 1 || (g9.a(integer14 + 1, integer13) & 0x800000) == 0x800000) {
                                pieces.add(new Piece(this.manager, "carpet_east", blockPos16.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation));
                            }
                            if (g10.a(integer14, integer13 + 1) == 1 || (g9.a(integer14, integer13 + 1) & 0x800000) == 0x800000) {
                                pieces.add(new Piece(this.manager, string18, blockPos16.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation));
                            }
                            if (g10.a(integer14 - 1, integer13) == 1 || (g9.a(integer14 - 1, integer13) & 0x800000) == 0x800000) {
                                pieces.add(new Piece(this.manager, string19, blockPos16.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation));
                            }
                        }
                    }
                }
                final String string20 = (integer12 == 0) ? "indoors_wall_1" : "indoors_wall_2";
                final String string21 = (integer12 == 0) ? "indoors_door_1" : "indoors_door_2";
                final List<Direction> list22 = Lists.newArrayList();
                for (int integer15 = 0; integer15 < g10.c; ++integer15) {
                    for (int integer16 = 0; integer16 < g10.b; ++integer16) {
                        boolean boolean13 = integer12 == 2 && g10.a(integer16, integer15) == 3;
                        if (g10.a(integer16, integer15) == 2 || boolean13) {
                            final int integer17 = g9.a(integer16, integer15);
                            final int integer18 = integer17 & 0xF0000;
                            final int integer19 = integer17 & 0xFFFF;
                            boolean13 = (boolean13 && (integer17 & 0x800000) == 0x800000);
                            list22.clear();
                            if ((integer17 & 0x200000) == 0x200000) {
                                for (final Direction direction30 : Direction.Type.HORIZONTAL) {
                                    if (g10.a(integer16 + direction30.getOffsetX(), integer15 + direction30.getOffsetZ()) == 1) {
                                        list22.add(direction30);
                                    }
                                }
                            }
                            Direction direction31 = null;
                            if (!list22.isEmpty()) {
                                direction31 = list22.get(this.random.nextInt(list22.size()));
                            }
                            else if ((integer17 & 0x100000) == 0x100000) {
                                direction31 = Direction.UP;
                            }
                            BlockPos blockPos17 = blockPos15.offset(rotation.rotate(Direction.SOUTH), 8 + (integer15 - this.d) * 8);
                            blockPos17 = blockPos17.offset(rotation.rotate(Direction.EAST), -1 + (integer16 - this.c) * 8);
                            if (WoodlandMansionGenerator.c.a(g10, integer16 - 1, integer15) && !c.a(g10, integer16 - 1, integer15, integer12, integer19)) {
                                pieces.add(new Piece(this.manager, (direction31 == Direction.WEST) ? string21 : string20, blockPos17, rotation));
                            }
                            if (g10.a(integer16 + 1, integer15) == 1 && !boolean13) {
                                final BlockPos blockPos18 = blockPos17.offset(rotation.rotate(Direction.EAST), 8);
                                pieces.add(new Piece(this.manager, (direction31 == Direction.EAST) ? string21 : string20, blockPos18, rotation));
                            }
                            if (WoodlandMansionGenerator.c.a(g10, integer16, integer15 + 1) && !c.a(g10, integer16, integer15 + 1, integer12, integer19)) {
                                BlockPos blockPos18 = blockPos17.offset(rotation.rotate(Direction.SOUTH), 7);
                                blockPos18 = blockPos18.offset(rotation.rotate(Direction.EAST), 7);
                                pieces.add(new Piece(this.manager, (direction31 == Direction.SOUTH) ? string21 : string20, blockPos18, rotation.rotate(BlockRotation.ROT_90)));
                            }
                            if (g10.a(integer16, integer15 - 1) == 1 && !boolean13) {
                                BlockPos blockPos18 = blockPos17.offset(rotation.rotate(Direction.NORTH), 1);
                                blockPos18 = blockPos18.offset(rotation.rotate(Direction.EAST), 7);
                                pieces.add(new Piece(this.manager, (direction31 == Direction.NORTH) ? string21 : string20, blockPos18, rotation.rotate(BlockRotation.ROT_90)));
                            }
                            if (integer18 == 65536) {
                                this.addSmallRoom(pieces, blockPos17, rotation, direction31, arr13[integer12]);
                            }
                            else if (integer18 == 131072 && direction31 != null) {
                                final Direction direction32 = c.b(g10, integer16, integer15, integer12, integer19);
                                final boolean boolean14 = (integer17 & 0x400000) == 0x400000;
                                this.addMediumRoom(pieces, blockPos17, rotation, direction32, direction31, arr13[integer12], boolean14);
                            }
                            else if (integer18 == 262144 && direction31 != null && direction31 != Direction.UP) {
                                Direction direction32 = direction31.rotateYClockwise();
                                if (!c.a(g10, integer16 + direction32.getOffsetX(), integer15 + direction32.getOffsetZ(), integer12, integer19)) {
                                    direction32 = direction32.getOpposite();
                                }
                                this.addBigRoom(pieces, blockPos17, rotation, direction32, direction31, arr13[integer12]);
                            }
                            else if (integer18 == 262144 && direction31 == Direction.UP) {
                                this.addBigSecretRoom(pieces, blockPos17, rotation, arr13[integer12]);
                            }
                        }
                    }
                }
            }
        }
        
        private void addRoof(final List<Piece> list, final GenerationPiece generationPiece, final g g, Direction direction, final int integer5, final int integer6, final int integer7, final int integer8) {
            int integer9 = integer5;
            int integer10 = integer6;
            final Direction direction2 = direction;
            do {
                if (!WoodlandMansionGenerator.c.a(g, integer9 + direction.getOffsetX(), integer10 + direction.getOffsetZ())) {
                    this.c(list, generationPiece);
                    direction = direction.rotateYClockwise();
                    if (integer9 == integer7 && integer10 == integer8 && direction2 == direction) {
                        continue;
                    }
                    this.b(list, generationPiece);
                }
                else if (WoodlandMansionGenerator.c.a(g, integer9 + direction.getOffsetX(), integer10 + direction.getOffsetZ()) && WoodlandMansionGenerator.c.a(g, integer9 + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), integer10 + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
                    this.d(list, generationPiece);
                    integer9 += direction.getOffsetX();
                    integer10 += direction.getOffsetZ();
                    direction = direction.rotateYCounterclockwise();
                }
                else {
                    integer9 += direction.getOffsetX();
                    integer10 += direction.getOffsetZ();
                    if (integer9 == integer7 && integer10 == integer8 && direction2 == direction) {
                        continue;
                    }
                    this.b(list, generationPiece);
                }
            } while (integer9 != integer7 || integer10 != integer8 || direction2 != direction);
        }
        
        private void a(final List<Piece> list, final BlockPos blockPos, final BlockRotation blockRotation, final g g4, @Nullable final g g5) {
            for (int integer6 = 0; integer6 < g4.c; ++integer6) {
                for (int integer7 = 0; integer7 < g4.b; ++integer7) {
                    BlockPos blockPos2 = blockPos;
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (integer6 - this.d) * 8);
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (integer7 - this.c) * 8);
                    final boolean boolean9 = g5 != null && WoodlandMansionGenerator.c.a(g5, integer7, integer6);
                    if (WoodlandMansionGenerator.c.a(g4, integer7, integer6) && !boolean9) {
                        list.add(new Piece(this.manager, "roof", blockPos2.up(3), blockRotation));
                        if (!WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6)) {
                            final BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                            list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation));
                        }
                        if (!WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6)) {
                            BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 0);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                            list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.ROT_180)));
                        }
                        if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                            final BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                            list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.ROT_270)));
                        }
                        if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                            BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                            list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.ROT_90)));
                        }
                    }
                }
            }
            if (g5 != null) {
                for (int integer6 = 0; integer6 < g4.c; ++integer6) {
                    for (int integer7 = 0; integer7 < g4.b; ++integer7) {
                        BlockPos blockPos2 = blockPos;
                        blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (integer6 - this.d) * 8);
                        blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (integer7 - this.c) * 8);
                        final boolean boolean9 = WoodlandMansionGenerator.c.a(g5, integer7, integer6);
                        if (WoodlandMansionGenerator.c.a(g4, integer7, integer6) && boolean9) {
                            if (!WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6)) {
                                final BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 7);
                                list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6)) {
                                BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                                blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                                list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.ROT_180)));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                                BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 0);
                                blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
                                list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.ROT_270)));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                                BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                                blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                                list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.ROT_90)));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6)) {
                                if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                                    BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 7);
                                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 2);
                                    list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation));
                                }
                                if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                                    BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 8);
                                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                                    list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.ROT_90)));
                                }
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6)) {
                                if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                                    BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 2);
                                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
                                    list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.ROT_270)));
                                }
                                if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                                    BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 8);
                                    list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.ROT_180)));
                                }
                            }
                        }
                    }
                }
            }
            for (int integer6 = 0; integer6 < g4.c; ++integer6) {
                for (int integer7 = 0; integer7 < g4.b; ++integer7) {
                    BlockPos blockPos2 = blockPos;
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (integer6 - this.d) * 8);
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (integer7 - this.c) * 8);
                    final boolean boolean9 = g5 != null && WoodlandMansionGenerator.c.a(g5, integer7, integer6);
                    if (WoodlandMansionGenerator.c.a(g4, integer7, integer6) && !boolean9) {
                        if (!WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6)) {
                            final BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                                final BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                                list.add(new Piece(this.manager, "roof_corner", blockPos4, blockRotation));
                            }
                            else if (WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6 + 1)) {
                                final BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 5);
                                list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                                list.add(new Piece(this.manager, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.ROT_270)));
                            }
                            else if (WoodlandMansionGenerator.c.a(g4, integer7 + 1, integer6 - 1)) {
                                BlockPos blockPos4 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 9);
                                blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.NORTH), 2);
                                list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.ROT_90)));
                            }
                        }
                        if (!WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6)) {
                            BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 0);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 0);
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 + 1)) {
                                final BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                                list.add(new Piece(this.manager, "roof_corner", blockPos4, blockRotation.rotate(BlockRotation.ROT_90)));
                            }
                            else if (WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6 + 1)) {
                                BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 8);
                                blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.WEST), 3);
                                list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.ROT_270)));
                            }
                            if (!WoodlandMansionGenerator.c.a(g4, integer7, integer6 - 1)) {
                                list.add(new Piece(this.manager, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.ROT_180)));
                            }
                            else if (WoodlandMansionGenerator.c.a(g4, integer7 - 1, integer6 - 1)) {
                                final BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 1);
                                list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.ROT_180)));
                            }
                        }
                    }
                }
            }
        }
        
        private void addEntrance(final List<Piece> list, final GenerationPiece generationPiece) {
            final Direction direction3 = generationPiece.rotation.rotate(Direction.WEST);
            list.add(new Piece(this.manager, "entrance", generationPiece.position.offset(direction3, 9), generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 16);
        }
        
        private void b(final List<Piece> list, final GenerationPiece generationPiece) {
            list.add(new Piece(this.manager, generationPiece.template, generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 7), generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 8);
        }
        
        private void c(final List<Piece> list, final GenerationPiece generationPiece) {
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -1);
            list.add(new Piece(this.manager, "wall_corner", generationPiece.position, generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -7);
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.WEST), -6);
            generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.ROT_90);
        }
        
        private void d(final List<Piece> list, final GenerationPiece generationPiece) {
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 6);
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 8);
            generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.ROT_270);
        }
        
        private void addSmallRoom(final List<Piece> list, final BlockPos blockPos, final BlockRotation blockRotation, final Direction direction, final RoomPool roomPool) {
            BlockRotation blockRotation2 = BlockRotation.ROT_0;
            String string7 = roomPool.getSmallRoom(this.random);
            if (direction != Direction.EAST) {
                if (direction == Direction.NORTH) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.ROT_270);
                }
                else if (direction == Direction.WEST) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.ROT_180);
                }
                else if (direction == Direction.SOUTH) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.ROT_90);
                }
                else {
                    string7 = roomPool.getSmallSecretRoom(this.random);
                }
            }
            BlockPos blockPos2 = Structure.a(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation2, 7, 7);
            blockRotation2 = blockRotation2.rotate(blockRotation);
            blockPos2 = blockPos2.rotate(blockRotation);
            final BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
            list.add(new Piece(this.manager, string7, blockPos3, blockRotation2));
        }
        
        private void addMediumRoom(final List<Piece> list, final BlockPos blockPos, final BlockRotation blockRotation, final Direction direction4, final Direction direction5, final RoomPool roomPool, final boolean staircase) {
            if (direction5 == Direction.EAST && direction4 == Direction.SOUTH) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation));
            }
            else if (direction5 == Direction.EAST && direction4 == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation, BlockMirror.LEFT_RIGHT));
            }
            else if (direction5 == Direction.WEST && direction4 == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_180)));
            }
            else if (direction5 == Direction.WEST && direction4 == Direction.SOUTH) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation, BlockMirror.FRONT_BACK));
            }
            else if (direction5 == Direction.SOUTH && direction4 == Direction.EAST) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_90), BlockMirror.LEFT_RIGHT));
            }
            else if (direction5 == Direction.SOUTH && direction4 == Direction.WEST) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_90)));
            }
            else if (direction5 == Direction.NORTH && direction4 == Direction.WEST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_90), BlockMirror.FRONT_BACK));
            }
            else if (direction5 == Direction.NORTH && direction4 == Direction.EAST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_270)));
            }
            else if (direction5 == Direction.SOUTH && direction4 == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.NORTH), 8);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation));
            }
            else if (direction5 == Direction.NORTH && direction4 == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 14);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_180)));
            }
            else if (direction5 == Direction.WEST && direction4 == Direction.EAST) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_90)));
            }
            else if (direction5 == Direction.EAST && direction4 == Direction.WEST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.WEST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.ROT_270)));
            }
            else if (direction5 == Direction.UP && direction4 == Direction.EAST) {
                final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation.rotate(BlockRotation.ROT_90)));
            }
            else if (direction5 == Direction.UP && direction4 == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.NORTH), 0);
                list.add(new Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation));
            }
        }
        
        private void addBigRoom(final List<Piece> list, final BlockPos blockPos, final BlockRotation blockRotation, final Direction direction4, final Direction direction5, final RoomPool roomPool) {
            int integer7 = 0;
            int integer8 = 0;
            BlockRotation blockRotation2 = blockRotation;
            BlockMirror blockMirror10 = BlockMirror.NONE;
            if (direction5 == Direction.EAST && direction4 == Direction.SOUTH) {
                integer7 = -7;
            }
            else if (direction5 == Direction.EAST && direction4 == Direction.NORTH) {
                integer7 = -7;
                integer8 = 6;
                blockMirror10 = BlockMirror.LEFT_RIGHT;
            }
            else if (direction5 == Direction.NORTH && direction4 == Direction.EAST) {
                integer7 = 1;
                integer8 = 14;
                blockRotation2 = blockRotation.rotate(BlockRotation.ROT_270);
            }
            else if (direction5 == Direction.NORTH && direction4 == Direction.WEST) {
                integer7 = 7;
                integer8 = 14;
                blockRotation2 = blockRotation.rotate(BlockRotation.ROT_270);
                blockMirror10 = BlockMirror.LEFT_RIGHT;
            }
            else if (direction5 == Direction.SOUTH && direction4 == Direction.WEST) {
                integer7 = 7;
                integer8 = -8;
                blockRotation2 = blockRotation.rotate(BlockRotation.ROT_90);
            }
            else if (direction5 == Direction.SOUTH && direction4 == Direction.EAST) {
                integer7 = 1;
                integer8 = -8;
                blockRotation2 = blockRotation.rotate(BlockRotation.ROT_90);
                blockMirror10 = BlockMirror.LEFT_RIGHT;
            }
            else if (direction5 == Direction.WEST && direction4 == Direction.NORTH) {
                integer7 = 15;
                integer8 = 6;
                blockRotation2 = blockRotation.rotate(BlockRotation.ROT_180);
            }
            else if (direction5 == Direction.WEST && direction4 == Direction.SOUTH) {
                integer7 = 15;
                blockMirror10 = BlockMirror.FRONT_BACK;
            }
            BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), integer7);
            blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), integer8);
            list.add(new Piece(this.manager, roomPool.getBigRoom(this.random), blockPos2, blockRotation2, blockMirror10));
        }
        
        private void addBigSecretRoom(final List<Piece> list, final BlockPos blockPos, final BlockRotation blockRotation, final RoomPool roomPool) {
            final BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
            list.add(new Piece(this.manager, roomPool.getBigSecretRoom(this.random), blockPos2, blockRotation, BlockMirror.NONE));
        }
    }
    
    static class c
    {
        private final Random a;
        private final g b;
        private final g c;
        private final g[] d;
        private final int e;
        private final int f;
        
        public c(final Random random) {
            this.a = random;
            final int integer2 = 11;
            this.e = 7;
            this.f = 4;
            (this.b = new g(11, 11, 5)).a(this.e, this.f, this.e + 1, this.f + 1, 3);
            this.b.a(this.e - 1, this.f, this.e - 1, this.f + 1, 2);
            this.b.a(this.e + 2, this.f - 2, this.e + 3, this.f + 3, 5);
            this.b.a(this.e + 1, this.f - 2, this.e + 1, this.f - 1, 1);
            this.b.a(this.e + 1, this.f + 2, this.e + 1, this.f + 3, 1);
            this.b.a(this.e - 1, this.f - 1, 1);
            this.b.a(this.e - 1, this.f + 2, 1);
            this.b.a(0, 0, 11, 1, 5);
            this.b.a(0, 9, 11, 11, 5);
            this.a(this.b, this.e, this.f - 2, Direction.WEST, 6);
            this.a(this.b, this.e, this.f + 3, Direction.WEST, 6);
            this.a(this.b, this.e - 2, this.f - 1, Direction.WEST, 3);
            this.a(this.b, this.e - 2, this.f + 2, Direction.WEST, 3);
            while (this.a(this.b)) {}
            (this.d = new g[3])[0] = new g(11, 11, 5);
            this.d[1] = new g(11, 11, 5);
            this.d[2] = new g(11, 11, 5);
            this.a(this.b, this.d[0]);
            this.a(this.b, this.d[1]);
            this.d[0].a(this.e + 1, this.f, this.e + 1, this.f + 1, 8388608);
            this.d[1].a(this.e + 1, this.f, this.e + 1, this.f + 1, 8388608);
            this.c = new g(this.b.b, this.b.c, 5);
            this.b();
            this.a(this.c, this.d[2]);
        }
        
        public static boolean a(final g g, final int integer2, final int integer3) {
            final int integer4 = g.a(integer2, integer3);
            return integer4 == 1 || integer4 == 2 || integer4 == 3 || integer4 == 4;
        }
        
        public boolean a(final g g, final int integer2, final int integer3, final int integer4, final int integer5) {
            return (this.d[integer4].a(integer2, integer3) & 0xFFFF) == integer5;
        }
        
        @Nullable
        public Direction b(final g g, final int integer2, final int integer3, final int integer4, final int integer5) {
            for (final Direction direction7 : Direction.Type.HORIZONTAL) {
                if (this.a(g, integer2 + direction7.getOffsetX(), integer3 + direction7.getOffsetZ(), integer4, integer5)) {
                    return direction7;
                }
            }
            return null;
        }
        
        private void a(final g g, final int integer2, final int integer3, final Direction direction, final int integer5) {
            if (integer5 <= 0) {
                return;
            }
            g.a(integer2, integer3, 1);
            g.a(integer2 + direction.getOffsetX(), integer3 + direction.getOffsetZ(), 0, 1);
            for (int integer6 = 0; integer6 < 8; ++integer6) {
                final Direction direction2 = Direction.fromHorizontal(this.a.nextInt(4));
                if (direction2 != direction.getOpposite()) {
                    if (direction2 != Direction.EAST || !this.a.nextBoolean()) {
                        final int integer7 = integer2 + direction.getOffsetX();
                        final int integer8 = integer3 + direction.getOffsetZ();
                        if (g.a(integer7 + direction2.getOffsetX(), integer8 + direction2.getOffsetZ()) == 0 && g.a(integer7 + direction2.getOffsetX() * 2, integer8 + direction2.getOffsetZ() * 2) == 0) {
                            this.a(g, integer2 + direction.getOffsetX() + direction2.getOffsetX(), integer3 + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, integer5 - 1);
                            break;
                        }
                    }
                }
            }
            final Direction direction3 = direction.rotateYClockwise();
            final Direction direction2 = direction.rotateYCounterclockwise();
            g.a(integer2 + direction3.getOffsetX(), integer3 + direction3.getOffsetZ(), 0, 2);
            g.a(integer2 + direction2.getOffsetX(), integer3 + direction2.getOffsetZ(), 0, 2);
            g.a(integer2 + direction.getOffsetX() + direction3.getOffsetX(), integer3 + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
            g.a(integer2 + direction.getOffsetX() + direction2.getOffsetX(), integer3 + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
            g.a(integer2 + direction.getOffsetX() * 2, integer3 + direction.getOffsetZ() * 2, 0, 2);
            g.a(integer2 + direction3.getOffsetX() * 2, integer3 + direction3.getOffsetZ() * 2, 0, 2);
            g.a(integer2 + direction2.getOffsetX() * 2, integer3 + direction2.getOffsetZ() * 2, 0, 2);
        }
        
        private boolean a(final g g) {
            boolean boolean2 = false;
            for (int integer3 = 0; integer3 < g.c; ++integer3) {
                for (int integer4 = 0; integer4 < g.b; ++integer4) {
                    if (g.a(integer4, integer3) == 0) {
                        int integer5 = 0;
                        integer5 += (a(g, integer4 + 1, integer3) ? 1 : 0);
                        integer5 += (a(g, integer4 - 1, integer3) ? 1 : 0);
                        integer5 += (a(g, integer4, integer3 + 1) ? 1 : 0);
                        integer5 += (a(g, integer4, integer3 - 1) ? 1 : 0);
                        if (integer5 >= 3) {
                            g.a(integer4, integer3, 2);
                            boolean2 = true;
                        }
                        else if (integer5 == 2) {
                            int integer6 = 0;
                            integer6 += (a(g, integer4 + 1, integer3 + 1) ? 1 : 0);
                            integer6 += (a(g, integer4 - 1, integer3 + 1) ? 1 : 0);
                            integer6 += (a(g, integer4 + 1, integer3 - 1) ? 1 : 0);
                            integer6 += (a(g, integer4 - 1, integer3 - 1) ? 1 : 0);
                            if (integer6 <= 1) {
                                g.a(integer4, integer3, 2);
                                boolean2 = true;
                            }
                        }
                    }
                }
            }
            return boolean2;
        }
        
        private void b() {
            final List<Pair<Integer, Integer>> list1 = Lists.newArrayList();
            final g g2 = this.d[1];
            for (int integer3 = 0; integer3 < this.c.c; ++integer3) {
                for (int integer4 = 0; integer4 < this.c.b; ++integer4) {
                    final int integer5 = g2.a(integer4, integer3);
                    final int integer6 = integer5 & 0xF0000;
                    if (integer6 == 131072 && (integer5 & 0x200000) == 0x200000) {
                        list1.add(new Pair<Integer, Integer>(integer4, integer3));
                    }
                }
            }
            if (list1.isEmpty()) {
                this.c.a(0, 0, this.c.b, this.c.c, 5);
                return;
            }
            final Pair<Integer, Integer> pair3 = list1.get(this.a.nextInt(list1.size()));
            int integer4 = g2.a(pair3.getLeft(), pair3.getRight());
            g2.a(pair3.getLeft(), pair3.getRight(), integer4 | 0x400000);
            final Direction direction5 = this.b(this.b, pair3.getLeft(), pair3.getRight(), 1, integer4 & 0xFFFF);
            final int integer6 = pair3.getLeft() + direction5.getOffsetX();
            final int integer7 = pair3.getRight() + direction5.getOffsetZ();
            for (int integer8 = 0; integer8 < this.c.c; ++integer8) {
                for (int integer9 = 0; integer9 < this.c.b; ++integer9) {
                    if (!a(this.b, integer9, integer8)) {
                        this.c.a(integer9, integer8, 5);
                    }
                    else if (integer9 == pair3.getLeft() && integer8 == pair3.getRight()) {
                        this.c.a(integer9, integer8, 3);
                    }
                    else if (integer9 == integer6 && integer8 == integer7) {
                        this.c.a(integer9, integer8, 3);
                        this.d[2].a(integer9, integer8, 8388608);
                    }
                }
            }
            final List<Direction> list2 = Lists.newArrayList();
            for (final Direction direction6 : Direction.Type.HORIZONTAL) {
                if (this.c.a(integer6 + direction6.getOffsetX(), integer7 + direction6.getOffsetZ()) == 0) {
                    list2.add(direction6);
                }
            }
            if (list2.isEmpty()) {
                this.c.a(0, 0, this.c.b, this.c.c, 5);
                g2.a(pair3.getLeft(), pair3.getRight(), integer4);
                return;
            }
            final Direction direction7 = list2.get(this.a.nextInt(list2.size()));
            this.a(this.c, integer6 + direction7.getOffsetX(), integer7 + direction7.getOffsetZ(), direction7, 4);
            while (this.a(this.c)) {}
        }
        
        private void a(final g g1, final g g2) {
            final List<Pair<Integer, Integer>> list3 = Lists.newArrayList();
            for (int integer4 = 0; integer4 < g1.c; ++integer4) {
                for (int integer5 = 0; integer5 < g1.b; ++integer5) {
                    if (g1.a(integer5, integer4) == 2) {
                        list3.add(new Pair<Integer, Integer>(integer5, integer4));
                    }
                }
            }
            Collections.shuffle(list3, this.a);
            int integer4 = 10;
            for (final Pair<Integer, Integer> pair6 : list3) {
                final int integer6 = pair6.getLeft();
                final int integer7 = pair6.getRight();
                if (g2.a(integer6, integer7) == 0) {
                    int integer8 = integer6;
                    int integer9 = integer6;
                    int integer10 = integer7;
                    int integer11 = integer7;
                    int integer12 = 65536;
                    if (g2.a(integer6 + 1, integer7) == 0 && g2.a(integer6, integer7 + 1) == 0 && g2.a(integer6 + 1, integer7 + 1) == 0 && g1.a(integer6 + 1, integer7) == 2 && g1.a(integer6, integer7 + 1) == 2 && g1.a(integer6 + 1, integer7 + 1) == 2) {
                        ++integer9;
                        ++integer11;
                        integer12 = 262144;
                    }
                    else if (g2.a(integer6 - 1, integer7) == 0 && g2.a(integer6, integer7 + 1) == 0 && g2.a(integer6 - 1, integer7 + 1) == 0 && g1.a(integer6 - 1, integer7) == 2 && g1.a(integer6, integer7 + 1) == 2 && g1.a(integer6 - 1, integer7 + 1) == 2) {
                        --integer8;
                        ++integer11;
                        integer12 = 262144;
                    }
                    else if (g2.a(integer6 - 1, integer7) == 0 && g2.a(integer6, integer7 - 1) == 0 && g2.a(integer6 - 1, integer7 - 1) == 0 && g1.a(integer6 - 1, integer7) == 2 && g1.a(integer6, integer7 - 1) == 2 && g1.a(integer6 - 1, integer7 - 1) == 2) {
                        --integer8;
                        --integer10;
                        integer12 = 262144;
                    }
                    else if (g2.a(integer6 + 1, integer7) == 0 && g1.a(integer6 + 1, integer7) == 2) {
                        ++integer9;
                        integer12 = 131072;
                    }
                    else if (g2.a(integer6, integer7 + 1) == 0 && g1.a(integer6, integer7 + 1) == 2) {
                        ++integer11;
                        integer12 = 131072;
                    }
                    else if (g2.a(integer6 - 1, integer7) == 0 && g1.a(integer6 - 1, integer7) == 2) {
                        --integer8;
                        integer12 = 131072;
                    }
                    else if (g2.a(integer6, integer7 - 1) == 0 && g1.a(integer6, integer7 - 1) == 2) {
                        --integer10;
                        integer12 = 131072;
                    }
                    int integer13 = this.a.nextBoolean() ? integer8 : integer9;
                    int integer14 = this.a.nextBoolean() ? integer10 : integer11;
                    int integer15 = 2097152;
                    if (!g1.b(integer13, integer14, 1)) {
                        integer13 = ((integer13 == integer8) ? integer9 : integer8);
                        integer14 = ((integer14 == integer10) ? integer11 : integer10);
                        if (!g1.b(integer13, integer14, 1)) {
                            integer14 = ((integer14 == integer10) ? integer11 : integer10);
                            if (!g1.b(integer13, integer14, 1)) {
                                integer13 = ((integer13 == integer8) ? integer9 : integer8);
                                integer14 = ((integer14 == integer10) ? integer11 : integer10);
                                if (!g1.b(integer13, integer14, 1)) {
                                    integer15 = 0;
                                    integer13 = integer8;
                                    integer14 = integer10;
                                }
                            }
                        }
                    }
                    for (int integer16 = integer10; integer16 <= integer11; ++integer16) {
                        for (int integer17 = integer8; integer17 <= integer9; ++integer17) {
                            if (integer17 == integer13 && integer16 == integer14) {
                                g2.a(integer17, integer16, 0x100000 | integer15 | integer12 | integer4);
                            }
                            else {
                                g2.a(integer17, integer16, integer12 | integer4);
                            }
                        }
                    }
                    ++integer4;
                }
            }
        }
    }
    
    static class g
    {
        private final int[][] a;
        private final int b;
        private final int c;
        private final int d;
        
        public g(final int integer1, final int integer2, final int integer3) {
            this.b = integer1;
            this.c = integer2;
            this.d = integer3;
            this.a = new int[integer1][integer2];
        }
        
        public void a(final int integer1, final int integer2, final int integer3) {
            if (integer1 >= 0 && integer1 < this.b && integer2 >= 0 && integer2 < this.c) {
                this.a[integer1][integer2] = integer3;
            }
        }
        
        public void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
            for (int integer6 = integer2; integer6 <= integer4; ++integer6) {
                for (int integer7 = integer1; integer7 <= integer3; ++integer7) {
                    this.a(integer7, integer6, integer5);
                }
            }
        }
        
        public int a(final int integer1, final int integer2) {
            if (integer1 >= 0 && integer1 < this.b && integer2 >= 0 && integer2 < this.c) {
                return this.a[integer1][integer2];
            }
            return this.d;
        }
        
        public void a(final int integer1, final int integer2, final int integer3, final int integer4) {
            if (this.a(integer1, integer2) == integer3) {
                this.a(integer1, integer2, integer4);
            }
        }
        
        public boolean b(final int integer1, final int integer2, final int integer3) {
            return this.a(integer1 - 1, integer2) == integer3 || this.a(integer1 + 1, integer2) == integer3 || this.a(integer1, integer2 + 1) == integer3 || this.a(integer1, integer2 - 1) == integer3;
        }
    }
    
    abstract static class RoomPool
    {
        private RoomPool() {
        }
        
        public abstract String getSmallRoom(final Random arg1);
        
        public abstract String getSmallSecretRoom(final Random arg1);
        
        public abstract String getMediumFunctionalRoom(final Random arg1, final boolean arg2);
        
        public abstract String getMediumGenericRoom(final Random arg1, final boolean arg2);
        
        public abstract String getMediumSecretRoom(final Random arg1);
        
        public abstract String getBigRoom(final Random arg1);
        
        public abstract String getBigSecretRoom(final Random arg1);
    }
    
    static class FirstFloorRoomPool extends RoomPool
    {
        private FirstFloorRoomPool() {
        }
        
        @Override
        public String getSmallRoom(final Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }
        
        @Override
        public String getSmallSecretRoom(final Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }
        
        @Override
        public String getMediumFunctionalRoom(final Random random, final boolean staircase) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }
        
        @Override
        public String getMediumGenericRoom(final Random random, final boolean staircase) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }
        
        @Override
        public String getMediumSecretRoom(final Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }
        
        @Override
        public String getBigRoom(final Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }
        
        @Override
        public String getBigSecretRoom(final Random random) {
            return "2x2_s1";
        }
    }
    
    static class SecondFloorRoomPool extends RoomPool
    {
        private SecondFloorRoomPool() {
        }
        
        @Override
        public String getSmallRoom(final Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }
        
        @Override
        public String getSmallSecretRoom(final Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }
        
        @Override
        public String getMediumFunctionalRoom(final Random random, final boolean staircase) {
            if (staircase) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + (random.nextInt(4) + 1);
        }
        
        @Override
        public String getMediumGenericRoom(final Random random, final boolean staircase) {
            if (staircase) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + (random.nextInt(5) + 1);
        }
        
        @Override
        public String getMediumSecretRoom(final Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }
        
        @Override
        public String getBigRoom(final Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }
        
        @Override
        public String getBigSecretRoom(final Random random) {
            return "2x2_s1";
        }
    }
    
    static class ThirdFloorRoomPool extends SecondFloorRoomPool
    {
        private ThirdFloorRoomPool() {
        }
    }
}
