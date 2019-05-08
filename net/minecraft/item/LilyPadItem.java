package net.minecraft.item;

import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.block.Block;

public class LilyPadItem extends BlockItem
{
    public LilyPadItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        return ActionResult.PASS;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final HitResult hitResult5 = Item.getHitResult(world, player, RayTraceContext.FluidHandling.b);
        if (hitResult5.getType() == HitResult.Type.NONE) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        if (hitResult5.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult6 = (BlockHitResult)hitResult5;
            final BlockPos blockPos7 = blockHitResult6.getBlockPos();
            final Direction direction8 = blockHitResult6.getSide();
            if (!world.canPlayerModifyAt(player, blockPos7) || !player.canPlaceOn(blockPos7.offset(direction8), direction8, itemStack4)) {
                return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
            }
            final BlockPos blockPos8 = blockPos7.up();
            final BlockState blockState10 = world.getBlockState(blockPos7);
            final Material material11 = blockState10.getMaterial();
            final FluidState fluidState12 = world.getFluidState(blockPos7);
            if ((fluidState12.getFluid() == Fluids.WATER || material11 == Material.ICE) && world.isAir(blockPos8)) {
                world.setBlockState(blockPos8, Blocks.dM.getDefaultState(), 11);
                if (player instanceof ServerPlayerEntity) {
                    Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)player, blockPos8, itemStack4);
                }
                if (!player.abilities.creativeMode) {
                    itemStack4.subtractAmount(1);
                }
                player.incrementStat(Stats.c.getOrCreateStat(this));
                world.playSound(player, blockPos7, SoundEvents.mS, SoundCategory.e, 1.0f, 1.0f);
                return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
            }
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
}
