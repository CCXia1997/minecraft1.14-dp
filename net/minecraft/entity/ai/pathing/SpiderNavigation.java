package net.minecraft.entity.ai.pathing;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class SpiderNavigation extends MobNavigation
{
    private BlockPos p;
    
    public SpiderNavigation(final MobEntity mobEntity, final World world) {
        super(mobEntity, world);
    }
    
    @Override
    public Path findPathTo(final BlockPos pos) {
        this.p = pos;
        return super.findPathTo(pos);
    }
    
    @Override
    public Path findPathTo(final Entity entity) {
        this.p = new BlockPos(entity);
        return super.findPathTo(entity);
    }
    
    @Override
    public boolean startMovingTo(final Entity entity, final double speed) {
        final Path path4 = this.findPathTo(entity);
        if (path4 != null) {
            return this.startMovingAlong(path4, speed);
        }
        this.p = new BlockPos(entity);
        this.speed = speed;
        return true;
    }
    
    @Override
    public void tick() {
        if (this.isIdle()) {
            if (this.p != null) {
                if (this.p.isWithinDistance(this.entity.getPos(), this.entity.getWidth()) || (this.entity.y > this.p.getY() && new BlockPos(this.p.getX(), MathHelper.floor(this.entity.y), this.p.getZ()).isWithinDistance(this.entity.getPos(), this.entity.getWidth()))) {
                    this.p = null;
                }
                else {
                    this.entity.getMoveControl().moveTo(this.p.getX(), this.p.getY(), this.p.getZ(), this.speed);
                }
            }
            return;
        }
        super.tick();
    }
}
