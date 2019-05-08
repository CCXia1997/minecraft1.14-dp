package net.minecraft.entity.ai.goal;

import net.minecraft.util.Hand;
import net.minecraft.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.mob.MobEntity;

public class HoldInHandsGoal<T extends MobEntity> extends Goal
{
    private final T a;
    private final ItemStack item;
    private final Predicate<? super T> condition;
    private final SoundEvent sound;
    
    public HoldInHandsGoal(final T mobEntity, final ItemStack itemStack, @Nullable final SoundEvent soundEvent, final Predicate<? super T> predicate) {
        this.a = mobEntity;
        this.item = itemStack;
        this.sound = soundEvent;
        this.condition = predicate;
    }
    
    @Override
    public boolean canStart() {
        return this.condition.test(this.a);
    }
    
    @Override
    public boolean shouldContinue() {
        return this.a.isUsingItem();
    }
    
    @Override
    public void start() {
        this.a.setEquippedStack(EquipmentSlot.HAND_MAIN, this.item.copy());
        this.a.setCurrentHand(Hand.a);
    }
    
    @Override
    public void stop() {
        this.a.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
        if (this.sound != null) {
            this.a.playSound(this.sound, 1.0f, this.a.getRand().nextFloat() * 0.2f + 0.9f);
        }
    }
}
