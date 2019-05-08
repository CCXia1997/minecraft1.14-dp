package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntityWithAi;

public class GoToNearbyEntityTask extends Task<MobEntityWithAi>
{
    private final MemoryModuleType<? extends Entity> entityMemory;
    private final float b;
    
    public GoToNearbyEntityTask(final MemoryModuleType<? extends Entity> memoryModuleType, final float float2) {
        this.entityMemory = memoryModuleType;
        this.b = float2;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        final Entity entity2 = (Entity)entity.getBrain().getOptionalMemory(this.entityMemory).get();
        return entity.squaredDistanceTo(entity2) < 16.0;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(this.entityMemory, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        final Entity entity2 = (Entity)entity.getBrain().getOptionalMemory(this.entityMemory).get();
        a(entity, entity2, this.b);
    }
    
    public static void a(final MobEntityWithAi mobEntityWithAi, final Entity entity, final float float3) {
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final Vec3d vec3d5 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
            final Vec3d vec3d6 = new Vec3d(entity.x, entity.y, entity.z);
            final Vec3d vec3d7 = vec3d5.subtract(vec3d6).normalize();
            final Vec3d vec3d8 = PathfindingUtil.a(mobEntityWithAi, 16, 7, vec3d7, 0.3141592741012573);
            if (vec3d8 != null) {
                mobEntityWithAi.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(vec3d8, float3, 0));
                return;
            }
        }
    }
}
