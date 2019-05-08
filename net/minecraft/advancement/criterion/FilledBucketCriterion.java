package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.item.ItemPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class FilledBucketCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> b;
    
    public FilledBucketCriterion() {
        this.b = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return FilledBucketCriterion.ID;
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
        final ItemPredicate itemPredicate3 = ItemPredicate.deserialize(obj.get("item"));
        return new Conditions(itemPredicate3);
    }
    
    public void handle(final ServerPlayerEntity serverPlayerEntity, final ItemStack itemStack) {
        final Handler handler3 = this.b.get(serverPlayerEntity.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(itemStack);
        }
    }
    
    static {
        ID = new Identifier("filled_bucket");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final ItemPredicate item;
        
        public Conditions(final ItemPredicate itemPredicate) {
            super(FilledBucketCriterion.ID);
            this.item = itemPredicate;
        }
        
        public static Conditions create(final ItemPredicate itemPredicate) {
            return new Conditions(itemPredicate);
        }
        
        public boolean matches(final ItemStack itemStack) {
            return this.item.test(itemStack);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("item", this.item.serialize());
            return jsonObject1;
        }
    }
    
    static class Handler
    {
        private final PlayerAdvancementTracker manager;
        private final Set<ConditionsContainer<Conditions>> conditions;
        
        public Handler(final PlayerAdvancementTracker manager) {
            this.conditions = Sets.newHashSet();
            this.manager = manager;
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
        
        public void handle(final ItemStack itemStack) {
            List<ConditionsContainer<Conditions>> list2 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer4 : this.conditions) {
                if (conditionsContainer4.getConditions().matches(itemStack)) {
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
