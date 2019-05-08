package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import java.util.EnumSet;
import net.minecraft.recipe.Ingredient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.TargetPredicate;

public class TemptGoal extends Goal
{
    private static final TargetPredicate TEMPTING_ENTITY_PREDICATE;
    protected final MobEntityWithAi owner;
    private final double speed;
    private double lastPlayerX;
    private double lastPlayerY;
    private double lastPlayerZ;
    private double lastPlayerPitch;
    private double lastPlayerYaw;
    protected PlayerEntity closestPlayer;
    private int cooldown;
    private boolean active;
    private final Ingredient temptItem;
    private final boolean canBeScared;
    
    public TemptGoal(final MobEntityWithAi owner, final double speed, final Ingredient temptItem, final boolean canBeScared) {
        this(owner, speed, canBeScared, temptItem);
    }
    
    public TemptGoal(final MobEntityWithAi owner, final double speed, final boolean canBeScared, final Ingredient temptItem) {
        this.owner = owner;
        this.speed = speed;
        this.temptItem = temptItem;
        this.canBeScared = canBeScared;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        if (!(owner.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }
    
    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.closestPlayer = this.owner.world.getClosestPlayer(TemptGoal.TEMPTING_ENTITY_PREDICATE, this.owner);
        return this.closestPlayer != null && (this.isTempedBy(this.closestPlayer.getMainHandStack()) || this.isTempedBy(this.closestPlayer.getOffHandStack()));
    }
    
    protected boolean isTempedBy(final ItemStack stack) {
        return this.temptItem.a(stack);
    }
    
    @Override
    public boolean shouldContinue() {
        if (this.canBeScared()) {
            if (this.owner.squaredDistanceTo(this.closestPlayer) < 36.0) {
                if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
                    return false;
                }
                if (Math.abs(this.closestPlayer.pitch - this.lastPlayerPitch) > 5.0 || Math.abs(this.closestPlayer.yaw - this.lastPlayerYaw) > 5.0) {
                    return false;
                }
            }
            else {
                this.lastPlayerX = this.closestPlayer.x;
                this.lastPlayerY = this.closestPlayer.y;
                this.lastPlayerZ = this.closestPlayer.z;
            }
            this.lastPlayerPitch = this.closestPlayer.pitch;
            this.lastPlayerYaw = this.closestPlayer.yaw;
        }
        return this.canStart();
    }
    
    protected boolean canBeScared() {
        return this.canBeScared;
    }
    
    @Override
    public void start() {
        this.lastPlayerX = this.closestPlayer.x;
        this.lastPlayerY = this.closestPlayer.y;
        this.lastPlayerZ = this.closestPlayer.z;
        this.active = true;
    }
    
    @Override
    public void stop() {
        this.closestPlayer = null;
        this.owner.getNavigation().stop();
        this.cooldown = 100;
        this.active = false;
    }
    
    @Override
    public void tick() {
        this.owner.getLookControl().lookAt(this.closestPlayer, (float)(this.owner.dA() + 20), (float)this.owner.getLookPitchSpeed());
        if (this.owner.squaredDistanceTo(this.closestPlayer) < 6.25) {
            this.owner.getNavigation().stop();
        }
        else {
            this.owner.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    static {
        TEMPTING_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeInvulnerable().includeTeammates().ignoreEntityTargetRules().includeHidden();
    }
}
