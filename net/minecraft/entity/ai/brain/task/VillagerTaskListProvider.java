package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.Entity;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.entity.ai.GoToNearbyPositionTask;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableList;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskListProvider
{
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(final VillagerProfession profession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new StayAboveWaterTask(0.4f, 0.8f)), Pair.of(0, new OpenDoorsTask()), Pair.of(0, new LookAroundTask(45, 90)), Pair.of(0, new PanicTask()), Pair.of(0, new WakeUpTask()), Pair.of(0, new HideWhenBellRingsTask()), Pair.of(0, new StartRaidTask()), Pair.of(1, new WanderAroundTask(200)), Pair.of(2, new FollowCustomerTask(float2)), Pair.of(10, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.c, true)), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.q, MemoryModuleType.b, false)), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.r, MemoryModuleType.d, true)), Pair.of(10, new GoToWorkTask()), Pair.of(10, new LoseJobOnSiteLossTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(final VillagerProfession profession, final float float2) {
        return ImmutableList.of(createBusyFollowTask(), (Pair<Integer, Task<LivingEntity>>)Pair.of(5, new RandomTask(ImmutableList.of(Pair.of((Object)new VillagerWorkTask(), (Object)7), Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.c, 4), (Object)2), Pair.of((Object)new GoToNearbyPositionTask(MemoryModuleType.c, 1, 10), (Object)5), Pair.of((Object)new GoToSecondaryPositionTask(MemoryModuleType.e, 0.4f, 1, 6, MemoryModuleType.c), (Object)5), Pair.of((Object)new FarmerVillagerTask(), (Object)5)))), (Pair<Integer, Task<LivingEntity>>)Pair.of(10, new HoldTradeOffersTask(400, 1600)), (Pair<Integer, Task<LivingEntity>>)Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)), (Pair<Integer, Task<LivingEntity>>)Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.c, float2, 9, 100)), (Pair<Integer, Task<LivingEntity>>)Pair.of(3, new GiveGiftsToHeroTask(100)), (Pair<Integer, Task<LivingEntity>>)Pair.of(3, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.c)), (Pair<Integer, Task<LivingEntity>>)Pair.of(99, new ScheduleActivityTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(final float float1) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new WanderAroundTask(100)), createFreeFollowTask(), Pair.of(5, new PlayWithVillagerBabiesTask()), Pair.of(5, new RandomTask(ImmutableSet.of(Pair.of((Object)MemoryModuleType.h, (Object)MemoryModuleState.b)), ImmutableList.of(Pair.of((Object)FindEntityTask.<LivingEntity>create(EntityType.VILLAGER, 8, MemoryModuleType.m, float1, 2), (Object)2), Pair.of((Object)FindEntityTask.<LivingEntity>create(EntityType.CAT, 8, MemoryModuleType.m, float1, 2), (Object)1), Pair.of((Object)new FindWalkTargetTask(float1), (Object)1), Pair.of((Object)new GoTowardsLookTarget(float1, 2), (Object)1), Pair.of((Object)new JumpInBedTask(float1), (Object)2), Pair.of((Object)new WaitTask(20, 40), (Object)2)))), Pair.of(99, new ScheduleActivityTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(final VillagerProfession profession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.b, float2, 1, 150)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.q, MemoryModuleType.b)), Pair.of(3, new SleepTask()), Pair.of(5, new RandomTask(ImmutableSet.of(Pair.of((Object)MemoryModuleType.b, (Object)MemoryModuleState.b)), ImmutableList.of(Pair.of((Object)new WalkHomeTask(float2), (Object)1), Pair.of((Object)new WanderIndoorsTask(float2), (Object)4), Pair.of((Object)new WaitTask(20, 40), (Object)2)))), createBusyFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(final VillagerProfession profession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.d, 40), (Object)2), Pair.of((Object)new MeetVillagerTask(), (Object)2)))), Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.d, float2, 6, 100)), Pair.of(3, new GiveGiftsToHeroTask(100)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.r, MemoryModuleType.d)), Pair.of(3, new CompositeTask(ImmutableSet.of(), ImmutableSet.of(MemoryModuleType.m), CompositeTask.Order.a, CompositeTask.RunMode.a, ImmutableList.of(Pair.of((Object)new GatherItemsVillagerTask(), (Object)1)))), createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(final VillagerProfession profession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of((Object)FindEntityTask.<LivingEntity>create(EntityType.VILLAGER, 8, MemoryModuleType.m, float2, 2), (Object)2), Pair.of((Object)new FindEntityTask(EntityType.VILLAGER, 8, VillagerEntity::isReadyToBreed, VillagerEntity::isReadyToBreed, (MemoryModuleType<LivingEntity>)MemoryModuleType.n, float2, 2), (Object)1), Pair.of((Object)FindEntityTask.<LivingEntity>create(EntityType.CAT, 8, MemoryModuleType.m, float2, 2), (Object)1), Pair.of((Object)new FindWalkTargetTask(float2), (Object)1), Pair.of((Object)new GoTowardsLookTarget(float2, 2), (Object)1), Pair.of((Object)new JumpInBedTask(float2), (Object)1), Pair.of((Object)new WaitTask(30, 60), (Object)1)))), Pair.of(3, new GiveGiftsToHeroTask(100)), Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(3, new HoldTradeOffersTask(400, 1600)), Pair.of(3, new CompositeTask(ImmutableSet.of(), ImmutableSet.of(MemoryModuleType.m), CompositeTask.Order.a, CompositeTask.RunMode.a, ImmutableList.of(Pair.of((Object)new GatherItemsVillagerTask(), (Object)1)))), Pair.of(3, new CompositeTask(ImmutableSet.of(), ImmutableSet.of(MemoryModuleType.n), CompositeTask.Order.a, CompositeTask.RunMode.a, ImmutableList.of(Pair.of((Object)new VillagerBreedTask(), (Object)1)))), createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(final VillagerProfession profession, final float float2) {
        final float float3 = float2 * 1.5f;
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new StopPanicingTask()), Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.t, float3)), Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.s, float3)), Pair.of(3, new FindWalkTargetTask(float3)), createBusyFollowTask());
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPreRaidTasks(final VillagerProfession villagerProfession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new RingBellTask()), Pair.of(0, new RandomTask(ImmutableList.of(Pair.of((Object)new VillagerWalkTowardsTask(MemoryModuleType.d, float2 * 1.5f, 3, 150), (Object)6), Pair.of((Object)new FindWalkTargetTask(float2 * 1.5f), (Object)2)))), createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRaidTasks(final VillagerProfession villagerProfession, final float float2) {
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new RandomTask(ImmutableList.of(Pair.of((Object)new SeekSkyAfterRaidWinTask(float2), (Object)5), Pair.of((Object)new RunAroundAfterRaidTask(float2 * 1.1f), (Object)2)))), Pair.of(0, new CelebrateRaidWinTask(600, 600)), Pair.of(2, new HideInHomeDuringRaidTask(24, float2 * 1.4f)), createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    }
    
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createHideTasks(final VillagerProfession villagerProfession, final float float2) {
        final int integer3 = 2;
        return ImmutableList.<Pair<Integer, ? extends Task<? super VillagerEntity>>>of(Pair.of(0, new ForgetBellRingTask(15, 2)), Pair.of(1, new HideInHomeTask(32, float2 * 1.25f, 2)), createBusyFollowTask());
    }
    
    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return (Pair<Integer, Task<LivingEntity>>)Pair.of(5, new RandomTask(ImmutableList.of(Pair.of((Object)new FollowMobTask(EntityType.CAT, 8.0f), (Object)8), Pair.of((Object)new FollowMobTask(EntityType.VILLAGER, 8.0f), (Object)2), Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)2), Pair.of((Object)new FollowMobTask(EntityCategory.b, 8.0f), (Object)1), Pair.of((Object)new FollowMobTask(EntityCategory.d, 8.0f), (Object)1), Pair.of((Object)new FollowMobTask(EntityCategory.a, 8.0f), (Object)1), Pair.of((Object)new WaitTask(30, 60), (Object)2))));
    }
    
    private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
        return (Pair<Integer, Task<LivingEntity>>)Pair.of(5, new RandomTask(ImmutableList.of(Pair.of((Object)new FollowMobTask(EntityType.VILLAGER, 8.0f), (Object)2), Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)2), Pair.of((Object)new WaitTask(30, 60), (Object)8))));
    }
}
