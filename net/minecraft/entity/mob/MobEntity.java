package net.minecraft.entity.mob;

import java.util.AbstractList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.AxeItem;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.Hand;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.EntityData;
import net.minecraft.item.Items;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.BlockView;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.EquipmentSlot;
import java.util.List;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.loot.context.LootContext;
import java.util.UUID;
import java.util.Iterator;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.vehicle.BoatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import java.util.Arrays;
import com.google.common.collect.Maps;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.entity.ai.pathing.PathNodeType;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.LivingEntity;

public abstract class MobEntity extends LivingEntity
{
    private static final TrackedData<Byte> MOB_FLAGS;
    public int ambientSoundChance;
    protected int experiencePoints;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyControl bodyControl;
    protected EntityNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final MobVisibilityCache visibilityCache;
    private final DefaultedList<ItemStack> handItems;
    protected final float[] handDropChances;
    private final DefaultedList<ItemStack> armorItems;
    protected final float[] armorDropChances;
    private boolean pickUpLoot;
    private boolean persistent;
    private final Map<PathNodeType, Float> pathNodeTypeWeights;
    private Identifier lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private CompoundTag leashTag;
    private BlockPos walkTarget;
    private float walkTargetRange;
    
    protected MobEntity(final EntityType<? extends MobEntity> type, final World world) {
        super(type, world);
        this.handItems = DefaultedList.<ItemStack>create(2, ItemStack.EMPTY);
        this.handDropChances = new float[2];
        this.armorItems = DefaultedList.<ItemStack>create(4, ItemStack.EMPTY);
        this.armorDropChances = new float[4];
        this.pathNodeTypeWeights = Maps.newEnumMap(PathNodeType.class);
        this.walkTarget = BlockPos.ORIGIN;
        this.walkTargetRange = -1.0f;
        this.goalSelector = new GoalSelector((world == null || world.getProfiler() == null) ? null : world.getProfiler());
        this.targetSelector = new GoalSelector((world == null || world.getProfiler() == null) ? null : world.getProfiler());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyControl = this.createBodyControl();
        this.navigation = this.createNavigation(world);
        this.visibilityCache = new MobVisibilityCache(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (world != null && !world.isClient) {
            this.initGoals();
        }
    }
    
    protected void initGoals() {
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_KNOCKBACK);
    }
    
    protected EntityNavigation createNavigation(final World world) {
        return new MobNavigation(this, world);
    }
    
    public float getPathNodeTypeWeight(final PathNodeType pathNodeType) {
        final Float float2 = this.pathNodeTypeWeights.get(pathNodeType);
        return (float2 == null) ? pathNodeType.getWeight() : float2;
    }
    
    public void setPathNodeTypeWeight(final PathNodeType type, final float float2) {
        this.pathNodeTypeWeights.put(type, float2);
    }
    
    protected BodyControl createBodyControl() {
        return new BodyControl(this);
    }
    
    public LookControl getLookControl() {
        return this.lookControl;
    }
    
    public MoveControl getMoveControl() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            final MobEntity mobEntity1 = (MobEntity)this.getVehicle();
            return mobEntity1.getMoveControl();
        }
        return this.moveControl;
    }
    
    public JumpControl getJumpControl() {
        return this.jumpControl;
    }
    
    public EntityNavigation getNavigation() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            final MobEntity mobEntity1 = (MobEntity)this.getVehicle();
            return mobEntity1.getNavigation();
        }
        return this.navigation;
    }
    
    public MobVisibilityCache getVisibilityCache() {
        return this.visibilityCache;
    }
    
    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }
    
    public void setTarget(@Nullable final LivingEntity target) {
        this.target = target;
    }
    
    @Override
    public boolean canTarget(final EntityType<?> entityType) {
        return entityType != EntityType.GHAST;
    }
    
    public void onEatingGrass() {
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(MobEntity.MOB_FLAGS, (Byte)0);
    }
    
    public int getMinAmbientSoundDelay() {
        return 80;
    }
    
    public void playAmbientSound() {
        final SoundEvent soundEvent1 = this.getAmbientSound();
        if (soundEvent1 != null) {
            this.playSound(soundEvent1, this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    @Override
    public void baseTick() {
        super.baseTick();
        this.world.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
            this.resetSoundDelay();
            this.playAmbientSound();
        }
        this.world.getProfiler().pop();
    }
    
    @Override
    protected void playHurtSound(final DamageSource damageSource) {
        this.resetSoundDelay();
        super.playHurtSound(damageSource);
    }
    
    private void resetSoundDelay() {
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        if (this.experiencePoints > 0) {
            int integer2 = this.experiencePoints;
            for (int integer3 = 0; integer3 < this.armorItems.size(); ++integer3) {
                if (!this.armorItems.get(integer3).isEmpty() && this.armorDropChances[integer3] <= 1.0f) {
                    integer2 += 1 + this.random.nextInt(3);
                }
            }
            for (int integer3 = 0; integer3 < this.handItems.size(); ++integer3) {
                if (!this.handItems.get(integer3).isEmpty() && this.handDropChances[integer3] <= 1.0f) {
                    integer2 += 1 + this.random.nextInt(3);
                }
            }
            return integer2;
        }
        return this.experiencePoints;
    }
    
    public void playSpawnEffects() {
        if (this.world.isClient) {
            for (int integer1 = 0; integer1 < 20; ++integer1) {
                final double double2 = this.random.nextGaussian() * 0.02;
                final double double3 = this.random.nextGaussian() * 0.02;
                final double double4 = this.random.nextGaussian() * 0.02;
                final double double5 = 10.0;
                this.world.addParticle(ParticleTypes.N, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth() - double2 * 10.0, this.y + this.random.nextFloat() * this.getHeight() - double3 * 10.0, this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth() - double4 * 10.0, double2, double3, double4);
            }
        }
        else {
            this.world.sendEntityStatus(this, (byte)20);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 20) {
            this.playSpawnEffects();
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.updateLeash();
            if (this.age % 5 == 0) {
                this.F();
            }
        }
    }
    
    protected void F() {
        final boolean boolean1 = !(this.getPrimaryPassenger() instanceof MobEntity);
        final boolean boolean2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.a, boolean1);
        this.goalSelector.setControlEnabled(Goal.Control.c, boolean1 && boolean2);
        this.goalSelector.setControlEnabled(Goal.Control.b, boolean1);
    }
    
    @Override
    protected float e(final float yaw, final float float2) {
        this.bodyControl.a();
        return float2;
    }
    
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        tag.putBoolean("PersistenceRequired", this.persistent);
        final ListTag listTag2 = new ListTag();
        for (final ItemStack itemStack4 : this.armorItems) {
            final CompoundTag compoundTag5 = new CompoundTag();
            if (!itemStack4.isEmpty()) {
                itemStack4.toTag(compoundTag5);
            }
            ((AbstractList<CompoundTag>)listTag2).add(compoundTag5);
        }
        tag.put("ArmorItems", listTag2);
        final ListTag listTag3 = new ListTag();
        for (final ItemStack itemStack5 : this.handItems) {
            final CompoundTag compoundTag6 = new CompoundTag();
            if (!itemStack5.isEmpty()) {
                itemStack5.toTag(compoundTag6);
            }
            ((AbstractList<CompoundTag>)listTag3).add(compoundTag6);
        }
        tag.put("HandItems", listTag3);
        final ListTag listTag4 = new ListTag();
        for (final float float8 : this.armorDropChances) {
            ((AbstractList<FloatTag>)listTag4).add(new FloatTag(float8));
        }
        tag.put("ArmorDropChances", listTag4);
        final ListTag listTag5 = new ListTag();
        for (final float float9 : this.handDropChances) {
            ((AbstractList<FloatTag>)listTag5).add(new FloatTag(float9));
        }
        tag.put("HandDropChances", listTag5);
        if (this.holdingEntity != null) {
            final CompoundTag compoundTag6 = new CompoundTag();
            if (this.holdingEntity instanceof LivingEntity) {
                final UUID uUID7 = this.holdingEntity.getUuid();
                compoundTag6.putUuid("UUID", uUID7);
            }
            else if (this.holdingEntity instanceof AbstractDecorationEntity) {
                final BlockPos blockPos7 = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
                compoundTag6.putInt("X", blockPos7.getX());
                compoundTag6.putInt("Y", blockPos7.getY());
                compoundTag6.putInt("Z", blockPos7.getZ());
            }
            tag.put("Leash", compoundTag6);
        }
        tag.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            tag.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isAiDisabled()) {
            tag.putBoolean("NoAI", this.isAiDisabled());
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(tag.getBoolean("CanPickUpLoot"));
        }
        this.persistent = tag.getBoolean("PersistenceRequired");
        if (tag.containsKey("ArmorItems", 9)) {
            final ListTag listTag2 = tag.getList("ArmorItems", 10);
            for (int integer3 = 0; integer3 < this.armorItems.size(); ++integer3) {
                this.armorItems.set(integer3, ItemStack.fromTag(listTag2.getCompoundTag(integer3)));
            }
        }
        if (tag.containsKey("HandItems", 9)) {
            final ListTag listTag2 = tag.getList("HandItems", 10);
            for (int integer3 = 0; integer3 < this.handItems.size(); ++integer3) {
                this.handItems.set(integer3, ItemStack.fromTag(listTag2.getCompoundTag(integer3)));
            }
        }
        if (tag.containsKey("ArmorDropChances", 9)) {
            final ListTag listTag2 = tag.getList("ArmorDropChances", 5);
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                this.armorDropChances[integer3] = listTag2.getFloat(integer3);
            }
        }
        if (tag.containsKey("HandDropChances", 9)) {
            final ListTag listTag2 = tag.getList("HandDropChances", 5);
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                this.handDropChances[integer3] = listTag2.getFloat(integer3);
            }
        }
        if (tag.containsKey("Leash", 10)) {
            this.leashTag = tag.getCompound("Leash");
        }
        this.setLeftHanded(tag.getBoolean("LeftHanded"));
        if (tag.containsKey("DeathLootTable", 8)) {
            this.lootTable = new Identifier(tag.getString("DeathLootTable"));
            this.lootTableSeed = tag.getLong("DeathLootTableSeed");
        }
        this.setAiDisabled(tag.getBoolean("NoAI"));
    }
    
    @Override
    protected void dropLoot(final DamageSource source, final boolean killedByPlayer) {
        super.dropLoot(source, killedByPlayer);
        this.lootTable = null;
    }
    
    @Override
    protected LootContext.Builder getLootContextBuilder(final boolean killedByPlayer, final DamageSource damageSource) {
        return super.getLootContextBuilder(killedByPlayer, damageSource).setRandom(this.lootTableSeed, this.random);
    }
    
    @Override
    public final Identifier getLootTable() {
        return (this.lootTable == null) ? this.getLootTableId() : this.lootTable;
    }
    
    protected Identifier getLootTableId() {
        return super.getLootTable();
    }
    
    public void setForwardSpeed(final float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }
    
    public void setUpwardSpeed(final float upwardSpeed) {
        this.upwardSpeed = upwardSpeed;
    }
    
    public void setSidewaysSpeed(final float sidewaysMovement) {
        this.sidewaysSpeed = sidewaysMovement;
    }
    
    @Override
    public void setMovementSpeed(final float movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        this.setForwardSpeed(movementSpeed);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        this.world.getProfiler().push("looting");
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
            final List<ItemEntity> list1 = this.world.<ItemEntity>getEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0));
            for (final ItemEntity itemEntity3 : list1) {
                if (!itemEntity3.removed && !itemEntity3.getStack().isEmpty()) {
                    if (itemEntity3.cannotPickup()) {
                        continue;
                    }
                    this.loot(itemEntity3);
                }
            }
        }
        this.world.getProfiler().pop();
    }
    
    protected void loot(final ItemEntity item) {
        final ItemStack itemStack2 = item.getStack();
        final EquipmentSlot equipmentSlot3 = getPreferredEquipmentSlot(itemStack2);
        final ItemStack itemStack3 = this.getEquippedStack(equipmentSlot3);
        final boolean boolean5 = this.isBetterItemFor(itemStack2, itemStack3, equipmentSlot3);
        if (boolean5 && this.canPickupItem(itemStack2)) {
            final double double6 = this.getDropChance(equipmentSlot3);
            if (!itemStack3.isEmpty() && this.random.nextFloat() - 0.1f < double6) {
                this.dropStack(itemStack3);
            }
            this.setEquippedStack(equipmentSlot3, itemStack2);
            switch (equipmentSlot3.getType()) {
                case HAND: {
                    this.handDropChances[equipmentSlot3.getEntitySlotId()] = 2.0f;
                    break;
                }
                case ARMOR: {
                    this.armorDropChances[equipmentSlot3.getEntitySlotId()] = 2.0f;
                    break;
                }
            }
            this.persistent = true;
            this.sendPickup(item, itemStack2.getAmount());
            item.remove();
        }
    }
    
    protected boolean isBetterItemFor(final ItemStack current, final ItemStack previous, final EquipmentSlot slot) {
        boolean boolean4 = true;
        if (!previous.isEmpty()) {
            if (slot.getType() == EquipmentSlot.Type.HAND) {
                if (current.getItem() instanceof SwordItem && !(previous.getItem() instanceof SwordItem)) {
                    boolean4 = true;
                }
                else if (current.getItem() instanceof SwordItem && previous.getItem() instanceof SwordItem) {
                    final SwordItem swordItem5 = (SwordItem)current.getItem();
                    final SwordItem swordItem6 = (SwordItem)previous.getItem();
                    if (swordItem5.getWeaponDamage() == swordItem6.getWeaponDamage()) {
                        boolean4 = (current.getDamage() < previous.getDamage() || (current.hasTag() && !previous.hasTag()));
                    }
                    else {
                        boolean4 = (swordItem5.getWeaponDamage() > swordItem6.getWeaponDamage());
                    }
                }
                else {
                    boolean4 = (current.getItem() instanceof BowItem && previous.getItem() instanceof BowItem && current.hasTag() && !previous.hasTag());
                }
            }
            else if (current.getItem() instanceof ArmorItem && !(previous.getItem() instanceof ArmorItem)) {
                boolean4 = true;
            }
            else if (current.getItem() instanceof ArmorItem && previous.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(previous)) {
                final ArmorItem armorItem5 = (ArmorItem)current.getItem();
                final ArmorItem armorItem6 = (ArmorItem)previous.getItem();
                if (armorItem5.getProtection() == armorItem6.getProtection()) {
                    boolean4 = (current.getDamage() < previous.getDamage() || (current.hasTag() && !previous.hasTag()));
                }
                else {
                    boolean4 = (armorItem5.getProtection() > armorItem6.getProtection());
                }
            }
            else {
                boolean4 = false;
            }
        }
        return boolean4;
    }
    
    protected boolean canPickupItem(final ItemStack itemStack) {
        return true;
    }
    
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return true;
    }
    
    protected boolean cannotDespawn() {
        return false;
    }
    
    protected void checkDespawn() {
        if (this.isPersistent() || this.cannotDespawn()) {
            this.despawnCounter = 0;
            return;
        }
        final Entity entity1 = this.world.getClosestPlayer(this, -1.0);
        if (entity1 != null) {
            final double double2 = entity1.squaredDistanceTo(this);
            if (double2 > 16384.0 && this.canImmediatelyDespawn(double2)) {
                this.remove();
            }
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && double2 > 1024.0 && this.canImmediatelyDespawn(double2)) {
                this.remove();
            }
            else if (double2 < 1024.0) {
                this.despawnCounter = 0;
            }
        }
    }
    
    @Override
    protected final void tickNewAi() {
        ++this.despawnCounter;
        this.world.getProfiler().push("checkDespawn");
        this.checkDespawn();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("sensing");
        this.visibilityCache.clear();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("navigation");
        this.navigation.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("mob tick");
        this.mobTick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("controls");
        this.world.getProfiler().push("move");
        this.moveControl.tick();
        this.world.getProfiler().swap("look");
        this.lookControl.tick();
        this.world.getProfiler().swap("jump");
        this.jumpControl.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().pop();
        this.sendAiDebugData();
    }
    
    protected void sendAiDebugData() {
        DebugRendererInfoManager.sendGoalSelector(this.world, this, this.goalSelector);
    }
    
    protected void mobTick() {
    }
    
    public int getLookPitchSpeed() {
        return 40;
    }
    
    public int dA() {
        return 75;
    }
    
    public int getLookYawSpeed() {
        return 10;
    }
    
    public void lookAtEntity(final Entity targetEntity, final float maxYawChange, final float maxPitchChange) {
        final double double4 = targetEntity.x - this.x;
        final double double5 = targetEntity.z - this.z;
        double double6;
        if (targetEntity instanceof LivingEntity) {
            final LivingEntity livingEntity10 = (LivingEntity)targetEntity;
            double6 = livingEntity10.y + livingEntity10.getStandingEyeHeight() - (this.y + this.getStandingEyeHeight());
        }
        else {
            double6 = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - (this.y + this.getStandingEyeHeight());
        }
        final double double7 = MathHelper.sqrt(double4 * double4 + double5 * double5);
        final float float12 = (float)(MathHelper.atan2(double5, double4) * 57.2957763671875) - 90.0f;
        final float float13 = (float)(-(MathHelper.atan2(double6, double7) * 57.2957763671875));
        this.pitch = this.changeAngle(this.pitch, float13, maxPitchChange);
        this.yaw = this.changeAngle(this.yaw, float12, maxYawChange);
    }
    
    private float changeAngle(final float oldAngle, final float newAngle, final float maxChangeInAngle) {
        float float4 = MathHelper.wrapDegrees(newAngle - oldAngle);
        if (float4 > maxChangeInAngle) {
            float4 = maxChangeInAngle;
        }
        if (float4 < -maxChangeInAngle) {
            float4 = -maxChangeInAngle;
        }
        return oldAngle + float4;
    }
    
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return this.a(iWorld, spawnType, new BlockPos(this));
    }
    
    protected boolean a(final IWorld iWorld, final SpawnType spawnType, final BlockPos blockPos) {
        final BlockPos blockPos2 = blockPos.down();
        return spawnType == SpawnType.c || iWorld.getBlockState(blockPos2).allowsSpawning(iWorld, blockPos2, this.getType());
    }
    
    public boolean canSpawn(final ViewableWorld world) {
        return !world.intersectsFluid(this.getBoundingBox()) && world.intersectsEntities(this);
    }
    
    public int getLimitPerChunk() {
        return 4;
    }
    
    public boolean spawnsTooManyForEachTry(final int count) {
        return false;
    }
    
    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int integer1 = (int)(this.getHealth() - this.getHealthMaximum() * 0.33f);
        integer1 -= (3 - this.world.getDifficulty().getId()) * 4;
        if (integer1 < 0) {
            integer1 = 0;
        }
        return integer1 + 3;
    }
    
    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.handItems;
    }
    
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }
    
    @Override
    public ItemStack getEquippedStack(final EquipmentSlot equipmentSlot) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                return this.handItems.get(equipmentSlot.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(equipmentSlot.getEntitySlotId());
            }
            default: {
                return ItemStack.EMPTY;
            }
        }
    }
    
    @Override
    public void setEquippedStack(final EquipmentSlot slot, final ItemStack itemStack) {
        switch (slot.getType()) {
            case HAND: {
                this.handItems.set(slot.getEntitySlotId(), itemStack);
                break;
            }
            case ARMOR: {
                this.armorItems.set(slot.getEntitySlotId(), itemStack);
                break;
            }
        }
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        for (final EquipmentSlot equipmentSlot7 : EquipmentSlot.values()) {
            final ItemStack itemStack8 = this.getEquippedStack(equipmentSlot7);
            final float float9 = this.getDropChance(equipmentSlot7);
            final boolean boolean10 = float9 > 1.0f;
            if (!itemStack8.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack8) && (dropAllowed || boolean10) && this.random.nextFloat() - addedDropChance * 0.01f < float9) {
                if (!boolean10 && itemStack8.hasDurability()) {
                    itemStack8.setDamage(itemStack8.getDurability() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack8.getDurability() - 3, 1))));
                }
                this.dropStack(itemStack8);
            }
        }
    }
    
    protected float getDropChance(final EquipmentSlot equipmentSlot) {
        float float2 = 0.0f;
        switch (equipmentSlot.getType()) {
            case HAND: {
                float2 = this.handDropChances[equipmentSlot.getEntitySlotId()];
                break;
            }
            case ARMOR: {
                float2 = this.armorDropChances[equipmentSlot.getEntitySlotId()];
                break;
            }
            default: {
                float2 = 0.0f;
                break;
            }
        }
        return float2;
    }
    
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        if (this.random.nextFloat() < 0.15f * localDifficulty.getClampedLocalDifficulty()) {
            int integer2 = this.random.nextInt(2);
            final float float3 = (this.world.getDifficulty() == Difficulty.HARD) ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++integer2;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++integer2;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++integer2;
            }
            boolean boolean4 = true;
            for (final EquipmentSlot equipmentSlot8 : EquipmentSlot.values()) {
                if (equipmentSlot8.getType() == EquipmentSlot.Type.ARMOR) {
                    final ItemStack itemStack9 = this.getEquippedStack(equipmentSlot8);
                    if (!boolean4 && this.random.nextFloat() < float3) {
                        break;
                    }
                    boolean4 = false;
                    if (itemStack9.isEmpty()) {
                        final Item item10 = getEquipmentForSlot(equipmentSlot8, integer2);
                        if (item10 != null) {
                            this.setEquippedStack(equipmentSlot8, new ItemStack(item10));
                        }
                    }
                }
            }
        }
    }
    
    public static EquipmentSlot getPreferredEquipmentSlot(final ItemStack stack) {
        final Item item2 = stack.getItem();
        if (item2 == Blocks.cN.getItem() || (item2 instanceof BlockItem && ((BlockItem)item2).getBlock() instanceof AbstractSkullBlock)) {
            return EquipmentSlot.HEAD;
        }
        if (item2 instanceof ArmorItem) {
            return ((ArmorItem)item2).getSlotType();
        }
        if (item2 == Items.oX) {
            return EquipmentSlot.CHEST;
        }
        if (item2 == Items.oW) {
            return EquipmentSlot.HAND_OFF;
        }
        return EquipmentSlot.HAND_MAIN;
    }
    
    @Nullable
    public static Item getEquipmentForSlot(final EquipmentSlot equipmentSlot, final int equipmentLevel) {
        switch (equipmentSlot) {
            case HEAD: {
                if (equipmentLevel == 0) {
                    return Items.jR;
                }
                if (equipmentLevel == 1) {
                    return Items.kh;
                }
                if (equipmentLevel == 2) {
                    return Items.jV;
                }
                if (equipmentLevel == 3) {
                    return Items.jZ;
                }
                if (equipmentLevel == 4) {
                    return Items.kd;
                }
            }
            case CHEST: {
                if (equipmentLevel == 0) {
                    return Items.jS;
                }
                if (equipmentLevel == 1) {
                    return Items.ki;
                }
                if (equipmentLevel == 2) {
                    return Items.jW;
                }
                if (equipmentLevel == 3) {
                    return Items.ka;
                }
                if (equipmentLevel == 4) {
                    return Items.ke;
                }
            }
            case LEGS: {
                if (equipmentLevel == 0) {
                    return Items.jT;
                }
                if (equipmentLevel == 1) {
                    return Items.kj;
                }
                if (equipmentLevel == 2) {
                    return Items.jX;
                }
                if (equipmentLevel == 3) {
                    return Items.kb;
                }
                if (equipmentLevel == 4) {
                    return Items.kf;
                }
            }
            case FEET: {
                if (equipmentLevel == 0) {
                    return Items.jU;
                }
                if (equipmentLevel == 1) {
                    return Items.kk;
                }
                if (equipmentLevel == 2) {
                    return Items.jY;
                }
                if (equipmentLevel == 3) {
                    return Items.kc;
                }
                if (equipmentLevel == 4) {
                    return Items.kg;
                }
                break;
            }
        }
        return null;
    }
    
    protected void updateEnchantments(final LocalDifficulty difficulty) {
        final float float2 = difficulty.getClampedLocalDifficulty();
        if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25f * float2) {
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0f + float2 * this.random.nextInt(18)), false));
        }
        for (final EquipmentSlot equipmentSlot6 : EquipmentSlot.values()) {
            if (equipmentSlot6.getType() == EquipmentSlot.Type.ARMOR) {
                final ItemStack itemStack7 = this.getEquippedStack(equipmentSlot6);
                if (!itemStack7.isEmpty() && this.random.nextFloat() < 0.5f * float2) {
                    this.setEquippedStack(equipmentSlot6, EnchantmentHelper.enchant(this.random, itemStack7, (int)(5.0f + float2 * this.random.nextInt(18)), false));
                }
            }
        }
    }
    
    @Nullable
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.b));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        }
        else {
            this.setLeftHanded(false);
        }
        return entityData;
    }
    
    public boolean canBeControlledByRider() {
        return false;
    }
    
    public void setPersistent() {
        this.persistent = true;
    }
    
    public void setEquipmentDropChance(final EquipmentSlot slot, final float float2) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = float2;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = float2;
                break;
            }
        }
    }
    
    public boolean canPickUpLoot() {
        return this.pickUpLoot;
    }
    
    public void setCanPickUpLoot(final boolean boolean1) {
        this.pickUpLoot = boolean1;
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty() && this.canPickUpLoot();
    }
    
    public boolean isPersistent() {
        return this.persistent;
    }
    
    @Override
    public final boolean interact(final PlayerEntity player, final Hand hand) {
        if (!this.isAlive()) {
            return false;
        }
        if (this.getHoldingEntity() == player) {
            this.detachLeash(true, !player.abilities.creativeMode);
            return true;
        }
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.oq && this.canBeLeashedBy(player)) {
            this.attachLeash(player, true);
            itemStack3.subtractAmount(1);
            return true;
        }
        return this.interactMob(player, hand) || super.interact(player, hand);
    }
    
    protected boolean interactMob(final PlayerEntity player, final Hand hand) {
        return false;
    }
    
    public boolean isInWalkTargetRange() {
        return this.isInWalkTargetRange(new BlockPos(this));
    }
    
    public boolean isInWalkTargetRange(final BlockPos blockPos) {
        return this.walkTargetRange == -1.0f || this.walkTarget.getSquaredDistance(blockPos) < this.walkTargetRange * this.walkTargetRange;
    }
    
    public void setWalkTarget(final BlockPos blockPos, final int integer) {
        this.walkTarget = blockPos;
        this.walkTargetRange = (float)integer;
    }
    
    public BlockPos getWalkTarget() {
        return this.walkTarget;
    }
    
    public float getWalkTargetRange() {
        return this.walkTargetRange;
    }
    
    public boolean hasWalkTargetRange() {
        return this.walkTargetRange != -1.0f;
    }
    
    protected void updateLeash() {
        if (this.leashTag != null) {
            this.deserializeLeashTag();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }
    
    public void detachLeash(final boolean sendPacket, final boolean boolean2) {
        if (this.holdingEntity != null) {
            this.teleporting = false;
            if (!(this.holdingEntity instanceof PlayerEntity)) {
                this.holdingEntity.teleporting = false;
            }
            this.holdingEntity = null;
            if (!this.world.isClient && boolean2) {
                this.dropItem(Items.oq);
            }
            if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
            }
        }
    }
    
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return !this.isLeashed() && !(this instanceof Monster);
    }
    
    public boolean isLeashed() {
        return this.holdingEntity != null;
    }
    
    @Nullable
    public Entity getHoldingEntity() {
        if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
            this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
        }
        return this.holdingEntity;
    }
    
    public void attachLeash(final Entity entity, final boolean boolean2) {
        this.holdingEntity = entity;
        this.teleporting = true;
        if (!(this.holdingEntity instanceof PlayerEntity)) {
            this.holdingEntity.teleporting = true;
        }
        if (!this.world.isClient && boolean2 && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void setHoldingEntityId(final int id) {
        this.holdingEntityId = id;
        this.detachLeash(false, false);
    }
    
    @Override
    public boolean startRiding(final Entity entity, final boolean boolean2) {
        final boolean boolean3 = super.startRiding(entity, boolean2);
        if (boolean3 && this.isLeashed()) {
            this.detachLeash(true, true);
        }
        return boolean3;
    }
    
    private void deserializeLeashTag() {
        if (this.leashTag != null && this.world instanceof ServerWorld) {
            if (this.leashTag.hasUuid("UUID")) {
                final UUID uUID1 = this.leashTag.getUuid("UUID");
                final Entity entity2 = ((ServerWorld)this.world).getEntity(uUID1);
                if (entity2 != null) {
                    this.attachLeash(entity2, true);
                }
            }
            else if (this.leashTag.containsKey("X", 99) && this.leashTag.containsKey("Y", 99) && this.leashTag.containsKey("Z", 99)) {
                final BlockPos blockPos1 = new BlockPos(this.leashTag.getInt("X"), this.leashTag.getInt("Y"), this.leashTag.getInt("Z"));
                this.attachLeash(LeadKnotEntity.getOrCreate(this.world, blockPos1), true);
            }
            else {
                this.detachLeash(false, true);
            }
            this.leashTag = null;
        }
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        EquipmentSlot equipmentSlot3;
        if (slot == 98) {
            equipmentSlot3 = EquipmentSlot.HAND_MAIN;
        }
        else if (slot == 99) {
            equipmentSlot3 = EquipmentSlot.HAND_OFF;
        }
        else if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.HEAD;
        }
        else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.CHEST;
        }
        else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.LEGS;
        }
        else {
            if (slot != 100 + EquipmentSlot.FEET.getEntitySlotId()) {
                return false;
            }
            equipmentSlot3 = EquipmentSlot.FEET;
        }
        if (item.isEmpty() || canEquipmentSlotContain(equipmentSlot3, item) || equipmentSlot3 == EquipmentSlot.HEAD) {
            this.setEquippedStack(equipmentSlot3, item);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
    }
    
    public static boolean canEquipmentSlotContain(final EquipmentSlot slot, final ItemStack item) {
        final EquipmentSlot equipmentSlot3 = getPreferredEquipmentSlot(item);
        return equipmentSlot3 == slot || (equipmentSlot3 == EquipmentSlot.HAND_MAIN && slot == EquipmentSlot.HAND_OFF) || (equipmentSlot3 == EquipmentSlot.HAND_OFF && slot == EquipmentSlot.HAND_MAIN);
    }
    
    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isAiDisabled();
    }
    
    public void setAiDisabled(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS);
        this.dataTracker.<Byte>set(MobEntity.MOB_FLAGS, boolean1 ? ((byte)(byte2 | 0x1)) : ((byte)(byte2 & 0xFFFFFFFE)));
    }
    
    public void setLeftHanded(final boolean leftHanded) {
        final byte byte2 = this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS);
        this.dataTracker.<Byte>set(MobEntity.MOB_FLAGS, leftHanded ? ((byte)(byte2 | 0x2)) : ((byte)(byte2 & 0xFFFFFFFD)));
    }
    
    public void setAttacking(final boolean attacking) {
        final byte byte2 = this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS);
        this.dataTracker.<Byte>set(MobEntity.MOB_FLAGS, attacking ? ((byte)(byte2 | 0x4)) : ((byte)(byte2 & 0xFFFFFFFB)));
    }
    
    public boolean isAiDisabled() {
        return (this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS) & 0x1) != 0x0;
    }
    
    public boolean isLeftHanded() {
        return (this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS) & 0x2) != 0x0;
    }
    
    public boolean isAttacking() {
        return (this.dataTracker.<Byte>get(MobEntity.MOB_FLAGS) & 0x4) != 0x0;
    }
    
    @Override
    public AbsoluteHand getMainHand() {
        return this.isLeftHanded() ? AbsoluteHand.a : AbsoluteHand.b;
    }
    
    @Override
    public boolean canTarget(final LivingEntity target) {
        return (target.getType() != EntityType.PLAYER || !((PlayerEntity)target).abilities.invulnerable) && super.canTarget(target);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        float float2 = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
        float float3 = (float)this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).getValue();
        if (entity instanceof LivingEntity) {
            float2 += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
            float3 += EnchantmentHelper.getKnockback(this);
        }
        final int integer4 = EnchantmentHelper.getFireAspect(this);
        if (integer4 > 0) {
            entity.setOnFireFor(integer4 * 4);
        }
        final boolean boolean5 = entity.damage(DamageSource.mob(this), float2);
        if (boolean5) {
            if (float3 > 0.0f && entity instanceof LivingEntity) {
                ((LivingEntity)entity).takeKnockback(this, float3 * 0.5f, MathHelper.sin(this.yaw * 0.017453292f), -MathHelper.cos(this.yaw * 0.017453292f));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (entity instanceof PlayerEntity) {
                final PlayerEntity playerEntity6 = (PlayerEntity)entity;
                final ItemStack itemStack7 = this.getMainHandStack();
                final ItemStack itemStack8 = playerEntity6.isUsingItem() ? playerEntity6.getActiveItem() : ItemStack.EMPTY;
                if (!itemStack7.isEmpty() && !itemStack8.isEmpty() && itemStack7.getItem() instanceof AxeItem && itemStack8.getItem() == Items.oW) {
                    final float float4 = 0.25f + EnchantmentHelper.getEfficiency(this) * 0.05f;
                    if (this.random.nextFloat() < float4) {
                        playerEntity6.getItemCooldownManager().set(Items.oW, 100);
                        this.world.sendEntityStatus(playerEntity6, (byte)30);
                    }
                }
            }
            this.dealDamage(this, entity);
        }
        return boolean5;
    }
    
    protected boolean isInDaylight() {
        if (this.world.isDaylight() && !this.world.isClient) {
            final float float1 = this.getBrightnessAtEyes();
            final BlockPos blockPos2 = (this.getVehicle() instanceof BoatEntity) ? new BlockPos(this.x, (double)Math.round(this.y), this.z).up() : new BlockPos(this.x, (double)Math.round(this.y), this.z);
            if (float1 > 0.5f && this.random.nextFloat() * 30.0f < (float1 - 0.4f) * 2.0f && this.world.isSkyVisible(blockPos2)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void swimUpward(final net.minecraft.tag.Tag<Fluid> fluid) {
        if (this.getNavigation().canSwim()) {
            super.swimUpward(fluid);
        }
        else {
            this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
        }
    }
    
    public boolean isHolding(final Item item) {
        return this.getMainHandStack().getItem() == item || this.getOffHandStack().getItem() == item;
    }
    
    static {
        MOB_FLAGS = DataTracker.<Byte>registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
