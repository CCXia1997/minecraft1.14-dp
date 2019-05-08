package net.minecraft.advancement.criterion;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import java.util.Collection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> b;
    
    public ChanneledLightningCriterion() {
        this.b = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return ChanneledLightningCriterion.ID;
    }
    
    @Override
    public void beginTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler3 = this.b.get(manager);
        if (handler3 == null) {
            handler3 = new Handler(manager);
            this.b.put(manager, handler3);
        }
        handler3.addCondition(conditionsContainer);
    }
    
    @Override
    public void endTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        final Handler handler3 = this.b.get(manager);
        if (handler3 != null) {
            handler3.removeCondition(conditionsContainer);
            if (handler3.isEmpty()) {
                this.b.remove(manager);
            }
        }
    }
    
    @Override
    public void endTracking(final PlayerAdvancementTracker playerAdvancementTracker) {
        this.b.remove(playerAdvancementTracker);
    }
    
    @Override
    public Conditions conditionsFromJson(final JsonObject obj, final JsonDeserializationContext jsonDeserializationContext) {
        final EntityPredicate[] arr3 = EntityPredicate.deserializeAll(obj.get("victims"));
        return new Conditions(arr3);
    }
    
    public void handle(final ServerPlayerEntity serverPlayerEntity, final Collection<? extends Entity> collection) {
        final Handler handler3 = this.b.get(serverPlayerEntity.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(serverPlayerEntity, collection);
        }
    }
    
    static {
        ID = new Identifier("channeled_lightning");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate[] victims;
        
        public Conditions(final EntityPredicate[] arr) {
            super(ChanneledLightningCriterion.ID);
            this.victims = arr;
        }
        
        public static Conditions create(final EntityPredicate... victims) {
            return new Conditions(victims);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final Collection<? extends Entity> collection) {
            for (final EntityPredicate entityPredicate6 : this.victims) {
                boolean boolean7 = false;
                for (final Entity entity9 : collection) {
                    if (entityPredicate6.test(serverPlayerEntity, entity9)) {
                        boolean7 = true;
                        break;
                    }
                }
                if (!boolean7) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("victims", EntityPredicate.serializeAll(this.victims));
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
        
        public void handle(final ServerPlayerEntity serverPlayerEntity, final Collection<? extends Entity> collection) {
            List<ConditionsContainer<Conditions>> list3 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer5 : this.conditions) {
                if (conditionsContainer5.getConditions().matches(serverPlayerEntity, collection)) {
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
