package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;

public class OnKilledCriterion implements Criterion<Conditions>
{
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    private final Identifier id;
    
    public OnKilledCriterion(final Identifier identifier) {
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
        return new Conditions(this.id, EntityPredicate.deserialize(obj.get("entity")), DamageSourcePredicate.deserialize(obj.get("killing_blow")));
    }
    
    public void handle(final ServerPlayerEntity player, final Entity entity, final DamageSource damageSource) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(player, entity, damageSource);
        }
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate entity;
        private final DamageSourcePredicate killingBlow;
        
        public Conditions(final Identifier identifier, final EntityPredicate entityPredicate, final DamageSourcePredicate damageSourcePredicate) {
            super(identifier);
            this.entity = entityPredicate;
            this.killingBlow = damageSourcePredicate;
        }
        
        public static Conditions createPlayerKilledEntity(final EntityPredicate.Builder builder) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
        }
        
        public static Conditions createPlayerKilledEntity() {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }
        
        public static Conditions createPlayerKilledEntity(final EntityPredicate.Builder builder, final DamageSourcePredicate.Builder builder) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder.build());
        }
        
        public static Conditions createEntityKilledPlayer() {
            return new Conditions(Criterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }
        
        public boolean test(final ServerPlayerEntity serverPlayerEntity, final Entity entity, final DamageSource damageSource) {
            return this.killingBlow.test(serverPlayerEntity, damageSource) && this.entity.test(serverPlayerEntity, entity);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("entity", this.entity.serialize());
            jsonObject1.add("killing_blow", this.killingBlow.serialize());
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
        
        public void handle(final ServerPlayerEntity serverPlayerEntity, final Entity entity, final DamageSource damageSource) {
            List<ConditionsContainer<Conditions>> list4 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer6 : this.conditions) {
                if (conditionsContainer6.getConditions().test(serverPlayerEntity, entity, damageSource)) {
                    if (list4 == null) {
                        list4 = Lists.newArrayList();
                    }
                    list4.add(conditionsContainer6);
                }
            }
            if (list4 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer6 : list4) {
                    conditionsContainer6.apply(this.manager);
                }
            }
        }
    }
}
