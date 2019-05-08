package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class SpawnerBlock extends BlockWithEntity
{
    protected SpawnerBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new MobSpawnerBlockEntity();
    }
    
    @Override
    public void onStacksDropped(final BlockState state, final World world, final BlockPos pos, final ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        final int integer5 = 15 + world.random.nextInt(15) + world.random.nextInt(15);
        this.dropExperience(world, pos, integer5);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
}
