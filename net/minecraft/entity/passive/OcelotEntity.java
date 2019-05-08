package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.EntityData;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.recipe.Ingredient;

public class OcelotEntity extends AnimalEntity
{
    private static final Ingredient TAMING_INGREDIENT;
    private static final TrackedData<Boolean> TRUSTING;
    private OcelotFleeGoal<PlayerEntity> fleeGoal;
    private OcelotTemptGoal temptGoal;
    
    public OcelotEntity(final EntityType<? extends OcelotEntity> type, final World world) {
        super(type, world);
        this.updateFleeing();
    }
    
    private boolean isTrusting() {
        return this.dataTracker.<Boolean>get(OcelotEntity.TRUSTING);
    }
    
    private void setTrusting(final boolean trusting) {
        this.dataTracker.<Boolean>set(OcelotEntity.TRUSTING, trusting);
        this.updateFleeing();
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Trusting", this.isTrusting());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setTrusting(tag.getBoolean("Trusting"));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(OcelotEntity.TRUSTING, false);
    }
    
    @Override
    protected void initGoals() {
        this.temptGoal = new OcelotTemptGoal(this, 0.6, OcelotEntity.TAMING_INGREDIENT, true);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, this.temptGoal);
        this.goalSelector.add(7, new PounceAtTargetGoal(this, 0.3f));
        this.goalSelector.add(8, new AttackGoal(this));
        this.goalSelector.add(9, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5f));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, ChickenEntity.class, false));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }
    
    public void mobTick() {
        if (this.getMoveControl().isMoving()) {
            final double double1 = this.getMoveControl().getSpeed();
            if (double1 == 0.6) {
                this.setSneaking(true);
                this.setSprinting(false);
            }
            else if (double1 == 1.33) {
                this.setSneaking(false);
                this.setSprinting(true);
            }
            else {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        }
        else {
            this.setSneaking(false);
            this.setSprinting(false);
        }
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return !this.isTrusting() && this.age > 2400;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.hf;
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 900;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.he;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.hg;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        return entity.damage(DamageSource.mob(this), 3.0f);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return !this.isInvulnerableTo(source) && super.damage(source, amount);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if ((this.temptGoal == null || this.temptGoal.isActive()) && !this.isTrusting() && this.isBreedingItem(itemStack3) && player.squaredDistanceTo(this) < 9.0) {
            this.eat(player, itemStack3);
            if (!this.world.isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)41);
                }
                else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)40);
                }
            }
            return true;
        }
        return super.interactMob(player, hand);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 41) {
            this.showEmoteParticle(true);
        }
        else if (status == 40) {
            this.showEmoteParticle(false);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    private void showEmoteParticle(final boolean positive) {
        ParticleParameters particleParameters2 = ParticleTypes.E;
        if (!positive) {
            particleParameters2 = ParticleTypes.Q;
        }
        for (int integer3 = 0; integer3 < 7; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleParameters2, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double4, double5, double6);
        }
    }
    
    protected void updateFleeing() {
        if (this.fleeGoal == null) {
            this.fleeGoal = new OcelotFleeGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.remove(this.fleeGoal);
        if (!this.isTrusting()) {
            this.goalSelector.add(4, this.fleeGoal);
        }
    }
    
    @Override
    public OcelotEntity createChild(final PassiveEntity mate) {
        return EntityType.OCELOT.create(this.world);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return OcelotEntity.TAMING_INGREDIENT.a(stack);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return this.random.nextInt(3) != 0;
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        if (world.intersectsEntities(this) && !world.intersectsFluid(this.getBoundingBox())) {
            final BlockPos blockPos2 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
            if (blockPos2.getY() < world.getSeaLevel()) {
                return false;
            }
            final BlockState blockState3 = world.getBlockState(blockPos2.down());
            final Block block4 = blockState3.getBlock();
            if (block4 == Blocks.i || blockState3.matches(BlockTags.C)) {
                return true;
            }
        }
        return false;
    }
    
    protected void spawnKittens() {
        for (int integer1 = 0; integer1 < 2; ++integer1) {
            final OcelotEntity ocelotEntity2 = EntityType.OCELOT.create(this.world);
            ocelotEntity2.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0f);
            ocelotEntity2.setBreedingAge(-24000);
            this.world.spawnEntity(ocelotEntity2);
        }
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (iWorld.getRandom().nextInt(7) == 0) {
            this.spawnKittens();
        }
        return entityData;
    }
    
    static {
        TAMING_INGREDIENT = Ingredient.ofItems(Items.lb, Items.lc);
        TRUSTING = DataTracker.<Boolean>registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
    
    static class OcelotFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
    {
        private final OcelotEntity ocelot;
        
        public OcelotFleeGoal(final OcelotEntity ocelotEntity, final Class<T> class2, final float float3, final double double4, final double double6) {
            super(ocelotEntity, class2, float3, double4, double6, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.ocelot = ocelotEntity;
        }
        
        @Override
        public boolean canStart() {
            return !this.ocelot.isTrusting() && super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.ocelot.isTrusting() && super.shouldContinue();
        }
    }
    
    static class OcelotTemptGoal extends TemptGoal
    {
        private final OcelotEntity ocelot;
        
        public OcelotTemptGoal(final OcelotEntity ocelotEntity, final double double2, final Ingredient ingredient, final boolean boolean5) {
            super(ocelotEntity, double2, ingredient, boolean5);
            this.ocelot = ocelotEntity;
        }
        
        @Override
        protected boolean canBeScared() {
            return super.canBeScared() && !this.ocelot.isTrusting();
        }
    }
}
