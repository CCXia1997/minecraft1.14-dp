package net.minecraft.advancement.criterion;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.SystemUtil;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Maps;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.Map;
import net.minecraft.util.Identifier;

public class EnterBlockCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public EnterBlockCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return EnterBlockCriterion.ID;
    }
    
    @Override
    public void beginTrackingCondition(final PlayerAdvancementTracker manager, final ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler3 = this.handlers.get(manager);
        if (handler3 == null) {
            handler3 = new Handler(manager);
            this.handlers.put(manager, handler3);
        }
        handler3.addConditon(conditionsContainer);
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
        Block block3 = null;
        if (obj.has("block")) {
            final Identifier identifier4 = new Identifier(JsonHelper.getString(obj, "block"));
            final Object o;
            final Object o2;
            block3 = Registry.BLOCK.getOrEmpty(identifier4).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown block type '" + o2 + "'");
                return o;
            });
        }
        Map<Property<?>, Object> map4 = null;
        if (obj.has("state")) {
            if (block3 == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }
            final StateFactory<Block, BlockState> stateFactory5 = block3.getStateFactory();
            for (final Map.Entry<String, JsonElement> entry7 : JsonHelper.getObject(obj, "state").entrySet()) {
                final Property<?> property8 = stateFactory5.getProperty(entry7.getKey());
                if (property8 == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + entry7.getKey() + "' for block '" + Registry.BLOCK.getId(block3) + "'");
                }
                final String string9 = JsonHelper.asString(entry7.getValue(), entry7.getKey());
                final Optional<?> optional10 = property8.getValue(string9);
                if (!optional10.isPresent()) {
                    throw new JsonSyntaxException("Invalid block state value '" + string9 + "' for property '" + entry7.getKey() + "' on block '" + Registry.BLOCK.getId(block3) + "'");
                }
                if (map4 == null) {
                    map4 = Maps.newHashMap();
                }
                map4.put(property8, optional10.get());
            }
        }
        return new Conditions(block3, map4);
    }
    
    public void handle(final ServerPlayerEntity serverPlayerEntity, final BlockState blockState) {
        final Handler handler3 = this.handlers.get(serverPlayerEntity.getAdvancementManager());
        if (handler3 != null) {
            handler3.handle(blockState);
        }
    }
    
    static {
        ID = new Identifier("enter_block");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final Block block;
        private final Map<Property<?>, Object> state;
        
        public Conditions(@Nullable final Block block, @Nullable final Map<Property<?>, Object> map) {
            super(EnterBlockCriterion.ID);
            this.block = block;
            this.state = map;
        }
        
        public static Conditions block(final Block block) {
            return new Conditions(block, null);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            if (this.block != null) {
                jsonObject1.addProperty("block", Registry.BLOCK.getId(this.block).toString());
                if (this.state != null && !this.state.isEmpty()) {
                    final JsonObject jsonObject2 = new JsonObject();
                    for (final Map.Entry<Property<?>, ?> entry4 : this.state.entrySet()) {
                        jsonObject2.addProperty(entry4.getKey().getName(), SystemUtil.getValueAsString(entry4.getKey(), entry4.getValue()));
                    }
                    jsonObject1.add("state", jsonObject2);
                }
            }
            return jsonObject1;
        }
        
        public boolean matches(final BlockState blockState) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            if (this.state != null) {
                for (final Map.Entry<Property<?>, Object> entry3 : this.state.entrySet()) {
                    if (blockState.get(entry3.getKey()) != entry3.getValue()) {
                        return false;
                    }
                }
            }
            return true;
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
        
        public void addConditon(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }
        
        public void removeCondition(final ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }
        
        public void handle(final BlockState blockState) {
            List<ConditionsContainer<Conditions>> list2 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer4 : this.conditions) {
                if (conditionsContainer4.getConditions().matches(blockState)) {
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
