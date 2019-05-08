package net.minecraft.item;

import net.minecraft.util.math.EulerRotation;
import java.util.Random;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.ActionResult;

public class ArmorStandItem extends Item
{
    public ArmorStandItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final Direction direction2 = usageContext.getFacing();
        if (direction2 == Direction.DOWN) {
            return ActionResult.c;
        }
        final World world3 = usageContext.getWorld();
        final ItemPlacementContext itemPlacementContext4 = new ItemPlacementContext(usageContext);
        final BlockPos blockPos5 = itemPlacementContext4.getBlockPos();
        final BlockPos blockPos6 = blockPos5.up();
        if (!itemPlacementContext4.canPlace() || !world3.getBlockState(blockPos6).canReplace(itemPlacementContext4)) {
            return ActionResult.c;
        }
        final double double7 = blockPos5.getX();
        final double double8 = blockPos5.getY();
        final double double9 = blockPos5.getZ();
        final List<Entity> list13 = world3.getEntities((Entity)null, new BoundingBox(double7, double8, double9, double7 + 1.0, double8 + 2.0, double9 + 1.0));
        if (!list13.isEmpty()) {
            return ActionResult.c;
        }
        final ItemStack itemStack14 = usageContext.getItemStack();
        if (!world3.isClient) {
            world3.clearBlockState(blockPos5, false);
            world3.clearBlockState(blockPos6, false);
            final ArmorStandEntity armorStandEntity15 = new ArmorStandEntity(world3, double7 + 0.5, double8, double9 + 0.5);
            final float float16 = MathHelper.floor((MathHelper.wrapDegrees(usageContext.getPlayerYaw() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            armorStandEntity15.setPositionAndAngles(double7 + 0.5, double8, double9 + 0.5, float16, 0.0f);
            this.setRotations(armorStandEntity15, world3.random);
            EntityType.loadFromEntityTag(world3, usageContext.getPlayer(), armorStandEntity15, itemStack14.getTag());
            world3.spawnEntity(armorStandEntity15);
            world3.playSound(null, armorStandEntity15.x, armorStandEntity15.y, armorStandEntity15.z, SoundEvents.A, SoundCategory.e, 0.75f, 0.8f);
        }
        itemStack14.subtractAmount(1);
        return ActionResult.a;
    }
    
    private void setRotations(final ArmorStandEntity armorStand, final Random random) {
        EulerRotation eulerRotation3 = armorStand.getHeadRotation();
        float float5 = random.nextFloat() * 5.0f;
        final float float6 = random.nextFloat() * 20.0f - 10.0f;
        EulerRotation eulerRotation4 = new EulerRotation(eulerRotation3.getX() + float5, eulerRotation3.getY() + float6, eulerRotation3.getZ());
        armorStand.setHeadRotation(eulerRotation4);
        eulerRotation3 = armorStand.getBodyRotation();
        float5 = random.nextFloat() * 10.0f - 5.0f;
        eulerRotation4 = new EulerRotation(eulerRotation3.getX(), eulerRotation3.getY() + float5, eulerRotation3.getZ());
        armorStand.setBodyRotation(eulerRotation4);
    }
}
