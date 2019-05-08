package net.minecraft.predicate.entity;

import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;

public class EntityFlagsPredicate
{
    public static final EntityFlagsPredicate ANY;
    @Nullable
    private final Boolean isOnFire;
    @Nullable
    private final Boolean isSneaking;
    @Nullable
    private final Boolean isSprinting;
    @Nullable
    private final Boolean isSwimming;
    @Nullable
    private final Boolean isBaby;
    
    public EntityFlagsPredicate(@Nullable final Boolean isOnFire, @Nullable final Boolean isSneaking, @Nullable final Boolean isSprinting, @Nullable final Boolean isSwimming, @Nullable final Boolean boolean5) {
        this.isOnFire = isOnFire;
        this.isSneaking = isSneaking;
        this.isSprinting = isSprinting;
        this.isSwimming = isSwimming;
        this.isBaby = boolean5;
    }
    
    public boolean test(final Entity entity) {
        return (this.isOnFire == null || entity.isOnFire() == this.isOnFire) && (this.isSneaking == null || entity.isSneaking() == this.isSneaking) && (this.isSprinting == null || entity.isSprinting() == this.isSprinting) && (this.isSwimming == null || entity.isSwimming() == this.isSwimming) && (this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isChild() == this.isBaby);
    }
    
    @Nullable
    private static Boolean deserializeBoolean(final JsonObject json, final String key) {
        return json.has(key) ? Boolean.valueOf(JsonHelper.getBoolean(json, key)) : null;
    }
    
    public static EntityFlagsPredicate deserialize(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return EntityFlagsPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(element, "entity flags");
        final Boolean boolean3 = deserializeBoolean(jsonObject2, "is_on_fire");
        final Boolean boolean4 = deserializeBoolean(jsonObject2, "is_sneaking");
        final Boolean boolean5 = deserializeBoolean(jsonObject2, "is_sprinting");
        final Boolean boolean6 = deserializeBoolean(jsonObject2, "is_swimming");
        final Boolean boolean7 = deserializeBoolean(jsonObject2, "is_baby");
        return new EntityFlagsPredicate(boolean3, boolean4, boolean5, boolean6, boolean7);
    }
    
    private void serializeBoolean(final JsonObject json, final String key, @Nullable final Boolean boolean3) {
        if (boolean3 != null) {
            json.addProperty(key, boolean3);
        }
    }
    
    public JsonElement serialize() {
        if (this == EntityFlagsPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        this.serializeBoolean(jsonObject1, "is_on_fire", this.isOnFire);
        this.serializeBoolean(jsonObject1, "is_sneaking", this.isSneaking);
        this.serializeBoolean(jsonObject1, "is_sprinting", this.isSprinting);
        this.serializeBoolean(jsonObject1, "is_swimming", this.isSwimming);
        this.serializeBoolean(jsonObject1, "is_baby", this.isBaby);
        return jsonObject1;
    }
    
    static {
        ANY = new Builder().build();
    }
    
    public static class Builder
    {
        @Nullable
        private Boolean isOnFire;
        @Nullable
        private Boolean isSneaking;
        @Nullable
        private Boolean isSprinting;
        @Nullable
        private Boolean isSwimming;
        @Nullable
        private Boolean isBaby;
        
        public static Builder create() {
            return new Builder();
        }
        
        public Builder onFire(@Nullable final Boolean boolean1) {
            this.isOnFire = boolean1;
            return this;
        }
        
        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}
