package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ItemDurabilityChangedCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public ItemDurabilityChangedCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return ItemDurabilityChangedCriterion.ID;
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
        final ItemPredicate itemPredicate3 = ItemPredicate.deserialize(obj.get("item"));
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(obj.get("durability"));
        final NumberRange.IntRange intRange5 = NumberRange.IntRange.fromJson(obj.get("delta"));
        return new Conditions(itemPredicate3, intRange4, intRange5);
    }
    
    public void handle(final ServerPlayerEntity player, final ItemStack item, final int integer) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(item, integer);
        }
    }
    
    static {
        ID = new Identifier("item_durability_changed");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final ItemPredicate item;
        private final NumberRange.IntRange durability;
        private final NumberRange.IntRange delta;
        
        public Conditions(final ItemPredicate itemPredicate, final NumberRange.IntRange intRange2, final NumberRange.IntRange intRange3) {
            super(ItemDurabilityChangedCriterion.ID);
            this.item = itemPredicate;
            this.durability = intRange2;
            this.delta = intRange3;
        }
        
        public static Conditions create(final ItemPredicate itemPredicate, final NumberRange.IntRange intRange) {
            return new Conditions(itemPredicate, intRange, NumberRange.IntRange.ANY);
        }
        
        public boolean matches(final ItemStack itemStack, final int integer) {
            return this.item.test(itemStack) && this.durability.test(itemStack.getDurability() - integer) && this.delta.test(itemStack.getDamage() - integer);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("item", this.item.serialize());
            jsonObject1.add("durability", this.durability.serialize());
            jsonObject1.add("delta", this.delta.serialize());
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
        
        public void handle(final ItemStack itemStack, final int integer) {
            List<ConditionsContainer<Conditions>> list3 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer5 : this.conditions) {
                if (conditionsContainer5.getConditions().matches(itemStack, integer)) {
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
