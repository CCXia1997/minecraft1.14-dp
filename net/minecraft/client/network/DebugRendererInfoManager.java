package net.minecraft.client.network;

import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Logger;

public class DebugRendererInfoManager
{
    private static final Logger LOGGER;
    
    public static void a(final ServerWorld serverWorld, final ChunkPos chunkPos) {
    }
    
    public static void a(final ServerWorld serverWorld, final BlockPos blockPos) {
    }
    
    public static void b(final ServerWorld serverWorld, final BlockPos blockPos) {
    }
    
    public static void sendPointOfInterest(final ServerWorld serverWorld, final BlockPos blockPos) {
    }
    
    public static void sendPathfindingData(final World world, final MobEntity mobEntity, @Nullable final Path path, final float float4) {
    }
    
    public static void sendBlockUpdate(final World world, final BlockPos blockPos) {
    }
    
    public static void sendStructureStart(final IWorld iWorld, final StructureStart structureStart) {
    }
    
    public static void sendGoalSelector(final World world, final MobEntity mob, final GoalSelector goalSelector) {
    }
    
    public static void sendVillagerAiDebugData(final LivingEntity livingEntity) {
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
