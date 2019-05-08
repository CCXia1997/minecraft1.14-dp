package net.minecraft.advancement.criterion;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NumberRange;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class UsedEnderEyeCriterion implements Criterion<Conditions>
{
    private static final Identifier id;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public UsedEnderEyeCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return UsedEnderEyeCriterion.id;
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
        final NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(obj.get("distance"));
        return new Conditions(floatRange3);
    }
    
    public void handle(final ServerPlayerEntity player, final BlockPos blockPos) {
        final Handler handler3 = this.handlers.get(player.getAdvancementManager());
        if (handler3 != null) {
            final double double4 = player.x - blockPos.getX();
            final double double5 = player.z - blockPos.getZ();
            handler3.handle(double4 * double4 + double5 * double5);
        }
    }
    
    static {
        id = new Identifier("used_ender_eye");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final NumberRange.FloatRange distance;
        
        public Conditions(final NumberRange.FloatRange floatRange) {
            super(UsedEnderEyeCriterion.id);
            this.distance = floatRange;
        }
        
        public boolean matches(final double double1) {
            return this.distance.matchesSquared(double1);
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
        
        public void handle(final double double1) {
            List<ConditionsContainer<Conditions>> list3 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer5 : this.conditions) {
                if (conditionsContainer5.getConditions().matches(double1)) {
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
