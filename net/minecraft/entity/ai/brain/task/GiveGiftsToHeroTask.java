package net.minecraft.entity.ai.brain.task;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.world.loot.LootTables;
import java.util.HashMap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.effect.StatusEffects;
import java.util.function.Predicate;
import java.util.Optional;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import java.util.Map;
import net.minecraft.entity.passive.VillagerEntity;

public class GiveGiftsToHeroTask extends Task<VillagerEntity>
{
    private static final Map<VillagerProfession, Identifier> GIFTS;
    private int ticksLeft;
    private boolean done;
    private long startTime;
    
    public GiveGiftsToHeroTask(final int exactRunTime) {
        super(exactRunTime);
        this.ticksLeft = 600;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.m, MemoryModuleState.c), Pair.of(MemoryModuleType.j, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        if (!this.isNearestPlayerHero(entity)) {
            return false;
        }
        if (this.ticksLeft > 0) {
            --this.ticksLeft;
            return false;
        }
        return true;
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        this.done = false;
        this.startTime = time;
        final PlayerEntity playerEntity5 = this.getNearestPlayerIfHero(entity).get();
        entity.getBrain().<LivingEntity>putMemory(MemoryModuleType.m, playerEntity5);
        LookTargetUtil.lookAt(entity, playerEntity5);
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.isNearestPlayerHero(entity) && !this.done;
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final PlayerEntity playerEntity5 = this.getNearestPlayerIfHero(entity).get();
        LookTargetUtil.lookAt(entity, playerEntity5);
        if (this.isCloseEnough(entity, playerEntity5)) {
            if (time - this.startTime > 20L) {
                this.giveGifts(entity, playerEntity5);
                this.done = true;
            }
        }
        else {
            LookTargetUtil.walkTowards(entity, playerEntity5, 5);
        }
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        this.ticksLeft = getNextGiftDelay(world);
        entity.getBrain().forget(MemoryModuleType.m);
        entity.getBrain().forget(MemoryModuleType.k);
        entity.getBrain().forget(MemoryModuleType.l);
    }
    
    private void giveGifts(final VillagerEntity villager, final LivingEntity recipient) {
        final List<ItemStack> list3 = this.getGifts(villager);
        for (final ItemStack itemStack5 : list3) {
            LookTargetUtil.give(villager, itemStack5, recipient);
        }
    }
    
    private List<ItemStack> getGifts(final VillagerEntity villager) {
        if (villager.isChild()) {
            return ImmutableList.<ItemStack>of(new ItemStack(Items.POPPY));
        }
        final VillagerProfession villagerProfession2 = villager.getVillagerData().getProfession();
        if (GiveGiftsToHeroTask.GIFTS.containsKey(villagerProfession2)) {
            final LootSupplier lootSupplier3 = villager.world.getServer().getLootManager().getSupplier(GiveGiftsToHeroTask.GIFTS.get(villagerProfession2));
            final LootContext.Builder builder4 = new LootContext.Builder((ServerWorld)villager.world).<BlockPos>put(LootContextParameters.f, new BlockPos(villager)).<Entity>put(LootContextParameters.a, villager).setRandom(villager.getRand());
            return lootSupplier3.getDrops(builder4.build(LootContextTypes.GIFT));
        }
        return ImmutableList.<ItemStack>of(new ItemStack(Items.jO));
    }
    
    private boolean isNearestPlayerHero(final VillagerEntity villager) {
        return this.getNearestPlayerIfHero(villager).isPresent();
    }
    
    private Optional<PlayerEntity> getNearestPlayerIfHero(final VillagerEntity villager) {
        return villager.getBrain().<PlayerEntity>getOptionalMemory(MemoryModuleType.j).filter(this::isHero);
    }
    
    private boolean isHero(final PlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.F);
    }
    
    private boolean isCloseEnough(final VillagerEntity villager, final PlayerEntity player) {
        final BlockPos blockPos3 = new BlockPos(player);
        final BlockPos blockPos4 = new BlockPos(villager);
        return blockPos4.isWithinDistance(blockPos3, 5.0);
    }
    
    private static int getNextGiftDelay(final ServerWorld world) {
        return 600 + world.random.nextInt(6001);
    }
    
    static {
        GIFTS = SystemUtil.<Map<VillagerProfession, Identifier>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(VillagerProfession.b, LootTables.ag);
            hashMap.put(VillagerProfession.c, LootTables.ah);
            hashMap.put(VillagerProfession.d, LootTables.ai);
            hashMap.put(VillagerProfession.e, LootTables.aj);
            hashMap.put(VillagerProfession.f, LootTables.ak);
            hashMap.put(VillagerProfession.g, LootTables.al);
            hashMap.put(VillagerProfession.h, LootTables.am);
            hashMap.put(VillagerProfession.i, LootTables.an);
            hashMap.put(VillagerProfession.j, LootTables.ao);
            hashMap.put(VillagerProfession.k, LootTables.ap);
            hashMap.put(VillagerProfession.m, LootTables.aq);
            hashMap.put(VillagerProfession.n, LootTables.ar);
            hashMap.put(VillagerProfession.o, LootTables.as);
        });
    }
}
