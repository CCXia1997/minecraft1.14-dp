package net.minecraft.entity;

import java.util.AbstractList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.ListTag;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ParticleArgumentType;
import com.mojang.brigadier.StringReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import java.util.Collection;
import net.minecraft.potion.PotionUtil;
import net.minecraft.particle.ParticleTypes;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;
import java.util.UUID;
import java.util.Map;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import net.minecraft.potion.Potion;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.entity.data.TrackedData;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity extends Entity
{
    private static final Logger LOGGER;
    private static final TrackedData<Float> RADIUS;
    private static final TrackedData<Integer> COLOR;
    private static final TrackedData<Boolean> WAITING;
    private static final TrackedData<ParticleParameters> PARTICLE_ID;
    private Potion potion;
    private final List<StatusEffectInstance> effects;
    private final Map<Entity, Integer> affectedEntities;
    private int duration;
    private int waitTime;
    private int reapplicationDelay;
    private boolean customColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusGrowth;
    private LivingEntity owner;
    private UUID ownerUuid;
    
    public AreaEffectCloudEntity(final EntityType<? extends AreaEffectCloudEntity> type, final World world) {
        super(type, world);
        this.potion = Potions.a;
        this.effects = Lists.newArrayList();
        this.affectedEntities = Maps.newHashMap();
        this.duration = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noClip = true;
        this.setRadius(3.0f);
    }
    
    public AreaEffectCloudEntity(final World world, final double x, final double double4, final double double6) {
        this(EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, double4, double6);
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<Integer>startTracking(AreaEffectCloudEntity.COLOR, 0);
        this.getDataTracker().<Float>startTracking(AreaEffectCloudEntity.RADIUS, 0.5f);
        this.getDataTracker().<Boolean>startTracking(AreaEffectCloudEntity.WAITING, false);
        this.getDataTracker().<ParticleParameters>startTracking(AreaEffectCloudEntity.PARTICLE_ID, ParticleTypes.u);
    }
    
    public void setRadius(final float float1) {
        if (!this.world.isClient) {
            this.getDataTracker().<Float>set(AreaEffectCloudEntity.RADIUS, float1);
        }
    }
    
    @Override
    public void refreshSize() {
        final double double1 = this.x;
        final double double2 = this.y;
        final double double3 = this.z;
        super.refreshSize();
        this.setPosition(double1, double2, double3);
    }
    
    public float getRadius() {
        return this.getDataTracker().<Float>get(AreaEffectCloudEntity.RADIUS);
    }
    
    public void setPotion(final Potion potion) {
        this.potion = potion;
        if (!this.customColor) {
            this.updateColor();
        }
    }
    
    private void updateColor() {
        if (this.potion == Potions.a && this.effects.isEmpty()) {
            this.getDataTracker().<Integer>set(AreaEffectCloudEntity.COLOR, 0);
        }
        else {
            this.getDataTracker().<Integer>set(AreaEffectCloudEntity.COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }
    
    public void addEffect(final StatusEffectInstance statusEffectInstance) {
        this.effects.add(statusEffectInstance);
        if (!this.customColor) {
            this.updateColor();
        }
    }
    
    public int getColor() {
        return this.getDataTracker().<Integer>get(AreaEffectCloudEntity.COLOR);
    }
    
    public void setColor(final int integer) {
        this.customColor = true;
        this.getDataTracker().<Integer>set(AreaEffectCloudEntity.COLOR, integer);
    }
    
    public ParticleParameters getParticleType() {
        return this.getDataTracker().<ParticleParameters>get(AreaEffectCloudEntity.PARTICLE_ID);
    }
    
    public void setParticleType(final ParticleParameters particleParameters) {
        this.getDataTracker().<ParticleParameters>set(AreaEffectCloudEntity.PARTICLE_ID, particleParameters);
    }
    
    protected void setWaiting(final boolean boolean1) {
        this.getDataTracker().<Boolean>set(AreaEffectCloudEntity.WAITING, boolean1);
    }
    
    public boolean isWaiting() {
        return this.getDataTracker().<Boolean>get(AreaEffectCloudEntity.WAITING);
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public void setDuration(final int integer) {
        this.duration = integer;
    }
    
    @Override
    public void tick() {
        super.tick();
        final boolean boolean1 = this.isWaiting();
        float float2 = this.getRadius();
        if (this.world.isClient) {
            final ParticleParameters particleParameters3 = this.getParticleType();
            if (boolean1) {
                if (this.random.nextBoolean()) {
                    for (int integer4 = 0; integer4 < 2; ++integer4) {
                        final float float3 = this.random.nextFloat() * 6.2831855f;
                        final float float4 = MathHelper.sqrt(this.random.nextFloat()) * 0.2f;
                        final float float5 = MathHelper.cos(float3) * float4;
                        final float float6 = MathHelper.sin(float3) * float4;
                        if (particleParameters3.getType() == ParticleTypes.u) {
                            final int integer5 = this.random.nextBoolean() ? 16777215 : this.getColor();
                            final int integer6 = integer5 >> 16 & 0xFF;
                            final int integer7 = integer5 >> 8 & 0xFF;
                            final int integer8 = integer5 & 0xFF;
                            this.world.addImportantParticle(particleParameters3, this.x + float5, this.y, this.z + float6, integer6 / 255.0f, integer7 / 255.0f, integer8 / 255.0f);
                        }
                        else {
                            this.world.addImportantParticle(particleParameters3, this.x + float5, this.y, this.z + float6, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            else {
                final float float7 = 3.1415927f * float2 * float2;
                for (int integer9 = 0; integer9 < float7; ++integer9) {
                    final float float4 = this.random.nextFloat() * 6.2831855f;
                    final float float5 = MathHelper.sqrt(this.random.nextFloat()) * float2;
                    final float float6 = MathHelper.cos(float4) * float5;
                    final float float8 = MathHelper.sin(float4) * float5;
                    if (particleParameters3.getType() == ParticleTypes.u) {
                        final int integer6 = this.getColor();
                        final int integer7 = integer6 >> 16 & 0xFF;
                        final int integer8 = integer6 >> 8 & 0xFF;
                        final int integer10 = integer6 & 0xFF;
                        this.world.addImportantParticle(particleParameters3, this.x + float6, this.y, this.z + float8, integer7 / 255.0f, integer8 / 255.0f, integer10 / 255.0f);
                    }
                    else {
                        this.world.addImportantParticle(particleParameters3, this.x + float6, this.y, this.z + float8, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
                    }
                }
            }
        }
        else {
            if (this.age >= this.waitTime + this.duration) {
                this.remove();
                return;
            }
            final boolean boolean2 = this.age < this.waitTime;
            if (boolean1 != boolean2) {
                this.setWaiting(boolean2);
            }
            if (boolean2) {
                return;
            }
            if (this.radiusGrowth != 0.0f) {
                float2 += this.radiusGrowth;
                if (float2 < 0.5f) {
                    this.remove();
                    return;
                }
                this.setRadius(float2);
            }
            if (this.age % 5 == 0) {
                final Iterator<Map.Entry<Entity, Integer>> iterator4 = this.affectedEntities.entrySet().iterator();
                while (iterator4.hasNext()) {
                    final Map.Entry<Entity, Integer> entry5 = iterator4.next();
                    if (this.age >= entry5.getValue()) {
                        iterator4.remove();
                    }
                }
                final List<StatusEffectInstance> list4 = Lists.newArrayList();
                for (final StatusEffectInstance statusEffectInstance6 : this.potion.getEffects()) {
                    list4.add(new StatusEffectInstance(statusEffectInstance6.getEffectType(), statusEffectInstance6.getDuration() / 4, statusEffectInstance6.getAmplifier(), statusEffectInstance6.isAmbient(), statusEffectInstance6.shouldShowParticles()));
                }
                list4.addAll(this.effects);
                if (list4.isEmpty()) {
                    this.affectedEntities.clear();
                }
                else {
                    final List<LivingEntity> list5 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getBoundingBox());
                    if (!list5.isEmpty()) {
                        for (final LivingEntity livingEntity7 : list5) {
                            if (!this.affectedEntities.containsKey(livingEntity7)) {
                                if (!livingEntity7.dt()) {
                                    continue;
                                }
                                final double double8 = livingEntity7.x - this.x;
                                final double double9 = livingEntity7.z - this.z;
                                final double double10 = double8 * double8 + double9 * double9;
                                if (double10 > float2 * float2) {
                                    continue;
                                }
                                this.affectedEntities.put(livingEntity7, this.age + this.reapplicationDelay);
                                for (final StatusEffectInstance statusEffectInstance7 : list4) {
                                    if (statusEffectInstance7.getEffectType().isInstant()) {
                                        statusEffectInstance7.getEffectType().applyInstantEffect(this, this.getOwner(), livingEntity7, statusEffectInstance7.getAmplifier(), 0.5);
                                    }
                                    else {
                                        livingEntity7.addPotionEffect(new StatusEffectInstance(statusEffectInstance7));
                                    }
                                }
                                if (this.radiusOnUse != 0.0f) {
                                    float2 += this.radiusOnUse;
                                    if (float2 < 0.5f) {
                                        this.remove();
                                        return;
                                    }
                                    this.setRadius(float2);
                                }
                                if (this.durationOnUse == 0) {
                                    continue;
                                }
                                this.duration += this.durationOnUse;
                                if (this.duration <= 0) {
                                    this.remove();
                                    return;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void setRadiusStart(final float float1) {
        this.radiusOnUse = float1;
    }
    
    public void setRadiusGrowth(final float float1) {
        this.radiusGrowth = float1;
    }
    
    public void setWaitTime(final int integer) {
        this.waitTime = integer;
    }
    
    public void setOwner(@Nullable final LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUuid = ((livingEntity == null) ? null : livingEntity.getUuid());
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
            final Entity entity1 = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            if (entity1 instanceof LivingEntity) {
                this.owner = (LivingEntity)entity1;
            }
        }
        return this.owner;
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        this.age = tag.getInt("Age");
        this.duration = tag.getInt("Duration");
        this.waitTime = tag.getInt("WaitTime");
        this.reapplicationDelay = tag.getInt("ReapplicationDelay");
        this.durationOnUse = tag.getInt("DurationOnUse");
        this.radiusOnUse = tag.getFloat("RadiusOnUse");
        this.radiusGrowth = tag.getFloat("RadiusPerTick");
        this.setRadius(tag.getFloat("Radius"));
        this.ownerUuid = tag.getUuid("OwnerUUID");
        if (tag.containsKey("Particle", 8)) {
            try {
                this.setParticleType(ParticleArgumentType.readParameters(new StringReader(tag.getString("Particle"))));
            }
            catch (CommandSyntaxException commandSyntaxException2) {
                AreaEffectCloudEntity.LOGGER.warn("Couldn't load custom particle {}", tag.getString("Particle"), commandSyntaxException2);
            }
        }
        if (tag.containsKey("Color", 99)) {
            this.setColor(tag.getInt("Color"));
        }
        if (tag.containsKey("Potion", 8)) {
            this.setPotion(PotionUtil.getPotion(tag));
        }
        if (tag.containsKey("Effects", 9)) {
            final ListTag listTag2 = tag.getList("Effects", 10);
            this.effects.clear();
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                final StatusEffectInstance statusEffectInstance4 = StatusEffectInstance.deserialize(listTag2.getCompoundTag(integer3));
                if (statusEffectInstance4 != null) {
                    this.addEffect(statusEffectInstance4);
                }
            }
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putInt("Duration", this.duration);
        tag.putInt("WaitTime", this.waitTime);
        tag.putInt("ReapplicationDelay", this.reapplicationDelay);
        tag.putInt("DurationOnUse", this.durationOnUse);
        tag.putFloat("RadiusOnUse", this.radiusOnUse);
        tag.putFloat("RadiusPerTick", this.radiusGrowth);
        tag.putFloat("Radius", this.getRadius());
        tag.putString("Particle", this.getParticleType().asString());
        if (this.ownerUuid != null) {
            tag.putUuid("OwnerUUID", this.ownerUuid);
        }
        if (this.customColor) {
            tag.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.a && this.potion != null) {
            tag.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            final ListTag listTag2 = new ListTag();
            for (final StatusEffectInstance statusEffectInstance4 : this.effects) {
                ((AbstractList<CompoundTag>)listTag2).add(statusEffectInstance4.serialize(new CompoundTag()));
            }
            tag.put("Effects", listTag2);
        }
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (AreaEffectCloudEntity.RADIUS.equals(data)) {
            this.refreshSize();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.d;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        return EntitySize.resizeable(this.getRadius() * 2.0f, 0.5f);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        RADIUS = DataTracker.<Float>registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
        COLOR = DataTracker.<Integer>registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
        WAITING = DataTracker.<Boolean>registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PARTICLE_ID = DataTracker.<ParticleParameters>registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    }
}
