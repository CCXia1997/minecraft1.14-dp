package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;

public class EnderEyeItem extends Item
{
    public EnderEyeItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() != Blocks.dV || blockState4.<Boolean>get((Property<Boolean>)EndPortalFrameBlock.EYE)) {
            return ActionResult.PASS;
        }
        if (world2.isClient) {
            return ActionResult.a;
        }
        final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)blockState4).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, true);
        Block.pushEntitiesUpBeforeBlockChange(blockState4, blockState5, world2, blockPos3);
        world2.setBlockState(blockPos3, blockState5, 2);
        world2.updateHorizontalAdjacent(blockPos3, Blocks.dV);
        usageContext.getItemStack().subtractAmount(1);
        world2.playLevelEvent(1503, blockPos3, 0);
        final BlockPattern.Result result6 = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world2, blockPos3);
        if (result6 != null) {
            final BlockPos blockPos4 = result6.getFrontTopLeft().add(-3, 0, -3);
            for (int integer8 = 0; integer8 < 3; ++integer8) {
                for (int integer9 = 0; integer9 < 3; ++integer9) {
                    world2.setBlockState(blockPos4.add(integer8, 0, integer9), Blocks.dU.getDefaultState(), 2);
                }
            }
            world2.playGlobalEvent(1038, blockPos4.add(1, 0, 1), 0);
        }
        return ActionResult.a;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final HitResult hitResult5 = Item.getHitResult(world, player, RayTraceContext.FluidHandling.NONE);
        if (hitResult5.getType() == HitResult.Type.BLOCK && world.getBlockState(((BlockHitResult)hitResult5).getBlockPos()).getBlock() == Blocks.dV) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        player.setCurrentHand(hand);
        if (!world.isClient) {
            final BlockPos blockPos6 = world.getChunkManager().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(player), 100, false);
            if (blockPos6 != null) {
                final EnderEyeEntity enderEyeEntity7 = new EnderEyeEntity(world, player.x, player.y + player.getHeight() / 2.0f, player.z);
                enderEyeEntity7.b(itemStack4);
                enderEyeEntity7.a(blockPos6);
                world.spawnEntity(enderEyeEntity7);
                if (player instanceof ServerPlayerEntity) {
                    Criterions.USED_ENDER_EYE.handle((ServerPlayerEntity)player, blockPos6);
                }
                world.playSound(null, player.x, player.y, player.z, SoundEvents.cE, SoundCategory.g, 0.5f, 0.4f / (EnderEyeItem.random.nextFloat() * 0.4f + 0.8f));
                world.playLevelEvent(null, 1003, new BlockPos(player), 0);
                if (!player.abilities.creativeMode) {
                    itemStack4.subtractAmount(1);
                }
                player.incrementStat(Stats.c.getOrCreateStat(this));
                return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
            }
        }
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
}
