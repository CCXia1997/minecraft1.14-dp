package net.minecraft.block;

import net.minecraft.state.property.Properties;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.state.StateFactory;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class BeetrootsBlock extends CropBlock
{
    public static final IntegerProperty AGE;
    private static final VoxelShape[] AGE_TO_SHAPE;
    
    public BeetrootsBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public IntegerProperty getAgeProperty() {
        return BeetrootsBlock.AGE;
    }
    
    @Override
    public int getCropAgeMaximum() {
        return 3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    protected ItemProvider getCropItem() {
        return Items.oP;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (random.nextInt(3) != 0) {
            super.onScheduledTick(state, world, pos, random);
        }
    }
    
    @Override
    protected int getGrowthAmount(final World world) {
        return super.getGrowthAmount(world) / 3;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BeetrootsBlock.AGE);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return BeetrootsBlock.AGE_TO_SHAPE[state.<Integer>get((Property<Integer>)this.getAgeProperty())];
    }
    
    static {
        AGE = Properties.AGE_3;
        AGE_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0) };
    }
}
