package net.minecraft.predicate.entity;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import com.google.common.base.Predicates;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;

public final class EntityPredicates
{
    public static final Predicate<Entity> VALID_ENTITY;
    public static final Predicate<LivingEntity> VALID_ENTITY_LIVING;
    public static final Predicate<Entity> NOT_MOUNTED;
    public static final Predicate<Entity> VALID_INVENTORIES;
    public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR;
    public static final Predicate<Entity> EXCEPT_SPECTATOR;
    
    public static Predicate<Entity> maximumDistance(final double x, final double double3, final double double5, final double double7) {
        final double double8 = double7 * double7;
        final double n;
        return entity9 -> entity9 != null && entity9.squaredDistanceTo(x, double3, double5) <= n;
    }
    
    public static Predicate<Entity> canBePushedBy(final Entity entity) {
        final AbstractTeam abstractTeam2 = entity.getScoreboardTeam();
        final AbstractTeam.CollisionRule collisionRule3 = (abstractTeam2 == null) ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam2.getCollisionRule();
        if (collisionRule3 == AbstractTeam.CollisionRule.b) {
            return Predicates.alwaysFalse();
        }
        AbstractTeam abstractTeam3;
        AbstractTeam.CollisionRule collisionRule4;
        final AbstractTeam abstractTeam4;
        boolean boolean7;
        final AbstractTeam.CollisionRule collisionRule5;
        return EntityPredicates.EXCEPT_SPECTATOR.and(entity4 -> {
            if (!entity4.isPushable()) {
                return false;
            }
            else if (entity.world.isClient && (!(entity4 instanceof PlayerEntity) || !entity4.isMainPlayer())) {
                return false;
            }
            else {
                abstractTeam3 = entity4.getScoreboardTeam();
                collisionRule4 = ((abstractTeam3 == null) ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam3.getCollisionRule());
                if (collisionRule4 == AbstractTeam.CollisionRule.b) {
                    return false;
                }
                else {
                    boolean7 = (abstractTeam4 != null && abstractTeam4.isEqual(abstractTeam3));
                    return ((collisionRule5 != AbstractTeam.CollisionRule.d && collisionRule4 != AbstractTeam.CollisionRule.d) || !boolean7) && ((collisionRule5 != AbstractTeam.CollisionRule.c && collisionRule4 != AbstractTeam.CollisionRule.c) || boolean7);
                }
            }
        });
    }
    
    public static Predicate<Entity> rides(final Entity entity) {
        return entity2 -> {
            while (entity2.hasVehicle()) {
                entity2 = entity2.getVehicle();
                if (entity2 == entity) {
                    return false;
                }
            }
            return true;
        };
    }
    
    static {
        VALID_ENTITY = Entity::isAlive;
        VALID_ENTITY_LIVING = LivingEntity::isAlive;
        NOT_MOUNTED = (entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle());
        VALID_INVENTORIES = (entity -> entity instanceof Inventory && entity.isAlive());
        EXCEPT_CREATIVE_OR_SPECTATOR = (entity -> !(entity instanceof PlayerEntity) || (!entity.isSpectator() && !entity.isCreative()));
        EXCEPT_SPECTATOR = (entity -> !entity.isSpectator());
    }
    
    public static class CanPickup implements Predicate<Entity>
    {
        private final ItemStack itemstack;
        
        public CanPickup(final ItemStack itemStack) {
            this.itemstack = itemStack;
        }
        
        public boolean a(@Nullable final Entity context) {
            if (!context.isAlive()) {
                return false;
            }
            if (!(context instanceof LivingEntity)) {
                return false;
            }
            final LivingEntity livingEntity2 = (LivingEntity)context;
            return livingEntity2.canPickUp(this.itemstack);
        }
    }
}
