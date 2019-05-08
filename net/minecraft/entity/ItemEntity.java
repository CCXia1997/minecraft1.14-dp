package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.item.Item;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Items;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.World;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;

public class ItemEntity extends Entity
{
    private static final TrackedData<ItemStack> STACK;
    private int age;
    private int pickupDelay;
    private int health;
    private UUID thrower;
    private UUID owner;
    public final float hoverHeight;
    
    public ItemEntity(final EntityType<? extends ItemEntity> type, final World world) {
        super(type, world);
        this.health = 5;
        this.hoverHeight = (float)(Math.random() * 3.141592653589793 * 2.0);
    }
    
    public ItemEntity(final World world, final double x, final double y, final double z) {
        this(EntityType.ITEM, world);
        this.setPosition(x, y, z);
        this.yaw = this.random.nextFloat() * 360.0f;
        this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
    }
    
    public ItemEntity(final World world, final double x, final double y, final double z, final ItemStack stack) {
        this(world, x, y, z);
        this.setStack(stack);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(ItemEntity.STACK, ItemStack.EMPTY);
    }
    
    @Override
    public void tick() {
        if (this.getStack().isEmpty()) {
            this.remove();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        final Vec3d vec3d1 = this.getVelocity();
        if (this.isInFluid(FluidTags.a)) {
            this.v();
        }
        else if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        if (this.world.isClient) {
            this.noClip = false;
        }
        else {
            this.noClip = !this.world.doesNotCollide(this);
            if (this.noClip) {
                this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
            }
        }
        this.move(MovementType.a, this.getVelocity());
        final boolean boolean2 = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
        final int integer3 = boolean2 ? 2 : 40;
        if (this.age % integer3 == 0) {
            if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.b)) {
                this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.20000000298023224, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                this.playSound(SoundEvents.dF, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
            }
            if (!this.world.isClient && this.z()) {
                this.tryMerge();
            }
        }
        float float4 = 0.98f;
        if (this.onGround) {
            float4 = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.98f;
        }
        this.setVelocity(this.getVelocity().multiply(float4, 0.98, float4));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.5, 1.0));
        }
        if (this.age != -32768) {
            ++this.age;
        }
        this.velocityDirty |= this.ax();
        if (!this.world.isClient) {
            final double double5 = this.getVelocity().subtract(vec3d1).lengthSquared();
            if (double5 > 0.01) {
                this.velocityDirty = true;
            }
        }
        if (!this.world.isClient && this.age >= 6000) {
            this.remove();
        }
    }
    
    private void v() {
        final Vec3d vec3d1 = this.getVelocity();
        this.setVelocity(vec3d1.x * 0.9900000095367432, vec3d1.y + ((vec3d1.y < 0.05999999865889549) ? 5.0E-4f : 0.0f), vec3d1.z * 0.9900000095367432);
    }
    
    private void tryMerge() {
        final List<ItemEntity> list1 = this.world.<ItemEntity>getEntities(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), itemEntity -> itemEntity != this && itemEntity.z());
        if (!list1.isEmpty()) {
            for (final ItemEntity itemEntity2 : list1) {
                if (!this.z()) {
                    return;
                }
                this.tryMerge(itemEntity2);
            }
        }
    }
    
    private boolean z() {
        final ItemStack itemStack1 = this.getStack();
        return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemStack1.getAmount() < itemStack1.getMaxAmount();
    }
    
    private void tryMerge(final ItemEntity itemEntity) {
        final ItemStack itemStack2 = this.getStack();
        final ItemStack itemStack3 = itemEntity.getStack();
        if (itemStack3.getItem() != itemStack2.getItem()) {
            return;
        }
        if (itemStack3.getAmount() + itemStack2.getAmount() > itemStack3.getMaxAmount()) {
            return;
        }
        if (itemStack3.hasTag() ^ itemStack2.hasTag()) {
            return;
        }
        if (itemStack3.hasTag() && !itemStack3.getTag().equals(itemStack2.getTag())) {
            return;
        }
        if (itemStack3.getAmount() < itemStack2.getAmount()) {
            a(this, itemStack2, itemEntity, itemStack3);
        }
        else {
            a(itemEntity, itemStack3, this, itemStack2);
        }
    }
    
    private static void a(final ItemEntity itemEntity1, final ItemStack itemStack2, final ItemEntity itemEntity3, final ItemStack itemStack4) {
        final int integer5 = Math.min(itemStack2.getMaxAmount() - itemStack2.getAmount(), itemStack4.getAmount());
        final ItemStack itemStack5 = itemStack2.copy();
        itemStack5.addAmount(integer5);
        itemEntity1.setStack(itemStack5);
        itemStack4.subtractAmount(integer5);
        itemEntity3.setStack(itemStack4);
        itemEntity1.pickupDelay = Math.max(itemEntity1.pickupDelay, itemEntity3.pickupDelay);
        itemEntity1.age = Math.min(itemEntity1.age, itemEntity3.age);
        if (itemStack4.isEmpty()) {
            itemEntity3.remove();
        }
    }
    
    public void f() {
        this.age = 4800;
    }
    
    @Override
    protected void burn(final int integer) {
        this.damage(DamageSource.IN_FIRE, (float)integer);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.getStack().isEmpty() && this.getStack().getItem() == Items.nV && source.isExplosive()) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health -= (int)amount;
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putShort("Health", (short)this.health);
        tag.putShort("Age", (short)this.age);
        tag.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            tag.put("Thrower", TagHelper.serializeUuid(this.getThrower()));
        }
        if (this.getOwner() != null) {
            tag.put("Owner", TagHelper.serializeUuid(this.getOwner()));
        }
        if (!this.getStack().isEmpty()) {
            tag.put("Item", this.getStack().toTag(new CompoundTag()));
        }
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.age = tag.getShort("Age");
        if (tag.containsKey("PickupDelay")) {
            this.pickupDelay = tag.getShort("PickupDelay");
        }
        if (tag.containsKey("Owner", 10)) {
            this.owner = TagHelper.deserializeUuid(tag.getCompound("Owner"));
        }
        if (tag.containsKey("Thrower", 10)) {
            this.thrower = TagHelper.deserializeUuid(tag.getCompound("Thrower"));
        }
        final CompoundTag compoundTag2 = tag.getCompound("Item");
        this.setStack(ItemStack.fromTag(compoundTag2));
        if (this.getStack().isEmpty()) {
            this.remove();
        }
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        if (this.world.isClient) {
            return;
        }
        final ItemStack itemStack2 = this.getStack();
        final Item item3 = itemStack2.getItem();
        final int integer4 = itemStack2.getAmount();
        if (this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(playerEntity.getUuid())) && playerEntity.inventory.insertStack(itemStack2)) {
            playerEntity.sendPickup(this, integer4);
            if (itemStack2.isEmpty()) {
                this.remove();
                itemStack2.setAmount(integer4);
            }
            playerEntity.increaseStat(Stats.e.getOrCreateStat(item3), integer4);
        }
    }
    
    @Override
    public TextComponent getName() {
        final TextComponent textComponent1 = this.getCustomName();
        if (textComponent1 != null) {
            return textComponent1;
        }
        return new TranslatableTextComponent(this.getStack().getTranslationKey(), new Object[0]);
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType newDimension) {
        final Entity entity2 = super.changeDimension(newDimension);
        if (!this.world.isClient && entity2 instanceof ItemEntity) {
            ((ItemEntity)entity2).tryMerge();
        }
        return entity2;
    }
    
    public ItemStack getStack() {
        return this.getDataTracker().<ItemStack>get(ItemEntity.STACK);
    }
    
    public void setStack(final ItemStack itemStack) {
        this.getDataTracker().<ItemStack>set(ItemEntity.STACK, itemStack);
    }
    
    @Nullable
    public UUID getOwner() {
        return this.owner;
    }
    
    public void setOwner(@Nullable final UUID uUID) {
        this.owner = uUID;
    }
    
    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }
    
    public void setThrower(@Nullable final UUID uUID) {
        this.thrower = uUID;
    }
    
    @Environment(EnvType.CLIENT)
    public int getAge() {
        return this.age;
    }
    
    public void setToDefaultPickupDelay() {
        this.pickupDelay = 10;
    }
    
    public void resetPickupDelay() {
        this.pickupDelay = 0;
    }
    
    public void setPickupDelayInfinite() {
        this.pickupDelay = 32767;
    }
    
    public void setPickupDelay(final int integer) {
        this.pickupDelay = integer;
    }
    
    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }
    
    public void s() {
        this.age = -6000;
    }
    
    public void u() {
        this.setPickupDelayInfinite();
        this.age = 5999;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        STACK = DataTracker.<ItemStack>registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }
}
