package net.minecraft.item;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;

public class SignItem extends WallStandingBlockItem
{
    public SignItem(final Settings settings, final Block block2, final Block block3) {
        super(block2, block3, settings);
    }
    
    @Override
    protected boolean afterBlockPlaced(final BlockPos blockPos, final World world, @Nullable final PlayerEntity playerEntity, final ItemStack itemStack, final BlockState blockState) {
        final boolean boolean6 = super.afterBlockPlaced(blockPos, world, playerEntity, itemStack, blockState);
        if (!world.isClient && !boolean6 && playerEntity != null) {
            playerEntity.openEditSignScreen((SignBlockEntity)world.getBlockEntity(blockPos));
        }
        return boolean6;
    }
}
