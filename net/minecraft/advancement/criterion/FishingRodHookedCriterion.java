package net.minecraft.advancement.criterion;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import java.util.Iterator;
import net.minecraft.entity.ItemEntity;
import java.util.Collection;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> b;
    
    public FishingRodHookedCriterion() {
        this.b = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return FishingRodHookedCriterion.ID;
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
        final ItemPredicate itemPredicate3 = ItemPredicate.deserialize(obj.get("rod"));
        final EntityPredicate entityPredicate4 = EntityPredicate.deserialize(obj.get("entity"));
        final ItemPredicate itemPredicate4 = ItemPredicate.deserialize(obj.get("item"));
        return new Conditions(itemPredicate3, entityPredicate4, itemPredicate4);
    }
    
    public void handle(final ServerPlayerEntity serverPlayerEntity, final ItemStack itemStack, final FishHookEntity fishHookEntity, final Collection<ItemStack> collection) {
        final Handler handler5 = this.b.get(serverPlayerEntity.getAdvancementManager());
        if (handler5 != null) {
            handler5.handle(serverPlayerEntity, itemStack, fishHookEntity, collection);
        }
    }
    
    static {
        ID = new Identifier("fishing_rod_hooked");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final ItemPredicate rod;
        private final EntityPredicate entity;
        private final ItemPredicate item;
        
        public Conditions(final ItemPredicate entity, final EntityPredicate item, final ItemPredicate itemPredicate3) {
            super(FishingRodHookedCriterion.ID);
            this.rod = entity;
            this.entity = item;
            this.item = itemPredicate3;
        }
        
        public static Conditions create(final ItemPredicate itemPredicate1, final EntityPredicate entityPredicate, final ItemPredicate itemPredicate3) {
            return new Conditions(itemPredicate1, entityPredicate, itemPredicate3);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final ItemStack itemStack, final FishHookEntity fishHookEntity, final Collection<ItemStack> collection) {
            if (!this.rod.test(itemStack)) {
                return false;
            }
            if (!this.entity.test(serverPlayerEntity, fishHookEntity.hookedEntity)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                boolean boolean5 = false;
                if (fishHookEntity.hookedEntity instanceof ItemEntity) {
                    final ItemEntity itemEntity6 = (ItemEntity)fishHookEntity.hookedEntity;
                    if (this.item.test(itemEntity6.getStack())) {
                        boolean5 = true;
                    }
                }
                for (final ItemStack itemStack2 : collection) {
                    if (this.item.test(itemStack2)) {
                        boolean5 = true;
                        break;
                    }
                }
                if (!boolean5) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("rod", this.rod.serialize());
            jsonObject1.add("entity", this.entity.serialize());
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
        
        public void handle(final ServerPlayerEntity serverPlayerEntity, final ItemStack itemStack, final FishHookEntity fishHookEntity, final Collection<ItemStack> collection) {
            List<ConditionsContainer<Conditions>> list5 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer7 : this.conditions) {
                if (conditionsContainer7.getConditions().matches(serverPlayerEntity, itemStack, fishHookEntity, collection)) {
                    if (list5 == null) {
                        list5 = Lists.newArrayList();
                    }
                    list5.add(conditionsContainer7);
                }
            }
            if (list5 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer7 : list5) {
                    conditionsContainer7.apply(this.manager);
                }
            }
        }
    }
}
