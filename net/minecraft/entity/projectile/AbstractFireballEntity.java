package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.SystemUtil;
import net.minecraft.item.Items;
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
public abstract class AbstractFireballEntity extends ExplosiveProjectileEntity implements FlyingItemEntity
{
    private static final TrackedData<ItemStack> ITEM;
    
    public AbstractFireballEntity(final EntityType<? extends AbstractFireballEntity> type, final World world) {
        super(type, world);
    }
    
    public AbstractFireballEntity(final EntityType<? extends AbstractFireballEntity> world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final World world14) {
        super(world, double2, double4, double6, double8, double10, double12, world14);
    }
    
    public AbstractFireballEntity(final EntityType<? extends AbstractFireballEntity> world, final LivingEntity owner, final double double3, final double double5, final double double7, final World world9) {
        super(world, owner, double3, double5, double7, world9);
    }
    
    public void b(final ItemStack itemStack) {
        if (itemStack.getItem() != Items.nC || itemStack.hasTag()) {
            this.getDataTracker().<ItemStack>set(AbstractFireballEntity.ITEM, (ItemStack)SystemUtil.<T>consume((T)itemStack.copy(), itemStack -> itemStack.setAmount(1)));
        }
    }
    
    protected ItemStack getItem() {
        return this.getDataTracker().<ItemStack>get(AbstractFireballEntity.ITEM);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getStack() {
        final ItemStack itemStack1 = this.getItem();
        return itemStack1.isEmpty() ? new ItemStack(Items.nC) : itemStack1;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(AbstractFireballEntity.ITEM, ItemStack.EMPTY);
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
        this.b(itemStack2);
    }
    
    static {
        ITEM = DataTracker.<ItemStack>registerData(AbstractFireballEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }
}
