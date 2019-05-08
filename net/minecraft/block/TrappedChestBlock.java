package net.minecraft.block;

import net.minecraft.util.math.MathHelper;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.stat.Stat;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class TrappedChestBlock extends ChestBlock
{
    public TrappedChestBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new TrappedChestBlockEntity();
    }
    
    @Override
    protected Stat<Identifier> getOpenStat() {
        return Stats.i.getOrCreateStat(Stats.ag);
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return MathHelper.clamp(ChestBlockEntity.getPlayersLookingInChestCount(view, pos), 0, 15);
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (facing == Direction.UP) {
            return state.getWeakRedstonePower(view, pos, facing);
        }
        return 0;
    }
}
