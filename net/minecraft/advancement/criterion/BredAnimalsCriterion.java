package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class BredAnimalsCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public BredAnimalsCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return BredAnimalsCriterion.ID;
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
        final EntityPredicate entityPredicate3 = EntityPredicate.deserialize(obj.get("parent"));
        final EntityPredicate entityPredicate4 = EntityPredicate.deserialize(obj.get("partner"));
        final EntityPredicate entityPredicate5 = EntityPredicate.deserialize(obj.get("child"));
        return new Conditions(entityPredicate3, entityPredicate4, entityPredicate5);
    }
    
    public void handle(final ServerPlayerEntity player, final AnimalEntity parent1, @Nullable final AnimalEntity parent2, @Nullable final PassiveEntity passiveEntity) {
        final Handler handler5 = this.handlers.get(player.getAdvancementManager());
        if (handler5 != null) {
            handler5.handle(player, parent1, parent2, passiveEntity);
        }
    }
    
    static {
        ID = new Identifier("bred_animals");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate parent;
        private final EntityPredicate partner;
        private final EntityPredicate child;
        
        public Conditions(final EntityPredicate entityPredicate1, final EntityPredicate entityPredicate2, final EntityPredicate entityPredicate3) {
            super(BredAnimalsCriterion.ID);
            this.parent = entityPredicate1;
            this.partner = entityPredicate2;
            this.child = entityPredicate3;
        }
        
        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public static Conditions create(final EntityPredicate.Builder builder) {
            return new Conditions(builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final AnimalEntity animalEntity2, @Nullable final AnimalEntity animalEntity3, @Nullable final PassiveEntity passiveEntity) {
            return this.child.test(serverPlayerEntity, passiveEntity) && ((this.parent.test(serverPlayerEntity, animalEntity2) && this.partner.test(serverPlayerEntity, animalEntity3)) || (this.parent.test(serverPlayerEntity, animalEntity3) && this.partner.test(serverPlayerEntity, animalEntity2)));
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("parent", this.parent.serialize());
            jsonObject1.add("partner", this.partner.serialize());
            jsonObject1.add("child", this.child.serialize());
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
        
        public void handle(final ServerPlayerEntity parent1, final AnimalEntity parent2, @Nullable final AnimalEntity child, @Nullable final PassiveEntity passiveEntity) {
            List<ConditionsContainer<Conditions>> list5 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer7 : this.conditions) {
                if (conditionsContainer7.getConditions().matches(parent1, parent2, child, passiveEntity)) {
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
