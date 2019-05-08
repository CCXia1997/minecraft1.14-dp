package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.entity.player.PlayerEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;

public class KilledByPlayerLootCondition implements LootCondition
{
    private static final KilledByPlayerLootCondition INSTANCE;
    
    private KilledByPlayerLootCondition() {
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.b);
    }
    
    public boolean a(final LootContext context) {
        return context.hasParameter(LootContextParameters.b);
    }
    
    public static Builder builder() {
        return () -> KilledByPlayerLootCondition.INSTANCE;
    }
    
    static {
        INSTANCE = new KilledByPlayerLootCondition();
    }
    
    public static class Factory extends LootCondition.Factory<KilledByPlayerLootCondition>
    {
        protected Factory() {
            super(new Identifier("killed_by_player"), KilledByPlayerLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final KilledByPlayerLootCondition condition, final JsonSerializationContext context) {
        }
        
        @Override
        public KilledByPlayerLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            return KilledByPlayerLootCondition.INSTANCE;
        }
    }
}
