package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.ZombieEntity;

public class ZombieRaiseArmsGoal extends MeleeAttackGoal
{
    private final ZombieEntity zombie;
    private int e;
    
    public ZombieRaiseArmsGoal(final ZombieEntity zombieEntity, final double double2, final boolean boolean4) {
        super(zombieEntity, double2, boolean4);
        this.zombie = zombieEntity;
    }
    
    @Override
    public void start() {
        super.start();
        this.e = 0;
    }
    
    @Override
    public void stop() {
        super.stop();
        this.zombie.setAttacking(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.e;
        if (this.e >= 5 && this.ticksUntilAttack < 10) {
            this.zombie.setAttacking(true);
        }
        else {
            this.zombie.setAttacking(false);
        }
    }
}
