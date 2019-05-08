package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider
{
    protected BlockWithEntity(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    public boolean onBlockAction(final BlockState state, final World world, final BlockPos pos, final int type, final int data) {
        super.onBlockAction(state, world, pos, type, data);
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        return blockEntity6 != null && blockEntity6.onBlockAction(type, data);
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        return (blockEntity4 instanceof NameableContainerProvider) ? blockEntity4 : null;
    }
}
