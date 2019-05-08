package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.loot.context.LootContext;
import javax.annotation.Nullable;

public class WeatherCheckLootCondition implements LootCondition
{
    @Nullable
    private final Boolean raining;
    @Nullable
    private final Boolean thundering;
    
    private WeatherCheckLootCondition(@Nullable final Boolean raining, @Nullable final Boolean thundering) {
        this.raining = raining;
        this.thundering = thundering;
    }
    
    public boolean a(final LootContext context) {
        final ServerWorld serverWorld2 = context.getWorld();
        return (this.raining == null || this.raining == serverWorld2.isRaining()) && (this.thundering == null || this.thundering == serverWorld2.isThundering());
    }
    
    public static class Factory extends LootCondition.Factory<WeatherCheckLootCondition>
    {
        public Factory() {
            super(new Identifier("weather_check"), WeatherCheckLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final WeatherCheckLootCondition condition, final JsonSerializationContext context) {
            json.addProperty("raining", condition.raining);
            json.addProperty("thundering", condition.thundering);
        }
        
        @Override
        public WeatherCheckLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final Boolean boolean3 = json.has("raining") ? Boolean.valueOf(JsonHelper.getBoolean(json, "raining")) : null;
            final Boolean boolean4 = json.has("thundering") ? Boolean.valueOf(JsonHelper.getBoolean(json, "thundering")) : null;
            return new WeatherCheckLootCondition(boolean3, boolean4, null);
        }
    }
}
