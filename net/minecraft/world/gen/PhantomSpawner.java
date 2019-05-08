package net.minecraft.world.gen;

import net.minecraft.entity.EntityData;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.SpawnHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PhantomSpawner
{
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
        this.ticksUntilNextSpawn += (60 + random4.nextInt(60)) * 20;
        if (serverWorld.getAmbientDarkness() < 5 && serverWorld.dimension.hasSkyLight()) {
            return 0;
        }
        int integer5 = 0;
        for (final PlayerEntity playerEntity7 : serverWorld.getPlayers()) {
            if (playerEntity7.isSpectator()) {
                continue;
            }
            final BlockPos blockPos8 = new BlockPos(playerEntity7);
            if (serverWorld.dimension.hasSkyLight()) {
                if (blockPos8.getY() < serverWorld.getSeaLevel()) {
                    continue;
                }
                if (!serverWorld.isSkyVisible(blockPos8)) {
                    continue;
                }
            }
            final LocalDifficulty localDifficulty9 = serverWorld.getLocalDifficulty(blockPos8);
            if (!localDifficulty9.a(random4.nextFloat() * 3.0f)) {
                continue;
            }
            final ServerStatHandler serverStatHandler10 = ((ServerPlayerEntity)playerEntity7).getStatHandler();
            final int integer6 = MathHelper.clamp(serverStatHandler10.getStat(Stats.i.getOrCreateStat(Stats.m)), 1, Integer.MAX_VALUE);
            final int integer7 = 24000;
            if (random4.nextInt(integer6) < 72000) {
                continue;
            }
            final BlockPos blockPos9 = blockPos8.up(20 + random4.nextInt(15)).east(-10 + random4.nextInt(21)).south(-10 + random4.nextInt(21));
            final BlockState blockState14 = serverWorld.getBlockState(blockPos9);
            final FluidState fluidState15 = serverWorld.getFluidState(blockPos9);
            if (!SpawnHelper.isClearForSpawn(serverWorld, blockPos9, blockState14, fluidState15)) {
                continue;
            }
            EntityData entityData16 = null;
            final int integer8 = 1 + random4.nextInt(localDifficulty9.getGlobalDifficulty().getId() + 1);
            for (int integer9 = 0; integer9 < integer8; ++integer9) {
                final PhantomEntity phantomEntity19 = EntityType.PHANTOM.create(serverWorld);
                phantomEntity19.setPositionAndAngles(blockPos9, 0.0f, 0.0f);
                entityData16 = phantomEntity19.initialize(serverWorld, localDifficulty9, SpawnType.a, entityData16, null);
                serverWorld.spawnEntity(phantomEntity19);
            }
            integer5 += integer8;
        }
        return integer5;
    }
}
