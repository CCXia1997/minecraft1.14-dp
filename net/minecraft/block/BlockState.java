package net.minecraft.block;

import java.util.Arrays;
import net.minecraft.world.EmptyBlockView;
import java.util.Iterator;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import java.util.List;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.AbstractPropertyContainer;

public class BlockState extends AbstractPropertyContainer<Block, BlockState> implements PropertyContainer<BlockState>
{
    @Nullable
    private ShapeCache shapeCache;
    private final int luminance;
    private final boolean e;
    
    public BlockState(final Block block, final ImmutableMap<Property<?>, Comparable<?>> propertyMap) {
        super(block, propertyMap);
        this.luminance = block.getLuminance(this);
        this.e = block.n(this);
    }
    
    public void initShapeCache() {
        if (!this.getBlock().hasDynamicBounds()) {
            this.shapeCache = new ShapeCache(this);
        }
    }
    
    public Block getBlock() {
        return (Block)this.owner;
    }
    
    public Material getMaterial() {
        return this.getBlock().getMaterial(this);
    }
    
    public boolean allowsSpawning(final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return this.getBlock().allowsSpawning(this, blockView, blockPos, entityType);
    }
    
    public boolean isTranslucent(final BlockView view, final BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.d;
        }
        return this.getBlock().isTranslucent(this, view, pos);
    }
    
    public int getLightSubtracted(final BlockView view, final BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.e;
        }
        return this.getBlock().getLightSubtracted(this, view, pos);
    }
    
    @Environment(EnvType.CLIENT)
    public VoxelShape getCullShape(final BlockView view, final BlockPos pos, final Direction facing) {
        if (this.shapeCache != null && ShapeCache.c(this.shapeCache) != null) {
            return ShapeCache.c(this.shapeCache)[facing.ordinal()];
        }
        return VoxelShapes.a(this.j(view, pos), facing);
    }
    
    public boolean f() {
        return this.shapeCache == null || this.shapeCache.g;
    }
    
    public boolean g() {
        return this.e;
    }
    
    public int getLuminance() {
        return this.luminance;
    }
    
    public boolean isAir() {
        return this.getBlock().isAir(this);
    }
    
    public MaterialColor getTopMaterialColor(final BlockView view, final BlockPos pos) {
        return this.getBlock().getMapColor(this, view, pos);
    }
    
    public BlockState rotate(final BlockRotation rotation) {
        return this.getBlock().rotate(this, rotation);
    }
    
    public BlockState mirror(final BlockMirror mirror) {
        return this.getBlock().mirror(this, mirror);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender() {
        return this.getBlock().hasBlockEntityBreakingRender(this);
    }
    
    public BlockRenderType getRenderType() {
        return this.getBlock().getRenderType(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getBlockBrightness(final ExtendedBlockView view, final BlockPos pos) {
        return this.getBlock().getBlockBrightness(this, view, pos);
    }
    
    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(final BlockView view, final BlockPos pos) {
        return this.getBlock().getAmbientOcclusionLightLevel(this, view, pos);
    }
    
    public boolean isSimpleFullBlock(final BlockView view, final BlockPos pos) {
        return this.getBlock().isSimpleFullBlock(this, view, pos);
    }
    
    public boolean emitsRedstonePower() {
        return this.getBlock().emitsRedstonePower(this);
    }
    
    public int getWeakRedstonePower(final BlockView view, final BlockPos pos, final Direction facing) {
        return this.getBlock().getWeakRedstonePower(this, view, pos, facing);
    }
    
    public boolean hasComparatorOutput() {
        return this.getBlock().hasComparatorOutput(this);
    }
    
    public int getComparatorOutput(final World world, final BlockPos pos) {
        return this.getBlock().getComparatorOutput(this, world, pos);
    }
    
    public float getHardness(final BlockView view, final BlockPos pos) {
        return this.getBlock().getHardness(this, view, pos);
    }
    
    public float calcBlockBreakingDelta(final PlayerEntity player, final BlockView view, final BlockPos pos) {
        return this.getBlock().calcBlockBreakingDelta(this, player, view, pos);
    }
    
    public int getStrongRedstonePower(final BlockView view, final BlockPos pos, final Direction facing) {
        return this.getBlock().getStrongRedstonePower(this, view, pos, facing);
    }
    
    public PistonBehavior getPistonBehavior() {
        return this.getBlock().getPistonBehavior(this);
    }
    
    public boolean isFullOpaque(final BlockView view, final BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.fullOpaque;
        }
        return this.getBlock().isFullOpaque(this, view, pos);
    }
    
    public boolean isFullBoundsCubeForCulling() {
        if (this.shapeCache != null) {
            return this.shapeCache.cull;
        }
        return this.getBlock().isFullBoundsCubeForCulling(this);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean skipRenderingSide(final BlockState neighbor, final Direction facing) {
        return this.getBlock().skipRenderingSide(this, neighbor, facing);
    }
    
    public VoxelShape getOutlineShape(final BlockView blockView, final BlockPos blockPos) {
        return this.getOutlineShape(blockView, blockPos, VerticalEntityPosition.minValue());
    }
    
    public VoxelShape getOutlineShape(final BlockView view, final BlockPos blockPos, final VerticalEntityPosition verticalEntityPosition) {
        return this.getBlock().getOutlineShape(this, view, blockPos, verticalEntityPosition);
    }
    
    public VoxelShape getCollisionShape(final BlockView view, final BlockPos pos) {
        return this.getCollisionShape(view, pos, VerticalEntityPosition.minValue());
    }
    
    public VoxelShape getCollisionShape(final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return this.getBlock().getCollisionShape(this, view, pos, ePos);
    }
    
    public VoxelShape j(final BlockView view, final BlockPos pos) {
        return this.getBlock().h(this, view, pos);
    }
    
    public VoxelShape getRayTraceShape(final BlockView view, final BlockPos pos) {
        return this.getBlock().getRayTraceShape(this, view, pos);
    }
    
    public final boolean hasSolidTopSurface(final BlockView blockView, final BlockPos blockPos, final Entity entity) {
        return Block.isFaceFullSquare(this.getCollisionShape(blockView, blockPos, VerticalEntityPosition.fromEntity(entity)), Direction.UP);
    }
    
    public Vec3d getOffsetPos(final BlockView view, final BlockPos pos) {
        return this.getBlock().getOffsetPos(this, view, pos);
    }
    
    public boolean onBlockAction(final World world, final BlockPos pos, final int type, final int data) {
        return this.getBlock().onBlockAction(this, world, pos, type, data);
    }
    
    public void neighborUpdate(final World world, final BlockPos pos, final Block neighborBlock, final BlockPos neighborPos, final boolean boolean5) {
        this.getBlock().neighborUpdate(this, world, pos, neighborBlock, neighborPos, boolean5);
    }
    
    public void updateNeighborStates(final IWorld world, final BlockPos pos, final int flags) {
        this.getBlock().updateNeighborStates(this, world, pos, flags);
    }
    
    public void b(final IWorld world, final BlockPos pos, final int flags) {
        this.getBlock().b(this, world, pos, flags);
    }
    
    public void onBlockAdded(final World world, final BlockPos pos, final BlockState oldState, final boolean boolean4) {
        this.getBlock().onBlockAdded(this, world, pos, oldState, boolean4);
    }
    
    public void onBlockRemoved(final World world, final BlockPos pos, final BlockState newState, final boolean boolean4) {
        this.getBlock().onBlockRemoved(this, world, pos, newState, boolean4);
    }
    
    public void scheduledTick(final World world, final BlockPos pos, final Random rnd) {
        this.getBlock().onScheduledTick(this, world, pos, rnd);
    }
    
    public void onRandomTick(final World world, final BlockPos pos, final Random rnd) {
        this.getBlock().onRandomTick(this, world, pos, rnd);
    }
    
    public void onEntityCollision(final World world, final BlockPos pos, final Entity entity) {
        this.getBlock().onEntityCollision(this, world, pos, entity);
    }
    
    public void onStacksDropped(final World world, final BlockPos pos, final ItemStack stack) {
        this.getBlock().onStacksDropped(this, world, pos, stack);
    }
    
    public List<ItemStack> getDroppedStacks(final LootContext.Builder builder) {
        return this.getBlock().getDroppedStacks(this, builder);
    }
    
    public boolean activate(final World world, final PlayerEntity playerEntity, final Hand hand, final BlockHitResult blockHitResult) {
        return this.getBlock().activate(this, world, blockHitResult.getBlockPos(), playerEntity, hand, blockHitResult);
    }
    
    public void onBlockBreakStart(final World world, final BlockPos pos, final PlayerEntity player) {
        this.getBlock().onBlockBreakStart(this, world, pos, player);
    }
    
    public boolean canSuffocate(final BlockView view, final BlockPos pos) {
        return this.getBlock().canSuffocate(this, view, pos);
    }
    
    public BlockState getStateForNeighborUpdate(final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        return this.getBlock().getStateForNeighborUpdate(this, facing, neighborState, world, pos, neighborPos);
    }
    
    public boolean canPlaceAtSide(final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return this.getBlock().canPlaceAtSide(this, view, pos, env);
    }
    
    public boolean canReplace(final ItemPlacementContext ctx) {
        return this.getBlock().canReplace(this, ctx);
    }
    
    public boolean canPlaceAt(final ViewableWorld world, final BlockPos pos) {
        return this.getBlock().canPlaceAt(this, world, pos);
    }
    
    public boolean shouldPostProcess(final BlockView view, final BlockPos pos) {
        return this.getBlock().shouldPostProcess(this, view, pos);
    }
    
    @Nullable
    public NameableContainerProvider createContainerProvider(final World world, final BlockPos pos) {
        return this.getBlock().createContainerProvider(this, world, pos);
    }
    
    public boolean matches(final Tag<Block> tag) {
        return this.getBlock().matches(tag);
    }
    
    public FluidState getFluidState() {
        return this.getBlock().getFluidState(this);
    }
    
    public boolean hasRandomTicks() {
        return this.getBlock().hasRandomTicks(this);
    }
    
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(final BlockPos pos) {
        return this.getBlock().getRenderingSeed(this, pos);
    }
    
    public BlockSoundGroup getSoundGroup() {
        return this.getBlock().getSoundGroup(this);
    }
    
    public void onProjectileHit(final World world, final BlockState state, final BlockHitResult hitResult, final Entity projectile) {
        this.getBlock().onProjectileHit(world, state, hitResult, projectile);
    }
    
    public static <T> Dynamic<T> serialize(final DynamicOps<T> ops, final BlockState state) {
        final ImmutableMap<Property<?>, Comparable<?>> immutableMap3 = state.getEntries();
        T object4;
        if (immutableMap3.isEmpty()) {
            object4 = (T)ops.createMap((Map)ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString())));
        }
        else {
            object4 = (T)ops.createMap((Map)ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()), ops.createString("Properties"), ops.createMap((Map)immutableMap3.entrySet().stream().map(entry -> Pair.of(ops.createString(entry.getKey().getName()), ops.createString(PropertyContainer.<Comparable>getValueAsString((Property<Comparable>)entry.getKey(), entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, object4);
    }
    
    public static <T> BlockState deserialize(final Dynamic<T> dynamic) {
        final Block block2 = Registry.BLOCK.get(new Identifier(dynamic.getElement("Name").<String>flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
        final Map<String, String> map3 = (Map<String, String>)dynamic.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        BlockState blockState4 = block2.getDefaultState();
        final StateFactory<Block, BlockState> stateFactory5 = block2.getStateFactory();
        for (final Map.Entry<String, String> entry7 : map3.entrySet()) {
            final String string8 = entry7.getKey();
            final Property<?> property9 = stateFactory5.getProperty(string8);
            if (property9 != null) {
                blockState4 = PropertyContainer.deserialize(blockState4, property9, string8, dynamic.toString(), entry7.getValue());
            }
        }
        return blockState4;
    }
    
    static final class ShapeCache
    {
        private static final Direction[] DIRECTIONS;
        private final boolean cull;
        private final boolean fullOpaque;
        private final boolean d;
        private final int e;
        private final VoxelShape[] shapes;
        private final boolean g;
        
        private ShapeCache(final BlockState state) {
            final Block block2 = state.getBlock();
            this.cull = block2.isFullBoundsCubeForCulling(state);
            this.fullOpaque = block2.isFullOpaque(state, EmptyBlockView.a, BlockPos.ORIGIN);
            this.d = block2.isTranslucent(state, EmptyBlockView.a, BlockPos.ORIGIN);
            this.e = block2.getLightSubtracted(state, EmptyBlockView.a, BlockPos.ORIGIN);
            if (!state.isFullBoundsCubeForCulling()) {
                this.shapes = null;
            }
            else {
                this.shapes = new VoxelShape[ShapeCache.DIRECTIONS.length];
                final VoxelShape voxelShape3 = block2.h(state, EmptyBlockView.a, BlockPos.ORIGIN);
                for (final Direction direction7 : ShapeCache.DIRECTIONS) {
                    this.shapes[direction7.ordinal()] = VoxelShapes.a(voxelShape3, direction7);
                }
            }
            final VoxelShape voxelShape3 = block2.getCollisionShape(state, EmptyBlockView.a, BlockPos.ORIGIN, VerticalEntityPosition.minValue());
            final VoxelShape voxelShape4;
            this.g = Arrays.<Direction.Axis>stream(Direction.Axis.values()).anyMatch(axis -> voxelShape4.getMinimum(axis) < 0.0 || voxelShape4.getMaximum(axis) > 1.0);
        }
        
        @Environment(EnvType.CLIENT)
        static /* synthetic */ VoxelShape[] c(final ShapeCache shapeCache) {
            return shapeCache.shapes;
        }
        
        static {
            DIRECTIONS = Direction.values();
        }
    }
}
