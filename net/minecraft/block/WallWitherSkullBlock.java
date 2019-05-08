package net.minecraft.block;

import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallWitherSkullBlock extends WallSkullBlock
{
    protected WallWitherSkullBlock(final Settings settings) {
        super(SkullBlock.Type.WITHER_SKELETON, settings);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        Blocks.eW.onPlaced(world, pos, state, placer, itemStack);
    }
}
