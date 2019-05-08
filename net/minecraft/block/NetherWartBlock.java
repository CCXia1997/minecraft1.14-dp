package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class NetherWartBlock extends PlantBlock
{
    public static final IntegerProperty AGE;
    private static final VoxelShape[] AGE_TO_SHAPE;
    
    protected NetherWartBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)NetherWartBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return NetherWartBlock.AGE_TO_SHAPE[state.<Integer>get((Property<Integer>)NetherWartBlock.AGE)];
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return floor.getBlock() == Blocks.cK;
    }
    
    @Override
    public void onScheduledTick(BlockState state, final World world, final BlockPos pos, final Random random) {
        final int integer5 = state.<Integer>get((Property<Integer>)NetherWartBlock.AGE);
        if (integer5 < 3 && random.nextInt(10) == 0) {
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)NetherWartBlock.AGE, integer5 + 1);
            world.setBlockState(pos, state, 2);
        }
        super.onScheduledTick(state, world, pos, random);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(Items.mk);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(NetherWartBlock.AGE);
    }
    
    static {
        AGE = Properties.AGE_3;
        AGE_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0) };
    }
}
