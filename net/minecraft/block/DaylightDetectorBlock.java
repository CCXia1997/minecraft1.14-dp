package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;

public class DaylightDetectorBlock extends BlockWithEntity
{
    public static final IntegerProperty POWER;
    public static final BooleanProperty INVERTED;
    protected static final VoxelShape SHAPE;
    
    public DaylightDetectorBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)DaylightDetectorBlock.POWER, 0)).<Comparable, Boolean>with((Property<Comparable>)DaylightDetectorBlock.INVERTED, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return DaylightDetectorBlock.SHAPE;
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Integer>get((Property<Integer>)DaylightDetectorBlock.POWER);
    }
    
    public static void updateState(final BlockState blockState, final World world, final BlockPos blockPos) {
        if (!world.dimension.hasSkyLight()) {
            return;
        }
        int integer4 = world.getLightLevel(LightType.SKY, blockPos) - world.getAmbientDarkness();
        float float5 = world.b(1.0f);
        final boolean boolean6 = blockState.<Boolean>get((Property<Boolean>)DaylightDetectorBlock.INVERTED);
        if (boolean6) {
            integer4 = 15 - integer4;
        }
        else if (integer4 > 0) {
            final float float6 = (float5 < 3.1415927f) ? 0.0f : 6.2831855f;
            float5 += (float6 - float5) * 0.2f;
            integer4 = Math.round(integer4 * MathHelper.cos(float5));
        }
        integer4 = MathHelper.clamp(integer4, 0, 15);
        if (blockState.<Integer>get((Property<Integer>)DaylightDetectorBlock.POWER) != integer4) {
            world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Integer>with((Property<Comparable>)DaylightDetectorBlock.POWER, integer4), 3);
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (!player.canModifyWorld()) {
            return super.activate(state, world, pos, player, hand, blockHitResult);
        }
        if (world.isClient) {
            return true;
        }
        final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)DaylightDetectorBlock.INVERTED);
        world.setBlockState(pos, blockState7, 4);
        updateState(blockState7, world, pos);
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new DaylightDetectorBlockEntity();
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(DaylightDetectorBlock.POWER, DaylightDetectorBlock.INVERTED);
    }
    
    static {
        POWER = Properties.POWER;
        INVERTED = Properties.INVERTED;
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    }
}
