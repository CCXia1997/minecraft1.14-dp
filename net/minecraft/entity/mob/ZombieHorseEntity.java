package net.minecraft.entity.mob;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseBaseEntity;

public class ZombieHorseEntity extends HorseBaseEntity
{
    public ZombieHorseEntity(final EntityType<? extends ZombieHorseEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
        this.getAttributeInstance(ZombieHorseEntity.JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.nZ;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.oa;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ob;
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return EntityType.ZOMBIE_HORSE.create(this.world);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isTame()) {
            return false;
        }
        if (this.isChild()) {
            return super.interactMob(player, hand);
        }
        if (player.isSneaking()) {
            this.openInventory(player);
            return true;
        }
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (!itemStack3.isEmpty()) {
            if (!this.isSaddled() && itemStack3.getItem() == Items.kB) {
                this.openInventory(player);
                return true;
            }
            if (itemStack3.interactWithEntity(player, this, hand)) {
                return true;
            }
        }
        this.putPlayerOnBack(player);
        return true;
    }
    
    @Override
    protected void initCustomGoals() {
    }
}
