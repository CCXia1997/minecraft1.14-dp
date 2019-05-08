package net.minecraft.world.gen;

import java.util.Arrays;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.util.WeightedPicker;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.List;

public class PillagerSpawner
{
    private static final List<SpawnEntry> SPAWN_ENTRIES;
    private int ticksUntilNextSpawn;
    
    public int spawn(final ServerWorld serverWorld, final boolean spawnMonsters, final boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        }
        final Random random4 = serverWorld.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += 6000 + random4.nextInt(1200);
        final long long5 = serverWorld.getTimeOfDay() / 24000L;
        if (long5 < 5L || !serverWorld.isDaylight()) {
            return 0;
        }
        if (random4.nextInt(5) != 0) {
            return 0;
        }
        final int integer7 = serverWorld.getPlayers().size();
        if (integer7 < 1) {
            return 0;
        }
        final PlayerEntity playerEntity8 = serverWorld.getPlayers().get(random4.nextInt(integer7));
        if (playerEntity8.isSpectator()) {
            return 0;
        }
        if (serverWorld.isNearOccupiedPointOfInterest(playerEntity8.getBlockPos())) {
            return 0;
        }
        final int integer8 = (24 + random4.nextInt(24)) * (random4.nextBoolean() ? -1 : 1);
        final int integer9 = (24 + random4.nextInt(24)) * (random4.nextBoolean() ? -1 : 1);
        final BlockPos blockPos11 = new BlockPos(playerEntity8).add(integer8, 0, integer9);
        if (!serverWorld.isAreaLoaded(blockPos11.getX() - 10, blockPos11.getY() - 10, blockPos11.getZ() - 10, blockPos11.getX() + 10, blockPos11.getY() + 10, blockPos11.getZ() + 10)) {
            return 0;
        }
        final Biome biome12 = serverWorld.getBiome(blockPos11);
        final Biome.Category category13 = biome12.getCategory();
        if (category13 != Biome.Category.PLAINS && category13 != Biome.Category.TAIGA && category13 != Biome.Category.DESERT && category13 != Biome.Category.SAVANNA) {
            return 0;
        }
        int integer10 = 1;
        this.spawnOneEntity(serverWorld, blockPos11, random4, true);
        for (int integer11 = (int)Math.ceil(serverWorld.getLocalDifficulty(blockPos11).getLocalDifficulty()), integer12 = 0; integer12 < integer11; ++integer12) {
            ++integer10;
            this.spawnOneEntity(serverWorld, blockPos11, random4, false);
        }
        return integer10;
    }
    
    private void spawnOneEntity(final World world, final BlockPos pos, final Random random, final boolean boolean4) {
        final SpawnEntry spawnEntry5 = WeightedPicker.<SpawnEntry>getRandom(random, PillagerSpawner.SPAWN_ENTRIES);
        final PatrolEntity patrolEntity6 = (PatrolEntity)spawnEntry5.entityType.create(world);
        if (patrolEntity6 != null) {
            final double double7 = pos.getX() + random.nextInt(5) - random.nextInt(5);
            final double double8 = pos.getZ() + random.nextInt(5) - random.nextInt(5);
            final BlockPos blockPos11 = patrolEntity6.world.getTopPosition(Heightmap.Type.f, new BlockPos(double7, pos.getY(), double8));
            if (boolean4) {
                patrolEntity6.setPatrolLeader(true);
                patrolEntity6.setRandomPatrolTarget();
            }
            patrolEntity6.setPosition(blockPos11.getX(), blockPos11.getY(), blockPos11.getZ());
            patrolEntity6.initialize(world, world.getLocalDifficulty(blockPos11), SpawnType.p, null, null);
            world.spawnEntity(patrolEntity6);
        }
    }
    
    static {
        SPAWN_ENTRIES = Arrays.<SpawnEntry>asList(new SpawnEntry(EntityType.PILLAGER, 80), new SpawnEntry(EntityType.VINDICATOR, 20));
    }
    
    public static class SpawnEntry extends WeightedPicker.Entry
    {
        public final EntityType<? extends PatrolEntity> entityType;
        
        public SpawnEntry(final EntityType<? extends PatrolEntity> entityType, final int weight) {
            super(weight);
            this.entityType = entityType;
        }
    }
}
