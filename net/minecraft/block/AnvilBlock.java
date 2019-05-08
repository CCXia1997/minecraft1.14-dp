package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.DirectionProperty;

public class AnvilBlock extends FallingBlock
{
    public static final DirectionProperty FACING;
    private static final VoxelShape b;
    private static final VoxelShape c;
    private static final VoxelShape d;
    private static final VoxelShape e;
    private static final VoxelShape f;
    private static final VoxelShape g;
    private static final VoxelShape h;
    private static final VoxelShape X_AXIS_SHAPE;
    private static final VoxelShape Z_AXIS_SHAPE;
    private static final TranslatableTextComponent CONTAINER_NAME;
    
    public AnvilBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AnvilBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AnvilBlock.FACING, ctx.getPlayerHorizontalFacing().rotateYClockwise());
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        player.openContainer(state.createContainerProvider(world, pos));
        return true;
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new AnvilContainer(integer, playerInventory, BlockContext.create(world, pos)), AnvilBlock.CONTAINER_NAME);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)AnvilBlock.FACING);
        if (direction5.getAxis() == Direction.Axis.X) {
            return AnvilBlock.X_AXIS_SHAPE;
        }
        return AnvilBlock.Z_AXIS_SHAPE;
    }
    
    @Override
    protected void configureFallingBlockEntity(final FallingBlockEntity entity) {
        entity.setHurtEntities(true);
    }
    
    @Override
    public void onLanding(final World world, final BlockPos pos, final BlockState fallingBlockState, final BlockState currentStateInPos) {
        world.playLevelEvent(1031, pos, 0);
    }
    
    @Override
    public void onDestroyedOnLanding(final World world, final BlockPos pos) {
        world.playLevelEvent(1029, pos, 0);
    }
    
    @Nullable
    public static BlockState getLandingState(final BlockState fallingState) {
        final Block block2 = fallingState.getBlock();
        if (block2 == Blocks.fg) {
            return ((AbstractPropertyContainer<O, BlockState>)Blocks.fh.getDefaultState()).<Comparable, Comparable>with((Property<Comparable>)AnvilBlock.FACING, (Comparable)fallingState.<V>get((Property<V>)AnvilBlock.FACING));
        }
        if (block2 == Blocks.fh) {
            return ((AbstractPropertyContainer<O, BlockState>)Blocks.fi.getDefaultState()).<Comparable, Comparable>with((Property<Comparable>)AnvilBlock.FACING, (Comparable)fallingState.<V>get((Property<V>)AnvilBlock.FACING));
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)AnvilBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)AnvilBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AnvilBlock.FACING);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        b = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
        c = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
        d = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
        e = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
        f = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
        g = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
        h = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
        X_AXIS_SHAPE = VoxelShapes.union(AnvilBlock.b, AnvilBlock.c, AnvilBlock.d, AnvilBlock.e);
        Z_AXIS_SHAPE = VoxelShapes.union(AnvilBlock.b, AnvilBlock.f, AnvilBlock.g, AnvilBlock.h);
        CONTAINER_NAME = new TranslatableTextComponent("container.repair", new Object[0]);
    }
}
