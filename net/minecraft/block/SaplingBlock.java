package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class SaplingBlock extends PlantBlock implements Fertilizable
{
    public static final IntegerProperty STAGE;
    protected static final VoxelShape SHAPE;
    private final SaplingGenerator generator;
    
    protected SaplingBlock(final SaplingGenerator saplingGenerator, final Settings settings) {
        super(settings);
        this.generator = saplingGenerator;
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SaplingBlock.STAGE, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return SaplingBlock.SHAPE;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        super.onScheduledTick(state, world, pos, random);
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
            this.generate(world, pos, state, random);
        }
    }
    
    public void generate(final IWorld world, final BlockPos pos, final BlockState state, final Random random) {
        if (state.<Integer>get((Property<Integer>)SaplingBlock.STAGE) == 0) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)SaplingBlock.STAGE), 4);
        }
        else {
            this.generator.generate(world, pos, state, random);
        }
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return world.random.nextFloat() < 0.45;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        this.generate(world, pos, state, random);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SaplingBlock.STAGE);
    }
    
    static {
        STAGE = Properties.SAPLING_STAGE;
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    }
}
