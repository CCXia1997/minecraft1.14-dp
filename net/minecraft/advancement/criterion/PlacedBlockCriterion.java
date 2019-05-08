package net.minecraft.advancement.criterion;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.SystemUtil;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
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

public class PlacedBlockCriterion implements Criterion<Conditions>
{
    private static final Identifier ID;
    private final Map<PlayerAdvancementTracker, Handler> handlers;
    
    public PlacedBlockCriterion() {
        this.handlers = Maps.newHashMap();
    }
    
    @Override
    public Identifier getId() {
        return PlacedBlockCriterion.ID;
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
        final LocationPredicate locationPredicate5 = LocationPredicate.deserialize(obj.get("location"));
        final ItemPredicate itemPredicate6 = ItemPredicate.deserialize(obj.get("item"));
        return new Conditions(block3, map4, locationPredicate5, itemPredicate6);
    }
    
    public void handle(final ServerPlayerEntity player, final BlockPos blockPos, final ItemStack itemStack) {
        final BlockState blockState4 = player.world.getBlockState(blockPos);
        final Handler handler5 = this.handlers.get(player.getAdvancementManager());
        if (handler5 != null) {
            handler5.handle(blockState4, blockPos, player.getServerWorld(), itemStack);
        }
    }
    
    static {
        ID = new Identifier("placed_block");
    }
    
    public static class Conditions extends AbstractCriterionConditions
    {
        private final Block block;
        private final Map<Property<?>, Object> state;
        private final LocationPredicate location;
        private final ItemPredicate item;
        
        public Conditions(@Nullable final Block block, @Nullable final Map<Property<?>, Object> map, final LocationPredicate locationPredicate, final ItemPredicate itemPredicate) {
            super(PlacedBlockCriterion.ID);
            this.block = block;
            this.state = map;
            this.location = locationPredicate;
            this.item = itemPredicate;
        }
        
        public static Conditions block(final Block block) {
            return new Conditions(block, null, LocationPredicate.ANY, ItemPredicate.ANY);
        }
        
        public boolean matches(final BlockState blockState, final BlockPos blockPos, final ServerWorld serverWorld, final ItemStack itemStack) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            if (this.state != null) {
                for (final Map.Entry<Property<?>, Object> entry6 : this.state.entrySet()) {
                    if (blockState.get(entry6.getKey()) != entry6.getValue()) {
                        return false;
                    }
                }
            }
            return this.location.test(serverWorld, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ()) && this.item.test(itemStack);
        }
        
        @Override
        public JsonElement toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            if (this.block != null) {
                jsonObject1.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            if (this.state != null) {
                final JsonObject jsonObject2 = new JsonObject();
                for (final Map.Entry<Property<?>, Object> entry4 : this.state.entrySet()) {
                    jsonObject2.addProperty(entry4.getKey().getName(), SystemUtil.getValueAsString(entry4.getKey(), entry4.getValue()));
                }
                jsonObject1.add("state", jsonObject2);
            }
            jsonObject1.add("location", this.location.serialize());
            jsonObject1.add("item", this.item.serialize());
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
        
        public void handle(final BlockState pos, final BlockPos world, final ServerWorld item, final ItemStack itemStack) {
            List<ConditionsContainer<Conditions>> list5 = null;
            for (final ConditionsContainer<Conditions> conditionsContainer7 : this.conditions) {
                if (conditionsContainer7.getConditions().matches(pos, world, item, itemStack)) {
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
