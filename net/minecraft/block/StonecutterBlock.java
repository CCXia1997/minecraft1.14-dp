package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TranslatableTextComponent;

public class StonecutterBlock extends Block
{
    private static final TranslatableTextComponent CONTAINER_NAME;
    public static final DirectionProperty FACING;
    protected static final VoxelShape SHAPE;
    
    public StonecutterBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StonecutterBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StonecutterBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        player.openContainer(state.createContainerProvider(world, pos));
        player.incrementStat(Stats.aw);
        return true;
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new StonecutterContainer(integer, playerInventory, BlockContext.create(world, pos)), StonecutterBlock.CONTAINER_NAME);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return StonecutterBlock.SHAPE;
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)StonecutterBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)StonecutterBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)StonecutterBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(StonecutterBlock.FACING);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        CONTAINER_NAME = new TranslatableTextComponent("container.stonecutter", new Object[0]);
        FACING = HorizontalFacingBlock.FACING;
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    }
}
