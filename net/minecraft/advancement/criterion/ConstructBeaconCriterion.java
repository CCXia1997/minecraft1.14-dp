package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NumberRange;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public ConstructBeaconCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return ConstructBeaconCriterion.ID;
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
        final NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(obj.get("level"));
        return new Conditions(intRange3);
    }
    
    public void handle(final ServerPlayerEntity player, final BeaconBlockEntity beaconBlockEntity) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(beaconBlockEntity);
        }
    }
    
    static {
        ID = new Identifier("construct_beacon");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final NumberRange.IntRange level;
        
        public Conditions(final NumberRange.IntRange intRange) {
            super(ConstructBeaconCriterion.ID);
            this.level = intRange;
        }
        
        public static Conditions level(final NumberRange.IntRange intRange) {
            return new Conditions(intRange);
        }
        
        public boolean matches(final BeaconBlockEntity beaconBlockEntity) {
            return this.level.test(beaconBlockEntity.getLevel());
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("level", this.level.serialize());
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
        
        public void handle(final BeaconBlockEntity beaconBlockEntity) {
            List<ConditionsContainer<Conditions>> list2 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer4 : this.conditions) {
                if (conditionsContainer4.getConditions().matches(beaconBlockEntity)) {
                    if (list2 == null) {
                        list2 = Lists.newArrayList();
                    }
                    list2.add(conditionsContainer4);
                }
            }
            if (list2 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer4 : list2) {
                    conditionsContainer4.apply(this.manager);
                }
            }
        }
    }
}
