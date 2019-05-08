package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.Entity;
import java.util.List;
import java.util.function.Predicate;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import net.minecraft.entity.EntityType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;

public class VillagerHostilesSensor extends Sensor<LivingEntity>
{
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER;
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.t);
    }
    
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        entity.getBrain().<LivingEntity>setMemory(MemoryModuleType.t, this.getNearestHostile(entity));
    }
    
    private Optional<LivingEntity> getNearestHostile(final LivingEntity entity) {
        return this.getVisibleMobs(entity).<LivingEntity>flatMap(list -> list.stream().filter(this::isHostile).filter(livingEntity2 -> this.isCloseEnoughForDanger(entity, livingEntity2)).min((livingEntity2, livingEntity3) -> this.compareDistances(entity, livingEntity2, livingEntity3)));
    }
    
    private Optional<List<LivingEntity>> getVisibleMobs(final LivingEntity entity) {
        return entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g);
    }
    
    private int compareDistances(final LivingEntity entity, final LivingEntity hostile1, final LivingEntity hostile2) {
        return (int)hostile1.squaredDistanceTo(entity) - (int)hostile2.squaredDistanceTo(entity);
    }
    
    private boolean isCloseEnoughForDanger(final LivingEntity entity, final LivingEntity hostile) {
        final float float3 = VillagerHostilesSensor.SQUARED_DISTANCES_FOR_DANGER.get(hostile.getType());
        return hostile.squaredDistanceTo(entity) <= float3 * float3;
    }
    
    private boolean isHostile(final LivingEntity entity) {
        return VillagerHostilesSensor.SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
    }
    
    static {
        SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<ZombieEntity>, Float>builder().put(EntityType.ZOMBIE, 8.0f).put((EntityType<ZombieEntity>)EntityType.EVOKER, 12.0f).put((EntityType<ZombieEntity>)EntityType.VINDICATOR, 10.0f).put((EntityType<ZombieEntity>)EntityType.VEX, 8.0f).put((EntityType<ZombieEntity>)EntityType.PILLAGER, 15.0f).put((EntityType<ZombieEntity>)EntityType.ILLUSIONER, 12.0f).put((EntityType<ZombieEntity>)EntityType.RAVAGER, 12.0f).put((EntityType<ZombieEntity>)EntityType.HUSK, 8.0f).build();
    }
}
