package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public SummonedEntityCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return SummonedEntityCriterion.ID;
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
        final EntityPredicate entityPredicate3 = EntityPredicate.deserialize(obj.get("entity"));
        return new Conditions(entityPredicate3);
    }
    
    public void handle(final ServerPlayerEntity player, final Entity entity) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(player, entity);
        }
    }
    
    static {
        ID = new Identifier("summoned_entity");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate entity;
        
        public Conditions(final EntityPredicate entityPredicate) {
            super(SummonedEntityCriterion.ID);
            this.entity = entityPredicate;
        }
        
        public static Conditions create(final EntityPredicate.Builder builder) {
            return new Conditions(builder.build());
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final Entity entity) {
            return this.entity.test(serverPlayerEntity, entity);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("entity", this.entity.serialize());
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
        
        public void handle(final ServerPlayerEntity serverPlayerEntity, final Entity entity) {
            List<ConditionsContainer<Conditions>> list3 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer5 : this.conditions) {
                if (conditionsContainer5.getConditions().matches(serverPlayerEntity, entity)) {
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
