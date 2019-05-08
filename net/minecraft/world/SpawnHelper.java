package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.util.WeightedPicker;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnType;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.SpawnRestriction;
import java.util.Objects;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.entity.EntityCategory;
import org.apache.logging.log4j.Logger;

public final class SpawnHelper
{
    private static final Logger LOGGER;
    
    public static void spawnEntitiesInChunk(final EntityCategory category, final World world, final WorldChunk chunk, final BlockPos spawnPos) {
        final ChunkGenerator<?> chunkGenerator5 = world.getChunkManager().getChunkGenerator();
        int integer6 = 0;
        final BlockPos blockPos7 = a(world, chunk);
        final int integer7 = blockPos7.getX();
        final int integer8 = blockPos7.getY();
        final int integer9 = blockPos7.getZ();
        if (integer8 < 1) {
            return;
        }
        final BlockState blockState11 = chunk.getBlockState(blockPos7);
        if (blockState11.isSimpleFullBlock(chunk, blockPos7)) {
            return;
        }
        final BlockPos.Mutable mutable12 = new BlockPos.Mutable();
        for (int integer10 = 0; integer10 < 3; ++integer10) {
            int integer11 = integer7;
            int integer12 = integer9;
            final int integer13 = 6;
            Biome.SpawnEntry spawnEntry17 = null;
            EntityData entityData18 = null;
            int integer14 = MathHelper.ceil(Math.random() * 4.0);
            int integer15 = 0;
            for (int integer16 = 0; integer16 < integer14; ++integer16) {
                integer11 += world.random.nextInt(6) - world.random.nextInt(6);
                integer12 += world.random.nextInt(6) - world.random.nextInt(6);
                mutable12.set(integer11, integer8, integer12);
                final float float22 = integer11 + 0.5f;
                final float float23 = integer12 + 0.5f;
                final PlayerEntity playerEntity24 = world.getClosestPlayer(float22, float23, -1.0);
                if (playerEntity24 != null) {
                    if (playerEntity24.squaredDistanceTo(float22, integer8, float23) > 576.0) {
                        if (!spawnPos.isWithinDistance(new Vec3d(float22, integer8, float23), 24.0)) {
                            final ChunkPos chunkPos25 = new ChunkPos(mutable12);
                            if (Objects.equals(chunkPos25, chunk.getPos())) {
                                if (spawnEntry17 == null) {
                                    spawnEntry17 = a(chunkGenerator5, category, world.random, mutable12);
                                    if (spawnEntry17 == null) {
                                        break;
                                    }
                                    integer14 = spawnEntry17.minGroupSize + world.random.nextInt(1 + spawnEntry17.maxGroupSize - spawnEntry17.minGroupSize);
                                }
                                if (spawnEntry17.type.getCategory() != EntityCategory.e) {
                                    final EntityType<?> entityType26 = spawnEntry17.type;
                                    if (entityType26.isSummonable()) {
                                        if (a(chunkGenerator5, category, spawnEntry17, mutable12)) {
                                            final SpawnRestriction.Location location27 = SpawnRestriction.getLocation(entityType26);
                                            if (location27 != null) {
                                                if (canSpawn(location27, world, mutable12, entityType26)) {
                                                    if (world.doesNotCollide(entityType26.createSimpleBoundingBox(float22, integer8, float23))) {
                                                        MobEntity mobEntity28;
                                                        try {
                                                            final Entity entity29 = (Entity)entityType26.create(world);
                                                            if (!(entity29 instanceof MobEntity)) {
                                                                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType26));
                                                            }
                                                            mobEntity28 = (MobEntity)entity29;
                                                        }
                                                        catch (Exception exception29) {
                                                            SpawnHelper.LOGGER.warn("Failed to create mob", (Throwable)exception29);
                                                            return;
                                                        }
                                                        mobEntity28.setPositionAndAngles(float22, integer8, float23, world.random.nextFloat() * 360.0f, 0.0f);
                                                        if (playerEntity24.squaredDistanceTo(float22, integer8, float23) <= 16384.0 || !mobEntity28.canImmediatelyDespawn(playerEntity24.squaredDistanceTo(float22, integer8, float23))) {
                                                            if (mobEntity28.canSpawn(world, SpawnType.a)) {
                                                                if (mobEntity28.canSpawn(world)) {
                                                                    entityData18 = mobEntity28.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity28)), SpawnType.a, entityData18, null);
                                                                    ++integer6;
                                                                    ++integer15;
                                                                    world.spawnEntity(mobEntity28);
                                                                    if (integer6 >= mobEntity28.getLimitPerChunk()) {
                                                                        return;
                                                                    }
                                                                    if (mobEntity28.spawnsTooManyForEachTry(integer15)) {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Nullable
    private static Biome.SpawnEntry a(final ChunkGenerator<?> chunkGenerator, final EntityCategory entityCategory, final Random random, final BlockPos blockPos) {
        final List<Biome.SpawnEntry> list5 = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
        if (list5.isEmpty()) {
            return null;
        }
        return WeightedPicker.<Biome.SpawnEntry>getRandom(random, list5);
    }
    
    private static boolean a(final ChunkGenerator<?> chunkGenerator, final EntityCategory entityCategory, final Biome.SpawnEntry spawnEntry, final BlockPos blockPos) {
        final List<Biome.SpawnEntry> list5 = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
        return !list5.isEmpty() && list5.contains(spawnEntry);
    }
    
    private static BlockPos a(final World world, final WorldChunk worldChunk) {
        final ChunkPos chunkPos3 = worldChunk.getPos();
        final int integer4 = chunkPos3.getStartX() + world.random.nextInt(16);
        final int integer5 = chunkPos3.getStartZ() + world.random.nextInt(16);
        final int integer6 = worldChunk.sampleHeightmap(Heightmap.Type.b, integer4, integer5) + 1;
        final int integer7 = world.random.nextInt(integer6 + 1);
        return new BlockPos(integer4, integer7, integer5);
    }
    
    public static boolean isClearForSpawn(final BlockView blockView, final BlockPos blockPos, final BlockState blockState, final FluidState fluidState) {
        return !Block.isShapeFullCube(blockState.getCollisionShape(blockView, blockPos)) && !blockState.emitsRedstonePower() && fluidState.isEmpty() && !blockState.matches(BlockTags.B);
    }
    
    public static boolean canSpawn(final SpawnRestriction.Location location, final ViewableWorld viewableWorld, final BlockPos blockPos, @Nullable final EntityType<?> entityType) {
        if (entityType == null || !viewableWorld.getWorldBorder().contains(blockPos)) {
            return false;
        }
        final BlockState blockState5 = viewableWorld.getBlockState(blockPos);
        final FluidState fluidState6 = viewableWorld.getFluidState(blockPos);
        final BlockPos blockPos2 = blockPos.up();
        final BlockPos blockPos3 = blockPos.down();
        switch (location) {
            case b: {
                return fluidState6.matches(FluidTags.a) && viewableWorld.getFluidState(blockPos3).matches(FluidTags.a) && !viewableWorld.getBlockState(blockPos2).isSimpleFullBlock(viewableWorld, blockPos2);
            }
            default: {
                final BlockState blockState6 = viewableWorld.getBlockState(blockPos3);
                return blockState6.allowsSpawning(viewableWorld, blockPos3, entityType) && isClearForSpawn(viewableWorld, blockPos, blockState5, fluidState6) && isClearForSpawn(viewableWorld, blockPos2, viewableWorld.getBlockState(blockPos2), viewableWorld.getFluidState(blockPos2));
            }
        }
    }
    
    public static void populateEntities(final IWorld world, final Biome biome, final int chunkX, final int chunkZ, final Random random) {
        final List<Biome.SpawnEntry> list6 = biome.getEntitySpawnList(EntityCategory.b);
        if (list6.isEmpty()) {
            return;
        }
        final int integer7 = chunkX << 4;
        final int integer8 = chunkZ << 4;
        while (random.nextFloat() < biome.getMaxSpawnLimit()) {
            final Biome.SpawnEntry spawnEntry9 = WeightedPicker.<Biome.SpawnEntry>getRandom(random, list6);
            final int integer9 = spawnEntry9.minGroupSize + random.nextInt(1 + spawnEntry9.maxGroupSize - spawnEntry9.minGroupSize);
            EntityData entityData11 = null;
            int integer10 = integer7 + random.nextInt(16);
            int integer11 = integer8 + random.nextInt(16);
            final int integer12 = integer10;
            final int integer13 = integer11;
            for (int integer14 = 0; integer14 < integer9; ++integer14) {
                boolean boolean17 = false;
                for (int integer15 = 0; !boolean17 && integer15 < 4; ++integer15) {
                    final BlockPos blockPos19 = a(world, spawnEntry9.type, integer10, integer11);
                    if (spawnEntry9.type.isSummonable() && canSpawn(SpawnRestriction.Location.a, world, blockPos19, spawnEntry9.type)) {
                        final float float20 = spawnEntry9.type.getWidth();
                        final double double21 = MathHelper.clamp(integer10, integer7 + (double)float20, integer7 + 16.0 - float20);
                        final double double22 = MathHelper.clamp(integer11, integer8 + (double)float20, integer8 + 16.0 - float20);
                        if (!world.doesNotCollide(spawnEntry9.type.createSimpleBoundingBox(double21, blockPos19.getY(), double22))) {
                            continue;
                        }
                        Entity entity25;
                        try {
                            entity25 = (Entity)spawnEntry9.type.create(world.getWorld());
                        }
                        catch (Exception exception26) {
                            SpawnHelper.LOGGER.warn("Failed to create mob", (Throwable)exception26);
                            continue;
                        }
                        entity25.setPositionAndAngles(double21, blockPos19.getY(), double22, random.nextFloat() * 360.0f, 0.0f);
                        if (entity25 instanceof MobEntity) {
                            final MobEntity mobEntity26 = (MobEntity)entity25;
                            if (mobEntity26.canSpawn(world, SpawnType.b) && mobEntity26.canSpawn(world)) {
                                entityData11 = mobEntity26.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity26)), SpawnType.b, entityData11, null);
                                world.spawnEntity(mobEntity26);
                                boolean17 = true;
                            }
                        }
                    }
                    for (integer10 += random.nextInt(5) - random.nextInt(5), integer11 += random.nextInt(5) - random.nextInt(5); integer10 < integer7 || integer10 >= integer7 + 16 || integer11 < integer8 || integer11 >= integer8 + 16; integer10 = integer12 + random.nextInt(5) - random.nextInt(5), integer11 = integer13 + random.nextInt(5) - random.nextInt(5)) {}
                }
            }
        }
    }
    
    private static BlockPos a(final ViewableWorld viewableWorld, @Nullable final EntityType<?> entityType, final int integer3, final int integer4) {
        final BlockPos blockPos5 = new BlockPos(integer3, viewableWorld.getTop(SpawnRestriction.getHeightMapType(entityType), integer3, integer4), integer4);
        final BlockPos blockPos6 = blockPos5.down();
        if (viewableWorld.getBlockState(blockPos6).canPlaceAtSide(viewableWorld, blockPos6, BlockPlacementEnvironment.a)) {
            return blockPos6;
        }
        return blockPos5;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
