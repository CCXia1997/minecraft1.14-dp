package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.gson.JsonElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class NetherTravelCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public NetherTravelCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return NetherTravelCriterion.ID;
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
        final LocationPredicate locationPredicate3 = LocationPredicate.deserialize(obj.get("entered"));
        final LocationPredicate locationPredicate4 = LocationPredicate.deserialize(obj.get("exited"));
        final DistancePredicate distancePredicate5 = DistancePredicate.deserialize(obj.get("distance"));
        return new Conditions(locationPredicate3, locationPredicate4, distancePredicate5);
    }
    
    public void handle(final ServerPlayerEntity player, final Vec3d vec3d) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(player.getServerWorld(), vec3d, player.x, player.y, player.z);
        }
    }
    
    static {
        ID = new Identifier("nether_travel");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final LocationPredicate entered;
        private final LocationPredicate exited;
        private final DistancePredicate distance;
        
        public Conditions(final LocationPredicate locationPredicate1, final LocationPredicate locationPredicate2, final DistancePredicate distancePredicate) {
            super(NetherTravelCriterion.ID);
            this.entered = locationPredicate1;
            this.exited = locationPredicate2;
            this.distance = distancePredicate;
        }
        
        public static Conditions distance(final DistancePredicate distancePredicate) {
            return new Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
        }
        
        public boolean matches(final ServerWorld serverWorld, final Vec3d vec3d, final double double3, final double double5, final double double7) {
            return this.entered.test(serverWorld, vec3d.x, vec3d.y, vec3d.z) && this.exited.test(serverWorld, double3, double5, double7) && this.distance.test(vec3d.x, vec3d.y, vec3d.z, double3, double5, double7);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("entered", this.entered.serialize());
            jsonObject1.add("exited", this.exited.serialize());
            jsonObject1.add("distance", this.distance.serialize());
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
        
        public void handle(final ServerWorld enteredCoord, final Vec3d exitedX, final double exitedY, final double double5, final double double7) {
            List<ConditionsContainer<Conditions>> list9 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer11 : this.conditions) {
                if (conditionsContainer11.getConditions().matches(enteredCoord, exitedX, exitedY, double5, double7)) {
                    if (list9 == null) {
                        list9 = Lists.newArrayList();
                    }
                    list9.add(conditionsContainer11);
                }
            }
            if (list9 != null) {
                for (final ConditionsContainer<Conditions> conditionsContainer11 : list9) {
                    conditionsContainer11.apply(this.manager);
                }
            }
        }
    }
}
