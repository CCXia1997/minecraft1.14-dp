package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock
{
    public IceBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getLevel(Enchantments.t, stack) == 0) {
            if (world.dimension.doesWaterVaporize()) {
                world.clearBlockState(pos, false);
                return;
            }
            final Material material7 = world.getBlockState(pos.down()).getMaterial();
            if (material7.blocksMovement() || material7.isLiquid()) {
                world.setBlockState(pos, Blocks.A.getDefaultState());
            }
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getLightSubtracted(world, pos)) {
            this.melt(state, world, pos);
        }
    }
    
    protected void melt(final BlockState state, final World world, final BlockPos pos) {
        if (world.dimension.doesWaterVaporize()) {
            world.clearBlockState(pos, false);
            return;
        }
        world.setBlockState(pos, Blocks.A.getDefaultState());
        world.updateNeighbor(pos, Blocks.A, pos);
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.a;
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return entityType == EntityType.POLAR_BEAR;
    }
}
