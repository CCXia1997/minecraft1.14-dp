package net.minecraft.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ArrowItem;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.function.Predicate;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import javax.annotation.Nullable;

public final class ProjectileUtil
{
    public static HitResult getCollision(final Entity entity, final boolean boolean2, final boolean boolean3, @Nullable final Entity entity4, final RayTraceContext.ShapeType shapeType) {
        return getCollision(entity, boolean2, boolean3, entity4, shapeType, true, entity3 -> !entity3.isSpectator() && entity3.collides() && (boolean3 || !entity3.isPartOf(entity4)) && !entity3.noClip, entity.getBoundingBox().stretch(entity.getVelocity()).expand(1.0));
    }
    
    public static HitResult getCollision(final Entity entity, final BoundingBox box, final Predicate<Entity> entityCollisionPredicate, final RayTraceContext.ShapeType shapeType, final boolean boolean5) {
        return getCollision(entity, boolean5, false, null, shapeType, false, entityCollisionPredicate, box);
    }
    
    @Nullable
    public static EntityHitResult getEntityCollision(final World world, final Entity entity, final Vec3d vec3d3, final Vec3d vec3d4, final BoundingBox boundingBox, final Predicate<Entity> predicate) {
        return getEntityCollision(world, entity, vec3d3, vec3d4, boundingBox, predicate, Double.MAX_VALUE);
    }
    
    private static HitResult getCollision(final Entity entity, final boolean boolean2, final boolean boolean3, @Nullable final Entity entity4, final RayTraceContext.ShapeType shapeType, final boolean boolean6, final Predicate<Entity> entityCollisionPredicate, final BoundingBox boz) {
        final double double9 = entity.x;
        final double double10 = entity.y;
        final double double11 = entity.z;
        final Vec3d vec3d15 = entity.getVelocity();
        final World world16 = entity.world;
        final Vec3d vec3d16 = new Vec3d(double9, double10, double11);
        if (boolean6 && !world16.doesNotCollide(entity, entity.getBoundingBox(), (Set<Entity>)((boolean3 || entity4 == null) ? ImmutableSet.of() : getEntityAndRidingEntity(entity4)))) {
            return new BlockHitResult(vec3d16, Direction.getFacing(vec3d15.x, vec3d15.y, vec3d15.z), new BlockPos(entity), false);
        }
        Vec3d vec3d17 = vec3d16.add(vec3d15);
        HitResult hitResult19 = world16.rayTrace(new RayTraceContext(vec3d16, vec3d17, shapeType, RayTraceContext.FluidHandling.NONE, entity));
        if (boolean2) {
            if (hitResult19.getType() != HitResult.Type.NONE) {
                vec3d17 = hitResult19.getPos();
            }
            final HitResult hitResult20 = getEntityCollision(world16, entity, vec3d16, vec3d17, boz, entityCollisionPredicate);
            if (hitResult20 != null) {
                hitResult19 = hitResult20;
            }
        }
        return hitResult19;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static EntityHitResult rayTrace(final Entity entity, final Vec3d vec3d2, final Vec3d vec3d3, final BoundingBox boundingBox, final Predicate<Entity> predicate, final double double6) {
        final World world8 = entity.world;
        double double7 = double6;
        Entity entity2 = null;
        Vec3d vec3d4 = null;
        for (final Entity entity3 : world8.getEntities(entity, boundingBox, predicate)) {
            final BoundingBox boundingBox2 = entity3.getBoundingBox().expand(entity3.getBoundingBoxMarginForTargeting());
            final Optional<Vec3d> optional16 = boundingBox2.rayTrace(vec3d2, vec3d3);
            if (boundingBox2.contains(vec3d2)) {
                if (double7 < 0.0) {
                    continue;
                }
                entity2 = entity3;
                vec3d4 = optional16.orElse(vec3d2);
                double7 = 0.0;
            }
            else {
                if (!optional16.isPresent()) {
                    continue;
                }
                final Vec3d vec3d5 = optional16.get();
                final double double8 = vec3d2.squaredDistanceTo(vec3d5);
                if (double8 >= double7 && double7 != 0.0) {
                    continue;
                }
                if (entity3.getTopmostVehicle() == entity.getTopmostVehicle()) {
                    if (double7 != 0.0) {
                        continue;
                    }
                    entity2 = entity3;
                    vec3d4 = vec3d5;
                }
                else {
                    entity2 = entity3;
                    vec3d4 = vec3d5;
                    double7 = double8;
                }
            }
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2, vec3d4);
    }
    
    @Nullable
    public static EntityHitResult getEntityCollision(final World world, final Entity entity, final Vec3d vec3d3, final Vec3d vec3d4, final BoundingBox boundingBox, final Predicate<Entity> predicate, final double double7) {
        double double8 = double7;
        Entity entity2 = null;
        for (final Entity entity3 : world.getEntities(entity, boundingBox, predicate)) {
            final BoundingBox boundingBox2 = entity3.getBoundingBox().expand(0.30000001192092896);
            final Optional<Vec3d> optional15 = boundingBox2.rayTrace(vec3d3, vec3d4);
            if (optional15.isPresent()) {
                final double double9 = vec3d3.squaredDistanceTo(optional15.get());
                if (double9 >= double8) {
                    continue;
                }
                entity2 = entity3;
                double8 = double9;
            }
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2);
    }
    
    private static Set<Entity> getEntityAndRidingEntity(final Entity entity) {
        final Entity entity2 = entity.getVehicle();
        return (entity2 != null) ? ImmutableSet.<Entity>of(entity, entity2) : ImmutableSet.<Entity>of(entity);
    }
    
    public static final void a(final Entity entity, final float float2) {
        final Vec3d vec3d3 = entity.getVelocity();
        final float float3 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d3));
        entity.yaw = (float)(MathHelper.atan2(vec3d3.z, vec3d3.x) * 57.2957763671875) + 90.0f;
        entity.pitch = (float)(MathHelper.atan2(float3, vec3d3.y) * 57.2957763671875) - 90.0f;
        while (entity.pitch - entity.prevPitch < -180.0f) {
            entity.prevPitch -= 360.0f;
        }
        while (entity.pitch - entity.prevPitch >= 180.0f) {
            entity.prevPitch += 360.0f;
        }
        while (entity.yaw - entity.prevYaw < -180.0f) {
            entity.prevYaw -= 360.0f;
        }
        while (entity.yaw - entity.prevYaw >= 180.0f) {
            entity.prevYaw += 360.0f;
        }
        entity.pitch = MathHelper.lerp(float2, entity.prevPitch, entity.pitch);
        entity.yaw = MathHelper.lerp(float2, entity.prevYaw, entity.yaw);
    }
    
    public static Hand getHandPossiblyHolding(final LivingEntity entity, final Item item) {
        return (entity.getMainHandStack().getItem() == item) ? Hand.a : Hand.b;
    }
    
    public static ProjectileEntity createArrowProjectile(final LivingEntity livingEntity, final ItemStack itemStack, final float float3) {
        final ArrowItem arrowItem4 = (ArrowItem)((itemStack.getItem() instanceof ArrowItem) ? itemStack.getItem() : Items.jg);
        final ProjectileEntity projectileEntity5 = arrowItem4.createProjectile(livingEntity.world, itemStack, livingEntity);
        projectileEntity5.a(livingEntity, float3);
        if (itemStack.getItem() == Items.oU && projectileEntity5 instanceof ArrowEntity) {
            ((ArrowEntity)projectileEntity5).initFromStack(itemStack);
        }
        return projectileEntity5;
    }
}
