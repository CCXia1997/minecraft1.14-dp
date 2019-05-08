package net.minecraft.predicate.entity;

import net.minecraft.tag.Tag;
import net.minecraft.entity.EntityType;
import com.google.gson.JsonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.predicate.NbtPredicate;

public class EntityPredicate
{
    public static final EntityPredicate ANY;
    public static final EntityPredicate[] EMPTY;
    private final EntityTypePredicate type;
    private final DistancePredicate distance;
    private final LocationPredicate location;
    private final EntityEffectPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final Identifier catType;
    
    private EntityPredicate(final EntityTypePredicate type, final DistancePredicate distance, final LocationPredicate location, final EntityEffectPredicate effects, final NbtPredicate nbt, final EntityFlagsPredicate flags, final EntityEquipmentPredicate equipment, @Nullable final Identifier identifier) {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.catType = identifier;
    }
    
    public boolean test(final ServerPlayerEntity player, @Nullable final Entity entity) {
        return this.test(player.getServerWorld(), new Vec3d(player.x, player.y, player.z), entity);
    }
    
    public boolean test(final ServerWorld world, final Vec3d pos, @Nullable final Entity entity) {
        return this == EntityPredicate.ANY || (entity != null && this.type.matches(entity.getType()) && this.distance.test(pos.x, pos.y, pos.z, entity.x, entity.y, entity.z) && this.location.test(world, entity.x, entity.y, entity.z) && this.effects.test(entity) && this.nbt.test(entity) && this.flags.test(entity) && this.equipment.test(entity) && (this.catType == null || (entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType))));
    }
    
    public static EntityPredicate deserialize(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return EntityPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(el, "entity");
        final EntityTypePredicate entityTypePredicate3 = EntityTypePredicate.deserialize(jsonObject2.get("type"));
        final DistancePredicate distancePredicate4 = DistancePredicate.deserialize(jsonObject2.get("distance"));
        final LocationPredicate locationPredicate5 = LocationPredicate.deserialize(jsonObject2.get("location"));
        final EntityEffectPredicate entityEffectPredicate6 = EntityEffectPredicate.deserialize(jsonObject2.get("effects"));
        final NbtPredicate nbtPredicate7 = NbtPredicate.deserialize(jsonObject2.get("nbt"));
        final EntityFlagsPredicate entityFlagsPredicate8 = EntityFlagsPredicate.deserialize(jsonObject2.get("flags"));
        final EntityEquipmentPredicate entityEquipmentPredicate9 = EntityEquipmentPredicate.deserialize(jsonObject2.get("equipment"));
        final Identifier identifier10 = jsonObject2.has("catType") ? new Identifier(JsonHelper.getString(jsonObject2, "catType")) : null;
        return new Builder().type(entityTypePredicate3).distance(distancePredicate4).location(locationPredicate5).effects(entityEffectPredicate6).nbt(nbtPredicate7).flags(entityFlagsPredicate8).equipment(entityEquipmentPredicate9).catType(identifier10).build();
    }
    
    public static EntityPredicate[] deserializeAll(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return EntityPredicate.EMPTY;
        }
        final JsonArray jsonArray2 = JsonHelper.asArray(element, "entities");
        final EntityPredicate[] arr3 = new EntityPredicate[jsonArray2.size()];
        for (int integer4 = 0; integer4 < jsonArray2.size(); ++integer4) {
            arr3[integer4] = deserialize(jsonArray2.get(integer4));
        }
        return arr3;
    }
    
    public JsonElement serialize() {
        if (this == EntityPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("type", this.type.toJson());
        jsonObject1.add("distance", this.distance.serialize());
        jsonObject1.add("location", this.location.serialize());
        jsonObject1.add("effects", this.effects.serialize());
        jsonObject1.add("nbt", this.nbt.serialize());
        jsonObject1.add("flags", this.flags.serialize());
        jsonObject1.add("equipment", this.equipment.serialize());
        if (this.catType != null) {
            jsonObject1.addProperty("catType", this.catType.toString());
        }
        return jsonObject1;
    }
    
    public static JsonElement serializeAll(final EntityPredicate[] predicates) {
        if (predicates == EntityPredicate.EMPTY) {
            return JsonNull.INSTANCE;
        }
        final JsonArray jsonArray2 = new JsonArray();
        for (final EntityPredicate entityPredicate6 : predicates) {
            final JsonElement jsonElement7 = entityPredicate6.serialize();
            if (!jsonElement7.isJsonNull()) {
                jsonArray2.add(jsonElement7);
            }
        }
        return jsonArray2;
    }
    
    static {
        ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, EntityEffectPredicate.EMPTY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, null);
        EMPTY = new EntityPredicate[0];
    }
    
    public static class Builder
    {
        private EntityTypePredicate type;
        private DistancePredicate distance;
        private LocationPredicate location;
        private EntityEffectPredicate effects;
        private NbtPredicate nbt;
        private EntityFlagsPredicate flags;
        private EntityEquipmentPredicate equipment;
        @Nullable
        private Identifier catType;
        
        public Builder() {
            this.type = EntityTypePredicate.ANY;
            this.distance = DistancePredicate.ANY;
            this.location = LocationPredicate.ANY;
            this.effects = EntityEffectPredicate.EMPTY;
            this.nbt = NbtPredicate.ANY;
            this.flags = EntityFlagsPredicate.ANY;
            this.equipment = EntityEquipmentPredicate.ANY;
        }
        
        public static Builder create() {
            return new Builder();
        }
        
        public Builder type(final EntityType<?> entityType) {
            this.type = EntityTypePredicate.create(entityType);
            return this;
        }
        
        public Builder type(final Tag<EntityType<?>> tag) {
            this.type = EntityTypePredicate.create(tag);
            return this;
        }
        
        public Builder type(final Identifier identifier) {
            this.catType = identifier;
            return this;
        }
        
        public Builder type(final EntityTypePredicate entityTypePredicate) {
            this.type = entityTypePredicate;
            return this;
        }
        
        public Builder distance(final DistancePredicate distancePredicate) {
            this.distance = distancePredicate;
            return this;
        }
        
        public Builder location(final LocationPredicate locationPredicate) {
            this.location = locationPredicate;
            return this;
        }
        
        public Builder effects(final EntityEffectPredicate entityEffectPredicate) {
            this.effects = entityEffectPredicate;
            return this;
        }
        
        public Builder nbt(final NbtPredicate nbtPredicate) {
            this.nbt = nbtPredicate;
            return this;
        }
        
        public Builder flags(final EntityFlagsPredicate entityFlagsPredicate) {
            this.flags = entityFlagsPredicate;
            return this;
        }
        
        public Builder equipment(final EntityEquipmentPredicate entityEquipmentPredicate) {
            this.equipment = entityEquipmentPredicate;
            return this;
        }
        
        public Builder catType(@Nullable final Identifier identifier) {
            this.catType = identifier;
            return this;
        }
        
        public EntityPredicate build() {
            return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType, null);
        }
    }
}
