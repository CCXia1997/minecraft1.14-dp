package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShape;

public class LilyPadBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE;
    
    protected LilyPadBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (entity instanceof BoatEntity) {
            world.breakBlock(new BlockPos(pos), true);
        }
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return LilyPadBlock.SHAPE;
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        final FluidState fluidState4 = view.getFluidState(pos);
        return fluidState4.getFluid() == Fluids.WATER || floor.getMaterial() == Material.ICE;
    }
    
    static {
        SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);
    }
}
