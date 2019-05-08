package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import java.util.Iterator;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import com.google.common.collect.Maps;
import net.minecraft.util.registry.Registry;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import java.util.Map;

public class StatusEffect
{
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final StatusEffectType type;
    private final int color;
    @Nullable
    private String translationKey;
    
    @Nullable
    public static StatusEffect byRawId(final int rawId) {
        return Registry.STATUS_EFFECT.get(rawId);
    }
    
    public static int getRawId(final StatusEffect type) {
        return Registry.STATUS_EFFECT.getRawId(type);
    }
    
    protected StatusEffect(final StatusEffectType statusEffectType, final int integer) {
        this.attributeModifiers = Maps.newHashMap();
        this.type = statusEffectType;
        this.color = integer;
    }
    
    public void applyUpdateEffect(final LivingEntity entity, final int integer) {
        if (this == StatusEffects.j) {
            if (entity.getHealth() < entity.getHealthMaximum()) {
                entity.heal(1.0f);
            }
        }
        else if (this == StatusEffects.s) {
            if (entity.getHealth() > 1.0f) {
                entity.damage(DamageSource.MAGIC, 1.0f);
            }
        }
        else if (this == StatusEffects.t) {
            entity.damage(DamageSource.WITHER, 1.0f);
        }
        else if (this == StatusEffects.q && entity instanceof PlayerEntity) {
            ((PlayerEntity)entity).addExhaustion(0.005f * (integer + 1));
        }
        else if (this == StatusEffects.w && entity instanceof PlayerEntity) {
            if (!entity.world.isClient) {
                ((PlayerEntity)entity).getHungerManager().add(integer + 1, 1.0f);
            }
        }
        else if ((this == StatusEffects.f && !entity.isUndead()) || (this == StatusEffects.g && entity.isUndead())) {
            entity.heal((float)Math.max(4 << integer, 0));
        }
        else if ((this == StatusEffects.g && !entity.isUndead()) || (this == StatusEffects.f && entity.isUndead())) {
            entity.damage(DamageSource.MAGIC, (float)(6 << integer));
        }
    }
    
    public void applyInstantEffect(@Nullable final Entity source, @Nullable final Entity attacker, final LivingEntity target, final int amplifier, final double double5) {
        if ((this == StatusEffects.f && !target.isUndead()) || (this == StatusEffects.g && target.isUndead())) {
            final int integer7 = (int)(double5 * (4 << amplifier) + 0.5);
            target.heal((float)integer7);
        }
        else if ((this == StatusEffects.g && !target.isUndead()) || (this == StatusEffects.f && target.isUndead())) {
            final int integer7 = (int)(double5 * (6 << amplifier) + 0.5);
            if (source == null) {
                target.damage(DamageSource.MAGIC, (float)integer7);
            }
            else {
                target.damage(DamageSource.magic(source, attacker), (float)integer7);
            }
        }
        else {
            this.applyUpdateEffect(target, amplifier);
        }
    }
    
    public boolean canApplyUpdateEffect(final int duration, final int integer2) {
        if (this == StatusEffects.j) {
            final int integer3 = 50 >> integer2;
            return integer3 <= 0 || duration % integer3 == 0;
        }
        if (this == StatusEffects.s) {
            final int integer3 = 25 >> integer2;
            return integer3 <= 0 || duration % integer3 == 0;
        }
        if (this == StatusEffects.t) {
            final int integer3 = 40 >> integer2;
            return integer3 <= 0 || duration % integer3 == 0;
        }
        return this == StatusEffects.q;
    }
    
    public boolean isInstant() {
        return false;
    }
    
    protected String b() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("effect", Registry.STATUS_EFFECT.getId(this));
        }
        return this.translationKey;
    }
    
    public String getTranslationKey() {
        return this.b();
    }
    
    public TextComponent d() {
        return new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
    }
    
    @Environment(EnvType.CLIENT)
    public StatusEffectType getType() {
        return this.type;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public StatusEffect addAttributeModifier(final EntityAttribute attribute, final String uuid, final double amount, final EntityAttributeModifier.Operation operation) {
        final EntityAttributeModifier entityAttributeModifier6 = new EntityAttributeModifier(UUID.fromString(uuid), this::getTranslationKey, amount, operation);
        this.attributeModifiers.put(attribute, entityAttributeModifier6);
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }
    
    public void a(final LivingEntity livingEntity, final AbstractEntityAttributeContainer abstractEntityAttributeContainer, final int integer) {
        for (final Map.Entry<EntityAttribute, EntityAttributeModifier> entry5 : this.attributeModifiers.entrySet()) {
            final EntityAttributeInstance entityAttributeInstance6 = abstractEntityAttributeContainer.get(entry5.getKey());
            if (entityAttributeInstance6 != null) {
                entityAttributeInstance6.removeModifier(entry5.getValue());
            }
        }
    }
    
    public void b(final LivingEntity livingEntity, final AbstractEntityAttributeContainer abstractEntityAttributeContainer, final int integer) {
        for (final Map.Entry<EntityAttribute, EntityAttributeModifier> entry5 : this.attributeModifiers.entrySet()) {
            final EntityAttributeInstance entityAttributeInstance6 = abstractEntityAttributeContainer.get(entry5.getKey());
            if (entityAttributeInstance6 != null) {
                final EntityAttributeModifier entityAttributeModifier7 = entry5.getValue();
                entityAttributeInstance6.removeModifier(entityAttributeModifier7);
                entityAttributeInstance6.addModifier(new EntityAttributeModifier(entityAttributeModifier7.getId(), this.getTranslationKey() + " " + integer, this.a(integer, entityAttributeModifier7), entityAttributeModifier7.getOperation()));
            }
        }
    }
    
    public double a(final int integer, final EntityAttributeModifier entityAttributeModifier) {
        return entityAttributeModifier.getAmount() * (integer + 1);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean h() {
        return this.type == StatusEffectType.a;
    }
}
