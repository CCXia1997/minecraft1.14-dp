package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.EntityPosWrapper;
import java.util.Iterator;
import net.minecraft.village.TradeOffer;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.passive.VillagerEntity;

public class HoldTradeOffersTask extends Task<VillagerEntity>
{
    @Nullable
    private ItemStack a;
    private final List<ItemStack> offers;
    private int c;
    private int d;
    private int e;
    
    public HoldTradeOffersTask(final int minRunTime, final int maxRunTime) {
        super(minRunTime, maxRunTime);
        this.offers = Lists.newArrayList();
    }
    
    public boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        final Brain<?> brain3 = entity.getBrain();
        if (!brain3.<LivingEntity>getOptionalMemory(MemoryModuleType.m).isPresent()) {
            return false;
        }
        final LivingEntity livingEntity4 = brain3.<LivingEntity>getOptionalMemory(MemoryModuleType.m).get();
        return livingEntity4.getType() == EntityType.PLAYER && entity.isAlive() && livingEntity4.isAlive() && !entity.isChild() && entity.squaredDistanceTo(livingEntity4) <= 17.0;
    }
    
    public boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.shouldRun(world, entity) && this.e > 0 && entity.getBrain().<LivingEntity>getOptionalMemory(MemoryModuleType.m).isPresent();
    }
    
    public void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        super.run(world, entity, time);
        this.c(entity);
        this.c = 0;
        this.d = 0;
        this.e = 40;
    }
    
    public void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final LivingEntity livingEntity5 = this.c(entity);
        this.a(livingEntity5, entity);
        if (!this.offers.isEmpty()) {
            this.d(entity);
        }
        else {
            entity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
            this.e = Math.min(this.e, 40);
        }
        --this.e;
    }
    
    public void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        super.finishRunning(world, entity, time);
        entity.getBrain().forget(MemoryModuleType.m);
        entity.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
        this.a = null;
    }
    
    public Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.m, MemoryModuleState.a));
    }
    
    private void a(final LivingEntity livingEntity, final VillagerEntity villagerEntity) {
        boolean boolean3 = false;
        final ItemStack itemStack4 = livingEntity.getMainHandStack();
        if (this.a == null || !ItemStack.areEqualIgnoreTags(this.a, itemStack4)) {
            this.a = itemStack4;
            boolean3 = true;
            this.offers.clear();
        }
        if (boolean3 && !this.a.isEmpty()) {
            this.b(villagerEntity);
            if (!this.offers.isEmpty()) {
                this.e = 900;
                this.a(villagerEntity);
            }
        }
    }
    
    private void a(final VillagerEntity villagerEntity) {
        villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, this.offers.get(0));
    }
    
    private void b(final VillagerEntity villagerEntity) {
        for (final TradeOffer tradeOffer3 : villagerEntity.getOffers()) {
            if (!tradeOffer3.isDisabled() && this.a(tradeOffer3)) {
                this.offers.add(tradeOffer3.getMutableSellItem());
            }
        }
    }
    
    private boolean a(final TradeOffer tradeOffer) {
        return ItemStack.areEqualIgnoreTags(this.a, tradeOffer.getAdjustedFirstBuyItem()) || ItemStack.areEqualIgnoreTags(this.a, tradeOffer.getSecondBuyItem());
    }
    
    private LivingEntity c(final VillagerEntity villagerEntity) {
        final Brain<?> brain2 = villagerEntity.getBrain();
        final LivingEntity livingEntity3 = brain2.<LivingEntity>getOptionalMemory(MemoryModuleType.m).get();
        brain2.<EntityPosWrapper>putMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.l, new EntityPosWrapper(livingEntity3));
        return livingEntity3;
    }
    
    private void d(final VillagerEntity villagerEntity) {
        if (this.offers.size() >= 2 && ++this.c >= 40) {
            ++this.d;
            this.c = 0;
            if (this.d > this.offers.size() - 1) {
                this.d = 0;
            }
            villagerEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, this.offers.get(this.d));
        }
    }
}
