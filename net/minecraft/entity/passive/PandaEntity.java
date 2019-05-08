package net.minecraft.entity.passive;

import net.minecraft.world.BlockView;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.control.MoveControl;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.recipe.Ingredient;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.data.TrackedData;

public class PandaEntity extends AnimalEntity
{
    private static final TrackedData<Integer> ASK_FOR_BAMBOO_TICKS;
    private static final TrackedData<Integer> SNEEZE_PROGRESS;
    private static final TrackedData<Integer> EATING_TICKS;
    private static final TrackedData<Byte> MAIN_GENE;
    private static final TrackedData<Byte> HIDDEN_GENE;
    private static final TrackedData<Byte> PANDA_FLAGS;
    private boolean shouldGetRevenge;
    private boolean shouldAttack;
    public int playingTicks;
    private Vec3d playingJump;
    private float scaredAnimationProgress;
    private float lastScaredAnimationProgress;
    private float lieOnBackAnimationProgress;
    private float lastLieOnBackAnimationProgress;
    private float rollOverAnimationProgress;
    private float lastRollOverAnimationProgress;
    private static final Predicate<ItemEntity> IS_FOOD;
    
    public PandaEntity(final EntityType<? extends PandaEntity> type, final World world) {
        super(type, world);
        this.moveControl = new PandaMoveControl(this);
        if (!this.isChild()) {
            this.setCanPickUpLoot(true);
        }
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = MobEntity.getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty() && equipmentSlot2 == EquipmentSlot.HAND_MAIN && super.canPickUp(itemStack);
    }
    
    public int getAskForBambooTicks() {
        return this.dataTracker.<Integer>get(PandaEntity.ASK_FOR_BAMBOO_TICKS);
    }
    
    public void setAskForBambooTicks(final int askForBambooTicks) {
        this.dataTracker.<Integer>set(PandaEntity.ASK_FOR_BAMBOO_TICKS, askForBambooTicks);
    }
    
    public boolean isSneezing() {
        return this.hasPandaFlag(2);
    }
    
    public boolean isScared() {
        return this.hasPandaFlag(8);
    }
    
    public void setScared(final boolean scared) {
        this.setPandaFlag(8, scared);
    }
    
    public boolean isLyingOnBack() {
        return this.hasPandaFlag(16);
    }
    
    public void setLyingOnBack(final boolean boolean1) {
        this.setPandaFlag(16, boolean1);
    }
    
    public boolean isEating() {
        return this.dataTracker.<Integer>get(PandaEntity.EATING_TICKS) > 0;
    }
    
    public void setEating(final boolean eating) {
        this.dataTracker.<Integer>set(PandaEntity.EATING_TICKS, eating ? 1 : 0);
    }
    
    private int getEatingTicks() {
        return this.dataTracker.<Integer>get(PandaEntity.EATING_TICKS);
    }
    
    private void setEatingTicks(final int eatingTicks) {
        this.dataTracker.<Integer>set(PandaEntity.EATING_TICKS, eatingTicks);
    }
    
    public void setSneezing(final boolean sneezing) {
        this.setPandaFlag(2, sneezing);
        if (!sneezing) {
            this.setSneezeProgress(0);
        }
    }
    
    public int getSneezeProgress() {
        return this.dataTracker.<Integer>get(PandaEntity.SNEEZE_PROGRESS);
    }
    
    public void setSneezeProgress(final int sneezeProgress) {
        this.dataTracker.<Integer>set(PandaEntity.SNEEZE_PROGRESS, sneezeProgress);
    }
    
    public Gene getMainGene() {
        return Gene.byId(this.dataTracker.<Byte>get(PandaEntity.MAIN_GENE));
    }
    
    public void setMainGene(Gene gene) {
        if (gene.getId() > 6) {
            gene = Gene.createRandom(this.random);
        }
        this.dataTracker.<Byte>set(PandaEntity.MAIN_GENE, (byte)gene.getId());
    }
    
    public Gene getHiddenGene() {
        return Gene.byId(this.dataTracker.<Byte>get(PandaEntity.HIDDEN_GENE));
    }
    
    public void setHiddenGene(Gene gene) {
        if (gene.getId() > 6) {
            gene = Gene.createRandom(this.random);
        }
        this.dataTracker.<Byte>set(PandaEntity.HIDDEN_GENE, (byte)gene.getId());
    }
    
    public boolean isPlaying() {
        return this.hasPandaFlag(4);
    }
    
    public void setPlaying(final boolean playing) {
        this.setPandaFlag(4, playing);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(PandaEntity.ASK_FOR_BAMBOO_TICKS, 0);
        this.dataTracker.<Integer>startTracking(PandaEntity.SNEEZE_PROGRESS, 0);
        this.dataTracker.<Byte>startTracking(PandaEntity.MAIN_GENE, (Byte)0);
        this.dataTracker.<Byte>startTracking(PandaEntity.HIDDEN_GENE, (Byte)0);
        this.dataTracker.<Byte>startTracking(PandaEntity.PANDA_FLAGS, (Byte)0);
        this.dataTracker.<Integer>startTracking(PandaEntity.EATING_TICKS, 0);
    }
    
    private boolean hasPandaFlag(final int integer) {
        return (this.dataTracker.<Byte>get(PandaEntity.PANDA_FLAGS) & integer) != 0x0;
    }
    
    private void setPandaFlag(final int mask, final boolean value) {
        final byte byte3 = this.dataTracker.<Byte>get(PandaEntity.PANDA_FLAGS);
        if (value) {
            this.dataTracker.<Byte>set(PandaEntity.PANDA_FLAGS, (byte)(byte3 | mask));
        }
        else {
            this.dataTracker.<Byte>set(PandaEntity.PANDA_FLAGS, (byte)(byte3 & ~mask));
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putString("MainGene", this.getMainGene().getName());
        tag.putString("HiddenGene", this.getHiddenGene().getName());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setMainGene(Gene.byName(tag.getString("MainGene")));
        this.setHiddenGene(Gene.byName(tag.getString("HiddenGene")));
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        final PandaEntity pandaEntity2 = EntityType.PANDA.create(this.world);
        if (mate instanceof PandaEntity) {
            pandaEntity2.initGenes(this, (PandaEntity)mate);
        }
        pandaEntity2.resetAttributes();
        return pandaEntity2;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new ExtinguishFireGoal(this, 2.0));
        this.goalSelector.add(2, new PandaMateGoal(this, 1.0));
        this.goalSelector.add(3, new AttackGoal(this, 1.2000000476837158, true));
        this.goalSelector.add(4, new TemptGoal(this, 1.0, Ingredient.ofItems(Blocks.kQ.getItem()), false));
        this.goalSelector.add(6, new PandaFleeGoal<>(this, PlayerEntity.class, 8.0f, 2.0, 2.0));
        this.goalSelector.add(6, new PandaFleeGoal<>(this, HostileEntity.class, 4.0f, 2.0, 2.0));
        this.goalSelector.add(7, new PickUpFoodGoal());
        this.goalSelector.add(8, new LieOnBackGoal(this));
        this.goalSelector.add(8, new SneezeGoal(this));
        this.goalSelector.add(9, new PandaLookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.goalSelector.add(12, new PlayGoal(this));
        this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new PandaRevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.15000000596046448);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }
    
    public Gene getProductGene() {
        return getProductGene(this.getMainGene(), this.getHiddenGene());
    }
    
    public boolean isLazy() {
        return this.getProductGene() == Gene.b;
    }
    
    public boolean isWorried() {
        return this.getProductGene() == Gene.c;
    }
    
    public boolean isPlayful() {
        return this.getProductGene() == Gene.d;
    }
    
    public boolean isWeak() {
        return this.getProductGene() == Gene.f;
    }
    
    @Override
    public boolean isAttacking() {
        return this.getProductGene() == Gene.g;
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return false;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        this.playSound(SoundEvents.ht, 1.0f, 1.0f);
        if (!this.isAttacking()) {
            this.shouldAttack = true;
        }
        return super.tryAttack(entity);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.world.isThundering() && !this.isInsideWater()) {
                this.setScared(true);
                this.setEating(false);
            }
            else if (!this.isEating()) {
                this.setScared(false);
            }
        }
        if (this.getTarget() == null) {
            this.shouldGetRevenge = false;
            this.shouldAttack = false;
        }
        if (this.getAskForBambooTicks() > 0) {
            if (this.getTarget() != null) {
                this.lookAtEntity(this.getTarget(), 90.0f, 90.0f);
            }
            if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
                this.playSound(SoundEvents.hp, 1.0f, 1.0f);
            }
            this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
        }
        if (this.isSneezing()) {
            this.setSneezeProgress(this.getSneezeProgress() + 1);
            if (this.getSneezeProgress() > 20) {
                this.setSneezing(false);
                this.sneeze();
            }
            else if (this.getSneezeProgress() == 1) {
                this.playSound(SoundEvents.hj, 1.0f, 1.0f);
            }
        }
        if (this.isPlaying()) {
            this.updatePlaying();
        }
        else {
            this.playingTicks = 0;
        }
        if (this.isScared()) {
            this.pitch = 0.0f;
        }
        this.updateScaredAnimation();
        this.updateEatingAnimation();
        this.updateLieOnBackAnimation();
        this.updateRollOverAnimation();
    }
    
    public boolean eo() {
        return this.isWorried() && this.world.isThundering();
    }
    
    private void updateEatingAnimation() {
        if (!this.isEating() && this.isScared() && !this.eo() && !this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && this.random.nextInt(80) == 1) {
            this.setEating(true);
        }
        else if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() || !this.isScared()) {
            this.setEating(false);
        }
        if (this.isEating()) {
            this.playEatingAnimation();
            if (!this.world.isClient && this.getEatingTicks() > 80 && this.random.nextInt(20) == 1) {
                if (this.getEatingTicks() > 100 && this.canEat(this.getEquippedStack(EquipmentSlot.HAND_MAIN))) {
                    if (!this.world.isClient) {
                        this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                    }
                    this.setScared(false);
                }
                this.setEating(false);
                return;
            }
            this.setEatingTicks(this.getEatingTicks() + 1);
        }
    }
    
    private void playEatingAnimation() {
        if (this.getEatingTicks() % 5 == 0) {
            this.playSound(SoundEvents.hn, 0.5f + 0.5f * this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            for (int integer1 = 0; integer1 < 6; ++integer1) {
                Vec3d vec3d2 = new Vec3d((this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, (this.random.nextFloat() - 0.5) * 0.1);
                vec3d2 = vec3d2.rotateX(-this.pitch * 0.017453292f);
                vec3d2 = vec3d2.rotateY(-this.yaw * 0.017453292f);
                final double double3 = -this.random.nextFloat() * 0.6 - 0.3;
                Vec3d vec3d3 = new Vec3d((this.random.nextFloat() - 0.5) * 0.8, double3, 1.0 + (this.random.nextFloat() - 0.5) * 0.4);
                vec3d3 = vec3d3.rotateY(-this.aK * 0.017453292f);
                vec3d3 = vec3d3.add(this.x, this.y + this.getStandingEyeHeight() + 1.0, this.z);
                this.world.addParticle(new ItemStackParticleParameters(ParticleTypes.G, this.getEquippedStack(EquipmentSlot.HAND_MAIN)), vec3d3.x, vec3d3.y, vec3d3.z, vec3d2.x, vec3d2.y + 0.05, vec3d2.z);
            }
        }
    }
    
    private void updateScaredAnimation() {
        this.lastScaredAnimationProgress = this.scaredAnimationProgress;
        if (this.isScared()) {
            this.scaredAnimationProgress = Math.min(1.0f, this.scaredAnimationProgress + 0.15f);
        }
        else {
            this.scaredAnimationProgress = Math.max(0.0f, this.scaredAnimationProgress - 0.19f);
        }
    }
    
    private void updateLieOnBackAnimation() {
        this.lastLieOnBackAnimationProgress = this.lieOnBackAnimationProgress;
        if (this.isLyingOnBack()) {
            this.lieOnBackAnimationProgress = Math.min(1.0f, this.lieOnBackAnimationProgress + 0.15f);
        }
        else {
            this.lieOnBackAnimationProgress = Math.max(0.0f, this.lieOnBackAnimationProgress - 0.19f);
        }
    }
    
    private void updateRollOverAnimation() {
        this.lastRollOverAnimationProgress = this.rollOverAnimationProgress;
        if (this.isPlaying()) {
            this.rollOverAnimationProgress = Math.min(1.0f, this.rollOverAnimationProgress + 0.15f);
        }
        else {
            this.rollOverAnimationProgress = Math.max(0.0f, this.rollOverAnimationProgress - 0.19f);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float getScaredAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public float getLieOnBackAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public float getRollOverAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
    }
    
    private void updatePlaying() {
        ++this.playingTicks;
        if (this.playingTicks > 32) {
            this.setPlaying(false);
            return;
        }
        if (!this.world.isClient) {
            final Vec3d vec3d1 = this.getVelocity();
            if (this.playingTicks == 1) {
                final float float2 = this.yaw * 0.017453292f;
                final float float3 = this.isChild() ? 0.1f : 0.2f;
                this.playingJump = new Vec3d(vec3d1.x + -MathHelper.sin(float2) * float3, 0.0, vec3d1.z + MathHelper.cos(float2) * float3);
                this.setVelocity(this.playingJump.add(0.0, 0.27, 0.0));
            }
            else if (this.playingTicks == 7.0f || this.playingTicks == 15.0f || this.playingTicks == 23.0f) {
                this.setVelocity(0.0, this.onGround ? 0.27 : vec3d1.y, 0.0);
            }
            else {
                this.setVelocity(this.playingJump.x, vec3d1.y, this.playingJump.z);
            }
        }
    }
    
    private void sneeze() {
        final Vec3d vec3d1 = this.getVelocity();
        this.world.addParticle(ParticleTypes.R, this.x - (this.getWidth() + 1.0f) * 0.5 * MathHelper.sin(this.aK * 0.017453292f), this.y + this.getStandingEyeHeight() - 0.10000000149011612, this.z + (this.getWidth() + 1.0f) * 0.5 * MathHelper.cos(this.aK * 0.017453292f), vec3d1.x, 0.0, vec3d1.z);
        this.playSound(SoundEvents.hk, 1.0f, 1.0f);
        final List<PandaEntity> list2 = this.world.<PandaEntity>getEntities(PandaEntity.class, this.getBoundingBox().expand(10.0));
        for (final PandaEntity pandaEntity4 : list2) {
            if (!pandaEntity4.isChild() && pandaEntity4.onGround && !pandaEntity4.isInsideWater() && pandaEntity4.eq()) {
                pandaEntity4.jump();
            }
        }
        if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
            this.dropItem(Items.kT);
        }
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && PandaEntity.IS_FOOD.test(item)) {
            final ItemStack itemStack2 = item.getStack();
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack2);
            this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0f;
            this.sendPickup(item, itemStack2.getAmount());
            item.remove();
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        this.setScared(false);
        return super.damage(source, amount);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        this.setMainGene(Gene.createRandom(this.random));
        this.setHiddenGene(Gene.createRandom(this.random));
        this.resetAttributes();
        if (entityData instanceof SpawnData) {
            if (this.random.nextInt(5) == 0) {
                this.setBreedingAge(-24000);
            }
        }
        else {
            entityData = new SpawnData();
        }
        return entityData;
    }
    
    public void initGenes(final PandaEntity pandaEntity1, @Nullable final PandaEntity pandaEntity2) {
        if (pandaEntity2 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(pandaEntity1.getRandomGene());
                this.setHiddenGene(Gene.createRandom(this.random));
            }
            else {
                this.setMainGene(Gene.createRandom(this.random));
                this.setHiddenGene(pandaEntity1.getRandomGene());
            }
        }
        else if (this.random.nextBoolean()) {
            this.setMainGene(pandaEntity1.getRandomGene());
            this.setHiddenGene(pandaEntity2.getRandomGene());
        }
        else {
            this.setMainGene(pandaEntity2.getRandomGene());
            this.setHiddenGene(pandaEntity1.getRandomGene());
        }
        if (this.random.nextInt(32) == 0) {
            this.setMainGene(Gene.createRandom(this.random));
        }
        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(Gene.createRandom(this.random));
        }
    }
    
    private Gene getRandomGene() {
        if (this.random.nextBoolean()) {
            return this.getMainGene();
        }
        return this.getHiddenGene();
    }
    
    public void resetAttributes() {
        if (this.isWeak()) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07000000029802322);
        }
    }
    
    private void stop() {
        if (!this.isInsideWater()) {
            this.setForwardSpeed(0.0f);
            this.getNavigation().stop();
            this.setScared(true);
        }
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (this.eo()) {
            return false;
        }
        if (this.isLyingOnBack()) {
            this.setLyingOnBack(false);
            return true;
        }
        if (this.isBreedingItem(itemStack3)) {
            if (this.getTarget() != null) {
                this.shouldGetRevenge = true;
            }
            if (this.isChild()) {
                this.eat(player, itemStack3);
                this.growUp((int)(-this.getBreedingAge() / 20 * 0.1f), true);
            }
            else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
                this.eat(player, itemStack3);
                this.lovePlayer(player);
            }
            else {
                if (this.world.isClient || this.isScared() || this.isInsideWater()) {
                    return false;
                }
                this.stop();
                this.setEating(true);
                final ItemStack itemStack4 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
                if (!itemStack4.isEmpty() && !player.abilities.creativeMode) {
                    this.dropStack(itemStack4);
                }
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(itemStack3.getItem(), 1));
                this.eat(player, itemStack3);
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAttacking()) {
            return SoundEvents.hq;
        }
        if (this.isWorried()) {
            return SoundEvents.hr;
        }
        return SoundEvents.hl;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.ho, 0.15f, 1.0f);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return stack.getItem() == Blocks.kQ.getItem();
    }
    
    private boolean canEat(final ItemStack itemStack) {
        return this.isBreedingItem(itemStack) || itemStack.getItem() == Blocks.cP.getItem();
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.hm;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.hs;
    }
    
    public boolean eq() {
        return !this.isLyingOnBack() && !this.eo() && !this.isEating() && !this.isPlaying() && !this.isScared();
    }
    
    static {
        ASK_FOR_BAMBOO_TICKS = DataTracker.<Integer>registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SNEEZE_PROGRESS = DataTracker.<Integer>registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
        EATING_TICKS = DataTracker.<Integer>registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
        MAIN_GENE = DataTracker.<Byte>registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
        HIDDEN_GENE = DataTracker.<Byte>registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
        PANDA_FLAGS = DataTracker.<Byte>registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
        final Item item2;
        IS_FOOD = (itemEntity -> {
            item2 = itemEntity.getStack().getItem();
            return (item2 == Blocks.kQ.getItem() || item2 == Blocks.cP.getItem()) && itemEntity.isAlive() && !itemEntity.cannotPickup();
        });
    }
    
    public enum Gene
    {
        a(0, "normal", false), 
        b(1, "lazy", false), 
        c(2, "worried", false), 
        d(3, "playful", false), 
        e(4, "brown", true), 
        f(5, "weak", true), 
        g(6, "aggressive", false);
        
        private static final Gene[] VALUES;
        private final int id;
        private final String name;
        private final boolean recessive;
        
        private Gene(final int id, final String name, final boolean recessive) {
            this.id = id;
            this.name = name;
            this.recessive = recessive;
        }
        
        public int getId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean isRecessive() {
            return this.recessive;
        }
        
        private static Gene getProductGene(final Gene mainGene, final Gene hiddenGene) {
            if (!mainGene.isRecessive()) {
                return mainGene;
            }
            if (mainGene == hiddenGene) {
                return mainGene;
            }
            return Gene.a;
        }
        
        public static Gene byId(int integer) {
            if (integer < 0 || integer >= Gene.VALUES.length) {
                integer = 0;
            }
            return Gene.VALUES[integer];
        }
        
        public static Gene byName(final String string) {
            for (final Gene gene5 : values()) {
                if (gene5.name.equals(string)) {
                    return gene5;
                }
            }
            return Gene.a;
        }
        
        public static Gene createRandom(final Random random) {
            final int integer2 = random.nextInt(16);
            if (integer2 == 0) {
                return Gene.b;
            }
            if (integer2 == 1) {
                return Gene.c;
            }
            if (integer2 == 2) {
                return Gene.d;
            }
            if (integer2 == 4) {
                return Gene.g;
            }
            if (integer2 < 9) {
                return Gene.f;
            }
            if (integer2 < 11) {
                return Gene.e;
            }
            return Gene.a;
        }
        
        static {
            VALUES = Arrays.<Gene>stream(values()).sorted(Comparator.comparingInt(Gene::getId)).<Gene>toArray(Gene[]::new);
        }
    }
    
    static class PandaMoveControl extends MoveControl
    {
        private final PandaEntity panda;
        
        public PandaMoveControl(final PandaEntity pandaEntity) {
            super(pandaEntity);
            this.panda = pandaEntity;
        }
        
        @Override
        public void tick() {
            if (!this.panda.eq()) {
                return;
            }
            super.tick();
        }
    }
    
    static class SpawnData implements EntityData
    {
        private SpawnData() {
        }
    }
    
    static class AttackGoal extends MeleeAttackGoal
    {
        private final PandaEntity panda;
        
        public AttackGoal(final PandaEntity pandaEntity, final double double2, final boolean boolean4) {
            super(pandaEntity, double2, boolean4);
            this.panda = pandaEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.panda.eq() && super.canStart();
        }
    }
    
    static class PandaLookAtEntityGoal extends LookAtEntityGoal
    {
        private final PandaEntity panda;
        
        public PandaLookAtEntityGoal(final PandaEntity panda, final Class<? extends LivingEntity> class2, final float float3) {
            super(panda, class2, float3);
            this.panda = panda;
        }
        
        @Override
        public boolean canStart() {
            return this.panda.eq() && super.canStart();
        }
    }
    
    static class PlayGoal extends Goal
    {
        private final PandaEntity panda;
        
        public PlayGoal(final PandaEntity panda) {
            this.panda = panda;
            this.setControls(EnumSet.<Control>of(Control.a, Control.b, Control.c));
        }
        
        @Override
        public boolean canStart() {
            if ((!this.panda.isChild() && !this.panda.isPlayful()) || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.eq()) {
                return false;
            }
            final float float1 = this.panda.yaw * 0.017453292f;
            int integer2 = 0;
            int integer3 = 0;
            final float float2 = -MathHelper.sin(float1);
            final float float3 = MathHelper.cos(float1);
            if (Math.abs(float2) > 0.5) {
                integer2 += (int)(float2 / Math.abs(float2));
            }
            if (Math.abs(float3) > 0.5) {
                integer3 += (int)(float3 / Math.abs(float3));
            }
            return this.panda.world.getBlockState(new BlockPos(this.panda).add(integer2, -1, integer3)).isAir() || (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1) || this.panda.random.nextInt(500) == 1;
        }
        
        @Override
        public boolean shouldContinue() {
            return false;
        }
        
        @Override
        public void start() {
            this.panda.setPlaying(true);
        }
        
        @Override
        public boolean canStop() {
            return false;
        }
    }
    
    static class SneezeGoal extends Goal
    {
        private final PandaEntity panda;
        
        public SneezeGoal(final PandaEntity panda) {
            this.panda = panda;
        }
        
        @Override
        public boolean canStart() {
            return this.panda.isChild() && this.panda.eq() && ((this.panda.isWeak() && this.panda.random.nextInt(500) == 1) || this.panda.random.nextInt(6000) == 1);
        }
        
        @Override
        public boolean shouldContinue() {
            return false;
        }
        
        @Override
        public void start() {
            this.panda.setSneezing(true);
        }
    }
    
    static class PandaMateGoal extends AnimalMateGoal
    {
        private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
        private final PandaEntity panda;
        private int nextAskPlayerForBambooAge;
        
        public PandaMateGoal(final PandaEntity panda, final double chance) {
            super(panda, chance);
            this.panda = panda;
        }
        
        @Override
        public boolean canStart() {
            if (!super.canStart() || this.panda.getAskForBambooTicks() != 0) {
                return false;
            }
            if (!this.isBambooClose()) {
                if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                    this.panda.setAskForBambooTicks(32);
                    this.nextAskPlayerForBambooAge = this.panda.age + 600;
                    if (this.panda.canMoveVoluntarily()) {
                        final PlayerEntity playerEntity1 = this.world.getClosestPlayer(PandaMateGoal.CLOSE_PLAYER_PREDICATE, this.panda);
                        this.panda.setTarget(playerEntity1);
                    }
                }
                return false;
            }
            return true;
        }
        
        private boolean isBambooClose() {
            final BlockPos blockPos1 = new BlockPos(this.panda);
            final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
            for (int integer3 = 0; integer3 < 3; ++integer3) {
                for (int integer4 = 0; integer4 < 8; ++integer4) {
                    for (int integer5 = 0; integer5 <= integer4; integer5 = ((integer5 > 0) ? (-integer5) : (1 - integer5))) {
                        for (int integer6 = (integer5 < integer4 && integer5 > -integer4) ? integer4 : 0; integer6 <= integer4; integer6 = ((integer6 > 0) ? (-integer6) : (1 - integer6))) {
                            mutable2.set(blockPos1).setOffset(integer5, integer3, integer6);
                            if (this.world.getBlockState(mutable2).getBlock() == Blocks.kQ) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        static {
            CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
        }
    }
    
    static class PandaFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
    {
        private final PandaEntity owner;
        
        public PandaFleeGoal(final PandaEntity pandaEntity, final Class<T> class2, final float float3, final double double4, final double double6) {
            super(pandaEntity, class2, float3, double4, double6, EntityPredicates.EXCEPT_SPECTATOR::test);
            this.owner = pandaEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.owner.isWorried() && this.owner.eq() && super.canStart();
        }
    }
    
    class PickUpFoodGoal extends Goal
    {
        private int startAge;
        
        public PickUpFoodGoal() {
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            if (this.startAge > PandaEntity.this.age || PandaEntity.this.isChild() || PandaEntity.this.isInsideWater() || !PandaEntity.this.eq() || PandaEntity.this.getAskForBambooTicks() > 0) {
                return false;
            }
            final List<ItemEntity> list1 = PandaEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity.IS_FOOD);
            return !list1.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
        }
        
        @Override
        public boolean shouldContinue() {
            return !PandaEntity.this.isInsideWater() && (PandaEntity.this.isLazy() || PandaEntity.this.random.nextInt(600) != 1) && PandaEntity.this.random.nextInt(2000) != 1;
        }
        
        @Override
        public void tick() {
            if (!PandaEntity.this.isScared() && !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
                PandaEntity.this.stop();
            }
        }
        
        @Override
        public void start() {
            final List<ItemEntity> list1 = PandaEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity.IS_FOOD);
            if (!list1.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
                PandaEntity.this.getNavigation().startMovingTo(list1.get(0), 1.2000000476837158);
            }
            else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
                PandaEntity.this.stop();
            }
            this.startAge = 0;
        }
        
        @Override
        public void stop() {
            final ItemStack itemStack1 = PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (!itemStack1.isEmpty()) {
                PandaEntity.this.dropStack(itemStack1);
                PandaEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                final int integer2 = PandaEntity.this.isLazy() ? (PandaEntity.this.random.nextInt(50) + 10) : (PandaEntity.this.random.nextInt(150) + 10);
                this.startAge = PandaEntity.this.age + integer2 * 20;
            }
            PandaEntity.this.setScared(false);
        }
    }
    
    static class LieOnBackGoal extends Goal
    {
        private final PandaEntity panda;
        private int nextLieOnBackAge;
        
        public LieOnBackGoal(final PandaEntity pandaEntity) {
            this.panda = pandaEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.eq() && this.panda.random.nextInt(400) == 1;
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.panda.isInsideWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1) && this.panda.random.nextInt(2000) != 1;
        }
        
        @Override
        public void start() {
            this.panda.setLyingOnBack(true);
            this.nextLieOnBackAge = 0;
        }
        
        @Override
        public void stop() {
            this.panda.setLyingOnBack(false);
            this.nextLieOnBackAge = this.panda.age + 200;
        }
    }
    
    static class PandaRevengeGoal extends RevengeGoal
    {
        private final PandaEntity panda;
        
        public PandaRevengeGoal(final PandaEntity panda, final Class<?>... arr) {
            super(panda, arr);
            this.panda = panda;
        }
        
        @Override
        public boolean shouldContinue() {
            if (this.panda.shouldGetRevenge || this.panda.shouldAttack) {
                this.panda.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }
        
        @Override
        protected void setMobEntityTarget(final MobEntity mobEntity, final LivingEntity livingEntity) {
            if (mobEntity instanceof PandaEntity && ((PandaEntity)mobEntity).isAttacking()) {
                mobEntity.setTarget(livingEntity);
            }
        }
    }
    
    static class ExtinguishFireGoal extends EscapeDangerGoal
    {
        private final PandaEntity panda;
        
        public ExtinguishFireGoal(final PandaEntity pandaEntity, final double double2) {
            super(pandaEntity, double2);
            this.panda = pandaEntity;
        }
        
        @Override
        public boolean canStart() {
            if (!this.panda.isOnFire()) {
                return false;
            }
            final BlockPos blockPos1 = this.locateClosestWater(this.owner.world, this.owner, 5, 4);
            if (blockPos1 != null) {
                this.targetX = blockPos1.getX();
                this.targetY = blockPos1.getY();
                this.targetZ = blockPos1.getZ();
                return true;
            }
            return this.findTarget();
        }
        
        @Override
        public boolean shouldContinue() {
            if (this.panda.isScared()) {
                this.panda.getNavigation().stop();
                return false;
            }
            return super.shouldContinue();
        }
    }
}
