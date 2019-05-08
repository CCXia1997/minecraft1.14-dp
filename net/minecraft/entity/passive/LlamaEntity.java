package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.sound.BlockSoundGroup;
import java.util.Iterator;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.inventory.Inventory;
import net.minecraft.tag.ItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.ai.RangedAttacker;

public class LlamaEntity extends AbstractDonkeyEntity implements RangedAttacker
{
    private static final TrackedData<Integer> ATTR_STRENGTH;
    private static final TrackedData<Integer> CARPET_COLOR;
    private static final TrackedData<Integer> ATTR_VARIANT;
    private boolean bM;
    @Nullable
    private LlamaEntity bN;
    @Nullable
    private LlamaEntity bO;
    
    public LlamaEntity(final EntityType<? extends LlamaEntity> type, final World world) {
        super(type, world);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isTrader() {
        return false;
    }
    
    private void setStrength(final int integer) {
        this.dataTracker.<Integer>set(LlamaEntity.ATTR_STRENGTH, Math.max(1, Math.min(5, integer)));
    }
    
    private void initializeStrength() {
        final int integer1 = (this.random.nextFloat() < 0.04f) ? 5 : 3;
        this.setStrength(1 + this.random.nextInt(integer1));
    }
    
    public int getStrength() {
        return this.dataTracker.<Integer>get(LlamaEntity.ATTR_STRENGTH);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
        tag.putInt("Strength", this.getStrength());
        if (!this.items.getInvStack(1).isEmpty()) {
            tag.put("DecorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.setStrength(tag.getInt("Strength"));
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.containsKey("DecorItem", 10)) {
            this.items.setInvStack(1, ItemStack.fromTag(tag.getCompound("DecorItem")));
        }
        this.updateSaddle();
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new FormCaravanGoal(this, 2.0999999046325684));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new c(this));
        this.targetSelector.add(2, new a(this));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(LlamaEntity.ATTR_STRENGTH, 0);
        this.dataTracker.<Integer>startTracking(LlamaEntity.CARPET_COLOR, -1);
        this.dataTracker.<Integer>startTracking(LlamaEntity.ATTR_VARIANT, 0);
    }
    
    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.<Integer>get(LlamaEntity.ATTR_VARIANT), 0, 3);
    }
    
    public void setVariant(final int integer) {
        this.dataTracker.<Integer>set(LlamaEntity.ATTR_VARIANT, integer);
    }
    
    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.dZ();
        }
        return super.getInventorySize();
    }
    
    @Override
    public void updatePassengerPosition(final Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        final float float2 = MathHelper.cos(this.aK * 0.017453292f);
        final float float3 = MathHelper.sin(this.aK * 0.017453292f);
        final float float4 = 0.3f;
        passenger.setPosition(this.x + 0.3f * float3, this.y + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.z - 0.3f * float2);
    }
    
    @Override
    public double getMountedHeightOffset() {
        return this.getHeight() * 0.67;
    }
    
    @Override
    public boolean canBeControlledByRider() {
        return false;
    }
    
    @Override
    protected boolean receiveFood(final PlayerEntity player, final ItemStack item) {
        int integer3 = 0;
        int integer4 = 0;
        float float5 = 0.0f;
        boolean boolean6 = false;
        final Item item2 = item.getItem();
        if (item2 == Items.jP) {
            integer3 = 10;
            integer4 = 3;
            float5 = 2.0f;
        }
        else if (item2 == Blocks.gs.getItem()) {
            integer3 = 90;
            integer4 = 6;
            float5 = 10.0f;
            if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
                boolean6 = true;
                this.lovePlayer(player);
            }
        }
        if (this.getHealth() < this.getHealthMaximum() && float5 > 0.0f) {
            this.heal(float5);
            boolean6 = true;
        }
        if (this.isChild() && integer3 > 0) {
            this.world.addParticle(ParticleTypes.C, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(integer3);
            }
            boolean6 = true;
        }
        if (integer4 > 0 && (boolean6 || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            boolean6 = true;
            if (!this.world.isClient) {
                this.addTemper(integer4);
            }
        }
        if (boolean6 && !this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.gd, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return boolean6;
    }
    
    @Override
    protected boolean cannotMove() {
        return this.getHealth() <= 0.0f || this.isEatingGrass();
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        this.initializeStrength();
        int integer6;
        if (entityData instanceof b) {
            integer6 = ((b)entityData).a;
        }
        else {
            integer6 = this.random.nextInt(4);
            entityData = new b(integer6);
        }
        this.setVariant(integer6);
        return entityData;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ga;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.fZ;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.ge;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.gc;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.gg, 0.15f, 1.0f);
    }
    
    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.gb, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }
    
    @Override
    public void playAngrySound() {
        final SoundEvent soundEvent1 = this.getAngrySound();
        if (soundEvent1 != null) {
            this.playSound(soundEvent1, this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    @Override
    public int dZ() {
        return this.getStrength();
    }
    
    @Override
    public boolean canEquip() {
        return true;
    }
    
    @Override
    public boolean canEquip(final ItemStack item) {
        final Item item2 = item.getItem();
        return ItemTags.f.contains(item2);
    }
    
    @Override
    public boolean canBeSaddled() {
        return false;
    }
    
    @Override
    public void onInvChange(final Inventory inventory) {
        final DyeColor dyeColor2 = this.getCarpetColor();
        super.onInvChange(inventory);
        final DyeColor dyeColor3 = this.getCarpetColor();
        if (this.age > 20 && dyeColor3 != null && dyeColor3 != dyeColor2) {
            this.playSound(SoundEvents.gh, 0.5f, 1.0f);
        }
    }
    
    @Override
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        super.updateSaddle();
        this.setCarpetColor(getColorFromCarpet(this.items.getInvStack(1)));
    }
    
    private void setCarpetColor(@Nullable final DyeColor color) {
        this.dataTracker.<Integer>set(LlamaEntity.CARPET_COLOR, (color == null) ? -1 : color.getId());
    }
    
    @Nullable
    private static DyeColor getColorFromCarpet(final ItemStack color) {
        final Block block2 = Block.getBlockFromItem(color.getItem());
        if (block2 instanceof CarpetBlock) {
            return ((CarpetBlock)block2).getColor();
        }
        return null;
    }
    
    @Nullable
    public DyeColor getCarpetColor() {
        final int integer1 = this.dataTracker.<Integer>get(LlamaEntity.CARPET_COLOR);
        return (integer1 == -1) ? null : DyeColor.byId(integer1);
    }
    
    @Override
    public int getMaxTemper() {
        return 30;
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        return other != this && other instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)other).canBreed();
    }
    
    @Override
    public LlamaEntity createChild(final PassiveEntity mate) {
        final LlamaEntity llamaEntity2 = this.createChild();
        this.setChildAttributes(mate, llamaEntity2);
        final LlamaEntity llamaEntity3 = (LlamaEntity)mate;
        int integer4 = this.random.nextInt(Math.max(this.getStrength(), llamaEntity3.getStrength())) + 1;
        if (this.random.nextFloat() < 0.03f) {
            ++integer4;
        }
        llamaEntity2.setStrength(integer4);
        llamaEntity2.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity3.getVariant());
        return llamaEntity2;
    }
    
    protected LlamaEntity createChild() {
        return EntityType.LLAMA.create(this.world);
    }
    
    private void h(final LivingEntity livingEntity) {
        final LlamaSpitEntity llamaSpitEntity2 = new LlamaSpitEntity(this.world, this);
        final double double3 = livingEntity.x - this.x;
        final double double4 = livingEntity.getBoundingBox().minY + livingEntity.getHeight() / 3.0f - llamaSpitEntity2.y;
        final double double5 = livingEntity.z - this.z;
        final float float9 = MathHelper.sqrt(double3 * double3 + double5 * double5) * 0.2f;
        llamaSpitEntity2.setVelocity(double3, double4 + float9, double5, 1.5f, 10.0f);
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.gf, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        this.world.spawnEntity(llamaSpitEntity2);
        this.bM = true;
    }
    
    private void z(final boolean boolean1) {
        this.bM = boolean1;
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        final int integer3 = MathHelper.ceil((fallDistance * 0.5f - 3.0f) * damageMultiplier);
        if (integer3 <= 0) {
            return;
        }
        if (fallDistance >= 6.0f) {
            this.damage(DamageSource.FALL, (float)integer3);
            if (this.hasPassengers()) {
                for (final Entity entity5 : this.getPassengersDeep()) {
                    entity5.damage(DamageSource.FALL, (float)integer3);
                }
            }
        }
        final BlockState blockState4 = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - this.prevYaw, this.z));
        if (!blockState4.isAir() && !this.isSilent()) {
            final BlockSoundGroup blockSoundGroup5 = blockState4.getSoundGroup();
            this.world.playSound(null, this.x, this.y, this.z, blockSoundGroup5.getStepSound(), this.getSoundCategory(), blockSoundGroup5.getVolume() * 0.5f, blockSoundGroup5.getPitch() * 0.75f);
        }
    }
    
    public void eG() {
        if (this.bN != null) {
            this.bN.bO = null;
        }
        this.bN = null;
    }
    
    public void a(final LlamaEntity llamaEntity) {
        this.bN = llamaEntity;
        this.bN.bO = this;
    }
    
    public boolean eH() {
        return this.bO != null;
    }
    
    public boolean isFollowing() {
        return this.bN != null;
    }
    
    @Nullable
    public LlamaEntity getFollowing() {
        return this.bN;
    }
    
    @Override
    protected double getRunFromLeashSpeed() {
        return 2.0;
    }
    
    @Override
    protected void walkToParent() {
        if (!this.isFollowing() && this.isChild()) {
            super.walkToParent();
        }
    }
    
    @Override
    public boolean eatsGrass() {
        return false;
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        this.h(target);
    }
    
    static {
        ATTR_STRENGTH = DataTracker.<Integer>registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CARPET_COLOR = DataTracker.<Integer>registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ATTR_VARIANT = DataTracker.<Integer>registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    static class b implements EntityData
    {
        public final int a;
        
        private b(final int integer) {
            this.a = integer;
        }
    }
    
    static class c extends RevengeGoal
    {
        public c(final LlamaEntity llamaEntity) {
            super(llamaEntity, new Class[0]);
        }
        
        @Override
        public boolean shouldContinue() {
            if (this.entity instanceof LlamaEntity) {
                final LlamaEntity llamaEntity1 = (LlamaEntity)this.entity;
                if (llamaEntity1.bM) {
                    llamaEntity1.z(false);
                    return false;
                }
            }
            return super.shouldContinue();
        }
    }
    
    static class a extends FollowTargetGoal<WolfEntity>
    {
        public a(final LlamaEntity llamaEntity) {
            super(llamaEntity, WolfEntity.class, 16, false, true, livingEntity -> !livingEntity.isTamed());
        }
        
        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.25;
        }
    }
}
