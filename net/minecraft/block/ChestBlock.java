package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.container.Container;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.stat.Stat;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.DirectionProperty;

public class ChestBlock extends BlockWithEntity implements Waterloggable
{
    public static final DirectionProperty FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    protected static final VoxelShape SINGLE_SHAPE;
    private static final PropertyRetriever<Inventory> INVENTORY_RETRIEVER;
    private static final PropertyRetriever<NameableContainerProvider> NAME_RETRIEVER;
    
    protected ChestBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)ChestBlock.FACING, Direction.NORTH)).with(ChestBlock.CHEST_TYPE, ChestType.a)).<Comparable, Boolean>with((Property<Comparable>)ChestBlock.WATERLOGGED, false));
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
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)ChestBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (neighborState.getBlock() == this && facing.getAxis().isHorizontal()) {
            final ChestType chestType7 = neighborState.<ChestType>get(ChestBlock.CHEST_TYPE);
            if (state.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.a && chestType7 != ChestType.a && state.<Comparable>get((Property<Comparable>)ChestBlock.FACING) == neighborState.<Comparable>get((Property<Comparable>)ChestBlock.FACING) && getFacing(neighborState) == facing.getOpposite()) {
                return ((AbstractPropertyContainer<O, BlockState>)state).<ChestType, ChestType>with(ChestBlock.CHEST_TYPE, chestType7.getOpposite());
            }
        }
        else if (getFacing(state) == facing) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<ChestType, ChestType>with(ChestBlock.CHEST_TYPE, ChestType.a);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (state.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.a) {
            return ChestBlock.SINGLE_SHAPE;
        }
        switch (getFacing(state)) {
            default: {
                return ChestBlock.DOUBLE_NORTH_SHAPE;
            }
            case SOUTH: {
                return ChestBlock.DOUBLE_SOUTH_SHAPE;
            }
            case WEST: {
                return ChestBlock.DOUBLE_WEST_SHAPE;
            }
            case EAST: {
                return ChestBlock.DOUBLE_EAST_SHAPE;
            }
        }
    }
    
    public static Direction getFacing(final BlockState state) {
        final Direction direction2 = state.<Direction>get((Property<Direction>)ChestBlock.FACING);
        return (state.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.b) ? direction2.rotateYClockwise() : direction2.rotateYCounterclockwise();
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        ChestType chestType2 = ChestType.a;
        Direction direction3 = ctx.getPlayerHorizontalFacing().getOpposite();
        final FluidState fluidState4 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final boolean boolean5 = ctx.isPlayerSneaking();
        final Direction direction4 = ctx.getFacing();
        if (direction4.getAxis().isHorizontal() && boolean5) {
            final Direction direction5 = this.getNeighborChestDirection(ctx, direction4.getOpposite());
            if (direction5 != null && direction5.getAxis() != direction4.getAxis()) {
                direction3 = direction5;
                chestType2 = ((direction3.rotateYCounterclockwise() == direction4.getOpposite()) ? ChestType.c : ChestType.b);
            }
        }
        if (chestType2 == ChestType.a && !boolean5) {
            if (direction3 == this.getNeighborChestDirection(ctx, direction3.rotateYClockwise())) {
                chestType2 = ChestType.b;
            }
            else if (direction3 == this.getNeighborChestDirection(ctx, direction3.rotateYCounterclockwise())) {
                chestType2 = ChestType.c;
            }
        }
        return ((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)ChestBlock.FACING, direction3)).with(ChestBlock.CHEST_TYPE, chestType2)).<Comparable, Boolean>with((Property<Comparable>)ChestBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)ChestBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Nullable
    private Direction getNeighborChestDirection(final ItemPlacementContext itemPlacementContext, final Direction direction) {
        final BlockState blockState3 = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().offset(direction));
        return (blockState3.getBlock() == this && blockState3.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.a) ? blockState3.<Direction>get((Property<Direction>)ChestBlock.FACING) : null;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof ChestBlockEntity) {
                ((ChestBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory)blockEntity6);
            world.updateHorizontalAdjacent(pos, this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final NameableContainerProvider nameableContainerProvider7 = this.createContainerProvider(state, world, pos);
        if (nameableContainerProvider7 != null) {
            player.openContainer(nameableContainerProvider7);
            player.incrementStat(this.getOpenStat());
        }
        return true;
    }
    
    protected Stat<Identifier> getOpenStat() {
        return Stats.i.getOrCreateStat(Stats.am);
    }
    
    @Nullable
    public static <T> T retrieve(final BlockState blockState, final IWorld iWorld, final BlockPos blockPos, final boolean boolean4, final PropertyRetriever<T> propertyRetriever) {
        final BlockEntity blockEntity6 = iWorld.getBlockEntity(blockPos);
        if (!(blockEntity6 instanceof ChestBlockEntity)) {
            return null;
        }
        if (!boolean4 && isChestBlocked(iWorld, blockPos)) {
            return null;
        }
        final ChestBlockEntity chestBlockEntity7 = (ChestBlockEntity)blockEntity6;
        final ChestType chestType8 = blockState.<ChestType>get(ChestBlock.CHEST_TYPE);
        if (chestType8 == ChestType.a) {
            return propertyRetriever.getFromSingleChest(chestBlockEntity7);
        }
        final BlockPos blockPos2 = blockPos.offset(getFacing(blockState));
        final BlockState blockState2 = iWorld.getBlockState(blockPos2);
        if (blockState2.getBlock() == blockState.getBlock()) {
            final ChestType chestType9 = blockState2.<ChestType>get(ChestBlock.CHEST_TYPE);
            if (chestType9 != ChestType.a && chestType8 != chestType9 && blockState2.<Comparable>get((Property<Comparable>)ChestBlock.FACING) == blockState.<Comparable>get((Property<Comparable>)ChestBlock.FACING)) {
                if (!boolean4 && isChestBlocked(iWorld, blockPos2)) {
                    return null;
                }
                final BlockEntity blockEntity7 = iWorld.getBlockEntity(blockPos2);
                if (blockEntity7 instanceof ChestBlockEntity) {
                    final ChestBlockEntity chestBlockEntity8 = (ChestBlockEntity)((chestType8 == ChestType.c) ? chestBlockEntity7 : blockEntity7);
                    final ChestBlockEntity chestBlockEntity9 = (ChestBlockEntity)((chestType8 == ChestType.c) ? blockEntity7 : chestBlockEntity7);
                    return propertyRetriever.getFromDoubleChest(chestBlockEntity8, chestBlockEntity9);
                }
            }
        }
        return propertyRetriever.getFromSingleChest(chestBlockEntity7);
    }
    
    @Nullable
    public static Inventory getInventory(final BlockState blockState, final World world, final BlockPos blockPos, final boolean boolean4) {
        return ChestBlock.<Inventory>retrieve(blockState, world, blockPos, boolean4, ChestBlock.INVENTORY_RETRIEVER);
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return ChestBlock.<NameableContainerProvider>retrieve(state, world, pos, false, ChestBlock.NAME_RETRIEVER);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new ChestBlockEntity();
    }
    
    private static boolean isChestBlocked(final IWorld iWorld, final BlockPos blockPos) {
        return hasBlockOnTop(iWorld, blockPos) || hasOcelotOnTop(iWorld, blockPos);
    }
    
    private static boolean hasBlockOnTop(final BlockView blockView, final BlockPos blockPos) {
        final BlockPos blockPos2 = blockPos.up();
        return blockView.getBlockState(blockPos2).isSimpleFullBlock(blockView, blockPos2);
    }
    
    private static boolean hasOcelotOnTop(final IWorld iWorld, final BlockPos blockPos) {
        final List<CatEntity> list3 = iWorld.<CatEntity>getEntities(CatEntity.class, new BoundingBox(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 2, blockPos.getZ() + 1));
        if (!list3.isEmpty()) {
            for (final CatEntity catEntity5 : list3) {
                if (catEntity5.isSitting()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return Container.calculateComparatorOutput(getInventory(state, world, pos, false));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)ChestBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)ChestBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ChestBlock.FACING, ChestBlock.CHEST_TYPE, ChestBlock.WATERLOGGED);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        CHEST_TYPE = Properties.CHEST_TYPE;
        WATERLOGGED = Properties.WATERLOGGED;
        DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
        DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
        DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
        SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        INVENTORY_RETRIEVER = new PropertyRetriever<Inventory>() {
            @Override
            public Inventory getFromDoubleChest(final ChestBlockEntity chestBlockEntity1, final ChestBlockEntity chestBlockEntity2) {
                return new DoubleInventory(chestBlockEntity1, chestBlockEntity2);
            }
            
            @Override
            public Inventory getFromSingleChest(final ChestBlockEntity chestBlockEntity) {
                return chestBlockEntity;
            }
        };
        NAME_RETRIEVER = new PropertyRetriever<NameableContainerProvider>() {
            @Override
            public NameableContainerProvider getFromDoubleChest(final ChestBlockEntity chestBlockEntity1, final ChestBlockEntity chestBlockEntity2) {
                final Inventory inventory3 = new DoubleInventory(chestBlockEntity1, chestBlockEntity2);
                return new NameableContainerProvider() {
                    @Nullable
                    @Override
                    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
                        if (chestBlockEntity1.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                            chestBlockEntity1.checkLootInteraction(playerInventory.player);
                            chestBlockEntity2.checkLootInteraction(playerInventory.player);
                            return GenericContainer.createGeneric9x6(syncId, playerInventory, inventory3);
                        }
                        return null;
                    }
                    
                    @Override
                    public TextComponent getDisplayName() {
                        return new TranslatableTextComponent("container.chestDouble", new Object[0]);
                    }
                };
            }
            
            @Override
            public NameableContainerProvider getFromSingleChest(final ChestBlockEntity chestBlockEntity) {
                return chestBlockEntity;
            }
        };
    }
    
    interface PropertyRetriever<T>
    {
        T getFromDoubleChest(final ChestBlockEntity arg1, final ChestBlockEntity arg2);
        
        T getFromSingleChest(final ChestBlockEntity arg1);
    }
}
