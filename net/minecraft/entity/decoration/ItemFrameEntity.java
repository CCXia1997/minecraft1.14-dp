package net.minecraft.entity.decoration;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.util.Hand;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.item.map.MapState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BoundingBox;
import org.apache.commons.lang3.Validate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends AbstractDecorationEntity
{
    private static final Logger e;
    private static final TrackedData<ItemStack> ITEM_STACK;
    private static final TrackedData<Integer> ROTATION;
    private float itemDropChance;
    
    public ItemFrameEntity(final EntityType<? extends ItemFrameEntity> type, final World world) {
        super(type, world);
        this.itemDropChance = 1.0f;
    }
    
    public ItemFrameEntity(final World world, final BlockPos blockPos, final Direction direction) {
        super(EntityType.ITEM_FRAME, world, blockPos);
        this.itemDropChance = 1.0f;
        this.setFacing(direction);
    }
    
    @Override
    protected float getEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.0f;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(ItemFrameEntity.ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().<Integer>startTracking(ItemFrameEntity.ROTATION, 0);
    }
    
    @Override
    protected void setFacing(final Direction direction) {
        Validate.notNull(direction);
        this.facing = direction;
        if (direction.getAxis().isHorizontal()) {
            this.pitch = 0.0f;
            this.yaw = (float)(this.facing.getHorizontal() * 90);
        }
        else {
            this.pitch = (float)(-90 * direction.getDirection().offset());
            this.yaw = 0.0f;
        }
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.f();
    }
    
    @Override
    protected void f() {
        if (this.facing == null) {
            return;
        }
        final double double1 = 0.46875;
        this.x = this.blockPos.getX() + 0.5 - this.facing.getOffsetX() * 0.46875;
        this.y = this.blockPos.getY() + 0.5 - this.facing.getOffsetY() * 0.46875;
        this.z = this.blockPos.getZ() + 0.5 - this.facing.getOffsetZ() * 0.46875;
        double double2 = this.getWidthPixels();
        double double3 = this.getHeightPixels();
        double double4 = this.getWidthPixels();
        final Direction.Axis axis9 = this.facing.getAxis();
        switch (axis9) {
            case X: {
                double2 = 1.0;
                break;
            }
            case Y: {
                double3 = 1.0;
                break;
            }
            case Z: {
                double4 = 1.0;
                break;
            }
        }
        double2 /= 32.0;
        double3 /= 32.0;
        double4 /= 32.0;
        this.setBoundingBox(new BoundingBox(this.x - double2, this.y - double3, this.z - double4, this.x + double2, this.y + double3, this.z + double4));
    }
    
    @Override
    public boolean i() {
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        final BlockState blockState1 = this.world.getBlockState(this.blockPos.offset(this.facing.getOpposite()));
        return (blockState1.getMaterial().isSolid() || (this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState1))) && this.world.getEntities(this, this.getBoundingBox(), ItemFrameEntity.PREDICATE).isEmpty();
    }
    
    @Override
    public float getBoundingBoxMarginForTargeting() {
        return 0.0f;
    }
    
    @Override
    public void kill() {
        this.removeFromFrame(this.getHeldItemStack());
        super.kill();
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!source.isExplosive() && !this.getHeldItemStack().isEmpty()) {
            if (!this.world.isClient) {
                this.b(source.getAttacker(), false);
                this.playSound(SoundEvents.fC, 1.0f, 1.0f);
            }
            return true;
        }
        return super.damage(source, amount);
    }
    
    @Override
    public int getWidthPixels() {
        return 12;
    }
    
    @Override
    public int getHeightPixels() {
        return 12;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = 16.0;
        double3 *= 64.0 * getRenderDistanceMultiplier();
        return distance < double3 * double3;
    }
    
    @Override
    public void onBreak(@Nullable final Entity entity) {
        this.playSound(SoundEvents.fA, 1.0f, 1.0f);
        this.b(entity, true);
    }
    
    @Override
    public void onPlace() {
        this.playSound(SoundEvents.fB, 1.0f, 1.0f);
    }
    
    public void b(@Nullable final Entity entity, final boolean boolean2) {
        if (!this.world.getGameRules().getBoolean("doEntityDrops")) {
            if (entity == null) {
                this.removeFromFrame(this.getHeldItemStack());
            }
            return;
        }
        ItemStack itemStack3 = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (entity instanceof PlayerEntity) {
            final PlayerEntity playerEntity4 = (PlayerEntity)entity;
            if (playerEntity4.abilities.creativeMode) {
                this.removeFromFrame(itemStack3);
                return;
            }
        }
        if (boolean2) {
            this.dropItem(Items.nG);
        }
        if (!itemStack3.isEmpty()) {
            itemStack3 = itemStack3.copy();
            this.removeFromFrame(itemStack3);
            if (this.random.nextFloat() < this.itemDropChance) {
                this.dropStack(itemStack3);
            }
        }
    }
    
    private void removeFromFrame(final ItemStack map) {
        if (map.getItem() == Items.lV) {
            final MapState mapState2 = FilledMapItem.getOrCreateMapState(map, this.world);
            mapState2.removeFrame(this.blockPos, this.getEntityId());
            mapState2.setDirty(true);
        }
        map.setHoldingItemFrame(null);
    }
    
    public ItemStack getHeldItemStack() {
        return this.getDataTracker().<ItemStack>get(ItemFrameEntity.ITEM_STACK);
    }
    
    public void setHeldItemStack(final ItemStack itemStack) {
        this.setHeldItemStack(itemStack, true);
    }
    
    public void setHeldItemStack(ItemStack value, final boolean boolean2) {
        if (!value.isEmpty()) {
            value = value.copy();
            value.setAmount(1);
            value.setHoldingItemFrame(this);
        }
        this.getDataTracker().<ItemStack>set(ItemFrameEntity.ITEM_STACK, value);
        if (!value.isEmpty()) {
            this.playSound(SoundEvents.fz, 1.0f, 1.0f);
        }
        if (boolean2 && this.blockPos != null) {
            this.world.updateHorizontalAdjacent(this.blockPos, Blocks.AIR);
        }
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (slot == 0) {
            this.setHeldItemStack(item);
            return true;
        }
        return false;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (data.equals(ItemFrameEntity.ITEM_STACK)) {
            final ItemStack itemStack2 = this.getHeldItemStack();
            if (!itemStack2.isEmpty() && itemStack2.getHoldingItemFrame() != this) {
                itemStack2.setHoldingItemFrame(this);
            }
        }
    }
    
    public int getRotation() {
        return this.getDataTracker().<Integer>get(ItemFrameEntity.ROTATION);
    }
    
    public void setRotation(final int integer) {
        this.setRotation(integer, true);
    }
    
    private void setRotation(final int value, final boolean boolean2) {
        this.getDataTracker().<Integer>set(ItemFrameEntity.ROTATION, value % 8);
        if (boolean2 && this.blockPos != null) {
            this.world.updateHorizontalAdjacent(this.blockPos, Blocks.AIR);
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (!this.getHeldItemStack().isEmpty()) {
            tag.put("Item", this.getHeldItemStack().toTag(new CompoundTag()));
            tag.putByte("ItemRotation", (byte)this.getRotation());
            tag.putFloat("ItemDropChance", this.itemDropChance);
        }
        tag.putByte("Facing", (byte)this.facing.getId());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        final CompoundTag compoundTag2 = tag.getCompound("Item");
        if (compoundTag2 != null && !compoundTag2.isEmpty()) {
            final ItemStack itemStack3 = ItemStack.fromTag(compoundTag2);
            if (itemStack3.isEmpty()) {
                ItemFrameEntity.e.warn("Unable to load item from: {}", compoundTag2);
            }
            final ItemStack itemStack4 = this.getHeldItemStack();
            if (!itemStack4.isEmpty() && !ItemStack.areEqual(itemStack3, itemStack4)) {
                this.removeFromFrame(itemStack4);
            }
            this.setHeldItemStack(itemStack3, false);
            this.setRotation(tag.getByte("ItemRotation"), false);
            if (tag.containsKey("ItemDropChance", 99)) {
                this.itemDropChance = tag.getFloat("ItemDropChance");
            }
        }
        this.setFacing(Direction.byId(tag.getByte("Facing")));
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (!this.world.isClient) {
            if (this.getHeldItemStack().isEmpty()) {
                if (!itemStack3.isEmpty()) {
                    this.setHeldItemStack(itemStack3);
                    if (!player.abilities.creativeMode) {
                        itemStack3.subtractAmount(1);
                    }
                }
            }
            else {
                this.playSound(SoundEvents.fD, 1.0f, 1.0f);
                this.setRotation(this.getRotation() + 1);
            }
        }
        return true;
    }
    
    public int getComparatorPower() {
        if (this.getHeldItemStack().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), this.facing.getId(), this.getDecorationBlockPos());
    }
    
    static {
        e = LogManager.getLogger();
        ITEM_STACK = DataTracker.<ItemStack>registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        ROTATION = DataTracker.<Integer>registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
