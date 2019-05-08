package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.GenericContainer;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class EnderChestBlock extends BlockWithEntity implements Waterloggable
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    public static final TranslatableTextComponent CONTAINER_NAME;
    
    protected EnderChestBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)EnderChestBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)EnderChestBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return EnderChestBlock.SHAPE;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.b;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)EnderChestBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite())).<Comparable, Boolean>with((Property<Comparable>)EnderChestBlock.WATERLOGGED, fluidState2.getFluid() == Fluids.WATER);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final EnderChestInventory enderChestInventory7 = player.getEnderChestInventory();
        final BlockEntity blockEntity8 = world.getBlockEntity(pos);
        if (enderChestInventory7 == null || !(blockEntity8 instanceof EnderChestBlockEntity)) {
            return true;
        }
        final BlockPos blockPos9 = pos.up();
        if (world.getBlockState(blockPos9).isSimpleFullBlock(world, blockPos9)) {
            return true;
        }
        if (world.isClient) {
            return true;
        }
        final EnderChestBlockEntity enderChestBlockEntity10 = (EnderChestBlockEntity)blockEntity8;
        enderChestInventory7.setCurrentBlockEntity(enderChestBlockEntity10);
        player.openContainer(new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> GenericContainer.createGeneric9x3(integer, playerInventory, enderChestInventory7), EnderChestBlock.CONTAINER_NAME));
        player.incrementStat(Stats.ah);
        return true;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new EnderChestBlockEntity();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        for (int integer5 = 0; integer5 < 3; ++integer5) {
            final int integer6 = rnd.nextInt(2) * 2 - 1;
            final int integer7 = rnd.nextInt(2) * 2 - 1;
            final double double8 = pos.getX() + 0.5 + 0.25 * integer6;
            final double double9 = pos.getY() + rnd.nextFloat();
            final double double10 = pos.getZ() + 0.5 + 0.25 * integer7;
            final double double11 = rnd.nextFloat() * integer6;
            final double double12 = (rnd.nextFloat() - 0.5) * 0.125;
            final double double13 = rnd.nextFloat() * integer7;
            world.addParticle(ParticleTypes.O, double8, double9, double10, double11, double12, double13);
        }
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)EnderChestBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)EnderChestBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)EnderChestBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(EnderChestBlock.FACING, EnderChestBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)EnderChestBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)EnderChestBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        CONTAINER_NAME = new TranslatableTextComponent("container.enderchest", new Object[0]);
    }
}
