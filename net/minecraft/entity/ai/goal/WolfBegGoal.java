package net.minecraft.entity.ai.goal;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.WolfEntity;

public class WolfBegGoal extends Goal
{
    private final WolfEntity owner;
    private PlayerEntity begFrom;
    private final World world;
    private final float begDistance;
    private int timer;
    private final TargetPredicate validPlayerPredicate;
    
    public WolfBegGoal(final WolfEntity owner, final float begDistance) {
        this.owner = owner;
        this.world = owner.world;
        this.begDistance = begDistance;
        this.validPlayerPredicate = new TargetPredicate().setBaseMaxDistance(begDistance).includeInvulnerable().includeTeammates().ignoreEntityTargetRules();
        this.setControls(EnumSet.<Control>of(Control.b));
    }
    
    @Override
    public boolean canStart() {
        this.begFrom = this.world.getClosestPlayer(this.validPlayerPredicate, this.owner);
        return this.begFrom != null && this.isAttractive(this.begFrom);
    }
    
    @Override
    public boolean shouldContinue() {
        return this.begFrom.isAlive() && this.owner.squaredDistanceTo(this.begFrom) <= this.begDistance * this.begDistance && this.timer > 0 && this.isAttractive(this.begFrom);
    }
    
    @Override
    public void start() {
        this.owner.setBegging(true);
        this.timer = 40 + this.owner.getRand().nextInt(40);
    }
    
    @Override
    public void stop() {
        this.owner.setBegging(false);
        this.begFrom = null;
    }
    
    @Override
    public void tick() {
        this.owner.getLookControl().lookAt(this.begFrom.x, this.begFrom.y + this.begFrom.getStandingEyeHeight(), this.begFrom.z, 10.0f, (float)this.owner.getLookPitchSpeed());
        --this.timer;
    }
    
    private boolean isAttractive(final PlayerEntity playerEntity) {
        for (final Hand hand5 : Hand.values()) {
            final ItemStack itemStack6 = playerEntity.getStackInHand(hand5);
            if (this.owner.isTamed() && itemStack6.getItem() == Items.lB) {
                return true;
            }
            if (this.owner.isBreedingItem(itemStack6)) {
                return true;
            }
        }
        return false;
    }
}
