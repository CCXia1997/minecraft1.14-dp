package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import net.minecraft.server.world.ServerWorld;
import java.util.List;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class VillagerBabiesSensor extends Sensor<LivingEntity>
{
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.h);
    }
    
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        entity.getBrain().<List<LivingEntity>>putMemory(MemoryModuleType.h, this.getVisibleVillagerBabies(entity));
    }
    
    private List<LivingEntity> getVisibleVillagerBabies(final LivingEntity entities) {
        return this.getVisibleMobs(entities).stream().filter(this::isVillagerBaby).collect(Collectors.toList());
    }
    
    private boolean isVillagerBaby(final LivingEntity entity) {
        return entity.getType() == EntityType.VILLAGER && entity.isChild();
    }
    
    private List<LivingEntity> getVisibleMobs(final LivingEntity entity) {
        return entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).orElse(Lists.newArrayList());
    }
}
