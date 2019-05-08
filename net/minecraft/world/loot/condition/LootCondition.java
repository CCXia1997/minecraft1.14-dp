package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.loot.context.ParameterConsumer;

@FunctionalInterface
public interface LootCondition extends ParameterConsumer, Predicate<LootContext>
{
    @FunctionalInterface
    public interface Builder
    {
        LootCondition build();
        
        default Builder invert() {
            return InvertedLootCondition.builder(this);
        }
        
        default AlternativeLootCondition.Builder withCondition(final Builder condition) {
            return AlternativeLootCondition.builder(this, condition);
        }
    }
    
    public abstract static class Factory<T extends LootCondition>
    {
        private final Identifier id;
        private final Class<T> conditionClass;
        
        protected Factory(final Identifier id, final Class<T> clazz) {
            this.id = id;
            this.conditionClass = clazz;
        }
        
        public Identifier getId() {
            return this.id;
        }
        
        public Class<T> getConditionClass() {
            return this.conditionClass;
        }
        
        public abstract void toJson(final JsonObject arg1, final T arg2, final JsonSerializationContext arg3);
        
        public abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2);
    }
}
