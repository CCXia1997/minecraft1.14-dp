package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public abstract class AbstractPhase implements Phase
{
    protected final EnderDragonEntity dragon;
    
    public AbstractPhase(final EnderDragonEntity dragon) {
        this.dragon = dragon;
    }
    
    @Override
    public boolean a() {
        return false;
    }
    
    @Override
    public void b() {
    }
    
    @Override
    public void c() {
    }
    
    @Override
    public void crystalDestroyed(final EnderCrystalEntity crystal, final BlockPos pos, final DamageSource source, @Nullable final PlayerEntity player) {
    }
    
    @Override
    public void beginPhase() {
    }
    
    @Override
    public void endPhase() {
    }
    
    @Override
    public float f() {
        return 0.6f;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return null;
    }
    
    @Override
    public float modifyDamageTaken(final DamageSource damageSource, final float float2) {
        return float2;
    }
    
    @Override
    public float h() {
        final float float1 = MathHelper.sqrt(Entity.squaredHorizontalLength(this.dragon.getVelocity())) + 1.0f;
        final float float2 = Math.min(float1, 40.0f);
        return 0.7f / float2 / float1;
    }
}
