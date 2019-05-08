package net.minecraft.predicate;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.NumberRange;

public class DamagePredicate
{
    public static final DamagePredicate ANY;
    private final NumberRange.FloatRange dealt;
    private final NumberRange.FloatRange taken;
    private final EntityPredicate sourceEntity;
    private final Boolean blocked;
    private final DamageSourcePredicate type;
    
    public DamagePredicate() {
        this.dealt = NumberRange.FloatRange.ANY;
        this.taken = NumberRange.FloatRange.ANY;
        this.sourceEntity = EntityPredicate.ANY;
        this.blocked = null;
        this.type = DamageSourcePredicate.EMPTY;
    }
    
    public DamagePredicate(final NumberRange.FloatRange dealt, final NumberRange.FloatRange taken, final EntityPredicate sourceEntity, @Nullable final Boolean blocked, final DamageSourcePredicate damageSourcePredicate) {
        this.dealt = dealt;
        this.taken = taken;
        this.sourceEntity = sourceEntity;
        this.blocked = blocked;
        this.type = damageSourcePredicate;
    }
    
    public boolean test(final ServerPlayerEntity serverPlayerEntity, final DamageSource damageSource, final float float3, final float float4, final boolean boolean5) {
        return this == DamagePredicate.ANY || (this.dealt.matches(float3) && this.taken.matches(float4) && this.sourceEntity.test(serverPlayerEntity, damageSource.getAttacker()) && (this.blocked == null || this.blocked == boolean5) && this.type.test(serverPlayerEntity, damageSource));
    }
    
    public static DamagePredicate deserialize(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return DamagePredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "damage");
        final NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject2.get("dealt"));
        final NumberRange.FloatRange floatRange4 = NumberRange.FloatRange.fromJson(jsonObject2.get("taken"));
        final Boolean boolean5 = jsonObject2.has("blocked") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject2, "blocked")) : null;
        final EntityPredicate entityPredicate6 = EntityPredicate.deserialize(jsonObject2.get("source_entity"));
        final DamageSourcePredicate damageSourcePredicate7 = DamageSourcePredicate.deserialize(jsonObject2.get("type"));
        return new DamagePredicate(floatRange3, floatRange4, entityPredicate6, boolean5, damageSourcePredicate7);
    }
    
    public JsonElement serialize() {
        if (this == DamagePredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("dealt", this.dealt.serialize());
        jsonObject1.add("taken", this.taken.serialize());
        jsonObject1.add("source_entity", this.sourceEntity.serialize());
        jsonObject1.add("type", this.type.serialize());
        if (this.blocked != null) {
            jsonObject1.addProperty("blocked", this.blocked);
        }
        return jsonObject1;
    }
    
    static {
        ANY = Builder.create().build();
    }
    
    public static class Builder
    {
        private NumberRange.FloatRange dealt;
        private NumberRange.FloatRange taken;
        private EntityPredicate sourceEntity;
        private Boolean blocked;
        private DamageSourcePredicate type;
        
        public Builder() {
            this.dealt = NumberRange.FloatRange.ANY;
            this.taken = NumberRange.FloatRange.ANY;
            this.sourceEntity = EntityPredicate.ANY;
            this.type = DamageSourcePredicate.EMPTY;
        }
        
        public static Builder create() {
            return new Builder();
        }
        
        public Builder blocked(final Boolean boolean1) {
            this.blocked = boolean1;
            return this;
        }
        
        public Builder type(final DamageSourcePredicate.Builder builder) {
            this.type = builder.build();
            return this;
        }
        
        public DamagePredicate build() {
            return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
        }
    }
}
