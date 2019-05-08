package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.Vec3d;

public class RayTraceContext
{
    private final Vec3d start;
    private final Vec3d end;
    private final ShapeType shapeType;
    private final FluidHandling fluid;
    private final VerticalEntityPosition entityPosition;
    
    public RayTraceContext(final Vec3d vec3d1, final Vec3d vec3d2, final ShapeType shapeType, final FluidHandling fluidHandling, final Entity entity) {
        this.start = vec3d1;
        this.end = vec3d2;
        this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.entityPosition = VerticalEntityPosition.fromEntity(entity);
    }
    
    public Vec3d getEnd() {
        return this.end;
    }
    
    public Vec3d getStart() {
        return this.start;
    }
    
    public VoxelShape getBlockShape(final BlockState blockState, final BlockView blockView, final BlockPos blockPos) {
        return this.shapeType.get(blockState, blockView, blockPos, this.entityPosition);
    }
    
    public VoxelShape getFluidShape(final FluidState fluidState, final BlockView blockView, final BlockPos blockPos) {
        return this.fluid.handled(fluidState) ? fluidState.getShape(blockView, blockPos) : VoxelShapes.empty();
    }
    
    public enum ShapeType implements ShapeProvider
    {
        a(BlockState::getCollisionShape), 
        b(BlockState::getOutlineShape);
        
        private final ShapeProvider provider;
        
        private ShapeType(final ShapeProvider shapeProvider) {
            this.provider = shapeProvider;
        }
        
        @Override
        public VoxelShape get(final BlockState blockState, final BlockView blockView, final BlockPos blockPos, final VerticalEntityPosition verticalEntityPosition) {
            return this.provider.get(blockState, blockView, blockPos, verticalEntityPosition);
        }
    }
    
    public enum FluidHandling
    {
        NONE(fluidState -> false), 
        b(FluidState::isStill), 
        c(fluidState -> !fluidState.isEmpty());
        
        private final Predicate<FluidState> predicate;
        
        private FluidHandling(final Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }
        
        public boolean handled(final FluidState fluidState) {
            return this.predicate.test(fluidState);
        }
    }
    
    public interface ShapeProvider
    {
        VoxelShape get(final BlockState arg1, final BlockView arg2, final BlockPos arg3, final VerticalEntityPosition arg4);
    }
}
