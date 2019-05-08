package net.minecraft.advancement.criterion;

import net.minecraft.advancement.Advancement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions>
{
    Identifier getId();
    
    void beginTrackingCondition(final PlayerAdvancementTracker arg1, final ConditionsContainer<T> arg2);
    
    void endTrackingCondition(final PlayerAdvancementTracker arg1, final ConditionsContainer<T> arg2);
    
    void endTracking(final PlayerAdvancementTracker arg1);
    
    T conditionsFromJson(final JsonObject arg1, final JsonDeserializationContext arg2);
    
    public static class ConditionsContainer<T extends CriterionConditions>
    {
        private final T conditions;
        private final Advancement advancement;
        private final String id;
        
        public ConditionsContainer(final T criterionConditions, final Advancement advancement, final String string) {
            this.conditions = criterionConditions;
            this.advancement = advancement;
            this.id = string;
        }
        
        public T getConditions() {
            return this.conditions;
        }
        
        public void apply(final PlayerAdvancementTracker playerAdvancementTracker) {
            playerAdvancementTracker.grantCriterion(this.advancement, this.id);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ConditionsContainer<?> conditionsContainer2 = o;
            return this.conditions.equals(conditionsContainer2.conditions) && this.advancement.equals(conditionsContainer2.advancement) && this.id.equals(conditionsContainer2.id);
        }
        
        @Override
        public int hashCode() {
            int integer1 = this.conditions.hashCode();
            integer1 = 31 * integer1 + this.advancement.hashCode();
            integer1 = 31 * integer1 + this.id.hashCode();
            return integer1;
        }
    }
}
