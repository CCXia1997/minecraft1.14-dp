package net.minecraft.world.gen.feature;

import org.apache.logging.log4j.LogManager;
import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.block.Material;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.BlockView;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature<DefaultFeatureConfig>
{
    private static final Logger LOGGER;
    private static final EntityType<?>[] MOB_SPAWNER_ENTITIES;
    private static final BlockState AIR;
    
    public DungeonFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final int integer6 = 3;
        final int integer7 = random.nextInt(2) + 2;
        final int integer8 = -integer7 - 1;
        final int integer9 = integer7 + 1;
        final int integer10 = -1;
        final int integer11 = 4;
        final int integer12 = random.nextInt(2) + 2;
        final int integer13 = -integer12 - 1;
        final int integer14 = integer12 + 1;
        int integer15 = 0;
        for (int integer16 = integer8; integer16 <= integer9; ++integer16) {
            for (int integer17 = -1; integer17 <= 4; ++integer17) {
                for (int integer18 = integer13; integer18 <= integer14; ++integer18) {
                    final BlockPos blockPos19 = pos.add(integer16, integer17, integer18);
                    final Material material20 = world.getBlockState(blockPos19).getMaterial();
                    final boolean boolean21 = material20.isSolid();
                    if (integer17 == -1 && !boolean21) {
                        return false;
                    }
                    if (integer17 == 4 && !boolean21) {
                        return false;
                    }
                    if ((integer16 == integer8 || integer16 == integer9 || integer18 == integer13 || integer18 == integer14) && integer17 == 0 && world.isAir(blockPos19) && world.isAir(blockPos19.up())) {
                        ++integer15;
                    }
                }
            }
        }
        if (integer15 < 1 || integer15 > 5) {
            return false;
        }
        for (int integer16 = integer8; integer16 <= integer9; ++integer16) {
            for (int integer17 = 3; integer17 >= -1; --integer17) {
                for (int integer18 = integer13; integer18 <= integer14; ++integer18) {
                    final BlockPos blockPos19 = pos.add(integer16, integer17, integer18);
                    if (integer16 == integer8 || integer17 == -1 || integer18 == integer13 || integer16 == integer9 || integer17 == 4 || integer18 == integer14) {
                        if (blockPos19.getY() >= 0 && !world.getBlockState(blockPos19.down()).getMaterial().isSolid()) {
                            world.setBlockState(blockPos19, DungeonFeature.AIR, 2);
                        }
                        else if (world.getBlockState(blockPos19).getMaterial().isSolid() && world.getBlockState(blockPos19).getBlock() != Blocks.bP) {
                            if (integer17 == -1 && random.nextInt(4) != 0) {
                                world.setBlockState(blockPos19, Blocks.bI.getDefaultState(), 2);
                            }
                            else {
                                world.setBlockState(blockPos19, Blocks.m.getDefaultState(), 2);
                            }
                        }
                    }
                    else if (world.getBlockState(blockPos19).getBlock() != Blocks.bP) {
                        world.setBlockState(blockPos19, DungeonFeature.AIR, 2);
                    }
                }
            }
        }
        for (int integer16 = 0; integer16 < 2; ++integer16) {
            for (int integer17 = 0; integer17 < 3; ++integer17) {
                final int integer18 = pos.getX() + random.nextInt(integer7 * 2 + 1) - integer7;
                final int integer19 = pos.getY();
                final int integer20 = pos.getZ() + random.nextInt(integer12 * 2 + 1) - integer12;
                final BlockPos blockPos20 = new BlockPos(integer18, integer19, integer20);
                if (world.isAir(blockPos20)) {
                    int integer21 = 0;
                    for (final Direction direction24 : Direction.Type.HORIZONTAL) {
                        if (world.getBlockState(blockPos20.offset(direction24)).getMaterial().isSolid()) {
                            ++integer21;
                        }
                    }
                    if (integer21 == 1) {
                        world.setBlockState(blockPos20, StructurePiece.a(world, blockPos20, Blocks.bP.getDefaultState()), 2);
                        LootableContainerBlockEntity.setLootTable(world, random, blockPos20, LootTables.CHEST_SIMPLE_DUNGEON);
                        break;
                    }
                }
            }
        }
        world.setBlockState(pos, Blocks.bN.getDefaultState(), 2);
        final BlockEntity blockEntity16 = world.getBlockEntity(pos);
        if (blockEntity16 instanceof MobSpawnerBlockEntity) {
            ((MobSpawnerBlockEntity)blockEntity16).getLogic().setEntityId(this.getMobSpawnerEntity(random));
        }
        else {
            DungeonFeature.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    private EntityType<?> getMobSpawnerEntity(final Random random) {
        return DungeonFeature.MOB_SPAWNER_ENTITIES[random.nextInt(DungeonFeature.MOB_SPAWNER_ENTITIES.length)];
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MOB_SPAWNER_ENTITIES = new EntityType[] { EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER };
        AIR = Blocks.kT.getDefaultState();
    }
}
