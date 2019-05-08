package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.DyeColor;
import net.minecraft.client.block.ColoredBlock;

public class BeaconBlock extends BlockWithEntity implements ColoredBlock
{
    public BeaconBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public DyeColor getColor() {
        return DyeColor.a;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new BeaconBlockEntity();
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof BeaconBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity7);
            player.incrementStat(Stats.Z);
        }
        return true;
    }
    
    @Override
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof BeaconBlockEntity) {
                ((BeaconBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
