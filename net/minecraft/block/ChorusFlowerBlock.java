package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.state.StateFactory;
import net.minecraft.world.IWorld;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;

public class ChorusFlowerBlock extends Block
{
    public static final IntegerProperty AGE;
    private final ChorusPlantBlock plantBlock;
    
    protected ChorusFlowerBlock(final ChorusPlantBlock chorusPlantBlock, final Settings settings) {
        super(settings);
        this.plantBlock = chorusPlantBlock;
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)ChorusFlowerBlock.AGE, 0));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }
        final BlockPos blockPos5 = pos.up();
        if (!world.isAir(blockPos5) || blockPos5.getY() >= 256) {
            return;
        }
        final int integer6 = state.<Integer>get((Property<Integer>)ChorusFlowerBlock.AGE);
        if (integer6 >= 5) {
            return;
        }
        boolean boolean7 = false;
        boolean boolean8 = false;
        final BlockState blockState9 = world.getBlockState(pos.down());
        final Block block10 = blockState9.getBlock();
        if (block10 == Blocks.dW) {
            boolean7 = true;
        }
        else if (block10 == this.plantBlock) {
            int integer7 = 1;
            int integer8 = 0;
            while (integer8 < 4) {
                final Block block11 = world.getBlockState(pos.down(integer7 + 1)).getBlock();
                if (block11 == this.plantBlock) {
                    ++integer7;
                    ++integer8;
                }
                else {
                    if (block11 == Blocks.dW) {
                        boolean8 = true;
                        break;
                    }
                    break;
                }
            }
            if (integer7 < 2 || integer7 <= random.nextInt(boolean8 ? 5 : 4)) {
                boolean7 = true;
            }
        }
        else if (blockState9.isAir()) {
            boolean7 = true;
        }
        if (boolean7 && isSurroundedByAir(world, blockPos5, null) && world.isAir(pos.up(2))) {
            world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
            this.grow(world, blockPos5, integer6);
        }
        else if (integer6 < 4) {
            int integer7 = random.nextInt(4);
            if (boolean8) {
                ++integer7;
            }
            boolean boolean9 = false;
            for (int integer9 = 0; integer9 < integer7; ++integer9) {
                final Direction direction14 = Direction.Type.HORIZONTAL.random(random);
                final BlockPos blockPos6 = pos.offset(direction14);
                if (world.isAir(blockPos6) && world.isAir(blockPos6.down()) && isSurroundedByAir(world, blockPos6, direction14.getOpposite())) {
                    this.grow(world, blockPos6, integer6 + 1);
                    boolean9 = true;
                }
            }
            if (boolean9) {
                world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
            }
            else {
                this.die(world, pos);
            }
        }
        else {
            this.die(world, pos);
        }
    }
    
    private void grow(final World world, final BlockPos pos, final int age) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)ChorusFlowerBlock.AGE, age), 2);
        world.playLevelEvent(1033, pos, 0);
    }
    
    private void die(final World world, final BlockPos pos) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)ChorusFlowerBlock.AGE, 5), 2);
        world.playLevelEvent(1034, pos, 0);
    }
    
    private static boolean isSurroundedByAir(final ViewableWorld world, final BlockPos pos, @Nullable final Direction exceptDirection) {
        for (final Direction direction5 : Direction.Type.HORIZONTAL) {
            if (direction5 != exceptDirection && !world.isAir(pos.offset(direction5))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing != Direction.UP && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos.down());
        final Block block5 = blockState4.getBlock();
        if (block5 == this.plantBlock || block5 == Blocks.dW) {
            return true;
        }
        if (!blockState4.isAir()) {
            return false;
        }
        boolean boolean6 = false;
        for (final Direction direction8 : Direction.Type.HORIZONTAL) {
            final BlockState blockState5 = world.getBlockState(pos.offset(direction8));
            if (blockState5.getBlock() == this.plantBlock) {
                if (boolean6) {
                    return false;
                }
                boolean6 = true;
            }
            else {
                if (!blockState5.isAir()) {
                    return false;
                }
                continue;
            }
        }
        return boolean6;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ChorusFlowerBlock.AGE);
    }
    
    public static void generate(final IWorld world, final BlockPos pos, final Random random, final int size) {
        world.setBlockState(pos, ((ChorusPlantBlock)Blocks.ip).withConnectionProperties(world, pos), 2);
        generate(world, pos, random, pos, size, 0);
    }
    
    private static void generate(final IWorld world, final BlockPos pos, final Random random, final BlockPos blockPos4, final int size, final int layer) {
        final ChorusPlantBlock chorusPlantBlock7 = (ChorusPlantBlock)Blocks.ip;
        int integer8 = random.nextInt(4) + 1;
        if (layer == 0) {
            ++integer8;
        }
        for (int integer9 = 0; integer9 < integer8; ++integer9) {
            final BlockPos blockPos5 = pos.up(integer9 + 1);
            if (!isSurroundedByAir(world, blockPos5, null)) {
                return;
            }
            world.setBlockState(blockPos5, chorusPlantBlock7.withConnectionProperties(world, blockPos5), 2);
            world.setBlockState(blockPos5.down(), chorusPlantBlock7.withConnectionProperties(world, blockPos5.down()), 2);
        }
        boolean boolean9 = false;
        if (layer < 4) {
            int integer10 = random.nextInt(4);
            if (layer == 0) {
                ++integer10;
            }
            for (int integer11 = 0; integer11 < integer10; ++integer11) {
                final Direction direction12 = Direction.Type.HORIZONTAL.random(random);
                final BlockPos blockPos6 = pos.up(integer8).offset(direction12);
                if (Math.abs(blockPos6.getX() - blockPos4.getX()) < size) {
                    if (Math.abs(blockPos6.getZ() - blockPos4.getZ()) < size) {
                        if (world.isAir(blockPos6) && world.isAir(blockPos6.down()) && isSurroundedByAir(world, blockPos6, direction12.getOpposite())) {
                            boolean9 = true;
                            world.setBlockState(blockPos6, chorusPlantBlock7.withConnectionProperties(world, blockPos6), 2);
                            world.setBlockState(blockPos6.offset(direction12.getOpposite()), chorusPlantBlock7.withConnectionProperties(world, blockPos6.offset(direction12.getOpposite())), 2);
                            generate(world, blockPos6, random, blockPos4, size, layer + 1);
                        }
                    }
                }
            }
        }
        if (!boolean9) {
            world.setBlockState(pos.up(integer8), ((AbstractPropertyContainer<O, BlockState>)Blocks.iq.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)ChorusFlowerBlock.AGE, 5), 2);
        }
    }
    
    @Override
    public void onProjectileHit(final World world, final BlockState state, final BlockHitResult hitResult, final Entity entity) {
        final BlockPos blockPos5 = hitResult.getBlockPos();
        Block.dropStack(world, blockPos5, new ItemStack(this));
        world.breakBlock(blockPos5, true);
    }
    
    static {
        AGE = Properties.AGE_5;
    }
}
