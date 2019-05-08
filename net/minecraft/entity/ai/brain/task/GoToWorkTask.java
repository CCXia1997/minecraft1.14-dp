package net.minecraft.entity.ai.brain.task;

import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;

public class GoToWorkTask extends Task<VillagerEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.c, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        return entity.getVillagerData().getProfession() == VillagerProfession.a;
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        final GlobalPos globalPos5 = entity.getBrain().<GlobalPos>getOptionalMemory(MemoryModuleType.c).get();
        final MinecraftServer minecraftServer6 = world.getServer();
        minecraftServer6.getWorld(globalPos5.getDimension()).getPointOfInterestStorage().getType(globalPos5.getPos()).ifPresent(pointOfInterestType -> Registry.VILLAGER_PROFESSION.stream().filter(villagerProfession -> villagerProfession.getWorkStation() == pointOfInterestType).findFirst().ifPresent(villagerProfession -> {
            entity.setVillagerData(entity.getVillagerData().withProfession(villagerProfession));
            entity.reinitializeBrain(world);
        }));
    }
}
