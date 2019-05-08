package net.minecraft.entity.effect;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.ComparisonChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance>
{
    private static final Logger LOGGER;
    private final StatusEffect type;
    private int duration;
    private int amplifier;
    private boolean splash;
    private boolean ambient;
    @Environment(EnvType.CLIENT)
    private boolean permanent;
    private boolean showParticles;
    private boolean showIcon;
    
    public StatusEffectInstance(final StatusEffect statusEffect) {
        this(statusEffect, 0, 0);
    }
    
    public StatusEffectInstance(final StatusEffect type, final int integer) {
        this(type, integer, 0);
    }
    
    public StatusEffectInstance(final StatusEffect type, final int duration, final int integer3) {
        this(type, duration, integer3, false, true);
    }
    
    public StatusEffectInstance(final StatusEffect effect, final int duration, final int amplifier, final boolean boolean4, final boolean showParticles) {
        this(effect, duration, amplifier, boolean4, showParticles, showParticles);
    }
    
    public StatusEffectInstance(final StatusEffect type, final int duration, final int amplifier, final boolean ambient, final boolean showParticles, final boolean showIcon) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
    }
    
    public StatusEffectInstance(final StatusEffectInstance statusEffectInstance) {
        this.type = statusEffectInstance.type;
        this.duration = statusEffectInstance.duration;
        this.amplifier = statusEffectInstance.amplifier;
        this.ambient = statusEffectInstance.ambient;
        this.showParticles = statusEffectInstance.showParticles;
        this.showIcon = statusEffectInstance.showIcon;
    }
    
    public boolean upgrade(final StatusEffectInstance statusEffectInstance) {
        if (this.type != statusEffectInstance.type) {
            StatusEffectInstance.LOGGER.warn("This method should only be called for matching effects!");
        }
        boolean boolean2 = false;
        if (statusEffectInstance.amplifier > this.amplifier) {
            this.amplifier = statusEffectInstance.amplifier;
            this.duration = statusEffectInstance.duration;
            boolean2 = true;
        }
        else if (statusEffectInstance.amplifier == this.amplifier && this.duration < statusEffectInstance.duration) {
            this.duration = statusEffectInstance.duration;
            boolean2 = true;
        }
        if ((!statusEffectInstance.ambient && this.ambient) || boolean2) {
            this.ambient = statusEffectInstance.ambient;
            boolean2 = true;
        }
        if (statusEffectInstance.showParticles != this.showParticles) {
            this.showParticles = statusEffectInstance.showParticles;
            boolean2 = true;
        }
        if (statusEffectInstance.showIcon != this.showIcon) {
            this.showIcon = statusEffectInstance.showIcon;
            boolean2 = true;
        }
        return boolean2;
    }
    
    public StatusEffect getEffectType() {
        return this.type;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public int getAmplifier() {
        return this.amplifier;
    }
    
    public boolean isAmbient() {
        return this.ambient;
    }
    
    public boolean shouldShowParticles() {
        return this.showParticles;
    }
    
    public boolean shouldShowIcon() {
        return this.showIcon;
    }
    
    public boolean update(final LivingEntity livingEntity) {
        if (this.duration > 0) {
            if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
                this.applyUpdateEffect(livingEntity);
            }
            this.updateDuration();
        }
        return this.duration > 0;
    }
    
    private int updateDuration() {
        return --this.duration;
    }
    
    public void applyUpdateEffect(final LivingEntity livingEntity) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(livingEntity, this.amplifier);
        }
    }
    
    public String getTranslationKey() {
        return this.type.getTranslationKey();
    }
    
    @Override
    public String toString() {
        String string1;
        if (this.amplifier > 0) {
            string1 = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        }
        else {
            string1 = this.getTranslationKey() + ", Duration: " + this.duration;
        }
        if (this.splash) {
            string1 += ", Splash: true";
        }
        if (!this.showParticles) {
            string1 += ", Particles: false";
        }
        if (!this.showIcon) {
            string1 += ", Show Icon: false";
        }
        return string1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof StatusEffectInstance) {
            final StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)o;
            return this.duration == statusEffectInstance2.duration && this.amplifier == statusEffectInstance2.amplifier && this.splash == statusEffectInstance2.splash && this.ambient == statusEffectInstance2.ambient && this.type.equals(statusEffectInstance2.type);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.type.hashCode();
        integer1 = 31 * integer1 + this.duration;
        integer1 = 31 * integer1 + this.amplifier;
        integer1 = 31 * integer1 + (this.splash ? 1 : 0);
        integer1 = 31 * integer1 + (this.ambient ? 1 : 0);
        return integer1;
    }
    
    public CompoundTag serialize(final CompoundTag compoundTag) {
        compoundTag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        compoundTag.putByte("Amplifier", (byte)this.getAmplifier());
        compoundTag.putInt("Duration", this.getDuration());
        compoundTag.putBoolean("Ambient", this.isAmbient());
        compoundTag.putBoolean("ShowParticles", this.shouldShowParticles());
        compoundTag.putBoolean("ShowIcon", this.shouldShowIcon());
        return compoundTag;
    }
    
    public static StatusEffectInstance deserialize(final CompoundTag tag) {
        final int integer2 = tag.getByte("Id");
        final StatusEffect statusEffect3 = StatusEffect.byRawId(integer2);
        if (statusEffect3 == null) {
            return null;
        }
        final int integer3 = tag.getByte("Amplifier");
        final int integer4 = tag.getInt("Duration");
        final boolean boolean6 = tag.getBoolean("Ambient");
        boolean boolean7 = true;
        if (tag.containsKey("ShowParticles", 1)) {
            boolean7 = tag.getBoolean("ShowParticles");
        }
        boolean boolean8 = boolean7;
        if (tag.containsKey("ShowIcon", 1)) {
            boolean8 = tag.getBoolean("ShowIcon");
        }
        return new StatusEffectInstance(statusEffect3, integer4, (integer3 < 0) ? 0 : integer3, boolean6, boolean7, boolean8);
    }
    
    @Environment(EnvType.CLIENT)
    public void setPermanent(final boolean boolean1) {
        this.permanent = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isPermanent() {
        return this.permanent;
    }
    
    public int b(final StatusEffectInstance statusEffectInstance) {
        final int integer2 = 32147;
        if ((this.getDuration() > 32147 && statusEffectInstance.getDuration() > 32147) || (this.isAmbient() && statusEffectInstance.isAmbient())) {
            return ComparisonChain.start().compare(this.isAmbient(), Boolean.valueOf(statusEffectInstance.isAmbient())).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result();
        }
        return ComparisonChain.start().compare(this.isAmbient(), Boolean.valueOf(statusEffectInstance.isAmbient())).compare(this.getDuration(), statusEffectInstance.getDuration()).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
