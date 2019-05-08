package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class SittingAttackingPhase extends AbstractSittingPhase
{
    private int b;
    
    public SittingAttackingPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void b() {
        this.dragon.world.playSound(this.dragon.x, this.dragon.y, this.dragon.z, SoundEvents.cA, this.dragon.getSoundCategory(), 2.5f, 0.8f + this.dragon.getRand().nextFloat() * 0.3f, false);
    }
    
    @Override
    public void c() {
        if (this.b++ >= 40) {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
        }
    }
    
    @Override
    public void beginPhase() {
        this.b = 0;
    }
    
    @Override
    public PhaseType<SittingAttackingPhase> getType() {
        return PhaseType.SITTING_ATTACKING;
    }
}
