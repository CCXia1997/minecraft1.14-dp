package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.sound.SoundEvent;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public abstract class SpellcastingIllagerEntity extends IllagerEntity
{
    private static final TrackedData<Byte> SPELL;
    protected int spellTicks;
    private a spell;
    
    protected SpellcastingIllagerEntity(final EntityType<? extends SpellcastingIllagerEntity> type, final World world) {
        super(type, world);
        this.spell = a.a;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(SpellcastingIllagerEntity.SPELL, (Byte)0);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.spellTicks = tag.getInt("SpellTicks");
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("SpellTicks", this.spellTicks);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public State getState() {
        if (this.isSpellcasting()) {
            return State.c;
        }
        if (this.isCelebrating()) {
            return State.g;
        }
        return State.a;
    }
    
    public boolean isSpellcasting() {
        if (this.world.isClient) {
            return this.dataTracker.<Byte>get(SpellcastingIllagerEntity.SPELL) > 0;
        }
        return this.spellTicks > 0;
    }
    
    public void setSpell(final a spell) {
        this.spell = spell;
        this.dataTracker.<Byte>set(SpellcastingIllagerEntity.SPELL, (byte)spell.g);
    }
    
    protected a getSpell() {
        if (!this.world.isClient) {
            return this.spell;
        }
        return a.a(this.dataTracker.<Byte>get(SpellcastingIllagerEntity.SPELL));
    }
    
    @Override
    protected void mobTick() {
        super.mobTick();
        if (this.spellTicks > 0) {
            --this.spellTicks;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient && this.isSpellcasting()) {
            final a a1 = this.getSpell();
            final double double2 = a1.h[0];
            final double double3 = a1.h[1];
            final double double4 = a1.h[2];
            final float float8 = this.aK * 0.017453292f + MathHelper.cos(this.age * 0.6662f) * 0.25f;
            final float float9 = MathHelper.cos(float8);
            final float float10 = MathHelper.sin(float8);
            this.world.addParticle(ParticleTypes.u, this.x + float9 * 0.6, this.y + 1.8, this.z + float10 * 0.6, double2, double3, double4);
            this.world.addParticle(ParticleTypes.u, this.x - float9 * 0.6, this.y + 1.8, this.z - float10 * 0.6, double2, double3, double4);
        }
    }
    
    protected int getSpellTicks() {
        return this.spellTicks;
    }
    
    protected abstract SoundEvent getCastSpellSound();
    
    static {
        SPELL = DataTracker.<Byte>registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    public class LookAtTargetGoal extends Goal
    {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
        }
        
        @Override
        public void start() {
            super.start();
            SpellcastingIllagerEntity.this.navigation.stop();
        }
        
        @Override
        public void stop() {
            super.stop();
            SpellcastingIllagerEntity.this.setSpell(a.a);
        }
        
        @Override
        public void tick() {
            if (SpellcastingIllagerEntity.this.getTarget() != null) {
                SpellcastingIllagerEntity.this.getLookControl().lookAt(SpellcastingIllagerEntity.this.getTarget(), (float)SpellcastingIllagerEntity.this.dA(), (float)SpellcastingIllagerEntity.this.getLookPitchSpeed());
            }
        }
    }
    
    public abstract class CastSpellGoal extends Goal
    {
        protected int spellCooldown;
        protected int startTime;
        
        protected CastSpellGoal() {
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = SpellcastingIllagerEntity.this.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && !SpellcastingIllagerEntity.this.isSpellcasting() && SpellcastingIllagerEntity.this.age >= this.startTime;
        }
        
        @Override
        public boolean shouldContinue() {
            final LivingEntity livingEntity1 = SpellcastingIllagerEntity.this.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && this.spellCooldown > 0;
        }
        
        @Override
        public void start() {
            this.spellCooldown = this.getInitialCooldown();
            SpellcastingIllagerEntity.this.spellTicks = this.getSpellTicks();
            this.startTime = SpellcastingIllagerEntity.this.age + this.startTimeDelay();
            final SoundEvent soundEvent1 = this.getSoundPrepare();
            if (soundEvent1 != null) {
                SpellcastingIllagerEntity.this.playSound(soundEvent1, 1.0f, 1.0f);
            }
            SpellcastingIllagerEntity.this.setSpell(this.l());
        }
        
        @Override
        public void tick() {
            --this.spellCooldown;
            if (this.spellCooldown == 0) {
                this.castSpell();
                SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastSpellSound(), 1.0f, 1.0f);
            }
        }
        
        protected abstract void castSpell();
        
        protected int getInitialCooldown() {
            return 20;
        }
        
        protected abstract int getSpellTicks();
        
        protected abstract int startTimeDelay();
        
        @Nullable
        protected abstract SoundEvent getSoundPrepare();
        
        protected abstract a l();
    }
    
    public enum a
    {
        a(0, 0.0, 0.0, 0.0), 
        b(1, 0.7, 0.7, 0.8), 
        c(2, 0.4, 0.3, 0.35), 
        d(3, 0.7, 0.5, 0.2), 
        e(4, 0.3, 0.3, 0.8), 
        f(5, 0.1, 0.1, 0.2);
        
        private final int g;
        private final double[] h;
        
        private a(final int integer1, final double double2, final double double4, final double double6) {
            this.g = integer1;
            this.h = new double[] { double2, double4, double6 };
        }
        
        public static a a(final int integer) {
            for (final a a5 : values()) {
                if (integer == a5.g) {
                    return a5;
                }
            }
            return a.a;
        }
    }
}
