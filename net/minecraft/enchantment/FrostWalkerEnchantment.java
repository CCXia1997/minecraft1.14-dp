package net.minecraft.enchantment;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.util.math.Position;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;

public class FrostWalkerEnchantment extends Enchantment
{
    public FrostWalkerEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.FEET, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return integer * 10;
    }
    
    @Override
    public boolean isTreasure() {
        return true;
    }
    
    @Override
    public int getMaximumLevel() {
        return 2;
    }
    
    public static void freezeWater(final LivingEntity entity, final World world, final BlockPos blockPos, final int level) {
        if (!entity.onGround) {
            return;
        }
        final BlockState blockState5 = Blocks.iA.getDefaultState();
        final float float6 = (float)Math.min(16, 2 + level);
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (final BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-float6, -1.0, -float6), blockPos.add(float6, -1.0, float6))) {
            if (blockPos2.isWithinDistance(entity.getPos(), float6)) {
                mutable7.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                final BlockState blockState6 = world.getBlockState(mutable7);
                if (!blockState6.isAir()) {
                    continue;
                }
                final BlockState blockState7 = world.getBlockState(blockPos2);
                if (blockState7.getMaterial() != Material.WATER || blockState7.<Integer>get((Property<Integer>)FluidBlock.LEVEL) != 0 || !blockState5.canPlaceAt(world, blockPos2) || !world.canPlace(blockState5, blockPos2, VerticalEntityPosition.minValue())) {
                    continue;
                }
                world.setBlockState(blockPos2, blockState5);
                world.getBlockTickScheduler().schedule(blockPos2.toImmutable(), Blocks.iA, MathHelper.nextInt(entity.getRand(), 60, 120));
            }
        }
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.i;
    }
}
