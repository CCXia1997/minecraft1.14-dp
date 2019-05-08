package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Position;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.pathing.Path;
import javax.annotation.Nullable;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.boss.dragon.phase.Phase;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.TrackedData;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.MobEntity;

public class EnderDragonEntity extends MobEntity implements Monster
{
    private static final Logger LOGGER;
    public static final TrackedData<Integer> PHASE_TYPE;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    public final double[][] c;
    public int d;
    public final EnderDragonPart[] parts;
    public final EnderDragonPart partHead;
    public final EnderDragonPart partNeck;
    public final EnderDragonPart partBody;
    public final EnderDragonPart partTail1;
    public final EnderDragonPart partTail2;
    public final EnderDragonPart partTail3;
    public final EnderDragonPart partWingRight;
    public final EnderDragonPart partWingLeft;
    public float bI;
    public float bJ;
    public boolean bK;
    public int bL;
    public EnderCrystalEntity enderCrystal;
    private final EnderDragonFight fight;
    private final PhaseManager phaseManager;
    private int bR;
    private int bS;
    private final PathNode[] bT;
    private final int[] bU;
    private final PathMinHeap bV;
    
    public EnderDragonEntity(final EntityType<? extends EnderDragonEntity> type, final World world) {
        super(EntityType.ENDER_DRAGON, world);
        this.c = new double[64][3];
        this.d = -1;
        this.bR = 100;
        this.bT = new PathNode[24];
        this.bU = new int[24];
        this.bV = new PathMinHeap();
        this.partHead = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.partNeck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.partBody = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.partTail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partWingRight = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.partWingLeft = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.parts = new EnderDragonPart[] { this.partHead, this.partNeck, this.partBody, this.partTail1, this.partTail2, this.partTail3, this.partWingRight, this.partWingLeft };
        this.setHealth(this.getHealthMaximum());
        this.noClip = true;
        this.ignoreCameraFrustum = true;
        if (!world.isClient && world.dimension instanceof TheEndDimension) {
            this.fight = ((TheEndDimension)world.dimension).q();
        }
        else {
            this.fight = null;
        }
        this.phaseManager = new PhaseManager(this);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(200.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().<Integer>startTracking(EnderDragonEntity.PHASE_TYPE, PhaseType.HOVER.getTypeId());
    }
    
    public double[] a(final int integer, float float2) {
        if (this.getHealth() <= 0.0f) {
            float2 = 0.0f;
        }
        float2 = 1.0f - float2;
        final int integer2 = this.d - integer & 0x3F;
        final int integer3 = this.d - integer - 1 & 0x3F;
        final double[] arr5 = new double[3];
        double double6 = this.c[integer2][0];
        double double7 = MathHelper.wrapDegrees(this.c[integer3][0] - double6);
        arr5[0] = double6 + double7 * float2;
        double6 = this.c[integer2][1];
        double7 = this.c[integer3][1] - double6;
        arr5[1] = double6 + double7 * float2;
        arr5[2] = MathHelper.lerp(float2, this.c[integer2][2], this.c[integer3][2]);
        return arr5;
    }
    
    @Override
    public void updateState() {
        if (this.world.isClient) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                final float float1 = MathHelper.cos(this.bJ * 6.2831855f);
                final float float2 = MathHelper.cos(this.bI * 6.2831855f);
                if (float2 <= -0.3f && float1 >= -0.3f) {
                    this.world.playSound(this.x, this.y, this.z, SoundEvents.cz, this.getSoundCategory(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
                }
                if (!this.phaseManager.getCurrent().a() && --this.bR < 0) {
                    this.world.playSound(this.x, this.y, this.z, SoundEvents.cA, this.getSoundCategory(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                    this.bR = 200 + this.random.nextInt(200);
                }
            }
        }
        this.bI = this.bJ;
        if (this.getHealth() <= 0.0f) {
            final float float1 = (this.random.nextFloat() - 0.5f) * 8.0f;
            final float float2 = (this.random.nextFloat() - 0.5f) * 4.0f;
            final float float3 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.w, this.x + float1, this.y + 2.0 + float2, this.z + float3, 0.0, 0.0, 0.0);
            return;
        }
        this.dW();
        final Vec3d vec3d1 = this.getVelocity();
        float float2 = 0.2f / (MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d1)) * 10.0f + 1.0f);
        float2 *= (float)Math.pow(2.0, vec3d1.y);
        if (this.phaseManager.getCurrent().a()) {
            this.bJ += 0.1f;
        }
        else if (this.bK) {
            this.bJ += float2 * 0.5f;
        }
        else {
            this.bJ += float2;
        }
        this.yaw = MathHelper.wrapDegrees(this.yaw);
        if (this.isAiDisabled()) {
            this.bJ = 0.5f;
            return;
        }
        if (this.d < 0) {
            for (int integer3 = 0; integer3 < this.c.length; ++integer3) {
                this.c[integer3][0] = this.yaw;
                this.c[integer3][1] = this.y;
            }
        }
        if (++this.d == this.c.length) {
            this.d = 0;
        }
        this.c[this.d][0] = this.yaw;
        this.c[this.d][1] = this.y;
        if (this.world.isClient) {
            if (this.bf > 0) {
                final double double3 = this.x + (this.bg - this.x) / this.bf;
                final double double4 = this.y + (this.bh - this.y) / this.bf;
                final double double5 = this.z + (this.bi - this.z) / this.bf;
                final double double6 = MathHelper.wrapDegrees(this.bj - this.yaw);
                this.yaw += (float)(double6 / this.bf);
                this.pitch += (float)((this.bk - this.pitch) / this.bf);
                --this.bf;
                this.setPosition(double3, double4, double5);
                this.setRotation(this.yaw, this.pitch);
            }
            this.phaseManager.getCurrent().b();
        }
        else {
            Phase phase3 = this.phaseManager.getCurrent();
            phase3.c();
            if (this.phaseManager.getCurrent() != phase3) {
                phase3 = this.phaseManager.getCurrent();
                phase3.c();
            }
            final Vec3d vec3d2 = phase3.getTarget();
            if (vec3d2 != null) {
                final double double4 = vec3d2.x - this.x;
                double double5 = vec3d2.y - this.y;
                final double double6 = vec3d2.z - this.z;
                final double double7 = double4 * double4 + double5 * double5 + double6 * double6;
                final float float4 = phase3.f();
                final double double8 = MathHelper.sqrt(double4 * double4 + double6 * double6);
                if (double8 > 0.0) {
                    double5 = MathHelper.clamp(double5 / double8, -float4, float4);
                }
                this.setVelocity(this.getVelocity().add(0.0, double5 * 0.01, 0.0));
                this.yaw = MathHelper.wrapDegrees(this.yaw);
                final double double9 = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(double4, double6) * 57.2957763671875 - this.yaw), -50.0, 50.0);
                final Vec3d vec3d3 = vec3d2.subtract(this.x, this.y, this.z).normalize();
                final Vec3d vec3d4 = new Vec3d(MathHelper.sin(this.yaw * 0.017453292f), this.getVelocity().y, -MathHelper.cos(this.yaw * 0.017453292f)).normalize();
                final float float5 = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5f) / 1.5f, 0.0f);
                this.be *= 0.8f;
                this.be += (float)(double9 * phase3.h());
                this.yaw += this.be * 0.1f;
                final float float6 = (float)(2.0 / (double7 + 1.0));
                final float float7 = 0.06f;
                this.updateVelocity(0.06f * (float5 * float6 + (1.0f - float6)), new Vec3d(0.0, 0.0, -1.0));
                if (this.bK) {
                    this.move(MovementType.a, this.getVelocity().multiply(0.800000011920929));
                }
                else {
                    this.move(MovementType.a, this.getVelocity());
                }
                final Vec3d vec3d5 = this.getVelocity().normalize();
                final double double10 = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
                this.setVelocity(this.getVelocity().multiply(double10, 0.9100000262260437, double10));
            }
        }
        this.aK = this.yaw;
        final Vec3d[] arr3 = new Vec3d[this.parts.length];
        for (int integer4 = 0; integer4 < this.parts.length; ++integer4) {
            arr3[integer4] = new Vec3d(this.parts[integer4].x, this.parts[integer4].y, this.parts[integer4].z);
        }
        final float float8 = (float)(this.a(5, 1.0f)[1] - this.a(10, 1.0f)[1]) * 10.0f * 0.017453292f;
        final float float9 = MathHelper.cos(float8);
        final float float10 = MathHelper.sin(float8);
        final float float11 = this.yaw * 0.017453292f;
        final float float12 = MathHelper.sin(float11);
        final float float13 = MathHelper.cos(float11);
        this.partBody.tick();
        this.partBody.setPositionAndAngles(this.x + float12 * 0.5f, this.y, this.z - float13 * 0.5f, 0.0f, 0.0f);
        this.partWingRight.tick();
        this.partWingRight.setPositionAndAngles(this.x + float13 * 4.5f, this.y + 2.0, this.z + float12 * 4.5f, 0.0f, 0.0f);
        this.partWingLeft.tick();
        this.partWingLeft.setPositionAndAngles(this.x - float13 * 4.5f, this.y + 2.0, this.z - float12 * 4.5f, 0.0f, 0.0f);
        if (!this.world.isClient && this.hurtTime == 0) {
            this.a(this.world.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.a(this.world.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.b(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.b(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
        final double[] arr4 = this.a(5, 1.0f);
        final float float14 = MathHelper.sin(this.yaw * 0.017453292f - this.be * 0.01f);
        final float float15 = MathHelper.cos(this.yaw * 0.017453292f - this.be * 0.01f);
        this.partHead.tick();
        this.partNeck.tick();
        final float float4 = this.v(1.0f);
        this.partHead.setPositionAndAngles(this.x + float14 * 6.5f * float9, this.y + float4 + float10 * 6.5f, this.z - float15 * 6.5f * float9, 0.0f, 0.0f);
        this.partNeck.setPositionAndAngles(this.x + float14 * 5.5f * float9, this.y + float4 + float10 * 5.5f, this.z - float15 * 5.5f * float9, 0.0f, 0.0f);
        for (int integer5 = 0; integer5 < 3; ++integer5) {
            EnderDragonPart enderDragonPart12 = null;
            if (integer5 == 0) {
                enderDragonPart12 = this.partTail1;
            }
            if (integer5 == 1) {
                enderDragonPart12 = this.partTail2;
            }
            if (integer5 == 2) {
                enderDragonPart12 = this.partTail3;
            }
            final double[] arr5 = this.a(12 + integer5 * 2, 1.0f);
            final float float16 = this.yaw * 0.017453292f + this.d(arr5[0] - arr4[0]) * 0.017453292f;
            final float float17 = MathHelper.sin(float16);
            final float float18 = MathHelper.cos(float16);
            final float float19 = 1.5f;
            final float float20 = (integer5 + 1) * 2.0f;
            enderDragonPart12.tick();
            enderDragonPart12.setPositionAndAngles(this.x - (float12 * 1.5f + float17 * float20) * float9, this.y + (arr5[1] - arr4[1]) - (float20 + 1.5f) * float10 + 1.5, this.z + (float13 * 1.5f + float18 * float20) * float9, 0.0f, 0.0f);
        }
        if (!this.world.isClient) {
            this.bK = (this.b(this.partHead.getBoundingBox()) | this.b(this.partNeck.getBoundingBox()) | this.b(this.partBody.getBoundingBox()));
            if (this.fight != null) {
                this.fight.updateFight(this);
            }
        }
        for (int integer5 = 0; integer5 < this.parts.length; ++integer5) {
            this.parts[integer5].prevX = arr3[integer5].x;
            this.parts[integer5].prevY = arr3[integer5].y;
            this.parts[integer5].prevZ = arr3[integer5].z;
        }
    }
    
    private float v(final float float1) {
        double double2;
        if (this.phaseManager.getCurrent().a()) {
            double2 = -1.0;
        }
        else {
            final double[] arr4 = this.a(5, 1.0f);
            final double[] arr5 = this.a(0, 1.0f);
            double2 = arr4[1] - arr5[1];
        }
        return (float)double2;
    }
    
    private void dW() {
        if (this.enderCrystal != null) {
            if (this.enderCrystal.removed) {
                this.enderCrystal = null;
            }
            else if (this.age % 10 == 0 && this.getHealth() < this.getHealthMaximum()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            final List<EnderCrystalEntity> list1 = this.world.<EnderCrystalEntity>getEntities(EnderCrystalEntity.class, this.getBoundingBox().expand(32.0));
            EnderCrystalEntity enderCrystalEntity2 = null;
            double double3 = Double.MAX_VALUE;
            for (final EnderCrystalEntity enderCrystalEntity3 : list1) {
                final double double4 = enderCrystalEntity3.squaredDistanceTo(this);
                if (double4 < double3) {
                    double3 = double4;
                    enderCrystalEntity2 = enderCrystalEntity3;
                }
            }
            this.enderCrystal = enderCrystalEntity2;
        }
    }
    
    private void a(final List<Entity> list) {
        final double double2 = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
        final double double3 = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;
        for (final Entity entity7 : list) {
            if (entity7 instanceof LivingEntity) {
                final double double4 = entity7.x - double2;
                final double double5 = entity7.z - double3;
                final double double6 = double4 * double4 + double5 * double5;
                entity7.addVelocity(double4 / double6 * 4.0, 0.20000000298023224, double5 / double6 * 4.0);
                if (this.phaseManager.getCurrent().a() || ((LivingEntity)entity7).getLastAttackedTime() >= entity7.age - 2) {
                    continue;
                }
                entity7.damage(DamageSource.mob(this), 5.0f);
                this.dealDamage(this, entity7);
            }
        }
    }
    
    private void b(final List<Entity> list) {
        for (int integer2 = 0; integer2 < list.size(); ++integer2) {
            final Entity entity3 = list.get(integer2);
            if (entity3 instanceof LivingEntity) {
                entity3.damage(DamageSource.mob(this), 10.0f);
                this.dealDamage(this, entity3);
            }
        }
    }
    
    private float d(final double double1) {
        return (float)MathHelper.wrapDegrees(double1);
    }
    
    private boolean b(final BoundingBox boundingBox) {
        final int integer2 = MathHelper.floor(boundingBox.minX);
        final int integer3 = MathHelper.floor(boundingBox.minY);
        final int integer4 = MathHelper.floor(boundingBox.minZ);
        final int integer5 = MathHelper.floor(boundingBox.maxX);
        final int integer6 = MathHelper.floor(boundingBox.maxY);
        final int integer7 = MathHelper.floor(boundingBox.maxZ);
        boolean boolean8 = false;
        boolean boolean9 = false;
        for (int integer8 = integer2; integer8 <= integer5; ++integer8) {
            for (int integer9 = integer3; integer9 <= integer6; ++integer9) {
                for (int integer10 = integer4; integer10 <= integer7; ++integer10) {
                    final BlockPos blockPos13 = new BlockPos(integer8, integer9, integer10);
                    final BlockState blockState14 = this.world.getBlockState(blockPos13);
                    final Block block15 = blockState14.getBlock();
                    if (!blockState14.isAir()) {
                        if (blockState14.getMaterial() != Material.FIRE) {
                            if (!this.world.getGameRules().getBoolean("mobGriefing") || BlockTags.W.contains(block15)) {
                                boolean8 = true;
                            }
                            else {
                                boolean9 = (this.world.clearBlockState(blockPos13, false) || boolean9);
                            }
                        }
                    }
                }
            }
        }
        if (boolean9) {
            final BlockPos blockPos14 = new BlockPos(integer2 + this.random.nextInt(integer5 - integer2 + 1), integer3 + this.random.nextInt(integer6 - integer3 + 1), integer4 + this.random.nextInt(integer7 - integer4 + 1));
            this.world.playLevelEvent(2008, blockPos14, 0);
        }
        return boolean8;
    }
    
    public boolean damagePart(final EnderDragonPart enderDragonPart, final DamageSource damageSource, float float3) {
        float3 = this.phaseManager.getCurrent().modifyDamageTaken(damageSource, float3);
        if (enderDragonPart != this.partHead) {
            float3 = float3 / 4.0f + Math.min(float3, 1.0f);
        }
        if (float3 < 0.01f) {
            return false;
        }
        if (damageSource.getAttacker() instanceof PlayerEntity || damageSource.isExplosive()) {
            final float float4 = this.getHealth();
            this.e(damageSource, float3);
            if (this.getHealth() <= 0.0f && !this.phaseManager.getCurrent().a()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(PhaseType.DYING);
            }
            if (this.phaseManager.getCurrent().a()) {
                this.bS += (int)(float4 - this.getHealth());
                if (this.bS > 0.25f * this.getHealthMaximum()) {
                    this.bS = 0;
                    this.phaseManager.setPhase(PhaseType.TAKEOFF);
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (source instanceof EntityDamageSource && ((EntityDamageSource)source).y()) {
            this.damagePart(this.partBody, source, amount);
        }
        return false;
    }
    
    protected boolean e(final DamageSource damageSource, final float float2) {
        return super.damage(damageSource, float2);
    }
    
    @Override
    public void kill() {
        this.remove();
        if (this.fight != null) {
            this.fight.updateFight(this);
            this.fight.dragonKilled(this);
        }
    }
    
    @Override
    protected void updatePostDeath() {
        if (this.fight != null) {
            this.fight.updateFight(this);
        }
        ++this.bL;
        if (this.bL >= 180 && this.bL <= 200) {
            final float float1 = (this.random.nextFloat() - 0.5f) * 8.0f;
            final float float2 = (this.random.nextFloat() - 0.5f) * 4.0f;
            final float float3 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.v, this.x + float1, this.y + 2.0 + float2, this.z + float3, 0.0, 0.0, 0.0);
        }
        final boolean boolean1 = this.world.getGameRules().getBoolean("doMobLoot");
        int integer2 = 500;
        if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
            integer2 = 12000;
        }
        if (!this.world.isClient) {
            if (this.bL > 150 && this.bL % 5 == 0 && boolean1) {
                this.a(MathHelper.floor(integer2 * 0.08f));
            }
            if (this.bL == 1) {
                this.world.playGlobalEvent(1028, new BlockPos(this), 0);
            }
        }
        this.move(MovementType.a, new Vec3d(0.0, 0.10000000149011612, 0.0));
        this.yaw += 20.0f;
        this.aK = this.yaw;
        if (this.bL == 200 && !this.world.isClient) {
            if (boolean1) {
                this.a(MathHelper.floor(integer2 * 0.2f));
            }
            if (this.fight != null) {
                this.fight.dragonKilled(this);
            }
            this.remove();
        }
    }
    
    private void a(int integer) {
        while (integer > 0) {
            final int integer2 = ExperienceOrbEntity.roundToOrbSize(integer);
            integer -= integer2;
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, integer2));
        }
    }
    
    public int l() {
        if (this.bT[0] == null) {
            for (int integer1 = 0; integer1 < 24; ++integer1) {
                int integer2 = 5;
                int integer3;
                int integer4;
                int integer5;
                if ((integer3 = integer1) < 12) {
                    integer4 = (int)(60.0f * MathHelper.cos(2.0f * (-3.1415927f + 0.2617994f * integer3)));
                    integer5 = (int)(60.0f * MathHelper.sin(2.0f * (-3.1415927f + 0.2617994f * integer3)));
                }
                else if (integer1 < 20) {
                    integer3 -= 12;
                    integer4 = (int)(40.0f * MathHelper.cos(2.0f * (-3.1415927f + 0.3926991f * integer3)));
                    integer5 = (int)(40.0f * MathHelper.sin(2.0f * (-3.1415927f + 0.3926991f * integer3)));
                    integer2 += 10;
                }
                else {
                    integer3 -= 20;
                    integer4 = (int)(20.0f * MathHelper.cos(2.0f * (-3.1415927f + 0.7853982f * integer3)));
                    integer5 = (int)(20.0f * MathHelper.sin(2.0f * (-3.1415927f + 0.7853982f * integer3)));
                }
                final int integer6 = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.f, new BlockPos(integer4, 0, integer5)).getY() + integer2);
                this.bT[integer1] = new PathNode(integer4, integer6, integer5);
            }
            this.bU[0] = 6146;
            this.bU[1] = 8197;
            this.bU[2] = 8202;
            this.bU[3] = 16404;
            this.bU[4] = 32808;
            this.bU[5] = 32848;
            this.bU[6] = 65696;
            this.bU[7] = 131392;
            this.bU[8] = 131712;
            this.bU[9] = 263424;
            this.bU[10] = 526848;
            this.bU[11] = 525313;
            this.bU[12] = 1581057;
            this.bU[13] = 3166214;
            this.bU[14] = 2138120;
            this.bU[15] = 6373424;
            this.bU[16] = 4358208;
            this.bU[17] = 12910976;
            this.bU[18] = 9044480;
            this.bU[19] = 9706496;
            this.bU[20] = 15216640;
            this.bU[21] = 13688832;
            this.bU[22] = 11763712;
            this.bU[23] = 8257536;
        }
        return this.k(this.x, this.y, this.z);
    }
    
    public int k(final double double1, final double double3, final double double5) {
        float float7 = 10000.0f;
        int integer8 = 0;
        final PathNode pathNode9 = new PathNode(MathHelper.floor(double1), MathHelper.floor(double3), MathHelper.floor(double5));
        int integer9 = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            integer9 = 12;
        }
        for (int integer10 = integer9; integer10 < 24; ++integer10) {
            if (this.bT[integer10] != null) {
                final float float8 = this.bT[integer10].distanceSquared(pathNode9);
                if (float8 < float7) {
                    float7 = float8;
                    integer8 = integer10;
                }
            }
        }
        return integer8;
    }
    
    @Nullable
    public Path a(final int integer1, final int integer2, @Nullable final PathNode pathNode) {
        for (int integer3 = 0; integer3 < 24; ++integer3) {
            final PathNode pathNode2 = this.bT[integer3];
            pathNode2.i = false;
            pathNode2.heapWeight = 0.0f;
            pathNode2.e = 0.0f;
            pathNode2.f = 0.0f;
            pathNode2.h = null;
            pathNode2.heapIndex = -1;
        }
        final PathNode pathNode3 = this.bT[integer1];
        PathNode pathNode2 = this.bT[integer2];
        pathNode3.e = 0.0f;
        pathNode3.f = pathNode3.distance(pathNode2);
        pathNode3.heapWeight = pathNode3.f;
        this.bV.clear();
        this.bV.push(pathNode3);
        PathNode pathNode4 = pathNode3;
        int integer4 = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            integer4 = 12;
        }
        while (!this.bV.isEmpty()) {
            final PathNode pathNode5 = this.bV.pop();
            if (pathNode5.equals(pathNode2)) {
                if (pathNode != null) {
                    pathNode.h = pathNode2;
                    pathNode2 = pathNode;
                }
                return this.a(pathNode3, pathNode2);
            }
            if (pathNode5.distance(pathNode2) < pathNode4.distance(pathNode2)) {
                pathNode4 = pathNode5;
            }
            pathNode5.i = true;
            int integer5 = 0;
            for (int integer6 = 0; integer6 < 24; ++integer6) {
                if (this.bT[integer6] == pathNode5) {
                    integer5 = integer6;
                    break;
                }
            }
            for (int integer6 = integer4; integer6 < 24; ++integer6) {
                if ((this.bU[integer5] & 1 << integer6) > 0) {
                    final PathNode pathNode6 = this.bT[integer6];
                    if (!pathNode6.i) {
                        final float float12 = pathNode5.e + pathNode5.distance(pathNode6);
                        if (!pathNode6.isInHeap() || float12 < pathNode6.e) {
                            pathNode6.h = pathNode5;
                            pathNode6.e = float12;
                            pathNode6.f = pathNode6.distance(pathNode2);
                            if (pathNode6.isInHeap()) {
                                this.bV.setNodeWeight(pathNode6, pathNode6.e + pathNode6.f);
                            }
                            else {
                                pathNode6.heapWeight = pathNode6.e + pathNode6.f;
                                this.bV.push(pathNode6);
                            }
                        }
                    }
                }
            }
        }
        if (pathNode4 == pathNode3) {
            return null;
        }
        EnderDragonEntity.LOGGER.debug("Failed to find path from {} to {}", integer1, integer2);
        if (pathNode != null) {
            pathNode.h = pathNode4;
            pathNode4 = pathNode;
        }
        return this.a(pathNode3, pathNode4);
    }
    
    private Path a(final PathNode pathNode1, final PathNode pathNode2) {
        final List<PathNode> list3 = Lists.newArrayList();
        PathNode pathNode3 = pathNode2;
        list3.add(0, pathNode3);
        while (pathNode3.h != null) {
            pathNode3 = pathNode3.h;
            list3.add(0, pathNode3);
        }
        return new Path(list3);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("DragonPhase")) {
            this.phaseManager.setPhase(PhaseType.getFromId(tag.getInt("DragonPhase")));
        }
    }
    
    @Override
    protected void checkDespawn() {
    }
    
    public EnderDragonPart[] dT() {
        return this.parts;
    }
    
    @Override
    public boolean collides() {
        return false;
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.cw;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.cB;
    }
    
    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public float a(final int integer, final double[] arr2, final double[] arr3) {
        final Phase phase4 = this.phaseManager.getCurrent();
        final PhaseType<? extends Phase> phaseType5 = phase4.getType();
        double double6;
        if (phaseType5 == PhaseType.LANDING || phaseType5 == PhaseType.TAKEOFF) {
            final BlockPos blockPos8 = this.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN);
            final float float9 = Math.max(MathHelper.sqrt(blockPos8.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            double6 = integer / float9;
        }
        else if (phase4.a()) {
            double6 = integer;
        }
        else if (integer == 6) {
            double6 = 0.0;
        }
        else {
            double6 = arr3[1] - arr2[1];
        }
        return (float)double6;
    }
    
    public Vec3d u(final float float1) {
        final Phase phase2 = this.phaseManager.getCurrent();
        final PhaseType<? extends Phase> phaseType3 = phase2.getType();
        Vec3d vec3d4;
        if (phaseType3 == PhaseType.LANDING || phaseType3 == PhaseType.TAKEOFF) {
            final BlockPos blockPos5 = this.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN);
            final float float2 = Math.max(MathHelper.sqrt(blockPos5.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            final float float3 = 6.0f / float2;
            final float float4 = this.pitch;
            final float float5 = 1.5f;
            this.pitch = -float3 * 1.5f * 5.0f;
            vec3d4 = this.getRotationVec(float1);
            this.pitch = float4;
        }
        else if (phase2.a()) {
            final float float6 = this.pitch;
            final float float2 = 1.5f;
            this.pitch = -45.0f;
            vec3d4 = this.getRotationVec(float1);
            this.pitch = float6;
        }
        else {
            vec3d4 = this.getRotationVec(float1);
        }
        return vec3d4;
    }
    
    public void crystalDestroyed(final EnderCrystalEntity crystal, final BlockPos pos, final DamageSource source) {
        PlayerEntity playerEntity4;
        if (source.getAttacker() instanceof PlayerEntity) {
            playerEntity4 = (PlayerEntity)source.getAttacker();
        }
        else {
            playerEntity4 = this.world.getClosestPlayer(EnderDragonEntity.CLOSE_PLAYER_PREDICATE, pos.getX(), pos.getY(), pos.getZ());
        }
        if (crystal == this.enderCrystal) {
            this.damagePart(this.partHead, DamageSource.explosion(playerEntity4), 10.0f);
        }
        this.phaseManager.getCurrent().crystalDestroyed(crystal, pos, source, playerEntity4);
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (EnderDragonEntity.PHASE_TYPE.equals(data) && this.world.isClient) {
            this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().<Integer>get(EnderDragonEntity.PHASE_TYPE)));
        }
        super.onTrackedDataSet(data);
    }
    
    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }
    
    @Nullable
    public EnderDragonFight getFight() {
        return this.fight;
    }
    
    @Override
    public boolean addPotionEffect(final StatusEffectInstance statusEffectInstance) {
        return false;
    }
    
    @Override
    protected boolean canStartRiding(final Entity entity) {
        return false;
    }
    
    @Override
    public boolean canUsePortals() {
        return false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PHASE_TYPE = DataTracker.<Integer>registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    }
}
