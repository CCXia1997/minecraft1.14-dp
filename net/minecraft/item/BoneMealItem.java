package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.biome.Biomes;
import net.minecraft.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.util.ActionResult;

public class BoneMealItem extends Item
{
    public BoneMealItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockPos blockPos4 = blockPos3.offset(usageContext.getFacing());
        if (useOnFertilizable(usageContext.getItemStack(), world2, blockPos3)) {
            if (!world2.isClient) {
                world2.playLevelEvent(2005, blockPos3, 0);
            }
            return ActionResult.a;
        }
        final BlockState blockState5 = world2.getBlockState(blockPos3);
        final boolean boolean6 = Block.isSolidFullSquare(blockState5, world2, blockPos3, usageContext.getFacing());
        if (boolean6 && useOnGround(usageContext.getItemStack(), world2, blockPos4, usageContext.getFacing())) {
            if (!world2.isClient) {
                world2.playLevelEvent(2005, blockPos4, 0);
            }
            return ActionResult.a;
        }
        return ActionResult.PASS;
    }
    
    public static boolean useOnFertilizable(final ItemStack stack, final World world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos);
        if (blockState4.getBlock() instanceof Fertilizable) {
            final Fertilizable fertilizable5 = (Fertilizable)blockState4.getBlock();
            if (fertilizable5.isFertilizable(world, pos, blockState4, world.isClient)) {
                if (!world.isClient) {
                    if (fertilizable5.canGrow(world, world.random, pos, blockState4)) {
                        fertilizable5.grow(world, world.random, pos, blockState4);
                    }
                    stack.subtractAmount(1);
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean useOnGround(final ItemStack stack, final World world, final BlockPos blockPos, @Nullable final Direction facing) {
        if (world.getBlockState(blockPos).getBlock() == Blocks.A && world.getFluidState(blockPos).getLevel() == 8) {
            if (!world.isClient) {
                int integer5 = 0;
            Label_0039:
                while (integer5 < 128) {
                    BlockPos blockPos2 = blockPos;
                    Biome biome7 = world.getBiome(blockPos2);
                    BlockState blockState8 = Blocks.aT.getDefaultState();
                    while (true) {
                        for (int integer6 = 0; integer6 < integer5 / 16; ++integer6) {
                            blockPos2 = blockPos2.add(BoneMealItem.random.nextInt(3) - 1, (BoneMealItem.random.nextInt(3) - 1) * BoneMealItem.random.nextInt(3) / 2, BoneMealItem.random.nextInt(3) - 1);
                            biome7 = world.getBiome(blockPos2);
                            if (Block.isShapeFullCube(world.getBlockState(blockPos2).getCollisionShape(world, blockPos2))) {
                                ++integer5;
                                continue Label_0039;
                            }
                        }
                        if (biome7 == Biomes.T || biome7 == Biomes.W) {
                            if (integer5 == 0 && facing != null && facing.getAxis().isHorizontal()) {
                                blockState8 = ((AbstractPropertyContainer<O, BlockState>)BlockTags.O.getRandom(world.random).getDefaultState()).<Comparable, Direction>with((Property<Comparable>)DeadCoralWallFanBlock.FACING, facing);
                            }
                            else if (BoneMealItem.random.nextInt(4) == 0) {
                                blockState8 = BlockTags.M.getRandom(BoneMealItem.random).getDefaultState();
                            }
                        }
                        if (blockState8.getBlock().matches(BlockTags.O)) {
                            for (int integer6 = 0; !blockState8.canPlaceAt(world, blockPos2) && integer6 < 4; blockState8 = ((AbstractPropertyContainer<O, BlockState>)blockState8).<Comparable, Direction>with((Property<Comparable>)DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(BoneMealItem.random)), ++integer6) {}
                        }
                        if (!blockState8.canPlaceAt(world, blockPos2)) {
                            continue;
                        }
                        final BlockState blockState9 = world.getBlockState(blockPos2);
                        if (blockState9.getBlock() == Blocks.A && world.getFluidState(blockPos2).getLevel() == 8) {
                            world.setBlockState(blockPos2, blockState8, 3);
                            continue;
                        }
                        if (blockState9.getBlock() == Blocks.aT && BoneMealItem.random.nextInt(10) == 0) {
                            ((Fertilizable)Blocks.aT).grow(world, BoneMealItem.random, blockPos2, blockState9);
                        }
                        continue;
                    }
                }
                stack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public static void playEffects(final IWorld world, final BlockPos pos, int amount) {
        if (amount == 0) {
            amount = 15;
        }
        final BlockState blockState4 = world.getBlockState(pos);
        if (blockState4.isAir()) {
            return;
        }
        for (int integer5 = 0; integer5 < amount; ++integer5) {
            final double double6 = BoneMealItem.random.nextGaussian() * 0.02;
            final double double7 = BoneMealItem.random.nextGaussian() * 0.02;
            final double double8 = BoneMealItem.random.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.C, pos.getX() + BoneMealItem.random.nextFloat(), pos.getY() + BoneMealItem.random.nextFloat() * blockState4.getOutlineShape(world, pos).getMaximum(Direction.Axis.Y), pos.getZ() + BoneMealItem.random.nextFloat(), double6, double7, double8);
        }
    }
}
