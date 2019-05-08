package net.minecraft.block;

import java.util.List;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallPlayerSkullBlock extends WallSkullBlock
{
    protected WallPlayerSkullBlock(final Settings settings) {
        super(SkullBlock.Type.PLAYER, settings);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        Blocks.fa.onPlaced(world, pos, state, placer, itemStack);
    }
    
    @Override
    public List<ItemStack> getDroppedStacks(final BlockState state, final LootContext.Builder builder) {
        return Blocks.fa.getDroppedStacks(state, builder);
    }
}
