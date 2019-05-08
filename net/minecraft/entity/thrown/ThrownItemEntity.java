package net.minecraft.entity.thrown;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.FlyingItemEntity;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = awj.class) })
public abstract class ThrownItemEntity extends ThrownEntity implements FlyingItemEntity
{
    private static final TrackedData<ItemStack> ITEM;
    
    public ThrownItemEntity(final EntityType<? extends ThrownItemEntity> type, final World world) {
        super(type, world);
    }
    
    public ThrownItemEntity(final EntityType<? extends ThrownItemEntity> type, final double x, final double y, final double z, final World world) {
        super(type, x, y, z, world);
    }
    
    public ThrownItemEntity(final EntityType<? extends ThrownItemEntity> type, final LivingEntity owner, final World world) {
        super(type, owner, world);
    }
    
    public void setItem(final ItemStack itemStack) {
        if (itemStack.getItem() != this.getDefaultItem() || itemStack.hasTag()) {
            this.getDataTracker().<ItemStack>set(ThrownItemEntity.ITEM, (ItemStack)SystemUtil.<T>consume((T)itemStack.copy(), itemStack -> itemStack.setAmount(1)));
        }
    }
    
    protected abstract Item getDefaultItem();
    
    protected ItemStack getItem() {
        return this.getDataTracker().<ItemStack>get(ThrownItemEntity.ITEM);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getStack() {
        final ItemStack itemStack1 = this.getItem();
        return itemStack1.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemStack1;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(ThrownItemEntity.ITEM, ItemStack.EMPTY);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final ItemStack itemStack2 = this.getItem();
        if (!itemStack2.isEmpty()) {
            tag.put("Item", itemStack2.toTag(new CompoundTag()));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        final ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("Item"));
        this.setItem(itemStack2);
    }
    
    static {
        ITEM = DataTracker.<ItemStack>registerData(ThrownItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }
}
