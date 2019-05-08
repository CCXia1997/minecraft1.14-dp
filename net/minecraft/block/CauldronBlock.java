package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.container.Container;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class CauldronBlock extends Block
{
    public static final IntegerProperty LEVEL;
    private static final VoxelShape RAY_TRACE_SHAPE;
    protected static final VoxelShape OUTLINE_SHAPE;
    
    public CauldronBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)CauldronBlock.LEVEL, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CauldronBlock.OUTLINE_SHAPE;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return false;
    }
    
    @Override
    public VoxelShape getRayTraceShape(final BlockState state, final BlockView view, final BlockPos pos) {
        return CauldronBlock.RAY_TRACE_SHAPE;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        final int integer5 = state.<Integer>get((Property<Integer>)CauldronBlock.LEVEL);
        final float float6 = pos.getY() + (6.0f + 3 * integer5) / 16.0f;
        if (!world.isClient && entity.isOnFire() && integer5 > 0 && entity.getBoundingBox().minY <= float6) {
            entity.extinguish();
            this.setLevel(world, pos, state, integer5 - 1);
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final ItemStack itemStack7 = player.getStackInHand(hand);
        if (itemStack7.isEmpty()) {
            return true;
        }
        final int integer8 = state.<Integer>get((Property<Integer>)CauldronBlock.LEVEL);
        final Item item9 = itemStack7.getItem();
        if (item9 == Items.ky) {
            if (integer8 < 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    player.setStackInHand(hand, new ItemStack(Items.kx));
                }
                player.incrementStat(Stats.T);
                this.setLevel(world, pos, state, 3);
                world.playSound(null, pos, SoundEvents.aw, SoundCategory.e, 1.0f, 1.0f);
            }
            return true;
        }
        if (item9 == Items.kx) {
            if (integer8 == 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    itemStack7.subtractAmount(1);
                    if (itemStack7.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.ky));
                    }
                    else if (!player.inventory.insertStack(new ItemStack(Items.ky))) {
                        player.dropItem(new ItemStack(Items.ky), false);
                    }
                }
                player.incrementStat(Stats.U);
                this.setLevel(world, pos, state, 0);
                world.playSound(null, pos, SoundEvents.az, SoundCategory.e, 1.0f, 1.0f);
            }
            return true;
        }
        if (item9 == Items.mm) {
            if (integer8 > 0 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    final ItemStack itemStack8 = PotionUtil.setPotion(new ItemStack(Items.ml), Potions.b);
                    player.incrementStat(Stats.U);
                    itemStack7.subtractAmount(1);
                    if (itemStack7.isEmpty()) {
                        player.setStackInHand(hand, itemStack8);
                    }
                    else if (!player.inventory.insertStack(itemStack8)) {
                        player.dropItem(itemStack8, false);
                    }
                    else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).openContainer(player.playerContainer);
                    }
                }
                world.playSound(null, pos, SoundEvents.ao, SoundCategory.e, 1.0f, 1.0f);
                this.setLevel(world, pos, state, integer8 - 1);
            }
            return true;
        }
        if (item9 == Items.ml && PotionUtil.getPotion(itemStack7) == Potions.b) {
            if (integer8 < 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    final ItemStack itemStack8 = new ItemStack(Items.mm);
                    player.incrementStat(Stats.U);
                    player.setStackInHand(hand, itemStack8);
                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).openContainer(player.playerContainer);
                    }
                }
                world.playSound(null, pos, SoundEvents.an, SoundCategory.e, 1.0f, 1.0f);
                this.setLevel(world, pos, state, integer8 + 1);
            }
            return true;
        }
        if (integer8 > 0 && item9 instanceof DyeableItem) {
            final DyeableItem dyeableItem10 = (DyeableItem)item9;
            if (dyeableItem10.hasColor(itemStack7) && !world.isClient) {
                dyeableItem10.removeColor(itemStack7);
                this.setLevel(world, pos, state, integer8 - 1);
                player.incrementStat(Stats.V);
                return true;
            }
        }
        if (integer8 > 0 && item9 instanceof BannerItem) {
            if (BannerBlockEntity.getPatternCount(itemStack7) > 0 && !world.isClient) {
                final ItemStack itemStack8 = itemStack7.copy();
                itemStack8.setAmount(1);
                BannerBlockEntity.loadFromItemStack(itemStack8);
                player.incrementStat(Stats.W);
                if (!player.abilities.creativeMode) {
                    itemStack7.subtractAmount(1);
                    this.setLevel(world, pos, state, integer8 - 1);
                }
                if (itemStack7.isEmpty()) {
                    player.setStackInHand(hand, itemStack8);
                }
                else if (!player.inventory.insertStack(itemStack8)) {
                    player.dropItem(itemStack8, false);
                }
                else if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)player).openContainer(player.playerContainer);
                }
            }
            return true;
        }
        if (integer8 > 0 && item9 instanceof BlockItem) {
            final Block block10 = ((BlockItem)item9).getBlock();
            if (block10 instanceof ShulkerBoxBlock && !world.isClient()) {
                final ItemStack itemStack9 = new ItemStack(Blocks.iH, 1);
                if (itemStack7.hasTag()) {
                    itemStack9.setTag(itemStack7.getTag().copy());
                }
                player.setStackInHand(hand, itemStack9);
                this.setLevel(world, pos, state, integer8 - 1);
                player.incrementStat(Stats.X);
            }
            return true;
        }
        return false;
    }
    
    public void setLevel(final World world, final BlockPos pos, final BlockState state, final int level) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CauldronBlock.LEVEL, MathHelper.clamp(level, 0, 3)), 2);
        world.updateHorizontalAdjacent(pos, this);
    }
    
    @Override
    public void onRainTick(final World world, final BlockPos pos) {
        if (world.random.nextInt(20) != 1) {
            return;
        }
        final float float3 = world.getBiome(pos).getTemperature(pos);
        if (float3 < 0.15f) {
            return;
        }
        final BlockState blockState4 = world.getBlockState(pos);
        if (blockState4.<Integer>get((Property<Integer>)CauldronBlock.LEVEL) < 3) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)blockState4).<Comparable>cycle((Property<Comparable>)CauldronBlock.LEVEL), 2);
        }
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return state.<Integer>get((Property<Integer>)CauldronBlock.LEVEL);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CauldronBlock.LEVEL);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        LEVEL = Properties.CAULDRON_LEVEL;
        RAY_TRACE_SHAPE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), CauldronBlock.RAY_TRACE_SHAPE), BooleanBiFunction.ONLY_FIRST);
    }
}
