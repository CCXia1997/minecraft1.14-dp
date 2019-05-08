package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.util.math.Position;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import java.util.function.Predicate;

public class BreakDoorGoal extends DoorInteractGoal
{
    private final Predicate<Difficulty> g;
    protected int breakProgress;
    protected int prevBreakProgress;
    protected int c;
    
    public BreakDoorGoal(final MobEntity mobEntity, final Predicate<Difficulty> predicate) {
        super(mobEntity);
        this.prevBreakProgress = -1;
        this.c = -1;
        this.g = predicate;
    }
    
    public BreakDoorGoal(final MobEntity mobEntity, final int integer, final Predicate<Difficulty> predicate) {
        this(mobEntity, predicate);
        this.c = integer;
    }
    
    protected int f() {
        return Math.max(240, this.c);
    }
    
    @Override
    public boolean canStart() {
        return super.canStart() && this.owner.world.getGameRules().getBoolean("mobGriefing") && this.a(this.owner.world.getDifficulty()) && !this.g();
    }
    
    @Override
    public void start() {
        super.start();
        this.breakProgress = 0;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.f() && !this.g() && this.doorPos.isWithinDistance(this.owner.getPos(), 2.0) && this.a(this.owner.world.getDifficulty());
    }
    
    @Override
    public void stop() {
        super.stop();
        this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, -1);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.owner.getRand().nextInt(20) == 0) {
            this.owner.world.playLevelEvent(1019, this.doorPos, 0);
            if (!this.owner.isHandSwinging) {
                this.owner.swingHand(this.owner.getActiveHand());
            }
        }
        ++this.breakProgress;
        final int integer1 = (int)(this.breakProgress / (float)this.f() * 10.0f);
        if (integer1 != this.prevBreakProgress) {
            this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, integer1);
            this.prevBreakProgress = integer1;
        }
        if (this.breakProgress == this.f() && this.a(this.owner.world.getDifficulty())) {
            this.owner.world.clearBlockState(this.doorPos, false);
            this.owner.world.playLevelEvent(1021, this.doorPos, 0);
            this.owner.world.playLevelEvent(2001, this.doorPos, Block.getRawIdFromState(this.owner.world.getBlockState(this.doorPos)));
        }
    }
    
    private boolean a(final Difficulty difficulty) {
        return this.g.test(difficulty);
    }
}
