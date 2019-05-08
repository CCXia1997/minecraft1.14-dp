package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.decoration.EnderCrystalEntity;

public interface Phase
{
    boolean a();
    
    void b();
    
    void c();
    
    void crystalDestroyed(final EnderCrystalEntity arg1, final BlockPos arg2, final DamageSource arg3, @Nullable final PlayerEntity arg4);
    
    void beginPhase();
    
    void endPhase();
    
    float f();
    
    float h();
    
    PhaseType<? extends Phase> getType();
    
    @Nullable
    Vec3d getTarget();
    
    float modifyDamageTaken(final DamageSource arg1, final float arg2);
}
