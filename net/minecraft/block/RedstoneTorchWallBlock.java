package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import java.util.Random;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class RedstoneTorchWallBlock extends RedstoneTorchBlock
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty c;
    
    protected RedstoneTorchWallBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)RedstoneTorchWallBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)RedstoneTorchWallBlock.c, true));
    }
    
    @Override
    public String getTranslationKey() {
        return this.getItem().getTranslationKey();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return WallTorchBlock.getBoundingShape(state);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return Blocks.bL.canPlaceAt(state, world, pos);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        return Blocks.bL.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = Blocks.bL.getPlacementState(ctx);
        return (blockState2 == null) ? null : ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Comparable>with((Property<Comparable>)RedstoneTorchWallBlock.FACING, (Comparable)blockState2.<V>get((Property<V>)RedstoneTorchWallBlock.FACING));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)RedstoneTorchWallBlock.c)) {
            return;
        }
        final Direction direction5 = state.<Direction>get((Property<Direction>)RedstoneTorchWallBlock.FACING).getOpposite();
        final double double6 = 0.27;
        final double double7 = pos.getX() + 0.5 + (rnd.nextDouble() - 0.5) * 0.2 + 0.27 * direction5.getOffsetX();
        final double double8 = pos.getY() + 0.7 + (rnd.nextDouble() - 0.5) * 0.2 + 0.22;
        final double double9 = pos.getZ() + 0.5 + (rnd.nextDouble() - 0.5) * 0.2 + 0.27 * direction5.getOffsetZ();
        world.addParticle(DustParticleParameters.RED, double7, double8, double9, 0.0, 0.0, 0.0);
    }
    
    @Override
    protected boolean a(final World world, final BlockPos pos, final BlockState state) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)RedstoneTorchWallBlock.FACING).getOpposite();
        return world.isEmittingRedstonePower(pos.offset(direction4), direction4);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (state.<Boolean>get((Property<Boolean>)RedstoneTorchWallBlock.c) && state.<Comparable>get((Property<Comparable>)RedstoneTorchWallBlock.FACING) != facing) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return Blocks.bL.rotate(state, rotation);
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return Blocks.bL.mirror(state, mirror);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RedstoneTorchWallBlock.FACING, RedstoneTorchWallBlock.c);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        c = RedstoneTorchBlock.LIT;
    }
}
