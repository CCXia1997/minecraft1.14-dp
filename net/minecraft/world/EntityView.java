package net.minecraft.world;

import java.util.UUID;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import java.util.Iterator;
import java.util.function.Function;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.Set;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BoundingBox;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public interface EntityView
{
    List<Entity> getEntities(@Nullable final Entity arg1, final BoundingBox arg2, @Nullable final Predicate<? super Entity> arg3);
    
     <T extends Entity> List<T> getEntities(final Class<? extends T> arg1, final BoundingBox arg2, @Nullable final Predicate<? super T> arg3);
    
    List<? extends PlayerEntity> getPlayers();
    
    default List<Entity> getEntities(@Nullable final Entity except, final BoundingBox box) {
        return this.getEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
    }
    
    default boolean intersectsEntities(@Nullable final Entity except, final VoxelShape shape) {
        return shape.isEmpty() || this.getEntities(except, shape.getBoundingBox()).stream().filter(entity2 -> !entity2.removed && entity2.i && (except == null || !entity2.isConnectedThroughVehicle(except))).noneMatch(entity -> VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND));
    }
    
    default <T extends Entity> List<T> getEntities(final Class<? extends T> entityClass, final BoundingBox box) {
        return this.<T>getEntities(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }
    
    default Stream<VoxelShape> getCollisionShapes(@Nullable final Entity entity, final VoxelShape entityBoundingBox, final Set<Entity> otherEntities) {
        if (entityBoundingBox.isEmpty()) {
            return Stream.<VoxelShape>empty();
        }
        final BoundingBox boundingBox3 = entityBoundingBox.getBoundingBox();
        return this.getEntities(entity, boundingBox3.expand(0.25)).stream().filter(entity3 -> !otherEntities.contains(entity3) && (entity == null || !entity.isConnectedThroughVehicle(entity3))).<VoxelShape>flatMap(entity3 -> Stream.<BoundingBox>of(new BoundingBox[] { entity3.ap(), (entity == null) ? null : entity.j(entity3) }).filter(Objects::nonNull).filter(boundingBox2 -> boundingBox2.intersects(boundingBox3)).map(VoxelShapes::cuboid));
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final double x, final double y, final double z, final double maxDistance, @Nullable final Predicate<Entity> targetPredicate) {
        double double10 = -1.0;
        PlayerEntity playerEntity12 = null;
        for (final PlayerEntity playerEntity13 : this.getPlayers()) {
            if (targetPredicate != null && !targetPredicate.test(playerEntity13)) {
                continue;
            }
            final double double11 = playerEntity13.squaredDistanceTo(x, y, z);
            if ((maxDistance >= 0.0 && double11 >= maxDistance * maxDistance) || (double10 != -1.0 && double11 >= double10)) {
                continue;
            }
            double10 = double11;
            playerEntity12 = playerEntity13;
        }
        return playerEntity12;
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final Entity entity, final double maxDistance) {
        return this.getClosestPlayer(entity.x, entity.y, entity.z, maxDistance, false);
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final double x, final double y, final double z, final double maxDistance, final boolean ignoreCreative) {
        final Predicate<Entity> predicate10 = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(x, y, z, maxDistance, predicate10);
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final double x, final double z, final double maxDistance) {
        double double7 = -1.0;
        PlayerEntity playerEntity9 = null;
        for (final PlayerEntity playerEntity10 : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity10)) {
                continue;
            }
            final double double8 = playerEntity10.squaredDistanceTo(x, playerEntity10.y, z);
            if ((maxDistance >= 0.0 && double8 >= maxDistance * maxDistance) || (double7 != -1.0 && double8 >= double7)) {
                continue;
            }
            double7 = double8;
            playerEntity9 = playerEntity10;
        }
        return playerEntity9;
    }
    
    default boolean isPlayerInRange(final double x, final double y, final double z, final double range) {
        for (final PlayerEntity playerEntity10 : this.getPlayers()) {
            if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity10)) {
                if (!EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity10)) {
                    continue;
                }
                final double double11 = playerEntity10.squaredDistanceTo(x, y, z);
                if (range < 0.0 || double11 < range * range) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final TargetPredicate targetPredicate, final LivingEntity livingEntity) {
        return this.<PlayerEntity>getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, livingEntity.x, livingEntity.y, livingEntity.z);
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final TargetPredicate targetPredicate, final LivingEntity livingEntity, final double x, final double y, final double z) {
        return this.<PlayerEntity>getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, x, y, z);
    }
    
    @Nullable
    default PlayerEntity getClosestPlayer(final TargetPredicate targetPredicate, final double x, final double y, final double z) {
        return this.<PlayerEntity>getClosestEntity(this.getPlayers(), targetPredicate, (LivingEntity)null, x, y, z);
    }
    
    @Nullable
    default <T extends LivingEntity> T getClosestEntity(final Class<? extends T> entityClass, final TargetPredicate targetPredicate, @Nullable final LivingEntity livingEntity, final double x, final double y, final double z, final BoundingBox boundingBox10) {
        return this.<T>getClosestEntity(this.getEntities(entityClass, boundingBox10, null), targetPredicate, livingEntity, x, y, z);
    }
    
    @Nullable
    default <T extends LivingEntity> T getClosestEntity(final List<? extends T> entityList, final TargetPredicate targetPredicate, @Nullable final LivingEntity livingEntity, final double x, final double y, final double z) {
        double double10 = -1.0;
        T livingEntity2 = null;
        for (final T livingEntity3 : entityList) {
            if (!targetPredicate.test(livingEntity, livingEntity3)) {
                continue;
            }
            final double double11 = livingEntity3.squaredDistanceTo(x, y, z);
            if (double10 != -1.0 && double11 >= double10) {
                continue;
            }
            double10 = double11;
            livingEntity2 = livingEntity3;
        }
        return livingEntity2;
    }
    
    default List<PlayerEntity> getPlayersInBox(final TargetPredicate targetPredicate, final LivingEntity livingEntity, final BoundingBox boundingBox) {
        final List<PlayerEntity> list4 = Lists.newArrayList();
        for (final PlayerEntity playerEntity6 : this.getPlayers()) {
            if (boundingBox.contains(playerEntity6.x, playerEntity6.y, playerEntity6.z) && targetPredicate.test(livingEntity, playerEntity6)) {
                list4.add(playerEntity6);
            }
        }
        return list4;
    }
    
    default <T extends LivingEntity> List<T> getTargets(final Class<? extends T> entityClass, final TargetPredicate targetPredicate, final LivingEntity targettingEntity, final BoundingBox box) {
        final List<T> list5 = this.<T>getEntities(entityClass, box, null);
        final List<T> list6 = Lists.newArrayList();
        for (final T livingEntity8 : list5) {
            if (targetPredicate.test(targettingEntity, livingEntity8)) {
                list6.add(livingEntity8);
            }
        }
        return list6;
    }
    
    @Nullable
    default PlayerEntity getPlayerByUuid(final UUID uuid) {
        for (int integer2 = 0; integer2 < this.getPlayers().size(); ++integer2) {
            final PlayerEntity playerEntity3 = (PlayerEntity)this.getPlayers().get(integer2);
            if (uuid.equals(playerEntity3.getUuid())) {
                return playerEntity3;
            }
        }
        return null;
    }
}
