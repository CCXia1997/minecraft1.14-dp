package net.minecraft.entity;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.item.Items;
import net.minecraft.item.Item;

public class VerticalEntityPositionImpl implements VerticalEntityPosition
{
    protected static final VerticalEntityPosition MIN_VALUE;
    private final boolean sneaking;
    private final double posY;
    private final Item d;
    
    protected VerticalEntityPositionImpl(final boolean sneaking, final double double2, final Item item4) {
        this.sneaking = sneaking;
        this.posY = double2;
        this.d = item4;
    }
    
    @Deprecated
    protected VerticalEntityPositionImpl(final Entity entity) {
        this(entity.isSneaking(), entity.getBoundingBox().minY, (entity instanceof LivingEntity) ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR);
    }
    
    @Override
    public boolean a(final Item item) {
        return this.d == item;
    }
    
    @Override
    public boolean isSneaking() {
        return this.sneaking;
    }
    
    @Override
    public boolean isAboveBlock(final VoxelShape shape, final BlockPos blockPos, final boolean boolean3) {
        return this.posY > blockPos.getY() + shape.getMaximum(Direction.Axis.Y) - 9.999999747378752E-6;
    }
    
    static {
        MIN_VALUE = new VerticalEntityPositionImpl(false, -1.7976931348623157E308, Items.AIR) {
            @Override
            public boolean isAboveBlock(final VoxelShape shape, final BlockPos blockPos, final boolean boolean3) {
                return boolean3;
            }
        };
    }
}
