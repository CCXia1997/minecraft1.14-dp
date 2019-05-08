package net.minecraft.entity.passive;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.Items;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.BlazeEntity;
import java.util.HashMap;
import net.minecraft.sound.SoundCategory;
import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.ParrotClimbOntoPlayerGoal;
import net.minecraft.entity.ai.goal.FlyAroundGoal;
import net.minecraft.entity.ai.goal.FlyToOwnerGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.ai.control.ParrotMoveControl;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityType;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.entity.mob.MobEntity;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Bird;

public class ParrotEntity extends TameableShoulderEntity implements Bird
{
    private static final TrackedData<Integer> ATTR_VARIANT;
    private static final Predicate<MobEntity> CAN_IMITATE;
    private static final Item COOKIE;
    private static final Set<Item> TAMING_INGREDIENTS;
    private static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS;
    public float bD;
    public float bE;
    public float bF;
    public float bG;
    public float bH;
    private boolean songPlaying;
    private BlockPos songSource;
    
    public ParrotEntity(final EntityType<? extends ParrotEntity> type, final World world) {
        super(type, world);
        this.bH = 1.0f;
        this.moveControl = new ParrotMoveControl(this);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setVariant(this.random.nextInt(5));
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(2, new FlyToOwnerGoal(this, 1.0, 5.0f, 1.0f));
        this.goalSelector.add(2, new FlyAroundGoal(this, 1.0));
        this.goalSelector.add(3, new ParrotClimbOntoPlayerGoal(this));
        this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0f, 7.0f));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.FLYING_SPEED);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.FLYING_SPEED).setBaseValue(0.4000000059604645);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        final BirdNavigation birdNavigation2 = new BirdNavigation(this, world);
        birdNavigation2.setCanPathThroughDoors(false);
        birdNavigation2.setCanSwim(true);
        birdNavigation2.setCanEnterOpenDoors(true);
        return birdNavigation2;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.6f;
    }
    
    @Override
    public void updateState() {
        imitateNearbyMob(this.world, this);
        if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || this.world.getBlockState(this.songSource).getBlock() != Blocks.cG) {
            this.songPlaying = false;
            this.songSource = null;
        }
        super.updateState();
        this.ei();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setNearbySongPlaying(final BlockPos songSource, final boolean playing) {
        this.songSource = songSource;
        this.songPlaying = playing;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean getSongPlaying() {
        return this.songPlaying;
    }
    
    private void ei() {
        this.bG = this.bD;
        this.bF = this.bE;
        this.bE += (float)((this.onGround ? -1 : 4) * 0.3);
        this.bE = MathHelper.clamp(this.bE, 0.0f, 1.0f);
        if (!this.onGround && this.bH < 1.0f) {
            this.bH = 1.0f;
        }
        this.bH *= (float)0.9;
        final Vec3d vec3d1 = this.getVelocity();
        if (!this.onGround && vec3d1.y < 0.0) {
            this.setVelocity(vec3d1.multiply(1.0, 0.6, 1.0));
        }
        this.bD += this.bH * 2.0f;
    }
    
    private static boolean imitateNearbyMob(final World world, final Entity parrot) {
        if (!parrot.isAlive() || parrot.isSilent() || world.random.nextInt(50) != 0) {
            return false;
        }
        final List<MobEntity> list3 = world.<MobEntity>getEntities(MobEntity.class, parrot.getBoundingBox().expand(20.0), ParrotEntity.CAN_IMITATE);
        if (!list3.isEmpty()) {
            final MobEntity mobEntity4 = list3.get(world.random.nextInt(list3.size()));
            if (!mobEntity4.isSilent()) {
                final SoundEvent soundEvent5 = getSound(mobEntity4.getType());
                world.playSound(null, parrot.x, parrot.y, parrot.z, soundEvent5, parrot.getSoundCategory(), 0.7f, getSoundPitch(world.random));
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (!this.isTamed() && ParrotEntity.TAMING_INGREDIENTS.contains(itemStack3.getItem())) {
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            if (!this.isSilent()) {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.hw, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            if (!this.world.isClient) {
                if (this.random.nextInt(10) == 0) {
                    this.setOwner(player);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)7);
                }
                else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            return true;
        }
        if (itemStack3.getItem() == ParrotEntity.COOKIE) {
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            this.addPotionEffect(new StatusEffectInstance(StatusEffects.s, 900));
            if (player.isCreative() || !this.isInvulnerable()) {
                this.damage(DamageSource.player(player), Float.MAX_VALUE);
            }
            return true;
        }
        if (!this.world.isClient && !this.isInAir() && this.isTamed() && this.isOwner(player)) {
            this.sitGoal.setEnabledWithOwner(!this.isSitting());
        }
        return super.interactMob(player, hand);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final int integer3 = MathHelper.floor(this.x);
        final int integer4 = MathHelper.floor(this.getBoundingBox().minY);
        final int integer5 = MathHelper.floor(this.z);
        final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
        final Block block7 = iWorld.getBlockState(blockPos6.down()).getBlock();
        return block7.matches(BlockTags.C) || block7 == Blocks.aQ || block7 instanceof LogBlock || (block7 == Blocks.AIR && super.canSpawn(iWorld, spawnType));
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        return false;
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return null;
    }
    
    public static void playMobSound(final World world, final Entity parrot) {
        if (!parrot.isSilent() && !imitateNearbyMob(world, parrot) && world.random.nextInt(200) == 0) {
            world.playSound(null, parrot.x, parrot.y, parrot.z, getRandomSound(world.random), parrot.getSoundCategory(), 1.0f, getSoundPitch(world.random));
        }
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        return entity.damage(DamageSource.mob(this), 3.0f);
    }
    
    @Nullable
    public SoundEvent getAmbientSound() {
        return getRandomSound(this.random);
    }
    
    private static SoundEvent getRandomSound(final Random random) {
        if (random.nextInt(1000) == 0) {
            final List<EntityType<?>> list2 = Lists.newArrayList(ParrotEntity.MOB_SOUNDS.keySet());
            return getSound(list2.get(random.nextInt(list2.size())));
        }
        return SoundEvents.hu;
    }
    
    public static SoundEvent getSound(final EntityType<?> imitate) {
        return ParrotEntity.MOB_SOUNDS.getOrDefault(imitate, SoundEvents.hu);
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.hy;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.hv;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.ig, 0.15f, 1.0f);
    }
    
    @Override
    protected float e(final float float1) {
        this.playSound(SoundEvents.hx, 0.15f, 1.0f);
        return float1 + this.bE / 2.0f;
    }
    
    @Override
    protected boolean al() {
        return true;
    }
    
    @Override
    protected float getSoundPitch() {
        return getSoundPitch(this.random);
    }
    
    private static float getSoundPitch(final Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.g;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    protected void pushAway(final Entity entity) {
        if (entity instanceof PlayerEntity) {
            return;
        }
        super.pushAway(entity);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.sitGoal != null) {
            this.sitGoal.setEnabledWithOwner(false);
        }
        return super.damage(source, amount);
    }
    
    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.<Integer>get(ParrotEntity.ATTR_VARIANT), 0, 4);
    }
    
    public void setVariant(final int integer) {
        this.dataTracker.<Integer>set(ParrotEntity.ATTR_VARIANT, integer);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(ParrotEntity.ATTR_VARIANT, 0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
    }
    
    public boolean isInAir() {
        return !this.onGround;
    }
    
    static {
        ATTR_VARIANT = DataTracker.<Integer>registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAN_IMITATE = new Predicate<MobEntity>() {
            public boolean a(@Nullable final MobEntity mobEntity) {
                return mobEntity != null && ParrotEntity.MOB_SOUNDS.containsKey(mobEntity.getType());
            }
        };
        COOKIE = Items.lU;
        TAMING_INGREDIENTS = Sets.<Item>newHashSet(Items.jO, Items.ma, Items.lZ, Items.oP);
        MOB_SOUNDS = SystemUtil.<Map<EntityType<?>, SoundEvent>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(EntityType.BLAZE, SoundEvents.hz);
            hashMap.put((EntityType<BlazeEntity>)EntityType.CAVE_SPIDER, SoundEvents.hV);
            hashMap.put((EntityType<BlazeEntity>)EntityType.CREEPER, SoundEvents.hA);
            hashMap.put((EntityType<BlazeEntity>)EntityType.DROWNED, SoundEvents.hB);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ELDER_GUARDIAN, SoundEvents.hC);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ENDER_DRAGON, SoundEvents.hD);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ENDERMAN, SoundEvents.hE);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ENDERMITE, SoundEvents.hF);
            hashMap.put((EntityType<BlazeEntity>)EntityType.EVOKER, SoundEvents.hG);
            hashMap.put((EntityType<BlazeEntity>)EntityType.GHAST, SoundEvents.hH);
            hashMap.put((EntityType<BlazeEntity>)EntityType.GUARDIAN, SoundEvents.hI);
            hashMap.put((EntityType<BlazeEntity>)EntityType.HUSK, SoundEvents.hJ);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ILLUSIONER, SoundEvents.hK);
            hashMap.put((EntityType<BlazeEntity>)EntityType.MAGMA_CUBE, SoundEvents.hL);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ZOMBIE_PIGMAN, SoundEvents.ie);
            hashMap.put((EntityType<BlazeEntity>)EntityType.PANDA, SoundEvents.hM);
            hashMap.put((EntityType<BlazeEntity>)EntityType.PHANTOM, SoundEvents.hN);
            hashMap.put((EntityType<BlazeEntity>)EntityType.PILLAGER, SoundEvents.hO);
            hashMap.put((EntityType<BlazeEntity>)EntityType.POLAR_BEAR, SoundEvents.hP);
            hashMap.put((EntityType<BlazeEntity>)EntityType.RAVAGER, SoundEvents.hQ);
            hashMap.put((EntityType<BlazeEntity>)EntityType.SHULKER, SoundEvents.hR);
            hashMap.put((EntityType<BlazeEntity>)EntityType.SILVERFISH, SoundEvents.hS);
            hashMap.put((EntityType<BlazeEntity>)EntityType.SKELETON, SoundEvents.hT);
            hashMap.put((EntityType<BlazeEntity>)EntityType.SLIME, SoundEvents.hU);
            hashMap.put((EntityType<BlazeEntity>)EntityType.SPIDER, SoundEvents.hV);
            hashMap.put((EntityType<BlazeEntity>)EntityType.STRAY, SoundEvents.hW);
            hashMap.put((EntityType<BlazeEntity>)EntityType.VEX, SoundEvents.hX);
            hashMap.put((EntityType<BlazeEntity>)EntityType.VINDICATOR, SoundEvents.hY);
            hashMap.put((EntityType<BlazeEntity>)EntityType.WITCH, SoundEvents.hZ);
            hashMap.put((EntityType<BlazeEntity>)EntityType.WITHER, SoundEvents.ia);
            hashMap.put((EntityType<BlazeEntity>)EntityType.WITHER_SKELETON, SoundEvents.ib);
            hashMap.put((EntityType<BlazeEntity>)EntityType.WOLF, SoundEvents.ic);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ZOMBIE, SoundEvents.id);
            hashMap.put((EntityType<BlazeEntity>)EntityType.ZOMBIE_VILLAGER, SoundEvents.if_);
        });
    }
}
