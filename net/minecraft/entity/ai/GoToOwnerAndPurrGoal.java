package net.minecraft.entity.ai;

import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;

public class GoToOwnerAndPurrGoal extends MoveToTargetPosGoal
{
    private final CatEntity cat;
    
    public GoToOwnerAndPurrGoal(final CatEntity catEntity, final double double2, final int integer4) {
        super(catEntity, double2, integer4, 6);
        this.cat = catEntity;
        this.lowestY = -2;
        this.setControls(EnumSet.<Control>of(Control.c, Control.a));
    }
    
    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.isSleepingWithOwner() && super.canStart();
    }
    
    @Override
    public void start() {
        super.start();
        this.cat.getSitGoal().setEnabledWithOwner(false);
    }
    
    @Override
    protected int getInterval(final MobEntityWithAi mob) {
        return 40;
    }
    
    @Override
    public void stop() {
        super.stop();
        this.cat.setSleepingWithOwner(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.cat.getSitGoal().setEnabledWithOwner(false);
        if (!this.hasReached()) {
            this.cat.setSleepingWithOwner(false);
        }
        else if (!this.cat.isSleepingWithOwner()) {
            this.cat.setSleepingWithOwner(true);
        }
    }
    
    @Override
    protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
        return world.isAir(pos.up()) && world.getBlockState(pos).getBlock().matches(BlockTags.F);
    }
}
