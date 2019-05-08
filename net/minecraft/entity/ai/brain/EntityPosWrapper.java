package net.minecraft.entity.ai.brain;

import java.util.Optional;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;

public class EntityPosWrapper implements LookTarget
{
    private final LivingEntity entity;
    
    public EntityPosWrapper(final LivingEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public BlockPos getBlockPos() {
        return new BlockPos(this.entity.x, this.entity.y, this.entity.z);
    }
    
    @Override
    public Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y + this.entity.getStandingEyeHeight(), this.entity.z);
    }
    
    @Override
    public boolean isSeenBy(final LivingEntity livingEntity) {
        final Optional<List<LivingEntity>> optional2 = livingEntity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g);
        return this.entity.isAlive() && optional2.isPresent() && optional2.get().contains(this.entity);
    }
    
    @Override
    public String toString() {
        return "EntityPosWrapper for " + this.entity;
    }
}
