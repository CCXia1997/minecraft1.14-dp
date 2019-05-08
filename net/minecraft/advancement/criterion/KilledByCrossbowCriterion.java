package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityType;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import java.util.Collection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class KilledByCrossbowCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public KilledByCrossbowCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return KilledByCrossbowCriterion.ID;
    }
    
    @Override
    public void beginTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler3 = this.handlers.get(manager);
        if (handler3 == null) {
            handler3 = new Handler(manager);
            this.handlers.put(manager, handler3);
        }
        handler3.add(conditionsContainer);
    }
    
    @Override
    public void endTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        final Handler handler3 = this.handlers.get(manager);
        if (handler3 != null) {
            handler3.remove(conditionsContainer);
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
        final EntityPredicate[] arr3 = EntityPredicate.deserializeAll(obj.get("victims"));
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(obj.get("unique_entity_types"));
        return new Conditions(arr3, intRange4);
    }
    
    public void trigger(final ServerPlayerEntity serverPlayerEntity, final Collection<Entity> collection, final int integer) {
        final Handler handler4 = this.handlers.get(serverPlayerEntity.getAdvancementManager());
        if (handler4 != null) {
            handler4.trigger(serverPlayerEntity, collection, integer);
        }
    }
    
    static {
        ID = new Identifier("killed_by_crossbow");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate[] victims;
        private final NumberRange.IntRange uniqueEntityTypes;
        
        public Conditions(final EntityPredicate[] arr, final NumberRange.IntRange intRange) {
            super(KilledByCrossbowCriterion.ID);
            this.victims = arr;
            this.uniqueEntityTypes = intRange;
        }
        
        public static Conditions create(final EntityPredicate.Builder... arr) {
            final EntityPredicate[] arr2 = new EntityPredicate[arr.length];
            for (int integer3 = 0; integer3 < arr.length; ++integer3) {
                final EntityPredicate.Builder builder4 = arr[integer3];
                arr2[integer3] = builder4.build();
            }
            return new Conditions(arr2, NumberRange.IntRange.ANY);
        }
        
        public static Conditions create(final NumberRange.IntRange intRange) {
            final EntityPredicate[] arr2 = new EntityPredicate[0];
            return new Conditions(arr2, intRange);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final Collection<Entity> collection, final int integer) {
            if (this.victims.length > 0) {
                final List<Entity> list4 = Lists.newArrayList(collection);
                for (final EntityPredicate entityPredicate8 : this.victims) {
                    boolean boolean9 = false;
                    final Iterator<Entity> iterator10 = list4.iterator();
                    while (iterator10.hasNext()) {
                        final Entity entity11 = iterator10.next();
                        if (entityPredicate8.test(serverPlayerEntity, entity11)) {
                            iterator10.remove();
                            boolean9 = true;
                            break;
                        }
                    }
                    if (!boolean9) {
                        return false;
                    }
                }
            }
            if (this.uniqueEntityTypes != NumberRange.IntRange.ANY) {
                final Set<EntityType<?>> set4 = Sets.newHashSet();
                for (final Entity entity12 : collection) {
                    set4.add(entity12.getType());
                }
                return this.uniqueEntityTypes.test(set4.size()) && this.uniqueEntityTypes.test(integer);
            }
            return true;
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("victims", EntityPredicate.serializeAll(this.victims));
            jsonObject1.add("unique_entity_types", this.uniqueEntityTypes.serialize());
            return jsonObject1;
        }
    }
    
    static class Handler
    {
        private final PlayerAdvancementTracker tracker;
        private final Set<ConditionsContainer<Conditions>> conditions;
        
        public Handler(final PlayerAdvancementTracker playerAdvancementTracker) {
            this.conditions = Sets.newHashSet();
            this.tracker = playerAdvancementTracker;
        }
        
        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }
        
        public void add(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }
        
        public void remove(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }
        
        public void trigger(final ServerPlayerEntity serverPlayerEntity, final Collection<Entity> collection, final int integer) {
            List<ConditionsContainer<Conditions>> list4 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer6 : this.conditions) {
                if (conditionsContainer6.getConditions().matches(serverPlayerEntity, collection, integer)) {
                    if (list4 == null) {
                        list4 = Lists.newArrayList();
                    }
                    list4.add(conditionsContainer6);
                }
            }
            if (list4 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer6 : list4) {
                    conditionsContainer6.apply(this.tracker);
                }
            }
        }
    }
}
