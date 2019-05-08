package net.minecraft.block;

import net.minecraft.util.DyeColor;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.ItemGroup;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.context.LootContextParameters;
import java.util.Collections;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.client.network.DebugRendererInfoManager;
import java.util.Random;
import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.entity.EntityType;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.BlockItem;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.item.Item;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.state.StateFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import net.minecraft.util.IdList;
import org.apache.logging.log4j.Logger;
import net.minecraft.item.ItemProvider;

public class Block implements ItemProvider
{
    protected static final Logger LOGGER;
    public static final IdList<BlockState> STATE_IDS;
    private static final Direction[] FACINGS;
    private static final VoxelShape b;
    private static final VoxelShape c;
    protected final int lightLevel;
    protected final float hardness;
    protected final float resistance;
    protected final boolean randomTicks;
    protected final BlockSoundGroup soundGroup;
    protected final Material material;
    protected final MaterialColor materialColor;
    private final float friction;
    protected final StateFactory<Block, BlockState> stateFactory;
    private BlockState defaultState;
    protected final boolean collidable;
    private final boolean dynamicBounds;
    @Nullable
    private Identifier dropTableId;
    @Nullable
    private String translationKey;
    @Nullable
    private Item cachedItem;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<NeighborGroup>> FACE_CULL_MAP;
    
    public static int getRawIdFromState(@Nullable final BlockState state) {
        if (state == null) {
            return 0;
        }
        final int integer2 = Block.STATE_IDS.getId(state);
        return (integer2 == -1) ? 0 : integer2;
    }
    
    public static BlockState getStateFromRawId(final int stateId) {
        final BlockState blockState2 = Block.STATE_IDS.get(stateId);
        return (blockState2 == null) ? Blocks.AIR.getDefaultState() : blockState2;
    }
    
    public static Block getBlockFromItem(@Nullable final Item item) {
        if (item instanceof BlockItem) {
            return ((BlockItem)item).getBlock();
        }
        return Blocks.AIR;
    }
    
    public static BlockState pushEntitiesUpBeforeBlockChange(final BlockState from, final BlockState to, final World world, final BlockPos pos) {
        final VoxelShape voxelShape5 = VoxelShapes.combine(from.getCollisionShape(world, pos), to.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND).offset(pos.getX(), pos.getY(), pos.getZ());
        final List<Entity> list6 = world.getEntities((Entity)null, voxelShape5.getBoundingBox());
        for (final Entity entity8 : list6) {
            final double double9 = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity8.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.<VoxelShape>of(voxelShape5), -1.0);
            entity8.requestTeleport(entity8.x, entity8.y + 1.0 + double9, entity8.z);
        }
        return to;
    }
    
    public static VoxelShape createCuboidShape(final double xMin, final double yMin, final double zMin, final double xMax, final double yMax, final double zMax) {
        return VoxelShapes.cuboid(xMin / 16.0, yMin / 16.0, zMin / 16.0, xMax / 16.0, yMax / 16.0, zMax / 16.0);
    }
    
    @Deprecated
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return isSolidFullSquare(state, blockView, blockPos, Direction.UP);
    }
    
    @Deprecated
    public boolean isAir(final BlockState state) {
        return false;
    }
    
    @Deprecated
    public int getLuminance(final BlockState state) {
        return this.lightLevel;
    }
    
    @Deprecated
    public Material getMaterial(final BlockState state) {
        return this.material;
    }
    
    @Deprecated
    public MaterialColor getMapColor(final BlockState state, final BlockView view, final BlockPos pos) {
        return this.materialColor;
    }
    
    @Deprecated
    public void updateNeighborStates(final BlockState state, final IWorld world, final BlockPos pos, final int flags) {
        try (final BlockPos.PooledMutable pooledMutable5 = BlockPos.PooledMutable.get()) {
            for (final Direction direction10 : Block.FACINGS) {
                pooledMutable5.set((Vec3i)pos).setOffset(direction10);
                final BlockState blockState11 = world.getBlockState(pooledMutable5);
                final BlockState blockState12 = blockState11.getStateForNeighborUpdate(direction10.getOpposite(), state, world, pooledMutable5, pos);
                replaceBlock(blockState11, blockState12, world, pooledMutable5, flags);
            }
        }
    }
    
    public boolean matches(final Tag<Block> tag) {
        return tag.contains(this);
    }
    
    public static BlockState getRenderingState(final BlockState state, final IWorld world, final BlockPos pos) {
        BlockState blockState4 = state;
        final BlockPos.Mutable mutable5 = new BlockPos.Mutable();
        for (final Direction direction9 : Block.FACINGS) {
            mutable5.set(pos).setOffset(direction9);
            blockState4 = blockState4.getStateForNeighborUpdate(direction9, world.getBlockState(mutable5), world, pos, mutable5);
        }
        return blockState4;
    }
    
    public static void replaceBlock(final BlockState state, final BlockState newState, final IWorld world, final BlockPos pos, final int flags) {
        if (newState != state) {
            if (newState.isAir()) {
                if (!world.isClient()) {
                    world.breakBlock(pos, (flags & 0x20) == 0x0);
                }
            }
            else {
                world.setBlockState(pos, newState, flags & 0xFFFFFFDF);
            }
        }
    }
    
    @Deprecated
    public void b(final BlockState state, final IWorld world, final BlockPos pos, final int flags) {
    }
    
    @Deprecated
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        return state;
    }
    
    @Deprecated
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return state;
    }
    
    @Deprecated
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state;
    }
    
    public Block(final Settings settings) {
        final StateFactory.Builder<Block, BlockState> builder2 = new StateFactory.Builder<Block, BlockState>(this);
        this.appendProperties(builder2);
        this.material = settings.material;
        this.materialColor = settings.materialColor;
        this.collidable = settings.collidable;
        this.soundGroup = settings.soundGroup;
        this.lightLevel = settings.luminance;
        this.resistance = settings.resistance;
        this.hardness = settings.hardness;
        this.randomTicks = settings.randomTicks;
        this.friction = settings.friction;
        this.dynamicBounds = settings.dynamicBounds;
        this.dropTableId = settings.dropTableId;
        this.stateFactory = builder2.<BlockState>build(BlockState::new);
        this.setDefaultState(this.stateFactory.getDefaultState());
    }
    
    public static boolean canConnect(final Block block) {
        return block instanceof LeavesBlock || block == Blocks.gg || block == Blocks.cN || block == Blocks.cO || block == Blocks.dC || block == Blocks.cI;
    }
    
    @Deprecated
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return state.getMaterial().blocksLight() && isShapeFullCube(state.getCollisionShape(view, pos)) && !state.emitsRedstonePower();
    }
    
    @Deprecated
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return this.material.blocksMovement() && isShapeFullCube(state.getCollisionShape(view, pos));
    }
    
    @Deprecated
    @Environment(EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return false;
    }
    
    @Deprecated
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return !isShapeFullCube(world.getCollisionShape(view, pos));
            }
            case b: {
                return view.getFluidState(pos).matches(FluidTags.a);
            }
            case c: {
                return !isShapeFullCube(world.getCollisionShape(view, pos));
            }
            default: {
                return false;
            }
        }
    }
    
    @Deprecated
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Deprecated
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        return this.material.isReplaceable() && (ctx.getItemStack().isEmpty() || ctx.getItemStack().getItem() != this.getItem());
    }
    
    @Deprecated
    public float getHardness(final BlockState state, final BlockView world, final BlockPos pos) {
        return this.hardness;
    }
    
    public boolean hasRandomTicks(final BlockState state) {
        return this.randomTicks;
    }
    
    public boolean hasBlockEntity() {
        return this instanceof BlockEntityProvider;
    }
    
    @Deprecated
    public boolean shouldPostProcess(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Deprecated
    @Environment(EnvType.CLIENT)
    public int getBlockBrightness(final BlockState state, final ExtendedBlockView view, final BlockPos pos) {
        return view.getLightmapIndex(pos, state.getLuminance());
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean shouldDrawSide(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        final BlockPos blockPos5 = pos.offset(facing);
        final BlockState blockState6 = view.getBlockState(blockPos5);
        if (state.skipRenderingSide(blockState6, facing)) {
            return false;
        }
        if (!blockState6.isFullBoundsCubeForCulling()) {
            return true;
        }
        final NeighborGroup neighborGroup7 = new NeighborGroup(state, blockState6, facing);
        final Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap8 = Block.FACE_CULL_MAP.get();
        final byte byte9 = object2ByteLinkedOpenHashMap8.getAndMoveToFirst(neighborGroup7);
        if (byte9 != 127) {
            return byte9 != 0;
        }
        final VoxelShape voxelShape10 = state.getCullShape(view, pos, facing);
        final VoxelShape voxelShape11 = blockState6.getCullShape(view, blockPos5, facing.getOpposite());
        final boolean boolean12 = VoxelShapes.matchesAnywhere(voxelShape10, voxelShape11, BooleanBiFunction.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap8.size() == 200) {
            object2ByteLinkedOpenHashMap8.removeLastByte();
        }
        object2ByteLinkedOpenHashMap8.putAndMoveToFirst(neighborGroup7, (byte)(byte)(boolean12 ? 1 : 0));
        return boolean12;
    }
    
    @Deprecated
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return this.collidable && this.getRenderLayer() == BlockRenderLayer.SOLID;
    }
    
    @Deprecated
    @Environment(EnvType.CLIENT)
    public boolean skipRenderingSide(final BlockState state, final BlockState neighbor, final Direction facing) {
        return false;
    }
    
    @Deprecated
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.fullCube();
    }
    
    @Deprecated
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return this.collidable ? state.getOutlineShape(view, pos) : VoxelShapes.empty();
    }
    
    @Deprecated
    public VoxelShape h(final BlockState state, final BlockView view, final BlockPos pos) {
        return state.getOutlineShape(view, pos);
    }
    
    @Deprecated
    public VoxelShape getRayTraceShape(final BlockState state, final BlockView view, final BlockPos pos) {
        return VoxelShapes.empty();
    }
    
    public static boolean isSolidMediumSquare(final BlockView world, final BlockPos pos) {
        final BlockState blockState3 = world.getBlockState(pos);
        return !blockState3.matches(BlockTags.C) && !VoxelShapes.matchesAnywhere(blockState3.getCollisionShape(world, pos).getFace(Direction.UP), Block.b, BooleanBiFunction.ONLY_SECOND);
    }
    
    public static boolean isSolidSmallSquare(final ViewableWorld world, final BlockPos pos, final Direction side) {
        final BlockState blockState4 = world.getBlockState(pos);
        return !blockState4.matches(BlockTags.C) && !VoxelShapes.matchesAnywhere(blockState4.getCollisionShape(world, pos).getFace(side), Block.c, BooleanBiFunction.ONLY_SECOND);
    }
    
    public static boolean isSolidFullSquare(final BlockState state, final BlockView world, final BlockPos pos, final Direction side) {
        return !state.matches(BlockTags.C) && isFaceFullSquare(state.getCollisionShape(world, pos), side);
    }
    
    public static boolean isFaceFullSquare(final VoxelShape shape, final Direction side) {
        final VoxelShape voxelShape3 = shape.getFace(side);
        return isShapeFullCube(voxelShape3);
    }
    
    public static boolean isShapeFullCube(final VoxelShape shape) {
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), shape, BooleanBiFunction.NOT_SAME);
    }
    
    @Deprecated
    public final boolean isFullOpaque(final BlockState state, final BlockView view, final BlockPos pos) {
        return state.isFullBoundsCubeForCulling() && isShapeFullCube(state.j(view, pos));
    }
    
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return !isShapeFullCube(state.getOutlineShape(view, pos)) && state.getFluidState().isEmpty();
    }
    
    @Deprecated
    public int getLightSubtracted(final BlockState state, final BlockView view, final BlockPos pos) {
        if (state.isFullOpaque(view, pos)) {
            return view.getMaxLightLevel();
        }
        return state.isTranslucent(view, pos) ? 0 : 1;
    }
    
    @Deprecated
    public boolean n(final BlockState state) {
        return false;
    }
    
    @Deprecated
    public void onRandomTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        this.onScheduledTick(state, world, pos, random);
    }
    
    @Deprecated
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
    }
    
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
    }
    
    public void onBroken(final IWorld world, final BlockPos pos, final BlockState state) {
    }
    
    @Deprecated
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        DebugRendererInfoManager.sendBlockUpdate(world, pos);
    }
    
    public int getTickRate(final ViewableWorld world) {
        return 10;
    }
    
    @Nullable
    @Deprecated
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return null;
    }
    
    @Deprecated
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
    }
    
    @Deprecated
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (this.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            world.removeBlockEntity(pos);
        }
    }
    
    @Deprecated
    public float calcBlockBreakingDelta(final BlockState state, final PlayerEntity player, final BlockView world, final BlockPos pos) {
        final float float5 = state.getHardness(world, pos);
        if (float5 == -1.0f) {
            return 0.0f;
        }
        final int integer6 = player.isUsingEffectiveTool(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / float5 / integer6;
    }
    
    @Deprecated
    public void onStacksDropped(final BlockState state, final World world, final BlockPos pos, final ItemStack stack) {
    }
    
    public Identifier getDropTableId() {
        if (this.dropTableId == null) {
            final Identifier identifier1 = Registry.BLOCK.getId(this);
            this.dropTableId = new Identifier(identifier1.getNamespace(), "blocks/" + identifier1.getPath());
        }
        return this.dropTableId;
    }
    
    @Deprecated
    public List<ItemStack> getDroppedStacks(final BlockState state, final LootContext.Builder builder) {
        final Identifier identifier3 = this.getDropTableId();
        if (identifier3 == LootTables.EMPTY) {
            return Collections.<ItemStack>emptyList();
        }
        final LootContext lootContext4 = builder.<BlockState>put(LootContextParameters.g, state).build(LootContextTypes.BLOCK);
        final ServerWorld serverWorld5 = lootContext4.getWorld();
        final LootSupplier lootSupplier6 = serverWorld5.getServer().getLootManager().getSupplier(identifier3);
        return lootSupplier6.getDrops(lootContext4);
    }
    
    public static List<ItemStack> getDroppedStacks(final BlockState state, final ServerWorld world, final BlockPos pos, @Nullable final BlockEntity blockEntity) {
        final LootContext.Builder builder5 = new LootContext.Builder(world).setRandom(world.random).<BlockPos>put(LootContextParameters.f, pos).<ItemStack>put(LootContextParameters.i, ItemStack.EMPTY).<BlockEntity>putNullable(LootContextParameters.h, blockEntity);
        return state.getDroppedStacks(builder5);
    }
    
    public static List<ItemStack> getDroppedStacks(final BlockState state, final ServerWorld world, final BlockPos pos, @Nullable final BlockEntity blockEntity, final Entity entity, final ItemStack stack) {
        final LootContext.Builder builder7 = new LootContext.Builder(world).setRandom(world.random).<BlockPos>put(LootContextParameters.f, pos).<ItemStack>put(LootContextParameters.i, stack).<Entity>put(LootContextParameters.a, entity).<BlockEntity>putNullable(LootContextParameters.h, blockEntity);
        return state.getDroppedStacks(builder7);
    }
    
    public static void dropStacks(final BlockState state, final LootContext.Builder builder) {
        final ServerWorld serverWorld3 = builder.getWorld();
        final BlockPos blockPos4 = builder.<BlockPos>get(LootContextParameters.f);
        state.getDroppedStacks(builder).forEach(itemStack -> dropStack(serverWorld3, blockPos4, itemStack));
        state.onStacksDropped(serverWorld3, blockPos4, ItemStack.EMPTY);
    }
    
    public static void dropStacks(final BlockState state, final World world, final BlockPos pos) {
        if (world instanceof ServerWorld) {
            getDroppedStacks(state, (ServerWorld)world, pos, null).forEach(itemStack -> dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, ItemStack.EMPTY);
    }
    
    public static void dropStacks(final BlockState state, final World world, final BlockPos pos, @Nullable final BlockEntity blockEntity) {
        if (world instanceof ServerWorld) {
            getDroppedStacks(state, (ServerWorld)world, pos, blockEntity).forEach(itemStack -> dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, ItemStack.EMPTY);
    }
    
    public static void dropStacks(final BlockState state, final World world, final BlockPos pos, @Nullable final BlockEntity blockEntity, final Entity entity, final ItemStack stack) {
        if (world instanceof ServerWorld) {
            getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack).forEach(itemStack -> dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, stack);
    }
    
    public static void dropStack(final World world, final BlockPos pos, final ItemStack stack) {
        if (world.isClient || stack.isEmpty() || !world.getGameRules().getBoolean("doTileDrops")) {
            return;
        }
        final float float4 = 0.5f;
        final double double5 = world.random.nextFloat() * 0.5f + 0.25;
        final double double6 = world.random.nextFloat() * 0.5f + 0.25;
        final double double7 = world.random.nextFloat() * 0.5f + 0.25;
        final ItemEntity itemEntity11 = new ItemEntity(world, pos.getX() + double5, pos.getY() + double6, pos.getZ() + double7, stack);
        itemEntity11.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity11);
    }
    
    protected void dropExperience(final World world, final BlockPos pos, int size) {
        if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
            while (size > 0) {
                final int integer4 = ExperienceOrbEntity.roundToOrbSize(size);
                size -= integer4;
                world.spawnEntity(new ExperienceOrbEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, integer4));
            }
        }
    }
    
    public float getBlastResistance() {
        return this.resistance;
    }
    
    public void onDestroyedByExplosion(final World world, final BlockPos pos, final Explosion explosion) {
    }
    
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }
    
    @Deprecated
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return true;
    }
    
    @Deprecated
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        return false;
    }
    
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
    }
    
    @Nullable
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return this.getDefaultState();
    }
    
    @Deprecated
    public void onBlockBreakStart(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
    }
    
    @Deprecated
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return 0;
    }
    
    @Deprecated
    public boolean emitsRedstonePower(final BlockState state) {
        return false;
    }
    
    @Deprecated
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
    }
    
    @Deprecated
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return 0;
    }
    
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        player.incrementStat(Stats.a.getOrCreateStat(this));
        player.addExhaustion(0.005f);
        dropStacks(state, world, pos, blockEntity, player, stack);
    }
    
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
    }
    
    public boolean canMobSpawnInside() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getTextComponent() {
        return new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
    }
    
    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("block", Registry.BLOCK.getId(this));
        }
        return this.translationKey;
    }
    
    @Deprecated
    public boolean onBlockAction(final BlockState state, final World world, final BlockPos pos, final int type, final int data) {
        return false;
    }
    
    @Deprecated
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return this.material.getPistonBehavior();
    }
    
    @Deprecated
    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(final BlockState state, final BlockView view, final BlockPos pos) {
        return isShapeFullCube(state.getCollisionShape(view, pos)) ? 0.2f : 1.0f;
    }
    
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        entity.handleFallDamage(distance, 1.0f);
    }
    
    public void onEntityLand(final BlockView world, final Entity entity) {
        entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(this);
    }
    
    public void addStacksForDisplay(final ItemGroup group, final DefaultedList<ItemStack> list) {
        list.add(new ItemStack(this));
    }
    
    @Deprecated
    public FluidState getFluidState(final BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }
    
    public float getFrictionCoefficient() {
        return this.friction;
    }
    
    @Deprecated
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(final BlockState state, final BlockPos pos) {
        return MathHelper.hashCode(pos);
    }
    
    public void onProjectileHit(final World world, final BlockState state, final BlockHitResult hitResult, final Entity entity) {
    }
    
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
    }
    
    public void onRainTick(final World world, final BlockPos pos) {
    }
    
    public boolean shouldDropItemsOnExplosion(final Explosion explosion) {
        return true;
    }
    
    @Deprecated
    public boolean hasComparatorOutput(final BlockState state) {
        return false;
    }
    
    @Deprecated
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return 0;
    }
    
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
    }
    
    public StateFactory<Block, BlockState> getStateFactory() {
        return this.stateFactory;
    }
    
    protected final void setDefaultState(final BlockState state) {
        this.defaultState = state;
    }
    
    public final BlockState getDefaultState() {
        return this.defaultState;
    }
    
    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }
    
    @Deprecated
    public Vec3d getOffsetPos(final BlockState state, final BlockView view, final BlockPos blockPos) {
        final OffsetType offsetType4 = this.getOffsetType();
        if (offsetType4 == OffsetType.NONE) {
            return Vec3d.ZERO;
        }
        final long long5 = MathHelper.hashCode(blockPos.getX(), 0, blockPos.getZ());
        return new Vec3d(((long5 & 0xFL) / 15.0f - 0.5) * 0.5, (offsetType4 == OffsetType.XYZ) ? (((long5 >> 4 & 0xFL) / 15.0f - 1.0) * 0.2) : 0.0, ((long5 >> 8 & 0xFL) / 15.0f - 0.5) * 0.5);
    }
    
    public BlockSoundGroup getSoundGroup(final BlockState state) {
        return this.soundGroup;
    }
    
    @Override
    public Item getItem() {
        if (this.cachedItem == null) {
            this.cachedItem = Item.getItemFromBlock(this);
        }
        return this.cachedItem;
    }
    
    public boolean hasDynamicBounds() {
        return this.dynamicBounds;
    }
    
    @Override
    public String toString() {
        return "Block{" + Registry.BLOCK.getId(this) + "}";
    }
    
    @Environment(EnvType.CLIENT)
    public void buildTooltip(final ItemStack stack, @Nullable final BlockView view, final List<TextComponent> tooltip, final TooltipContext options) {
    }
    
    public static boolean isNaturalStone(final Block block) {
        return block == Blocks.b || block == Blocks.c || block == Blocks.e || block == Blocks.g;
    }
    
    public static boolean isNaturalDirt(final Block block) {
        return block == Blocks.j || block == Blocks.k || block == Blocks.l;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        STATE_IDS = new IdList<BlockState>();
        FACINGS = new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP };
        b = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
        c = createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
        final Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap1;
        FACE_CULL_MAP = ThreadLocal.<Object2ByteLinkedOpenHashMap<NeighborGroup>>withInitial(() -> {
            object2ByteLinkedOpenHashMap1 = new Object2ByteLinkedOpenHashMap<NeighborGroup>(200) {
                protected void rehash(final int integer) {
                }
            };
            object2ByteLinkedOpenHashMap1.defaultReturnValue((byte)127);
            return object2ByteLinkedOpenHashMap1;
        });
    }
    
    public static final class NeighborGroup
    {
        private final BlockState self;
        private final BlockState other;
        private final Direction facing;
        
        public NeighborGroup(final BlockState self, final BlockState other, final Direction facing) {
            this.self = self;
            this.other = other;
            this.facing = facing;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof NeighborGroup)) {
                return false;
            }
            final NeighborGroup neighborGroup2 = (NeighborGroup)o;
            return this.self == neighborGroup2.self && this.other == neighborGroup2.other && this.facing == neighborGroup2.facing;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.self, this.other, this.facing);
        }
    }
    
    public static class Settings
    {
        private Material material;
        private MaterialColor materialColor;
        private boolean collidable;
        private BlockSoundGroup soundGroup;
        private int luminance;
        private float resistance;
        private float hardness;
        private boolean randomTicks;
        private float friction;
        private Identifier dropTableId;
        private boolean dynamicBounds;
        
        private Settings(final Material material, final MaterialColor materialColor) {
            this.collidable = true;
            this.soundGroup = BlockSoundGroup.STONE;
            this.friction = 0.6f;
            this.material = material;
            this.materialColor = materialColor;
        }
        
        public static Settings of(final Material material) {
            return of(material, material.getColor());
        }
        
        public static Settings of(final Material material, final DyeColor color) {
            return of(material, color.getMaterialColor());
        }
        
        public static Settings of(final Material material, final MaterialColor color) {
            return new Settings(material, color);
        }
        
        public static Settings copy(final Block source) {
            final Settings settings2 = new Settings(source.material, source.materialColor);
            settings2.material = source.material;
            settings2.hardness = source.hardness;
            settings2.resistance = source.resistance;
            settings2.collidable = source.collidable;
            settings2.randomTicks = source.randomTicks;
            settings2.luminance = source.lightLevel;
            settings2.materialColor = source.materialColor;
            settings2.soundGroup = source.soundGroup;
            settings2.friction = source.getFrictionCoefficient();
            settings2.dynamicBounds = source.dynamicBounds;
            return settings2;
        }
        
        public Settings noCollision() {
            this.collidable = false;
            return this;
        }
        
        public Settings friction(final float friction) {
            this.friction = friction;
            return this;
        }
        
        protected Settings sounds(final BlockSoundGroup soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }
        
        protected Settings lightLevel(final int luminance) {
            this.luminance = luminance;
            return this;
        }
        
        public Settings strength(final float hardness, final float resistance) {
            this.hardness = hardness;
            this.resistance = Math.max(0.0f, resistance);
            return this;
        }
        
        protected Settings breakInstantly() {
            return this.strength(0.0f);
        }
        
        protected Settings strength(final float strength) {
            this.strength(strength, strength);
            return this;
        }
        
        protected Settings ticksRandomly() {
            this.randomTicks = true;
            return this;
        }
        
        protected Settings hasDynamicBounds() {
            this.dynamicBounds = true;
            return this;
        }
        
        protected Settings dropsNothing() {
            this.dropTableId = LootTables.EMPTY;
            return this;
        }
        
        public Settings dropsLike(final Block source) {
            this.dropTableId = source.getDropTableId();
            return this;
        }
    }
    
    public enum OffsetType
    {
        NONE, 
        XZ, 
        XYZ;
    }
}
