package net.minecraft.advancement.criterion;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.NumberRange;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class InventoryChangedCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public InventoryChangedCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return InventoryChangedCriterion.ID;
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
        final JsonObject jsonObject3 = JsonHelper.getObject(obj, "slots", new JsonObject());
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(jsonObject3.get("occupied"));
        final NumberRange.IntRange intRange5 = NumberRange.IntRange.fromJson(jsonObject3.get("full"));
        final NumberRange.IntRange intRange6 = NumberRange.IntRange.fromJson(jsonObject3.get("empty"));
        final ItemPredicate[] arr7 = ItemPredicate.deserializeAll(obj.get("items"));
        return new Conditions(intRange4, intRange5, intRange6, arr7);
    }
    
    public void handle(final ServerPlayerEntity player, final PlayerInventory playerInventory) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(playerInventory);
        }
    }
    
    static {
        ID = new Identifier("inventory_changed");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final NumberRange.IntRange occupied;
        private final NumberRange.IntRange full;
        private final NumberRange.IntRange empty;
        private final ItemPredicate[] items;
        
        public Conditions(final NumberRange.IntRange intRange1, final NumberRange.IntRange intRange2, final NumberRange.IntRange intRange3, final ItemPredicate[] arr) {
            super(InventoryChangedCriterion.ID);
            this.occupied = intRange1;
            this.full = intRange2;
            this.empty = intRange3;
            this.items = arr;
        }
        
        public static Conditions items(final ItemPredicate... arr) {
            return new Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, arr);
        }
        
        public static Conditions items(final ItemProvider... arr) {
            final ItemPredicate[] arr2 = new ItemPredicate[arr.length];
            for (int integer3 = 0; integer3 < arr.length; ++integer3) {
                arr2[integer3] = new ItemPredicate(null, arr[integer3].getItem(), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new EnchantmentPredicate[0], null, NbtPredicate.ANY);
            }
            return items(arr2);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            if (!this.occupied.isDummy() || !this.full.isDummy() || !this.empty.isDummy()) {
                final JsonObject jsonObject2 = new JsonObject();
                jsonObject2.add("occupied", this.occupied.serialize());
                jsonObject2.add("full", this.full.serialize());
                jsonObject2.add("empty", this.empty.serialize());
                jsonObject1.add("slots", jsonObject2);
            }
            if (this.items.length > 0) {
                final JsonArray jsonArray2 = new JsonArray();
                for (final ItemPredicate itemPredicate6 : this.items) {
                    jsonArray2.add(itemPredicate6.serialize());
                }
                jsonObject1.add("items", jsonArray2);
            }
            return jsonObject1;
        }
        
        public boolean matches(final PlayerInventory playerInventory) {
            int integer2 = 0;
            int integer3 = 0;
            int integer4 = 0;
            final List<ItemPredicate> list5 = Lists.<ItemPredicate>newArrayList(this.items);
            for (int integer5 = 0; integer5 < playerInventory.getInvSize(); ++integer5) {
                final ItemStack itemStack7 = playerInventory.getInvStack(integer5);
                if (itemStack7.isEmpty()) {
                    ++integer3;
                }
                else {
                    ++integer4;
                    if (itemStack7.getAmount() >= itemStack7.getMaxAmount()) {
                        ++integer2;
                    }
                    final Iterator<ItemPredicate> iterator8 = list5.iterator();
                    while (iterator8.hasNext()) {
                        final ItemPredicate itemPredicate9 = iterator8.next();
                        if (itemPredicate9.test(itemStack7)) {
                            iterator8.remove();
                        }
                    }
                }
            }
            return this.full.test(integer2) && this.empty.test(integer3) && this.occupied.test(integer4) && list5.isEmpty();
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
        
        public void handle(final PlayerInventory playerInventory) {
            List<ConditionsContainer<Conditions>> list2 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer4 : this.conditions) {
                if (conditionsContainer4.getConditions().matches(playerInventory)) {
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
