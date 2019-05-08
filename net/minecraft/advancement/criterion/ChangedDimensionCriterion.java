package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ChangedDimensionCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public ChangedDimensionCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return ChangedDimensionCriterion.ID;
    }
    
    @Override
    public void beginTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler3 = this.handlers.get(manager);
        if (handler3 == null) {
            handler3 = new Handler(manager);
            this.handlers.put(manager, handler3);
        }
        handler3.addCondition(conditionsContainer);
    }
    
    @Override
    public void endTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        final Handler handler3 = this.handlers.get(manager);
        if (handler3 != null) {
            handler3.removeCondition(conditionsContainer);
            if (handler3.isEmpty()) {
                this.handlers.remove(manager);
            }
        }
    }
    
    @Override
    public void endTracking(final PlayerAdvancementTracker playerAdvancementTracker) {
        this.handlers.remove(playerAdvancementTracker);
    }
    
    @Override
    public Conditions conditionsFromJson(final JsonObject obj, final JsonDeserializationContext jsonDeserializationContext) {
        final DimensionType dimensionType3 = obj.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(obj, "from"))) : null;
        final DimensionType dimensionType4 = obj.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(obj, "to"))) : null;
        return new Conditions(dimensionType3, dimensionType4);
    }
    
    public void handle(final ServerPlayerEntity player, final DimensionType dimensionType2, final DimensionType dimensionType3) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(dimensionType2, dimensionType3);
        }
    }
    
    static {
        ID = new Identifier("changed_dimension");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        @Nullable
        private final DimensionType from;
        @Nullable
        private final DimensionType to;
        
        public Conditions(@Nullable final DimensionType dimensionType1, @Nullable final DimensionType dimensionType2) {
            super(ChangedDimensionCriterion.ID);
            this.from = dimensionType1;
            this.to = dimensionType2;
        }
        
        public static Conditions to(final DimensionType dimensionType) {
            return new Conditions(null, dimensionType);
        }
        
        public boolean matches(final DimensionType dimensionType1, final DimensionType dimensionType2) {
            return (this.from == null || this.from == dimensionType1) && (this.to == null || this.to == dimensionType2);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            if (this.from != null) {
                jsonObject1.addProperty("from", DimensionType.getId(this.from).toString());
            }
            if (this.to != null) {
                jsonObject1.addProperty("to", DimensionType.getId(this.to).toString());
            }
            return jsonObject1;
        }
    }
    
    static class Handler
    {
        private final PlayerAdvancementTracker manager;
        private final Set<ConditionsContainer<Conditions>> conditions;
        
        public Handler(final PlayerAdvancementTracker playerAdvancementTracker) {
            this.conditions = Sets.newHashSet();
            this.manager = playerAdvancementTracker;
        }
        
        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }
        
        public void addCondition(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }
        
        public void removeCondition(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }
        
        public void handle(final DimensionType dimensionType1, final DimensionType dimensionType2) {
            List<ConditionsContainer<Conditions>> list3 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer5 : this.conditions) {
                if (conditionsContainer5.getConditions().matches(dimensionType1, dimensionType2)) {
                    if (list3 == null) {
                        list3 = Lists.newArrayList();
                    }
                    list3.add(conditionsContainer5);
                }
            }
            if (list3 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer5 : list3) {
                    conditionsContainer5.apply(this.manager);
                }
            }
        }
    }
}
