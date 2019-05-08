package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;

public class EndCrystalItem extends Item
{
    public EndCrystalItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() != Blocks.bJ && blockState4.getBlock() != Blocks.z) {
            return ActionResult.c;
        }
        final BlockPos blockPos4 = blockPos3.up();
        if (!world2.isAir(blockPos4)) {
            return ActionResult.c;
        }
        final double double6 = blockPos4.getX();
        final double double7 = blockPos4.getY();
        final double double8 = blockPos4.getZ();
        final List<Entity> list12 = world2.getEntities((Entity)null, new BoundingBox(double6, double7, double8, double6 + 1.0, double7 + 2.0, double8 + 1.0));
        if (!list12.isEmpty()) {
            return ActionResult.c;
        }
        if (!world2.isClient) {
            final EnderCrystalEntity enderCrystalEntity13 = new EnderCrystalEntity(world2, double6 + 0.5, double7, double8 + 0.5);
            enderCrystalEntity13.setShowBottom(false);
            world2.spawnEntity(enderCrystalEntity13);
            if (world2.dimension instanceof TheEndDimension) {
                final EnderDragonFight enderDragonFight14 = ((TheEndDimension)world2.dimension).q();
                enderDragonFight14.respawnDragon();
            }
        }
        usageContext.getItemStack().subtractAmount(1);
        return ActionResult.a;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
}
