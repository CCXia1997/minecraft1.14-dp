package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.state.StateFactory;
import net.minecraft.item.ItemPlacementContext;
import java.util.Iterator;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.state.property.DirectionProperty;

public class CarvedPumpkinBlock extends HorizontalFacingBlock
{
    public static final DirectionProperty FACING;
    @Nullable
    private BlockPattern b;
    @Nullable
    private BlockPattern c;
    @Nullable
    private BlockPattern d;
    @Nullable
    private BlockPattern e;
    private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE;
    
    protected CarvedPumpkinBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)CarvedPumpkinBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        this.a(world, pos);
    }
    
    public boolean a(final ViewableWorld viewableWorld, final BlockPos blockPos) {
        return this.d().searchAround(viewableWorld, blockPos) != null || this.f().searchAround(viewableWorld, blockPos) != null;
    }
    
    private void a(final World world, final BlockPos blockPos) {
        BlockPattern.Result result3 = this.e().searchAround(world, blockPos);
        if (result3 != null) {
            for (int integer4 = 0; integer4 < this.e().getHeight(); ++integer4) {
                final CachedBlockPosition cachedBlockPosition5 = result3.translate(0, integer4, 0);
                world.setBlockState(cachedBlockPosition5.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                world.playLevelEvent(2001, cachedBlockPosition5.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition5.getBlockState()));
            }
            final SnowmanEntity snowmanEntity4 = EntityType.SNOW_GOLEM.create(world);
            final BlockPos blockPos2 = result3.translate(0, 2, 0).getBlockPos();
            snowmanEntity4.setPositionAndAngles(blockPos2.getX() + 0.5, blockPos2.getY() + 0.05, blockPos2.getZ() + 0.5, 0.0f, 0.0f);
            world.spawnEntity(snowmanEntity4);
            for (final ServerPlayerEntity serverPlayerEntity7 : world.<ServerPlayerEntity>getEntities(ServerPlayerEntity.class, snowmanEntity4.getBoundingBox().expand(5.0))) {
                Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity7, snowmanEntity4);
            }
            for (int integer5 = 0; integer5 < this.e().getHeight(); ++integer5) {
                final CachedBlockPosition cachedBlockPosition6 = result3.translate(0, integer5, 0);
                world.updateNeighbors(cachedBlockPosition6.getBlockPos(), Blocks.AIR);
            }
        }
        else {
            result3 = this.q().searchAround(world, blockPos);
            if (result3 != null) {
                for (int integer4 = 0; integer4 < this.q().getWidth(); ++integer4) {
                    for (int integer6 = 0; integer6 < this.q().getHeight(); ++integer6) {
                        final CachedBlockPosition cachedBlockPosition7 = result3.translate(integer4, integer6, 0);
                        world.setBlockState(cachedBlockPosition7.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                        world.playLevelEvent(2001, cachedBlockPosition7.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition7.getBlockState()));
                    }
                }
                final BlockPos blockPos3 = result3.translate(1, 2, 0).getBlockPos();
                final IronGolemEntity ironGolemEntity5 = EntityType.IRON_GOLEM.create(world);
                ironGolemEntity5.setPlayerCreated(true);
                ironGolemEntity5.setPositionAndAngles(blockPos3.getX() + 0.5, blockPos3.getY() + 0.05, blockPos3.getZ() + 0.5, 0.0f, 0.0f);
                world.spawnEntity(ironGolemEntity5);
                for (final ServerPlayerEntity serverPlayerEntity7 : world.<ServerPlayerEntity>getEntities(ServerPlayerEntity.class, ironGolemEntity5.getBoundingBox().expand(5.0))) {
                    Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity7, ironGolemEntity5);
                }
                for (int integer5 = 0; integer5 < this.q().getWidth(); ++integer5) {
                    for (int integer7 = 0; integer7 < this.q().getHeight(); ++integer7) {
                        final CachedBlockPosition cachedBlockPosition8 = result3.translate(integer5, integer7, 0);
                        world.updateNeighbors(cachedBlockPosition8.getBlockPos(), Blocks.AIR);
                    }
                }
            }
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)CarvedPumpkinBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CarvedPumpkinBlock.FACING);
    }
    
    private BlockPattern d() {
        if (this.b == null) {
            this.b = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.cC))).build();
        }
        return this.b;
    }
    
    private BlockPattern e() {
        if (this.c == null) {
            this.c = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockPosition.matchesBlockState(CarvedPumpkinBlock.IS_PUMPKIN_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.cC))).build();
        }
        return this.c;
    }
    
    private BlockPattern f() {
        if (this.d == null) {
            this.d = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.bE))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.d;
    }
    
    private BlockPattern q() {
        if (this.e == null) {
            this.e = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockPosition.matchesBlockState(CarvedPumpkinBlock.IS_PUMPKIN_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.bE))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.e;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        IS_PUMPKIN_PREDICATE = (blockState -> blockState != null && (blockState.getBlock() == Blocks.cN || blockState.getBlock() == Blocks.cO));
    }
}
