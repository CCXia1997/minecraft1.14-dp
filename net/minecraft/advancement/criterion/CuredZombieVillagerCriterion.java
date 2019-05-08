package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public CuredZombieVillagerCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return CuredZombieVillagerCriterion.ID;
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
        final EntityPredicate entityPredicate3 = EntityPredicate.deserialize(obj.get("zombie"));
        final EntityPredicate entityPredicate4 = EntityPredicate.deserialize(obj.get("villager"));
        return new Conditions(entityPredicate3, entityPredicate4);
    }
    
    public void handle(final ServerPlayerEntity player, final ZombieEntity zombie, final VillagerEntity villagerEntity) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(player, zombie, villagerEntity);
        }
    }
    
    static {
        ID = new Identifier("cured_zombie_villager");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final EntityPredicate zombie;
        private final EntityPredicate villager;
        
        public Conditions(final EntityPredicate entityPredicate1, final EntityPredicate entityPredicate2) {
            super(CuredZombieVillagerCriterion.ID);
            this.zombie = entityPredicate1;
            this.villager = entityPredicate2;
        }
        
        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final ZombieEntity zombieEntity, final VillagerEntity villagerEntity) {
            return this.zombie.test(serverPlayerEntity, zombieEntity) && this.villager.test(serverPlayerEntity, villagerEntity);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("zombie", this.zombie.serialize());
            jsonObject1.add("villager", this.villager.serialize());
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
        
        public void handle(final ServerPlayerEntity serverPlayerEntity, final ZombieEntity zombieEntity, final VillagerEntity villagerEntity) {
            List<ConditionsContainer<Conditions>> list4 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer6 : this.conditions) {
                if (conditionsContainer6.getConditions().matches(serverPlayerEntity, zombieEntity, villagerEntity)) {
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
