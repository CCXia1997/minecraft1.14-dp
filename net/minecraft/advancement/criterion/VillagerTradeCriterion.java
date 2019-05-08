package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public VillagerTradeCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return VillagerTradeCriterion.ID;
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
        final EntityPredicate entityPredicate3 = EntityPredicate.deserialize(obj.get("villager"));
        final ItemPredicate itemPredicate4 = ItemPredicate.deserialize(obj.get("item"));
        return new Conditions(entityPredicate3, itemPredicate4);
    }
    
    public void handle(final ServerPlayerEntity player, final AbstractTraderEntity abstractTraderEntity, final ItemStack itemStack) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(player, abstractTraderEntity, itemStack);
        }
    }
    
    static {
        ID = new Identifier("villager_trade");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate item;
        private final ItemPredicate villager;
        
        public Conditions(final EntityPredicate entityPredicate, final ItemPredicate itemPredicate) {
            super(VillagerTradeCriterion.ID);
            this.item = entityPredicate;
            this.villager = itemPredicate;
        }
        
        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final AbstractTraderEntity abstractTraderEntity, final ItemStack itemStack) {
            return this.item.test(serverPlayerEntity, abstractTraderEntity) && this.villager.test(itemStack);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("item", this.villager.serialize());
            jsonObject1.add("villager", this.item.serialize());
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
        
        public void handle(final ServerPlayerEntity villager, final AbstractTraderEntity abstractTraderEntity, final ItemStack itemStack) {
            List<ConditionsContainer<Conditions>> list4 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer6 : this.conditions) {
                if (conditionsContainer6.getConditions().matches(villager, abstractTraderEntity, itemStack)) {
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
