package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EndRodBlock extends FacingBlock
{
    protected static final VoxelShape Y_SHAPE;
    protected static final VoxelShape Z_SHAPE;
    protected static final VoxelShape X_SHAPE;
    
    protected EndRodBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndRodBlock.FACING, Direction.UP));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)EndRodBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)EndRodBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)EndRodBlock.FACING, mirror.apply(state.<Direction>get((Property<Direction>)EndRodBlock.FACING)));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction>get((Property<Direction>)EndRodBlock.FACING).getAxis()) {
            default: {
                return EndRodBlock.X_SHAPE;
            }
            case Z: {
                return EndRodBlock.Z_SHAPE;
            }
            case Y: {
                return EndRodBlock.Y_SHAPE;
            }
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Direction direction2 = ctx.getFacing();
        final BlockState blockState3 = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction2.getOpposite()));
        if (blockState3.getBlock() == this && blockState3.<Comparable>get((Property<Comparable>)EndRodBlock.FACING) == direction2) {
            return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndRodBlock.FACING, direction2.getOpposite());
        }
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)EndRodBlock.FACING, direction2);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)EndRodBlock.FACING);
        final double double6 = pos.getX() + 0.55 - rnd.nextFloat() * 0.1f;
        final double double7 = pos.getY() + 0.55 - rnd.nextFloat() * 0.1f;
        final double double8 = pos.getZ() + 0.55 - rnd.nextFloat() * 0.1f;
        final double double9 = 0.4f - (rnd.nextFloat() + rnd.nextFloat()) * 0.4f;
        if (rnd.nextInt(5) == 0) {
            world.addParticle(ParticleTypes.t, double6 + direction5.getOffsetX() * double9, double7 + direction5.getOffsetY() * double9, double8 + direction5.getOffsetZ() * double9, rnd.nextGaussian() * 0.005, rnd.nextGaussian() * 0.005, rnd.nextGaussian() * 0.005);
        }
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(EndRodBlock.FACING);
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.a;
    }
    
    static {
        Y_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
        Z_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
        X_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    }
}
