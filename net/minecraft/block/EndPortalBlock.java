package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class EndPortalBlock extends BlockWithEntity
{
    protected static final VoxelShape SHAPE;
    
    protected EndPortalBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new EndPortalBlockEntity();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return EndPortalBlock.SHAPE;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (!world.isClient && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals() && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), state.getOutlineShape(world, pos), BooleanBiFunction.AND)) {
            entity.changeDimension((world.dimension.getType() == DimensionType.c) ? DimensionType.a : DimensionType.c);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final double double5 = pos.getX() + rnd.nextFloat();
        final double double6 = pos.getY() + 0.8f;
        final double double7 = pos.getZ() + rnd.nextFloat();
        final double double8 = 0.0;
        final double double9 = 0.0;
        final double double10 = 0.0;
        world.addParticle(ParticleTypes.Q, double5, double6, double7, 0.0, 0.0, 0.0);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
    
    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    }
}
