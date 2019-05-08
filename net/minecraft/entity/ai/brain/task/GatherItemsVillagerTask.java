package net.minecraft.entity.ai.brain.task;

import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;

public class GatherItemsVillagerTask extends Task<VillagerEntity>
{
    private Set<Item> items;
    
    public GatherItemsVillagerTask() {
        this.items = ImmutableSet.of();
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.m, MemoryModuleState.a), Pair.of(MemoryModuleType.g, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        return LookTargetUtil.canSee(entity.getBrain(), MemoryModuleType.m, EntityType.VILLAGER);
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.shouldRun(world, entity);
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        final VillagerEntity villagerEntity5 = entity.getBrain().<VillagerEntity>getOptionalMemory((MemoryModuleType<VillagerEntity>)MemoryModuleType.m).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(entity, villagerEntity5);
        this.items = getGatherableItems(entity, villagerEntity5);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final VillagerEntity villagerEntity5 = entity.getBrain().<VillagerEntity>getOptionalMemory((MemoryModuleType<VillagerEntity>)MemoryModuleType.m).get();
        if (entity.squaredDistanceTo(villagerEntity5) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(entity, villagerEntity5);
        entity.talkWithVillager(villagerEntity5, time);
        if (entity.wantsToStartBreeding() && villagerEntity5.canBreed()) {
            giveHalfOfStack(entity, VillagerEntity.ITEM_FOOD_VALUES.keySet(), villagerEntity5);
        }
        if (!this.items.isEmpty() && entity.getInventory().containsAnyInInv(this.items)) {
            giveHalfOfStack(entity, this.items, villagerEntity5);
        }
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        entity.getBrain().forget(MemoryModuleType.m);
    }
    
    private static Set<Item> getGatherableItems(final VillagerEntity villagerEntity1, final VillagerEntity villagerEntity2) {
        final ImmutableSet<Item> immutableSet3 = villagerEntity2.getVillagerData().getProfession().getGatherableItems();
        final ImmutableSet<Item> immutableSet4 = villagerEntity1.getVillagerData().getProfession().getGatherableItems();
        return immutableSet3.stream().filter(item -> !immutableSet4.contains(item)).collect(Collectors.toSet());
    }
    
    private static void giveHalfOfStack(final VillagerEntity villager, final Set<Item> validItems, final LivingEntity target) {
        final BasicInventory basicInventory4 = villager.getInventory();
        ItemStack itemStack5 = ItemStack.EMPTY;
        for (int integer6 = 0; integer6 < basicInventory4.getInvSize(); ++integer6) {
            final ItemStack itemStack6 = basicInventory4.getInvStack(integer6);
            if (!itemStack6.isEmpty()) {
                final Item item8 = itemStack6.getItem();
                if (validItems.contains(item8)) {
                    final int integer7 = itemStack6.getAmount() / 2;
                    itemStack6.subtractAmount(integer7);
                    itemStack5 = new ItemStack(item8, integer7);
                    break;
                }
            }
        }
        if (!itemStack5.isEmpty()) {
            LookTargetUtil.give(villager, itemStack5, target);
        }
    }
}
