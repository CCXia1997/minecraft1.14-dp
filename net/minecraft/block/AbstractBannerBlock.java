package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.DyeColor;

public abstract class AbstractBannerBlock extends BlockWithEntity
{
    private final DyeColor color;
    
    protected AbstractBannerBlock(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
    }
    
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new BannerBlockEntity(this.color);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof BannerBlockEntity) {
                ((BannerBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof BannerBlockEntity) {
            return ((BannerBlockEntity)blockEntity4).getPickStack(state);
        }
        return super.getPickStack(world, pos, state);
    }
    
    public DyeColor getColor() {
        return this.color;
    }
}
