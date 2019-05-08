package net.minecraft.entity.passive;

import net.minecraft.world.loot.LootSupplier;
import java.util.Random;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.loot.LootTables;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.BedBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.tag.BlockTags;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import java.util.HashMap;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Item;
import net.minecraft.item.DyeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.GoToOwnerAndPurrGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.recipe.Ingredient;

public class CatEntity extends TameableEntity
{
    private static final Ingredient TAMING_INGREDIENT;
    private static final TrackedData<Integer> CAT_TYPE;
    private static final TrackedData<Boolean> SLEEPING_WITH_OWNER;
    private static final TrackedData<Boolean> HEAD_DOWN;
    private static final TrackedData<Integer> COLLAR_COLOR;
    public static final Map<Integer, Identifier> TEXTURES;
    private CatFleeGoal<PlayerEntity> fleeGoal;
    private TemptGoal temptGoal;
    private float sleepAnimation;
    private float prevSleepAnimation;
    private float tailCurlAnimation;
    private float prevTailCurlAnimation;
    private float headDownAnimation;
    private float prevHeadDownAniamtion;
    
    public CatEntity(final EntityType<? extends CatEntity> type, final World world) {
        super(type, world);
    }
    
    public Identifier getTexture() {
        return CatEntity.TEXTURES.get(this.getCatType());
    }
    
    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.temptGoal = new CatTemptGoal(this, 0.6, CatEntity.TAMING_INGREDIENT, true);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new SleepWithOwnerGoal(this));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(3, this.temptGoal);
        this.goalSelector.add(5, new GoToOwnerAndPurrGoal(this, 1.1, 8));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f));
        this.goalSelector.add(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.add(8, new PounceAtTargetGoal(this, 0.3f));
        this.goalSelector.add(9, new AttackGoal(this));
        this.goalSelector.add(10, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(11, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5f));
        this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.add(1, new FollowTargetIfTamedGoal<>(this, RabbitEntity.class, false, null));
        this.targetSelector.add(1, new FollowTargetIfTamedGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }
    
    public int getCatType() {
        return this.dataTracker.<Integer>get(CatEntity.CAT_TYPE);
    }
    
    public void setCatType(int type) {
        if (type < 0 || type >= 11) {
            type = this.random.nextInt(10);
        }
        this.dataTracker.<Integer>set(CatEntity.CAT_TYPE, type);
    }
    
    public void setSleepingWithOwner(final boolean sleeping) {
        this.dataTracker.<Boolean>set(CatEntity.SLEEPING_WITH_OWNER, sleeping);
    }
    
    public boolean isSleepingWithOwner() {
        return this.dataTracker.<Boolean>get(CatEntity.SLEEPING_WITH_OWNER);
    }
    
    public void setHeadDown(final boolean headDown) {
        this.dataTracker.<Boolean>set(CatEntity.HEAD_DOWN, headDown);
    }
    
    public boolean isHeadDown() {
        return this.dataTracker.<Boolean>get(CatEntity.HEAD_DOWN);
    }
    
    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataTracker.<Integer>get(CatEntity.COLLAR_COLOR));
    }
    
    public void setCollarColor(final DyeColor dyeColor) {
        this.dataTracker.<Integer>set(CatEntity.COLLAR_COLOR, dyeColor.getId());
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(CatEntity.CAT_TYPE, 1);
        this.dataTracker.<Boolean>startTracking(CatEntity.SLEEPING_WITH_OWNER, false);
        this.dataTracker.<Boolean>startTracking(CatEntity.HEAD_DOWN, false);
        this.dataTracker.<Integer>startTracking(CatEntity.COLLAR_COLOR, DyeColor.o.getId());
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("CatType", this.getCatType());
        tag.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setCatType(tag.getInt("CatType"));
        if (tag.containsKey("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        }
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
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isTamed()) {
            return SoundEvents.aE;
        }
        if (this.isInLove()) {
            return SoundEvents.aK;
        }
        if (this.random.nextInt(4) == 0) {
            return SoundEvents.aL;
        }
        return SoundEvents.aD;
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }
    
    public void hiss() {
        this.playSound(SoundEvents.aH, this.getSoundVolume(), this.getSoundPitch());
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.aJ;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.aF;
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
    
    @Override
    protected void eat(final PlayerEntity player, final ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(SoundEvents.aG, 1.0f, 1.0f);
        }
        super.eat(player, stack);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        return entity.damage(DamageSource.mob(this), 3.0f);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
            this.playSound(SoundEvents.aI, 1.0f, 1.0f);
        }
        this.updateAnimations();
    }
    
    private void updateAnimations() {
        if ((this.isSleepingWithOwner() || this.isHeadDown()) && this.age % 5 == 0) {
            this.playSound(SoundEvents.aK, 0.6f + 0.4f * (this.random.nextFloat() - this.random.nextFloat()), 1.0f);
        }
        this.updateSleepAnimation();
        this.updateHeadDownAnimation();
    }
    
    private void updateSleepAnimation() {
        this.prevSleepAnimation = this.sleepAnimation;
        this.prevTailCurlAnimation = this.tailCurlAnimation;
        if (this.isSleepingWithOwner()) {
            this.sleepAnimation = Math.min(1.0f, this.sleepAnimation + 0.15f);
            this.tailCurlAnimation = Math.min(1.0f, this.tailCurlAnimation + 0.08f);
        }
        else {
            this.sleepAnimation = Math.max(0.0f, this.sleepAnimation - 0.22f);
            this.tailCurlAnimation = Math.max(0.0f, this.tailCurlAnimation - 0.13f);
        }
    }
    
    private void updateHeadDownAnimation() {
        this.prevHeadDownAniamtion = this.headDownAnimation;
        if (this.isHeadDown()) {
            this.headDownAnimation = Math.min(1.0f, this.headDownAnimation + 0.1f);
        }
        else {
            this.headDownAnimation = Math.max(0.0f, this.headDownAnimation - 0.13f);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float getSleepAnimation(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevSleepAnimation, this.sleepAnimation);
    }
    
    @Environment(EnvType.CLIENT)
    public float getTailCurlAnimation(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevTailCurlAnimation, this.tailCurlAnimation);
    }
    
    @Environment(EnvType.CLIENT)
    public float getHeadDownAnimation(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevHeadDownAniamtion, this.headDownAnimation);
    }
    
    @Override
    public CatEntity createChild(final PassiveEntity mate) {
        final CatEntity catEntity2 = EntityType.CAT.create(this.world);
        if (mate instanceof CatEntity) {
            if (this.random.nextBoolean()) {
                catEntity2.setCatType(this.getCatType());
            }
            else {
                catEntity2.setCatType(((CatEntity)mate).getCatType());
            }
            if (this.isTamed()) {
                catEntity2.setOwnerUuid(this.getOwnerUuid());
                catEntity2.setTamed(true);
                if (this.random.nextBoolean()) {
                    catEntity2.setCollarColor(this.getCollarColor());
                }
                else {
                    catEntity2.setCollarColor(((CatEntity)mate).getCollarColor());
                }
            }
        }
        return catEntity2;
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        if (!this.isTamed()) {
            return false;
        }
        if (!(other instanceof CatEntity)) {
            return false;
        }
        final CatEntity catEntity2 = (CatEntity)other;
        return catEntity2.isTamed() && super.canBreedWith(other);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (iWorld.getMoonSize() > 0.9f) {
            this.setCatType(this.random.nextInt(11));
        }
        else {
            this.setCatType(this.random.nextInt(10));
        }
        if (Feature.SWAMP_HUT.isInsideStructure(iWorld, new BlockPos(this))) {
            this.setCatType(10);
            this.setPersistent();
        }
        return entityData;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final Item item4 = itemStack3.getItem();
        if (this.isTamed()) {
            if (this.isOwner(player)) {
                if (item4 instanceof DyeItem) {
                    final DyeColor dyeColor5 = ((DyeItem)item4).getColor();
                    if (dyeColor5 != this.getCollarColor()) {
                        this.setCollarColor(dyeColor5);
                        if (!player.abilities.creativeMode) {
                            itemStack3.subtractAmount(1);
                        }
                        this.setPersistent();
                        return true;
                    }
                }
                else if (this.isBreedingItem(itemStack3)) {
                    if (this.getHealth() < this.getHealthMaximum() && item4.isFood()) {
                        this.eat(player, itemStack3);
                        this.heal((float)item4.getFoodSetting().getHunger());
                        return true;
                    }
                }
                else if (!this.world.isClient) {
                    this.sitGoal.setEnabledWithOwner(!this.isSitting());
                }
            }
        }
        else if (this.isBreedingItem(itemStack3)) {
            this.eat(player, itemStack3);
            if (!this.world.isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setOwner(player);
                    this.showEmoteParticle(true);
                    this.sitGoal.setEnabledWithOwner(true);
                    this.world.sendEntityStatus(this, (byte)7);
                }
                else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            this.setPersistent();
            return true;
        }
        final boolean boolean5 = super.interactMob(player, hand);
        if (boolean5) {
            this.setPersistent();
        }
        return boolean5;
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return CatEntity.TAMING_INGREDIENT.a(stack);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.5f;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return !this.isTamed() && this.age > 2400;
    }
    
    @Override
    protected void onTamedChanged() {
        if (this.fleeGoal == null) {
            this.fleeGoal = new CatFleeGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.remove(this.fleeGoal);
        if (!this.isTamed()) {
            this.goalSelector.add(4, this.fleeGoal);
        }
    }
    
    static {
        TAMING_INGREDIENT = Ingredient.ofItems(Items.lb, Items.lc);
        CAT_TYPE = DataTracker.<Integer>registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SLEEPING_WITH_OWNER = DataTracker.<Boolean>registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        HEAD_DOWN = DataTracker.<Boolean>registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        COLLAR_COLOR = DataTracker.<Integer>registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
        TEXTURES = SystemUtil.<Map<Integer, Identifier>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(0, new Identifier("textures/entity/cat/tabby.png"));
            hashMap.put(1, new Identifier("textures/entity/cat/black.png"));
            hashMap.put(2, new Identifier("textures/entity/cat/red.png"));
            hashMap.put(3, new Identifier("textures/entity/cat/siamese.png"));
            hashMap.put(4, new Identifier("textures/entity/cat/british_shorthair.png"));
            hashMap.put(5, new Identifier("textures/entity/cat/calico.png"));
            hashMap.put(6, new Identifier("textures/entity/cat/persian.png"));
            hashMap.put(7, new Identifier("textures/entity/cat/ragdoll.png"));
            hashMap.put(8, new Identifier("textures/entity/cat/white.png"));
            hashMap.put(9, new Identifier("textures/entity/cat/jellie.png"));
            hashMap.put(10, new Identifier("textures/entity/cat/all_black.png"));
        });
    }
    
    static class CatFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
    {
        private final CatEntity entity;
        
        public CatFleeGoal(final CatEntity catEntity, final Class<T> class2, final float float3, final double double4, final double double6) {
            super(catEntity, class2, float3, double4, double6, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.entity = catEntity;
        }
        
        @Override
        public boolean canStart() {
            return !this.entity.isTamed() && super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.entity.isTamed() && super.shouldContinue();
        }
    }
    
    static class CatTemptGoal extends TemptGoal
    {
        @Nullable
        private PlayerEntity c;
        private final CatEntity cat;
        
        public CatTemptGoal(final CatEntity catEntity, final double double2, final Ingredient ingredient, final boolean boolean5) {
            super(catEntity, double2, ingredient, boolean5);
            this.cat = catEntity;
        }
        
        @Override
        public void tick() {
            super.tick();
            if (this.c == null && this.owner.getRand().nextInt(600) == 0) {
                this.c = this.closestPlayer;
            }
            else if (this.owner.getRand().nextInt(500) == 0) {
                this.c = null;
            }
        }
        
        @Override
        protected boolean canBeScared() {
            return (this.c == null || !this.c.equals(this.closestPlayer)) && super.canBeScared();
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !this.cat.isTamed();
        }
    }
    
    static class SleepWithOwnerGoal extends Goal
    {
        private final CatEntity entity;
        private PlayerEntity owner;
        private BlockPos bedPos;
        private int ticksOnBed;
        
        public SleepWithOwnerGoal(final CatEntity catEntity) {
            this.entity = catEntity;
        }
        
        @Override
        public boolean canStart() {
            if (!this.entity.isTamed()) {
                return false;
            }
            if (this.entity.isSitting()) {
                return false;
            }
            final LivingEntity livingEntity1 = this.entity.getOwner();
            if (livingEntity1 instanceof PlayerEntity) {
                this.owner = (PlayerEntity)livingEntity1;
                if (!livingEntity1.isSleeping()) {
                    return false;
                }
                if (this.entity.squaredDistanceTo(this.owner) > 100.0) {
                    return false;
                }
                final BlockPos blockPos2 = new BlockPos(this.owner);
                final BlockState blockState3 = this.entity.world.getBlockState(blockPos2);
                if (blockState3.getBlock().matches(BlockTags.F)) {
                    final Direction direction4 = blockState3.<Direction>get((Property<Direction>)BedBlock.FACING);
                    this.bedPos = new BlockPos(blockPos2.getX() - direction4.getOffsetX(), blockPos2.getY(), blockPos2.getZ() - direction4.getOffsetZ());
                    return !this.g();
                }
            }
            return false;
        }
        
        private boolean g() {
            final List<CatEntity> list1 = this.entity.world.<CatEntity>getEntities(CatEntity.class, new BoundingBox(this.bedPos).expand(2.0));
            for (final CatEntity catEntity3 : list1) {
                if (catEntity3 != this.entity && (catEntity3.isSleepingWithOwner() || catEntity3.isHeadDown())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean shouldContinue() {
            return this.entity.isTamed() && !this.entity.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.g();
        }
        
        @Override
        public void start() {
            if (this.bedPos != null) {
                this.entity.getSitGoal().setEnabledWithOwner(false);
                this.entity.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.100000023841858);
            }
        }
        
        @Override
        public void stop() {
            this.entity.setSleepingWithOwner(false);
            final float float1 = this.entity.world.getSkyAngle(1.0f);
            if (this.owner.getSleepTimer() >= 100 && float1 > 0.77 && float1 < 0.8 && this.entity.world.getRandom().nextFloat() < 0.7) {
                this.dropMorningGifts();
            }
            this.ticksOnBed = 0;
            this.entity.setHeadDown(false);
            this.entity.getNavigation().stop();
        }
        
        private void dropMorningGifts() {
            final Random random1 = this.entity.getRand();
            final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
            mutable2.set(this.entity);
            this.entity.teleport(mutable2.getX() + random1.nextInt(11) - 5, mutable2.getY() + random1.nextInt(5) - 2, mutable2.getZ() + random1.nextInt(11) - 5, false);
            mutable2.set(this.entity);
            final LootSupplier lootSupplier3 = this.entity.world.getServer().getLootManager().getSupplier(LootTables.GAMEPLAY_CAT_MORNING_GIFT);
            final LootContext.Builder builder4 = new LootContext.Builder((ServerWorld)this.entity.world).<BlockPos>put(LootContextParameters.f, mutable2).<Entity>put(LootContextParameters.a, this.entity).setRandom(random1);
            final List<ItemStack> list5 = lootSupplier3.getDrops(builder4.build(LootContextTypes.GIFT));
            for (final ItemStack itemStack7 : list5) {
                this.entity.world.spawnEntity(new ItemEntity(this.entity.world, mutable2.getX() - MathHelper.sin(this.entity.aK * 0.017453292f), mutable2.getY(), mutable2.getZ() + MathHelper.cos(this.entity.aK * 0.017453292f), itemStack7));
            }
        }
        
        @Override
        public void tick() {
            if (this.owner != null && this.bedPos != null) {
                this.entity.getSitGoal().setEnabledWithOwner(false);
                this.entity.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.100000023841858);
                if (this.entity.squaredDistanceTo(this.owner) < 2.5) {
                    ++this.ticksOnBed;
                    if (this.ticksOnBed > 16) {
                        this.entity.setSleepingWithOwner(true);
                        this.entity.setHeadDown(false);
                    }
                    else {
                        this.entity.lookAtEntity(this.owner, 45.0f, 45.0f);
                        this.entity.setHeadDown(true);
                    }
                }
                else {
                    this.entity.setSleepingWithOwner(false);
                }
            }
        }
    }
}
