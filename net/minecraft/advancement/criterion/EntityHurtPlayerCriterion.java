package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.DamagePredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public EntityHurtPlayerCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return EntityHurtPlayerCriterion.ID;
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
        return new Conditions(damagePredicate3);
    }
    
    public void handle(final ServerPlayerEntity player, final DamageSource source, final float dealt, final float taken, final boolean boolean5) {
        final Handler handler6 = this.handlers.get(player.getAdvancementManager());
        if (handler6 != null) {
            handler6.handle(player, source, dealt, taken, boolean5);
        }
    }
    
    static {
        ID = new Identifier("entity_hurt_player");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final DamagePredicate damage;
        
        public Conditions(final DamagePredicate damagePredicate) {
            super(EntityHurtPlayerCriterion.ID);
            this.damage = damagePredicate;
        }
        
        public static Conditions create(final DamagePredicate.Builder builder) {
            return new Conditions(builder.build());
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final DamageSource damageSource, final float float3, final float float4, final boolean boolean5) {
            return this.damage.test(serverPlayerEntity, damageSource, float3, float4, boolean5);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("damage", this.damage.serialize());
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
        
        public void handle(final ServerPlayerEntity source, final DamageSource dealt, final float taken, final float blocked, final boolean boolean5) {
            List<ConditionsContainer<Conditions>> list6 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer8 : this.conditions) {
                if (conditionsContainer8.getConditions().matches(source, dealt, taken, blocked, boolean5)) {
                    if (list6 == null) {
                        list6 = Lists.newArrayList();
                    }
                    list6.add(conditionsContainer8);
                }
            }
            if (list6 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer8 : list6) {
                    conditionsContainer8.apply(this.manager);
                }
            }
        }
    }
}
