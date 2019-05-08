package net.minecraft.entity.passive;

import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import java.util.EnumSet;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.Item;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.entity.Entity;
import net.minecraft.village.TradeOffers;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoToEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.HoldInHandsGoal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class WanderingTraderEntity extends AbstractTraderEntity
{
    @Nullable
    private BlockPos wanderTarget;
    private int despawnDelay;
    
    public WanderingTraderEntity(final EntityType<? extends WanderingTraderEntity> type, final World world) {
        super(type, world);
        this.teleporting = true;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new HoldInHandsGoal<>(this, PotionUtil.setPotion(new ItemStack(Items.ml), Potions.h), SoundEvents.mV, wanderingTraderEntity -> !this.world.isDaylight() && !wanderingTraderEntity.isInvisible()));
        this.goalSelector.add(0, new HoldInHandsGoal<>(this, new ItemStack(Items.kG), SoundEvents.na, wanderingTraderEntity -> this.world.isDaylight() && wanderingTraderEntity.isInvisible()));
        this.goalSelector.add(1, new StopFollowingCustomerGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, ZombieEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, EvokerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VindicatorEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VexEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PillagerEntity.class, 15.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, IllusionerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
        this.goalSelector.add(1, new LookAtCustomerGoal(this));
        this.goalSelector.add(2, new WanderToTargetGoal(this, 2.0, 0.35));
        this.goalSelector.add(4, new GoToWalkTargetGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35));
        this.goalSelector.add(9, new GoToEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return null;
    }
    
    @Override
    public boolean isLevelledTrader() {
        return false;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final boolean boolean4 = itemStack3.getItem() == Items.or;
        if (boolean4) {
            itemStack3.interactWithEntity(player, this, hand);
            return true;
        }
        if (itemStack3.getItem() == Items.nr || !this.isAlive() || this.hasCustomer() || this.isChild()) {
            return super.interactMob(player, hand);
        }
        if (hand == Hand.a) {
            player.incrementStat(Stats.Q);
        }
        if (this.getOffers().isEmpty()) {
            return super.interactMob(player, hand);
        }
        if (!this.world.isClient) {
            this.setCurrentCustomer(player);
            this.sendOffers(player, this.getDisplayName(), 1);
        }
        return true;
    }
    
    @Override
    protected void fillRecipes() {
        final TradeOffers.Factory[] arr1 = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(1);
        final TradeOffers.Factory[] arr2 = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(2);
        if (arr1 == null || arr2 == null) {
            return;
        }
        final TraderOfferList traderOfferList3 = this.getOffers();
        this.fillRecipesFromPool(traderOfferList3, arr1, 5);
        final int integer4 = this.random.nextInt(arr2.length);
        final TradeOffers.Factory factory5 = arr2[integer4];
        final TradeOffer tradeOffer6 = factory5.create(this, this.random);
        if (tradeOffer6 != null) {
            traderOfferList3.add(tradeOffer6);
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            tag.put("WanderTarget", TagHelper.serializeBlockPos(this.wanderTarget));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("DespawnDelay", 99)) {
            this.despawnDelay = tag.getInt("DespawnDelay");
        }
        if (tag.containsKey("WanderTarget")) {
            this.wanderTarget = TagHelper.deserializeBlockPos(tag.getCompound("WanderTarget"));
        }
        this.setBreedingAge(Math.max(0, this.getBreedingAge()));
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return false;
    }
    
    @Override
    protected void afterUsing(final TradeOffer tradeOffer) {
        if (tradeOffer.shouldRewardPlayerExperience()) {
            final int integer2 = 3 + this.random.nextInt(4);
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, integer2));
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasCustomer()) {
            return SoundEvents.nb;
        }
        return SoundEvents.mT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.mY;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.mU;
    }
    
    @Override
    protected SoundEvent getDrinkSound(final ItemStack itemStack) {
        final Item item2 = itemStack.getItem();
        if (item2 == Items.kG) {
            return SoundEvents.mW;
        }
        return SoundEvents.mX;
    }
    
    @Override
    protected SoundEvent getTradingSound(final boolean sold) {
        return sold ? SoundEvents.nc : SoundEvents.mZ;
    }
    
    @Override
    protected SoundEvent getYesSound() {
        return SoundEvents.nc;
    }
    
    public void setDespawnDelay(final int integer) {
        this.despawnDelay = integer;
    }
    
    public int getDespawnDelay() {
        return this.despawnDelay;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
            this.remove();
        }
    }
    
    public void setWanderTarget(@Nullable final BlockPos blockPos) {
        this.wanderTarget = blockPos;
    }
    
    @Nullable
    private BlockPos getWanderTarget() {
        return this.wanderTarget;
    }
    
    class WanderToTargetGoal extends Goal
    {
        final WanderingTraderEntity a;
        final double proximityDistance;
        final double speed;
        
        WanderToTargetGoal(final WanderingTraderEntity wanderingTraderEntity2, final double double3, final double double5) {
            this.a = wanderingTraderEntity2;
            this.proximityDistance = double3;
            this.speed = double5;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public void stop() {
            this.a.setWanderTarget(null);
            WanderingTraderEntity.this.navigation.stop();
        }
        
        @Override
        public boolean canStart() {
            final BlockPos blockPos1 = this.a.getWanderTarget();
            return blockPos1 != null && this.isTooFarFrom(blockPos1, this.proximityDistance);
        }
        
        @Override
        public void tick() {
            final BlockPos blockPos1 = this.a.getWanderTarget();
            if (blockPos1 != null && WanderingTraderEntity.this.navigation.isIdle()) {
                if (this.isTooFarFrom(blockPos1, 10.0)) {
                    final Vec3d vec3d2 = new Vec3d(blockPos1.getX() - this.a.x, blockPos1.getY() - this.a.y, blockPos1.getZ() - this.a.z).normalize();
                    final Vec3d vec3d3 = vec3d2.multiply(10.0).add(this.a.x, this.a.y, this.a.z);
                    WanderingTraderEntity.this.navigation.startMovingTo(vec3d3.x, vec3d3.y, vec3d3.z, this.speed);
                }
                else {
                    WanderingTraderEntity.this.navigation.startMovingTo(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ(), this.speed);
                }
            }
        }
        
        private boolean isTooFarFrom(final BlockPos blockPos, final double proximityDistance) {
            return !blockPos.isWithinDistance(this.a.getPos(), proximityDistance);
        }
    }
}
