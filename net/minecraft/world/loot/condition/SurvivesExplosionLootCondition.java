package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.Random;
import net.minecraft.world.loot.context.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;

public class SurvivesExplosionLootCondition implements LootCondition
{
    private static final SurvivesExplosionLootCondition INSTANCE;
    
    private SurvivesExplosionLootCondition() {
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.j);
    }
    
    public boolean a(final LootContext context) {
        final Float float2 = context.<Float>get(LootContextParameters.j);
        if (float2 != null) {
            final Random random3 = context.getRandom();
            final float float3 = 1.0f / float2;
            return random3.nextFloat() <= float3;
        }
        return true;
    }
    
    public static Builder builder() {
        return () -> SurvivesExplosionLootCondition.INSTANCE;
    }
    
    static {
        INSTANCE = new SurvivesExplosionLootCondition();
    }
    
    public static class Factory extends LootCondition.Factory<SurvivesExplosionLootCondition>
    {
        protected Factory() {
            super(new Identifier("survives_explosion"), SurvivesExplosionLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SurvivesExplosionLootCondition condition, final JsonSerializationContext context) {
        }
        
        @Override
        public SurvivesExplosionLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            return SurvivesExplosionLootCondition.INSTANCE;
        }
    }
}
