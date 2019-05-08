package net.minecraft.entity.boss.dragon;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import java.util.List;
import net.minecraft.server.world.ServerWorld;

public enum EnderDragonSpawnState
{
    START {
        @Override
        public void run(final ServerWorld serverWorld, final EnderDragonFight enderDragonFight, final List<EnderCrystalEntity> list, final int integer, final BlockPos blockPos) {
            final BlockPos blockPos2 = new BlockPos(0, 128, 0);
            for (final EnderCrystalEntity enderCrystalEntity8 : list) {
                enderCrystalEntity8.setBeamTarget(blockPos2);
            }
            enderDragonFight.setSpawnState(EnderDragonSpawnState$1.PREPARE_CREATE_SPIKES);
        }
    }, 
    PREPARE_CREATE_SPIKES {
        @Override
        public void run(final ServerWorld serverWorld, final EnderDragonFight enderDragonFight, final List<EnderCrystalEntity> list, final int integer, final BlockPos blockPos) {
            if (integer < 100) {
                if (integer == 0 || integer == 50 || integer == 51 || integer == 52 || integer >= 95) {
                    serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            }
            else {
                enderDragonFight.setSpawnState(EnderDragonSpawnState$2.CREATE_SPIKES);
            }
        }
    }, 
    CREATE_SPIKES {
        @Override
        public void run(final ServerWorld serverWorld, final EnderDragonFight enderDragonFight, final List<EnderCrystalEntity> list, final int integer, final BlockPos blockPos) {
            final int integer2 = 40;
            final boolean boolean7 = integer % 40 == 0;
            final boolean boolean8 = integer % 40 == 39;
            if (boolean7 || boolean8) {
                final List<EndSpikeFeature.Spike> list2 = EndSpikeFeature.getSpikes(serverWorld);
                final int integer3 = integer / 40;
                if (integer3 < list2.size()) {
                    final EndSpikeFeature.Spike spike11 = list2.get(integer3);
                    if (boolean7) {
                        for (final EnderCrystalEntity enderCrystalEntity13 : list) {
                            enderCrystalEntity13.setBeamTarget(new BlockPos(spike11.getCenterX(), spike11.getHeight() + 1, spike11.getCenterZ()));
                        }
                    }
                    else {
                        final int integer4 = 10;
                        for (final BlockPos blockPos2 : BlockPos.iterate(new BlockPos(spike11.getCenterX() - 10, spike11.getHeight() - 10, spike11.getCenterZ() - 10), new BlockPos(spike11.getCenterX() + 10, spike11.getHeight() + 10, spike11.getCenterZ() + 10))) {
                            serverWorld.clearBlockState(blockPos2, false);
                        }
                        serverWorld.createExplosion(null, spike11.getCenterX() + 0.5f, spike11.getHeight(), spike11.getCenterZ() + 0.5f, 5.0f, Explosion.DestructionType.c);
                        final EndSpikeFeatureConfig endSpikeFeatureConfig13 = new EndSpikeFeatureConfig(true, ImmutableList.<EndSpikeFeature.Spike>of(spike11), new BlockPos(0, 128, 0));
                        Feature.ay.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), new Random(), new BlockPos(spike11.getCenterX(), 45, spike11.getCenterZ()), endSpikeFeatureConfig13);
                    }
                }
                else if (boolean7) {
                    enderDragonFight.setSpawnState(EnderDragonSpawnState$3.SPAWN_DRAGON);
                }
            }
        }
    }, 
    SPAWN_DRAGON {
        @Override
        public void run(final ServerWorld serverWorld, final EnderDragonFight enderDragonFight, final List<EnderCrystalEntity> list, final int integer, final BlockPos blockPos) {
            if (integer >= 100) {
                enderDragonFight.setSpawnState(EnderDragonSpawnState$4.END);
                enderDragonFight.resetEndCrystals();
                for (final EnderCrystalEntity enderCrystalEntity7 : list) {
                    enderCrystalEntity7.setBeamTarget(null);
                    serverWorld.createExplosion(enderCrystalEntity7, enderCrystalEntity7.x, enderCrystalEntity7.y, enderCrystalEntity7.z, 6.0f, Explosion.DestructionType.a);
                    enderCrystalEntity7.remove();
                }
            }
            else if (integer >= 80) {
                serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
            else if (integer == 0) {
                for (final EnderCrystalEntity enderCrystalEntity7 : list) {
                    enderCrystalEntity7.setBeamTarget(new BlockPos(0, 128, 0));
                }
            }
            else if (integer < 5) {
                serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    }, 
    END {
        @Override
        public void run(final ServerWorld serverWorld, final EnderDragonFight enderDragonFight, final List<EnderCrystalEntity> list, final int integer, final BlockPos blockPos) {
        }
    };
    
    public abstract void run(final ServerWorld arg1, final EnderDragonFight arg2, final List<EnderCrystalEntity> arg3, final int arg4, final BlockPos arg5);
}
