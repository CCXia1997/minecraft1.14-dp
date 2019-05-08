package net.minecraft.entity.mob;

import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class GiantEntity extends HostileEntity
{
    public GiantEntity(final EntityType<? extends GiantEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 10.440001f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(50.0);
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        return world.getBrightness(pos) - 0.5f;
    }
}
