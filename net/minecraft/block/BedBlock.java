package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BoundingBox;
import java.util.Optional;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.BedPart;
import net.minecraft.state.property.EnumProperty;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider
{
    public static final EnumProperty<BedPart> PART;
    public static final BooleanProperty OCCUPIED;
    protected static final VoxelShape TOP_SHAPE;
    protected static final VoxelShape LEG_1_SHAPE;
    protected static final VoxelShape LEG_2_SHAPE;
    protected static final VoxelShape LEG_3_SHAPE;
    protected static final VoxelShape LEG_4_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    private final DyeColor color;
    
    public BedBlock(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with(BedBlock.PART, BedPart.b)).<Comparable, Boolean>with((Property<Comparable>)BedBlock.OCCUPIED, false));
    }
    
    @Override
    public MaterialColor getMapColor(final BlockState state, final BlockView view, final BlockPos pos) {
        if (state.<BedPart>get(BedBlock.PART) == BedPart.b) {
            return this.color.getMaterialColor();
        }
        return MaterialColor.WEB;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Direction getDirection(final BlockView world, final BlockPos pos) {
        final BlockState blockState3 = world.getBlockState(pos);
        return (blockState3.getBlock() instanceof BedBlock) ? blockState3.<Direction>get((Property<Direction>)BedBlock.FACING) : null;
    }
    
    @Override
    public boolean activate(BlockState state, final World world, BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        if (state.<BedPart>get(BedBlock.PART) != BedPart.a) {
            pos = pos.offset(state.<Direction>get((Property<Direction>)BedBlock.FACING));
            state = world.getBlockState(pos);
            if (state.getBlock() != this) {
                return true;
            }
        }
        if (!world.dimension.canPlayersSleep() || world.getBiome(pos) == Biomes.j) {
            world.clearBlockState(pos, false);
            final BlockPos blockPos7 = pos.offset(state.<Direction>get((Property<Direction>)BedBlock.FACING).getOpposite());
            if (world.getBlockState(blockPos7).getBlock() == this) {
                world.clearBlockState(blockPos7, false);
            }
            world.createExplosion(null, DamageSource.netherBed(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.c);
            return true;
        }
        if (state.<Boolean>get((Property<Boolean>)BedBlock.OCCUPIED)) {
            player.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.occupied", new Object[0]), true);
            return true;
        }
        player.trySleep(pos).ifLeft(sleepFailureReason -> {
            if (sleepFailureReason != null) {
                player.addChatMessage(sleepFailureReason.getText(), true);
            }
            return;
        });
        return true;
    }
    
    @Override
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        super.onLandedUpon(world, pos, entity, distance * 0.5f);
    }
    
    @Override
    public void onEntityLand(final BlockView world, final Entity entity) {
        if (entity.isSneaking()) {
            super.onEntityLand(world, entity);
        }
        else {
            final Vec3d vec3d3 = entity.getVelocity();
            if (vec3d3.y < 0.0) {
                final double double4 = (entity instanceof LivingEntity) ? 1.0 : 0.8;
                entity.setVelocity(vec3d3.x, -vec3d3.y * 0.6600000262260437 * double4, vec3d3.z);
            }
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing != getDirectionTowardsOtherPart(state.<BedPart>get(BedBlock.PART), state.<Direction>get((Property<Direction>)BedBlock.FACING))) {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        if (neighborState.getBlock() == this && neighborState.<BedPart>get(BedBlock.PART) != state.<BedPart>get(BedBlock.PART)) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Comparable>with((Property<Comparable>)BedBlock.OCCUPIED, (Comparable)neighborState.<V>get((Property<V>)BedBlock.OCCUPIED));
        }
        return Blocks.AIR.getDefaultState();
    }
    
    private static Direction getDirectionTowardsOtherPart(final BedPart part, final Direction direction) {
        return (part == BedPart.b) ? direction : direction.getOpposite();
    }
    
    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        final BedPart bedPart5 = state.<BedPart>get(BedBlock.PART);
        final BlockPos blockPos6 = pos.offset(getDirectionTowardsOtherPart(bedPart5, state.<Direction>get((Property<Direction>)BedBlock.FACING)));
        final BlockState blockState7 = world.getBlockState(blockPos6);
        if (blockState7.getBlock() == this && blockState7.<BedPart>get(BedBlock.PART) != bedPart5) {
            world.setBlockState(blockPos6, Blocks.AIR.getDefaultState(), 35);
            world.playLevelEvent(player, 2001, blockPos6, Block.getRawIdFromState(blockState7));
            if (!world.isClient && !player.isCreative()) {
                final ItemStack itemStack8 = player.getMainHandStack();
                Block.dropStacks(state, world, pos, null, player, itemStack8);
                Block.dropStacks(blockState7, world, blockPos6, null, player, itemStack8);
            }
            player.incrementStat(Stats.a.getOrCreateStat(this));
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Direction direction2 = ctx.getPlayerHorizontalFacing();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final BlockPos blockPos4 = blockPos3.offset(direction2);
        if (ctx.getWorld().getBlockState(blockPos4).canReplace(ctx)) {
            return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)BedBlock.FACING, direction2);
        }
        return null;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)BedBlock.FACING);
        final Direction direction6 = (state.<BedPart>get(BedBlock.PART) == BedPart.a) ? direction5 : direction5.getOpposite();
        switch (direction6) {
            case NORTH: {
                return BedBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return BedBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return BedBlock.WEST_SHAPE;
            }
            default: {
                return BedBlock.EAST_SHAPE;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return true;
    }
    
    public static Optional<Vec3d> findWakeUpPosition(final EntityType<?> entityType, final ViewableWorld viewableWorld, final BlockPos blockPos, int integer) {
        final Direction direction5 = viewableWorld.getBlockState(blockPos).<Direction>get((Property<Direction>)BedBlock.FACING);
        final int integer2 = blockPos.getX();
        final int integer3 = blockPos.getY();
        final int integer4 = blockPos.getZ();
        for (int integer5 = 0; integer5 <= 1; ++integer5) {
            final int integer6 = integer2 - direction5.getOffsetX() * integer5 - 1;
            final int integer7 = integer4 - direction5.getOffsetZ() * integer5 - 1;
            final int integer8 = integer6 + 2;
            final int integer9 = integer7 + 2;
            for (int integer10 = integer6; integer10 <= integer8; ++integer10) {
                for (int integer11 = integer7; integer11 <= integer9; ++integer11) {
                    final BlockPos blockPos2 = new BlockPos(integer10, integer3, integer11);
                    final Optional<Vec3d> optional17 = canWakeUpAt(entityType, viewableWorld, blockPos2);
                    if (optional17.isPresent()) {
                        if (integer <= 0) {
                            return optional17;
                        }
                        --integer;
                    }
                }
            }
        }
        return Optional.<Vec3d>empty();
    }
    
    protected static Optional<Vec3d> canWakeUpAt(final EntityType<?> entityType, final ViewableWorld viewableWorld, final BlockPos blockPos) {
        final VoxelShape voxelShape4 = viewableWorld.getBlockState(blockPos).getCollisionShape(viewableWorld, blockPos);
        if (voxelShape4.getMaximum(Direction.Axis.Y) > 0.4375) {
            return Optional.<Vec3d>empty();
        }
        final BlockPos.Mutable mutable5 = new BlockPos.Mutable(blockPos);
        while (mutable5.getY() >= 0 && blockPos.getY() - mutable5.getY() <= 2 && viewableWorld.getBlockState(mutable5).getCollisionShape(viewableWorld, mutable5).isEmpty()) {
            mutable5.setOffset(Direction.DOWN);
        }
        final VoxelShape voxelShape5 = viewableWorld.getBlockState(mutable5).getCollisionShape(viewableWorld, mutable5);
        if (voxelShape5.isEmpty()) {
            return Optional.<Vec3d>empty();
        }
        final double double7 = mutable5.getY() + voxelShape5.getMaximum(Direction.Axis.Y) + 2.0E-7;
        if (blockPos.getY() - double7 > 2.0) {
            return Optional.<Vec3d>empty();
        }
        final float float9 = entityType.getWidth() / 2.0f;
        final Vec3d vec3d10 = new Vec3d(mutable5.getX() + 0.5, double7, mutable5.getZ() + 0.5);
        if (viewableWorld.doesNotCollide(new BoundingBox(vec3d10.x - float9, vec3d10.y, vec3d10.z - float9, vec3d10.x + float9, vec3d10.y + entityType.getHeight(), vec3d10.z + float9))) {
            return Optional.<Vec3d>of(vec3d10);
        }
        return Optional.<Vec3d>empty();
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.b;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BedBlock.FACING, BedBlock.PART, BedBlock.OCCUPIED);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new BedBlockEntity(this.color);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            final BlockPos blockPos6 = pos.offset(state.<Direction>get((Property<Direction>)BedBlock.FACING));
            world.setBlockState(blockPos6, ((AbstractPropertyContainer<O, BlockState>)state).<BedPart, BedPart>with(BedBlock.PART, BedPart.a), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighborStates(world, pos, 3);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public DyeColor getColor() {
        return this.color;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public long getRenderingSeed(final BlockState state, final BlockPos pos) {
        final BlockPos blockPos3 = pos.offset(state.<Direction>get((Property<Direction>)BedBlock.FACING), (state.<BedPart>get(BedBlock.PART) != BedPart.a) ? 1 : 0);
        return MathHelper.hashCode(blockPos3.getX(), pos.getY(), blockPos3.getZ());
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        PART = Properties.BED_PART;
        OCCUPIED = Properties.OCCUPIED;
        TOP_SHAPE = Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
        LEG_1_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        LEG_2_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
        LEG_3_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
        LEG_4_SHAPE = Block.createCuboidShape(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
        NORTH_SHAPE = VoxelShapes.union(BedBlock.TOP_SHAPE, BedBlock.LEG_1_SHAPE, BedBlock.LEG_3_SHAPE);
        SOUTH_SHAPE = VoxelShapes.union(BedBlock.TOP_SHAPE, BedBlock.LEG_2_SHAPE, BedBlock.LEG_4_SHAPE);
        WEST_SHAPE = VoxelShapes.union(BedBlock.TOP_SHAPE, BedBlock.LEG_1_SHAPE, BedBlock.LEG_2_SHAPE);
        EAST_SHAPE = VoxelShapes.union(BedBlock.TOP_SHAPE, BedBlock.LEG_3_SHAPE, BedBlock.LEG_4_SHAPE);
    }
}
