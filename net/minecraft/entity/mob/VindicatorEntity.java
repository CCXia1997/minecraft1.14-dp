package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.entity.raid.Raid;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import com.google.common.collect.Maps;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.world.Difficulty;
import java.util.function.Predicate;

public class VindicatorEntity extends IllagerEntity
{
    private static final Predicate<Difficulty> b;
    private boolean isJohnny;
    
    public VindicatorEntity(final EntityType<? extends VindicatorEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new VindicatorBreakDoorGoal(this));
        this.goalSelector.add(2, new IllagerEntity.b(this));
        this.goalSelector.add(3, new PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(4, new c(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new b(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }
    
    @Override
    protected void mobTick() {
        if (!this.isAiDisabled()) {
            if (((ServerWorld)this.world).hasRaidAt(new BlockPos(this))) {
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
            }
            else {
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(false);
            }
        }
        super.mobTick();
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.isJohnny) {
            tag.putBoolean("Johnny", true);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public State getState() {
        if (this.isAttacking()) {
            return State.b;
        }
        if (this.isCelebrating()) {
            return State.g;
        }
        return State.a;
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Johnny", 99)) {
            this.isJohnny = tag.getBoolean("Johnny");
        }
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.mP;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        final EntityData entityData2 = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.initEquipment(localDifficulty);
        this.updateEnchantments(localDifficulty);
        return entityData2;
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        if (this.getRaid() == null) {
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jc));
        }
    }
    
    @Override
    public boolean isTeammate(final Entity entity) {
        return super.isTeammate(entity) || (entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null);
    }
    
    @Override
    public void setCustomName(@Nullable final TextComponent textComponent) {
        super.setCustomName(textComponent);
        if (!this.isJohnny && textComponent != null && textComponent.getString().equals("Johnny")) {
            this.isJohnny = true;
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.mO;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.mQ;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.mR;
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
        final ItemStack itemStack3 = new ItemStack(Items.jc);
        final Raid raid4 = this.getRaid();
        int integer5 = 1;
        if (wave > raid4.getMaxWaves(Difficulty.NORMAL)) {
            integer5 = 2;
        }
        final boolean boolean3 = this.random.nextFloat() <= raid4.getEnchantmentChance();
        if (boolean3) {
            final Map<Enchantment, Integer> map7 = Maps.newHashMap();
            map7.put(Enchantments.l, integer5);
            EnchantmentHelper.set(map7, itemStack3);
        }
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack3);
    }
    
    static {
        b = (difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD);
    }
    
    class c extends MeleeAttackGoal
    {
        public c(final VindicatorEntity vindicatorEntity2) {
            super(vindicatorEntity2, 1.0, false);
        }
        
        @Override
        protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
            if (this.entity.getVehicle() instanceof RavagerEntity) {
                final float float2 = this.entity.getVehicle().getWidth() - 0.1f;
                return float2 * 2.0f * (float2 * 2.0f) + entity.getWidth();
            }
            return super.getSquaredMaxAttackDistance(entity);
        }
    }
    
    static class VindicatorBreakDoorGoal extends BreakDoorGoal
    {
        public VindicatorBreakDoorGoal(final MobEntity mobEntity) {
            super(mobEntity, 6, VindicatorEntity.b);
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean shouldContinue() {
            final VindicatorEntity vindicatorEntity1 = (VindicatorEntity)this.owner;
            return vindicatorEntity1.hasActiveRaid() && super.shouldContinue();
        }
        
        @Override
        public boolean canStart() {
            final VindicatorEntity vindicatorEntity1 = (VindicatorEntity)this.owner;
            return vindicatorEntity1.hasActiveRaid() && vindicatorEntity1.random.nextInt(10) == 0 && super.canStart();
        }
        
        @Override
        public void start() {
            super.start();
            this.owner.setDespawnCounter(0);
        }
    }
    
    static class b extends FollowTargetGoal<LivingEntity>
    {
        public b(final VindicatorEntity vindicatorEntity) {
            super(vindicatorEntity, LivingEntity.class, 0, true, true, LivingEntity::du);
        }
        
        @Override
        public boolean canStart() {
            return ((VindicatorEntity)this.entity).isJohnny && super.canStart();
        }
        
        @Override
        public void start() {
            super.start();
            this.entity.setDespawnCounter(0);
        }
    }
}
