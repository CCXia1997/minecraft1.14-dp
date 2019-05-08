package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class CropBlock extends PlantBlock implements Fertilizable
{
    public static final IntegerProperty AGE;
    private static final VoxelShape[] AGE_TO_SHAPE;
    
    protected CropBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)this.getAgeProperty(), 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CropBlock.AGE_TO_SHAPE[state.<Integer>get((Property<Integer>)this.getAgeProperty())];
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return floor.getBlock() == Blocks.bV;
    }
    
    public IntegerProperty getAgeProperty() {
        return CropBlock.AGE;
    }
    
    public int getCropAgeMaximum() {
        return 7;
    }
    
    protected int getCropAge(final BlockState blockState) {
        return blockState.<Integer>get((Property<Integer>)this.getAgeProperty());
    }
    
    public BlockState withCropAge(final int integer) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)this.getAgeProperty(), integer);
    }
    
    public boolean isValidState(final BlockState blockState) {
        return blockState.<Integer>get((Property<Integer>)this.getAgeProperty()) >= this.getCropAgeMaximum();
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        super.onScheduledTick(state, world, pos, random);
        if (world.getLightLevel(pos, 0) >= 9) {
            final int integer5 = this.getCropAge(state);
            if (integer5 < this.getCropAgeMaximum()) {
                final float float6 = getAvailableMoisture(this, world, pos);
                if (random.nextInt((int)(25.0f / float6) + 1) == 0) {
                    world.setBlockState(pos, this.withCropAge(integer5 + 1), 2);
                }
            }
        }
    }
    
    public void applyGrowth(final World world, final BlockPos blockPos, final BlockState blockState) {
        int integer4 = this.getCropAge(blockState) + this.getGrowthAmount(world);
        final int integer5 = this.getCropAgeMaximum();
        if (integer4 > integer5) {
            integer4 = integer5;
        }
        world.setBlockState(blockPos, this.withCropAge(integer4), 2);
    }
    
    protected int getGrowthAmount(final World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }
    
    protected static float getAvailableMoisture(final Block block, final BlockView world, final BlockPos pos) {
        float float4 = 1.0f;
        final BlockPos blockPos5 = pos.down();
        for (int integer6 = -1; integer6 <= 1; ++integer6) {
            for (int integer7 = -1; integer7 <= 1; ++integer7) {
                float float5 = 0.0f;
                final BlockState blockState9 = world.getBlockState(blockPos5.add(integer6, 0, integer7));
                if (blockState9.getBlock() == Blocks.bV) {
                    float5 = 1.0f;
                    if (blockState9.<Integer>get((Property<Integer>)FarmlandBlock.MOISTURE) > 0) {
                        float5 = 3.0f;
                    }
                }
                if (integer6 != 0 || integer7 != 0) {
                    float5 /= 4.0f;
                }
                float4 += float5;
            }
        }
        final BlockPos blockPos6 = pos.north();
        final BlockPos blockPos7 = pos.south();
        final BlockPos blockPos8 = pos.west();
        final BlockPos blockPos9 = pos.east();
        final boolean boolean10 = block == world.getBlockState(blockPos8).getBlock() || block == world.getBlockState(blockPos9).getBlock();
        final boolean boolean11 = block == world.getBlockState(blockPos6).getBlock() || block == world.getBlockState(blockPos7).getBlock();
        if (boolean10 && boolean11) {
            float4 /= 2.0f;
        }
        else {
            final boolean boolean12 = block == world.getBlockState(blockPos8.north()).getBlock() || block == world.getBlockState(blockPos9.north()).getBlock() || block == world.getBlockState(blockPos9.south()).getBlock() || block == world.getBlockState(blockPos8.south()).getBlock();
            if (boolean12) {
                float4 /= 2.0f;
            }
        }
        return float4;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean("mobGriefing")) {
            world.breakBlock(pos, true);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
    
    @Environment(EnvType.CLIENT)
    protected ItemProvider getCropItem() {
        return Items.jO;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(this.getCropItem());
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return !this.isValidState(state);
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        this.applyGrowth(world, pos, state);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CropBlock.AGE);
    }
    
    static {
        AGE = Properties.AGE_7;
        AGE_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
    }
}
