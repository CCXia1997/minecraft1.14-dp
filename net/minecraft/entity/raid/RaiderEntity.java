package net.minecraft.entity.raid;

import java.util.Objects;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import java.util.Optional;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.util.math.Position;
import java.util.List;
import java.util.EnumSet;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.entity.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.PatrolEntity;

public abstract class RaiderEntity extends PatrolEntity
{
    protected static final TrackedData<Boolean> CELEBRATING;
    private static final Predicate<ItemEntity> OBTAINABLE_OMINOUS_BANNER_PREDICATE;
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean ableToJoinRaid;
    private int outOfRaidCounter;
    
    protected RaiderEntity(final EntityType<? extends RaiderEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new PickupBannerAsLeaderGoal<>(this));
        this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
        this.goalSelector.add(4, new AttackHomeGoal(this, 1.0499999523162842, 1));
        this.goalSelector.add(5, new CelebrateGoal(this));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(RaiderEntity.CELEBRATING, false);
    }
    
    public abstract void addBonusForWave(final int arg1, final boolean arg2);
    
    public boolean canJoinRaid() {
        return this.ableToJoinRaid;
    }
    
    public void setAbleToJoinRaid(final boolean ableToJoinRaid) {
        this.ableToJoinRaid = ableToJoinRaid;
    }
    
    @Override
    public void updateState() {
        if (this.world instanceof ServerWorld && this.isAlive()) {
            final Raid raid1 = this.getRaid();
            if (this.canJoinRaid()) {
                if (raid1 == null) {
                    if (this.world.getTime() % 20L == 0L) {
                        final Raid raid2 = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
                        if (raid2 != null && RaidManager.isValidRaiderFor(this, raid2)) {
                            raid2.addRaider(raid2.getGroupsSpawned(), this, null, true);
                        }
                    }
                }
                else {
                    final LivingEntity livingEntity2 = this.getTarget();
                    if (livingEntity2 != null && (livingEntity2.getType() == EntityType.PLAYER || livingEntity2.getType() == EntityType.IRON_GOLEM)) {
                        this.despawnCounter = 0;
                    }
                }
            }
        }
        super.updateState();
    }
    
    @Override
    protected void updateDespawnCounter() {
        this.despawnCounter += 2;
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        if (this.world instanceof ServerWorld) {
            final Entity entity2 = damageSource.getAttacker();
            if (this.getRaid() != null) {
                if (this.isPatrolLeader()) {
                    this.getRaid().removeLeader(this.getWave());
                }
                if (entity2 != null && entity2.getType() == EntityType.PLAYER) {
                    this.getRaid().addHero(entity2);
                }
                this.getRaid().removeFromWave(this, false);
            }
            if (this.isPatrolLeader() && this.getRaid() == null && ((ServerWorld)this.world).getRaidAt(new BlockPos(this)) == null) {
                final ItemStack itemStack3 = this.getEquippedStack(EquipmentSlot.HEAD);
                PlayerEntity playerEntity4 = null;
                final Entity entity3 = entity2;
                if (entity3 instanceof PlayerEntity) {
                    playerEntity4 = (PlayerEntity)entity3;
                }
                else if (entity3 instanceof WolfEntity) {
                    final WolfEntity wolfEntity6 = (WolfEntity)entity3;
                    final LivingEntity livingEntity7 = wolfEntity6.getOwner();
                    if (wolfEntity6.isTamed() && livingEntity7 instanceof PlayerEntity) {
                        playerEntity4 = (PlayerEntity)livingEntity7;
                    }
                }
                if (!itemStack3.isEmpty() && ItemStack.areEqual(itemStack3, Raid.OMINOUS_BANNER) && playerEntity4 != null) {
                    final StatusEffectInstance statusEffectInstance6 = playerEntity4.getStatusEffect(StatusEffects.E);
                    int integer7 = 1;
                    if (statusEffectInstance6 != null) {
                        integer7 += statusEffectInstance6.getAmplifier();
                        playerEntity4.removePotionEffect(StatusEffects.E);
                    }
                    else {
                        --integer7;
                    }
                    integer7 = MathHelper.clamp(integer7, 0, 5);
                    final StatusEffectInstance statusEffectInstance7 = new StatusEffectInstance(StatusEffects.E, 120000, integer7, false, false, true);
                    playerEntity4.addPotionEffect(statusEffectInstance7);
                }
            }
        }
        super.onDeath(damageSource);
    }
    
    @Override
    public boolean hasNoRaid() {
        return !this.hasActiveRaid();
    }
    
    public void setRaid(@Nullable final Raid raid) {
        this.raid = raid;
    }
    
    @Nullable
    public Raid getRaid() {
        return this.raid;
    }
    
    public boolean hasActiveRaid() {
        return this.getRaid() != null && this.getRaid().isActive();
    }
    
    public void setWave(final int integer) {
        this.wave = integer;
    }
    
    public int getWave() {
        return this.wave;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isCelebrating() {
        return this.dataTracker.<Boolean>get(RaiderEntity.CELEBRATING);
    }
    
    public void setCelebrating(final boolean boolean1) {
        this.dataTracker.<Boolean>set(RaiderEntity.CELEBRATING, boolean1);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Wave", this.wave);
        tag.putBoolean("CanJoinRaid", this.ableToJoinRaid);
        if (this.raid != null) {
            tag.putInt("RaidId", this.raid.getRaidId());
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.wave = tag.getInt("Wave");
        this.ableToJoinRaid = tag.getBoolean("CanJoinRaid");
        if (tag.containsKey("RaidId", 3)) {
            if (this.world instanceof ServerWorld) {
                this.raid = ((ServerWorld)this.world).getRaidManager().getRaid(tag.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addToWave(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setWaveCaptain(this.wave, this);
                }
            }
        }
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        final ItemStack itemStack2 = item.getStack();
        final boolean boolean3 = this.hasActiveRaid() && this.getRaid().getCaptain(this.getWave()) != null;
        if (this.hasActiveRaid() && !boolean3 && ItemStack.areEqual(itemStack2, Raid.OMINOUS_BANNER)) {
            final EquipmentSlot equipmentSlot4 = EquipmentSlot.HEAD;
            final ItemStack itemStack3 = this.getEquippedStack(equipmentSlot4);
            final double double6 = this.getDropChance(equipmentSlot4);
            if (!itemStack3.isEmpty() && this.random.nextFloat() - 0.1f < double6) {
                this.dropStack(itemStack3);
            }
            this.setEquippedStack(equipmentSlot4, itemStack2);
            this.sendPickup(item, itemStack2.getAmount());
            item.remove();
            this.getRaid().setWaveCaptain(this.getWave(), this);
            this.setPatrolLeader(true);
        }
        else {
            super.loot(item);
        }
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return this.getRaid() == null && super.canImmediatelyDespawn(distanceSquared);
    }
    
    public boolean cannotDespawn() {
        return this.getRaid() != null;
    }
    
    public int getOutOfRaidCounter() {
        return this.outOfRaidCounter;
    }
    
    public void setOutOfRaidCounter(final int integer) {
        this.outOfRaidCounter = integer;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.hasActiveRaid()) {
            this.getRaid().updateBar();
        }
        return super.damage(source, amount);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || difficulty != SpawnType.a);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    public abstract SoundEvent getCelebratingSound();
    
    static {
        CELEBRATING = DataTracker.<Boolean>registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        OBTAINABLE_OMINOUS_BANNER_PREDICATE = (itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive() && ItemStack.areEqual(itemEntity.getStack(), Raid.OMINOUS_BANNER));
    }
    
    public class PickupBannerAsLeaderGoal<T extends RaiderEntity> extends Goal
    {
        private final T b;
        
        public PickupBannerAsLeaderGoal(final T raiderEntity2) {
            this.b = raiderEntity2;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            final Raid raid1 = this.b.getRaid();
            if (!this.b.hasActiveRaid() || this.b.getRaid().isFinished() || !this.b.canLead() || ItemStack.areEqual(this.b.getEquippedStack(EquipmentSlot.HEAD), Raid.OMINOUS_BANNER)) {
                return false;
            }
            final RaiderEntity raiderEntity2 = raid1.getCaptain(this.b.getWave());
            if (raiderEntity2 == null || !raiderEntity2.isAlive()) {
                final List<ItemEntity> list3 = this.b.world.<ItemEntity>getEntities(ItemEntity.class, this.b.getBoundingBox().expand(16.0, 8.0, 16.0), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE);
                if (!list3.isEmpty()) {
                    return this.b.getNavigation().startMovingTo(list3.get(0), 1.149999976158142);
                }
            }
            return false;
        }
        
        @Override
        public void tick() {
            if (this.b.getNavigation().getTargetPos().isWithinDistance(this.b.getPos(), 1.414)) {
                final List<ItemEntity> list1 = this.b.world.<ItemEntity>getEntities(ItemEntity.class, this.b.getBoundingBox().expand(4.0, 4.0, 4.0), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE);
                if (!list1.isEmpty()) {
                    this.b.loot(list1.get(0));
                }
            }
        }
    }
    
    public class CelebrateGoal extends Goal
    {
        private final RaiderEntity b;
        
        CelebrateGoal(final RaiderEntity raiderEntity2) {
            this.b = raiderEntity2;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            final Raid raid1 = this.b.getRaid();
            return this.b.isAlive() && this.b.getTarget() == null && raid1 != null && raid1.hasLost();
        }
        
        @Override
        public void start() {
            this.b.setCelebrating(true);
            super.start();
        }
        
        @Override
        public void stop() {
            this.b.setCelebrating(false);
            super.stop();
        }
        
        @Override
        public void tick() {
            if (!this.b.isSilent() && this.b.random.nextInt(100) == 0) {
                RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound(), LivingEntity.this.getSoundVolume(), LivingEntity.this.getSoundPitch());
            }
            if (!this.b.hasVehicle() && this.b.random.nextInt(50) == 0) {
                this.b.getJumpControl().setActive();
            }
            super.tick();
        }
    }
    
    public class PatrolApproachGoal extends Goal
    {
        private final RaiderEntity raiderEntity;
        private final float squaredDistance;
        public final TargetPredicate closeRaiderPredicate;
        
        public PatrolApproachGoal(final IllagerEntity raiderEntity, final float distance) {
            this.closeRaiderPredicate = new TargetPredicate().setBaseMaxDistance(8.0).ignoreEntityTargetRules().includeInvulnerable().includeTeammates().includeHidden().ignoreDistanceScalingFactor();
            this.raiderEntity = raiderEntity;
            this.squaredDistance = distance * distance;
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = this.raiderEntity.getAttacker();
            return this.raiderEntity.getRaid() == null && this.raiderEntity.isRaidCenterSet() && this.raiderEntity.getTarget() != null && !this.raiderEntity.isAttacking() && (livingEntity1 == null || livingEntity1.getType() != EntityType.PLAYER);
        }
        
        @Override
        public void start() {
            super.start();
            this.raiderEntity.getNavigation().stop();
            final List<RaiderEntity> list1 = this.raiderEntity.world.<RaiderEntity>getTargets(RaiderEntity.class, this.closeRaiderPredicate, (LivingEntity)this.raiderEntity, this.raiderEntity.getBoundingBox().expand(8.0, 8.0, 8.0));
            for (final RaiderEntity raiderEntity3 : list1) {
                raiderEntity3.setTarget(this.raiderEntity.getTarget());
            }
        }
        
        @Override
        public void stop() {
            super.stop();
            final LivingEntity livingEntity1 = this.raiderEntity.getTarget();
            if (livingEntity1 != null) {
                final List<RaiderEntity> list2 = this.raiderEntity.world.<RaiderEntity>getTargets(RaiderEntity.class, this.closeRaiderPredicate, (LivingEntity)this.raiderEntity, this.raiderEntity.getBoundingBox().expand(8.0, 8.0, 8.0));
                for (final RaiderEntity raiderEntity4 : list2) {
                    raiderEntity4.setTarget(livingEntity1);
                    raiderEntity4.setAttacking(true);
                }
                this.raiderEntity.setAttacking(true);
            }
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = this.raiderEntity.getTarget();
            if (livingEntity1 == null) {
                return;
            }
            if (this.raiderEntity.squaredDistanceTo(livingEntity1) > this.squaredDistance) {
                this.raiderEntity.getLookControl().lookAt(livingEntity1, 30.0f, 30.0f);
                if (this.raiderEntity.random.nextInt(50) == 0) {
                    this.raiderEntity.playAmbientSound();
                }
            }
            else {
                this.raiderEntity.setAttacking(true);
            }
            super.tick();
        }
    }
    
    static class AttackHomeGoal extends Goal
    {
        private final RaiderEntity owner;
        private final double speed;
        private BlockPos home;
        private final List<BlockPos> lastHomes;
        private final int distance;
        private boolean finished;
        
        public AttackHomeGoal(final RaiderEntity raiderEntity, final double speed, final int distance) {
            this.lastHomes = Lists.newArrayList();
            this.owner = raiderEntity;
            this.speed = speed;
            this.distance = distance;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            this.purgeMemory();
            return this.isRaiding() && this.tryFindHome() && this.owner.getTarget() == null;
        }
        
        private boolean isRaiding() {
            return this.owner.hasActiveRaid() && !this.owner.getRaid().isFinished();
        }
        
        private boolean tryFindHome() {
            final ServerWorld serverWorld1 = (ServerWorld)this.owner.world;
            final BlockPos blockPos2 = new BlockPos(this.owner);
            final Optional<BlockPos> optional3 = serverWorld1.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.q, this::canLootHome, PointOfInterestStorage.OccupationStatus.ANY, blockPos2, 48, this.owner.random);
            if (!optional3.isPresent()) {
                return false;
            }
            this.home = optional3.get().toImmutable();
            return true;
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.owner.getNavigation().isIdle() && this.owner.getTarget() == null && !this.home.isWithinDistance(this.owner.getPos(), this.owner.getWidth() + this.distance) && !this.finished;
        }
        
        @Override
        public void stop() {
            if (this.home.isWithinDistance(this.owner.getPos(), this.distance)) {
                this.lastHomes.add(this.home);
            }
        }
        
        @Override
        public void start() {
            super.start();
            this.owner.setDespawnCounter(0);
            this.owner.getNavigation().startMovingTo(this.home.getX(), this.home.getY(), this.home.getZ(), this.speed);
            this.finished = false;
        }
        
        @Override
        public void tick() {
            if (this.owner.getNavigation().isIdle()) {
                final int integer1 = this.home.getX();
                final int integer2 = this.home.getY();
                final int integer3 = this.home.getZ();
                Vec3d vec3d4 = PathfindingUtil.a(this.owner, 16, 7, new Vec3d(integer1, integer2, integer3), 0.3141592741012573);
                if (vec3d4 == null) {
                    vec3d4 = PathfindingUtil.a(this.owner, 8, 7, new Vec3d(integer1, integer2, integer3));
                }
                if (vec3d4 == null) {
                    this.finished = true;
                    return;
                }
                this.owner.getNavigation().startMovingTo(vec3d4.x, vec3d4.y, vec3d4.z, this.speed);
            }
        }
        
        private boolean canLootHome(final BlockPos blockPos) {
            for (final BlockPos blockPos2 : this.lastHomes) {
                if (Objects.equals(blockPos, blockPos2)) {
                    return false;
                }
            }
            return true;
        }
        
        private void purgeMemory() {
            if (this.lastHomes.size() > 2) {
                this.lastHomes.remove(0);
            }
        }
    }
}
