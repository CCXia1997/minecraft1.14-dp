package net.minecraft.predicate.entity;

import net.minecraft.util.NumberRange;
import java.util.Collections;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import java.util.Iterator;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import com.google.common.collect.Maps;
import net.minecraft.entity.effect.StatusEffect;
import java.util.Map;

public class EntityEffectPredicate
{
    public static final EntityEffectPredicate EMPTY;
    private final Map<StatusEffect, EffectData> effects;
    
    public EntityEffectPredicate(final Map<StatusEffect, EffectData> map) {
        this.effects = map;
    }
    
    public static EntityEffectPredicate create() {
        return new EntityEffectPredicate(Maps.newHashMap());
    }
    
    public EntityEffectPredicate withEffect(final StatusEffect statusEffect) {
        this.effects.put(statusEffect, new EffectData());
        return this;
    }
    
    public boolean test(final Entity entity) {
        return this == EntityEffectPredicate.EMPTY || (entity instanceof LivingEntity && this.test(((LivingEntity)entity).getActiveStatusEffects()));
    }
    
    public boolean test(final LivingEntity livingEntity) {
        return this == EntityEffectPredicate.EMPTY || this.test(livingEntity.getActiveStatusEffects());
    }
    
    public boolean test(final Map<StatusEffect, StatusEffectInstance> map) {
        if (this == EntityEffectPredicate.EMPTY) {
            return true;
        }
        for (final Map.Entry<StatusEffect, EffectData> entry3 : this.effects.entrySet()) {
            final StatusEffectInstance statusEffectInstance4 = map.get(entry3.getKey());
            if (!entry3.getValue().test(statusEffectInstance4)) {
                return false;
            }
        }
        return true;
    }
    
    public static EntityEffectPredicate deserialize(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityEffectPredicate.EMPTY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "effects");
        final Map<StatusEffect, EffectData> map3 = Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry5 : jsonObject2.entrySet()) {
            final Identifier identifier6 = new Identifier(entry5.getKey());
            final Object o;
            final Object o2;
            final StatusEffect statusEffect7 = Registry.STATUS_EFFECT.getOrEmpty(identifier6).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown effect '" + o2 + "'");
                return o;
            });
            final EffectData effectData8 = EffectData.deserialize(JsonHelper.asObject(entry5.getValue(), entry5.getKey()));
            map3.put(statusEffect7, effectData8);
        }
        return new EntityEffectPredicate(map3);
    }
    
    public JsonElement serialize() {
        if (this == EntityEffectPredicate.EMPTY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        for (final Map.Entry<StatusEffect, EffectData> entry3 : this.effects.entrySet()) {
            jsonObject1.add(Registry.STATUS_EFFECT.getId(entry3.getKey()).toString(), entry3.getValue().serialize());
        }
        return jsonObject1;
    }
    
    static {
        EMPTY = new EntityEffectPredicate(Collections.<StatusEffect, EffectData>emptyMap());
    }
    
    public static class EffectData
    {
        private final NumberRange.IntRange amplifier;
        private final NumberRange.IntRange duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;
        
        public EffectData(final NumberRange.IntRange amplifier, final NumberRange.IntRange duration, @Nullable final Boolean ambient, @Nullable final Boolean boolean4) {
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.visible = boolean4;
        }
        
        public EffectData() {
            this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, null, null);
        }
        
        public boolean test(@Nullable final StatusEffectInstance statusEffectInstance) {
            return statusEffectInstance != null && this.amplifier.test(statusEffectInstance.getAmplifier()) && this.duration.test(statusEffectInstance.getDuration()) && (this.ambient == null || this.ambient == statusEffectInstance.isAmbient()) && (this.visible == null || this.visible == statusEffectInstance.shouldShowParticles());
        }
        
        public JsonElement serialize() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("amplifier", this.amplifier.serialize());
            jsonObject1.add("duration", this.duration.serialize());
            jsonObject1.addProperty("ambient", this.ambient);
            jsonObject1.addProperty("visible", this.visible);
            return jsonObject1;
        }
        
        public static EffectData deserialize(final JsonObject json) {
            final NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(json.get("amplifier"));
            final NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(json.get("duration"));
            final Boolean boolean4 = json.has("ambient") ? Boolean.valueOf(JsonHelper.getBoolean(json, "ambient")) : null;
            final Boolean boolean5 = json.has("visible") ? Boolean.valueOf(JsonHelper.getBoolean(json, "visible")) : null;
            return new EffectData(intRange2, intRange3, boolean4, boolean5);
        }
    }
}
