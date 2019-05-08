package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.TargetPredicate;

public class RevengeGoal extends TrackTargetGoal
{
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE;
    private boolean groupRevenge;
    private int lastAttackedTime;
    private final Class<?>[] noRevengeTypes;
    private Class<?>[] noHelpTypes;
    
    public RevengeGoal(final MobEntityWithAi mobEntityWithAi, final Class<?>... arr) {
        super(mobEntityWithAi, true);
        this.noRevengeTypes = arr;
        this.setControls(EnumSet.<Control>of(Control.d));
    }
    
    @Override
    public boolean canStart() {
        final int integer1 = this.entity.getLastAttackedTime();
        final LivingEntity livingEntity2 = this.entity.getAttacker();
        if (integer1 == this.lastAttackedTime || livingEntity2 == null) {
            return false;
        }
        for (final Class<?> class6 : this.noRevengeTypes) {
            if (class6.isAssignableFrom(livingEntity2.getClass())) {
                return false;
            }
        }
        return this.canTrack(livingEntity2, RevengeGoal.VALID_AVOIDABLES_PREDICATE);
    }
    
    public RevengeGoal setGroupRevenge(final Class<?>... noHelpTypes) {
        this.groupRevenge = true;
        this.noHelpTypes = noHelpTypes;
        return this;
    }
    
    @Override
    public void start() {
        this.entity.setTarget(this.entity.getAttacker());
        this.target = this.entity.getTarget();
        this.lastAttackedTime = this.entity.getLastAttackedTime();
        this.maxTimeWithoutVisibility = 300;
        if (this.groupRevenge) {
            this.callSameTypeForRevenge();
        }
        super.start();
    }
    
    protected void callSameTypeForRevenge() {
        final double double1 = this.getFollowRange();
        final List<MobEntity> list3 = this.entity.world.<MobEntity>getEntities(this.entity.getClass(), new BoundingBox(this.entity.x, this.entity.y, this.entity.z, this.entity.x + 1.0, this.entity.y + 1.0, this.entity.z + 1.0).expand(double1, 10.0, double1));
        for (final MobEntity mobEntity5 : list3) {
            if (this.entity == mobEntity5) {
                continue;
            }
            if (mobEntity5.getTarget() != null) {
                continue;
            }
            if (this.entity instanceof TameableEntity && ((TameableEntity)this.entity).getOwner() != ((TameableEntity)mobEntity5).getOwner()) {
                continue;
            }
            if (mobEntity5.isTeammate(this.entity.getAttacker())) {
                continue;
            }
            if (this.noHelpTypes != null) {
                boolean boolean6 = false;
                for (final Class<?> class10 : this.noHelpTypes) {
                    if (mobEntity5.getClass() == class10) {
                        boolean6 = true;
                        break;
                    }
                }
                if (boolean6) {
                    continue;
                }
            }
            this.setMobEntityTarget(mobEntity5, this.entity.getAttacker());
        }
    }
    
    protected void setMobEntityTarget(final MobEntity mobEntity, final LivingEntity livingEntity) {
        mobEntity.setTarget(livingEntity);
    }
    
    static {
        VALID_AVOIDABLES_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
    }
}
