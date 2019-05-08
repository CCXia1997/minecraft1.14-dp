package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.gson.JsonElement;
import net.minecraft.predicate.entity.LocationPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion implements Criterion<Conditions>
{
    private final Identifier id;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public LocationArrivalCriterion(final Identifier identifier) {
        this.handlers = Maps.newHashMap();
        this.id = identifier;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
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
        final LocationPredicate locationPredicate3 = LocationPredicate.deserialize(obj);
        return new Conditions(this.id, locationPredicate3);
    }
    
    public void handle(final ServerPlayerEntity serverPlayerEntity) {
        final Handler handler2 = this.handlers.get(serverPlayerEntity.getAdvancementManager());
        if (handler2 != null) {
            handler2.handle(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
        }
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final LocationPredicate location;
        
        public Conditions(final Identifier identifier, final LocationPredicate locationPredicate) {
            super(identifier);
            this.location = locationPredicate;
        }
        
        public static Conditions create(final LocationPredicate locationPredicate) {
            return new Conditions(Criterions.LOCATION.id, locationPredicate);
        }
        
        public static Conditions createSleptInBed() {
            return new Conditions(Criterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
        }
        
        public static Conditions createHeroOfTheVillage() {
            return new Conditions(Criterions.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
        }
        
        public boolean matches(final ServerWorld serverWorld, final double double2, final double double4, final double double6) {
            return this.location.test(serverWorld, double2, double4, double6);
        }
        
        @Override
        public JsonElement toJson() {
            return this.location.serialize();
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
        
        public void handle(final ServerWorld x, final double y, final double double4, final double double6) {
            List<ConditionsContainer<Conditions>> list8 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer10 : this.conditions) {
                if (conditionsContainer10.getConditions().matches(x, y, double4, double6)) {
                    if (list8 == null) {
                        list8 = Lists.newArrayList();
                    }
                    list8.add(conditionsContainer10);
                }
            }
            if (list8 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer10 : list8) {
                    conditionsContainer10.apply(this.manager);
                }
            }
        }
    }
}
