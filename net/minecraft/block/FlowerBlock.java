package net.minecraft.block;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.shape.VoxelShape;

public class FlowerBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE;
    private final StatusEffect effectInStew;
    private final int effectInStewDuration;
    
    public FlowerBlock(final StatusEffect suspiciousStewEffect, final int integer, final Settings settings) {
        super(settings);
        this.effectInStew = suspiciousStewEffect;
        if (suspiciousStewEffect.isInstant()) {
            this.effectInStewDuration = integer;
        }
        else {
            this.effectInStewDuration = integer * 20;
        }
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Vec3d vec3d5 = state.getOffsetPos(view, pos);
        return FlowerBlock.SHAPE.offset(vec3d5.x, vec3d5.y, vec3d5.z);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    public StatusEffect getEffectInStew() {
        return this.effectInStew;
    }
    
    public int getEffectInStewDuration() {
        return this.effectInStewDuration;
    }
    
    static {
        SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
    }
}
