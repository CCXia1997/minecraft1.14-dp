package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.stream.Collector;
import net.minecraft.util.SystemUtil;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.TheEndDimension;
import java.util.Random;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;

public class FireBlock extends Block
{
    public static final IntegerProperty AGE;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES;
    private final Object2IntMap<Block> burnChances;
    private final Object2IntMap<Block> spreadChances;
    
    protected FireBlock(final Settings settings) {
        super(settings);
        this.burnChances = (Object2IntMap<Block>)new Object2IntOpenHashMap();
        this.spreadChances = (Object2IntMap<Block>)new Object2IntOpenHashMap();
        this.setDefaultState((((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)FireBlock.AGE, 0)).with((Property<Comparable>)FireBlock.NORTH, false)).with((Property<Comparable>)FireBlock.EAST, false)).with((Property<Comparable>)FireBlock.SOUTH, false)).with((Property<Comparable>)FireBlock.WEST, false)).<Comparable, Boolean>with((Property<Comparable>)FireBlock.UP, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.empty();
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (this.canPlaceAt(state, world, pos)) {
            return ((AbstractPropertyContainer<O, BlockState>)this.getStateForPosition(world, pos)).<Comparable, Comparable>with((Property<Comparable>)FireBlock.AGE, (Comparable)state.<V>get((Property<V>)FireBlock.AGE));
        }
        return Blocks.AIR.getDefaultState();
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
    }
    
    public BlockState getStateForPosition(final BlockView world, final BlockPos pos) {
        final BlockPos blockPos3 = pos.down();
        final BlockState blockState4 = world.getBlockState(blockPos3);
        if (this.isFlammable(blockState4) || Block.isSolidFullSquare(blockState4, world, blockPos3, Direction.UP)) {
            return this.getDefaultState();
        }
        BlockState blockState5 = this.getDefaultState();
        for (final Direction direction9 : Direction.values()) {
            final BooleanProperty booleanProperty10 = FireBlock.DIRECTION_PROPERTIES.get(direction9);
            if (booleanProperty10 != null) {
                blockState5 = ((AbstractPropertyContainer<O, BlockState>)blockState5).<Comparable, Boolean>with((Property<Comparable>)booleanProperty10, this.isFlammable(world.getBlockState(pos.offset(direction9))));
            }
        }
        return blockState5;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        return Block.isSolidFullSquare(world.getBlockState(blockPos4), world, blockPos4, Direction.UP) || this.areBlocksAroundFlammable(world, pos);
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 30;
    }
    
    @Override
    public void onScheduledTick(BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!world.getGameRules().getBoolean("doFireTick")) {
            return;
        }
        if (!state.canPlaceAt(world, pos)) {
            world.clearBlockState(pos, false);
        }
        final Block block5 = world.getBlockState(pos.down()).getBlock();
        final boolean boolean6 = (world.dimension instanceof TheEndDimension && block5 == Blocks.z) || block5 == Blocks.cJ || block5 == Blocks.iB;
        final int integer7 = state.<Integer>get((Property<Integer>)FireBlock.AGE);
        if (!boolean6 && world.isRaining() && this.isRainingAround(world, pos) && random.nextFloat() < 0.2f + integer7 * 0.03f) {
            world.clearBlockState(pos, false);
            return;
        }
        final int integer8 = Math.min(15, integer7 + random.nextInt(3) / 2);
        if (integer7 != integer8) {
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)FireBlock.AGE, integer8);
            world.setBlockState(pos, state, 4);
        }
        if (!boolean6) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world) + random.nextInt(10));
            if (!this.areBlocksAroundFlammable(world, pos)) {
                final BlockPos blockPos9 = pos.down();
                if (!Block.isSolidFullSquare(world.getBlockState(blockPos9), world, blockPos9, Direction.UP) || integer7 > 3) {
                    world.clearBlockState(pos, false);
                }
                return;
            }
            if (integer7 == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(pos.down()))) {
                world.clearBlockState(pos, false);
                return;
            }
        }
        final boolean boolean7 = world.hasHighHumidity(pos);
        final int integer9 = boolean7 ? -50 : 0;
        this.trySpreadingFire(world, pos.east(), 300 + integer9, random, integer7);
        this.trySpreadingFire(world, pos.west(), 300 + integer9, random, integer7);
        this.trySpreadingFire(world, pos.down(), 250 + integer9, random, integer7);
        this.trySpreadingFire(world, pos.up(), 250 + integer9, random, integer7);
        this.trySpreadingFire(world, pos.north(), 300 + integer9, random, integer7);
        this.trySpreadingFire(world, pos.south(), 300 + integer9, random, integer7);
        final BlockPos.Mutable mutable11 = new BlockPos.Mutable();
        for (int integer10 = -1; integer10 <= 1; ++integer10) {
            for (int integer11 = -1; integer11 <= 1; ++integer11) {
                for (int integer12 = -1; integer12 <= 4; ++integer12) {
                    if (integer10 != 0 || integer12 != 0 || integer11 != 0) {
                        int integer13 = 100;
                        if (integer12 > 1) {
                            integer13 += (integer12 - 1) * 100;
                        }
                        mutable11.set(pos).setOffset(integer10, integer12, integer11);
                        final int integer14 = this.getBurnChance(world, mutable11);
                        if (integer14 > 0) {
                            int integer15 = (integer14 + 40 + world.getDifficulty().getId() * 7) / (integer7 + 30);
                            if (boolean7) {
                                integer15 /= 2;
                            }
                            if (integer15 > 0 && random.nextInt(integer13) <= integer15) {
                                if (!world.isRaining() || !this.isRainingAround(world, mutable11)) {
                                    final int integer16 = Math.min(15, integer7 + random.nextInt(5) / 4);
                                    world.setBlockState(mutable11, ((AbstractPropertyContainer<O, BlockState>)this.getStateForPosition(world, mutable11)).<Comparable, Integer>with((Property<Comparable>)FireBlock.AGE, integer16), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected boolean isRainingAround(final World world, final BlockPos pos) {
        return world.hasRain(pos) || world.hasRain(pos.west()) || world.hasRain(pos.east()) || world.hasRain(pos.north()) || world.hasRain(pos.south());
    }
    
    private int getSpreadChance(final BlockState blockState) {
        if (blockState.<Comparable>contains((Property<Comparable>)Properties.WATERLOGGED) && blockState.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED)) {
            return 0;
        }
        return this.spreadChances.getInt(blockState.getBlock());
    }
    
    private int getBurnChance(final BlockState blockState) {
        if (blockState.<Comparable>contains((Property<Comparable>)Properties.WATERLOGGED) && blockState.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED)) {
            return 0;
        }
        return this.burnChances.getInt(blockState.getBlock());
    }
    
    private void trySpreadingFire(final World world, final BlockPos blockPos, final int spreadFactor, final Random random, final int integer5) {
        final int integer6 = this.getSpreadChance(world.getBlockState(blockPos));
        if (random.nextInt(spreadFactor) < integer6) {
            final BlockState blockState7 = world.getBlockState(blockPos);
            if (random.nextInt(integer5 + 10) < 5 && !world.hasRain(blockPos)) {
                final int integer7 = Math.min(integer5 + random.nextInt(5) / 4, 15);
                world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)this.getStateForPosition(world, blockPos)).<Comparable, Integer>with((Property<Comparable>)FireBlock.AGE, integer7), 3);
            }
            else {
                world.clearBlockState(blockPos, false);
            }
            final Block block8 = blockState7.getBlock();
            if (block8 instanceof TntBlock) {
                final TntBlock tntBlock = (TntBlock)block8;
                TntBlock.primeTnt(world, blockPos);
            }
        }
    }
    
    private boolean areBlocksAroundFlammable(final BlockView world, final BlockPos pos) {
        for (final Direction direction6 : Direction.values()) {
            if (this.isFlammable(world.getBlockState(pos.offset(direction6)))) {
                return true;
            }
        }
        return false;
    }
    
    private int getBurnChance(final ViewableWorld world, final BlockPos pos) {
        if (!world.isAir(pos)) {
            return 0;
        }
        int integer3 = 0;
        for (final Direction direction7 : Direction.values()) {
            final BlockState blockState8 = world.getBlockState(pos.offset(direction7));
            integer3 = Math.max(this.getBurnChance(blockState8), integer3);
        }
        return integer3;
    }
    
    public boolean isFlammable(final BlockState blockState) {
        return this.getBurnChance(blockState) > 0;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if ((world.dimension.getType() == DimensionType.a || world.dimension.getType() == DimensionType.b) && ((PortalBlock)Blocks.cM).a(world, pos)) {
            return;
        }
        if (!state.canPlaceAt(world, pos)) {
            world.clearBlockState(pos, false);
            return;
        }
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world) + world.random.nextInt(10));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (rnd.nextInt(24) == 0) {
            world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.dp, SoundCategory.e, 1.0f + rnd.nextFloat(), rnd.nextFloat() * 0.7f + 0.3f, false);
        }
        final BlockPos blockPos5 = pos.down();
        final BlockState blockState6 = world.getBlockState(blockPos5);
        if (this.isFlammable(blockState6) || Block.isSolidFullSquare(blockState6, world, blockPos5, Direction.UP)) {
            for (int integer7 = 0; integer7 < 3; ++integer7) {
                final double double8 = pos.getX() + rnd.nextDouble();
                final double double9 = pos.getY() + rnd.nextDouble() * 0.5 + 0.5;
                final double double10 = pos.getZ() + rnd.nextDouble();
                world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
            }
        }
        else {
            if (this.isFlammable(world.getBlockState(pos.west()))) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    final double double8 = pos.getX() + rnd.nextDouble() * 0.10000000149011612;
                    final double double9 = pos.getY() + rnd.nextDouble();
                    final double double10 = pos.getZ() + rnd.nextDouble();
                    world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.east()))) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    final double double8 = pos.getX() + 1 - rnd.nextDouble() * 0.10000000149011612;
                    final double double9 = pos.getY() + rnd.nextDouble();
                    final double double10 = pos.getZ() + rnd.nextDouble();
                    world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.north()))) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    final double double8 = pos.getX() + rnd.nextDouble();
                    final double double9 = pos.getY() + rnd.nextDouble();
                    final double double10 = pos.getZ() + rnd.nextDouble() * 0.10000000149011612;
                    world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.south()))) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    final double double8 = pos.getX() + rnd.nextDouble();
                    final double double9 = pos.getY() + rnd.nextDouble();
                    final double double10 = pos.getZ() + 1 - rnd.nextDouble() * 0.10000000149011612;
                    world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.up()))) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    final double double8 = pos.getX() + rnd.nextDouble();
                    final double double9 = pos.getY() + 1 - rnd.nextDouble() * 0.10000000149011612;
                    final double double10 = pos.getZ() + rnd.nextDouble();
                    world.addParticle(ParticleTypes.J, double8, double9, double10, 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FireBlock.AGE, FireBlock.NORTH, FireBlock.EAST, FireBlock.SOUTH, FireBlock.WEST, FireBlock.UP);
    }
    
    public void registerFlammableBlock(final Block block, final int burnChance, final int spreadChance) {
        this.burnChances.put(block, burnChance);
        this.spreadChances.put(block, spreadChance);
    }
    
    public static void registerDefaultFlammables() {
        final FireBlock fireBlock1 = (FireBlock)Blocks.bM;
        fireBlock1.registerFlammableBlock(Blocks.n, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.o, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.p, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.q, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.r, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.s, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hC, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hD, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hE, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hF, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hG, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hH, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.dI, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.hZ, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ia, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ib, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.id, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ic, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.cH, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ie, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.if_, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ig, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ii, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ih, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.bO, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.eh, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.eg, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ei, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.gd, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.ge, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.I, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.J, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.K, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.L, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.M, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.N, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.T, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.O, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.P, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.Q, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.R, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.S, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.aa, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.ab, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.ac, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.ad, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.ae, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.af, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.U, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.V, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.W, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.X, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.Y, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.Z, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.ag, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.ah, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.ai, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.aj, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.ak, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.al, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bH, 30, 20);
        fireBlock1.registerFlammableBlock(Blocks.bG, 15, 100);
        fireBlock1.registerFlammableBlock(Blocks.aQ, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.aR, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.aS, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gM, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gN, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gO, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gP, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gQ, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.gR, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bo, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bp, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bq, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.br, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bs, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bt, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bu, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bv, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bw, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bx, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.by, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bA, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.bz, 60, 100);
        fireBlock1.registerFlammableBlock(Blocks.aX, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.aY, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.aZ, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.ba, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bb, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bc, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bd, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.be, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bf, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bg, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bh, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bi, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bj, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bk, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bl, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.bm, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.dH, 15, 100);
        fireBlock1.registerFlammableBlock(Blocks.gK, 5, 5);
        fireBlock1.registerFlammableBlock(Blocks.gs, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gt, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gu, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gv, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gw, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gx, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gy, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gz, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gA, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gB, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gC, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gD, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gE, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gF, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gG, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gH, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.gI, 60, 20);
        fireBlock1.registerFlammableBlock(Blocks.jW, 30, 60);
        fireBlock1.registerFlammableBlock(Blocks.kQ, 60, 60);
        fireBlock1.registerFlammableBlock(Blocks.lI, 60, 60);
        fireBlock1.registerFlammableBlock(Blocks.lQ, 30, 20);
        fireBlock1.registerFlammableBlock(Blocks.lZ, 5, 20);
        fireBlock1.registerFlammableBlock(Blocks.lW, 60, 100);
    }
    
    static {
        AGE = Properties.AGE_15;
        NORTH = ConnectedPlantBlock.NORTH;
        EAST = ConnectedPlantBlock.EAST;
        SOUTH = ConnectedPlantBlock.SOUTH;
        WEST = ConnectedPlantBlock.WEST;
        UP = ConnectedPlantBlock.UP;
        DIRECTION_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(SystemUtil.<Direction, BooleanProperty>toMap());
    }
}
