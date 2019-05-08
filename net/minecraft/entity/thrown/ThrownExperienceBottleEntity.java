package net.minecraft.entity.thrown;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class ThrownExperienceBottleEntity extends ThrownItemEntity
{
    public ThrownExperienceBottleEntity(final EntityType<? extends ThrownExperienceBottleEntity> type, final World world) {
        super(type, world);
    }
    
    public ThrownExperienceBottleEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.EXPERIENCE_BOTTLE, livingEntity, world);
    }
    
    public ThrownExperienceBottleEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.EXPERIENCE_BOTTLE, double2, double4, double6, world);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.nB;
    }
    
    @Override
    protected float getGravity() {
        return 0.07f;
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (!this.world.isClient) {
            this.world.playLevelEvent(2002, new BlockPos(this), PotionUtil.getColor(Potions.b));
            int integer2 = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);
            while (integer2 > 0) {
                final int integer3 = ExperienceOrbEntity.roundToOrbSize(integer2);
                integer2 -= integer3;
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, integer3));
            }
            this.remove();
        }
    }
}
