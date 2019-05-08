package net.minecraft.item;

import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.ActionResult;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class GlassBottleItem extends Item
{
    public GlassBottleItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final List<AreaEffectCloudEntity> list4 = world.<AreaEffectCloudEntity>getEntities(AreaEffectCloudEntity.class, player.getBoundingBox().expand(2.0), areaEffectCloudEntity -> areaEffectCloudEntity != null && areaEffectCloudEntity.isAlive() && areaEffectCloudEntity.getOwner() instanceof EnderDragonEntity);
        final ItemStack itemStack5 = player.getStackInHand(hand);
        if (!list4.isEmpty()) {
            final AreaEffectCloudEntity areaEffectCloudEntity2 = list4.get(0);
            areaEffectCloudEntity2.setRadius(areaEffectCloudEntity2.getRadius() - 0.5f);
            world.playSound(null, player.x, player.y, player.z, SoundEvents.ap, SoundCategory.g, 1.0f, 1.0f);
            return new TypedActionResult<ItemStack>(ActionResult.a, this.fill(itemStack5, player, new ItemStack(Items.oR)));
        }
        final HitResult hitResult6 = Item.getHitResult(world, player, RayTraceContext.FluidHandling.b);
        if (hitResult6.getType() == HitResult.Type.NONE) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack5);
        }
        if (hitResult6.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos7 = ((BlockHitResult)hitResult6).getBlockPos();
            if (!world.canPlayerModifyAt(player, blockPos7)) {
                return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack5);
            }
            if (world.getFluidState(blockPos7).matches(FluidTags.a)) {
                world.playSound(player, player.x, player.y, player.z, SoundEvents.ao, SoundCategory.g, 1.0f, 1.0f);
                return new TypedActionResult<ItemStack>(ActionResult.a, this.fill(itemStack5, player, PotionUtil.setPotion(new ItemStack(Items.ml), Potions.b)));
            }
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack5);
    }
    
    protected ItemStack fill(final ItemStack emptyBottle, final PlayerEntity player, final ItemStack filledBottle) {
        emptyBottle.subtractAmount(1);
        player.incrementStat(Stats.c.getOrCreateStat(this));
        if (emptyBottle.isEmpty()) {
            return filledBottle;
        }
        if (!player.inventory.insertStack(filledBottle)) {
            player.dropItem(filledBottle, false);
        }
        return emptyBottle;
    }
}
