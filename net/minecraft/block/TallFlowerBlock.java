package net.minecraft.block;

import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;

public class TallFlowerBlock extends TallPlantBlock implements Fertilizable
{
    public TallFlowerBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        return false;
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        Block.dropStack(world, pos, new ItemStack(this));
    }
}
