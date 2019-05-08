package net.minecraft.item;

import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BoundingBox;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;

public class BoatItem extends Item
{
    private static final Predicate<Entity> a;
    private final BoatEntity.Type type;
    
    public BoatItem(final BoatEntity.Type type, final Settings settings) {
        super(settings);
        this.type = type;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final HitResult hitResult5 = Item.getHitResult(world, player, RayTraceContext.FluidHandling.c);
        if (hitResult5.getType() == HitResult.Type.NONE) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        final Vec3d vec3d6 = player.getRotationVec(1.0f);
        final double double7 = 5.0;
        final List<Entity> list9 = world.getEntities(player, player.getBoundingBox().stretch(vec3d6.multiply(5.0)).expand(1.0), BoatItem.a);
        if (!list9.isEmpty()) {
            final Vec3d vec3d7 = player.getCameraPosVec(1.0f);
            for (final Entity entity12 : list9) {
                final BoundingBox boundingBox13 = entity12.getBoundingBox().expand(entity12.getBoundingBoxMarginForTargeting());
                if (boundingBox13.contains(vec3d7)) {
                    return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
                }
            }
        }
        if (hitResult5.getType() != HitResult.Type.BLOCK) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        final BoatEntity boatEntity10 = new BoatEntity(world, hitResult5.getPos().x, hitResult5.getPos().y, hitResult5.getPos().z);
        boatEntity10.setBoatType(this.type);
        boatEntity10.yaw = player.yaw;
        if (!world.doesNotCollide(boatEntity10, boatEntity10.getBoundingBox().expand(-0.1))) {
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        if (!world.isClient) {
            world.spawnEntity(boatEntity10);
        }
        if (!player.abilities.creativeMode) {
            itemStack4.subtractAmount(1);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    static {
        a = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);
    }
}
