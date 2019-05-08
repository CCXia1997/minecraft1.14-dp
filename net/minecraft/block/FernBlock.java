package net.minecraft.block;

import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class FernBlock extends PlantBlock implements Fertilizable
{
    protected static final VoxelShape SHAPE;
    
    protected FernBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return FernBlock.SHAPE;
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        final TallPlantBlock tallPlantBlock5 = (TallPlantBlock)((this == Blocks.aR) ? Blocks.gR : Blocks.gQ);
        if (tallPlantBlock5.getDefaultState().canPlaceAt(world, pos) && world.isAir(pos.up())) {
            tallPlantBlock5.placeAt(world, pos, 2);
        }
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XYZ;
    }
    
    static {
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    }
}
