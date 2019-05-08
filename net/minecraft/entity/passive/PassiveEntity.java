package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntityWithAi;

public abstract class PassiveEntity extends MobEntityWithAi
{
    private static final TrackedData<Boolean> CHILD;
    protected int breedingAge;
    protected int forcedAge;
    protected int happyTicksRemaining;
    
    protected PassiveEntity(final EntityType<? extends PassiveEntity> type, final World world) {
        super(type, world);
    }
    
    @Nullable
    public abstract PassiveEntity createChild(final PassiveEntity arg1);
    
    protected void onPlayerSpawnedChild(final PlayerEntity player, final PassiveEntity child) {
    }
    
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final Item item4 = itemStack3.getItem();
        if (item4 instanceof SpawnEggItem && ((SpawnEggItem)item4).isOfSameEntityType(itemStack3.getTag(), this.getType())) {
            if (!this.world.isClient) {
                final PassiveEntity passiveEntity5 = this.createChild(this);
                if (passiveEntity5 != null) {
                    passiveEntity5.setBreedingAge(-24000);
                    passiveEntity5.setPositionAndAngles(this.x, this.y, this.z, 0.0f, 0.0f);
                    this.world.spawnEntity(passiveEntity5);
                    if (itemStack3.hasDisplayName()) {
                        passiveEntity5.setCustomName(itemStack3.getDisplayName());
                    }
                    this.onPlayerSpawnedChild(player, passiveEntity5);
                    if (!player.abilities.creativeMode) {
                        itemStack3.subtractAmount(1);
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(PassiveEntity.CHILD, false);
    }
    
    public int getBreedingAge() {
        if (this.world.isClient) {
            return this.dataTracker.<Boolean>get(PassiveEntity.CHILD) ? -1 : 1;
        }
        return this.breedingAge;
    }
    
    public void growUp(final int age, final boolean overGrow) {
        final int integer4;
        int integer3 = integer4 = this.getBreedingAge();
        integer3 += age * 20;
        if (integer3 > 0) {
            integer3 = 0;
        }
        final int integer5 = integer3 - integer4;
        this.setBreedingAge(integer3);
        if (overGrow) {
            this.forcedAge += integer5;
            if (this.happyTicksRemaining == 0) {
                this.happyTicksRemaining = 40;
            }
        }
        if (this.getBreedingAge() == 0) {
            this.setBreedingAge(this.forcedAge);
        }
    }
    
    public void growUp(final int age) {
        this.growUp(age, false);
    }
    
    public void setBreedingAge(final int age) {
        final int integer2 = this.breedingAge;
        this.breedingAge = age;
        if ((integer2 < 0 && age >= 0) || (integer2 >= 0 && age < 0)) {
            this.dataTracker.<Boolean>set(PassiveEntity.CHILD, age < 0);
            this.onGrowUp();
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Age", this.getBreedingAge());
        tag.putInt("ForcedAge", this.forcedAge);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setBreedingAge(tag.getInt("Age"));
        this.forcedAge = tag.getInt("ForcedAge");
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (PassiveEntity.CHILD.equals(data)) {
            this.refreshSize();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.world.isClient) {
            if (this.happyTicksRemaining > 0) {
                if (this.happyTicksRemaining % 4 == 0) {
                    this.world.addParticle(ParticleTypes.C, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), 0.0, 0.0, 0.0);
                }
                --this.happyTicksRemaining;
            }
        }
        else if (this.isAlive()) {
            int integer1 = this.getBreedingAge();
            if (integer1 < 0) {
                ++integer1;
                this.setBreedingAge(integer1);
            }
            else if (integer1 > 0) {
                --integer1;
                this.setBreedingAge(integer1);
            }
        }
    }
    
    protected void onGrowUp() {
    }
    
    @Override
    public boolean isChild() {
        return this.getBreedingAge() < 0;
    }
    
    static {
        CHILD = DataTracker.<Boolean>registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
