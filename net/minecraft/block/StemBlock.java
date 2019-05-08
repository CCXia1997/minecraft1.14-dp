package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class StemBlock extends PlantBlock implements Fertilizable
{
    public static final IntegerProperty AGE;
    protected static final VoxelShape[] AGE_TO_SHAPE;
    private final GourdBlock gourdBlock;
    
    protected StemBlock(final GourdBlock gourdBlock, final Settings settings) {
        super(settings);
        this.gourdBlock = gourdBlock;
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)StemBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return StemBlock.AGE_TO_SHAPE[state.<Integer>get((Property<Integer>)StemBlock.AGE)];
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return floor.getBlock() == Blocks.bV;
    }
    
    @Override
    public void onScheduledTick(BlockState state, final World world, final BlockPos pos, final Random random) {
        super.onScheduledTick(state, world, pos, random);
        if (world.getLightLevel(pos, 0) < 9) {
            return;
        }
        final float float5 = CropBlock.getAvailableMoisture(this, world, pos);
        if (random.nextInt((int)(25.0f / float5) + 1) == 0) {
            final int integer6 = state.<Integer>get((Property<Integer>)StemBlock.AGE);
            if (integer6 < 7) {
                state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)StemBlock.AGE, integer6 + 1);
                world.setBlockState(pos, state, 2);
            }
            else {
                final Direction direction7 = Direction.Type.HORIZONTAL.random(random);
                final BlockPos blockPos8 = pos.offset(direction7);
                final Block block9 = world.getBlockState(blockPos8.down()).getBlock();
                if (world.getBlockState(blockPos8).isAir() && (block9 == Blocks.bV || block9 == Blocks.j || block9 == Blocks.k || block9 == Blocks.l || block9 == Blocks.i)) {
                    world.setBlockState(blockPos8, this.gourdBlock.getDefaultState());
                    world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)this.gourdBlock.getAttachedStem().getDefaultState()).<Comparable, Direction>with((Property<Comparable>)HorizontalFacingBlock.FACING, direction7));
                }
            }
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    protected Item getPickItem() {
        if (this.gourdBlock == Blocks.cI) {
            return Items.lZ;
        }
        if (this.gourdBlock == Blocks.dC) {
            return Items.ma;
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        final Item item4 = this.getPickItem();
        return (item4 == null) ? ItemStack.EMPTY : new ItemStack(item4);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return state.<Integer>get((Property<Integer>)StemBlock.AGE) != 7;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        final int integer5 = Math.min(7, state.<Integer>get((Property<Integer>)StemBlock.AGE) + MathHelper.nextInt(world.random, 2, 5));
        final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)StemBlock.AGE, integer5);
        world.setBlockState(pos, blockState6, 2);
        if (integer5 == 7) {
            blockState6.scheduledTick(world, pos, world.random);
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(StemBlock.AGE);
    }
    
    public GourdBlock getGourdBlock() {
        return this.gourdBlock;
    }
    
    static {
        AGE = Properties.AGE_7;
        AGE_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0) };
    }
}
