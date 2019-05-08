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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.DamagePredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public PlayerHurtEntityCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return PlayerHurtEntityCriterion.ID;
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
        final DamagePredicate damagePredicate3 = DamagePredicate.deserialize(obj.get("damage"));
        final EntityPredicate entityPredicate4 = EntityPredicate.deserialize(obj.get("entity"));
        return new Conditions(damagePredicate3, entityPredicate4);
    }
    
    public void handle(final ServerPlayerEntity player, final Entity entity, final DamageSource damageSource, final float float4, final float float5, final boolean boolean6) {
        final Handler handler7 = this.handlers.get(player.getAdvancementManager());
        if (handler7 != null) {
            handler7.handle(player, entity, damageSource, float4, float5, boolean6);
        }
    }
    
    static {
        ID = new Identifier("player_hurt_entity");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final DamagePredicate damage;
        private final EntityPredicate entity;
        
        public Conditions(final DamagePredicate damagePredicate, final EntityPredicate entityPredicate) {
            super(PlayerHurtEntityCriterion.ID);
            this.damage = damagePredicate;
            this.entity = entityPredicate;
        }
        
        public static Conditions create(final DamagePredicate.Builder builder) {
            return new Conditions(builder.build(), EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final Entity entity, final DamageSource damageSource, final float float4, final float float5, final boolean boolean6) {
            return this.damage.test(serverPlayerEntity, damageSource, float4, float5, boolean6) && this.entity.test(serverPlayerEntity, entity);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("damage", this.damage.serialize());
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
        
        public void handle(final ServerPlayerEntity entity, final Entity entity, final DamageSource damageSource, final float float4, final float float5, final boolean boolean6) {
            List<ConditionsContainer<Conditions>> list7 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer9 : this.conditions) {
                if (conditionsContainer9.getConditions().matches(entity, entity, damageSource, float4, float5, boolean6)) {
                    if (list7 == null) {
                        list7 = Lists.newArrayList();
                    }
                    list7.add(conditionsContainer9);
                }
            }
            if (list7 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer9 : list7) {
                    conditionsContainer9.apply(this.manager);
                }
            }
        }
    }
}
