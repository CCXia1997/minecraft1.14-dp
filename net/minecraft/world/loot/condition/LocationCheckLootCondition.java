package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.predicate.entity.LocationPredicate;

public class LocationCheckLootCondition implements LootCondition
{
    private final LocationPredicate predicate;
    
    private LocationCheckLootCondition(final LocationPredicate predicate) {
        this.predicate = predicate;
    }
    
    public boolean a(final LootContext context) {
        final BlockPos blockPos2 = context.<BlockPos>get(LootContextParameters.f);
        return blockPos2 != null && this.predicate.test(context.getWorld(), (float)blockPos2.getX(), (float)blockPos2.getY(), (float)blockPos2.getZ());
    }
    
    public static Builder builder(final LocationPredicate.Builder predicateBuilder) {
        return () -> new LocationCheckLootCondition(predicateBuilder.build());
    }
    
    public static class Factory extends LootCondition.Factory<LocationCheckLootCondition>
    {
        public Factory() {
            super(new Identifier("location_check"), LocationCheckLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final LocationCheckLootCondition condition, final JsonSerializationContext context) {
            json.add("predicate", condition.predicate.serialize());
        }
        
        @Override
        public LocationCheckLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final LocationPredicate locationPredicate3 = LocationPredicate.deserialize(json.get("predicate"));
            return new LocationCheckLootCondition(locationPredicate3, null);
        }
    }
}
