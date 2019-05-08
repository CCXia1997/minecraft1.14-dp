package net.minecraft.predicate.entity;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import javax.annotation.Nullable;

public class DamageSourcePredicate
{
    public static final DamageSourcePredicate EMPTY;
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;
    
    public DamageSourcePredicate(@Nullable final Boolean boolean1, @Nullable final Boolean boolean2, @Nullable final Boolean boolean3, @Nullable final Boolean boolean4, @Nullable final Boolean boolean5, @Nullable final Boolean boolean6, @Nullable final Boolean boolean7, @Nullable final Boolean boolean8, final EntityPredicate entityPredicate9, final EntityPredicate entityPredicate10) {
        this.isProjectile = boolean1;
        this.isExplosion = boolean2;
        this.bypassesArmor = boolean3;
        this.bypassesInvulnerability = boolean4;
        this.bypassesMagic = boolean5;
        this.isFire = boolean6;
        this.isMagic = boolean7;
        this.isLightning = boolean8;
        this.directEntity = entityPredicate9;
        this.sourceEntity = entityPredicate10;
    }
    
    public boolean test(final ServerPlayerEntity player, final DamageSource damageSource) {
        return this.test(player.getServerWorld(), new Vec3d(player.x, player.y, player.z), damageSource);
    }
    
    public boolean test(final ServerWorld world, final Vec3d pos, final DamageSource damageSource) {
        return this == DamageSourcePredicate.EMPTY || ((this.isProjectile == null || this.isProjectile == damageSource.isProjectile()) && (this.isExplosion == null || this.isExplosion == damageSource.isExplosive()) && (this.bypassesArmor == null || this.bypassesArmor == damageSource.bypassesArmor()) && (this.bypassesInvulnerability == null || this.bypassesInvulnerability == damageSource.doesDamageToCreative()) && (this.bypassesMagic == null || this.bypassesMagic == damageSource.isUnblockable()) && (this.isFire == null || this.isFire == damageSource.isFire()) && (this.isMagic == null || this.isMagic == damageSource.getMagic()) && (this.isLightning == null || this.isLightning == (damageSource == DamageSource.LIGHTNING_BOLT)) && this.directEntity.test(world, pos, damageSource.getSource()) && this.sourceEntity.test(world, pos, damageSource.getAttacker()));
    }
    
    public static DamageSourcePredicate deserialize(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return DamageSourcePredicate.EMPTY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(element, "damage type");
        final Boolean boolean3 = getBoolean(jsonObject2, "is_projectile");
        final Boolean boolean4 = getBoolean(jsonObject2, "is_explosion");
        final Boolean boolean5 = getBoolean(jsonObject2, "bypasses_armor");
        final Boolean boolean6 = getBoolean(jsonObject2, "bypasses_invulnerability");
        final Boolean boolean7 = getBoolean(jsonObject2, "bypasses_magic");
        final Boolean boolean8 = getBoolean(jsonObject2, "is_fire");
        final Boolean boolean9 = getBoolean(jsonObject2, "is_magic");
        final Boolean boolean10 = getBoolean(jsonObject2, "is_lightning");
        final EntityPredicate entityPredicate11 = EntityPredicate.deserialize(jsonObject2.get("direct_entity"));
        final EntityPredicate entityPredicate12 = EntityPredicate.deserialize(jsonObject2.get("source_entity"));
        return new DamageSourcePredicate(boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, boolean9, boolean10, entityPredicate11, entityPredicate12);
    }
    
    @Nullable
    private static Boolean getBoolean(final JsonObject obj, final String name) {
        return obj.has(name) ? Boolean.valueOf(JsonHelper.getBoolean(obj, name)) : null;
    }
    
    public JsonElement serialize() {
        if (this == DamageSourcePredicate.EMPTY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        this.addProperty(jsonObject1, "is_projectile", this.isProjectile);
        this.addProperty(jsonObject1, "is_explosion", this.isExplosion);
        this.addProperty(jsonObject1, "bypasses_armor", this.bypassesArmor);
        this.addProperty(jsonObject1, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addProperty(jsonObject1, "bypasses_magic", this.bypassesMagic);
        this.addProperty(jsonObject1, "is_fire", this.isFire);
        this.addProperty(jsonObject1, "is_magic", this.isMagic);
        this.addProperty(jsonObject1, "is_lightning", this.isLightning);
        jsonObject1.add("direct_entity", this.directEntity.serialize());
        jsonObject1.add("source_entity", this.sourceEntity.serialize());
        return jsonObject1;
    }
    
    private void addProperty(final JsonObject json, final String key, @Nullable final Boolean boolean3) {
        if (boolean3 != null) {
            json.addProperty(key, boolean3);
        }
    }
    
    static {
        EMPTY = Builder.create().build();
    }
    
    public static class Builder
    {
        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private EntityPredicate directEntity;
        private EntityPredicate sourceEntity;
        
        public Builder() {
            this.directEntity = EntityPredicate.ANY;
            this.sourceEntity = EntityPredicate.ANY;
        }
        
        public static Builder create() {
            return new Builder();
        }
        
        public Builder projectile(final Boolean boolean1) {
            this.isProjectile = boolean1;
            return this;
        }
        
        public Builder lightning(final Boolean boolean1) {
            this.isLightning = boolean1;
            return this;
        }
        
        public Builder directEntity(final EntityPredicate.Builder builder) {
            this.directEntity = builder.build();
            return this;
        }
        
        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}
