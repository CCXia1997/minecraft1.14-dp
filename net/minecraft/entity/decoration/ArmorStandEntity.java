package net.minecraft.entity.decoration;

import java.util.AbstractList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.LightningEntity;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.block.Block;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.EulerRotation;
import net.minecraft.entity.LivingEntity;

public class ArmorStandEntity extends LivingEntity
{
    private static final EulerRotation DEFAULT_HEAD_ROTATION;
    private static final EulerRotation DEFAULT_BODY_ROTATION;
    private static final EulerRotation DEFAULT_LEFT_ARM_ROTATION;
    private static final EulerRotation DEFAULT_RIGHT_ARM_ROTATION;
    private static final EulerRotation DEFAULT_LEFT_LEG_ROTATION;
    private static final EulerRotation DEFAULT_RIGHT_LEG_ROTATION;
    public static final TrackedData<Byte> ARMOR_STAND_FLAGS;
    public static final TrackedData<EulerRotation> TRACKER_HEAD_ROTATION;
    public static final TrackedData<EulerRotation> TRACKER_BODY_ROTATION;
    public static final TrackedData<EulerRotation> TRACKER_LEFT_ARM_ROTATION;
    public static final TrackedData<EulerRotation> TRACKER_RIGHT_ARM_ROTATION;
    public static final TrackedData<EulerRotation> TRACKER_LEFT_LEG_ROTATION;
    public static final TrackedData<EulerRotation> TRACKER_RIGHT_LEG_ROTATION;
    private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE;
    private final DefaultedList<ItemStack> heldItems;
    private final DefaultedList<ItemStack> armorItems;
    private boolean bD;
    public long bt;
    private int disabledSlots;
    private EulerRotation headRotation;
    private EulerRotation bodyRotation;
    private EulerRotation leftArmRotation;
    private EulerRotation rightArmRotation;
    private EulerRotation leftLegRotation;
    private EulerRotation rightLegRotation;
    
    public ArmorStandEntity(final EntityType<? extends ArmorStandEntity> type, final World world) {
        super(type, world);
        this.heldItems = DefaultedList.<ItemStack>create(2, ItemStack.EMPTY);
        this.armorItems = DefaultedList.<ItemStack>create(4, ItemStack.EMPTY);
        this.headRotation = ArmorStandEntity.DEFAULT_HEAD_ROTATION;
        this.bodyRotation = ArmorStandEntity.DEFAULT_BODY_ROTATION;
        this.leftArmRotation = ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION;
        this.rightArmRotation = ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION;
        this.leftLegRotation = ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION;
        this.rightLegRotation = ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION;
        this.stepHeight = 0.0f;
    }
    
    public ArmorStandEntity(final World world, final double double2, final double double4, final double double6) {
        this(EntityType.ARMOR_STAND, world);
        this.setPosition(double2, double4, double6);
    }
    
    @Override
    public void refreshSize() {
        final double double1 = this.x;
        final double double2 = this.y;
        final double double3 = this.z;
        super.refreshSize();
        this.setPosition(double1, double2, double3);
    }
    
    private boolean canClip() {
        return !this.isMarker() && !this.isUnaffectedByGravity();
    }
    
    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && this.canClip();
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(ArmorStandEntity.ARMOR_STAND_FLAGS, (Byte)0);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_HEAD_ROTATION, ArmorStandEntity.DEFAULT_HEAD_ROTATION);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_BODY_ROTATION, ArmorStandEntity.DEFAULT_BODY_ROTATION);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_LEFT_ARM_ROTATION, ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_RIGHT_ARM_ROTATION, ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_LEFT_LEG_ROTATION, ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION);
        this.dataTracker.<EulerRotation>startTracking(ArmorStandEntity.TRACKER_RIGHT_LEG_ROTATION, ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION);
    }
    
    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.heldItems;
    }
    
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }
    
    @Override
    public ItemStack getEquippedStack(final EquipmentSlot equipmentSlot) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                return this.heldItems.get(equipmentSlot.getEntitySlotId());
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
                this.onEquipStack(itemStack);
                this.heldItems.set(slot.getEntitySlotId(), itemStack);
                break;
            }
            case ARMOR: {
                this.onEquipStack(itemStack);
                this.armorItems.set(slot.getEntitySlotId(), itemStack);
                break;
            }
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
        if (item.isEmpty() || MobEntity.canEquipmentSlotContain(equipmentSlot3, item) || equipmentSlot3 == EquipmentSlot.HEAD) {
            this.setEquippedStack(equipmentSlot3, item);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = MobEntity.getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty() && !this.d(equipmentSlot2);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
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
        for (final ItemStack itemStack5 : this.heldItems) {
            final CompoundTag compoundTag6 = new CompoundTag();
            if (!itemStack5.isEmpty()) {
                itemStack5.toTag(compoundTag6);
            }
            ((AbstractList<CompoundTag>)listTag3).add(compoundTag6);
        }
        tag.put("HandItems", listTag3);
        tag.putBoolean("Invisible", this.isInvisible());
        tag.putBoolean("Small", this.isSmall());
        tag.putBoolean("ShowArms", this.shouldShowArms());
        tag.putInt("DisabledSlots", this.disabledSlots);
        tag.putBoolean("NoBasePlate", this.shouldHideBasePlate());
        if (this.isMarker()) {
            tag.putBoolean("Marker", this.isMarker());
        }
        tag.put("Pose", this.serializePose());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("ArmorItems", 9)) {
            final ListTag listTag2 = tag.getList("ArmorItems", 10);
            for (int integer3 = 0; integer3 < this.armorItems.size(); ++integer3) {
                this.armorItems.set(integer3, ItemStack.fromTag(listTag2.getCompoundTag(integer3)));
            }
        }
        if (tag.containsKey("HandItems", 9)) {
            final ListTag listTag2 = tag.getList("HandItems", 10);
            for (int integer3 = 0; integer3 < this.heldItems.size(); ++integer3) {
                this.heldItems.set(integer3, ItemStack.fromTag(listTag2.getCompoundTag(integer3)));
            }
        }
        this.setInvisible(tag.getBoolean("Invisible"));
        this.setSmall(tag.getBoolean("Small"));
        this.setShowArms(tag.getBoolean("ShowArms"));
        this.disabledSlots = tag.getInt("DisabledSlots");
        this.setHideBasePlate(tag.getBoolean("NoBasePlate"));
        this.setMarker(tag.getBoolean("Marker"));
        this.noClip = !this.canClip();
        final CompoundTag compoundTag2 = tag.getCompound("Pose");
        this.deserializePose(compoundTag2);
    }
    
    private void deserializePose(final CompoundTag compoundTag) {
        final ListTag listTag2 = compoundTag.getList("Head", 5);
        this.setHeadRotation(listTag2.isEmpty() ? ArmorStandEntity.DEFAULT_HEAD_ROTATION : new EulerRotation(listTag2));
        final ListTag listTag3 = compoundTag.getList("Body", 5);
        this.setBodyRotation(listTag3.isEmpty() ? ArmorStandEntity.DEFAULT_BODY_ROTATION : new EulerRotation(listTag3));
        final ListTag listTag4 = compoundTag.getList("LeftArm", 5);
        this.setLeftArmRotation(listTag4.isEmpty() ? ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION : new EulerRotation(listTag4));
        final ListTag listTag5 = compoundTag.getList("RightArm", 5);
        this.setRightArmRotation(listTag5.isEmpty() ? ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION : new EulerRotation(listTag5));
        final ListTag listTag6 = compoundTag.getList("LeftLeg", 5);
        this.setLeftLegRotation(listTag6.isEmpty() ? ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION : new EulerRotation(listTag6));
        final ListTag listTag7 = compoundTag.getList("RightLeg", 5);
        this.setRightLegRotation(listTag7.isEmpty() ? ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION : new EulerRotation(listTag7));
    }
    
    private CompoundTag serializePose() {
        final CompoundTag compoundTag1 = new CompoundTag();
        if (!ArmorStandEntity.DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
            compoundTag1.put("Head", this.headRotation.serialize());
        }
        if (!ArmorStandEntity.DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
            compoundTag1.put("Body", this.bodyRotation.serialize());
        }
        if (!ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION.equals(this.leftArmRotation)) {
            compoundTag1.put("LeftArm", this.leftArmRotation.serialize());
        }
        if (!ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION.equals(this.rightArmRotation)) {
            compoundTag1.put("RightArm", this.rightArmRotation.serialize());
        }
        if (!ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION.equals(this.leftLegRotation)) {
            compoundTag1.put("LeftLeg", this.leftLegRotation.serialize());
        }
        if (!ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION.equals(this.rightLegRotation)) {
            compoundTag1.put("RightLeg", this.rightLegRotation.serialize());
        }
        return compoundTag1;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected void pushAway(final Entity entity) {
    }
    
    @Override
    protected void doPushLogic() {
        final List<Entity> list1 = this.world.getEntities(this, this.getBoundingBox(), ArmorStandEntity.RIDEABLE_MINECART_PREDICATE);
        for (int integer2 = 0; integer2 < list1.size(); ++integer2) {
            final Entity entity3 = list1.get(integer2);
            if (this.squaredDistanceTo(entity3) <= 0.2) {
                entity3.pushAwayFrom(this);
            }
        }
    }
    
    @Override
    public ActionResult interactAt(final PlayerEntity player, final Vec3d hitPos, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (this.isMarker() || itemStack4.getItem() == Items.or) {
            return ActionResult.PASS;
        }
        if (this.world.isClient || player.isSpectator()) {
            return ActionResult.a;
        }
        final EquipmentSlot equipmentSlot5 = MobEntity.getPreferredEquipmentSlot(itemStack4);
        if (itemStack4.isEmpty()) {
            final EquipmentSlot equipmentSlot6 = this.f(hitPos);
            final EquipmentSlot equipmentSlot7 = this.d(equipmentSlot6) ? equipmentSlot5 : equipmentSlot6;
            if (this.isEquippedStackValid(equipmentSlot7)) {
                this.a(player, equipmentSlot7, itemStack4, hand);
            }
        }
        else {
            if (this.d(equipmentSlot5)) {
                return ActionResult.c;
            }
            if (equipmentSlot5.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
                return ActionResult.c;
            }
            this.a(player, equipmentSlot5, itemStack4, hand);
        }
        return ActionResult.a;
    }
    
    protected EquipmentSlot f(final Vec3d vec3d) {
        EquipmentSlot equipmentSlot2 = EquipmentSlot.HAND_MAIN;
        final boolean boolean3 = this.isSmall();
        final double double4 = boolean3 ? (vec3d.y * 2.0) : vec3d.y;
        final EquipmentSlot equipmentSlot3 = EquipmentSlot.FEET;
        if (double4 >= 0.1 && double4 < 0.1 + (boolean3 ? 0.8 : 0.45) && this.isEquippedStackValid(equipmentSlot3)) {
            equipmentSlot2 = EquipmentSlot.FEET;
        }
        else if (double4 >= 0.9 + (boolean3 ? 0.3 : 0.0) && double4 < 0.9 + (boolean3 ? 1.0 : 0.7) && this.isEquippedStackValid(EquipmentSlot.CHEST)) {
            equipmentSlot2 = EquipmentSlot.CHEST;
        }
        else if (double4 >= 0.4 && double4 < 0.4 + (boolean3 ? 1.0 : 0.8) && this.isEquippedStackValid(EquipmentSlot.LEGS)) {
            equipmentSlot2 = EquipmentSlot.LEGS;
        }
        else if (double4 >= 1.6 && this.isEquippedStackValid(EquipmentSlot.HEAD)) {
            equipmentSlot2 = EquipmentSlot.HEAD;
        }
        else if (!this.isEquippedStackValid(EquipmentSlot.HAND_MAIN) && this.isEquippedStackValid(EquipmentSlot.HAND_OFF)) {
            equipmentSlot2 = EquipmentSlot.HAND_OFF;
        }
        return equipmentSlot2;
    }
    
    public boolean d(final EquipmentSlot equipmentSlot) {
        return (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId()) != 0x0 || (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms());
    }
    
    private void a(final PlayerEntity playerEntity, final EquipmentSlot equipmentSlot, final ItemStack itemStack, final Hand hand) {
        final ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
        if (!itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 8) != 0x0) {
            return;
        }
        if (itemStack2.isEmpty() && (this.disabledSlots & 1 << equipmentSlot.getArmorStandSlotId() + 16) != 0x0) {
            return;
        }
        if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
            final ItemStack itemStack3 = itemStack.copy();
            itemStack3.setAmount(1);
            this.setEquippedStack(equipmentSlot, itemStack3);
            return;
        }
        if (itemStack.isEmpty() || itemStack.getAmount() <= 1) {
            this.setEquippedStack(equipmentSlot, itemStack);
            playerEntity.setStackInHand(hand, itemStack2);
            return;
        }
        if (!itemStack2.isEmpty()) {
            return;
        }
        final ItemStack itemStack3 = itemStack.copy();
        itemStack3.setAmount(1);
        this.setEquippedStack(equipmentSlot, itemStack3);
        itemStack.subtractAmount(1);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.world.isClient || this.removed) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals(source)) {
            this.remove();
            return false;
        }
        if (this.isInvulnerableTo(source) || this.bD || this.isMarker()) {
            return false;
        }
        if (source.isExplosive()) {
            this.g(source);
            this.remove();
            return false;
        }
        if (DamageSource.IN_FIRE.equals(source)) {
            if (this.isOnFire()) {
                this.e(source, 0.15f);
            }
            else {
                this.setOnFireFor(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5f) {
            this.e(source, 4.0f);
            return false;
        }
        final boolean boolean3 = source.getSource() instanceof ProjectileEntity;
        final boolean boolean4 = boolean3 && ((ProjectileEntity)source.getSource()).getPierceLevel() > 0;
        final boolean boolean5 = "player".equals(source.getName());
        if (!boolean5 && !boolean3) {
            return false;
        }
        if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity)source.getAttacker()).abilities.allowModifyWorld) {
            return false;
        }
        if (source.isSourceCreativePlayer()) {
            this.F();
            this.D();
            this.remove();
            return boolean4;
        }
        final long long6 = this.world.getTime();
        if (long6 - this.bt <= 5L || boolean3) {
            this.f(source);
            this.D();
            this.remove();
        }
        else {
            this.world.sendEntityStatus(this, (byte)32);
            this.bt = long6;
        }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 32) {
            if (this.world.isClient) {
                this.world.playSound(this.x, this.y, this.z, SoundEvents.z, this.getSoundCategory(), 0.3f, 1.0f, false);
                this.bt = this.world.getTime();
            }
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension() * 4.0;
        if (Double.isNaN(double3) || double3 == 0.0) {
            double3 = 4.0;
        }
        double3 *= 64.0;
        return distance < double3 * double3;
    }
    
    private void D() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).<BlockStateParticleParameters>spawnParticles(new BlockStateParticleParameters(ParticleTypes.d, Blocks.n.getDefaultState()), this.x, this.y + this.getHeight() / 1.5, this.z, 10, this.getWidth() / 4.0f, this.getHeight() / 4.0f, this.getWidth() / 4.0f, 0.05);
        }
    }
    
    private void e(final DamageSource damageSource, final float float2) {
        float float3 = this.getHealth();
        float3 -= float2;
        if (float3 <= 0.5f) {
            this.g(damageSource);
            this.remove();
        }
        else {
            this.setHealth(float3);
        }
    }
    
    private void f(final DamageSource damageSource) {
        Block.dropStack(this.world, new BlockPos(this), new ItemStack(Items.ol));
        this.g(damageSource);
    }
    
    private void g(final DamageSource damageSource) {
        this.F();
        this.drop(damageSource);
        for (int integer2 = 0; integer2 < this.heldItems.size(); ++integer2) {
            final ItemStack itemStack3 = this.heldItems.get(integer2);
            if (!itemStack3.isEmpty()) {
                Block.dropStack(this.world, new BlockPos(this).up(), itemStack3);
                this.heldItems.set(integer2, ItemStack.EMPTY);
            }
        }
        for (int integer2 = 0; integer2 < this.armorItems.size(); ++integer2) {
            final ItemStack itemStack3 = this.armorItems.get(integer2);
            if (!itemStack3.isEmpty()) {
                Block.dropStack(this.world, new BlockPos(this).up(), itemStack3);
                this.armorItems.set(integer2, ItemStack.EMPTY);
            }
        }
    }
    
    private void F() {
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.x, this.getSoundCategory(), 1.0f, 1.0f);
    }
    
    @Override
    protected float e(final float yaw, final float float2) {
        this.aL = this.prevYaw;
        this.aK = this.yaw;
        return 0.0f;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * (this.isChild() ? 0.5f : 0.9f);
    }
    
    @Override
    public double getHeightOffset() {
        return this.isMarker() ? 0.0 : 0.10000000149011612;
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (!this.canClip()) {
            return;
        }
        super.travel(movementInput);
    }
    
    @Override
    public void setYaw(final float float1) {
        this.prevYaw = float1;
        this.aL = float1;
        this.headYaw = float1;
        this.prevHeadYaw = float1;
    }
    
    @Override
    public void setHeadYaw(final float headYaw) {
        this.prevYaw = headYaw;
        this.aL = headYaw;
        this.headYaw = headYaw;
        this.prevHeadYaw = headYaw;
    }
    
    @Override
    public void tick() {
        super.tick();
        final EulerRotation eulerRotation1 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_HEAD_ROTATION);
        if (!this.headRotation.equals(eulerRotation1)) {
            this.setHeadRotation(eulerRotation1);
        }
        final EulerRotation eulerRotation2 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_BODY_ROTATION);
        if (!this.bodyRotation.equals(eulerRotation2)) {
            this.setBodyRotation(eulerRotation2);
        }
        final EulerRotation eulerRotation3 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_LEFT_ARM_ROTATION);
        if (!this.leftArmRotation.equals(eulerRotation3)) {
            this.setLeftArmRotation(eulerRotation3);
        }
        final EulerRotation eulerRotation4 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_RIGHT_ARM_ROTATION);
        if (!this.rightArmRotation.equals(eulerRotation4)) {
            this.setRightArmRotation(eulerRotation4);
        }
        final EulerRotation eulerRotation5 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_LEFT_LEG_ROTATION);
        if (!this.leftLegRotation.equals(eulerRotation5)) {
            this.setLeftLegRotation(eulerRotation5);
        }
        final EulerRotation eulerRotation6 = this.dataTracker.<EulerRotation>get(ArmorStandEntity.TRACKER_RIGHT_LEG_ROTATION);
        if (!this.rightLegRotation.equals(eulerRotation6)) {
            this.setRightLegRotation(eulerRotation6);
        }
    }
    
    @Override
    protected void updatePotionVisibility() {
        this.setInvisible(this.bD);
    }
    
    @Override
    public void setInvisible(final boolean invisible) {
        super.setInvisible(this.bD = invisible);
    }
    
    @Override
    public boolean isChild() {
        return this.isSmall();
    }
    
    @Override
    public void kill() {
        this.remove();
    }
    
    @Override
    public boolean isImmuneToExplosion() {
        return this.isInvisible();
    }
    
    @Override
    public PistonBehavior getPistonBehavior() {
        if (this.isMarker()) {
            return PistonBehavior.d;
        }
        return super.getPistonBehavior();
    }
    
    private void setSmall(final boolean boolean1) {
        this.dataTracker.<Byte>set(ArmorStandEntity.ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS), 1, boolean1));
    }
    
    public boolean isSmall() {
        return (this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS) & 0x1) != 0x0;
    }
    
    private void setShowArms(final boolean boolean1) {
        this.dataTracker.<Byte>set(ArmorStandEntity.ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS), 4, boolean1));
    }
    
    public boolean shouldShowArms() {
        return (this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS) & 0x4) != 0x0;
    }
    
    private void setHideBasePlate(final boolean boolean1) {
        this.dataTracker.<Byte>set(ArmorStandEntity.ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS), 8, boolean1));
    }
    
    public boolean shouldHideBasePlate() {
        return (this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS) & 0x8) != 0x0;
    }
    
    private void setMarker(final boolean boolean1) {
        this.dataTracker.<Byte>set(ArmorStandEntity.ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS), 16, boolean1));
    }
    
    public boolean isMarker() {
        return (this.dataTracker.<Byte>get(ArmorStandEntity.ARMOR_STAND_FLAGS) & 0x10) != 0x0;
    }
    
    private byte setBitField(byte value, final int bitField, final boolean set) {
        if (set) {
            value |= (byte)bitField;
        }
        else {
            value &= (byte)~bitField;
        }
        return value;
    }
    
    public void setHeadRotation(final EulerRotation eulerRotation) {
        this.headRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_HEAD_ROTATION, eulerRotation);
    }
    
    public void setBodyRotation(final EulerRotation eulerRotation) {
        this.bodyRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_BODY_ROTATION, eulerRotation);
    }
    
    public void setLeftArmRotation(final EulerRotation eulerRotation) {
        this.leftArmRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_LEFT_ARM_ROTATION, eulerRotation);
    }
    
    public void setRightArmRotation(final EulerRotation eulerRotation) {
        this.rightArmRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_RIGHT_ARM_ROTATION, eulerRotation);
    }
    
    public void setLeftLegRotation(final EulerRotation eulerRotation) {
        this.leftLegRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_LEFT_LEG_ROTATION, eulerRotation);
    }
    
    public void setRightLegRotation(final EulerRotation eulerRotation) {
        this.rightLegRotation = eulerRotation;
        this.dataTracker.<EulerRotation>set(ArmorStandEntity.TRACKER_RIGHT_LEG_ROTATION, eulerRotation);
    }
    
    public EulerRotation getHeadRotation() {
        return this.headRotation;
    }
    
    public EulerRotation getBodyRotation() {
        return this.bodyRotation;
    }
    
    @Environment(EnvType.CLIENT)
    public EulerRotation getLeftArmRotation() {
        return this.leftArmRotation;
    }
    
    @Environment(EnvType.CLIENT)
    public EulerRotation getRightArmRotation() {
        return this.rightArmRotation;
    }
    
    @Environment(EnvType.CLIENT)
    public EulerRotation getLeftLegRotation() {
        return this.leftLegRotation;
    }
    
    @Environment(EnvType.CLIENT)
    public EulerRotation getRightLegRotation() {
        return this.rightLegRotation;
    }
    
    @Override
    public boolean collides() {
        return super.collides() && !this.isMarker();
    }
    
    @Override
    public AbsoluteHand getMainHand() {
        return AbsoluteHand.b;
    }
    
    @Override
    protected SoundEvent getFallSound(final int integer) {
        return SoundEvents.y;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.z;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.x;
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
    }
    
    @Override
    public boolean dt() {
        return false;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (ArmorStandEntity.ARMOR_STAND_FLAGS.equals(data)) {
            this.refreshSize();
            this.i = !this.isMarker();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public boolean du() {
        return false;
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        final float float2 = this.isMarker() ? 0.0f : (this.isChild() ? 0.5f : 1.0f);
        return this.getType().getDefaultSize().scaled(float2);
    }
    
    static {
        DEFAULT_HEAD_ROTATION = new EulerRotation(0.0f, 0.0f, 0.0f);
        DEFAULT_BODY_ROTATION = new EulerRotation(0.0f, 0.0f, 0.0f);
        DEFAULT_LEFT_ARM_ROTATION = new EulerRotation(-10.0f, 0.0f, -10.0f);
        DEFAULT_RIGHT_ARM_ROTATION = new EulerRotation(-15.0f, 0.0f, 10.0f);
        DEFAULT_LEFT_LEG_ROTATION = new EulerRotation(-1.0f, 0.0f, -1.0f);
        DEFAULT_RIGHT_LEG_ROTATION = new EulerRotation(1.0f, 0.0f, 1.0f);
        ARMOR_STAND_FLAGS = DataTracker.<Byte>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
        TRACKER_HEAD_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        TRACKER_BODY_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        TRACKER_LEFT_ARM_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        TRACKER_RIGHT_ARM_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        TRACKER_LEFT_LEG_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        TRACKER_RIGHT_LEG_ROTATION = DataTracker.<EulerRotation>registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
        RIDEABLE_MINECART_PREDICATE = (entity -> entity instanceof AbstractMinecartEntity && entity.getMinecartType() == AbstractMinecartEntity.Type.a);
    }
}
