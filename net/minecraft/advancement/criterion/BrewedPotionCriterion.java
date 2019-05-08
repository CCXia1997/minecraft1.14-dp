package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.potion.Potion;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class BrewedPotionCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public BrewedPotionCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return BrewedPotionCriterion.ID;
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
        Potion potion3 = null;
        if (obj.has("potion")) {
            final Identifier identifier4 = new Identifier(JsonHelper.getString(obj, "potion"));
            final Object o;
            final Object o2;
            potion3 = Registry.POTION.getOrEmpty(identifier4).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown potion '" + o2 + "'");
                return o;
            });
        }
        return new Conditions(potion3);
    }
    
    public void handle(final ServerPlayerEntity player, final Potion potion) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(potion);
        }
    }
    
    static {
        ID = new Identifier("brewed_potion");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final Potion potion;
        
        public Conditions(@Nullable final Potion potion) {
            super(BrewedPotionCriterion.ID);
            this.potion = potion;
        }
        
        public static Conditions any() {
            return new Conditions((Potion)null);
        }
        
        public boolean matches(final Potion potion) {
            return this.potion == null || this.potion == potion;
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            if (this.potion != null) {
                jsonObject1.addProperty("potion", Registry.POTION.getId(this.potion).toString());
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
        
        public void handle(final Potion potion) {
            List<ConditionsContainer<Conditions>> list2 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer4 : this.conditions) {
                if (conditionsContainer4.getConditions().matches(potion)) {
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
