package net.minecraft.entity.mob;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class ElderGuardianEntity extends GuardianEntity
{
    public static final float b;
    
    public ElderGuardianEntity(final EntityType<? extends ElderGuardianEntity> type, final World world) {
        super(type, world);
        this.setPersistent();
        if (this.wanderGoal != null) {
            this.wanderGoal.setChance(400);
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(80.0);
    }
    
    @Override
    public int getWarmupTime() {
        return 60;
    }
    
    @Environment(EnvType.CLIENT)
    public void straightenTail() {
        this.tailAngle = 1.0f;
        this.prevTailAngle = this.tailAngle;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ck : SoundEvents.cl;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.cq : SoundEvents.cr;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.cn : SoundEvents.co;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.cp;
    }
    
    @Override
    protected void mobTick() {
        super.mobTick();
        final int integer1 = 1200;
        if ((this.age + this.getEntityId()) % 1200 == 0) {
            final StatusEffect statusEffect2 = StatusEffects.d;
            final List<ServerPlayerEntity> list3 = ((ServerWorld)this.world).getPlayers(serverPlayerEntity -> this.squaredDistanceTo(serverPlayerEntity) < 2500.0 && serverPlayerEntity.interactionManager.isSurvivalLike());
            final int integer2 = 2;
            final int integer3 = 6000;
            final int integer4 = 1200;
            for (final ServerPlayerEntity serverPlayerEntity2 : list3) {
                if (!serverPlayerEntity2.hasStatusEffect(statusEffect2) || serverPlayerEntity2.getStatusEffect(statusEffect2).getAmplifier() < 2 || serverPlayerEntity2.getStatusEffect(statusEffect2).getDuration() < 1200) {
                    serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeS2CPacket(10, 0.0f));
                    serverPlayerEntity2.addPotionEffect(new StatusEffectInstance(statusEffect2, 6000, 2));
                }
            }
        }
        if (!this.hasWalkTargetRange()) {
            this.setWalkTarget(new BlockPos(this), 16);
        }
    }
    
    static {
        b = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();
    }
}
