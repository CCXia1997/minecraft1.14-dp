package net.minecraft.world.gen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;

public class CatSpawner
{
    private int ticksUntilNextSpawn;
    
    public int spawn(final ServerWorld serverWorld, final boolean spawnMonsters, final boolean spawnAnimals) {
        if (!spawnAnimals || !serverWorld.getGameRules().getBoolean("doMobSpawning")) {
            return 0;
        }
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn = 1200;
        final PlayerEntity playerEntity4 = serverWorld.getRandomAlivePlayer();
        if (playerEntity4 == null) {
            return 0;
        }
        final Random random5 = serverWorld.random;
        final int integer6 = (8 + random5.nextInt(24)) * (random5.nextBoolean() ? -1 : 1);
        final int integer7 = (8 + random5.nextInt(24)) * (random5.nextBoolean() ? -1 : 1);
        final BlockPos blockPos8 = new BlockPos(playerEntity4).add(integer6, 0, integer7);
        if (!serverWorld.isAreaLoaded(blockPos8.getX() - 10, blockPos8.getY() - 10, blockPos8.getZ() - 10, blockPos8.getX() + 10, blockPos8.getY() + 10, blockPos8.getZ() + 10)) {
            return 0;
        }
        if (SpawnHelper.canSpawn(SpawnRestriction.Location.a, serverWorld, blockPos8, EntityType.CAT)) {
            if (serverWorld.isNearOccupiedPointOfInterest(blockPos8, 2)) {
                return this.spawnInHouse(serverWorld, blockPos8);
            }
            if (Feature.SWAMP_HUT.isInsideStructure(serverWorld, blockPos8)) {
                return this.spawnInSwampHut(serverWorld, blockPos8);
            }
        }
        return 0;
    }
    
    private int spawnInHouse(final ServerWorld world, final BlockPos pos) {
        final int integer3 = 48;
        if (world.getPointOfInterestStorage().count(PointOfInterestType.q.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED) > 4L) {
            final List<CatEntity> list4 = world.<CatEntity>getEntities(CatEntity.class, new BoundingBox(pos).expand(48.0, 8.0, 48.0));
            if (list4.size() < 5) {
                return this.spawn(pos, world);
            }
        }
        return 0;
    }
    
    private int spawnInSwampHut(final World world, final BlockPos pos) {
        final int integer3 = 16;
        final List<CatEntity> list4 = world.<CatEntity>getEntities(CatEntity.class, new BoundingBox(pos).expand(16.0, 8.0, 16.0));
        if (list4.size() < 1) {
            return this.spawn(pos, world);
        }
        return 0;
    }
    
    private int spawn(final BlockPos pos, final World world) {
        final CatEntity catEntity3 = EntityType.CAT.create(world);
        if (catEntity3 == null) {
            return 0;
        }
        catEntity3.initialize(world, world.getLocalDifficulty(pos), SpawnType.a, null, null);
        catEntity3.setPositionAndAngles(pos, 0.0f, 0.0f);
        world.spawnEntity(catEntity3);
        return 1;
    }
}
