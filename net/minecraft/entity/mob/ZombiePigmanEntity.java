package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.util.Hand;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.Entity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.ZombieRaiseArmsGoal;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

public class ZombiePigmanEntity extends ZombieEntity
{
    private static final UUID b;
    private static final EntityAttributeModifier c;
    private int anger;
    private int bA;
    private UUID angerTarget;
    
    public ZombiePigmanEntity(final EntityType<? extends ZombiePigmanEntity> type, final World world) {
        super(type, world);
        this.setPathNodeTypeWeight(PathNodeType.f, 8.0f);
    }
    
    @Override
    public void setAttacker(@Nullable final LivingEntity livingEntity) {
        super.setAttacker(livingEntity);
        if (livingEntity != null) {
            this.angerTarget = livingEntity.getUuid();
        }
    }
    
    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieRaiseArmsGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new AvoidZombiesGoal(this));
        this.targetSelector.add(2, new FollowPlayerIfAngryGoal(this));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(ZombiePigmanEntity.SPAWN_REINFORCEMENTS).setBaseValue(0.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    protected boolean canConvertInWater() {
        return false;
    }
    
    @Override
    protected void mobTick() {
        final EntityAttributeInstance entityAttributeInstance1 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (this.isAngry()) {
            if (!this.isChild() && !entityAttributeInstance1.hasModifier(ZombiePigmanEntity.c)) {
                entityAttributeInstance1.addModifier(ZombiePigmanEntity.c);
            }
            --this.anger;
        }
        else if (entityAttributeInstance1.hasModifier(ZombiePigmanEntity.c)) {
            entityAttributeInstance1.removeModifier(ZombiePigmanEntity.c);
        }
        if (this.bA > 0 && --this.bA == 0) {
            this.playSound(SoundEvents.of, this.getSoundVolume() * 2.0f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 1.8f);
        }
        if (this.anger > 0 && this.angerTarget != null && this.getAttacker() == null) {
            final PlayerEntity playerEntity2 = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(playerEntity2);
            this.attackingPlayer = playerEntity2;
            this.playerHitTimer = this.getLastAttackedTime();
        }
        super.mobTick();
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return world.intersectsEntities(this) && !world.intersectsFluid(this.getBoundingBox());
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putShort("Anger", (short)this.anger);
        if (this.angerTarget != null) {
            tag.putString("HurtBy", this.angerTarget.toString());
        }
        else {
            tag.putString("HurtBy", "");
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.anger = tag.getShort("Anger");
        final String string2 = tag.getString("HurtBy");
        if (!string2.isEmpty()) {
            this.angerTarget = UUID.fromString(string2);
            final PlayerEntity playerEntity3 = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(playerEntity3);
            if (playerEntity3 != null) {
                this.attackingPlayer = playerEntity3;
                this.playerHitTimer = this.getLastAttackedTime();
            }
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        final Entity entity3 = source.getAttacker();
        if (entity3 instanceof PlayerEntity && !((PlayerEntity)entity3).isCreative()) {
            this.copyEntityData(entity3);
        }
        return super.damage(source, amount);
    }
    
    private void copyEntityData(final Entity entity) {
        this.anger = 400 + this.random.nextInt(400);
        this.bA = this.random.nextInt(40);
        if (entity instanceof LivingEntity) {
            this.setAttacker((LivingEntity)entity);
        }
    }
    
    public boolean isAngry() {
        return this.anger > 0;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.oe;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.oh;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.og;
    }
    
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        return false;
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jC));
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean isAngryAt(final PlayerEntity player) {
        return this.isAngry();
    }
    
    static {
        b = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
        c = new EntityAttributeModifier(ZombiePigmanEntity.b, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.a).setSerialize(false);
    }
    
    static class AvoidZombiesGoal extends RevengeGoal
    {
        public AvoidZombiesGoal(final ZombiePigmanEntity zombiePigmanEntity) {
            super(zombiePigmanEntity, new Class[0]);
            this.setGroupRevenge(ZombieEntity.class);
        }
        
        @Override
        protected void setMobEntityTarget(final MobEntity mobEntity, final LivingEntity livingEntity) {
            super.setMobEntityTarget(mobEntity, livingEntity);
            if (mobEntity instanceof ZombiePigmanEntity) {
                ((ZombiePigmanEntity)mobEntity).copyEntityData(livingEntity);
            }
        }
    }
    
    static class FollowPlayerIfAngryGoal extends FollowTargetGoal<PlayerEntity>
    {
        public FollowPlayerIfAngryGoal(final ZombiePigmanEntity zombiePigmanEntity) {
            super(zombiePigmanEntity, PlayerEntity.class, true);
        }
        
        @Override
        public boolean canStart() {
            return ((ZombiePigmanEntity)this.entity).isAngry() && super.canStart();
        }
    }
}
