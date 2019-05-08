package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import javax.annotation.Nullable;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.util.SystemUtil;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.state.property.Properties;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.item.Items;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.item.ItemProvider;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.state.property.IntegerProperty;

public class ComposterBlock extends Block implements InventoryProvider
{
    public static final IntegerProperty LEVEL;
    public static final Object2FloatMap<ItemProvider> ITEM_TO_LEVEL_INCREASE_CHANCE;
    public static final VoxelShape RAY_TRACE_SHAPE;
    private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE;
    
    public static void registerDefaultCompostableItems() {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0f);
        final float float1 = 0.3f;
        final float float2 = 0.5f;
        final float float3 = 0.65f;
        final float float4 = 0.85f;
        final float float5 = 1.0f;
        registerCompostableItem(0.3f, Items.JUNGLE_LEAVES);
        registerCompostableItem(0.3f, Items.OAK_LEAVES);
        registerCompostableItem(0.3f, Items.SPRUCE_LEAVES);
        registerCompostableItem(0.3f, Items.DARK_OAK_LEAVES);
        registerCompostableItem(0.3f, Items.ACACIA_LEAVES);
        registerCompostableItem(0.3f, Items.BIRCH_LEAVES);
        registerCompostableItem(0.3f, Items.OAK_SAPLING);
        registerCompostableItem(0.3f, Items.SPRUCE_SAPLING);
        registerCompostableItem(0.3f, Items.BIRCH_SAPLING);
        registerCompostableItem(0.3f, Items.JUNGLE_SAPLING);
        registerCompostableItem(0.3f, Items.ACACIA_SAPLING);
        registerCompostableItem(0.3f, Items.DARK_OAK_SAPLING);
        registerCompostableItem(0.3f, Items.oP);
        registerCompostableItem(0.3f, Items.lY);
        registerCompostableItem(0.3f, Items.GRASS);
        registerCompostableItem(0.3f, Items.KELP);
        registerCompostableItem(0.3f, Items.ma);
        registerCompostableItem(0.3f, Items.lZ);
        registerCompostableItem(0.3f, Items.SEAGRASS);
        registerCompostableItem(0.3f, Items.pR);
        registerCompostableItem(0.3f, Items.jO);
        registerCompostableItem(0.5f, Items.DRIED_KELP_BLOCK);
        registerCompostableItem(0.5f, Items.TALL_GRASS);
        registerCompostableItem(0.5f, Items.CACTUS);
        registerCompostableItem(0.5f, Items.SUGAR_CANE);
        registerCompostableItem(0.5f, Items.VINE);
        registerCompostableItem(0.5f, Items.lX);
        registerCompostableItem(0.65f, Items.SEA_PICKLE);
        registerCompostableItem(0.65f, Items.LILY_PAD);
        registerCompostableItem(0.65f, Items.PUMPKIN);
        registerCompostableItem(0.65f, Items.CARVED_PUMPKIN);
        registerCompostableItem(0.65f, Items.MELON);
        registerCompostableItem(0.65f, Items.je);
        registerCompostableItem(0.65f, Items.oO);
        registerCompostableItem(0.65f, Items.nI);
        registerCompostableItem(0.65f, Items.lk);
        registerCompostableItem(0.65f, Items.nJ);
        registerCompostableItem(0.65f, Items.jP);
        registerCompostableItem(0.65f, Items.BROWN_MUSHROOM);
        registerCompostableItem(0.65f, Items.RED_MUSHROOM);
        registerCompostableItem(0.65f, Items.MUSHROOM_STEM);
        registerCompostableItem(0.65f, Items.DANDELION);
        registerCompostableItem(0.65f, Items.POPPY);
        registerCompostableItem(0.65f, Items.BLUE_ORCHID);
        registerCompostableItem(0.65f, Items.ALLIUM);
        registerCompostableItem(0.65f, Items.AZURE_BLUET);
        registerCompostableItem(0.65f, Items.RED_TULIP);
        registerCompostableItem(0.65f, Items.ORANGE_TULIP);
        registerCompostableItem(0.65f, Items.WHITE_TULIP);
        registerCompostableItem(0.65f, Items.PINK_TULIP);
        registerCompostableItem(0.65f, Items.OXEYE_DAISY);
        registerCompostableItem(0.65f, Items.CORNFLOWER);
        registerCompostableItem(0.65f, Items.LILY_OF_THE_VALLEY);
        registerCompostableItem(0.65f, Items.WITHER_ROSE);
        registerCompostableItem(0.65f, Items.FERN);
        registerCompostableItem(0.65f, Items.SUNFLOWER);
        registerCompostableItem(0.65f, Items.LILAC);
        registerCompostableItem(0.65f, Items.ROSE_BUSH);
        registerCompostableItem(0.65f, Items.PEONY);
        registerCompostableItem(0.65f, Items.LARGE_FERN);
        registerCompostableItem(0.85f, Items.HAY_BLOCK);
        registerCompostableItem(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        registerCompostableItem(0.85f, Items.RED_MUSHROOM_BLOCK);
        registerCompostableItem(0.85f, Items.jQ);
        registerCompostableItem(0.85f, Items.nK);
        registerCompostableItem(0.85f, Items.lU);
        registerCompostableItem(1.0f, Items.CAKE);
        registerCompostableItem(1.0f, Items.nW);
    }
    
    private static void registerCompostableItem(final float levelIncreaseChance, final ItemProvider item) {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.getItem(), levelIncreaseChance);
    }
    
    public ComposterBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)ComposterBlock.LEVEL, 0));
    }
    
    @Environment(EnvType.CLIENT)
    public static void playEffects(final World world, final BlockPos pos, final boolean fill) {
        final BlockState blockState4 = world.getBlockState(pos);
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), fill ? SoundEvents.bj : SoundEvents.bi, SoundCategory.e, 1.0f, 1.0f, false);
        final double double5 = blockState4.getOutlineShape(world, pos).b(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        final double double6 = 0.13124999403953552;
        final double double7 = 0.737500011920929;
        final Random random11 = world.getRandom();
        for (int integer12 = 0; integer12 < 10; ++integer12) {
            final double double8 = random11.nextGaussian() * 0.02;
            final double double9 = random11.nextGaussian() * 0.02;
            final double double10 = random11.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.D, pos.getX() + 0.13124999403953552 + 0.737500011920929 * random11.nextFloat(), pos.getY() + double5 + random11.nextFloat() * (1.0 - double5), pos.getZ() + 0.13124999403953552 + 0.737500011920929 * random11.nextFloat(), double8, double9, double10);
        }
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return ComposterBlock.LEVEL_TO_COLLISION_SHAPE[state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL)];
    }
    
    @Override
    public VoxelShape getRayTraceShape(final BlockState state, final BlockView view, final BlockPos pos) {
        return ComposterBlock.RAY_TRACE_SHAPE;
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return ComposterBlock.LEVEL_TO_COLLISION_SHAPE[0];
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL) == 7) {
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final int integer7 = state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL);
        final ItemStack itemStack8 = player.getStackInHand(hand);
        if (integer7 < 8 && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack8.getItem())) {
            if (integer7 < 7 && !world.isClient) {
                final boolean boolean9 = addToComposter(state, world, pos, itemStack8);
                world.playLevelEvent(1500, pos, boolean9 ? 1 : 0);
                if (!player.abilities.creativeMode) {
                    itemStack8.subtractAmount(1);
                }
            }
            return true;
        }
        if (integer7 == 8) {
            if (!world.isClient) {
                final float float9 = 0.7f;
                final double double10 = world.random.nextFloat() * 0.7f + 0.15000000596046448;
                final double double11 = world.random.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
                final double double12 = world.random.nextFloat() * 0.7f + 0.15000000596046448;
                final ItemEntity itemEntity16 = new ItemEntity(world, pos.getX() + double10, pos.getY() + double11, pos.getZ() + double12, new ItemStack(Items.lw));
                itemEntity16.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity16);
            }
            emptyComposter(state, world, pos);
            world.playSound(null, pos, SoundEvents.bh, SoundCategory.e, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
    
    private static void emptyComposter(final BlockState state, final IWorld world, final BlockPos pos) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)ComposterBlock.LEVEL, 0), 3);
    }
    
    private static boolean addToComposter(final BlockState state, final IWorld world, final BlockPos pos, final ItemStack item) {
        final int integer5 = state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL);
        final float float6 = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item.getItem());
        if ((integer5 == 0 && float6 > 0.0f) || world.getRandom().nextDouble() < float6) {
            final int integer6 = integer5 + 1;
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)ComposterBlock.LEVEL, integer6), 3);
            if (integer6 == 7) {
                world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL) == 7) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)ComposterBlock.LEVEL), 3);
            world.playSound(null, pos, SoundEvents.bk, SoundCategory.e, 1.0f, 1.0f);
        }
        super.onScheduledTick(state, world, pos, random);
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return state.<Integer>get((Property<Integer>)ComposterBlock.LEVEL);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ComposterBlock.LEVEL);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    @Override
    public SidedInventory getInventory(final BlockState blockState, final IWorld iWorld, final BlockPos blockPos) {
        final int integer4 = blockState.<Integer>get((Property<Integer>)ComposterBlock.LEVEL);
        if (integer4 == 8) {
            return new FullComposterInventory(blockState, iWorld, blockPos, new ItemStack(Items.lw));
        }
        if (integer4 < 7) {
            return new ComposterInventory(blockState, iWorld, blockPos);
        }
        return new a();
    }
    
    static {
        LEVEL = Properties.COMPOSTER_LEVEL;
        ITEM_TO_LEVEL_INCREASE_CHANCE = (Object2FloatMap)new Object2FloatOpenHashMap();
        RAY_TRACE_SHAPE = VoxelShapes.fullCube();
        int integer2;
        LEVEL_TO_COLLISION_SHAPE = SystemUtil.<VoxelShape[]>consume(new VoxelShape[9], arr -> {
            for (integer2 = 0; integer2 < 8; ++integer2) {
                arr[integer2] = VoxelShapes.combineAndSimplify(ComposterBlock.RAY_TRACE_SHAPE, Block.createCuboidShape(2.0, Math.max(2, 1 + integer2 * 2), 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
            }
            arr[8] = arr[7];
        });
    }
    
    static class a extends BasicInventory implements SidedInventory
    {
        public a() {
            super(0);
        }
        
        @Override
        public int[] getInvAvailableSlots(final Direction side) {
            return new int[0];
        }
        
        @Override
        public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
            return false;
        }
        
        @Override
        public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
            return false;
        }
    }
    
    static class FullComposterInventory extends BasicInventory implements SidedInventory
    {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean dirty;
        
        public FullComposterInventory(final BlockState state, final IWorld world, final BlockPos pos, final ItemStack outputItem) {
            super(outputItem);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }
        
        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }
        
        @Override
        public int[] getInvAvailableSlots(final Direction side) {
            return (side == Direction.DOWN) ? new int[] { 0 } : new int[0];
        }
        
        @Override
        public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
            return false;
        }
        
        @Override
        public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
            return !this.dirty && direction == Direction.DOWN && stack.getItem() == Items.lw;
        }
        
        @Override
        public void markDirty() {
            emptyComposter(this.state, this.world, this.pos);
            this.dirty = true;
        }
    }
    
    static class ComposterInventory extends BasicInventory implements SidedInventory
    {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean dirty;
        
        public ComposterInventory(final BlockState state, final IWorld world, final BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }
        
        @Override
        public int getInvMaxStackAmount() {
            return 1;
        }
        
        @Override
        public int[] getInvAvailableSlots(final Direction side) {
            return (side == Direction.UP) ? new int[] { 0 } : new int[0];
        }
        
        @Override
        public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
            return !this.dirty && direction == Direction.UP && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
        }
        
        @Override
        public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
            return false;
        }
        
        @Override
        public void markDirty() {
            final ItemStack itemStack1 = this.getInvStack(0);
            if (!itemStack1.isEmpty()) {
                this.dirty = true;
                addToComposter(this.state, this.world, this.pos, itemStack1);
                this.removeInvStack(0);
            }
        }
    }
}
