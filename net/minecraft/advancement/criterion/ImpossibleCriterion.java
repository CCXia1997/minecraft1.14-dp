package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    
    @Override
    public Identifier getId() {
        return ImpossibleCriterion.ID;
    }
    
    @Override
    public void beginTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
    }
    
    @Override
    public void endTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
    }
    
    @Override
    public void endTracking(final PlayerAdvancementTracker playerAdvancementTracker) {
    }
    
    @Override
    public Conditions conditionsFromJson(final JsonObject obj, final JsonDeserializationContext jsonDeserializationContext) {
        return new Conditions();
    }
    
    static {
        ID = new Identifier("impossible");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        public Conditions() {
            super(ImpossibleCriterion.ID);
        }
    }
}
