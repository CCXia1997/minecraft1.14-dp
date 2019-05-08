package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class ShearsItem extends Item
{
    public ShearsItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean onBlockBroken(final ItemStack stack, final World world, final BlockState state, final BlockPos pos, final LivingEntity livingEntity) {
        if (!world.isClient) {
            stack.<LivingEntity>applyDamage(1, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        }
        final Block block6 = state.getBlock();
        return state.matches(BlockTags.C) || block6 == Blocks.aP || block6 == Blocks.aQ || block6 == Blocks.aR || block6 == Blocks.aS || block6 == Blocks.dH || block6 == Blocks.ee || block6.matches(BlockTags.a) || super.onBlockBroken(stack, world, state, pos, livingEntity);
    }
    
    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        final Block block2 = blockState.getBlock();
        return block2 == Blocks.aP || block2 == Blocks.bQ || block2 == Blocks.ee;
    }
    
    @Override
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        final Block block3 = blockState.getBlock();
        if (block3 == Blocks.aP || blockState.matches(BlockTags.C)) {
            return 15.0f;
        }
        if (block3.matches(BlockTags.a)) {
            return 5.0f;
        }
        return super.getBlockBreakingSpeed(stack, blockState);
    }
}
