package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class LevitationCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public LevitationCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return LevitationCriterion.ID;
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
        final DistancePredicate distancePredicate3 = DistancePredicate.deserialize(obj.get("distance"));
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(obj.get("duration"));
        return new Conditions(distancePredicate3, intRange4);
    }
    
    public void handle(final ServerPlayerEntity player, final Vec3d coord, final int integer) {
        final Handler handler4 = this.handlers.get(player.getAdvancementManager());
        if (handler4 != null) {
            handler4.handle(player, coord, integer);
        }
    }
    
    static {
        ID = new Identifier("levitation");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final DistancePredicate distance;
        private final NumberRange.IntRange duration;
        
        public Conditions(final DistancePredicate distancePredicate, final NumberRange.IntRange intRange) {
            super(LevitationCriterion.ID);
            this.distance = distancePredicate;
            this.duration = intRange;
        }
        
        public static Conditions create(final DistancePredicate distancePredicate) {
            return new Conditions(distancePredicate, NumberRange.IntRange.ANY);
        }
        
        public boolean matches(final ServerPlayerEntity serverPlayerEntity, final Vec3d vec3d, final int integer) {
            return this.distance.test(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z) && this.duration.test(integer);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("distance", this.distance.serialize());
            jsonObject1.add("duration", this.duration.serialize());
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
        
        public void handle(final ServerPlayerEntity coord, final Vec3d duration, final int integer) {
            List<ConditionsContainer<Conditions>> list4 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer6 : this.conditions) {
                if (conditionsContainer6.getConditions().matches(coord, duration, integer)) {
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
