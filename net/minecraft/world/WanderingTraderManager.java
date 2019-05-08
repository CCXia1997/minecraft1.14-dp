package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.Entity;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.server.world.ServerWorld;
import java.util.Random;

public class WanderingTraderManager
{
    private final Random random;
    private final ServerWorld world;
    private int c;
    private int d;
    private int e;
    
    public WanderingTraderManager(final ServerWorld serverWorld) {
        this.random = new Random();
        this.world = serverWorld;
        this.c = 1200;
        final LevelProperties levelProperties2 = serverWorld.getLevelProperties();
        this.d = levelProperties2.getWanderingTraderSpawnDelay();
        this.e = levelProperties2.getWanderingTraderSpawnChance();
        if (this.d == 0 && this.e == 0) {
            levelProperties2.setWanderingTraderSpawnDelay(this.d = 24000);
            levelProperties2.setWanderingTraderSpawnChance(this.e = 25);
        }
    }
    
    public void tick() {
        final int c = this.c - 1;
        this.c = c;
        if (c > 0) {
            return;
        }
        this.c = 1200;
        final LevelProperties levelProperties1 = this.world.getLevelProperties();
        levelProperties1.setWanderingTraderSpawnDelay(this.d -= 1200);
        if (this.d > 0) {
            return;
        }
        this.d = 24000;
        if (!this.world.getGameRules().getBoolean("doMobSpawning")) {
            return;
        }
        final int integer2 = this.e;
        levelProperties1.setWanderingTraderSpawnChance(this.e = MathHelper.clamp(this.e + 25, 25, 75));
        if (this.random.nextInt(100) > integer2) {
            return;
        }
        if (this.b()) {
            this.e = 25;
        }
    }
    
    private boolean b() {
        final PlayerEntity playerEntity1 = this.world.getRandomAlivePlayer();
        if (playerEntity1 == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        final BlockPos blockPos2 = playerEntity1.getBlockPos();
        final int integer3 = 48;
        final PointOfInterestStorage pointOfInterestStorage4 = this.world.getPointOfInterestStorage();
        final Optional<BlockPos> optional5 = pointOfInterestStorage4.getPosition(PointOfInterestType.r.getCompletionCondition(), blockPos -> true, blockPos2, 48, PointOfInterestStorage.OccupationStatus.ANY);
        final BlockPos blockPos3 = optional5.orElse(blockPos2);
        final BlockPos blockPos4 = this.a(blockPos3, 48);
        if (blockPos4 != null) {
            if (this.world.getBiome(blockPos4) == Biomes.aa) {
                return false;
            }
            final WanderingTraderEntity wanderingTraderEntity8 = EntityType.aK.spawn(this.world, null, null, null, blockPos4, SpawnType.h, false, false);
            if (wanderingTraderEntity8 != null) {
                for (int integer4 = 0; integer4 < 2; ++integer4) {
                    this.a(wanderingTraderEntity8, 4);
                }
                this.world.getLevelProperties().setWanderingTraderId(wanderingTraderEntity8.getUuid());
                wanderingTraderEntity8.setDespawnDelay(48000);
                wanderingTraderEntity8.setWanderTarget(blockPos3);
                wanderingTraderEntity8.setWalkTarget(blockPos3, 16);
                return true;
            }
        }
        return false;
    }
    
    private void a(final WanderingTraderEntity wanderingTraderEntity, final int integer) {
        final BlockPos blockPos3 = this.a(new BlockPos(wanderingTraderEntity), integer);
        if (blockPos3 == null) {
            return;
        }
        final TraderLlamaEntity traderLlamaEntity4 = EntityType.ax.spawn(this.world, null, null, null, blockPos3, SpawnType.h, false, false);
        if (traderLlamaEntity4 == null) {
            return;
        }
        traderLlamaEntity4.attachLeash(wanderingTraderEntity, true);
        traderLlamaEntity4.setDespawnDelay(47999);
    }
    
    @Nullable
    private BlockPos a(final BlockPos blockPos, final int integer) {
        BlockPos blockPos2 = null;
        for (int integer2 = 0; integer2 < 10; ++integer2) {
            final int integer3 = blockPos.getX() + this.random.nextInt(integer * 2) - integer;
            final int integer4 = blockPos.getZ() + this.random.nextInt(integer * 2) - integer;
            final int integer5 = this.world.getTop(Heightmap.Type.b, integer3, integer4);
            final BlockPos blockPos3 = new BlockPos(integer3, integer5, integer4);
            if (SpawnHelper.canSpawn(SpawnRestriction.Location.a, this.world, blockPos3, EntityType.aK)) {
                blockPos2 = blockPos3;
                break;
            }
        }
        return blockPos2;
    }
}
