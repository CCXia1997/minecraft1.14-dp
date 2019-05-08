package net.minecraft.entity.mob;

import java.util.AbstractList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.item.Item;
import net.minecraft.item.BannerItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.util.Hand;
import net.minecraft.item.CrossbowItem;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemProvider;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.LightType;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.CrossbowUser;

public class PillagerEntity extends IllagerEntity implements CrossbowUser, RangedAttacker
{
    private static final TrackedData<Boolean> CHARGING;
    private final BasicInventory inventory;
    
    public PillagerEntity(final EntityType<? extends PillagerEntity> type, final World world) {
        super(type, world);
        this.inventory = new BasicInventory(5);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(3, new CrossbowAttackGoal<>(this, 1.0, 8.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(PillagerEntity.CHARGING, false);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isCharging() {
        return this.dataTracker.<Boolean>get(PillagerEntity.CHARGING);
    }
    
    @Override
    public void setCharging(final boolean charging) {
        this.dataTracker.<Boolean>set(PillagerEntity.CHARGING, charging);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final ListTag listTag2 = new ListTag();
        for (int integer3 = 0; integer3 < this.inventory.getInvSize(); ++integer3) {
            final ItemStack itemStack4 = this.inventory.getInvStack(integer3);
            if (!itemStack4.isEmpty()) {
                ((AbstractList<CompoundTag>)listTag2).add(itemStack4.toTag(new CompoundTag()));
            }
        }
        tag.put("Inventory", listTag2);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public State getState() {
        if (this.isCharging()) {
            return State.f;
        }
        if (this.isHolding(Items.py)) {
            return State.e;
        }
        if (this.isAttacking()) {
            return State.b;
        }
        return State.a;
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        final ListTag listTag2 = tag.getList("Inventory", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final ItemStack itemStack4 = ItemStack.fromTag(listTag2.getCompoundTag(integer3));
            if (!itemStack4.isEmpty()) {
                this.inventory.add(itemStack4);
            }
        }
        this.setCanPickUpLoot(true);
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        final Block block3 = world.getBlockState(pos.down()).getBlock();
        if (block3 == Blocks.i || block3 == Blocks.C) {
            return 10.0f;
        }
        return 0.5f - world.getBrightness(pos);
    }
    
    @Override
    protected boolean checkLightLevelForSpawn() {
        return true;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return iWorld.getLightLevel(LightType.BLOCK, new BlockPos(this.x, this.y, this.z)) <= 8 && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    public int getLimitPerChunk() {
        return 1;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.initEquipment(localDifficulty);
        this.updateEnchantments(localDifficulty);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        final ItemStack itemStack2 = new ItemStack(Items.py);
        if (this.random.nextInt(300) == 0) {
            final Map<Enchantment, Integer> map3 = Maps.newHashMap();
            map3.put(Enchantments.I, 1);
            EnchantmentHelper.set(map3, itemStack2);
        }
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack2);
    }
    
    @Override
    public boolean isTeammate(final Entity entity) {
        return super.isTeammate(entity) || (entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.is;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.iu;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.iv;
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        final Hand hand3 = ProjectileUtil.getHandPossiblyHolding(this, Items.py);
        final ItemStack itemStack4 = this.getStackInHand(hand3);
        if (this.isHolding(Items.py)) {
            CrossbowItem.shootAllProjectiles(this.world, this, hand3, itemStack4, 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        }
        this.despawnCounter = 0;
    }
    
    @Override
    public void shoot(final LivingEntity target, final ItemStack itemStack, final Projectile projectile, final float float4) {
        final Entity entity5 = (Entity)projectile;
        final double double6 = target.x - this.x;
        final double double7 = target.z - this.z;
        final double double8 = MathHelper.sqrt(double6 * double6 + double7 * double7);
        final double double9 = target.getBoundingBox().minY + target.getHeight() / 3.0f - entity5.y + double8 * 0.20000000298023224;
        final Vector3f vector3f14 = this.getProjectileVelocity(new Vec3d(double6, double9, double7), float4);
        projectile.setVelocity(vector3f14.x(), vector3f14.y(), vector3f14.z(), 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.bH, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
    }
    
    private Vector3f getProjectileVelocity(final Vec3d vec3d, final float float2) {
        final Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (vec3d3.lengthSquared() <= 1.0E-7) {
            vec3d3 = vec3d2.crossProduct(this.getOppositeRotationVector(1.0f));
        }
        final Quaternion quaternion5 = new Quaternion(new Vector3f(vec3d3), 90.0f, true);
        final Vector3f vector3f6 = new Vector3f(vec3d2);
        vector3f6.a(quaternion5);
        final Quaternion quaternion6 = new Quaternion(vector3f6, float2, true);
        final Vector3f vector3f7 = new Vector3f(vec3d2);
        vector3f7.a(quaternion6);
        return vector3f7;
    }
    
    public BasicInventory getInventory() {
        return this.inventory;
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        final ItemStack itemStack2 = item.getStack();
        if (itemStack2.getItem() instanceof BannerItem) {
            super.loot(item);
        }
        else {
            final Item item2 = itemStack2.getItem();
            if (this.b(item2)) {
                final ItemStack itemStack3 = this.inventory.add(itemStack2);
                if (itemStack3.isEmpty()) {
                    item.remove();
                }
                else {
                    itemStack2.setAmount(itemStack3.getAmount());
                }
            }
        }
    }
    
    private boolean b(final Item item) {
        return this.hasActiveRaid() && item == Items.ov;
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (super.equip(slot, item)) {
            return true;
        }
        final int integer3 = slot - 300;
        if (integer3 >= 0 && integer3 < this.inventory.getInvSize()) {
            this.inventory.setInvStack(integer3, item);
            return true;
        }
        return false;
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
        final Raid raid3 = this.getRaid();
        final boolean boolean3 = this.random.nextFloat() <= raid3.getEnchantmentChance();
        if (boolean3) {
            final ItemStack itemStack5 = new ItemStack(Items.py);
            final Map<Enchantment, Integer> map6 = Maps.newHashMap();
            if (wave > raid3.getMaxWaves(Difficulty.NORMAL)) {
                map6.put(Enchantments.H, 2);
            }
            else if (wave > raid3.getMaxWaves(Difficulty.EASY)) {
                map6.put(Enchantments.H, 1);
            }
            map6.put(Enchantments.G, 1);
            EnchantmentHelper.set(map6, itemStack5);
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack5);
        }
    }
    
    @Override
    public boolean cannotDespawn() {
        return super.cannotDespawn() && this.getInventory().isInvEmpty();
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.it;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return super.canImmediatelyDespawn(distanceSquared) && this.getInventory().isInvEmpty();
    }
    
    static {
        CHARGING = DataTracker.<Boolean>registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
