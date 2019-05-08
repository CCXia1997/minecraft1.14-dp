package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;

public class LoseJobOnSiteLossTask extends Task<VillagerEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.c, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        final VillagerData villagerData3 = entity.getVillagerData();
        return villagerData3.getProfession() != VillagerProfession.a && villagerData3.getProfession() != VillagerProfession.l && entity.getExperience() == 0 && villagerData3.getLevel() <= 1;
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        entity.setVillagerData(entity.getVillagerData().withProfession(VillagerProfession.a));
        entity.reinitializeBrain(world);
    }
}
