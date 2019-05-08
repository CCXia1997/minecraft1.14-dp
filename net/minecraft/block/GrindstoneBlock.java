package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.shape.VoxelShape;

public class GrindstoneBlock extends WallMountedBlock
{
    public static final VoxelShape a;
    public static final VoxelShape b;
    public static final VoxelShape c;
    public static final VoxelShape d;
    public static final VoxelShape e;
    public static final VoxelShape f;
    public static final VoxelShape g;
    public static final VoxelShape NORTH_SOUTH_SHAPE;
    public static final VoxelShape i;
    public static final VoxelShape j;
    public static final VoxelShape k;
    public static final VoxelShape w;
    public static final VoxelShape x;
    public static final VoxelShape y;
    public static final VoxelShape z;
    public static final VoxelShape EAST_WEST_SHAPE;
    public static final VoxelShape B;
    public static final VoxelShape D;
    public static final VoxelShape E;
    public static final VoxelShape F;
    public static final VoxelShape G;
    public static final VoxelShape H;
    public static final VoxelShape I;
    public static final VoxelShape SOUTH_WALL_SHAPE;
    public static final VoxelShape K;
    public static final VoxelShape L;
    public static final VoxelShape M;
    public static final VoxelShape N;
    public static final VoxelShape O;
    public static final VoxelShape P;
    public static final VoxelShape Q;
    public static final VoxelShape NORTH_WALL_SHAPE;
    public static final VoxelShape S;
    public static final VoxelShape T;
    public static final VoxelShape U;
    public static final VoxelShape V;
    public static final VoxelShape W;
    public static final VoxelShape X;
    public static final VoxelShape Y;
    public static final VoxelShape WEST_WALL_SHAPE;
    public static final VoxelShape aa;
    public static final VoxelShape ab;
    public static final VoxelShape ac;
    public static final VoxelShape ad;
    public static final VoxelShape ae;
    public static final VoxelShape af;
    public static final VoxelShape ag;
    public static final VoxelShape EAST_WALL_SHAPE;
    public static final VoxelShape ai;
    public static final VoxelShape aj;
    public static final VoxelShape ak;
    public static final VoxelShape al;
    public static final VoxelShape am;
    public static final VoxelShape an;
    public static final VoxelShape ao;
    public static final VoxelShape NORTH_SOUTH_HANGING_SHAPE;
    public static final VoxelShape aq;
    public static final VoxelShape ar;
    public static final VoxelShape as;
    public static final VoxelShape at;
    public static final VoxelShape au;
    public static final VoxelShape av;
    public static final VoxelShape aw;
    public static final VoxelShape EAST_WEST_HANGING_SHAPE;
    private static final TranslatableTextComponent CONTAINER_NAME;
    
    protected GrindstoneBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)GrindstoneBlock.FACING, Direction.NORTH)).<WallMountLocation, WallMountLocation>with(GrindstoneBlock.FACE, WallMountLocation.b));
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    private VoxelShape getShape(final BlockState blockState) {
        final Direction direction2 = blockState.<Direction>get((Property<Direction>)GrindstoneBlock.FACING);
        switch (blockState.<WallMountLocation>get(GrindstoneBlock.FACE)) {
            case a: {
                if (direction2 == Direction.NORTH || direction2 == Direction.SOUTH) {
                    return GrindstoneBlock.NORTH_SOUTH_SHAPE;
                }
                return GrindstoneBlock.EAST_WEST_SHAPE;
            }
            case b: {
                if (direction2 == Direction.NORTH) {
                    return GrindstoneBlock.NORTH_WALL_SHAPE;
                }
                if (direction2 == Direction.SOUTH) {
                    return GrindstoneBlock.SOUTH_WALL_SHAPE;
                }
                if (direction2 == Direction.EAST) {
                    return GrindstoneBlock.EAST_WALL_SHAPE;
                }
                return GrindstoneBlock.WEST_WALL_SHAPE;
            }
            case c: {
                if (direction2 == Direction.NORTH || direction2 == Direction.SOUTH) {
                    return GrindstoneBlock.NORTH_SOUTH_HANGING_SHAPE;
                }
                return GrindstoneBlock.EAST_WEST_HANGING_SHAPE;
            }
            default: {
                return GrindstoneBlock.EAST_WEST_SHAPE;
            }
        }
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return this.getShape(state);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return this.getShape(state);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return true;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        player.openContainer(state.createContainerProvider(world, pos));
        return true;
    }
    
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new GrindstoneContainer(integer, playerInventory, BlockContext.create(world, pos)), GrindstoneBlock.CONTAINER_NAME);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)GrindstoneBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)GrindstoneBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)GrindstoneBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(GrindstoneBlock.FACING, GrindstoneBlock.FACE);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        a = Block.createCuboidShape(2.0, 0.0, 6.0, 4.0, 7.0, 10.0);
        b = Block.createCuboidShape(12.0, 0.0, 6.0, 14.0, 7.0, 10.0);
        c = Block.createCuboidShape(2.0, 7.0, 5.0, 4.0, 13.0, 11.0);
        d = Block.createCuboidShape(12.0, 7.0, 5.0, 14.0, 13.0, 11.0);
        e = VoxelShapes.union(GrindstoneBlock.a, GrindstoneBlock.c);
        f = VoxelShapes.union(GrindstoneBlock.b, GrindstoneBlock.d);
        g = VoxelShapes.union(GrindstoneBlock.e, GrindstoneBlock.f);
        NORTH_SOUTH_SHAPE = VoxelShapes.union(GrindstoneBlock.g, Block.createCuboidShape(4.0, 4.0, 2.0, 12.0, 16.0, 14.0));
        i = Block.createCuboidShape(6.0, 0.0, 2.0, 10.0, 7.0, 4.0);
        j = Block.createCuboidShape(6.0, 0.0, 12.0, 10.0, 7.0, 14.0);
        k = Block.createCuboidShape(5.0, 7.0, 2.0, 11.0, 13.0, 4.0);
        w = Block.createCuboidShape(5.0, 7.0, 12.0, 11.0, 13.0, 14.0);
        x = VoxelShapes.union(GrindstoneBlock.i, GrindstoneBlock.k);
        y = VoxelShapes.union(GrindstoneBlock.j, GrindstoneBlock.w);
        z = VoxelShapes.union(GrindstoneBlock.x, GrindstoneBlock.y);
        EAST_WEST_SHAPE = VoxelShapes.union(GrindstoneBlock.z, Block.createCuboidShape(2.0, 4.0, 4.0, 14.0, 16.0, 12.0));
        B = Block.createCuboidShape(2.0, 6.0, 0.0, 4.0, 10.0, 7.0);
        D = Block.createCuboidShape(12.0, 6.0, 0.0, 14.0, 10.0, 7.0);
        E = Block.createCuboidShape(2.0, 5.0, 7.0, 4.0, 11.0, 13.0);
        F = Block.createCuboidShape(12.0, 5.0, 7.0, 14.0, 11.0, 13.0);
        G = VoxelShapes.union(GrindstoneBlock.B, GrindstoneBlock.E);
        H = VoxelShapes.union(GrindstoneBlock.D, GrindstoneBlock.F);
        I = VoxelShapes.union(GrindstoneBlock.G, GrindstoneBlock.H);
        SOUTH_WALL_SHAPE = VoxelShapes.union(GrindstoneBlock.I, Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 16.0));
        K = Block.createCuboidShape(2.0, 6.0, 7.0, 4.0, 10.0, 16.0);
        L = Block.createCuboidShape(12.0, 6.0, 7.0, 14.0, 10.0, 16.0);
        M = Block.createCuboidShape(2.0, 5.0, 3.0, 4.0, 11.0, 9.0);
        N = Block.createCuboidShape(12.0, 5.0, 3.0, 14.0, 11.0, 9.0);
        O = VoxelShapes.union(GrindstoneBlock.K, GrindstoneBlock.M);
        P = VoxelShapes.union(GrindstoneBlock.L, GrindstoneBlock.N);
        Q = VoxelShapes.union(GrindstoneBlock.O, GrindstoneBlock.P);
        NORTH_WALL_SHAPE = VoxelShapes.union(GrindstoneBlock.Q, Block.createCuboidShape(4.0, 2.0, 0.0, 12.0, 14.0, 12.0));
        S = Block.createCuboidShape(7.0, 6.0, 2.0, 16.0, 10.0, 4.0);
        T = Block.createCuboidShape(7.0, 6.0, 12.0, 16.0, 10.0, 14.0);
        U = Block.createCuboidShape(3.0, 5.0, 2.0, 9.0, 11.0, 4.0);
        V = Block.createCuboidShape(3.0, 5.0, 12.0, 9.0, 11.0, 14.0);
        W = VoxelShapes.union(GrindstoneBlock.S, GrindstoneBlock.U);
        X = VoxelShapes.union(GrindstoneBlock.T, GrindstoneBlock.V);
        Y = VoxelShapes.union(GrindstoneBlock.W, GrindstoneBlock.X);
        WEST_WALL_SHAPE = VoxelShapes.union(GrindstoneBlock.Y, Block.createCuboidShape(0.0, 2.0, 4.0, 12.0, 14.0, 12.0));
        aa = Block.createCuboidShape(0.0, 6.0, 2.0, 9.0, 10.0, 4.0);
        ab = Block.createCuboidShape(0.0, 6.0, 12.0, 9.0, 10.0, 14.0);
        ac = Block.createCuboidShape(7.0, 5.0, 2.0, 13.0, 11.0, 4.0);
        ad = Block.createCuboidShape(7.0, 5.0, 12.0, 13.0, 11.0, 14.0);
        ae = VoxelShapes.union(GrindstoneBlock.aa, GrindstoneBlock.ac);
        af = VoxelShapes.union(GrindstoneBlock.ab, GrindstoneBlock.ad);
        ag = VoxelShapes.union(GrindstoneBlock.ae, GrindstoneBlock.af);
        EAST_WALL_SHAPE = VoxelShapes.union(GrindstoneBlock.ag, Block.createCuboidShape(4.0, 2.0, 4.0, 16.0, 14.0, 12.0));
        ai = Block.createCuboidShape(2.0, 9.0, 6.0, 4.0, 16.0, 10.0);
        aj = Block.createCuboidShape(12.0, 9.0, 6.0, 14.0, 16.0, 10.0);
        ak = Block.createCuboidShape(2.0, 3.0, 5.0, 4.0, 9.0, 11.0);
        al = Block.createCuboidShape(12.0, 3.0, 5.0, 14.0, 9.0, 11.0);
        am = VoxelShapes.union(GrindstoneBlock.ai, GrindstoneBlock.ak);
        an = VoxelShapes.union(GrindstoneBlock.aj, GrindstoneBlock.al);
        ao = VoxelShapes.union(GrindstoneBlock.am, GrindstoneBlock.an);
        NORTH_SOUTH_HANGING_SHAPE = VoxelShapes.union(GrindstoneBlock.ao, Block.createCuboidShape(4.0, 0.0, 2.0, 12.0, 12.0, 14.0));
        aq = Block.createCuboidShape(6.0, 9.0, 2.0, 10.0, 16.0, 4.0);
        ar = Block.createCuboidShape(6.0, 9.0, 12.0, 10.0, 16.0, 14.0);
        as = Block.createCuboidShape(5.0, 3.0, 2.0, 11.0, 9.0, 4.0);
        at = Block.createCuboidShape(5.0, 3.0, 12.0, 11.0, 9.0, 14.0);
        au = VoxelShapes.union(GrindstoneBlock.aq, GrindstoneBlock.as);
        av = VoxelShapes.union(GrindstoneBlock.ar, GrindstoneBlock.at);
        aw = VoxelShapes.union(GrindstoneBlock.au, GrindstoneBlock.av);
        EAST_WEST_HANGING_SHAPE = VoxelShapes.union(GrindstoneBlock.aw, Block.createCuboidShape(2.0, 0.0, 4.0, 14.0, 12.0, 12.0));
        CONTAINER_NAME = new TranslatableTextComponent("container.grindstone_title", new Object[0]);
    }
}
